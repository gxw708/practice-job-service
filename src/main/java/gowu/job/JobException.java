package gowu.job;

public class JobException extends Exception
{
	private static final long serialVersionUID = 8419091890056602218L;
	private JobErrorCode code;
	private String errorMessage;
	public JobException(JobErrorCode code, String message)
	{
		super(message);
		this.code = code;
		this.errorMessage = message;
	}


	public JobException(JobErrorCode code, String message, Exception ex)
	{
		super(ex);
		this.code = code;
		this.errorMessage = message;
	}

	public JobErrorCode getErrorCode()
	{
		return this.code;
	}

	public String getErrorMessage()
	{
		return this.code.toString() + " : " + this.errorMessage;
	}

	public enum JobErrorCode
	{
		INVALID_INPUT,
		JOB_NOT_FOUND,
		CREATION_FAILED,
		JOB_REJECTED,
		INTERNAL_ERROR,
		JOB_TYPE_NOT_SUPPORTED,
		JOB_EXECUTION_FAILED,
		INTERRUPTED
	}
}
