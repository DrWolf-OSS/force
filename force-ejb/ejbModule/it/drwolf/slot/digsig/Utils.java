package it.drwolf.slot.digsig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	private static Pattern cn = Pattern.compile("CN\\s*=([\\w\\s]+)");
	private static Pattern cf = Pattern
			.compile("SERIALNUMBER\\s*=([\\w:\\w]+)");

	public static String getCN(String dn) {
		Matcher m = Utils.cn.matcher(dn);
		if (m.find()) {
			return m.group(m.groupCount());
		}
		return "No CN";
	}

	public static String getCF(String dn) {
		Matcher m = Utils.cf.matcher(dn);
		if (m.find()) {
			return m.group(m.groupCount());
		}
		return "No CF";
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
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static Calendar dateToCalendar(Date date) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		return c;
	}

	// public static String decodeDocumentFileName(String encoded) {
	// // String encoded = getRealFileName();
	// String name = encoded;
	// String extension = "";
	// int dotIndex = encoded.lastIndexOf(".");
	// if (dotIndex != -1) {
	// extension = encoded.substring(dotIndex);
	// name = encoded.substring(0, dotIndex);
	// }
	//
	// int underscoreIndex = encoded.lastIndexOf("_");
	// String fileName = encoded;
	// if (underscoreIndex != -1) {
	// fileName = encoded.substring(0, underscoreIndex);
	// } else {
	// fileName = name;
	// }
	// return fileName.concat(extension);
	// }

}
