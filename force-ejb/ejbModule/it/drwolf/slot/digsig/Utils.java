package it.drwolf.slot.digsig;

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

}
