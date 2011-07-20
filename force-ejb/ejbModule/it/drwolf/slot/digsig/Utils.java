package it.drwolf.slot.digsig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	private static Pattern p = Pattern.compile("CN\\s*=([\\w\\s]+)");

	public static String getCN(String dn) {
		Matcher m = Utils.p.matcher(dn);
		if (m.find()) {
			return m.group(m.groupCount());
		}
		return "No CN";
	}

	public static String md5Encode(byte[] signature) {
		try {
			MessageDigest digest;
			digest = MessageDigest.getInstance("MD5");
			digest.update(signature);
			byte messageDigest[] = digest.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			System.out.println("---> MD5: " + hexString.toString());
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static Calendar dateToCalendar(Date date) {
		Date d = new Date();
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		return c;
	}

}
