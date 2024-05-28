package com.kcube.cloud.error;

import com.kcube.cloud.pub.enumer.ExceptionCodeEnum;

public class FileException extends RuntimeException
{
	private static final long serialVersionUID = 5694566799588821862L;

	public FileException()
	{
		super(ExceptionCodeEnum.FILE.value());
	}
	
	public FileException(String cause)
	{
		super(cause);
	}

	public FileException(Throwable cause)
	{
		super(ExceptionCodeEnum.FILE.value(), cause);
	}
}
