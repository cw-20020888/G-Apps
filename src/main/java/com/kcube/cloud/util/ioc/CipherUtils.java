package com.kcube.cloud.util.ioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kcube.cloud.pub.CipherAlgorithm;

/**
 * TripleDES 의 key size는 24byte DES는 8byte 나머지는 16Byte
 * @author kskim
 */
public class CipherUtils
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final String KCUBE_PRIVACY_KEY = "7654329876543210";

	private CipherAlgorithm algorithm;

	public CipherAlgorithm getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(CipherAlgorithm algorithm)
	{
		this.algorithm = algorithm;
	}

	public String encryptToHex(String value)
	{
		try
		{
			return bytesToHex(getAlgorithm().encrypt(value, KCUBE_PRIVACY_KEY));
		}
		catch (Exception e)
		{
			//logger.error("CipherUtil.encryptToHex error : {}", e.getMessage());
		}
		return value;
	}

	public String hexToDecrypt(String value)
	{
		try
		{
			return getAlgorithm().decrypt(hexToBytes(value), KCUBE_PRIVACY_KEY);
		}
		catch (Exception e)
		{
			//logger.error("CipherUtil.hexToDecrypt error : {}", e.getMessage());
		}
		return value;
	}

	public byte[] encrypt(String value)
	{
		try
		{
			return getAlgorithm().encrypt(value, KCUBE_PRIVACY_KEY);
		}
		catch (Exception e)
		{
			//logger.error("CipherUtil.encrypt error : {}", e.getMessage());
		}
		return (value != null) ? value.getBytes() : null;
	}

	public String decrypt(byte[] value)
	{
		try
		{
			return getAlgorithm().decrypt(value, KCUBE_PRIVACY_KEY);
		}
		catch (Exception e)
		{
			//logger.error("CipherUtil.hexToDecrypt error : {}", e.getMessage());
		}
		return (value != null) ? new String(value) : null;
	}

	/**
	 * bytes array를 hex string으로 변환한다.
	 */
	public String bytesToHex(byte[] data)
	{
		if (data == null)
		{
			return null;
		}
		int len = data.length;
		StringBuffer str = new StringBuffer();

		for (int i = 0; i < len; i++)
		{
			if ((data[i] & 0xFF) < 16)
			{
				str.append("0").append(Integer.toHexString(data[i] & 0xFF));
			}
			else
			{
				str.append(Integer.toHexString(data[i] & 0xFF));
			}
		}

		return str.toString();
	}

	/**
	 * hex string을 bytes array로 변환한다.
	 */
	public byte[] hexToBytes(String str)
	{
		if (str == null || str.length() < 2)
		{
			return null;
		}
		int len = str.length() / 2;
		byte[] buffer = new byte[len];
		for (int i = 0; i < len; i++)
		{
			buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
		}

		return buffer;
	}
}
