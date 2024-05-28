package com.kcube.cloud.pub.enumer;

public enum ExceptionCodeEnum implements AbstractEnum<String>
{
	DEFAUTL(0), FILE(1000), SQL(2000), TASK(3000), WEBSERVICE(4000), CUSTOM(5000), WEBSOCKET(6000);

	private int code;

	private ExceptionCodeEnum(int code)
	{
		this.code = code;
	}

	@Override
	public String value()
	{
		// TODO Auto-generated method stub
		return String.valueOf(code);
	}
}
