package com.kcube.cloud.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

public class TaskSchedulerFactory
{
	private Map<String, List<Object>> factory;

	public TaskSchedulerFactory(Map<String, List<Object>> factory)
	{
		this.factory = factory;
	}

	public void init()
	{
		for (String key : factory.keySet())
		{
			TaskScheduler taskScheduler = (TaskScheduler) factory.get(key).get(0);
			((ThreadPoolTaskScheduler) factory.get(key).get(1)).setErrorHandler(taskScheduler);
		}
	}

	public TaskScheduler getTaskScheduler(String key)
	{
		List<Object> taskSchedulers = factory.get(key);
		if (taskSchedulers != null)
		{
			return (TaskScheduler) taskSchedulers.get(0);
		}
		return null;
	}

	public Map<String, String> getTaskSchedulers()
	{
		Map<String, String> map = new HashMap<String, String>();
		for (String key : factory.keySet())
		{
			TaskScheduler taskScheduler = (TaskScheduler) factory.get(key).get(0);
			map.put(taskScheduler.getKey(), taskScheduler.getName());
		}
		return map;
	}

	public void executeTaskScheduler(String key)
	{
		List<Object> taskSchedulers = factory.get(key);
		if (taskSchedulers != null)
		{
			TaskScheduler taskScheduler = (TaskScheduler) taskSchedulers.get(0);
			((ThreadPoolTaskScheduler) taskSchedulers.get(1)).execute(taskScheduler);
		}
	}

	public void startTaskScheduler(String key, String cron)
	{
		List<Object> taskSchedulers = factory.get(key);
		if (taskSchedulers != null)
		{
			TaskScheduler taskScheduler = (TaskScheduler) taskSchedulers.get(0);
			taskSchedulers.add(
				2,
				((ThreadPoolTaskScheduler) taskSchedulers.get(1)).schedule(taskScheduler, new CronTrigger(cron)));
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean stopTaskScheduler(String key)
	{
		List<Object> taskSchedulers = factory.get(key);
		if (taskSchedulers != null)
		{
			ScheduledFuture scheduledFuture = (ScheduledFuture) taskSchedulers.get(2);
			if (scheduledFuture.cancel(true))
			{
				return taskSchedulers.remove(scheduledFuture);
			}
		}
		return false;
	}
}
