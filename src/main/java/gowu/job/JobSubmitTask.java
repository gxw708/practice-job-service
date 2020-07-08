package gowu.job;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JobSubmitTask implements Runnable, Comparable<JobSubmitTask>
{
	private static final Logger logger = Logger.getLogger(JobSubmitTask.class.getName());
	private JobContext job;
	private JobProcessor jobProcessor;
	private JobDirectoryProvider jobDirectoryProvider;
	private JobCallback jobCallback = null;

	public JobSubmitTask(JobContext job, JobProcessor jobProcessor)
	{
		this(job, jobProcessor, null);
	}

	public JobSubmitTask(JobContext job, JobProcessor jobProcessor, JobCallback jobCallback)
	{
		super();
		this.job = job;
		this.jobProcessor = jobProcessor;
		this.jobDirectoryProvider = jobProcessor.getJobDirectoryProvider();
		this.jobCallback = jobCallback;
	}
	
	@Override
	public void run()
	{
		try
		{
			if (jobCallback == null)
			{
				jobProcessor.processJob(job);
			}
			else
			{
				jobProcessor.processJob(job, jobCallback);
			}
		} catch (JobException ex)
		{
			if (ex.getErrorCode().equals(JobException.JobErrorCode.INTERRUPTED))
			{
				logger.log(Level.INFO, ex.getErrorMessage());
			} else if (ex.getErrorCode().equals(JobException.JobErrorCode.JOB_NOT_FOUND))
			{
				logger.log(Level.INFO, "JobContext {0} cannot be found or no longer exists", job.toString());
			} else
			{
				logger.log(Level.WARNING, "JobContext execution error", ex);
				try
				{
					jobDirectoryProvider.updateJobStatus(job.getJobId(), JobConstants.JobStatus.ERROR);
				} catch (JobException e)
				{
					logger.log(Level.SEVERE, "Failed to update job status");
				}
			}
		}

	}

	@Override
	public int compareTo(JobSubmitTask o)
	{
		return Integer.compare(job.getJobPriority().value(), o.job.getJobPriority().value());
	}
}
