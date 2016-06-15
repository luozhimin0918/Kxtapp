package com.jyh.kxt;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jyh.kxt.R;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.sqlte.SCDataSqlte;
import com.jyh.player.JCBuriedPoint;
import com.jyh.player.JCVideoPlayer;
import com.jyh.tool.UmengTool;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.soexample.commons.Constants;

public class PlayerActivity extends Activity {
	private WebView webView;
	private TextView erroeTv;
	private KXTApplication application;
	private LinearLayout ll_dp_back, ll_dp_sc, ll_dp_share;
	private SCDataSqlte dataSqlte;
	private SQLiteDatabase db;
	private Cursor cursor;
	private String title;
	private String discription;
	private String share_url;
	private String url;
	private String id;
	private String image_url;
	private String play_count;
	private UMSocialService mController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);
	private SnsPostListener mSnsPostListener;
	private LinearLayout dp_web_linear, lin_btm;
	private SharedPreferences preferences;
	private boolean Isyj = false;
	private static boolean isFrist = true;
	private String intString;
	private String typr;
	private String isPlayer = "";
	public static String VideoUrl = "http://appapi.kxt.com/Video/newview?id=";
	public String Letv = android.os.Build.BRAND;
	private JCVideoPlayer videoController;

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			this.setTheme(R.style.BrowserThemeNight);
		} else {
			this.setTheme(R.style.BrowserThemeDefault);
		}
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_spweb);
		InitFind();
		registerForContextMenu(webView);
		UmengTool.configPlatforms(this);
		mSnsPostListener = new SnsPostListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int stCode,
					SocializeEntity entity) {
				if (stCode == 200) {
					Toast.makeText(PlayerActivity.this, "分享成功",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(PlayerActivity.this, "分享失败",
							Toast.LENGTH_LONG).show();
				}
			}
		};
		if (isFrist) {
			mController.registerListener(mSnsPostListener);
		}
	}

	private void InitFind() {
		videoController = (JCVideoPlayer) findViewById(R.id.videocontroller1);
		erroeTv = (TextView) findViewById(R.id.error_tv);
		ll_dp_back = (LinearLayout) findViewById(R.id.ll_sp_back);
		lin_btm = (LinearLayout) findViewById(R.id.lin_btm);
		ll_dp_sc = (LinearLayout) findViewById(R.id.ll_sp_sc);
		ll_dp_share = (LinearLayout) findViewById(R.id.ll_sp_share);
		setOnClick();
		webView = (WebView) findViewById(R.id.sp_webView1);
		dp_web_linear = (LinearLayout) findViewById(R.id.sp_web_linear);
		if (Isyj) {
			dp_web_linear.setBackgroundColor(Color.parseColor("#04274c"));
		} else {
			dp_web_linear.setBackgroundColor(Color.parseColor("#116bcc"));
		}
		application = (KXTApplication) getApplication();
		application.addAct(this);
		dataSqlte = new SCDataSqlte(this);

		// 获取意图中的数据
		Intent intent = getIntent();
		if (intent.getData() == null) {
			String SPURL = intent.getStringExtra("url");
			intString = VideoUrl
					+ SPURL.substring(SPURL.lastIndexOf("/") + 1,
							SPURL.length());

			Log.i("zml", "SPURL=" + intent.getStringExtra("url"));
			Log.i("zml",
					"intString="
							+ SPURL.substring(SPURL.lastIndexOf("/") + 1,
									SPURL.length()));
			typr = intent.getStringExtra("type");
			isPlayer = intent.getStringExtra("sc_play");
		} else {
			intString = VideoUrl + intent.getData().getPath().replace("/", "");
		}

		InitData();
	}

	private void setOnClick() {
		ll_dp_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != typr && !typr.equals("")) {
					Intent intent = new Intent(PlayerActivity.this,
							MainActivity.class);
					intent.putExtra("type", typr);
					startActivity(intent);
					typr = "";
				} else if (null != isPlayer && !isPlayer.equals("")) {
					setResult(191);
					isPlayer = "";
				}
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});
		ll_dp_sc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				db = dataSqlte.getWritableDatabase();
				if (ll_dp_sc.isSelected()) {
					if (db != null && id != null) {
						db.delete("vedio", "id=?", new String[] { id });
						ll_dp_sc.setSelected(false);
						Toast.makeText(PlayerActivity.this, "取消收藏",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(PlayerActivity.this, "操作失败",
								Toast.LENGTH_LONG).show();
					}
				} else {
					if (db == null) {
						return;
					}

					if (title == null || discription == null
							|| share_url == null) {
						Toast.makeText(getApplication(), "数据加载中，请稍候再试",
								Toast.LENGTH_SHORT).show();
						return;
					}

					ContentValues values = new ContentValues();
					values.put("image_url", image_url);
					values.put("title", title);
					values.put("share_url", share_url);
					values.put("discription", discription);
					values.put("id", id);
					values.put("play_count", play_count);
					values.put("url", url);

					db.insert("vedio", null, values);
					Toast.makeText(PlayerActivity.this, "收藏成功",
							Toast.LENGTH_LONG).show();
					ll_dp_sc.setSelected(true);
				}
				db.close();
			}
		});
		ll_dp_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (title == null || discription == null || share_url == null) {
					Toast.makeText(getApplication(), "数据加载中，请稍候再试",
							Toast.LENGTH_SHORT).show();
					return;
				}
				UmengTool.addCustomPlatforms(PlayerActivity.this, mController);
			}
		});
	}

	private void InitData() {
		WebSettings s = webView.getSettings();
		s.setJavaScriptEnabled(true);// 是否支持JavaScript
		s.setBuiltInZoomControls(true);//
		s.setLoadsImagesAutomatically(true);// 是否加载图片
		s.setJavaScriptEnabled(true);
		s.setBlockNetworkImage(true);
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//
		// 排版适应屏幕
		s.setUseWideViewPort(true);// 可任意比例缩放
		s.setLoadWithOverviewMode(true);//
		// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
		s.setDomStorageEnabled(true);
		s.setMediaPlaybackRequiresUserGesture(false);
		if (Isyj) {
			webView.loadUrl(intString + "&yejian=1");
		} else {
			webView.loadUrl(intString);
		}
		webView.setWebChromeClient(new xWebChromeClient());
		webView.setWebViewClient(new xWebViewClientent());
		JCVideoPlayer.setJcBuriedPoint(jcBuriedPoint);
	}

	/**
	 * 处理Javascript的对话框、网站图标、网站标题以及网页加载进度等
	 * 
	 * @author
	 */
	public class xWebChromeClient extends WebChromeClient {

		@Override
		public boolean onJsAlert(WebView view, String u, String message,
				JsResult result) {
			try {
				JSONObject object = new JSONObject(message);
				title = object.getString("title");
				discription = object.getString("description");
				url = object.getString("url");
				share_url = object.getString("share_url");
				id = object.getString("id");
				image_url = object.getString("picture");
				play_count = object.getString("play_count");
				db = dataSqlte.getReadableDatabase();
				boolean b = false;
				cursor = db.query("vedio", null, "id=?", new String[] { id },
						null, null, null);
				b = cursor.moveToFirst();
				if (b == true) {
					ll_dp_sc.setSelected(true);
				} else {
					ll_dp_sc.setSelected(false);
				}
				HashMap<String, String> header = new HashMap<String, String>();
				header.put("referer", "http://www.kxt.com");
				videoController.setUp(object.getString("video_url"), "快讯通财经",
						header, handler);
				ImageLoader.getInstance().displayImage(image_url,
						videoController.ivThumb);
				cursor.close();
				db.close();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				UmengTool.setShareContent(mController, PlayerActivity.this,
						title, share_url, discription, image_url);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				title = null;
				discription = null;
				url = null;
				share_url = null;
				id = null;
				image_url = null;
				play_count = null;
			}
			result.confirm();
			return true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (videoController.ifFullScreen == true) {
				videoController.ivFullScreen.performClick();
				videoController.ifFullScreen = false;
				return true;
			} else {
				if (null != typr && !typr.equals("")) {
					Intent intent = new Intent(PlayerActivity.this,
							MainActivity.class);
					intent.putExtra("type", typr);
					startActivity(intent);
					typr = "";
				} else if (null != isPlayer && !isPlayer.equals("")) {
					setResult(191);
					isPlayer = "";
				}
				PlayerActivity.this.finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		}
		return false;
	}

	/**
	 * 处理各种通知、请求等事件
	 * 
	 * @author
	 */
	public class xWebViewClientent extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			dp_web_linear.setVisibility(View.VISIBLE);
			webView.getSettings().setBlockNetworkImage(false);
			view.loadUrl("javascript:alert( $('#app_data').html() )");
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			view.setVisibility(View.GONE);
			erroeTv.setVisibility(View.VISIBLE);
			Toast.makeText(application, "数据加载失败", 0).show();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		isFrist = false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 根据requestCode获取对应的SsoHandler
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		JCVideoPlayer.releaseAllVideos();
		MobclickAgent.onPause(this);
	}

	/**
	 * 当横竖屏切换时会调用该方法
	 * 
	 * @author
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				lin_btm.setVisibility(View.GONE);
				webView.setVisibility(View.GONE);
				videoController.ifFullScreen = true;
			} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
				lin_btm.setVisibility(View.VISIBLE);
				webView.setVisibility(View.VISIBLE);
				videoController.ifFullScreen = false;
			}
		}
	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				break;
			case 2:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				handler.sendEmptyMessageDelayed(1, 2 * 1000);
				break;
			case 3:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				handler.sendEmptyMessageDelayed(1, 2 * 1000);
				break;
			default:
				break;
			}
		};
	};

	JCBuriedPoint jcBuriedPoint = new JCBuriedPoint() {
		@Override
		public void POINT_START_ICON(String title, String url) {
		}

		@Override
		public void POINT_START_THUMB(String title, String url) {
		}

		@Override
		public void POINT_STOP(String title, String url) {
		}

		@Override
		public void POINT_STOP_FULLSCREEN(String title, String url) {

		}

		@Override
		public void POINT_RESUME(String title, String url) {

		}

		@Override
		public void POINT_RESUME_FULLSCREEN(String title, String url) {
		}

		@Override
		public void POINT_CLICK_BLANK(String title, String url) {
		}

		@Override
		public void POINT_CLICK_BLANK_FULLSCREEN(String title, String url) {
			// 点击缩小按钮
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		@Override
		public void POINT_CLICK_SEEKBAR(String title, String url) {
		}

		@Override
		public void POINT_CLICK_SEEKBAR_FULLSCREEN(String title, String url) {
		}

		@Override
		public void POINT_AUTO_COMPLETE(String title, String url) {
		}

		@Override
		public void POINT_AUTO_COMPLETE_FULLSCREEN(String title, String url) {
		}

		@Override
		public void POINT_ENTER_FULLSCREEN(String title, String url) {
		}

		@Override
		public void POINT_QUIT_FULLSCREEN(String title, String url) {
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		videoController.MyDestroy();
	}

}
