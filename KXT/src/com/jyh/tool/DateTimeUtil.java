package com.jyh.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
	private static SimpleDateFormat sdf_temp1 = new SimpleDateFormat(
			"yyyy-MM-dd"); // yyyy-MM-dd 
	private static SimpleDateFormat sdf_temp2 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss"); // yyyy-MM-dd 
	private static SimpleDateFormat sdf_temp3 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm"); // yyyy-MM-dd 
	/**
	 * @param time
	 * @return 2014/06/10
	 */
	public static String parseMillis(String time) {
		long long_time = Long.parseLong(time); // 这个可以
		String res = sdf_temp1.format(new Date(long_time*1000));
		return res;
	}
	/**
	 * @param time
	 * @return 2014/06/10 16:27
	 */
	public static String parseMillis2(String time) {
		long long_time = Long.parseLong(time); // 这个可以
		String res = sdf_temp3.format(new Date(long_time*1000));
		return res;
	}
	/**
	 * @param time
	 * @return 2014/06/10 16:27:08
	 */
	public static String FlashparseMillis(String time) {
		long long_time = Long.parseLong(time); // 这个可以
		String res = sdf_temp2.format(new Date(long_time*1000));
		return res;
	}
}
