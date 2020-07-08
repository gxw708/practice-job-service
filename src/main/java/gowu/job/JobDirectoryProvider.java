package gowu.job;

import java.sql.Timestamp;

public interface JobDirectoryProvider<T extends JobContext>
{
	T createJob(T job) throws JobException;

	T getJob(String jobId) throws JobException;

	boolean updateJobStatus(String jobId, JobConstants.JobStatus status) throws JobException;
	
	boolean cancelJob(String jobId) throws JobException;

	Timestamp getCurrentTimestamp() throws JobException;
}
