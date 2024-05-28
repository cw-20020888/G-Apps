package com.kcube.cloud.app.gapi;

import com.kcube.cloud.error.CustomException;

public class CommonGoogleTokenException
{
	public static class InvalidRefreshToken extends CustomException
	{
		private static final long serialVersionUID = -7067968438997456861L;

		public InvalidRefreshToken(String cause)
		{
			super(cause);
		}

		public InvalidRefreshToken(Throwable cause)
		{
			super(cause);
		}
	}
}
