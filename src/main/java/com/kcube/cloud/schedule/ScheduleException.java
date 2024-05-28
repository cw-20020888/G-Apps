package com.kcube.cloud.schedule;

import com.kcube.cloud.error.CustomException;

public class ScheduleException
{
	public static class CannotReservationException extends CustomException
	{
		private static final long serialVersionUID = 1920430766442026271L;

		public CannotReservationException(String message)
		{
			super(message);
		}
	}

	public static class ValidationPassWordException extends CustomException
	{
		private static final long serialVersionUID = -4078476873196805737L;

		public ValidationPassWordException(String message)
		{
			super(message);
		}
	}

	public static class CannotCalendarEventDeleteException extends CustomException
	{
		private static final long serialVersionUID = 4506742401382524379L;

		public CannotCalendarEventDeleteException(String message)
		{
			super(message);
		}
	}
}
