package com.kcube.cloud.pub.event;

import org.springframework.context.ApplicationListener;

public abstract class EventListener implements ApplicationListener<EventObject>
{
	private Class<?> key;

	public EventListener(Class<?> key)
	{
		this.key = key;
	}

	public Class<?> getKey()
	{
		return key;
	}

	@Override
	public void onApplicationEvent(EventObject event)
	{
		if (event.getKey().equals(this.getKey()))
		{
			this.onEvent(event.getSource());
		}
	}

	public abstract void onEvent(Object event);
}
