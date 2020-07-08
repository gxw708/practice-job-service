package gowu.job.noop;

import gowu.job.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NoOpJobProcessor<T extends JobContext> extends AbstractJobProcessor<T>
{
	private static final Logger logger = Logger.getLogger(NoOpJobProcessor.class.getName());
	
	public NoOpJobProcessor(JobDirectoryProvider jobDirectoryProvider)
	{
		super(jobDirectoryProvider);
	}

	@Override
	public JobResult executeJob(T job) throws JobException
	{
		this.getJobDirectoryProvider().updateJobStatus(job.getJobId(), JobConstants.JobStatus.RUNNING);
		try
		{
			int count = 3;
			while(count-- > 0)
			{
				job = (T) getJobDirectoryProvider().getJob(job.getJobId());
				if(!checkForForceCancelled(job) && !checkForRequestForCancel(job))
				{
					Thread.sleep(100);
				}
				else
				{
					return new JobResult(null, JobResult.ResultType.CANCELLED, null);
				}
			}
			
			this.getJobDirectoryProvider().updateJobStatus(job.getJobId(), JobConstants.JobStatus.SUCCEEDED);
			return new JobResult(null, JobResult.ResultType.SUCCEEDED, null);
		} catch(InterruptedException ex)
		{
			throw new JobException(JobException.JobErrorCode.INTERRUPTED, String.format("Job %s has interrupted",job.getJobId()),ex);
		}
	}

	@Override
	public void handleRequestForCancel(T job)
	{
		logger.log(Level.INFO, "performing actions since the job {0} is requested for cancel", new Object[]{job});
	}

	@Override
	public void handleForceCancelled(T job)
	{
		logger.log(Level.INFO, "performing actions since the job {0} is force canceled", new Object[]{job});
	}
}
