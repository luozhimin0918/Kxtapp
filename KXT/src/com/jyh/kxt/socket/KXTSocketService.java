package com.jyh.kxt.socket;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jyh.gson.bean.Flash_CjInfo;
import com.jyh.gson.bean.Flash_ZxImgInfo;
import com.jyh.gson.bean.ZxInfo;
import com.jyh.tool.Encrypt;
import com.jyh.tool.SelfCallBack;

import de.tavendo.autobahn.WebSocket.ConnectionHandler;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
/**
 * 快讯推送服务
 * @author PC
 *
 */
public class KXTSocketService extends IntentService {
	public KXTSocketService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public KXTSocketService() {
		// TODO Auto-generated constructor stub
		super("KXTSocketService");
	}

	SelfCallBack selfCallBack;
	FlashService flashService = new FlashService();
	static WebSocketConnection wsc;
	private List<String> data = new ArrayList<String>();
	private KXTApplication application;
	private static int k = 0;
	private String Flash_URL = "http://121.199.70.5:8888/gate";
	private String IP_URL = "";

	private RequestQueue queue;
	private JsonArrayRequest jsonArrayRequest;
	private int running = 1;
	private Intent intent = new Intent();
	private Gson gson;
	private static boolean IsOk = false;
	Random random = new Random();

	public class FlashService extends Binder {
		public KXTSocketService Getservire() {
			return KXTSocketService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStart(intent, startId);
		handler.postDelayed(runnable, 60000);
		return super.onStartCommand(intent, START_STICKY, startId);
	}

	private String GetToken() {
		// TODO Auto-generated method stub 3-12
		String Token = "";
		String key = "";
		String[] randomData = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
				"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
				"v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6",
				"7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
				"V", "W", "X", "Y", "Z" };

		for (int i = 0; i < 8; i++) {
			key = key + randomData[random.nextInt(randomData.length)];
		}
		try {
			Token = key.substring(0, 4)
					+ Encrypt
							.encrypt(
									"{\"app\" : \"kxt_android\",\"mark\" : \"yimingdashadiao\",\"cmd\" : \"login\",\"version\" : 3}",
									key, 0) + key.substring(4, key.length());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
		}
		return Token;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return flashService;
	}

	private void GetIPUrl(final String Url) {
		// TODO Auto-generated method stub
		gson = new Gson();
		application = (KXTApplication) getApplication();
		if (null == application.getQueue()) {
			queue = Volley.newRequestQueue(this);
			application.setQueue(queue);
		} else {
			queue = application.getQueue();
		}
		data.clear();
		jsonArrayRequest = new JsonArrayRequest(Url, new Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray arg0) {
				// TODO Auto-generated method stub
				try {
					JSONArray array = arg0;
					for (int i = 0; i < array.length(); i++) {
						String object = (String) array.get(i);
						data.add(object);
					}
					k = 0;
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				k++;
				if (k < 2) {
					handler.sendEmptyMessageDelayed(1, 10 * 1000);
				} else {
				}
			}

		});
		jsonArrayRequest.setCacheEntry(null);
		queue.add(jsonArrayRequest);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
					GetIPUrl(Flash_URL);
				} else {
					Toast.makeText(getApplicationContext(), "网络异常",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 30:
				if (null != wsc && wsc.isConnected()) {
					wsc.sendTextMessage(GetToken());
				}
				break;
			default:
				break;
			}
		};
	};

	protected void GetRandomIP() {
		// TODO Auto-generated method stub

		if (data.size() > 0
				&& NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
			int i = random.nextInt(data.size());
			IP_URL = "ws://" + data.get(i);
			connect(IP_URL);
		}
	}

	private void connect(String SocketIP) {
		try {
			if (wsc == null) {
				wsc = new WebSocketConnection();
			}
			if (wsc.isConnected()) {
				return;
			}
			wsc.connect(SocketIP, new ConnectionHandler() {
				int a = 0;

				@Override
				public void onBinaryMessage(byte[] payload) {

				}

				@SuppressWarnings("static-access")
				@Override
				public void onClose(int code, String reason) {
					// IsOk = false;
					if (NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
						a++;
						IsOk = true;
						try {
							Thread.sleep(10 * 1000);
							if (!application.IsOut) {
								if (a < 10) {
									if (!wsc.isConnected()) {
										wsc.reconnect();
									}
								} else {
									wsc.disconnect();
									GetRandomIP();
								}
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
					}
				}

				@Override
				public void onOpen() {
					handler.sendEmptyMessage(30);
				}

				@Override
				public void onRawTextMessage(byte[] payload) {
				}

				@Override
				public void onTextMessage(String payload) {
					IsOk = true;
					try {
						JSONObject jsonObject = new JSONObject(payload);
						intent.setAction("newflashData");
						if (payload.contains("cmd")
								&& jsonObject.getString("cmd").equals("del")) {
							intent.putExtra("del", jsonObject.getString("id"));
							intent.putExtra("type", "del");
						} else {
							if (null != jsonObject.get("type")
									&& jsonObject.get("type").equals("1")) {
								ZxInfo zxInfo = gson.fromJson(
										jsonObject.toString(), ZxInfo.class);
								intent.putExtra("zxinfo", zxInfo);
								intent.putExtra("type", "zx");
							} else if (null != jsonObject.get("type")
									&& jsonObject.get("type").equals("2")) {
								Flash_CjInfo flash_CjInfo = gson.fromJson(
										jsonObject.toString(),
										Flash_CjInfo.class);
								intent.putExtra("flash_CjInfo", flash_CjInfo);
								intent.putExtra("type", "cj");
							} else if (null != jsonObject.get("type")
									&& jsonObject.get("type").equals("1001")) {
								Flash_ZxImgInfo flash_ZxImgInfo = gson
										.fromJson(jsonObject.toString(),
												Flash_ZxImgInfo.class);
								intent.putExtra("flash_ZxImgInfo",
										flash_ZxImgInfo);
								intent.putExtra("type", "img");
							}
						}
						sendBroadcast(intent);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			IsOk = false;
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		while (running == 1) {
			if (data.size() > 0) {
				if (!IsOk) {
					GetRandomIP();
				}
			}
			if (data.size() < 1
					&& NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
				GetIPUrl(Flash_URL);
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 要做的事情
			if (null != wsc && wsc.isConnected()
					&& NetworkCenter.checkNetwork_JYH(getApplicationContext())) {
				wsc.sendTextMessage("");
			}
			handler.postDelayed(this, 60000);
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (null != wsc && wsc.isConnected()) {
			wsc.disconnect();
		}
		IsOk = false;
		handler.removeCallbacks(runnable);
		super.onDestroy();
		running = 0;
	}
}
