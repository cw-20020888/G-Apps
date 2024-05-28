package com.kcube.cloud.util.ioc;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.pub.VelocityLocaleFilter;

@Component
public class VelocityUtils
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier(value = "velocityEngine")
	private VelocityEngine velocityEngine;

	private ToolContext toolContext;

	@PostConstruct
	public void init()
	{
		ToolManager toolManager = new ToolManager();
		toolManager.configure("velocity-toolbox.xml");
		toolContext = toolManager.createContext();
	}

	/**
	 * String evaluate Object = String
	 * @param inStr
	 * @return
	 */
	public String evaluate(String inStr)
	{
		return this.evaluate(inStr, new HashMap<String, Object>());
	}

	public String evaluate(String inStr, Object pojo)
	{
		return this.evaluate(inStr, this.convertObjectMap(pojo));
	}

	public String evaluate(String inStr, Map<String, Object> context)
	{
		return this.evaluate(inStr, context, true);
	}

	public String evaluate(String inStr, Map<String, Object> context, boolean useEventFilter)
	{
		VelocityContext vContext = new VelocityContext(context, toolContext);
		if (useEventFilter)
		{
			EventCartridge ec = new EventCartridge();
			ec.addEventHandler(new VelocityLocaleFilter());
			ec.attachToContext(vContext);
		}

		StringWriter out = new StringWriter();
		try
		{
			velocityEngine.evaluate(vContext, out, "velocity evaluate", inStr);
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}

		return out.toString();
	}

	/**
	 * VM merge Object = String
	 * @param vmName
	 * @return
	 */
	public String merge(String vmName)
	{
		return this.merge(vmName, new HashMap<String, Object>());
	}

	public String merge(String vmName, Object pojo)
	{
		return this.merge(vmName, this.convertObjectMap(pojo));
	}

	public String merge(String vmName, Map<String, Object> context)
	{
		return this.merge(vmName, context, true);
	}

	public String merge(String vmName, Map<String, Object> context, boolean useEventFilter)
	{
		VelocityContext vContext = new VelocityContext(context, toolContext);
		if (useEventFilter)
		{
			EventCartridge ec = new EventCartridge();
			ec.addEventHandler(new VelocityLocaleFilter());
			ec.attachToContext(vContext);
		}

		StringWriter out = new StringWriter();
		try
		{
			Template template = velocityEngine.getTemplate(vmName, "UTF-8");
			template.merge(vContext, out);
		}
		catch (Exception e)
		{
			throw new DefaultException(e);
		}

		return out.toString();
	}

	public Map<String, Object> convertObjectMap(Object pojo)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		Method[] methods = pojo.getClass().getMethods();
		for (Method method : methods)
		{
			Pattern pattern = Pattern.compile("^get(\\w{1})(.*)");
			Matcher matcher = pattern.matcher(method.getName());
			if (matcher.matches())
			{
				method.setAccessible(true);
				try
				{
					Object value = method.invoke(pojo);
					if (value != null)
					{
						map.put(matcher.group(1).toLowerCase() + matcher.group(2), value);
					}
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					logger.error("VelocityUtil.convertObjectMap error : {}", e.getMessage());
				}
			}
		}

		return map;
	}
}
