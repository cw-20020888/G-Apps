package com.kcube.cloud.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kcube.cloud.error.TaskException;

public abstract class TaskScheduler extends TaskSchedulerHandler implements Runnable
{
	@Autowired
	private TaskSchedulerLock taskSchedulerLock;

	private String key;
	private String name;

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, RuntimeException.class})
	public void run()
	{
		try
		{
			Integer result = taskSchedulerLock.getLock(this.getKey());
			logger
				.debug("========================================start===============================================");
			logger.debug(
				"key : "
					+ this.getKey()
					+ ", result : "
					+ result
					+ ", time : "
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()));
			logger
				.debug("============================================================================================");
			if (result > 0)
			{
				this.handlePrevExecute();
				if (this.execute())
				{
					this.handleSuccess();
				}
			}
		}
		finally
		{
			taskSchedulerLock.releaseLock(this.getKey());
			logger
				.debug("========================================end=================================================");
			logger.debug("time : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()));
			logger
				.debug("============================================================================================");
		}
	}

	public abstract boolean execute() throws TaskException;
}
