package gowu.job.entities;

import gowu.job.JobConstants;
import gowu.job.JobException;

/**
 * JobContext represents the context data of a Job.
 */
public class JobContext
{
	private JobIdentifier identifier;
	private String description;
	private String payload;
	private JobConstants.JobPriority priority = JobConstants.JobPriority.MEDIUM;
	
	private JobContext(JobIdentifier identifier) throws JobException
	{
		if(identifier == null)
			throw new JobException(JobException.JobErrorCode.INVALID_INPUT, "JobContext's identifier can't be NULL");
		
		this.identifier = identifier;
	}

	public JobIdentifier getIdentifier()
	{
		return identifier;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public JobConstants.JobPriority getPriority()
	{
		return priority;
	}

	public void setPriority(JobConstants.JobPriority priority)
	{
		this.priority = priority;
	}

	public String getPayload()
	{
		return payload;
	}

	public void setPayload(String payload)
	{
		this.payload = payload;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object target)
	{
		if (target == null) return false;
		if (target == this) return true;
		if (target.getClass() != getClass()) return false;
		JobContext that = (JobContext) target;
		return that.getIdentifier().equals(this.getIdentifier());
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();

		buf.append("[JobContext")
				.append(": identifier=").append(identifier)
				.append(", description=").append(description)
				.append(", priority=").append(priority.toString())
				.append("]");

		return buf.toString();
	}
	
	public static class Builder
	{
		private JobIdentifier identifier;
		private String description;
		private String payload;
		private JobConstants.JobPriority priority = JobConstants.JobPriority.MEDIUM;
		
		public Builder(JobIdentifier identifier)
		{
			this.identifier = identifier;
		}
		
		public Builder description(String description)
		{
			this.description = description;
			return this;
		}
		
		public Builder priority(JobConstants.JobPriority priority)
		{
			this.priority = priority;
			return this;
		}
		
		public Builder payload(String payload)
		{
			this.payload = payload;
			return this;
		}
		
		public JobContext build() throws JobException
		{
			JobContext jobContext = new JobContext(identifier);
			jobContext.setDescription(description);
			jobContext.setPriority(priority);
			jobContext.setPayload(payload);
			return jobContext;
		}
	}
}
