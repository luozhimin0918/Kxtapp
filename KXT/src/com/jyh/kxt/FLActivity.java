package com.jyh.kxt;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jyh.bean.ChannelItem;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.GridViewAdapter;
import com.jyh.kxt.socket.KXTApplication;
import com.umeng.analytics.MobclickAgent;

public class FLActivity extends Activity {
	private ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();
	private ImageView fl_yw_activity;
	private KXTApplication application;
	private GridView fl_gr_ls;
	private GridViewAdapter adapter;
	private Intent intent;
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

	private LinearLayout fl_zt_color;

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

//		// 透明状态栏
//		getWindow()
//				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//		// 透明导航栏
//		getWindow().addFlags(
//				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		intent = new Intent();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fl);
		application = (KXTApplication) getApplication();
		InitView();
	}

	private void initColumnData() {
		if (application.getChannelItems().isEmpty()) {
			Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
		} else {
			userChannelList = (ArrayList<ChannelItem>) application
					.getChannelItems();
		}
		adapter = new GridViewAdapter(this, userChannelList);
		fl_gr_ls.setAdapter(adapter);
	}

	private void InitView() {
		// TODO Auto-generated method stub

		fl_zt_color = (LinearLayout) findViewById(R.id.fl_zt_color);
		if (Isyj) {
			fl_zt_color.setBackgroundColor(Color.parseColor("#04274c"));
		} else {
			fl_zt_color.setBackgroundColor(Color.parseColor("#1177e0"));
		}
		fl_gr_ls = (GridView) findViewById(R.id.fl_gr_ls);
		fl_yw_activity = (ImageView) findViewById(R.id.fl_yw_activity);
		initColumnData();
		InitLister();
	}

	private void InitLister() {
		fl_yw_activity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		fl_gr_ls.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				intent.putExtra("select", "" + arg2);
				setResult(111, intent);
				finish();
			}
		});
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
}
