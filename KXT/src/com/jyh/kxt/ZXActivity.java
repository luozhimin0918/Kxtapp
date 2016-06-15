package com.jyh.kxt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jyh.kxt.R;
import com.jyh.kxt.socket.KXTApplication;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.umeng.soexample.commons.Constants;

public class ZXActivity extends Activity implements OnClickListener {

	private WebView webview;
	private SharedPreferences preferences;
	private SharedPreferences preferences2;
	private boolean Isyj = false;
	private TextView title_tv;
	private LinearLayout zx_img_back, zx_ima_share, zx_add;
	private UMSocialService mController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);
	private String url;
	private String end = "&share=1";
	private String title = "";
	private String code;
	private String shareUrl = "";
	private String scdata;
	private ImageView hq_zx;
	private LinearLayout zx_zt_color;
	private Editor editor;
	private int yejian = 0;
	private String data = "";
	private UMSsoHandler ssoHandler;
	private Intent intent;
	private boolean Is_usp = false;
	@SuppressWarnings("unused")
	private KXTApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = this.getSharedPreferences("setup", Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			this.setTheme(R.style.BrowserThemeNight);
			yejian = 1;
		} else {
			yejian = 0;
			this.setTheme(R.style.BrowserThemeDefault);
		}

		// // 透明状态栏
		// getWindow()
		// .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// // 透明导航栏
		// getWindow().addFlags(
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.fragment_zx);
		application = (KXTApplication) getApplication();
		zx_zt_color = (LinearLayout) findViewById(R.id.zx_zt_color);
		if (Isyj) {
			zx_zt_color.setBackgroundColor(Color.parseColor("#04274c"));
		} else {
			zx_zt_color.setBackgroundColor(Color.parseColor("#116bcc"));
		}
		preferences = getSharedPreferences("apkinfo", Context.MODE_PRIVATE);
		preferences2 = getSharedPreferences("hqsetup", Context.MODE_PRIVATE);
		scdata = preferences2.getString("ischeck", "");
		editor = preferences2.edit();
		url = preferences.getString("hqSocket",
				"http://appapi.kxt.com/Quotes/newchart");

		title_tv = (TextView) findViewById(R.id.zx_title);
		zx_ima_share = (LinearLayout) findViewById(R.id.zx_share);
		zx_img_back = (LinearLayout) findViewById(R.id.zx_back);
		zx_add = (LinearLayout) findViewById(R.id.zx_add);
		hq_zx = (ImageView) findViewById(R.id.hq_zx);
		// ~~~ 获取参数
		intent = getIntent();
		if (intent.getData() == null) {
			title = intent.getStringExtra("title");
			code = intent.getStringExtra("code");
			Is_usp = false;
		} else {
			Is_usp = true;
			code = intent.getData().getPath().replace("/", "");
		}
		// ~~~ 绑定控件
		webview = (WebView) findViewById(R.id.zx_web);
		// ~~~ 设置数据
		// titleText.setText(name);
		webview.getSettings().setJavaScriptEnabled(true);
		intWeb();
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				if (!webview.getSettings().getLoadsImagesAutomatically()) {
					webview.getSettings().setLoadsImagesAutomatically(true);
				}
				webview.loadUrl("javascript:alert($('#share_title').text())");
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				webview.setVisibility(View.INVISIBLE);
				Toast.makeText(ZXActivity.this, "网络异常", Toast.LENGTH_SHORT)
						.show();
			}

		});
		webview.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				// TODO Auto-generated method stub
				if (message.length() < 10) {
					title = message;
					title_tv.setText(title);
				} else {
					data = message;
				}
				setShareContent();
				result.confirm();
				return true;
			}
		});
		zx_ima_share.setOnClickListener(this);
		zx_img_back.setOnClickListener(this);
		zx_add.setOnClickListener(this);
		if (scdata.contains(code)) {
			hq_zx.setBackgroundResource(R.drawable.hq_remove);
		} else {
			hq_zx.setBackgroundResource(R.drawable.hq_add);
		}
		shareUrl = url + "?code=" + code + end;
		webview.loadUrl(url + "?code=" + code + "&yejian=" + yejian);
		configPlatforms();
	}

	public void intWeb() {
		if (Build.VERSION.SDK_INT >= 19) {
			webview.getSettings().setLoadsImagesAutomatically(true);
		} else {
			webview.getSettings().setLoadsImagesAutomatically(false);
		}
	}

	private void configPlatforms() {
		addQQQZonePlatform();
		addWXPlatform();
	}

	private void addQQQZonePlatform() {
		String appId = "1101487761";
		String appKey = "YJCbjYB5ql2LNRyQ";
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appId, appKey);
		qqSsoHandler.setTargetUrl("http://m.kuaixun360.com/");
		qqSsoHandler.addToSocialSDK();
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(ZXActivity.this,
				appId, appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	private void addWXPlatform() {
		String appId = "wx93dccc483df30be4";
		String appSecret = "0828a1e0f1dd2791050113a9adb715a4";
		UMWXHandler wxHandler = new UMWXHandler(ZXActivity.this, appId,
				appSecret);
		wxHandler.addToSocialSDK();
		UMWXHandler wxCircleHandler = new UMWXHandler(ZXActivity.this, appId,
				appSecret);
		wxCircleHandler.showCompressToast(false);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	private void setShareContent() {
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(ZXActivity.this,
				"1101487761", "YJCbjYB5ql2LNRyQ");
		qZoneSsoHandler.addToSocialSDK();
		mController.setShareContent(title);

		UMImage urlImage = new UMImage(ZXActivity.this, R.drawable.share_img);
		// 微信
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent("【" + title + "】" + data);
		weixinContent.setTitle("【" + title + "】" + data);
		weixinContent.setTargetUrl(shareUrl);
		weixinContent.setShareMedia(urlImage);
		mController.setShareMedia(weixinContent);
		// 朋友圈
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent("【" + title + "】" + data);
		circleMedia.setTitle("【" + title + "】" + data);
		circleMedia.setShareImage(urlImage);
		circleMedia.setTargetUrl(shareUrl);
		mController.setShareMedia(circleMedia);

		// qzone
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent("【" + title + "】" + data);
		qzone.setTargetUrl(shareUrl);
		qzone.setTitle("【" + title + "】" + data);
		qzone.setShareImage(urlImage);
		mController.setShareMedia(qzone);
		// qq
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent("【" + title + "】" + data);
		qqShareContent.setTitle("【" + title + "】" + data);
		qqShareContent.setShareImage(urlImage);
		qqShareContent.setTargetUrl(shareUrl);
		mController.setShareMedia(qqShareContent);

		// 添加新浪sso授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.setShareContent("【" + title + "】" + data + shareUrl);
		mController.setShareImage(urlImage);
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
		mController.getConfig().closeToast();
	}

	private void addCustomPlatforms() {
		// 添加QQ平台
		addQQQZonePlatform();
		mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
				SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
				SHARE_MEDIA.SINA);
		mController.openShare(ZXActivity.this, false);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.zx_share:
			if (title.equals("")) {
				title = "快讯通财经";
			}
			webview.loadUrl("javascript:alert($('#share_content').text())");
			addCustomPlatforms();
			break;
		case R.id.zx_back:
			if (Is_usp) {
				intent = new Intent(ZXActivity.this, MainActivity.class);
				intent.putExtra("type", "hq");
				startActivity(intent);
				Is_usp = false;
			}
			finish();
			break;
		case R.id.zx_add:
			String rep = "|" + code;
			if (scdata != null) {
				if (scdata.contains(code)) {
					scdata = scdata.replace(rep, "");
					Toast.makeText(ZXActivity.this, "取消自选", Toast.LENGTH_LONG)
							.show();
					// if (Isyj) {
					hq_zx.setBackgroundResource(R.drawable.hq_add);
					// }else {
					// }
				} else {
					Toast.makeText(ZXActivity.this, "添加自选", Toast.LENGTH_LONG)
							.show();
					scdata = scdata + "|" + code;
					hq_zx.setBackgroundResource(R.drawable.hq_remove);
				}
			} else {
				scdata = scdata + "|" + code;

			}
			editor.putString("ischeck", scdata).commit();
			editor.putString("ischeck", scdata);

			break;
		default:
			break;
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
		if (webview != null) {
			webview.destroy();
		}
		super.onDestroy();
		editor.putString("ischeck", scdata).commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 根据requestCode获取对应的SsoHandler
		ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

	}

}