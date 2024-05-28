package com.kcube.cloud.error;

import com.kcube.cloud.pub.enumer.ExceptionCodeEnum;

public class TaskException extends RuntimeException
{
	private static final long serialVersionUID = 4952142042599250111L;

	public TaskException()
	{
		super(ExceptionCodeEnum.TASK.value());
	}

	public TaskException(String cause)
	{
		super(cause);
	}

	public TaskException(Throwable cause)
	{
		super(ExceptionCodeEnum.TASK.value(), cause);
	}
}
