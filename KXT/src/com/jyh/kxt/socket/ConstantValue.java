package com.jyh.kxt.socket;

public interface ConstantValue {
	/**
	 * socket服务器IP地址
	 */
	String SERVER_IP = "soft.txhuangjin.com";
	
	/**
	 * socket服务器端口
	 */
	int PORT = 1224;
	
	//Message
	int MESSAGE = 1;
	int COMPLETE = 2;
	
	/**
	 * 编码
	 */
	String UTF8 = "UTF-8";
	String GBK = "GBK";
	String GB2312 = "GB2312";
	
	/**
	 * 已经更新过
	 */
	boolean alreadyUpdated = false;
	
	//--------------------------
	/** 正常更新地址 */
	String updateVersionServerUrl = "http://appapi.kxt.com/app/info.xml";
	
	
	/**【由于签名问题，导致1.0用户无法升级(1.0用的是debug签名)】
	 * 所以将这5000个1.0用户独立出来，单独一个下载更新路径！！
	 * */
	
	/** debug更新地址，适用于1.0老用户 */
	String updateVersionServerUrl_debug_1_0 = "http://kuaixun360.com/app/debug/info.xml";
	
	String EXIT_APP = "exit";
	
	String NEW_MSG = "new push msg";
	
	String PUSH_MSG_CJRL = "push msg cjrl";
	
	String HISTORY_DATA = "zx and cjrl received over";
	
	/** 主页面刷新*/
	String REFRESH_OVER = "refresh over in MainActivity";
	
	/** 主页面加载更多*/
	String LOADMORE = "loadMore in MainActivity";
	
	/** 某一天的财经日历*/
	String ONEDAYCJRL = "one day cjrl";
	
	/** 关闭正在加载的dialog*/
	String CLOSE_DIALOG = "close the loading dialog";
	
	//国家
	String STATE_CHINA = "中国";
	String STATE_AMERICAN = "美国";
	String STATE_JAPAN = "日本";
	String STATE_ITALY = "意大利";
	
	String STATE_ENGLAND = "英国";
	String STATE_GERMAN = "德国";
	String STATE_SWITZERLAND = "瑞士";
	String STATE_FRANCE = "法国";
	
	String STATE_NEWZEALAND = "新西兰";
	String STATE_CANADA = "加拿大";
	String STATE_AUSTRALIA = "澳大利亚";
	String STATE_HONGKONG = "香港";
	
	String STATE_KOREA = "韩国";
	String STATE_EURO_AREA = "欧元区";
	String STATE_NEW_ZEALAND = "新西兰";
	String STATE_TAIWAN = "台湾";
	
	String STATE_SPANISH = "西班牙";
	String STATE_SINGAPORE = "新加坡";
	
	String STATE_BRAZIL = "巴西";
	String STATE_RUSSIA = "俄罗斯";
	String STATE_INDIA = "印度";
	String STATE_INDONESIA = "印度尼西亚";
	String STATE_SOUTH_AFRICA = "南非";
	
	String STATE_GREECE = "希腊";
	String STATE_ISRAEL = "以色列";
	
	//财经日历-重要性
	String CJRL_IMPORTANTANCE_HIGH = "高";
	String CJRL_IMPORTANTANCE_MID = "中";
	String CJRL_IMPORTANTANCE_LOW = "低";
	
}
