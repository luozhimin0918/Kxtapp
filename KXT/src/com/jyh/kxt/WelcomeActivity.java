package com.jyh.kxt;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jyh.bean.ChannelItem;
import com.jyh.bean.HqBeanData;
import com.jyh.bean.HqChildren;
import com.jyh.bean.MainAd;
import com.jyh.bean.TopBean;
import com.jyh.bean.VedioTypeTitle;
import com.jyh.kxt.R;
import com.jyh.kxt.customtool.BounceTopEnter;
import com.jyh.kxt.customtool.MaterialDialog;
import com.jyh.kxt.customtool.OnBtnClickL;
import com.jyh.kxt.customtool.SlideBottomExit;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.kxt.socket.VersionManager;
import com.umeng.analytics.MobclickAgent;

/**
 * 欢迎界面（ 展示欢迎界面、展示广告、获取视频栏目 、要闻的栏目）
 * 
 * @author Administrator
 *
 */
public class WelcomeActivity extends Activity {
	private KXTApplication application;
	public static boolean isLoadingInit = false;
	private List<ChannelItem> channelItems = new ArrayList<ChannelItem>();
	private VersionManager versionManager;
	private SharedPreferences preferences;
	private boolean isEnter = false;
	private static MaterialDialog testDialog;
	private BounceTopEnter bas_in;
	private SlideBottomExit bas_out;
	private static final String VedioGetUrl = "http://appapi.kxt.com/Video/nav";// 获取视频栏目
	private static final String TagURL = "http://appapi.kxt.com/data/tags";// 获取要闻栏目
	private static final String QuotesURL = "http://appapi.kxt.com/Data/quotes_list";// 获取行情列表
	private static final String AdURL = "http://appapi.kxt.com/Index/config?system=android&version=2.1";// 获取广告地址
	private RequestQueue queue;

	@SuppressWarnings("unused")
	private boolean IsFirstError = true;
	private JsonObjectRequest jsObjRequest;
	private List<JsonObjectRequest> jss = new ArrayList<JsonObjectRequest>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter);
		MobclickAgent.setDebugMode(true);
		bas_in = new BounceTopEnter();
		bas_out = new SlideBottomExit();
		application = (KXTApplication) getApplication();

		if (null == application.getQueue()) {
			queue = Volley.newRequestQueue(this);
			application.setQueue(queue);
		} else {
			queue = application.getQueue();
		}
		versionManager = VersionManager.getInstance();
		testDialog = new MaterialDialog(this);

		if (NetworkCenter.checkNetwork_JYH(this)) {
			versionManager.checkVersion(this, handler);
			handler.sendEmptyMessageDelayed(70, 4 * 1000);
		} else {
			handler.sendEmptyMessageDelayed(40, 2 * 1000);
		}
		handler.sendEmptyMessageDelayed(40, 5 * 1000);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 30:
				Toast.makeText(WelcomeActivity.this, "获取版本信息异常",
						Toast.LENGTH_SHORT).show();
				TagsTask(TagURL, 1);
				break;
			case 40:
				if (!isEnter) {
					if (!testDialog.isShowing()) {
						testDialog.content("当前网络不稳定，请检查手机网络")//
								.btnText("取消", "确定")//
								.showAnim(bas_in)//
								.dismissAnim(bas_out)//
								.show();
						testDialog.setOnBtnClickL(new OnBtnClickL() {// left btn
									@Override
									public void onBtnClick() {
										testDialog.dismiss();
										application.exitAppAll();
									}
								}, new OnBtnClickL() {// right btn click
									// listener
									@Override
									public void onBtnClick() {
										Intent intent = null;
										// 先判断当前系统版本
										if (android.os.Build.VERSION.SDK_INT > 10) { // 3.0以上
											intent = new Intent(
													android.provider.Settings.ACTION_WIRELESS_SETTINGS);
										} else {
											intent = new Intent();
											intent.setClassName(
													"com.android.settings",
													"com.android.settings.WirelessSettings");
										}
										startActivity(intent);
										testDialog.dismiss();
										application.exitAppAll();
									}
								});
						testDialog.setCanceledOnTouchOutside(false);
					}
				}
				break;
			case 60:
				// 启动更新
				preferences = getSharedPreferences("versions",
						Context.MODE_PRIVATE);
				String description = preferences.getString("description",
						"快讯通有新版啦");
				final String versionurl = preferences.getString("versionurl",
						"http://kxt.com/down.html");
				if (!testDialog.isShowing()) {
					testDialog.content(description)//
							.btnText("取消", "确定")//
							.showAnim(bas_in)//
							.dismissAnim(bas_out)//
							.show();
					testDialog.setOnBtnClickL(new OnBtnClickL() {// left btn
								@Override
								public void onBtnClick() {
									handler.sendEmptyMessageDelayed(70,
											2 * 1000);
									testDialog.dismiss();
								}
							}, new OnBtnClickL() {// right btn click listener
								@Override
								public void onBtnClick() {
									Intent intent = new Intent();
									intent.setAction(Intent.ACTION_VIEW);
									intent.setData(Uri.parse(versionurl));
									startActivity(intent);
									testDialog.dismiss();
									application.exitAppAll();
								}
							});
					testDialog.setCanceledOnTouchOutside(false);
				}
				break;
			case 70:
				TagsTask(TagURL, 1);
				break;
			case 80:
				if (null != testDialog && testDialog.isShowing()) {
					testDialog.dismiss();
				}
				testDialog = null;
				versionManager.DesSelf();
				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				startActivity(intent);
				for (int i = 0; i < jss.size(); i++) {
					jss.get(i).cancel();
				}
				finish();
				break;
			case 111:
				finish();
				break;
			default:
				break;
			}
		};
	};

	private void loadMainUI() {
		handler.sendEmptyMessageDelayed(80, 1000);
	}

	/**
	 * 根据url请求数据
	 * 
	 * @param url
	 * @param i
	 */
	private void TagsTask(final String url, final int i) {
		jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ResolveData(response, i);
						IsFirstError = true;
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessageDelayed(80, 1000);
					}

				});
		jsObjRequest.setCacheEntry(null);
		jss.add(jsObjRequest);
		queue.add(jsObjRequest);
	}

	public void ResolveData(JSONObject jsondata, int i) {
		try {
			JSONArray array;
			switch (i) {
			case 1:
				array = jsondata.getJSONArray("data");
				if (null != channelItems) {
					channelItems.clear();
				}
				for (int k = 0; k < array.length(); k++) {
					JSONObject object = (JSONObject) array.get(k);
					ChannelItem channelItem = new ChannelItem();
					channelItem.setId(object.getString("id"));
					channelItem.setName(object.getString("name"));
					if (!channelItems.contains(channelItem)) {
						channelItems.add(channelItem);
					}
				}
				ChannelItem channelItem = new ChannelItem("0", "全部");
				if (!channelItems.contains(channelItem)) {
					channelItems.add(0, channelItem);
				}
				application.setChannelItems(channelItems);
				TagsTask(VedioGetUrl, 2);
				break;
			case 2:
				List<VedioTypeTitle> titles = new ArrayList<VedioTypeTitle>();
				array = jsondata.getJSONArray("data");
				titles = JSON
						.parseArray(array.toString(), VedioTypeTitle.class);
				application.setVedioTypes(titles);
				TagsTask(QuotesURL, 3);
				break;
			case 3:
				List<TopBean> topBeans = new ArrayList<TopBean>();
				List<HqBeanData> hqBeanDatas = new ArrayList<HqBeanData>();
				array = jsondata.getJSONArray("data");
				for (int k = 0; k < array.length(); k++) {
					List<HqChildren> hqChildrens = new ArrayList<HqChildren>();
					JSONObject object = (JSONObject) array.get(k);
					HqBeanData hqBeanData = new HqBeanData();
					if (k == 0) {
						if (null != object.getJSONArray("top")) {
							JSONArray topArray = object.getJSONArray("top");
							for (int t = 0; t < topArray.length(); t++) {
								JSONObject topObject = (JSONObject) topArray
										.get(t);
								TopBean topBean = new TopBean();
								topBean.setCode(topObject.getString("code"));
								topBean.setName(topObject.getString("name"));
								topBeans.add(topBean);
							}
						}
					}
					if (null != object.getString("children")) {
						JSONArray childrenArray = object
								.getJSONArray("children");
						for (int z = 0; z < childrenArray.length(); z++) {
							JSONObject childrenObject = (JSONObject) childrenArray
									.get(z);
							HqChildren hqChildren = new HqChildren();
							hqChildren
									.setCode(childrenObject.getString("code"));
							hqChildren
									.setName(childrenObject.getString("name"));
							hqChildrens.add(hqChildren);
						}
					}
					hqBeanData.setCode(object.getString("code"));
					hqBeanData.setName(object.getString("name"));
					hqBeanData.setChildren(hqChildrens);
					if (null != topBeans) {
						hqBeanData.setTopBeans(topBeans);
					}
					hqBeanDatas.add(hqBeanData);
				}
				application.setHqBeanDatas(hqBeanDatas);
				TagsTask(AdURL, 4);
				break;
			case 4:
				isEnter = true;
				JSONObject object = jsondata.getJSONObject("data")
						.getJSONObject("load_ad");
				MainAd ad = null;
				if (null != jsondata.getJSONObject("data").getJSONObject(
						"load_ad")) {
					if (null != object.getString("image")
							&& !object.getString("image").equals("")) {
						ad = new MainAd();
						ad.setImage(object.getString("image"));
						ad.setUrl(object.getString("url"));
					}
				}

				if (null != jsondata.getJSONObject("data")
						.getJSONObject("appinfo").getString("icon")) {
					application.setIcon(jsondata.getJSONObject("data")
							.getJSONObject("appinfo").getString("icon"));

				}
				JSONObject dataobject = jsondata.getJSONObject("data")
						.getJSONObject("appinfo");
				preferences = getSharedPreferences("apkinfo",
						Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("hqSocket",
						dataobject.getString("hq_chart_url"));
				editor.putString("hqSocketUrl",
						dataobject.getString("hq_socket_url"));
				editor.putString("tzSocket",
						dataobject.getString("notice_socket_url"));
				editor.putString("flashSocket",
						dataobject.getString("alerts_socket_url"));
				editor.commit();
				if (ad != null) {
					Intent intent = new Intent(WelcomeActivity.this,
							AdActivity.class);
					intent.putExtra("image", ad.getImage());
					intent.putExtra("url", ad.getUrl());
					startActivity(intent);
					finish();
				} else {
					loadMainUI();
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessageDelayed(80, 1000);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SplashScreen"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		isEnter = true;
		MobclickAgent.onPageEnd("SplashScreen"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		// onPageEnd 在onPause
		// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			}
		} catch (Exception ex) {
		}
	}
}
