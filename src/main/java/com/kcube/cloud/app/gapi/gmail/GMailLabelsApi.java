package com.kcube.cloud.app.gapi.gmail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.kcube.cloud.app.gapi.CommonGoogleApi;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.error.DefaultException;

public class GMailLabelsApi extends CommonGoogleApi
{
	private String nextPageToken;

	public GMailLabelsApi(String email, GoogleAuthMethod googleOauth2Method)
	{
		super(email, googleOauth2Method);
	}

	public Label get(String userEmail, String id)
	{
		try
		{
			return createGmailService().users().labels().get(userEmail, id).execute();
		}
		// catch (TokenResponseException te)
		// {
		// throw new CommonGoogleTokenException.InvalidRefreshToken(te);
		// }
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	public List<Message> list(String userEmail, String q, List<String> labelIds)
	{
		return list(userEmail, q, labelIds, null, null);
	}

	@SuppressWarnings("deprecation")
	public List<Message> list(String userEmail, String q, List<String> labelIds, String nextPageToken, Long maxResults)
	{
		try
		{
			Date today = new Date();
			today.setMinutes(today.getMinutes() - 5);

			Gmail service = createGmailService();
			List<Message> messages = new ArrayList<Message>();
			ListMessagesResponse listMessagesResponse = service
				.users().messages().list(userEmail).setLabelIds(labelIds).setQ(q).setMaxResults(maxResults)
				.setPageToken(nextPageToken).execute();

			setNextPageToken(listMessagesResponse.getNextPageToken());

			if (!listMessagesResponse.isEmpty() && listMessagesResponse.getMessages() != null)
			{
				for (Message msg : listMessagesResponse.getMessages())
				{
					Message message = service
						.users().messages().get(userEmail, msg.getId()).setFields("id, payload(headers), internalDate")
						.execute();

					Date messageTime = new Date(message.getInternalDate());
					if (messageTime.getTime() < today.getTime())
					{
						messages.add(message);
					}
				}
			}

			return messages;
		}
		// catch (TokenResponseException te)
		// {
		// throw new CommonGoogleTokenException.InvalidRefreshToken(te);
		// }
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	public String getNextPageToken()
	{
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken)
	{
		this.nextPageToken = nextPageToken;
	}

	public boolean delete(String userEmail, String id)
	{
		boolean result = true;
		try
		{
			createGmailService().users().messages().delete(userEmail, id).execute();
		}
		// catch (TokenResponseException te)
		// {
		// result = false;
		// throw new CommonGoogleTokenException.InvalidRefreshToken(te);
		// }
		catch (Exception e)
		{
			e.printStackTrace();

			result = false;
		}

		return result;
	}
}
