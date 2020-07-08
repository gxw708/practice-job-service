package gowu.job;

import java.util.UUID;

public class JobContextBuilder
{
	private String jobId;
	private String jobDescription;
	private JobConstants.JobType jobType;
	private JobConstants.JobStatus jobStatus;
	private JobConstants.JobPriority priority;
	
	public JobContextBuilder(JobConstants.JobType jobType) 
	{
		this.jobId = UUID.randomUUID().toString();
		this.jobType = jobType;
		this.priority = JobConstants.JobPriority.MEDIUM;
		this.jobStatus = JobConstants.JobStatus.NOT_STARTED;
	}
	
	public JobContextBuilder description(String jobDescription)
	{
		this.jobDescription = jobDescription;
		return this;
	}
	
	public JobContextBuilder lowPriority()
	{
		this.priority = JobConstants.JobPriority.LOW;
		return this;
	}
	
	public JobContextBuilder hightPriority()
	{
		this.priority = JobConstants.JobPriority.HIGH;
		return this;
	}
	
	public JobContext build()
	{
		JobContext jobContext = new JobContext(this.jobId, this.jobDescription, this.jobType, this.jobStatus, this.priority);
		return jobContext;
	}
}
