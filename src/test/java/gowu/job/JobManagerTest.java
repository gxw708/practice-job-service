package gowu.job;

import gowu.job.entities.JobContext;
import gowu.job.entities.JobIdentifier;
import gowu.job.storages.MemJobDirectoryProvider;
import gowu.job.storages.JobDirectoryProvider;
import org.junit.*;

import java.util.concurrent.TimeUnit;

public class JobManagerTest
{
	JobManager manager;

	@Before
	public void setup()
	{
		JobDirectoryProvider jdp = new MemJobDirectoryProvider();
		manager = JobManager.getInstance(jdp);
	}

	@Test
	public void testSubmitJob() throws Exception
	{
		JobContext jobContext = makeTestJob();
		JobIdentifier jobIdentifier = manager.submitJob(jobContext);
		Assert.assertEquals(JobConstants.JobStatus.NOT_STARTED, manager.getJobStatus(jobIdentifier));
		Thread.sleep(100);
		Assert.assertTrue(manager.isJobRunning(jobIdentifier));
		Thread.sleep(300);
		Assert.assertTrue(manager.isJobDone(jobIdentifier));
		Assert.assertEquals(JobConstants.JobStatus.SUCCEEDED, manager.getJobStatus(jobIdentifier));
	}

	private JobContext makeTestJob() throws JobException
	{
		JobIdentifier testJobIdentifier = JobIdentifier.newInstance("My Test Job", JobConstants.JobType.NOOP);
		JobContext.Builder builder = new JobContext.Builder(testJobIdentifier);
		return builder.build();
	}

	@Test
	public void testScheduleJob() throws Exception
	{
		JobContext job = makeTestJob();
		JobIdentifier jobIdentifier = manager.scheduleJob(job, 1, TimeUnit.SECONDS);
		try
		{
			manager.getJobStatus(jobIdentifier);
		} catch (JobException ex)
		{
			if (!JobException.JobErrorCode.JOB_NOT_FOUND.equals(ex.getErrorCode()))
			{
				throw ex;
			}
			// expected
		}
		Thread.sleep(1100);
		Assert.assertTrue(manager.isJobRunning(jobIdentifier));
		Thread.sleep(300);
		Assert.assertTrue(manager.isJobDone(jobIdentifier));
		Assert.assertEquals(JobConstants.JobStatus.SUCCEEDED, manager.getJobStatus(jobIdentifier));
	}

	@Test
	public void testCancelJob() throws Exception
	{
		JobContext job = makeTestJob();
		JobIdentifier jobIdentifier = manager.submitJob(job);
		Thread.sleep(50);
		Assert.assertTrue(manager.isJobRunning(jobIdentifier));
		Thread.sleep(50);
		manager.cancelJob(jobIdentifier);
		Assert.assertTrue(manager.isJobRequestForCancel(jobIdentifier));
		Thread.sleep(110);
		Assert.assertTrue(manager.isJobDone(jobIdentifier));
		Assert.assertTrue(manager.isJobCancelled(jobIdentifier));
	}
}
