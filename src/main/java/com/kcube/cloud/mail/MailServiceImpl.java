package com.kcube.cloud.mail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.UrlUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.Members;
import com.google.api.services.gmail.model.Message;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.app.gapi.directory.DirectoryUsersApi;
import com.kcube.cloud.app.gapi.gmail.GMailLabelsApi;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.mail.MailException.InsertMailLogException;
import com.kcube.cloud.user.UserSession;
import com.kcube.cloud.util.HttpSslWrapUtils;
import com.kcube.cloud.util.NetmarbleApiUtils;
import com.kcube.cloud.util.ioc.VelocityUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("mailItemService")
public class MailServiceImpl implements MailService
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("appProp")
	private Properties googleAuthProp;

	@Autowired
	private VelocityUtils velocityUtils;

	@Autowired
	private MessageSourceAccessor messageSource;

	@Override
	public void deleteMailItems(List<Map<String, String>> items)
	{
		try
		{
			for (Map<String, String> item : items)
			{
				boolean isDelete = false;

				String to = item.get("to"); // 수신자
				String cc = StringUtils.isNotEmpty(item.get("cc")) ? item.get("cc") : ""; // 참조
				String bcc = StringUtils.isNotEmpty(item.get("bcc")) ? item.get("bcc") : ""; // 숨은참조

				if (StringUtils.isNotEmpty(to))
				{
					isDelete = mailDeleteByReceiver(to, item);
				}
				else
				{
					isDelete = true;
				}
				if (StringUtils.isNotEmpty(cc) && isDelete)
				{
					isDelete = mailDeleteByReceiver(cc, item);
				}
				if (StringUtils.isNotEmpty(bcc) && isDelete)
				{
					isDelete = mailDeleteByReceiver(bcc, item);
				}

				if (isDelete)
				{
					notifyMail("성공", item);
				}
				else
				{
					notifyMail("실패", item);
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{
			new DefaultException(e);
		}
	}

	@Override
	public void insertMailLog(List<Map<String, String>> items, String opinion) throws DefaultException
	{
		try
		{
			for (Map<String, String> item : items)
			{
				String id = item.get("id");
				String subject = item.get("subject");
				String from = item.get("from");
				String users = item.get("users");

				String to = StringUtils.isNotEmpty(item.get("to")) ? item.get("to").replaceAll(",", ";") : ""; // 수신자
				String cc = StringUtils.isNotEmpty(item.get("cc")) ? item.get("cc").replaceAll(",", ";") : ""; // 참조
				String bcc = StringUtils.isNotEmpty(item.get("bcc")) ? item.get("bcc").replaceAll(",", ";") : ""; // 숨은참조

				if (StringUtils.isNotEmpty(users))
				{
					List<NameValuePair> arguments = new ArrayList<NameValuePair>();
					arguments.add(new BasicNameValuePair("ItemID", id));
					arguments.add(new BasicNameValuePair("Subject", subject));
					arguments.add(new BasicNameValuePair("From", from));
					arguments.add(new BasicNameValuePair("To", to));
					arguments.add(new BasicNameValuePair("CC", cc));
					arguments.add(new BasicNameValuePair("BCC", bcc));
					arguments.add(new BasicNameValuePair("Owners", users.replaceAll(",", ";")));
					arguments.add(new BasicNameValuePair("SendDT", item.get("fullDate")));
					arguments.add(new BasicNameValuePair("Requestor", UserSession.getCurrentUser().getIntraEmail()));
					arguments.add(new BasicNameValuePair("Reason", opinion));

					HttpPost httpPost = NetmarbleApiUtils.getHttpPost(
						googleAuthProp.getProperty("netmarble.schema"),
						googleAuthProp.getProperty("netmarble.host"),
						"/GApps/InsertGmailCollectRequest.ashx",
						arguments);

					JSONObject result = JSONObject.fromObject(
						HttpSslWrapUtils.wrap(new DefaultHttpClient()).execute(httpPost, new BasicResponseHandler()));

					if (!result.getBoolean("result"))
					{
						throw new InsertMailLogException();
					}
				}
			}
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	@Override
	public JSONArray findMailLog(String email)
	{
		try
		{
			HttpGet httpGet = NetmarbleApiUtils.getHttpGet(
				googleAuthProp.getProperty("netmarble.schema"),
				googleAuthProp.getProperty("netmarble.host"),
				"/GApps/GetGmailCollectList.ashx",
				"UserEmail=" + email);

			JSONArray result = JSONArray.fromObject(
				HttpSslWrapUtils.wrap(new DefaultHttpClient()).execute(httpGet, new BasicResponseHandler()));

			return result;
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	private boolean mailDeleteByReceiver(String user, Map<String, String> item)
	{
		user = user.replaceAll(" ", "");

		boolean isDelete = true;
		String id = item.get("id");

		DirectoryUsersApi directoryUsersService = new DirectoryUsersApi(
			googleAuthProp.getProperty("google.adminEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);
		if (user.indexOf(",") > -1)
		{
			String[] users = user.split(",");
			for (String u : users)
			{
				updateMailLogApi(u, id, deleteByGroupMemberOrUser(directoryUsersService, u, item));
			}
		}
		else
		{
			updateMailLogApi(user, id, deleteByGroupMemberOrUser(directoryUsersService, user, item));
		}

		return isDelete;
	}

	private int deleteByGroupMemberOrUser(DirectoryUsersApi service, String receiver, Map<String, String> item)
	{
		boolean isDelete = true;
		String id = item.get("id");
		String from = item.get("from");
		String msgId = item.get("msgId");
		Members membersObject = service.getMembersObject(receiver);

		/**
		 * Group...
		 */
		if (membersObject != null)
		{
			List<Member> members = membersObject.getMembers();
			if (CollectionUtils.isNotEmpty(members))
			{
				for (Member m : members)
				{
					String memberEmail = m.getEmail();
					if (Pattern.matches(".*@(" + messageSource.getMessage("sys.domains") + ")", memberEmail))
					{
						if ("GROUP".equalsIgnoreCase(m.getType()))
						{
							int result = deleteByGroupMemberOrUser(service, memberEmail, item);
							if (result < 0)
							{
								return -1;
							}
						}
						else
						{
							if (service.get(memberEmail) != null)
							{
								isDelete = mailDeleteService(memberEmail, from, id, msgId);
								if (!isDelete)
								{
									return -1;
								}
							}
						}
					}
				}
			}
		}
		else
		{
			if (service.get(receiver) != null)
			{
				isDelete = mailDeleteService(receiver, from, id, msgId);
				if (!isDelete)
				{
					return -1;
				}
			}
		}

		return 1;
	}

	private boolean mailDeleteService(String receiver, String from, String id, String msgId)
	{
		GMailLabelsApi service = new GMailLabelsApi(receiver, GoogleAuthMethod.SERVICE_ACCOUNT);
		List<Message> messages = service.list("me", "Rfc822msgid:" + msgId, Arrays.asList("INBOX"));

		for (Message message : messages)
		{
			if (!receiver.equals(from))
			{
				return service.delete(receiver, message.getId());
			}
		}

		return true;
	}

	private void updateMailLogApi(String receiver, String id, int status)
	{
		try
		{
			List<NameValuePair> arguments = new ArrayList<NameValuePair>();
			arguments.add(new BasicNameValuePair("ItemID", id));
			arguments.add(new BasicNameValuePair("Owner", receiver));
			arguments.add(new BasicNameValuePair("Status", String.valueOf(status)));

			HttpPost httpPost = NetmarbleApiUtils.getHttpPost(
				googleAuthProp.getProperty("netmarble.schema"),
				googleAuthProp.getProperty("netmarble.host"),
				"/GApps/UpdateGmailCollectItem.ashx",
				arguments);

			JSONObject result = JSONObject.fromObject(
				HttpSslWrapUtils.wrap(new DefaultHttpClient()).execute(httpPost, new BasicResponseHandler()));

			if (!result.getBoolean("result"))
			{
				logger.error(result.toString());
			}
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	private void notifyMail(String status, Map<String, String> item) throws UnsupportedEncodingException
	{
		String subject = " [ " + item.get("subject") + " ] 메일을 회수" + status + " 하였습니다.";
		String users = item.get("users");
		String from = item.get("from");

		if (users.indexOf(",") > -1)
		{
			users = users.indexOf(",") > -1 ? users.split(",")[0] + "외 " + (users.split(",").length - 1) + "명" : users;
		}

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("status", status);
		context.put("title", subject);
		context.put("from", from);
		context.put("to", users);

		String message = velocityUtils.merge("mailcollect.vm", context);
		String sender = googleAuthProp.getProperty("google.mail.sender");

		try
		{
			List<NameValuePair> arguments = new ArrayList<NameValuePair>();
			arguments.add(new BasicNameValuePair("uri", "msg/v10/email"));
			arguments.add(new BasicNameValuePair("from", sender));
			arguments.add(new BasicNameValuePair("to", from));
			arguments.add(new BasicNameValuePair("title", subject));
			arguments.add(new BasicNameValuePair("content", UrlUtils.urlEncode(message)));
			arguments.add(new BasicNameValuePair("IsHtml", "true"));

			HttpPost httpPost = NetmarbleApiUtils.getHttpPost(
				googleAuthProp.getProperty("netmarble.schema"),
				googleAuthProp.getProperty("netmarble.host"),
				"/Bridge4HelloAPI.ashx",
				arguments);

			JSONObject result = JSONObject.fromObject(
				HttpSslWrapUtils.wrap(new DefaultHttpClient()).execute(httpPost, new BasicResponseHandler()));

			if (result.getInt("result") != 0)
			{
				logger.error(result.toString());
			}
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}
}
