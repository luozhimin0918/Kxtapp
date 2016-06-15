package com.jyh.kxt.socket;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jyh.bean.NoticAction;
import com.jyh.bean.NoticBean;
import com.jyh.bean.NoticData;
import com.jyh.kxt.R;
import com.jyh.tool.Encrypt;

import de.tavendo.autobahn.WebSocket.ConnectionHandler;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
/**
 * 推送服务类
 * @author PC
 *
 */
public class TestTZIntentService extends IntentService {
	String TAG = "MyIntentService";
	Thread thr;
	int running = 0;
	static WebSocketConnection wsc;
	private String socketip;
	private String token;
	private MyBinder myBinder = new MyBinder();
	private String a;
	private SharedPreferences sp;
	private boolean push_sound;
	private boolean push;
	private Context context;
	private Timer checkLinc;
	private RequestQueue queue;
	private KXTApplication application;
	private String type;
	private Long outTime;
	private alarmreceiver receiver;
	private static AlarmManager am;
	private PendingIntent sender;
	private NotifyReceiver notify = new NotifyReceiver();
	private IntentFilter filter;

	public TestTZIntentService() {
		super("MyIntentService");
	}

	public class MyBinder extends Binder {

		public TestTZIntentService getService1() {
			return TestTZIntentService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		context = getApplicationContext();
		application = (KXTApplication) getApplication();
		if (null != application.getQueue()) {
			queue = application.getQueue();
		} else {
			queue = Volley.newRequestQueue(context);
			application.setQueue(queue);
		}
		if (null == receiver) {
			receiver = new alarmreceiver();
			IntentFilter filter = new IntentFilter("repeating");
			context.registerReceiver(receiver, filter);
		}
		if (wsc == null) {
			wsc = new WebSocketConnection();
		}
		if (null == filter) {
			filter = new IntentFilter();
			registerReceiver(notify, filter);
		}
		startForegroundCompat();
		return super.onStartCommand(intent, START_STICKY, startId);
	}
	/**
	 * 提高存活率处理
	 */
	private void startForegroundCompat() {
		try {
			if (Build.VERSION.SDK_INT < 18) {
				startForeground(1120, new Notification());
			}
		} catch (Exception e) {
		}
	}
	/**
	 * 判断是否连接
	 */
	private void CheckL() {
		if (null == checkLinc) {
			checkLinc = new Timer();
			checkLinc.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (null != wsc && !wsc.isConnected()) {
						wsc.reconnect();
					}
				}
			}, 0, 10 * 1000);
		}
	}

	private void InitData() {
		if (null == am) {
			Intent intent = new Intent();
			intent.setAction("repeating");
			sender = PendingIntent.getBroadcast(context, 0, intent, 0);

			// 开始时间
			long firstime = SystemClock.elapsedRealtime();

			am = (AlarmManager) getSystemService(ALARM_SERVICE);
			am.setRepeating(AlarmManager.RTC_WAKEUP, firstime, 300 * 1000,
					sender);
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
		if (null != am && null != sender) {
			am.cancel(sender);
			am = null;
			sender = null;
		}
		if (null != receiver) {
			context.unregisterReceiver(receiver);
		}
		// stopForeground(true);
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
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 连接服务器操作
	 */
	private void connect() {
		a = "<login>{\"login\":\"" + token + "\"}";
		if (null != socketip && !socketip.equals("")) {
			try {
				wsc.connect("ws://" + socketip, new ConnectionHandler() {

					@Override
					public void onBinaryMessage(byte[] payload) {
					}

					@Override
					public void onClose(int code, String reason) {
						if (null != outTime) {
							Date dt = new Date();
							Long time = dt.getTime();
							if (time > outTime * 1000) {
								if (null != wsc && wsc.isConnected()) {
									wsc.disconnect();
								}
								InitSocketIP();
							} else {
								try {
									Thread.sleep(10000);
									wsc.reconnect();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						} else {
							if (null != wsc && wsc.isConnected()) {
								wsc.disconnect();
							}
							InitSocketIP();
						}
					}

					@Override
					public void onOpen() {
						if (null != wsc && !"".equals(a) && null != a) {
							wsc.sendTextMessage(a);
						}
						InitData();
						CheckL();
					}

					@Override
					public void onRawTextMessage(byte[] payload) {
					}

					@Override
					public void onTextMessage(String payload) {
						sp = context.getSharedPreferences("setup", 0);
						push_sound = sp.getBoolean("sound", true);
						push = sp.getBoolean("push", true);
						try {
							JSONObject object = new JSONObject(payload);
							NotificationKXT notification = new NotificationKXT();
							if (null != object.getJSONObject("action")) {
								JSONObject aciton = object
										.getJSONObject("action");
								if (aciton.getString("code").equals("kx")) {// 快讯
									JSONObject data = aciton
											.getJSONObject("data");
									if (push) {
										notification.send(
												getApplicationContext(),
												R.drawable.ic_launcher, object
														.getString("title"),
												data.getString("time")
														.substring(11, 16),
												push_sound,
												Integer.valueOf(data
														.getString("id")), null);
									}
								} else if (aciton.getString("code")
										.equals("cj")) {// 财经
									JSONObject data = aciton
											.getJSONObject("data");
									NoticData nData = new NoticData();
									nData.setId(data.getString("id"));
									nData.setTime(data.getString("time"));

									NoticAction nAction = new NoticAction();
									nAction.setCode(aciton.getString("code"));
									nAction.setData(nData);

									NoticBean bean = new NoticBean();
									bean.setTitle(object.getString("title"));
									bean.setAction(nAction);

									CjInfo cjInfo = new CjInfo();
									cjInfo.setPredictTime(data
											.getString("time"));
									String[] jsons = data.getString("content")
											.split("#");
									cjInfo.setPredictTime(jsons[0]);
									cjInfo.setNfi(jsons[1]);
									if (jsons[2].toString().contains("%")) {
										String jsonsdata = jsons[2].replace(
												"%", "");
										cjInfo.setBefore(jsonsdata + "%");// qian
									} else {
										cjInfo.setBefore(jsons[2]);// qian
									}
									if (jsons[3].toString().contains("%")) {
										String jsons2 = jsons[3].replace("%",
												"");
										cjInfo.setForecast(jsons2 + "%");
									} else {
										cjInfo.setForecast(jsons[3]);
									}
									if (jsons[4].toString().contains("%")) {
										String jsons2 = jsons[4].replace("%",
												"");
										cjInfo.setReality(jsons2 + "%");// gong
									} else {
										cjInfo.setReality(jsons[4]);// gong
									}
									cjInfo.setNature(jsons[5]);
									String title = jsons[6];
									String[] titles = title.split("\\|");
									if (titles.length > 0 && titles.length == 1) {
										cjInfo.setEffectGood(titles[0]);// 利空
									} else if (titles.length > 0
											&& titles.length == 2) {
										cjInfo.setEffectBad(titles[1]);// 利多
										cjInfo.setEffectGood(titles[0]);// 利空
									} else {
										cjInfo.setEffectMid("影响较小");
									}
									cjInfo.setDate(jsons[7]);
									cjInfo.setState(jsons[8]);
									cjInfo.setId(jsons[9]);
									int todata = Integer.parseInt(jsons[7]
											.substring(0, 8));
									cjInfo.setToDay(todata);

									if (push) {
										if (!"".equals(jsons[6])
												&& null != jsons[6]) {
											notification.sendNoification(
													getApplicationContext(),
													cjInfo, push_sound);
										}
									}
								} else if (aciton.getString("code").equals(
										"news")) {// 文章
									NoticAction nAction = new NoticAction();
									nAction.setCode(aciton.getString("code"));
									nAction.setUrl(aciton.getString("url"));
									nAction.setShareurl(aciton
											.getString("share_url"));
									nAction.setType("news");
									NoticBean bean = new NoticBean();
									bean.setTitle(object.getString("title"));
									bean.setAction(nAction);
									Random randow = new Random();
									if (push) {
										notification.send(
												getApplicationContext(),
												R.drawable.ic_launcher,
												"快讯通财经",
												object.getString("title"),
												push_sound,
												randow.nextInt(100), bean);
									}
								} else if (aciton.getString("code").equals(
										"dianping")) {
									NoticAction nAction = new NoticAction();
									nAction.setCode(aciton.getString("code"));
									nAction.setUrl(aciton.getString("url"));
									nAction.setShareurl(aciton
											.getString("share_url"));
									nAction.setType("dianping");
									NoticBean bean = new NoticBean();
									bean.setTitle(object.getString("title"));
									bean.setAction(nAction);
									Random randow = new Random();
									if (push) {
										notification.send(
												getApplicationContext(),
												R.drawable.ic_launcher,
												"快讯通财经",
												object.getString("title"),
												push_sound,
												randow.nextInt(100), bean);
									}
								} else if (aciton.getString("code").equals(
										"video")) {
									NoticAction nAction = new NoticAction();
									nAction.setCode(aciton.getString("code"));
									nAction.setUrl(aciton.getString("url"));
									nAction.setShareurl(aciton
											.getString("share_url"));
									nAction.setType("video");
									NoticBean bean = new NoticBean();
									bean.setTitle(object.getString("title"));
									bean.setAction(nAction);
									Random randow = new Random();
									if (push) {
										notification.send(
												getApplicationContext(),
												R.drawable.ic_launcher,
												"快讯通财经",
												object.getString("title"),
												push_sound,
												randow.nextInt(10000), bean);
									}
								} else {
									if (push) {
										notification.sendTitle(
												getApplicationContext(),
												R.drawable.ic_launcher,
												"快讯通财经",
												object.getString("title"),
												push_sound);
									}
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							wsc.reconnect();
						}
					}
				});
			} catch (WebSocketException e) {
				try {
					Thread.sleep(5 * 1000);
					if (null != wsc && !wsc.isConnected()) {
						wsc.reconnect();
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else if (null == socketip || socketip.equals("")) {
			InitSocketIP();
		}
	}
	/**
	 * 初始化数据IP地址
	 */
	private void InitSocketIP() {

		sp = context.getSharedPreferences("apkinfo", Context.MODE_PRIVATE);
		String noticurl = sp.getString("tzSocket",
				"http://appapi.kxt.com/info/notice2");
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				noticurl, new Listener<String>() {

					@Override
					public void onResponse(String arg0) {
						try {
							String data = Encrypt.decrypt(arg0.toString(),
									"kxt");
							JSONObject object = new JSONObject(data);
							socketip = object.getString("server");
							token = object.getString("token");
							type = object.getString("type");
							outTime = object.getLong("expire_time");
							if (null != type && type.equals("swoole")) {
								if (null != wsc) {
									if (wsc.isConnected()) {
										wsc.disconnect();
									}
									connect();
								}
							} else {
								if (null != wsc && wsc.isConnected()) {
									wsc.disconnect();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub

					}
				});
		queue.add(stringRequest);

	}
	/**
	 * 定时发送心跳
	 * @author PC
	 *
	 */
	public class alarmreceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("repeating")) {
				if (null != wsc) {
					if (wsc.isConnected()) {
						wsc.sendTextMessage("");
					} else {
						wsc.reconnect();
					}
				}
			}
		}
	}
}