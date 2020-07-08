package gowu.job;

public interface JobCallback<T extends JobContext>
{

	void onSucceeded(T job, JobResult result);

	void onCancelled(T job);

	void onFailed(T job, JobResult result);

	void onException(T job, Throwable cause);

}
