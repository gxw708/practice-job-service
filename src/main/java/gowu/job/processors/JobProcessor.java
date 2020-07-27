package gowu.job.processors;

import gowu.job.JobException;
import gowu.job.entities.Job;

public interface JobProcessor
{
	public void processJob(Job job) throws JobException;
}
