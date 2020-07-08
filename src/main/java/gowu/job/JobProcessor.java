package gowu.job;

public interface JobProcessor<T extends JobContext>
{
	void processJob(T job, JobCallback callback) throws JobException;

	default void processJob(T job) throws JobException
	{
		processJob(job,null);
	}

	JobDirectoryProvider getJobDirectoryProvider();
}
