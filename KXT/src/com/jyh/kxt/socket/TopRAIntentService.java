package com.jyh.kxt.socket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jyh.bean.HqDataBean;
import com.jyh.kxt.R;
import com.jyh.tool.Encrypt;

import de.tavendo.autobahn.WebSocket.ConnectionHandler;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

public class TopRAIntentService extends IntentService {
	String TAG = "MyIntentService";
	Thread thr;
	int running = 0;
	static WebSocketConnection wsc;
	private KXTApplication application;
	private List<HqDataBean> datas;
	private Map<String, HqDataBean> mapDatas;
	private boolean isOne = true;
	String a = "{\"crypt\":\"\",\"type\":\"client\",\"code\":[\"CL500XH\",\"CL100XH\",\"CL1000XH\",\"QHO10S\","
			+ "\"QHO20S\",\"QHO50S\",\"SG100S\",\"AG100\",\"AU9999\",\"HG\"]}";
	private SharedPreferences preferences3;
	private String HQSocketUrl;
	private String socketip;
	private RequestQueue queue;
	private MyBinder myBinder = new MyBinder();
	private PendingIntent pintent;
	private List<String> topCodesList;// 存放listview返回的code
	private boolean isAdd = true;// 是否是添加

	private RegTopBroadCast regBroadCast;
	private static AlarmManager am;
	private PendingIntent sender;
	String server = null;
	String token = null;

	public TopRAIntentService() {
		super("MyIntentService");
	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case 100:
				if (NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
					if (null != wsc && !wsc.isConnected()) {
						wsc.reconnect();
					}
				}
				break;
			case 20:
				testNetWork();
				break;
			}
		};
	};

	public class MyBinder extends Binder {

		public TopRAIntentService getService1() {
			return TopRAIntentService.this;
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		topCodesList = (List<String>) intent.getSerializableExtra("tops");
		preferences3 = getApplicationContext().getSharedPreferences("apkinfo",
				Context.MODE_PRIVATE);
		HQSocketUrl = preferences3.getString("hqSocketUrl",
				"http://appapi.kxt.com/info/hq");
		application = (KXTApplication) getApplication();
		if (null != application.getQueue()) {
			queue = application.getQueue();
		} else {
			queue = Volley.newRequestQueue(this);
			application.setQueue(queue);
		}
		if (wsc == null) {
			wsc = new WebSocketConnection();
		}
		testNetWork();
		// Notification notification = new Notification(R.drawable.ic_launcher,
		// "wf update service is running", System.currentTimeMillis());
		pintent = PendingIntent.getService(this, 2, intent, 2);
		// notification.setLatestEventInfo(this, "WF Update Service",
		// "wf update service is running！", pintent);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setDefaults(Notification.DEFAULT_VIBRATE);
		builder.setContentTitle("WF Update Service");
		builder.setContentText("wf update service is running！");
		builder.setWhen(System.currentTimeMillis());
		builder.setContentIntent(pintent);

		// 让该service前台运行，避免手机休眠时系统自动杀掉该服务
		// 如果 id 为 0 ，那么状态栏的 notification 将不会显示。
		startForeground(0, builder.build());
		handler.postDelayed(runnable, 60000);
		return super.onStartCommand(intent, START_STICKY, startId);
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 要做的事情
			if (null != wsc && wsc.isConnected()) {
				wsc.sendTextMessage("");
			}
			handler.postDelayed(this, 60000);
		}
	};
	private StringRequest jsObjRequest;

	private void InitData(String server, String token) {

		if (regBroadCast == null) {
			regBroadCast = new RegTopBroadCast();
			IntentFilter filter = new IntentFilter();
			filter.addAction("topAction");
			registerReceiver(regBroadCast, filter);
		}

		a = "";
		StringBuffer sb = new StringBuffer();

		if (null != topCodesList && topCodesList.size() > 0) {
			sb.append("{\"type\":\"client\",\"crypt\":\"" + token
					+ "\",\"code\":[\"");
			for (int i = 0; i < topCodesList.size(); i++) {
				if (topCodesList.size() > 1) {
					if (i == 0) {
						sb.append(topCodesList.get(i) + "\",");
					} else {
						if (i == topCodesList.size() - 1) {
							sb.append("\"" + topCodesList.get(i) + "\"]}");
						} else {
							sb.append("\"" + topCodesList.get(i) + "\",");
						}
					}
				} else {
					sb.append(topCodesList.get(i) + "\"]\",\"crypt\":\""
							+ token + "\"}");
				}
			}
			a = sb.toString();
			if (!isOne) {
				if (null != wsc && !"".equals(a) && null != a) {
					wsc.sendTextMessage(a);
				} else {
					connect();
				}
			}
		}
		isOne = false;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return myBinder;

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		running = 0;
		topCodesList = null;

		if (null != wsc) {
			wsc.disconnect();
		}
		if (null != am && null != sender) {
			am.cancel(sender);
			am = null;
			sender = null;
		}
		stopForeground(true);
		if (null != regBroadCast) {
			unregisterReceiver(regBroadCast);
		}
		isAdd = true;
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// 经测试，IntentService里面是可以进行耗时的操作的
		// IntentService使用队列的方式将请求的Intent加入队列，然后开启一个worker
		// thread(线程)来处理队列中的Intent
		// 对于异步的startService请求，IntentService会处理完成一个之后再处理第二个
		if (running != 1) {
			running = 1;
			connect();
			while (running == 1) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void connect() {
		if (null != socketip && !socketip.equals("")
				&& NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
			try {
				if (wsc.isConnected()) {
					return;
				}
				wsc.connect(socketip, new ConnectionHandler() {
					@Override
					public void onBinaryMessage(byte[] payload) {
					}

					@Override
					public void onClose(int code, String reason) {
						if (!KXTApplication.IsOut) {
							handler.sendEmptyMessageDelayed(100, 10000);
						}
					}

					@Override
					public void onOpen() {
						if (null != wsc && !"".equals(a) && null != a) {
							wsc.sendTextMessage(a);
						}
					}

					@Override
					public void onRawTextMessage(byte[] payload) {

					}

					@Override
					public void onTextMessage(String payload) {
						if (isAdd) {// 点击事件第一次请求数据
							if (!payload.equals("ok")) {
								try {
									JSONArray array = new JSONArray(payload);

									if (array.length() > 0 && array != null) {

										datas = new ArrayList<HqDataBean>();
										mapDatas = new HashMap<String, HqDataBean>();
										for (int i = 0; i < array.length(); i++) {

											if (null != array.get(i)) {
												JSONObject json = (JSONObject) array
														.get(i);
												HqDataBean data = new HqDataBean();
												data.setCode(json
														.getString("code"));
												data.setName(json
														.getString("name"));
												data.setLast(json
														.getString("last"));
												data.setOpen(json
														.getString("open"));
												data.setLastClose(json
														.getString("lastClose"));
												data.setHigh(json
														.getString("high"));
												data.setLow(json
														.getString("low"));
												data.setSwing(json
														.getString("swing"));
												data.setSwingRange(json
														.getString("swingRange"));
												data.setQuoteTime(json
														.getString("quoteTime"));
												data.setVolume(json
														.getString("volume"));
												mapDatas.put(
														json.getString("code"),
														data);
											}
										}
										sortDatas(mapDatas);
									}
								} catch (Exception e) {
									// 出现异常重新请求服务器
									if (null != wsc && !"".equals(a)) {
										wsc.sendTextMessage(a);
									}
								}
							}

						} else {// 服务器有更新数据过来
							try {
								JSONObject json = new JSONObject(payload);
								HqDataBean data = new HqDataBean();
								data.setCode(json.getString("code"));
								data.setName(json.getString("name"));
								data.setLast(json.getString("last"));
								data.setOpen(json.getString("open"));
								data.setLastClose(json.getString("lastClose"));
								data.setHigh(json.getString("high"));
								data.setLow(json.getString("low"));
								data.setSwing(json.getString("swing"));
								data.setSwingRange(json.getString("swingRange"));
								data.setQuoteTime(json.getString("quoteTime"));
								data.setVolume(json.getString("volume"));
								// 通过广播发送新的数据
								Intent intent = new Intent();
								intent.setAction("newData");
								intent.putExtra("newData", (Serializable) data);
								intent.putExtra("mess", "topUpdate");
								sendBroadcast(intent);// 发送广播，通知页面刷新

							} catch (JSONException e) {
								// TODO Auto-generated catch block
							}
						}
					}
				});
			} catch (WebSocketException e) {
				// TODO Auto-generated catch block
				if (NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
					try {
						Thread.sleep(5 * 1000);
						if (null != wsc && !wsc.isConnected()) {
							wsc.reconnect();
						}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
					}
				}
			}
		} else if (NetworkCenter.checkNetwork_JYH(getApplicationContext())
				&& (null == socketip || socketip.equals(""))) {
			handler.sendEmptyMessageDelayed(20, 3 * 1000);
		} else {

		}
	}

	/**
	 * 测试网络
	 */
	private void testNetWork() {
		if (!NetworkCenter.checkNetworkConnection(getApplicationContext())) {
			// Toast.makeText(getApplicationContext(), "网络异常",
			// Toast.LENGTH_LONG)
			// .show();
		} else {
			running = 0;
			InitSocketIP();
		}
	}

	/**
	 * 对服务器返回的数据进行排序 通过广播发送数据
	 */
	public void sortDatas(Map<String, HqDataBean> mapDatas) {
		/**
		 * 如果数据mapDatas 不为空
		 */
		if (null != mapDatas && mapDatas.size() > 0) {

			// 根据缓存code将数据排序后放入list中
			for (int i = 0; i < topCodesList.size(); i++) {
				if (null != mapDatas.get(topCodesList.get(i))) {
					HqDataBean mBean = mapDatas.get(topCodesList.get(i));
					if (null != mBean.getCode() && !"".equals(mBean.getCode())) {
						datas.add(mBean);
					}
				}
			}
			if (datas.size() != topCodesList.size()) {
				if (null != wsc && !"".equals(a)) {
					wsc.sendTextMessage(a);
				}
			}
			topCodesList.clear();

			Intent intent = new Intent();
			intent.putExtra("datas", (Serializable) datas);
			intent.setAction("newData");
			intent.putExtra("mess", "top");
			sendBroadcast(intent);// 发送广播，通知页面刷新

		}

		isAdd = false;
	}

	private void InitSocketIP() {
		// TODO Auto-generated method stub
		if (NetworkCenter.isNetworkConnected(getApplicationContext())) {
			InitSocketIPVoll(HQSocketUrl);
		}
	}

	private void InitSocketIPVoll(final String url) {
		jsObjRequest = new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				try {
					String data = Encrypt.decrypt(arg0, "kxt");
					JSONObject object = new JSONObject(data);
					server = object.getString("server");
					token = object.getString("token");
					application.TopRAServer = server;
					application.TopRAToken = token;
					socketip = server;
					if (null != server && null != token) {
						InitData(server, token);
					} else {
						handler.sendEmptyMessageDelayed(20, 3 * 1000);
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "连接行情服务器失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "获取行情服务器数据",
						Toast.LENGTH_SHORT).show();
			}
		});
		jsObjRequest.setCacheEntry(null);
		queue.add(jsObjRequest);
	}

	public class RegTopBroadCast extends BroadcastReceiver {

		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (null != intent.getStringExtra("isTop")
					&& intent.getStringExtra("isTop").equals("isTop")) {
				isAdd = intent.getBooleanExtra("isAdd", true);
				if (null != intent.getSerializableExtra("topCodes")) {
					if (null != topCodesList && topCodesList.size() > 0) {
						topCodesList.clear();
					}
					topCodesList = (List<String>) intent
							.getSerializableExtra("topCodes");
					application.setTopCodes(topCodesList);
					testNetWork();
				}

			} else if (null != intent.getStringExtra("enpty")
					&& intent.getStringExtra("enpty").equals("enpty")) {
				if (null != wsc) {
					if (wsc.isConnected()) {
						wsc.sendTextMessage("");
					} else {
						if (NetworkCenter
								.checkNetwork_JYH(getApplicationContext())) {
							wsc.reconnect();
						}
					}
				}
			}
		}

	}

}