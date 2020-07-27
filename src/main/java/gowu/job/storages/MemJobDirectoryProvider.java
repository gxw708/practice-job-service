package gowu.job.storages;

import gowu.job.JobException;
import gowu.job.entities.Job;
import gowu.job.entities.JobIdentifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemJobDirectoryProvider implements JobDirectoryProvider
{
	private static final Logger logger = Logger.getLogger(JobDirectoryProvider.class.getName());
	private final HashMap<JobIdentifier, Job> jobs;
	public MemJobDirectoryProvider() {
		jobs = new HashMap<JobIdentifier, Job>();
	}


	@Override
	public Job createJob(Job job) throws JobException
	{
		if(job == null) { return null; }
		
		JobIdentifier jobIdentifier = job.getContext().getIdentifier();
		if(jobs.containsKey(jobIdentifier))
		{
			Job existing = getJob(jobIdentifier);
			if(existing.isDone())
			{
				// the existing job has done, replace it with the new one
				jobs.put(jobIdentifier, job);
			}
			else
			{
				String msg = String.format("Job identifier %s existed and it's not completed, it can't be submitted again", jobIdentifier);
				logger.log(Level.SEVERE, msg);
				throw new JobException(JobException.JobErrorCode.CREATION_FAILED, msg);
			}
		}
		else
		{
			jobs.put(jobIdentifier, job);
		}
		
		return getJob(jobIdentifier);
	}

	@Override
	public Job updateJob(Job job) throws JobException
	{
		if(job == null) { return null; }
		
		JobIdentifier jobIdentifier = job.getContext().getIdentifier();
		jobs.put(jobIdentifier, job);
		return getJob(jobIdentifier);
	}

	@Override
	public boolean deleteJob(Job job) throws JobException
	{
		if(job == null) { return false; }

		JobIdentifier jobIdentifier = job.getContext().getIdentifier();
		return jobs.remove(jobIdentifier) != null;
	}

	@Override
	public Job getJob(JobIdentifier jobIdentifier) throws JobException
	{
		return jobs.containsKey(jobIdentifier) ? jobs.get(jobIdentifier) : null;
	}

	@Override
	public Timestamp getCurrentTimestamp() throws JobException
	{
		return Timestamp.from(Instant.now());
	}
}
