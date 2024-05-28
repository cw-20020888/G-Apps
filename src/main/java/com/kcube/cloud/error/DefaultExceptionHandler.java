package com.kcube.cloud.error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.kcube.cloud.error.CustomException.FileNotFoundException;

@ControllerAdvice
public class DefaultExceptionHandler
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler(value = {Exception.class, RuntimeException.class})
	public ModelAndView exceptionHandler(HttpServletRequest req, HttpServletResponse res, Exception ex)
	{
		ModelAndView mav = new ModelAndView();
		String accept = req.getHeader("Accept");
		if (accept.indexOf(MediaType.APPLICATION_JSON) > -1)
		{
			mav.setViewName("json");
			mav.addObject("message", ex.getMessage());
		}
		else
		{
			mav.setViewName("error/error");
		}
		mav.addObject("exception", ex != null ? ex.getClass().getName() : null);
		mav.addObject("exceptionStack", ExceptionUtils.getFullStackTrace(ex));

		if (ex != null && ex instanceof CustomException)
		{
			if (ex instanceof FileNotFoundException)
			{
				res.setStatus(HttpStatus.NOT_FOUND.value());
			}
			else
			{
				res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		}
		else
		{
			res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		logger.error(ExceptionUtils.getFullStackTrace(ex));

		return mav;
	}
}
