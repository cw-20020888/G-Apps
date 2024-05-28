package com.kcube.cloud.view;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcube.cloud.error.DefaultException;

public class JsonpView extends MappingJackson2JsonView
{
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String callback = (req.getParameter("callback") != null) ? req.getParameter("callback") : "callback";

			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(filterModel(model));

			res.getWriter().write(callback + "(" + json + ");");
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

}
