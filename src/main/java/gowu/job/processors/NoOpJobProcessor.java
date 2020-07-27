package gowu.job.processors;

import gowu.job.*;
import gowu.job.entities.Job;
import gowu.job.entities.JobContext;
import gowu.job.entities.JobIdentifier;
import gowu.job.storages.JobDirectoryProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NoOpJobProcessor extends AbstractJobProcessor
{
	private static final Logger logger = Logger.getLogger(NoOpJobProcessor.class.getName());
	
	public NoOpJobProcessor(JobDirectoryProvider jobDirectoryProvider)
	{
		super(jobDirectoryProvider);
	}

	@Override
	public JobResult executeJob(Job job) throws JobException
	{
		JobIdentifier jobIdentifier = job.getContext().getIdentifier();
		try
		{
			int count = 3;
			while(count-- > 0)
			{
				job = jobDirectoryProvider.getJob(jobIdentifier);
				if(!checkForForceCancelled(job) && !checkForRequestForCancel(job))
				{
					Thread.sleep(100);
				}
				else
				{
					return new JobResult(null, JobResult.ResultType.CANCELLED, null);
				}
			}
			return new JobResult(null, JobResult.ResultType.SUCCEEDED, null);
		} catch(InterruptedException ex)
		{
			throw new JobException(JobException.JobErrorCode.INTERRUPTED, String.format("Job %s has interrupted",job),ex);
		}
	}

	@Override
	public void handleRequestForCancel(Job job)
	{
		logger.log(Level.INFO, "performing actions since the job {0} is requested for cancel", new Object[]{job});
	}

	@Override
	public void handleForceCancelled(Job job)
	{
		logger.log(Level.INFO, "performing actions since the job {0} is force canceled", new Object[]{job});
	}
}
