package com.kcube.cloud.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpSslWrapUtils
{
	public static DefaultHttpClient wrap(HttpClient base)
	{
		try
		{
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager()
			{
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException
				{
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException
				{
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}
			};
			ctx.init(null, new TrustManager[] {tm}, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));

			return new DefaultHttpClient(ccm, base.getParams());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
