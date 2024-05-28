package com.kcube.cloud.error;

import com.kcube.cloud.pub.enumer.ExceptionCodeEnum;

public class CustomException extends RuntimeException
{
	private static final long serialVersionUID = -2968791948770188807L;

	public CustomException()
	{
		super(ExceptionCodeEnum.CUSTOM.value());
	}

	public CustomException(String cause)
	{
		super(cause);
	}

	public CustomException(Throwable cause)
	{
		super(ExceptionCodeEnum.CUSTOM.value(), cause);
	}

	public static class PermissionDeniedException extends CustomException
	{
		private static final long serialVersionUID = 5807386359245609835L;

		public PermissionDeniedException()
		{
			super();
		}

		public PermissionDeniedException(String message)
		{
			super(message);
		}
	}

	public static class ForbiddenException extends CustomException
	{
		private static final long serialVersionUID = 7572344808770578983L;
	}

	public static class ExpiredException extends CustomException
	{
		private static final long serialVersionUID = 6438094457436940861L;
	}
	
	public static class PageNotFoundException extends CustomException
	{
		private static final long serialVersionUID = 5729514578391418584L;	
	}
	
	public static class FileNotFoundException extends CustomException
	{
		private static final long serialVersionUID = -6638067194153725288L;
	}
}
