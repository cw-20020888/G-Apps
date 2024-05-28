package com.kcube.cloud.pub.enumer;

public enum DataStoreKindEnum implements AbstractEnum<String>
{
	REFRESH_TOKEN, SCHEDULE;

	@Override
	public String value()
	{
		// TODO Auto-generated method stub
		return this.name();
	}
}