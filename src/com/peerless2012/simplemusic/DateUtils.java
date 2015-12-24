package com.peerless2012.simplemusic;


public class DateUtils {
	public static String changSecondsToTime(long seconds) {
		if (seconds <= 0) {
			return "00:00:00";
		}
		StringBuffer time = new StringBuffer();

		time.append(retainInteger(seconds / 3600, 2));
		time.append(":");
		time.append(retainInteger((seconds % 3600) / 60, 2));
		time.append(":");
		time.append(retainInteger(seconds % 60, 2));

		return time.toString();
	}
	
	public static String retainInteger(long result, int digit) {

		String str = String.valueOf(result);
		if (str.length() < digit) {
			for (int i = 0; i < digit - str.length(); i++) {
				str = "0" + str;
			}
		}

		return str;

	}
}
