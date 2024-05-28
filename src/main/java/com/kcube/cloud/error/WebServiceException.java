package com.kcube.cloud.error;

import com.kcube.cloud.pub.enumer.ExceptionCodeEnum;

public class WebServiceException extends RuntimeException
{
	private static final long serialVersionUID = -2968791948770188807L;

	public WebServiceException()
	{
		super(ExceptionCodeEnum.WEBSERVICE.value());
	}

	public WebServiceException(String cause)
	{
		super(cause);
	}

	public WebServiceException(Throwable cause)
	{
		super(ExceptionCodeEnum.WEBSERVICE.value(), cause);
	}
}
