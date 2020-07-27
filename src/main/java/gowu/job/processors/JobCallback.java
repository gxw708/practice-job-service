package gowu.job.processors;

import gowu.job.JobResult;
import gowu.job.entities.JobContext;

public interface JobCallback<T extends JobContext>
{

	void onSucceeded(T job, JobResult result);

	void onCancelled(T job);

	void onFailed(T job, JobResult result);

	void onException(T job, Throwable cause);

}
