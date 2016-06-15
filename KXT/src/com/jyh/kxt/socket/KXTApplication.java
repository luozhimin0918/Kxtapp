package com.jyh.kxt.socket;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.android.volley.RequestQueue;
import com.jyh.bean.ChannelItem;
import com.jyh.bean.HqBeanData;
import com.jyh.bean.HqDataBean;
import com.jyh.bean.VedioTypeTitle;
import com.jyh.kxt.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class KXTApplication extends Application {

	private List<Activity> activities; // 保存子activity的实例
	private List<ChannelItem> channelItems;// 要闻标题列表
	private List<HqBeanData> hqBeanDatas = new ArrayList<HqBeanData>();
	private KXTSocketService kxtSocketService;
	private RAIntentService raIntentService;
	private int Cunrentfragment;
	private List<HqDataBean> hqDatabean = new ArrayList<HqDataBean>();
	private int index = 0;
	public boolean ischange = false;
	public boolean isfirst = true;
	public boolean HqIsOk = false;
	public boolean isAppcet = false;
	public String RAServer = "";
	public String RAToken = "";
	public String TopRAServer = "";
	public String TopRAToken = "";
	public String flashRAServer = "";
	public String flashRAToken = "";
	private List<String> codes;
	private List<String> topCodes;
	private Map<String, String> mmp = new HashMap<String, String>();
	public String TzServer = "";
	public String TzToken = "";
	private String icon;

	private List<VedioTypeTitle> vedioTypes;
	private RequestQueue queue;// volley请求队列
	public static boolean IsOut = false;// 判断程序是否退出
	private BroadcastReceiver receiver;
	public static Set<String> dpList = new HashSet<String>();// 点击过的点评文章
	public static Set<String> ywList = new HashSet<String>();// 点击过的要闻文章
	public int cjrlMin = -1;
	private String Hq_item;// 主界面快讯超链接跳行情处理
	private Fragment fragment_date;// 日期界面
	private Fragment fragment_hq;// 行情数据界面
	private Fragment fragment_zx;// 行情自选界面
	private Handler mainHander;// 主界面hander用于界面切换
	public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径
	public DisplayImageOptions options;// universal-image-loader-1.8.6-with-sources.jar
										// 图片下载配置

	public Fragment getFragmentZx() {
		return fragment_zx;
	}

	public void setFragmentZx(Fragment fragment_zx) {
		this.fragment_zx = fragment_zx;
	}

	public Fragment getFragmentHq() {
		return fragment_hq;
	}

	public void setFragmentHq(Fragment fragment_hq) {
		this.fragment_hq = fragment_hq;
	}

	public Fragment getFragment() {
		return fragment_date;
	}

	public void setFragment(Fragment fragment_date) {
		this.fragment_date = fragment_date;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}

	public void setCjrlMin(Integer cjrlMin) {
		this.cjrlMin = cjrlMin;
	}

	public Integer getCjrlMin() {
		return cjrlMin;
	}

	public List<VedioTypeTitle> getVedioTypes() {
		return vedioTypes;
	}

	public void setVedioTypes(List<VedioTypeTitle> vedioTypes) {
		this.vedioTypes = vedioTypes;
	}

	public void setRecevier(BroadcastReceiver receiver) {
		this.receiver = receiver;
	}

	public Map<String, String> getMmp() {
		return mmp;
	}

	public void setMmp(Map<String, String> mmp) {
		this.mmp = mmp;
	}

	public List<String> getCodes() {
		return codes;
	}

	public void setCodes(List<String> codes) {
		this.codes = codes;
	}

	public List<String> getTopCodes() {
		return topCodes;
	}

	public void setTopCodes(List<String> topCodes) {
		this.topCodes = topCodes;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<HqDataBean> getHqDatabean() {
		return hqDatabean;
	}

	public void setHqDatabean(List<HqDataBean> hqDatabean) {
		if (hqDatabean != null) {
			if (this.hqDatabean != null) {
				this.hqDatabean.clear();
				this.hqDatabean.addAll(hqDatabean);
			} else {
				this.hqDatabean.addAll(hqDatabean);
			}
		}
	}

	public List<HqBeanData> getHqBeanDatas() {
		return hqBeanDatas;
	}

	public void setHqBeanDatas(List<HqBeanData> hqBeanDatas) {
		this.hqBeanDatas.clear();
		this.hqBeanDatas.addAll(hqBeanDatas);
	}

	public Context getContext() {
		return getApplicationContext();
	}

	public int getCunrentfragment() {
		return Cunrentfragment;
	}

	public void setCunrentfragment(int cunrentfragment) {
		Cunrentfragment = cunrentfragment;
	}

	public KXTSocketService getKxtSocketService() {
		return kxtSocketService;
	}

	public void setKxtSocketService(KXTSocketService kxtSocketService) {
		this.kxtSocketService = kxtSocketService;
	}

	public List<ChannelItem> getChannelItems() {
		return channelItems;
	}

	public RAIntentService getRaIntentService() {
		return raIntentService;
	}

	public void setRaIntentService(RAIntentService raIntentService) {
		this.raIntentService = raIntentService;
	}

	public void setChannelItems(List<ChannelItem> channelItems) {
		this.channelItems = channelItems;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		activities = new ArrayList<Activity>();
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());
		initImageLoader();
		SharedPreferences preferences = getSharedPreferences("wzdata",
				Context.MODE_PRIVATE);
		ywList.addAll(preferences.getStringSet("ywlist", new HashSet<String>()));//
		dpList.addAll(preferences.getStringSet("dplist", new HashSet<String>()));
		Intent in = new Intent();
		in.setAction("MyBroadcastReceiver");
		IsOut = false;
		sendBroadcast(in);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.empty_photo)
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY).build();
	}

	private void initImageLoader() {
		File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
				.getOwnCacheDirectory(this, IMAGE_CACHE_PATH);

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new LruMemoryCache(12 * 1024 * 1024))
				.memoryCacheSize(12 * 1024 * 1024)
				.discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
	}

	public void addAct(Activity act) {
		if (activities != null) {
			activities.add(act);
		}
	}

	public void removeAct(Activity act) {
		if (activities != null) {
			activities.remove(act);
			act.finish();
		}
	}

	public void exitAppAll() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("wzdata",
				Context.MODE_PRIVATE);
		preferences.edit().putStringSet("ywlist", ywList).commit();
		preferences.edit().putStringSet("dplist", dpList).commit();
		IsOut = true;
		for (Activity act : activities) {
			act.finish();
		}
		this.mmp.clear();
		stopService(new Intent(getApplicationContext(), RAIntentService.class));
		stopService(new Intent(getApplicationContext(),
				TopRAIntentService.class));
		stopService(new Intent(getApplicationContext(), KXTSocketService.class));
		getSharedPreferences("hqview", Context.MODE_PRIVATE).edit()
				.putBoolean("iszx", false).commit();
		if (null != receiver) {
			unregisterReceiver(receiver);
			receiver = null;
		}

		System.exit(0);
		System.gc();
	}

	public void exitApp() {
		IsOut = true;
		if (activities != null && activities.size() > 0) {
			for (Activity act : activities) {
				if (!act.getClass().getSimpleName().equals("MainActivity")) {
					act.finish();
				}
			}
		}
		getSharedPreferences("hqview", Context.MODE_PRIVATE).edit()
				.putBoolean("iszx", false).commit();

		stopSer();
	}

	private void stopSer() {
		// **因为后台还有Service在运行，所以KXTApplication没有销毁，所以activities中变量还存在，得清空该集合！
		if (activities != null && activities.size() > 0) {
			activities.clear();
		}
		// 停止服务
		stopService(new Intent(this, KXTSocketService.class));
		System.gc();
	}

	public RequestQueue getQueue() {
		return queue;
	}

	public void setQueue(RequestQueue queue) {
		this.queue = queue;
	}

	public String getHq_item() {
		return Hq_item;
	}

	public void setHq_item(String hq_item) {
		Hq_item = hq_item;
	}

	public Handler getMainHander() {
		return mainHander;
	}

	public void setMainHander(Handler mainHander) {
		this.mainHander = mainHander;
	}

}
