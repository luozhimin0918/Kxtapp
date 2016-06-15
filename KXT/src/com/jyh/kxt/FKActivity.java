package com.jyh.kxt;

import com.jyh.kxt.R;
import com.jyh.kxt.socket.KXTApplication;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FKActivity extends Activity {
	private ImageView self_fk_img;
	private WebView webView;
	private RelativeLayout fa_zt_color;
	private LinearLayout fk_linear;
	private SharedPreferences preferences;
	private boolean Isyj = false;
	// 手指上下滑动时的最小速度
	private static final int YSPEED_MIN = 1000;

	// 手指向右滑动时的最小距离
	private static final int XDISTANCE_MIN = 50;

	// 手指向上滑或下滑时的最小距离
	private static final int YDISTANCE_MIN = 100;

	// 记录手指按下时的横坐标。
	private float xDown;

	// 记录手指按下时的纵坐标。
	private float yDown;

	// 记录手指移动时的横坐标。
	private float xMove;

	// 记录手指移动时的纵坐标。
	private float yMove;

	// 用于计算手指滑动的速度。
	private VelocityTracker mVelocityTracker;
	KXTApplication application;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			this.setTheme(R.style.BrowserThemeNight);
		} else {
			this.setTheme(R.style.BrowserThemeDefault);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fk);
		application = (KXTApplication) getApplication();
		application.addAct(this);
		fa_zt_color = (RelativeLayout) findViewById(R.id.fk_zt_color);
		if (Isyj) {
			fa_zt_color.setBackgroundColor(Color.parseColor("#04274c"));
		} else {
			fa_zt_color.setBackgroundColor(Color.parseColor("#ffffff"));
		}
		webView = (WebView) findViewById(R.id.webView1);
		self_fk_img = (ImageView) findViewById(R.id.self_fk_img);
		fk_linear = (LinearLayout) findViewById(R.id.fk_linear);
		webView.setWebViewClient(new MyWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);// 是否支持JavaScript
		webView.getSettings().setBuiltInZoomControls(true);//
		webView.getSettings().setDisplayZoomControls(true);//
		webView.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
		webView.loadUrl("http://appapi.kxt.com/page/feedback");
		self_fk_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			fk_linear.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			fk_linear.setVisibility(View.VISIBLE);
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
		MobclickAgent.onPause(this);
	}

	/**
	 * 创建VelocityTracker对象，并将触摸界面的滑动事件加入到VelocityTracker当中。
	 *
	 * @param event
	 *
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	/**
	 * 回收VelocityTracker对象。
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}

	/**
	 *
	 * @return 滑动速度，以每秒钟移动了多少像素值为单位。
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getYVelocity();
		return Math.abs(velocity);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		createVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = event.getRawX();
			yDown = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			xMove = event.getRawX();
			yMove = event.getRawY();
			// 滑动的距离
			int distanceX = (int) (xMove - xDown);
			int distanceY = (int) (yMove - yDown);
			// 获取顺时速度
			int ySpeed = getScrollVelocity();
			// 关闭Activity需满足以下条件：
			// 1.x轴滑动的距离>XDISTANCE_MIN
			// 2.y轴滑动的距离在YDISTANCE_MIN范围内
			// 3.y轴上（即上下滑动的速度）<XSPEED_MIN，如果大于，则认为用户意图是在上下滑动而非左滑结束Activity
			if (distanceX > XDISTANCE_MIN
					&& (distanceY < YDISTANCE_MIN && distanceY > -YDISTANCE_MIN)
					&& ySpeed < YSPEED_MIN) {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
			break;
		case MotionEvent.ACTION_UP:
			recycleVelocityTracker();
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
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
