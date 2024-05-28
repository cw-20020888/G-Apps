package com.kcube.cloud.error;

import com.kcube.cloud.pub.enumer.ExceptionCodeEnum;

public class DefaultException extends RuntimeException
{
	private static final long serialVersionUID = -7618489243816205225L;

	public DefaultException()
	{
		super(ExceptionCodeEnum.DEFAUTL.value());
	}

	public DefaultException(String cause)
	{
		super(cause);
	}

	public DefaultException(Throwable cause)
	{
		super(ExceptionCodeEnum.DEFAUTL.value(), cause);
	}
}
