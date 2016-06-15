package com.jyh.kxt;

import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.kxt.socket.VersionManager;
import com.jyh.kxt.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * 关于界面
 * @author PC
 *
 */
public class AboutActivity extends Activity implements OnClickListener {
	private LinearLayout ll_about_bb, ll_about_gw, ll_about_us, ll_about_tk;
	private LinearLayout about_zt_color;
	private KXTApplication application;
	private LinearLayout about_beak;
	private SharedPreferences preferences;
	public VersionManager manager;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		preferences = this.getSharedPreferences("setup", Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			this.setTheme(R.style.BrowserThemeNight);
		} else {
			this.setTheme(R.style.BrowserThemeDefault);
		}
//		// 透明状态栏
//		getWindow()
//				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//		// 透明导航栏
//		getWindow().addFlags(
//				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about_me);
		InitFind();
	}

	private void InitFind() {
		about_beak = (LinearLayout) findViewById(R.id.about_beak);
		ll_about_bb = (LinearLayout) findViewById(R.id.ll_about_bb);
		ll_about_gw = (LinearLayout) findViewById(R.id.ll_about_gw);
		ll_about_us = (LinearLayout) findViewById(R.id.ll_about_us);
		ll_about_tk = (LinearLayout) findViewById(R.id.ll_about_tk);
		about_zt_color = (LinearLayout) findViewById(R.id.about_zt_color);
		if (Isyj) {
			about_zt_color.setBackgroundColor(Color.parseColor("#04274c"));
		} else {
			about_zt_color.setBackgroundColor(Color.parseColor("#1677e0"));
		}

		application = (KXTApplication) getApplication();
		application.addAct(this);
		preferences = getSharedPreferences("kxt_version", Context.MODE_PRIVATE);
		preferences.getString("newversion", "");
		InitListen();
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

	private void InitListen() {
		// TODO Auto-generated method stub
		ll_about_bb.setOnClickListener(this);
		ll_about_gw.setOnClickListener(this);
		ll_about_us.setOnClickListener(this);
		ll_about_tk.setOnClickListener(this);
		about_beak.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.ll_about_bb:// 版本
			if (NetworkCenter.checkNetworkConnection(this)) {
				Toast.makeText(AboutActivity.this, "版本检测中。。。",
						Toast.LENGTH_SHORT).show();
				handler.sendEmptyMessageDelayed(100, 2 * 1000);
			} else {
				Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.ll_about_gw:// 官网
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse("http://www.kuaixun360.com/");
			intent.setData(content_url);
			startActivity(intent);

			// intent = new Intent(AboutActivity.this, GWActivity.class);
			// startActivity(intent);
			break;
		case R.id.ll_about_us:// 关于我们
			intent = new Intent(this, LinkuserActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_about_tk:// 声明条款
			intent = new Intent(this, ClauseActivity.class);
			startActivity(intent);
			break;
		case R.id.about_beak:
			finish();

			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 60:
				preferences = getSharedPreferences("versions",
						Context.MODE_PRIVATE);
				String description = preferences.getString("description",
						"快讯通有新版啦");
				final String versionurl = preferences.getString("versionurl",
						"http://kxt.com/down.html");
				new AlertDialog.Builder(AboutActivity.this)
						.setTitle("更新提示")
						.setMessage(description)
						.setPositiveButton(
								"是",
								new android.content.DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										Intent intent = new Intent();
										intent.setAction(Intent.ACTION_VIEW);
										intent.setData(Uri.parse(versionurl));
										startActivity(intent);
										finish();
									}
								})
						.setNegativeButton(
								"否",
								new android.content.DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {

									}
								}).show();
				break;
			case 70:
				Toast.makeText(AboutActivity.this, "已经是最高版本",
						Toast.LENGTH_SHORT).show();
				break;
			case 100:
				manager = VersionManager.getInstance();
				manager.checkVersion(AboutActivity.this, handler);
				break;
			default:
				break;
			}
		};
	};

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
}
