package gowu.job.entities;

import gowu.job.JobConstants;
import gowu.job.JobException;

import java.sql.Timestamp;


/**
 * Job represents the data of a job
 */
public class Job
{
	private JobContext context;
	private JobConstants.JobStatus status = JobConstants.JobStatus.NOT_STARTED;
	private Timestamp requestTime;
	private Timestamp startTime;
	private Timestamp endTime;
	
	private Job(JobContext context) throws JobException
	{
		if(context == null)
			throw new JobException(JobException.JobErrorCode.INVALID_INPUT, "Job's context can't be NULL");
		this.context = context;
	}

	public JobContext getContext()
	{
		return context;
	}

	public void setContext(JobContext context)
	{
		this.context = context;
	}

	public JobConstants.JobStatus getStatus()
	{
		return status;
	}

	public void setStatus(JobConstants.JobStatus status)
	{
		this.status = status;
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

	public boolean isRunning()
	{
		return JobConstants.JobStatus.RUNNING.equals(this.status) ||
				JobConstants.JobStatus.REQUEST_FOR_CANCEL.equals(this.status);
	}
	
	public boolean isDone()
	{
		return JobConstants.JobStatus.CANCELLED.equals(this.status) ||
				JobConstants.JobStatus.FAILED.equals(this.status) ||
				JobConstants.JobStatus.ERROR.equals(this.status) ||
				JobConstants.JobStatus.SUCCEEDED.equals(this.status);
	}

	public boolean isSucceeded()
	{
		return JobConstants.JobStatus.SUCCEEDED.equals(this.status);
	}

	public boolean isCanceled()
	{
		return JobConstants.JobStatus.CANCELLED.equals(this.status);
	}

	public boolean isFailed()
	{
		return JobConstants.JobStatus.FAILED.equals(this.status) ||
				JobConstants.JobStatus.ERROR.equals(this.status);
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((context == null) ? 0 : context.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object target)
	{
		if(target == null)
			return false;

		if(target.getClass() != this.getClass())
			return false;

		Job that = (Job)target;
		if(!that.getContext().equals(this.getContext()))
			return false;

		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();

		buf.append("[Job")
				.append(": context=").append(context)
				.append(", status=").append(status.toString())
				.append(", requestTime=").append(requestTime)
				.append(", startTime=").append(startTime)
				.append(", endTime=").append(endTime)
				.append("]");

		return buf.toString();
	}
	
	public static class Builder
	{
		private JobContext context;
		private JobConstants.JobStatus status = JobConstants.JobStatus.NOT_STARTED;
		private Timestamp requestTime;
		private Timestamp startTime;
		private Timestamp endTime;
		
		public Builder(JobContext context)
		{
			this.context = context;
		}
		
		public Builder status(JobConstants.JobStatus status)
		{
			this.status = status;
			return this;
		}
		
		public Builder requestTime(Timestamp timestamp)
		{
			this.requestTime = timestamp;
			return this;
		}
		
		public Job build() throws JobException
		{
			Job job = new Job(context);
			job.setStatus(status);
			job.setRequestTime(requestTime);
			return job;
		}
	}
}
