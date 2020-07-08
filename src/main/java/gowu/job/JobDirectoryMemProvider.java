package gowu.job;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobDirectoryMemProvider<T extends JobContext> implements JobDirectoryProvider<T>
{
	private static final Logger logger = Logger.getLogger(JobDirectoryProvider.class.getName());
	HashMap<String, T> jobs;
	public JobDirectoryMemProvider() {
		jobs = new HashMap<String, T>();
	}


	@Override
	public T createJob(T job) throws JobException
	{
		if(job == null)
		{
			logger.log(Level.WARNING, "Job data is null when creating this job");
			throw new JobException(JobException.JobErrorCode.INVALID_INPUT, "Job is null when creating this job");
		}
		
		jobs.put(job.getJobId(), job);
		return job;
	}

	@Override
	public T getJob(String jobId) throws JobException
	{
		if(!jobs.containsKey(jobId))
			throw new JobException(JobException.JobErrorCode.JOB_NOT_FOUND, String.format("Can't find job by the given id %s",jobId));
		
		return jobs.get(jobId);
	}

	@Override
	public boolean updateJobStatus(String jobId, JobConstants.JobStatus status) throws JobException
	{
		getJob(jobId).setJobStatus(status);
		return true;
	}

	@Override
	public boolean cancelJob(String jobId) throws JobException
	{
		getJob(jobId).setJobStatus(JobConstants.JobStatus.REQUEST_FOR_CANCEL);
		return true;
	}

	@Override
	public Timestamp getCurrentTimestamp() throws JobException
	{
		return Timestamp.from(Instant.now());
	}
}
