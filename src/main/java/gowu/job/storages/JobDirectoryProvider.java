package gowu.job.storages;

import gowu.job.JobConstants;
import gowu.job.JobException;
import gowu.job.entities.Job;
import gowu.job.entities.JobContext;
import gowu.job.entities.JobIdentifier;

import java.sql.Timestamp;
import java.util.List;

public interface JobDirectoryProvider<T extends JobContext>
{
	Job createJob(Job job) throws JobException;

	Job updateJob(Job job) throws JobException;

	boolean deleteJob(Job job) throws JobException;

	Job getJob(JobIdentifier jobIdentifier) throws JobException;

	Timestamp getCurrentTimestamp() throws JobException;
}
