package com.kcube.cloud.pub.event;

import org.springframework.context.ApplicationEvent;

public class EventObject extends ApplicationEvent
{
	private static final long serialVersionUID = -113354523891576220L;

	private Class<?> key;

	public EventObject(Object source, Class<?> key)
	{
		super(source);
		this.key = key;
	}

	public Class<?> getKey()
	{
		return key;
	}
}
