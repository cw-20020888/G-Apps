package com.kcube.cloud.pub;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.kcube.cloud.error.DefaultException;

public abstract class CipherAlgorithm
{
	public abstract byte[] encrypt(String value, String strkey);

	public abstract String decrypt(byte[] value, String strkey);

	/**
	 * Blowfish 알고리즘 암복호화
	 */
	public static class Blowfish extends CipherAlgorithm
	{
		@Override
		public byte[] encrypt(String value, String strkey)
		{
			try
			{
				SecretKeySpec key = new SecretKeySpec(strkey.getBytes(), "Blowfish");
				Cipher cipher = Cipher.getInstance("Blowfish");
				cipher.init(Cipher.ENCRYPT_MODE, key);
				return cipher.doFinal(value.getBytes("UTF-8"));
			}
			catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | UnsupportedEncodingException e)
			{
				throw new DefaultException(e);
			}
		}

		@Override
		public String decrypt(byte[] value, String strkey)
		{
			try
			{
				SecretKeySpec key = new SecretKeySpec(strkey.getBytes(), "Blowfish");
				Cipher cipher = Cipher.getInstance("Blowfish");
				cipher.init(Cipher.DECRYPT_MODE, key);
				return new String(cipher.doFinal(value), "UTF-8");
			}
			catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | UnsupportedEncodingException e)
			{
				throw new DefaultException(e);
			}
		}
	}

	/**
	 * DES 알고리즘 암복호화
	 */
	public static class DES extends CipherAlgorithm
	{
		@Override
		public byte[] encrypt(String value, String strkey)
		{
			try
			{
				DESKeySpec desKeySpec = new DESKeySpec(strkey.getBytes());
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				Key key = keyFactory.generateSecret(desKeySpec);
				Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, key);
				return cipher.doFinal(value.getBytes("UTF-8"));
			}
			catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | UnsupportedEncodingException | InvalidKeySpecException e)
			{
				throw new DefaultException(e);
			}
		}

		@Override
		public String decrypt(byte[] value, String strkey)
		{
			try
			{
				DESKeySpec desKeySpec = new DESKeySpec(strkey.getBytes());
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				Key key = keyFactory.generateSecret(desKeySpec);
				Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, key);
				return new String(cipher.doFinal(value), "UTF-8");
			}
			catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | UnsupportedEncodingException | InvalidKeySpecException e)
			{
				throw new DefaultException(e);
			}
		}

	}

	/**
	 * TripleDES 알고리즘 암복호화
	 */
	public static class TripleDES extends CipherAlgorithm
	{
		@Override
		public byte[] encrypt(String value, String strkey)
		{
			try
			{
				DESedeKeySpec desKeySpec = new DESedeKeySpec(strkey.getBytes());
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
				Key key = keyFactory.generateSecret(desKeySpec);
				Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, key);
				return cipher.doFinal(value.getBytes("UTF-8"));
			}
			catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | UnsupportedEncodingException | InvalidKeySpecException e)
			{
				throw new DefaultException(e);
			}
		}

		@Override
		public String decrypt(byte[] value, String strkey)
		{
			try
			{
				DESedeKeySpec desKeySpec = new DESedeKeySpec(strkey.getBytes());
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
				Key key = keyFactory.generateSecret(desKeySpec);
				Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, key);
				return new String(cipher.doFinal(value), "UTF-8");
			}
			catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | UnsupportedEncodingException | InvalidKeySpecException e)
			{
				throw new DefaultException(e);
			}
		}
	}

	/**
	 * AES128 알고리즘 암복호화
	 */
	public static class AES128 extends CipherAlgorithm
	{
		@Override
		public byte[] encrypt(String value, String strkey)
		{
			try
			{
				SecretKeySpec aesKeySpec = new SecretKeySpec(strkey.getBytes(), "AES");
				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
				return cipher.doFinal(value.getBytes("UTF-8"));
			}
			catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | UnsupportedEncodingException e)
			{
				throw new DefaultException(e);
			}
		}

		@Override
		public String decrypt(byte[] value, String strkey)
		{
			try
			{
				SecretKeySpec desKeySpec = new SecretKeySpec(strkey.getBytes(), "AES");
				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, desKeySpec);
				return new String(cipher.doFinal(value), "UTF-8");
			}
			catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | UnsupportedEncodingException e)
			{
				throw new DefaultException(e);
			}
		}
	}

	/**
	 * MD5 알고리즘 암복호화 EmployeeService.
	 * encrypt에서 사용된다. 
	 * Secure one-way hashing된 문자열을 돌려준다.
	 */
	public static class MD5 extends CipherAlgorithm
	{
		@Override
		public byte[] encrypt(String value, String strkey)
		{
			// TODO Auto-generated method stub
			return encrypt(value).getBytes();
		}

		@Deprecated
		@Override
		public String decrypt(byte[] value, String strkey)
		{
			// TODO Auto-generated method stub
			throw new DefaultException();
		}

		public String encrypt(String src)
		{
			try
			{
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				String eip;
				String encodeVal = "";
				String tst = src;
				byte[] bip = md5.digest(tst.getBytes());
				for (int i = 0; i < bip.length; i++)
				{
					eip = "" + Integer.toHexString(bip[i] & 0x000000ff);
					if (eip.length() < 2)
						eip = "0" + eip;
					encodeVal = encodeVal + eip;
				}
				return encodeVal.toUpperCase();
			}
			catch (NoSuchAlgorithmException e)
			{
				throw new DefaultException(e);
			}
		}
	}
}
