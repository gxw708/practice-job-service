package gowu.job;

import java.sql.Timestamp;

/**
 * JobContext represents the information of a Job.
 */
public class JobContext
{
	private String jobId;
	private String jobDescription;
	private JobConstants.JobType jobType;
	private JobConstants.JobStatus jobStatus;
	private JobConstants.JobPriority jobPriority;
	private Timestamp requestTime;
	private Timestamp startTime;
	private Timestamp endTime;
	
	public JobContext(String jobId, String jobDescription, JobConstants.JobType jobType, JobConstants.JobStatus jobStatus, JobConstants.JobPriority jobPriority)
	{
		this.jobId = jobId;
		this.jobDescription = jobDescription;
		this.jobType = jobType;
		this.jobStatus = jobStatus;
		this.jobPriority = jobPriority;
	}

	public String getJobId()
	{
		return jobId;
	}

	public String getJobDescription()
	{
		return jobDescription;
	}

	public void setJobDescription(String jobDescription)
	{
		this.jobDescription = jobDescription;
	}

	public JobConstants.JobType getJobType()
	{
		return jobType;
	}

	public JobConstants.JobStatus getJobStatus()
	{
		return jobStatus;
	}

	public void setJobStatus(JobConstants.JobStatus jobStatus)
	{
		this.jobStatus = jobStatus;
	}

	public Timestamp getRequestTime()
	{
		return requestTime;
	}

	public void setRequestTime(Timestamp requestTime)
	{
		this.requestTime = requestTime;
	}

	public Timestamp getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Timestamp startTime)
	{
		this.startTime = startTime;
	}

	public Timestamp getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Timestamp endTime)
	{
		this.endTime = endTime;
	}

	public JobConstants.JobPriority getJobPriority()
	{
		return jobPriority;
	}

	public void setJobPriority(JobConstants.JobPriority jobPriority)
	{
		this.jobPriority = jobPriority;
	}

	public boolean isPending()
	{
		return jobStatus == JobConstants.JobStatus.NOT_STARTED ||
				jobStatus == JobConstants.JobStatus.RUNNING;
	}

	public boolean isRunning()
	{
		return jobStatus == JobConstants.JobStatus.RUNNING;
	}

	public boolean isWaiting()
	{
		return jobStatus == JobConstants.JobStatus.NOT_STARTED;
	}

	public boolean isCanceled()
	{
		return jobStatus == JobConstants.JobStatus.CANCELLED;
	}

	public boolean hasSucceded()
	{
		return jobStatus == JobConstants.JobStatus.SUCCEEDED;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((jobId == null) ? 0 : jobId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null) return false;
		if (o == this) return true;
		if (o.getClass() != getClass()) return false;
		JobContext e = (JobContext) o;
		return getJobId().equals(e.getJobId());
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();

		buf.append("[JobContext")
				.append(": jobId=").append(jobId)
				.append(", jobType=").append(jobType.toString())
				.append(", jobDescription=").append(jobDescription)
				.append(", jobPriority=").append(jobPriority.toString())
				.append(", requestTime=").append(requestTime)
				.append(", startTime=").append(startTime)
				.append(", endTime=").append(endTime)
				.append("]");

		return buf.toString();
	}
}
