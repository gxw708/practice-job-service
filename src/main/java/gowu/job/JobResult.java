package gowu.job;

public class JobResult
{
	private String resultData;
	private String failedMessage;
	private ResultType resultType;

	public JobResult(String resultData, ResultType resultType, String failedMessage)
	{
		this.resultData = resultData;
		this.resultType = resultType;
		this.failedMessage = failedMessage;
	}

	public JobResult(String resultData, ResultType resultType)
	{
		this.resultData = resultData;
		this.resultType = resultType;
	}

	public String getResultData()
	{
		return resultData;
	}

	public void setResultData(String resultData)
	{
		this.resultData = resultData;
	}

	public ResultType getResultType()
	{
		return resultType;
	}

	public void setResultType(ResultType resultType)
	{
		this.resultType = resultType;
	}

	public String getFailedMessage()
	{
		return failedMessage;
	}

	public void setFailedMessage(String failedMessage)
	{
		this.failedMessage = failedMessage;
	}

	public enum ResultType
	{
		SUCCEEDED,
		CANCELLED,
		FAILED
	}
}
