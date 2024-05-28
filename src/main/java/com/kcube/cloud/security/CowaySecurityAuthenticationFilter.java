package com.kcube.cloud.security;

import com.google.api.client.json.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.app.gapi.directory.DirectoryUsersApi;
import com.kcube.cloud.pub.enumer.AuthorityEnum;
import com.kcube.cloud.user.User;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;
import jdk.nashorn.internal.parser.JSONParser;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

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
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CowaySecurityAuthenticationFilter extends AbstractAuthenticationProcessingFilter  {
    @Autowired
    @Qualifier("appProp")
    private Properties googleProp;

    public CowaySecurityAuthenticationFilter(String defaultFilterProcessesUrl)
    {
        super(defaultFilterProcessesUrl);
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        String accessToken = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                if ("CWIAM".equals(cookie.getName()))
                {
                    accessToken = cookie.getValue();
                }
            }
        }
        return getUsernamePasswordAuthenticationToken(
                request,
                response,
                accessToken,
                request.getHeader("User-Agent"));
    }

    @Override
    public void afterPropertiesSet()
    {
    }

    public UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(
            HttpServletRequest request,
            HttpServletResponse Response,
            String accessToken,
            String userAgent) throws ProtocolException {
        String exclusion = googleProp.getProperty("exclusion_coway");
        String queryString = "{\"token\":\"" + accessToken + "\"}" ;
        URL url = null;
        try {
            url = new URL("https://tools.coway.do/api/room/check/auth");
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
            con.setRequestProperty("User-Agent", userAgent);
            con.setRequestProperty("Content-Type","application/json");
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
            JsonObject jobj = (JsonObject)json.parse(response.toString());
            //응답 코드 200이 아니면
            if (Integer.parseInt(jobj.get("code").toString()) != 200) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null)
                {
                    for (Cookie cookie : cookies)
                    {
                        if ("CWIAM".equals(cookie.getName()))
                        {
                            Cookie CWIAM = new Cookie("CWIAM", "");
                            CWIAM.setMaxAge(0);
                            CWIAM.setPath("/"); // 모든 경로에서 접근 가능 하도록 설정
                            Response.addCookie(CWIAM);
                        }
                    }
                }
                throw new InternalAuthenticationServiceException(jobj.get("message").toString());
            }
            JsonObject data = (JsonObject)jobj.get("user");
            DirectoryUsersApi service = new DirectoryUsersApi(googleProp.getProperty("google.pubEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);
            List<User.Building> buildings = service.getUserBuildings("my_customer", exclusion);
            List<User.Resource> resources = service.getUserResources("my_customer");

            User user = new User();
            user.setId(data.get("userId").toString().replaceAll("\"",""));
            user.setFullName(data.get("userName").toString().replaceAll("\"",""));
            user.setPrimaryEmail(googleProp.getProperty("google.pubEmail"));
            user.setIntraEmail(data.get("email").toString().replaceAll("\"",""));
            user.setGoogleLogin(false);
            user.setLocation(null);
            user.setBuildings(buildings);
            user.setResources(resources);

            List<GrantedAuthority> grantedAuthoritys = new ArrayList<GrantedAuthority>();

            UserAgent agent = UserAgent.parseUserAgentString(userAgent);
            if (DeviceType.MOBILE.getName().equals(agent.getOperatingSystem().getDeviceType().getName()))
            {
                grantedAuthoritys.add(new SimpleGrantedAuthority(AuthorityEnum.makeAdminXid("3")));
            }
            else
            {
                grantedAuthoritys.add(new SimpleGrantedAuthority(AuthorityEnum.makeAdminXid("2")));
            }

            return new UsernamePasswordAuthenticationToken(user, "", grantedAuthoritys);

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null ;
    }
}
