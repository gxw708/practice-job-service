package gowu.job.processors;

import gowu.job.*;
import gowu.job.entities.Job;
import gowu.job.storages.JobDirectoryProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractJobProcessor implements JobProcessor
{
	private static final Logger logger = Logger.getLogger(AbstractJobProcessor.class.getName());
	protected JobDirectoryProvider jobDirectoryProvider;

	public AbstractJobProcessor(JobDirectoryProvider jobDirectoryProvider)
	{
		this.jobDirectoryProvider = jobDirectoryProvider;
	}

	@Override
	public void processJob(Job job) throws JobException
	{
		job = jobDirectoryProvider.getJob(job.getContext().getIdentifier());
		if(job == null)
		{
			throw new JobException(JobException.JobErrorCode.JOB_NOT_FOUND, String.format("Job %s not found", job));
		}
		job.setStatus(JobConstants.JobStatus.RUNNING);
		jobDirectoryProvider.updateJob(job);
		
		Timer timer = new Timer();
		JobResult result = executeJob(job);
		JobResult.ResultType resultType = result.getResultType();
		switch(resultType)
		{
			case SUCCEEDED:
				job.setStatus(JobConstants.JobStatus.SUCCEEDED);
				break;
			case FAILED:
				job.setStatus(JobConstants.JobStatus.FAILED);
				break;
			case CANCELLED:
				job.setStatus(JobConstants.JobStatus.CANCELLED);
				break;
			default:
				throw new JobException(JobException.JobErrorCode.INTERNAL_ERROR, String.format("Unexpected job result type %s for job %s",resultType, job));
		}
		jobDirectoryProvider.updateJob(job);
		logger.log(Level.FINE, "It took {0} to finish the job {1}", new Object[]{timer.formatElapsedTime(), job});
	}
	
	public final boolean checkForRequestForCancel(Job job) throws JobException
	{
		if (JobConstants.JobStatus.REQUEST_FOR_CANCEL == job.getStatus())
		{
			handleRequestForCancel(job);
			job.setStatus(JobConstants.JobStatus.CANCELLED);
			jobDirectoryProvider.updateJob(job);
			return true;
		}
		return false;
	}

	public final boolean checkForForceCancelled(Job job) throws JobException
	{
		if (JobConstants.JobStatus.CANCELLED == job.getStatus())
		{
			handleForceCancelled(job);
			return true;
		}
		return false;
	}

	public abstract JobResult executeJob(Job job) throws JobException;

	public abstract void handleRequestForCancel(Job job);

	public abstract void handleForceCancelled(Job job);
}
