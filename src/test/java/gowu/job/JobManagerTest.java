package gowu.job;

import org.junit.*;

import java.util.concurrent.TimeUnit;

public class JobManagerTest
{
	JobManager manager;

	@Before
	public void setup()
	{
		JobDirectoryProvider jdp = new JobDirectoryMemProvider();
		manager = JobManager.getInstance(jdp);
	}

	@Test
	public void testSubmitJob() throws Exception
	{
		JobContext job = makeTestJob();
		String jobId = manager.submitJob(job);
		Assert.assertEquals(JobConstants.JobStatus.NOT_STARTED, manager.getJobStatus(jobId));
		Thread.sleep(100);
		Assert.assertTrue(manager.isJobRunning(jobId));
		Thread.sleep(300);
		Assert.assertTrue(manager.isJobDone(jobId));
		Assert.assertEquals(JobConstants.JobStatus.SUCCEEDED, manager.getJobStatus(jobId));
	}

	private JobContext makeTestJob()
	{
		JobContext job = new JobContextBuilder(JobConstants.JobType.NOOP).description("test job").build();
		return job;
	}

	@Test
	public void testScheduleJob() throws Exception
	{
		JobContext job = makeTestJob();
		String jobId = manager.scheduleJob(job, 1, TimeUnit.SECONDS);
		try
		{
			manager.getJobStatus(jobId);
		} catch (JobException ex)
		{
			if (!JobException.JobErrorCode.JOB_NOT_FOUND.equals(ex.getErrorCode()))
			{
				throw ex;
			}
			// expected
		}
		Thread.sleep(1100);
		Assert.assertTrue(manager.isJobRunning(jobId));
		Thread.sleep(300);
		Assert.assertTrue(manager.isJobDone(jobId));
		Assert.assertEquals(JobConstants.JobStatus.SUCCEEDED, manager.getJobStatus(jobId));
	}

	@Test
	public void testCancelJob() throws Exception
	{
		JobContext job = makeTestJob();
		String jobId = manager.submitJob(job);
		Thread.sleep(50);
		Assert.assertTrue(manager.isJobRunning(jobId));
		Thread.sleep(50);
		manager.cancelJob(job);
		Assert.assertTrue(manager.isJobRequestForCancel(jobId));
		Thread.sleep(110);
		Assert.assertTrue(manager.isJobDone(jobId));
		Assert.assertTrue(manager.isJobCancelled(jobId));
	}
}
