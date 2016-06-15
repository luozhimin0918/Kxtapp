package com.jyh.kxt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jyh.bean.HqBeanData;
import com.jyh.fragment.fragment_hq_hq;
import com.jyh.kxt.R;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.kxt.socket.RAIntentService;
import com.jyh.kxt.socket.ServiceUtil;
import com.umeng.analytics.MobclickAgent;

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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

public class HQCenterActivity extends Activity {
	private GridView gridView;
	private SimpleAdapter adapter;
	private List<HqBeanData> hqBeanDatas;
	private KXTApplication application;
	private List<Map<String, Object>> list;
	private LinearLayout hq_center_beak;
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

	private LinearLayout hqcenter_zt_color;
	private KXTApplication app;

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
		// 透明状态栏
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_hq_center);
		initView();
	}

	public void initView() {
		app = (KXTApplication) getApplication();
		hqcenter_zt_color = (LinearLayout) findViewById(R.id.hqcenter_zt_color);
		if (Isyj) {
			hqcenter_zt_color.setBackgroundColor(Color.parseColor("#04274c"));
		} else {
			hqcenter_zt_color.setBackgroundColor(Color.parseColor("#1177e0"));
		}
		gridView = (GridView) findViewById(R.id.gridView1);
		hq_center_beak = (LinearLayout) findViewById(R.id.hq_center_beak);
		list = new ArrayList<Map<String, Object>>();
		application = (KXTApplication) getApplication();
		hqBeanDatas = application.getHqBeanDatas();
		for (int i = 0; i < hqBeanDatas.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", hqBeanDatas.get(i).getName());
			list.add(map);
		}
		adapter = new SimpleAdapter(HQCenterActivity.this, list,
				R.layout.activity_hq_center_item, new String[] { "name" },
				new int[] { R.id.hq_activity_tv });
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (NetworkCenter.checkNetworkConnection(HQCenterActivity.this)) {

					if (ServiceUtil.isServiceRunning(HQCenterActivity.this,
							"com.jyh.kxt.socket.RAIntentService")) {
					} else {
						Intent intent2 = new Intent(HQCenterActivity.this,
								RAIntentService.class);
						intent2.putExtra("isSend", true);// 是否发送
						HQCenterActivity.this.startService(intent2);
					}
				}
				Intent intent = new Intent();
				intent.putExtra("index", "" + arg2);
				setResult(140, intent);
				finish();
				fragment_hq_hq fhq = (fragment_hq_hq) app.getFragmentHq();
				fhq.getIndex(arg2);

			}
		});
		hq_center_beak.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	// TODO CHANGE
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		// super.onSaveInstanceState(outState);
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
