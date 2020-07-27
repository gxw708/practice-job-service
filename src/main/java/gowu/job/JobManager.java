package gowu.job;

import gowu.job.entities.Job;
import gowu.job.entities.JobContext;
import gowu.job.entities.JobIdentifier;
import gowu.job.processors.JobProcessor;
import gowu.job.processors.JobProcessorFactory;
import gowu.job.storages.JobDirectoryProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class JobManager
{
	private static final Logger logger = Logger.getLogger(JobManager.class.getName());
	private static volatile JobManager instance = null;
	private static volatile Timer scheduleTimer;
	//Do not initialize the scheduleTimer in constructor but first required
	private final Object timerLock = new Object();
	private JobThreadFactory threadFactory;
	private JobDirectoryProvider jobDirectoryProvider;
	private ThreadPoolExecutor executor;
	private int maxQueueSize;

	private JobManager(int corePoolSize,
	                   int maxPoolSize,
	                   int maxQueueSize,
	                   JobDirectoryProvider jobDirectoryProvider)
	{
		this.jobDirectoryProvider = jobDirectoryProvider;
		this.maxQueueSize = maxQueueSize;
		logger.log(Level.INFO, "Creating job manager thread pool with corePoolSize={0}, maxPoolSize={1}, maxQueueSize={2}", new Object[]{corePoolSize, maxPoolSize, maxQueueSize});
		this.executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0, MILLISECONDS,
				new PriorityBlockingQueue<Runnable>(maxQueueSize), new JobThreadFactory(JobConstants.BACKGROUND_JOB_THREAD_NAME));
	}

	public static JobManager getInstance(JobDirectoryProvider jobDirectoryProvider)
	{
		return getInstance(JobConstants.EXECUTOR_DEFAULT_CORE_POOL_SIZE, 
				JobConstants.EXEXUTOR_DEFAULT_MAX_POOL_SIZE, 
				JobConstants.EXECUTOR_DEFAULT_MAX_QUEUE_SIZE, 
				jobDirectoryProvider);	
	}

	public static JobManager getInstance(int corePoolSize,
	                                     int maxPoolSize,
	                                     int maxQueueSize,
	                                     JobDirectoryProvider jobDirectoryProvider)
	{
		if (instance == null)
		{
			synchronized (JobManager.class)
			{
				if (instance == null)
				{
					instance = new JobManager(corePoolSize, maxPoolSize, maxQueueSize, jobDirectoryProvider);
				}
			}
		}
		return instance;
	}


	/**
	 * To submit a job by the given JobContext. Returns jobId if the job succeeded to submit
	 * @param jobContext a JobContext represents a Job to be submit
	 * @return
	 */
	public JobIdentifier submitJob(JobContext jobContext) throws JobException
	{
		if (executor == null || executor.isShutdown())
		{
			logger.fine("The executor has been shut down.");
			return null;
		}
		if (jobContext == null)
		{
			throw new JobException(JobException.JobErrorCode.INVALID_INPUT, "Job creation failed due to empty job entity");
		}
		if (jobDirectoryProvider == null)
		{
			throw new JobException(JobException.JobErrorCode.INTERNAL_ERROR, "Empty jobDirectoryProvider found");
		}

		JobProcessorFactory jobProcessorFactory = JobProcessorFactory.getInstance(jobDirectoryProvider);
		JobProcessor jobProcessor = jobProcessorFactory.createJobProcessor(jobContext.getIdentifier().getType());
		
		Job job = new Job.Builder(jobContext).build();
		job = jobDirectoryProvider.createJob(job);
		if (job != null)
		{
			logger.log(Level.FINE, "A new job just submitted {0}", job.toString());

			//The scheduled thread pool using unlimited blocking queue, so using this queue size to limit the request count
			//Getting this queue size ignoring thread-unsafe for not bring bottle neck.
			int size = executor.getQueue().size();
			if (size < this.maxQueueSize)
			{
				try
				{
					executor.execute(new JobSubmitTask(job, jobProcessor, jobDirectoryProvider));
				} catch (RejectedExecutionException ex)
				{
					throw new JobException(JobException.JobErrorCode.JOB_REJECTED, "Job rejected by executor", ex);
				}
			} else
			{
				throw new JobException(JobException.JobErrorCode.JOB_REJECTED, "Job rejected by executor");
			}
		}
		else
		{
			logger.warning("Job submit failed");
			throw new JobException(JobException.JobErrorCode.CREATION_FAILED, "Job creation failed");
		}
		
		return jobContext.getIdentifier();
	}

	public JobIdentifier scheduleJob(JobContext jobContext, long delay, TimeUnit unit) throws JobException
	{
		if (executor == null || executor.isShutdown())
		{
			logger.fine("The executor has been shut down.");
			return null;
		}
		if (jobContext != null)
		{
			if (scheduleTimer == null)
			{
				synchronized (timerLock)
				{
					if (scheduleTimer == null)
					{
						scheduleTimer = new Timer(JobConstants.JOB_RESCHEDULED_SUBMITTER);
					}
				}
			}
			JobProcessorFactory jobProcessorFactory = JobProcessorFactory.getInstance(jobDirectoryProvider);
			JobProcessor processor = jobProcessorFactory.createJobProcessor(jobContext.getIdentifier().getType());
			scheduleTimer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					try
					{
						submitJob(jobContext);
					}
					catch(JobException ex)
					{
						logger.log(Level.WARNING, "Job scheduled failed due to exception", ex);
					}
				}
			}, MILLISECONDS.convert(delay, unit));
		} else
		{
			logger.warning("JobContext scheduled failed due to empty job entity");
			throw new JobException(JobException.JobErrorCode.INTERNAL_ERROR, "Job scheduled failed due to empty job entity");
		}
		
		return jobContext.getIdentifier();
	}

	public Job getJob(JobIdentifier jobIdentifier) throws JobException
	{
		return jobDirectoryProvider.getJob(jobIdentifier) == null ? null : jobDirectoryProvider.getJob(jobIdentifier);
	}

	public JobConstants.JobStatus getJobStatus(JobIdentifier jobId) throws JobException
	{
		Job jobContext = this.getJob(jobId);
		if (jobContext == null)
		{
			throw new JobException(JobException.JobErrorCode.JOB_NOT_FOUND, String.format("Job not found for this jobId %s", jobId));
		}
		return jobContext.getStatus();
	}

	public void cancelJob(JobIdentifier jobIdentifier) throws JobException
	{
		Job job = jobDirectoryProvider.getJob(jobIdentifier);
		if(job != null)
		{
			logger.fine(String.format("job with id %s is going to be canceled", jobIdentifier));
			job.setStatus(JobConstants.JobStatus.REQUEST_FOR_CANCEL);
			jobDirectoryProvider.updateJob(job);
		}
		else
		{
			throw new JobException(JobException.JobErrorCode.JOB_NOT_FOUND, String.format("Job not found for this jobId %s", jobIdentifier));
		}
	}

	public boolean isJobCancelled(JobIdentifier jobIdentifier) throws JobException
	{
		return JobConstants.JobStatus.CANCELLED.equals(getJobStatus(jobIdentifier));
	}

	public boolean isJobRequestForCancel(JobIdentifier jobIdentifier) throws JobException
	{
		return JobConstants.JobStatus.REQUEST_FOR_CANCEL.equals(getJobStatus(jobIdentifier));
	}

	public boolean isJobRunning(JobIdentifier jobIdentifier) throws JobException
	{
		return JobConstants.JobStatus.RUNNING.equals(getJobStatus(jobIdentifier));
	}

	public boolean isJobDone(JobIdentifier jobIdentifier) throws JobException
	{
		JobConstants.JobStatus currentStatus = getJobStatus(jobIdentifier);
		return JobConstants.JobStatus.SUCCEEDED.equals(currentStatus) ||
				JobConstants.JobStatus.FAILED.equals(currentStatus) ||
				JobConstants.JobStatus.CANCELLED.equals(currentStatus) ||
				JobConstants.JobStatus.ERROR.equals(currentStatus);
	}
}
