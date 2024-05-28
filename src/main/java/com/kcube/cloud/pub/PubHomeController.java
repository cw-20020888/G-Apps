package com.kcube.cloud.pub;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kcube.cloud.pub.enumer.AuthorityEnum;
import com.kcube.cloud.user.UserSession;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Controller
public class PubHomeController
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@RequestMapping(value = "/")
	public String defaultPage(HttpServletRequest req)
	{
		/*local 테스트 일경우*/
		return "forward:/schedule";

		/* 실계 반영시*/
		/*if (UserSession.getCurrentUser().getAuthorities().contains(Long.parseLong(AuthorityEnum.makeAdminXid("3"))))
		{
			return "forward:/mobile/schedule";
		}
		else
		{
			return "forward:/schedule";
		}*/
	}
	@RequestMapping(value = "/admin")
	public String admin()
	{
		return "redirect:/admin/mail";
	}
	/*coway 공용회의실 사용을 위해 추가*/
	@RequestMapping(value = "/cauth")
	public String cauthPage()
	{
		return "redirect:/schedule";
	}

	@RequestMapping(value = "/cwlogin")
	public String cwlogin()
	{
		return "coway/cwlogin";
	}

	@RequestMapping(value = "/cwlogin", method=RequestMethod.POST)
	public String cwlogin(
			HttpServletRequest request,
			HttpServletResponse Response,
			@RequestParam(value="username", required=false) String username,
			@RequestParam(value="password", required=false) String password
						  )
	{
		System.out.println("username : " + username);
		System.out.println("password : " + password);


		String queryString = "id=" + username + "&pwd=" + password;

		URL url = null;
		try {
			url = new URL("https://tools.coway.do/api/room/login");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		}
		};

		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			sc.init(null, trustAllCerts, new SecureRandom());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		try {
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			HttpsURLConnection con = null;
			con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			// HTTP POST 메소드 설정
			con.setRequestProperty("User-Agent", request.getHeader("User-Agent"));
			con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			con.setDoOutput(true);
			// POST 파라미터 전달을 위한 설정 // Send post request
			DataOutputStream wr = null;
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(queryString);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JsonParser json = new JsonParser();
			JsonObject jobj = (JsonObject) json.parse(response.toString());
			//응답 코드 200이 아니면

			System.out.println(jobj.toString());
			System.out.println(jobj.get("code").toString());
			System.out.println(jobj.get("message").toString());
			System.out.println(jobj.get("token").toString());

			//String token = jobj.get("token").toString().replaceAll("\"","");
			String token = jobj.get("token").toString().replaceAll("\"","");
			Cookie CWIAM = new Cookie("CWIAM", token);
			CWIAM.setMaxAge(60);
			CWIAM.setPath("/"); // 모든 경로에서 접근 가능 하도록 설정
			Response.addCookie(CWIAM);
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
		return "redirect:/cauth";
	}
}
