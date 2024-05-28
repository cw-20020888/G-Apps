package com.kcube.cloud.task;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public abstract class TaskSchedulerHandler implements ErrorHandler
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public void handlePrevExecute()
	{
	}

	public void handleSuccess()
	{
	}

	@Override
	public void handleError(Throwable t)
	{
		// TODO Auto-generated method stub
		logger.error("{} Schedule Error : {}", this.getName(), ExceptionUtils.getStackTrace(t));
	}

	public abstract String getKey();

	public abstract void setKey(String key);

	public abstract String getName();

	public abstract void setName(String name);

}
