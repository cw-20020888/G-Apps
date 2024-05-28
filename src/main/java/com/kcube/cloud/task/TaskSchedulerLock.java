package com.kcube.cloud.task;

import org.springframework.stereotype.Component;

import com.kcube.cloud.error.TaskException;

@Component
public class TaskSchedulerLock
{
	public int getLock(String key)
	{
		return -1;
	}

	public void releaseLock(String key)
	{
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e)
		{
			throw new TaskException(e);
		}
	}
}
