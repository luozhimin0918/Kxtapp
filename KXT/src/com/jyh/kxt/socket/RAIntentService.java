package com.jyh.kxt.socket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
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
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jyh.bean.HqDataBean;
import com.jyh.kxt.R;
import com.jyh.tool.Encrypt;
import de.tavendo.autobahn.WebSocket.ConnectionHandler;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
/**
 * 行情list数据推送服务
 * @author PC
 *
 */
public class RAIntentService extends IntentService {
	String TAG = "MyIntentService";
	Thread thr;
	int running = 0;
	private boolean isFrist = true;
	static WebSocketConnection wsc;
	private KXTApplication application;
	private List<HqDataBean> datas;
	private Map<String, HqDataBean> mapDatas;
	private boolean isOne = true;
	private RegBroadCast regBroadCast;
	private boolean isZx = false;
	String a = "{\"crypt\":\"\",\"type\":\"client\",\"code\":[\"CL500XH\",\"CL100XH\",\"CL1000XH\",\"QHO10S\","
			+ "\"QHO20S\",\"QHO50S\",\"SG100S\",\"AG100\",\"AU9999\",\"HG\"]}";
	private SharedPreferences preferences2;
	private SharedPreferences preferences;
	private SharedPreferences preferences3;
	private SharedPreferences preferences4;
	private String HQSocketUrl;
	private String socketip;

	private MyBinder myBinder = new MyBinder();
	private PendingIntent pintent;
	private String[] codes;
	private List<String> codesList = new ArrayList<String>();// 存放listview返回的code
	private Context context;
	private static AlarmManager am;
	private PendingIntent sender;
	String server = null;
	String token = null;
	private StringRequest jsObjRequest;
	private RequestQueue queue;

	public RAIntentService() {
		super("RAIntentService");
	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case 100:

				if (NetworkCenter.checkNetwork_JYH(context)) {
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

		public RAIntentService getService1() {
			return RAIntentService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		context = getApplicationContext();
		preferences3 = context.getSharedPreferences("apkinfo",
				Context.MODE_PRIVATE);
		preferences4 = context.getSharedPreferences("hqview",
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
		pintent = PendingIntent.getService(this, 0, intent, 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setDefaults(Notification.DEFAULT_VIBRATE);
		builder.setContentTitle("WF Update Service");
		builder.setContentText("wf update service is running！");
		builder.setWhen(System.currentTimeMillis());
		builder.setContentIntent(pintent);
		// 让该service前台运行，避免手机休眠时系统自动杀掉该服务
		// 如果 id 为 0 ，那么状态栏的 notification 将不会显示。
		startForeground(0, builder.build());
		handler.postDelayed(runnable, 2 * 60000);
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
			handler.postDelayed(this, 2 * 60000);
		}
	};
	/**
	 * 初始化数据
	 * @param server
	 * @param token
	 */
	/**
	 * 根据server token 拼接a
	 * 
	 * @param server
	 * @param token
	 */
	private void InitData(String server, String token) {
		if (regBroadCast == null) {
			regBroadCast = new RegBroadCast();
			IntentFilter filter = new IntentFilter();
			filter.addAction("行情注册完成");
			registerReceiver(regBroadCast, filter);
		}
		if (isFrist) {// 如果是第一次请求数据
			a = "";
			StringBuffer sb = new StringBuffer();
			isZx = preferences4.getBoolean("iszx", false);
			if (isZx) {
				preferences2 = context.getSharedPreferences("hqsetup",
						Context.MODE_PRIVATE);
				String value = preferences2.getString("ischeck", "");
				if (null != value && !"".equals(value)) {
					codes = value.split("\\|");
					sb.append("{\"type\":\"client\",\"crypt\":\"" + token
							+ "\",\"code\":[");
					if (null != codes && codes.length > 1) {
						for (int i = 0; i < codes.length; i++) {
							if (codes.length > 1) {
								if (!"".equals(codes[i])) {
									if (i == 0) {
										sb.append(codes[i] + "\",");
									} else {
										if (i == codes.length - 1) {
											sb.append("\"" + codes[i] + "\"]}");
										} else {
											sb.append("\"" + codes[i] + "\",");
										}
									}
								}

							} else {
								sb.append(codes[i] + "\"]}");
							}
						}
						a = sb.toString();
						isFrist = true;
					}
				} else {
					isFrist = false;
					a = "";
				}
			} else {
				if (null != codesList && codesList.size() > 0) {
					sb.append("{\"type\":\"client\",\"crypt\":\"" + token
							+ "\",\"code\":[\"");
					for (int i = 0; i < codesList.size(); i++) {
						if (codesList.size() > 1) {
							if (i == 0) {
								sb.append(codesList.get(i) + "\",");
							} else {
								if (i == codesList.size() - 1) {
									sb.append("\"" + codesList.get(i) + "\"]}");
								} else {
									sb.append("\"" + codesList.get(i) + "\",");
								}
							}
						} else {
							sb.append(codesList.get(i) + "\"]\",\"crypt\":\""
									+ token + "\"}");
						}
					}
					a = sb.toString();
				}
			}
			if (!isOne) {
				if (null != wsc && !TextUtils.isEmpty(a)) {
					if (wsc.isConnected()) {
						isFrist = true;
						wsc.sendTextMessage(a);
					} else {
						connect();
					}
				}
			}
			isOne = false;
		}
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
		super.onDestroy();
		running = 0;
		if (null != wsc) {
			wsc.disconnect();
		}

		if (null != regBroadCast) {
			unregisterReceiver(regBroadCast);
		}
		if (null != am && null != sender) {
			am.cancel(sender);
			am = null;
			sender = null;
		}
		stopForeground(true);
		isFrist = true;
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
	/**
	 * 连接服务操作
	 */
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
						if (isFrist) {// 点击事件第一次请求数据
							if (!payload.equals("ok")) {
								if (application.getIndex() == 0) {
									preferences = context
											.getSharedPreferences(
													"hqFristInfo",
													Context.MODE_PRIVATE);
									preferences.edit()
											.putString("fristHqInfo", payload)
											.commit();
								}
								paresFirstData(payload);
							}
							isFrist = false;

						} else {// 服务器有更新数据过来
							paresUpdateData(payload);
						}
					}

					/**
					 * 解析更新的数据 并通过广播发送给行情
					 * 
					 * @param payload
					 */
					private void paresUpdateData(String payload) {
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
							intent.putExtra("newData", data);
							intent.putExtra("isZx", isZx);
							intent.putExtra("mess", "update");
							sendBroadcast(intent);// 发送广播，通知页面刷新
						} catch (Exception e) {
							paresFirstData(payload);
						}
					}

					/***
					 * 解析服务器返回的数据，并排序
					 * 
					 * @param payload
					 */
					private void paresFirstData(String payload) {
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
										data.setCode(json.getString("code"));
										data.setName(json.getString("name"));
										data.setLast(json.getString("last"));
										data.setOpen(json.getString("open"));
										data.setLastClose(json
												.getString("lastClose"));
										data.setHigh(json.getString("high"));
										data.setLow(json.getString("low"));
										data.setSwing(json.getString("swing"));
										data.setSwingRange(json
												.getString("swingRange"));
										data.setQuoteTime(json
												.getString("quoteTime"));
										data.setVolume(json.getString("volume"));
										mapDatas.put(json.getString("code"),
												data);
									}
								}
								sortDatas(mapDatas);
							}
						} catch (Exception e) {
							if (null != wsc && !"".equals(a)) {
								isFrist = true;
								wsc.sendTextMessage(a);
							}
						}
					}
				});
			} catch (WebSocketException e) {
				// TODO Auto-generated catch block
				if (NetworkCenter.checkNetwork_JYH(context)) {
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

	public class RegBroadCast extends BroadcastReceiver {

		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (null != intent.getStringExtra("reg")
					&& intent.getStringExtra("reg").equals("ok")) {
				intent = new Intent();
				intent.putExtra("datas", (Serializable) datas);
				intent.setAction("newData");
				intent.putExtra("mess", "add");
				sendBroadcast(intent);// 发送广播，通知页面刷新
			} else if (null != intent.getStringExtra("init")
					&& intent.getStringExtra("init").equals("init")) {
				if (null != intent.getSerializableExtra("codes")) {
					if (null != codesList && codesList.size() > 0) {
						codesList.clear();
					}
					codesList = (List<String>) intent
							.getSerializableExtra("codes");
					application.setCodes(codesList);
				}
				isZx = false;
				isFrist = true;
				InitData(server, token);
			} else if (null != intent.getStringExtra("zx")
					&& intent.getStringExtra("zx").equals("ok")) {
				isZx = true;
				isFrist = true;
				InitData(server, token);
			}
		}
	}

	/**
	 * 测试网络
	 */
	private void testNetWork() {
		if (!NetworkCenter.checkNetworkConnection(getApplicationContext())) {
			running = 0;
		} else {
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

			if (isZx) {// 自选
				for (int i = 0; i < codes.length; i++) {
					if (null != mapDatas.get(codes[i])) {
						datas.add(mapDatas.get(codes[i]));
					}
				}
			} else {// 非自选

				for (int i = 0; i < codesList.size(); i++) {
					if (null != mapDatas.get(codesList.get(i))) {
						HqDataBean mBean = mapDatas.get(codesList.get(i));
						if (null != mBean.getCode()
								&& !"".equals(mBean.getCode())) {
							datas.add(mBean);
						}
					} else {
						HqDataBean mBean = new HqDataBean();
						mBean.setCode(codesList.get(i));
						mBean.setLast("0.00");
						mBean.setSwing("0.00");
						mBean.setSwingRange("0.00");
						mBean.setOpen("0.00");
						mBean.setHigh("0.00");
						mBean.setLow("0.00");
						mBean.setLastClose("0.00");
						mBean.setVolume("0.00");
						mBean.setQuoteTime("00:00:00");
						datas.add(mBean);
					}
				}
				codesList.clear();
			}

			Intent intent = new Intent();
			intent.putExtra("isZx", isZx);
			intent.putExtra("mess", "add");
			intent.putExtra("datas", (Serializable) datas);
			intent.setAction("newData");
			sendBroadcast(intent);// 发送广播，通知页面刷新
		}
	}

	private void InitSocketIP() {
		if (NetworkCenter.isNetworkConnected(context)) {
			InitSocketIPVoll(HQSocketUrl);
		}
	}
	/**
	 * 获取服务器ip地址
	 * @param url
	 */
	/**
	 * 连网获取server token
	 * 
	 * @param url
	 */
	private void InitSocketIPVoll(final String url) {
		jsObjRequest = new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String arg0) {
				// TODO Auto-generated method stub
				try {
					String data = Encrypt.decrypt(arg0, "kxt");
					JSONObject object = new JSONObject(data);
					server = object.getString("server");
					token = object.getString("token");
					socketip = server;
					application.RAServer = server;
					application.RAToken = token;
					if (null != server && null != token) {
						InitData(server, token);
					} else {
						handler.sendEmptyMessageDelayed(20, 3 * 1000);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

}