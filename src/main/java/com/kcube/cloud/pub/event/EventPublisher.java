package com.kcube.cloud.pub.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public abstract class EventPublisher implements ApplicationEventPublisherAware
{
	protected ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher)
	{
		// TODO Auto-generated method stub
		this.publisher = publisher;
	}
}
