package gowu.job.processors;

import gowu.job.JobConstants;
import gowu.job.storages.JobDirectoryProvider;
import gowu.job.JobException;

public class JobProcessorFactory
{
	private static volatile JobProcessorFactory instance = null;
	private JobDirectoryProvider jobDirectoryProvider;
	
	public static JobProcessorFactory getInstance(JobDirectoryProvider jobDirectoryProvider)
	{
		{
			synchronized (JobProcessorFactory.class)
			{
				if (instance == null)
				{
					instance = new JobProcessorFactory(jobDirectoryProvider);
				}
			}
		}
		return instance;
	}
	
	private JobProcessorFactory(JobDirectoryProvider jobDirectoryProvider)
	{
		this.jobDirectoryProvider = jobDirectoryProvider;	
	}
	
	public JobProcessor createJobProcessor(JobConstants.JobType jobType) throws JobException
	{
		switch (jobType)
		{
			case NOOP:
				return new NoOpJobProcessor(this.jobDirectoryProvider);
			default:
				throw new JobException(JobException.JobErrorCode.JOB_TYPE_NOT_SUPPORTED,
						String.format("Not supported job type %s", jobType));
		}
	}
}
