package gowu.job;

import gowu.job.entities.Job;
import gowu.job.processors.JobProcessor;
import gowu.job.storages.JobDirectoryProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JobSubmitTask implements Runnable, Comparable<JobSubmitTask>
{
	private static final Logger logger = Logger.getLogger(JobSubmitTask.class.getName());
	private Job job;
	private JobProcessor jobProcessor;
	private JobDirectoryProvider jobDirectoryProvider;

	public JobSubmitTask(Job job, JobProcessor jobProcessor, JobDirectoryProvider jobDirectoryProvider)
	{
		this.job = job;
		this.jobProcessor = jobProcessor;
		this.jobDirectoryProvider = jobDirectoryProvider;
	}
	
	@Override
	public void run()
	{
		try
		{
			jobProcessor.processJob(job);
		} catch (JobException ex)
		{
			if (ex.getErrorCode().equals(JobException.JobErrorCode.INTERRUPTED))
			{
				logger.log(Level.INFO, ex.getErrorMessage());
			} else if (ex.getErrorCode().equals(JobException.JobErrorCode.JOB_NOT_FOUND))
			{
				logger.log(Level.INFO, "Job {0} cannot be found or no longer exists", job.toString());
			} else
			{
				logger.log(Level.WARNING, "Job execution error", ex);
				try
				{
					job = jobDirectoryProvider.getJob(job.getContext().getIdentifier());
					job.setStatus(JobConstants.JobStatus.ERROR);
					jobDirectoryProvider.updateJob(job);
				} catch (JobException e)
				{
					logger.log(Level.SEVERE, "Failed to update jobContext status");
				}
			}
		}

	}

	@Override
	public int compareTo(JobSubmitTask o)
	{
		return Integer.compare(job.getContext().getPriority().value(), o.job.getContext().getPriority().value());
	}
}
