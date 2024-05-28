package com.kcube.cloud.error;

import com.kcube.cloud.pub.enumer.ExceptionCodeEnum;

public class WebSocketException extends RuntimeException
{
	private static final long serialVersionUID = -4031152759125874881L;

	public WebSocketException()
	{
		super(ExceptionCodeEnum.WEBSOCKET.value());
	}

	public WebSocketException(String cause)
	{
		super(cause);
	}

	public WebSocketException(Throwable cause)
	{
		super(ExceptionCodeEnum.WEBSOCKET.value(), cause);
	}
}
