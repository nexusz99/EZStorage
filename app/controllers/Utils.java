package controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import play.Logger;

public class Utils {
	public static String md5(String str)
	{
		String hash=null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte bs[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : bs) {
				sb.append(String.format("%02x", b & 0xff));
			}
			hash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			Logger.debug("MD5 Hashing fail", e);
		}
		return hash;
	}
}
