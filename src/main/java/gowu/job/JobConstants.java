package gowu.job;

public class JobConstants
{
	public static final int EXECUTOR_DEFAULT_MAX_QUEUE_SIZE = 500;
	
	public static final int EXECUTOR_DEFAULT_CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
	
	public static final int EXEXUTOR_DEFAULT_MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();

	public static final String JOB_RESCHEDULED_SUBMITTER = "RescheduledSubmitter";

	public static final String BACKGROUND_JOB_THREAD_NAME = "MyJobThread";

	public enum JobType
	{
		NOOP
	}

	public enum JobStatus
	{
		NOT_STARTED,
		RUNNING,
		SUCCEEDED,
		REQUEST_FOR_CANCEL,
		CANCELLED,
		FAILED,
		ERROR
	}

	public enum JobPriority
	{

		HIGH(1),
		MEDIUM(10),
		LOW(999);

		private final int value;

		JobPriority(int value)
		{
			this.value = value;
		}

		public int value()
		{
			return value;
		}
	}
}
