package com.kcube.cloud.error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/error")
public class DefaultExceptionController
{
	@Autowired
	private DefaultExceptionHandler defaultExceptionHandler;

	@RequestMapping(value = "/{status}")
	public ModelAndView handleStatus(HttpServletRequest req, HttpServletResponse res)
	{
		Exception throwable = req.getAttribute("javax.servlet.error.exception") != null
			? (Exception) req.getAttribute("javax.servlet.error.exception")
			: null;

		return defaultExceptionHandler.exceptionHandler(req, res, throwable);
	}
}
