package com.kcube.cloud.app.cronjob;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kcube.cloud.task.TaskSchedulerFactory;

@Controller
@RequestMapping("/cronjob")
public class CronJobController
{
	@Autowired
	private TaskSchedulerFactory taskSchedulerFactory;

	@RequestMapping("/{key}")
	public void execute(HttpServletResponse res, @PathVariable String key)
	{
		taskSchedulerFactory.getTaskScheduler(key).execute();
		res.setStatus(HttpStatus.OK.value());
	}
}