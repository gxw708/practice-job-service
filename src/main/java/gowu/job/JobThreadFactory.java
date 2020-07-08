package gowu.job;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class JobThreadFactory implements ThreadFactory
{
	private String threadNamePrefix;
	private AtomicInteger count = new AtomicInteger(0);

	public JobThreadFactory(String prefix)
	{
		if (prefix == null)
			throw new IllegalArgumentException("JobContext thread prefix can't be null.");

		this.threadNamePrefix = prefix;
	}

	@Override
	public Thread newThread(Runnable r)
	{
		Thread t = new Thread(r);
		String threadName = threadNamePrefix + "[" + count.addAndGet(1) + "]";
		t.setName(threadName);
		return t;
	}
}
