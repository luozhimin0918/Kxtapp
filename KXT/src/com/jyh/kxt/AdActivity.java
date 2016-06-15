package com.jyh.kxt;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
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

/**
 * 广告界面
 * 
 * @author beginner
 * @date 创建时间：2015年7月21日 下午4:53:38
 * @version 1.0
 */
public class AdActivity extends FragmentActivity implements OnClickListener {

	private ImageView img, img2;
	// private SharedPreferences preference;
	private String url;
	public static Activity ad;
	private Intent intent;
	private Timer timer;
	private String imgpath;
	private boolean Isyj = false;
	private SharedPreferences preferences;
	private ViewGroup ad_zt_color;
	private WebView webView;

	private UMSocialService mController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);
	private String title;
	private String share;
	private UMImage urlImage;
	public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径
	// 异步加载图片
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferences = this.getSharedPreferences("setup", Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			this.setTheme(R.style.BrowserThemeNight);
		} else {
			this.setTheme(R.style.BrowserThemeDefault);
		}
		// 透明状态栏
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ad);
		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new WebViewClient() {

		});
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, String titl) {
				// TODO Auto-generated method stub
				super.onReceivedTitle(view, titl);
				((TextView) findViewById(R.id.ad_title_tv)).setText(titl);
				title = titl;
				configPlatforms();
				setShareContent();
			}

		});
		initImageLoader();
		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY).build();
		ad_zt_color = (ViewGroup) findViewById(R.id.ad_zt_color);
		if (Isyj) {
			ad_zt_color.setBackgroundColor(Color.parseColor("#04274c"));
		} else {
			ad_zt_color.setBackgroundColor(Color.parseColor("#116bcc"));
		}
		ad = this;
		imgpath = getIntent().getStringExtra("image");
		url = getIntent().getStringExtra("url");
		img = (ImageView) findViewById(R.id.img);
		img2 = (ImageView) findViewById(R.id.img2);
		img.setOnClickListener(this);
		img2.setOnClickListener(this);
		findViewById(R.id.ad_ima_share).setOnClickListener(this);
		findViewById(R.id.ad_img_back).setOnClickListener(this);
		intent = new Intent(AdActivity.this, MainActivity.class);
		img.setImageResource(R.drawable.welcome);
		mImageLoader.displayImage(imgpath, img, options);
		timer = new Timer();
		share = url;
		urlImage = new UMImage(getApplicationContext(), imgpath);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				startActivity(intent);
				finish();
			}
		}, 2 * 1000);
	}

	private void initImageLoader() {
		File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
				.getOwnCacheDirectory(getApplicationContext(), IMAGE_CACHE_PATH);

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
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(AdActivity.this,
				appId, appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	private void addWXPlatform() {
		String appId = "wx93dccc483df30be4";
		String appSecret = "0828a1e0f1dd2791050113a9adb715a4";
		UMWXHandler wxHandler = new UMWXHandler(AdActivity.this, appId,
				appSecret);
		wxHandler.addToSocialSDK();
		UMWXHandler wxCircleHandler = new UMWXHandler(AdActivity.this, appId,
				appSecret);
		wxCircleHandler.showCompressToast(false);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	private void setShareContent() {

		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(AdActivity.this,
				"1101487761", "YJCbjYB5ql2LNRyQ");
		qZoneSsoHandler.addToSocialSDK();
		mController.setShareContent(title);

		// 微信
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(title);
		weixinContent.setTitle(title);
		weixinContent.setTargetUrl(share);
		weixinContent.setShareMedia(urlImage);
		mController.setShareMedia(weixinContent);
		// 朋友圈
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(title);
		circleMedia.setTitle(title);
		circleMedia.setShareImage(urlImage);
		circleMedia.setTargetUrl(share);
		mController.setShareMedia(circleMedia);
		// qzone
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent(title);
		qzone.setTargetUrl(share);
		qzone.setTitle(title);
		qzone.setShareImage(urlImage);
		mController.setShareMedia(qzone);
		// qq
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(title);
		qqShareContent.setTitle(title);
		qqShareContent.setShareImage(urlImage);
		qqShareContent.setTargetUrl(share);
		mController.setShareMedia(qqShareContent);

		// 添加新浪sso授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.setShareContent("【" + title + "】" + share);
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
		mController.openShare(AdActivity.this, false);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.img:
			// 广告跳转
			if (url != null && !url.equals("")) {
				timer.cancel();
				timer.purge();
				webView.loadUrl(url);
				findViewById(R.id.imgId).setVisibility(View.GONE);
				findViewById(R.id.webviewId).setVisibility(View.VISIBLE);
			}
			finish();	
			break;
		case R.id.img2:
		case R.id.ad_img_back:
			timer.cancel();
			timer.purge();
			startActivity(intent);
			finish();
			break;
		case R.id.ad_ima_share:
			addCustomPlatforms();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		timer.cancel();
		timer.purge();
		startActivity(intent);
		finish();
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

}
