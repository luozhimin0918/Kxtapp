package com.jyh.kxt.socket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * 日期格式化
 * @author Administrator
 *
 */
public class CjrlDateFormat {
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);   //yyyy-MM-dd HH:mm:ss
	private static SimpleDateFormat sdf_2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);   //yyyy-MM-dd HH:mm:ss
	/** 2014-06-04 16:20*/
	private static SimpleDateFormat sdf_0604 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);   //yyyy-MM-dd HH:mm:ss
	
	/**
	 * 将指定日期格式转换为毫秒值
	 * @param date
	 * @return
	 */
	public static long format(String date){
		if (date.contains("---")) {
			date = date.replace("---", "00:00");  //处理特殊数据:---替换成00:00，排序放在最上面
		}
		
		try {
			Date res = sdf.parse(date);
			long millis = res.getTime();
			return millis;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 将指定日期格式转换为毫秒值
	 * kxt v2
	 * @param date
	 * @return 2014-06-04 16:20
	 */
	public static long format2Millis(String date){
		try {
			Date res = sdf_0604.parse(date);
			long millis = res.getTime();
			return millis;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 2014-06-04 16:20
	 * @param date
	 * @return
	 */
	public static long format_0604(String date){
		try {
			if (date.contains("---")) {
				date = date.replace("---", "00:00");  //对于特殊财经日历数据的转换（2014-07-11 ---转换成2014-07-11 00:00）
			}
			
			Date res = sdf_0604.parse(date);
			long millis = res.getTime();
			return millis;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 点击“日历”图标，计算出今天的日期，作为默认日期
	 * @return 返回一个int数组，int[]{year,month,day}
	 */
	public static int[] getToday_cjrl() {
		Calendar calendar = Calendar.getInstance();    //使用日历类
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		//得到月，因为从0开始，所以要加1
		int day = calendar.get(Calendar.DAY_OF_MONTH);  //得到天
		int[] result = new int[3];   //创建一个3个位子的int数组
		result[0] = year;
		result[1] = month;
		result[2] = day;
		return result;
	}
	
	/**
	 * 返回今天的日期字符串
	 * @return 2014-08-11
	 */
	public static String getToday_cjrl_String() {
		Calendar calendar = Calendar.getInstance();    //使用日历类
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		//得到月，因为从0开始，所以要加1
		int day = calendar.get(Calendar.DAY_OF_MONTH);  //得到天
		
		//int hour = calendar.get(Calendar.HOUR);   //小时
		//int minute = calendar.get(Calendar.MINUTE);  //分钟
		//int second = calendar.get(Calendar.SECOND);  //秒
		
		//int[] result = new int[3];   //创建一个3个位子的int数组
		//result[0] = year;
		//result[1] = month;
		//result[2] = day;
		String monthStr;
		String dayStr;
		if (month<10) {
			monthStr = "0"+month;
		}else {
			monthStr = ""+month;
		}
		
		if (day<10) {
			dayStr = "0"+day;
		}else {
			dayStr = ""+day;
		}
		
		String result = year+"-"+monthStr+"-"+dayStr;
		return result;
	}
	
	/**
	 * 得到昨天的日期，如：2014-06-03
	 * @return
	 */
	public static String getYesterday_cjrl(){
		String today = getToday_cjrl_String();
		long ONE_DAY = 24*60*60*1000;
		
		try {
			long yesterday_cjrl = sdf_2.parse(today).getTime()-ONE_DAY;
			return sdf_2.format(new Date(yesterday_cjrl));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;  //异常，返回null
		
	}
	
	/**
	 * 730
	 * KXT v2
	 * @return 16:20
	 */
	public static String format730(String dateStr){
		try {
			//Date date = sdf.parse(dateStr);
			return sdf.format(sdf_0604.parse(dateStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//日  一   二   三   四   五   六
	//1  2  3  4  5  6  7
	//减一之后:
	//0  1  2  3  4  5  6
	//星期天是0  ！
	/** DAY OF WEEK 需要减一 */
	public static int getDayOfTheWeek(){
		//今天星期几
		//curr
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
	}
}
