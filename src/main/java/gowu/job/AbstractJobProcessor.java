package gowu.job;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractJobProcessor<T extends JobContext> implements JobProcessor<T>
{
	private static final Logger logger = Logger.getLogger(AbstractJobProcessor.class.getName());
	private JobDirectoryProvider jobDirectoryProvider;

	public AbstractJobProcessor(JobDirectoryProvider jobDirectoryProvider)
	{
		this.jobDirectoryProvider = jobDirectoryProvider;
	}

	@Override
	public void processJob(T job, JobCallback callback) throws JobException
	{
		boolean running = jobDirectoryProvider.updateJobStatus(job.getJobId(), JobConstants.JobStatus.RUNNING);
		if (!running)
		{
			throw new JobException(JobException.JobErrorCode.JOB_EXECUTION_FAILED, String.format("Can't updating job %s to RUNNING status",job.getJobId()));
		}
		Timer timer = new Timer();
		JobResult result = executeJob(job);
		JobResult.ResultType resultType = result.getResultType();
		switch(resultType)
		{
			case SUCCEEDED:
				jobDirectoryProvider.updateJobStatus(job.getJobId(), JobConstants.JobStatus.SUCCEEDED);
				if(callback != null)
					callback.onSucceeded(job, result);
				break;
			case FAILED:
				jobDirectoryProvider.updateJobStatus(job.getJobId(), JobConstants.JobStatus.FAILED);
				if(callback != null)
					callback.onFailed(job, result);
				break;
			case CANCELLED:
				jobDirectoryProvider.updateJobStatus(job.getJobId(), JobConstants.JobStatus.CANCELLED);
				if(callback != null)
					callback.onCancelled(job);
				break;
			default:
				throw new JobException(JobException.JobErrorCode.INTERNAL_ERROR, String.format("Unexpected job result type %s for job %s",resultType, job.getJobId()));
		}
		
		logger.log(Level.FINE, "It took {0} to finish the job {1}", new Object[]{timer.formatElapsedTime(), job.getJobId()});
	}

	@Override
	public JobDirectoryProvider getJobDirectoryProvider()
	{
		return jobDirectoryProvider;
	}

	public final boolean checkForRequestForCancel(T job) throws JobException
	{
		if (JobConstants.JobStatus.REQUEST_FOR_CANCEL == job.getJobStatus())
		{
			handleRequestForCancel(job);
			jobDirectoryProvider.updateJobStatus(job.getJobId(), JobConstants.JobStatus.CANCELLED);
			return true;
		}
		return false;
	}

	public final boolean checkForForceCancelled(T job) throws JobException
	{
		if (JobConstants.JobStatus.CANCELLED == job.getJobStatus())
		{
			handleForceCancelled(job);
			return true;
		}
		return false;
	}

	public abstract JobResult executeJob(T job) throws JobException;

	public abstract void handleRequestForCancel(T job);

	public abstract void handleForceCancelled(T job);
}
