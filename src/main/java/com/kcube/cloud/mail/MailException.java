package com.kcube.cloud.mail;

import com.kcube.cloud.error.CustomException;

public class MailException
{
	public static class NotifyMailException extends CustomException
	{
		private static final long serialVersionUID = -1400682673437911165L;

		public NotifyMailException(String message)
		{
			super(message);
		}
	}

	public static class InsertMailLogException extends CustomException
	{
		private static final long serialVersionUID = 1920430766442026271L;

		public InsertMailLogException()
		{
			super();
		}

		public InsertMailLogException(String message)
		{
			super(message);
		}
	}

	public static class UpdateMailLogException extends CustomException
	{
		private static final long serialVersionUID = 1995955160705007016L;

		public UpdateMailLogException(String message)
		{
			super(message);
		}
	}
}
