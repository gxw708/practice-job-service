package gowu.job.entities;

import gowu.job.JobConstants;
import gowu.job.JobException;

import java.util.UUID;

public class JobIdentifier
{
	private String name;
	private JobConstants.JobType type;
	
	private JobIdentifier(String name, JobConstants.JobType type) throws JobException
	{
		if(name == null || type == null)
			throw new JobException(JobException.JobErrorCode.INVALID_INPUT, "JobIdentifier's name or type can't be NULL");
		
		this.name = name;
		this.type = type;
	}
	
	@Override
	public boolean equals(Object target)
	{
		if(target == null) return false;
		if (target == this) return true;
		if(target.getClass() != this.getClass()) return false;
		
		JobIdentifier that = (JobIdentifier)target;
		
		return that.getType().equals(this.getType()) && that.getName().equals(this.getName());
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		result += PRIME * result + ((type == null) ? 0 : type.toString().hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();

		buf.append("[JobIdentifier")
				.append(": name=").append(name)
				.append(", type=").append(type.toString())
				.append("]");

		return buf.toString();	
	}
	
	public String getName()
	{
		return name;
	}

	public JobConstants.JobType getType()
	{
		return type;
	}
	
	public static JobIdentifier newInstance(String name, JobConstants.JobType type) throws JobException
	{
		return new JobIdentifier(name, type);
	}
}
