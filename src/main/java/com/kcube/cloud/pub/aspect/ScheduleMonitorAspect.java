package com.kcube.cloud.pub.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kcube.cloud.schedule.ScheduleMonitorServiceSecurity;

@Aspect
public class ScheduleMonitorAspect
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ScheduleMonitorServiceSecurity scheduleMonitorServiceSecurity;

	@Before("execution(* com.kcube.cloud.schedule.ScheduleMonitorController.*(..))")
	public void BeforeScheduleMonitor(JoinPoint joinPoint)
	{
		scheduleMonitorServiceSecurity.checkAllowAddr();
	}
}
