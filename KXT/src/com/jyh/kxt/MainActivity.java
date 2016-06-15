package com.jyh.kxt;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jyh.fragment.Fragment_hq_zx;
import com.jyh.fragment.HqItem_fragment.HqInterface;
import com.jyh.fragment.fragment_data;
import com.jyh.fragment.fragment_flash_main;
import com.jyh.fragment.fragment_kxthq;
import com.jyh.fragment.fragment_self.OnFragmentListener;
import com.jyh.fragment.fragment_yw;
import com.jyh.fragment.fragment_self;
import com.jyh.kxt.customtool.CHScrollView;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.KXTSocketService;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.kxt.socket.RAIntentService;
import com.jyh.kxt.socket.ServiceUtil;
import com.jyh.kxt.socket.TopRAIntentService;
import com.jyh.kxt.volley.RequestManager;
import com.jyh.kxt.R;
import com.umeng.analytics.MobclickAgent;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener,
		OnFragmentListener, HqInterface {
	private fragment_yw fragment_jw;
	// private fragment_hq fragment_hq;
	private fragment_kxthq fragment_kxthq;// 快讯通行情
	private fragment_data data;
	private fragment_self fragment_self;
	private ImageView imgdata, imgflash, imghq, imgself, imgjw;
	private fragment_flash_main fragment_flash_main;
	private KXTApplication application;
	private LinearLayout main_ll_flash, main_ll_yw, main_ll_hq, main_ll_rl,
			main_ll_self, main_zt_color;
	private long mExitTime;
	private SharedPreferences preferences;
	private boolean Isyj = false;
	private FragmentManager fragmentManager;
	public HorizontalScrollView mTouchView, mTouchView1;
	ImageView leftOk, leftNo, rightOk, rightNo;
	List<CHScrollView> mHScrollViews;
	private Timer timer;
	Handler handler;
	private FragmentTransaction transaction;
	private SharedPreferences preferences1;
	private Timer Checktimer;
	private boolean isCheck = true;
	public KXTSocketService mService;
	Handler mianHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 10:
				if (isCheck) {
					isCheck = false;
				}
				break;
			case 1:
				setTabSelection(1);
				break;
			case 2:
				setTabSelection(2);
				break;
			case 3:
				setTabSelection(3);
				break;
			case 4:
				setTabSelection(0);
				handler.sendEmptyMessage(2);
				break;
			default:
				break;
			}
		};
	};
	private Intent intent;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		Fresco.initialize(this);
		super.onCreate(arg0);
		RequestManager.init(this);
		application = (KXTApplication) getApplication();
		preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
		preferences1 = getSharedPreferences("isHQCenter", Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		setTheam();
		// 透明状态栏
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		InitFind();
		fragmentManager = getSupportFragmentManager();
		bindService();
		Intent intent = getIntent();
		if (intent.getData() == null) {
			String data = intent.getStringExtra("data");
			String enter = intent.getStringExtra("enter");
			String type = intent.getStringExtra("type");
			if (null != data && !data.equals("")) {
				Notifacation();
			} else if (null != enter && !enter.equals("")) {
				setTabSelection(4);
			} else if (null != type && type.contains("new")) {
				setTabSelection(1);
			} else if (null != type && type.contains("dian")) {
				setTabSelection(0);
				handler.sendEmptyMessage(1);
			} else if (null != type && type.contains("video")) {
				setTabSelection(0);
				handler.sendEmptyMessage(2);
			} else if (null != type && type.contains("hq")) {
				setTabSelection(2);
			} else {
				setTabSelection(0);
			}
		} else {
			if (intent.getData().getHost().equals("main")) {
				if (intent.getData().getPath().contains("rili")) {
					setTabSelection(3);
				} else if (intent.getData().getPath().contains("lquotes")) {
					application.setHq_item(intent.getData().getPath()
							.replace("/lquotes/", ""));
					setTabSelection(2);
				}
			}
		}
		Checktimer = new Timer();

		Checktimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (NetworkCenter.checkNetworkConnection(MainActivity.this)) {
					isCheck = true;
				} else {
					mianHandler.sendEmptyMessage(10);
				}
			}
		}, 0, 5 * 1000);
		application.setMainHander(mianHandler);
	}

	/**
	 * 根据提示设置主题样式
	 */
	private void setTheam() {
		// TODO Auto-generated method stub
		if (null != application.getIcon() && !"".equals(application.getIcon())) {
			if (application.getIcon().equals("xinnian")) {
				if (Isyj) {
					this.setTheme(R.style.BrowserThemeNewNight);
				} else {
					this.setTheme(R.style.BrowserThemeNew);
				}
			} else if (application.getIcon().equals("default")) {
				if (Isyj) {
					this.setTheme(R.style.BrowserThemeNight);
				} else {
					this.setTheme(R.style.BrowserThemeDefault);
				}

			} else if (application.getIcon().equals("yuanxiao")) {
				if (Isyj) {
					this.setTheme(R.style.BrowserThemeYuanXiaoNight);
				} else {
					this.setTheme(R.style.BrowserThemeYuanXiao);
				}
			}
		} else {
			if (Isyj) {
				this.setTheme(R.style.BrowserThemeNight);
			} else {
				this.setTheme(R.style.BrowserThemeDefault);
			}
		}
	}

	@SuppressLint("NewApi")
	private void setTabSelection(int index) {

		// 重置按钮
		resetBtn();
		// 开启一个Fragment事务
		transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 0:
			// 当点击了消息tab时，改变控件的图片和文字颜色
			imgflash.setSelected(true);
			main_ll_flash.setSelected(true);
			if (fragment_flash_main == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				fragment_flash_main = new fragment_flash_main();
				handler = fragment_flash_main.handler;
				transaction.add(R.id.frame_content, fragment_flash_main);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(fragment_flash_main);
			}
			break;
		case 1:
			// 当点击了消息tab时，改变控件的图片和文字颜色
			imgjw.setSelected(true);
			main_ll_yw.setSelected(true);
			if (fragment_jw == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				fragment_jw = new fragment_yw();
				transaction.add(R.id.frame_content, fragment_jw);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(fragment_jw);
			}
			break;
		case 2:
			imghq.setSelected(true);
			main_ll_hq.setSelected(true);
			if (fragment_kxthq == null) {
				fragment_kxthq = new fragment_kxthq();
				transaction.add(R.id.frame_content, fragment_kxthq);
			} else {
				transaction.show(fragment_kxthq);
			}
			//

			preferences1.edit().putBoolean("isChange", true).commit();
			preferences.edit().putBoolean("iszx", true).commit();
			break;
		case 3:
			// 当点击了设置tab时，改变控件的图片和文字颜色
			application.getMmp().put("4", "4");
			imgdata.setSelected(true);
			main_ll_rl.setSelected(true);
			if (data == null) {
				// 如果SettingFragment为空，则创建一个并添加到界面上
				data = new fragment_data();
				transaction.add(R.id.frame_content, data);
			} else {
				// 如果SettingFragment不为空，则直接将它显示出来
				transaction.show(data);
			}
			break;
		case 4:
			imgself.setSelected(true);
			main_ll_self.setSelected(true);
			if (fragment_self == null) {
				// 如果SettingFragment为空，则创建一个并添加到界面上
				fragment_self = new fragment_self();
				transaction.add(R.id.frame_content, fragment_self);
			} else {
				// 如果SettingFragment不为空，则直接将它显示出来
				transaction.show(fragment_self);
			}
			break;
		}
		transaction.commitAllowingStateLoss();
	}

	private void resetBtn() {
		imgdata.setSelected(false);
		imgflash.setSelected(false);
		imghq.setSelected(false);
		imgself.setSelected(false);
		imgjw.setSelected(false);

		main_ll_flash.setSelected(false);
		main_ll_yw.setSelected(false);
		main_ll_hq.setSelected(false);
		main_ll_rl.setSelected(false);
		main_ll_self.setSelected(false);
	}

	@SuppressLint("NewApi")
	private void hideFragments(FragmentTransaction transaction) {
		if (fragment_flash_main != null) {
			transaction.hide(fragment_flash_main);
		}
		// if (fragment_hq != null) {
		// transaction.hide(fragment_hq);
		// }
		if (fragment_kxthq != null) {
			transaction.hide(fragment_kxthq);
		}
		if (fragment_jw != null) {
			transaction.hide(fragment_jw);
		}
		if (fragment_self != null) {
			transaction.hide(fragment_self);
		}
		if (data != null) {
			transaction.hide(data);
		}
	}

	private void Notifacation() {
		// TODO Auto-generated method stub
		imgdata.setSelected(true);
		// flash = new fragment_flash();
		// fragment_flash_main = new fragment_flash_main();
		data = new fragment_data();
		FragmentTransaction fragmentTransaction = this
				.getSupportFragmentManager().beginTransaction();
		// 替换当前的页面
		fragmentTransaction.replace(R.id.frame_content, data);
		// 事务管理提交
		fragmentTransaction.commitAllowingStateLoss();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
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
	 * 用于获取历史数据
	 * 
	 * @author Administrator
	 *
	 */
	class InitHostroyData extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return 1;
		}

		/**
		 * 获取完成后开启推送服务
		 */
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		bindService();
		intent = getIntent();
	}

	/**
	 * 用于服务绑定 鉴于后台运行 遗弃
	 */
	private void bindService() {
		InitFlashServer();
		if (!(ServiceUtil.isServiceRunning(this,
				RAIntentService.class.getName()))) {
			Intent intent2 = new Intent(this, RAIntentService.class);
			startService(intent2);
		}
		if (!(ServiceUtil.isServiceRunning(this,
				TopRAIntentService.class.getName()))) {
			Intent intent3 = new Intent(this, TopRAIntentService.class);
			startService(intent3);
		}
	}

	private void InitFlashServer() {
		// TODO Auto-generated method stub
		if (!(ServiceUtil.isServiceRunning(this,
				KXTSocketService.class.getName()))) {
			intent = new Intent(MainActivity.this, KXTSocketService.class);
			startService(intent);
		}
	}

//	private ServiceConnection mConnection = new ServiceConnection() {
//
//		// Called when the connection with the service is established
//		public void onServiceConnected(ComponentName className, IBinder service) {
//			// Because we have bound to an explicit
//			// service that is running in our own process, we can
//			// cast its IBinder to a concrete class and directly access it.
//			FlashService flashService = (FlashService) service;
//			mService = flashService.Getservire();
//			mBound = true;
//		}
//
//		// Called when the connection with the service disconnects unexpectedly
//		public void onServiceDisconnected(ComponentName className) {
//			mBound = false;
//		}
//	};

	private void clickAtBtn() {
		main_ll_flash.setOnClickListener(this);
		main_ll_hq.setOnClickListener(this);
		main_ll_rl.setOnClickListener(this);
		main_ll_self.setOnClickListener(this);
		main_ll_yw.setOnClickListener(this);
	}

	private void InitFind() {
		main_zt_color = (LinearLayout) findViewById(R.id.main_zt_color);
		main_ll_flash = (LinearLayout) findViewById(R.id.mian_ll_flash);
		main_ll_hq = (LinearLayout) findViewById(R.id.mian_ll_hq);
		main_ll_rl = (LinearLayout) findViewById(R.id.mian_ll_rl);
		main_ll_self = (LinearLayout) findViewById(R.id.mian_ll_self);
		main_ll_yw = (LinearLayout) findViewById(R.id.mian_ll_yw);
		imgdata = (ImageView) findViewById(R.id.imgdata);
		imgflash = (ImageView) findViewById(R.id.imgflash);
		imghq = (ImageView) findViewById(R.id.imghq);
		imgself = (ImageView) findViewById(R.id.imgself);
		imgjw = (ImageView) findViewById(R.id.imgyw);

		if (Isyj) {
			main_zt_color.setBackgroundColor(Color.parseColor("#04274c"));
		} else {
			main_zt_color.setBackgroundColor(Color.parseColor("#1677e0"));
		}

		application.addAct(this);
		clickAtBtn();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mian_ll_flash:
			setTabSelection(0);
			break;
		case R.id.mian_ll_yw:
			setTabSelection(1);
			break;
		case R.id.mian_ll_hq:
			setTabSelection(2);
			break;
		case R.id.mian_ll_rl:
			setTabSelection(3);
			break;
		case R.id.mian_ll_self:
			setTabSelection(4);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				application.exitAppAll();
			}
			return true;
		}
		// 拦截MENU按钮点击事件，让他无任何操作
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFragmentAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getHorizontalScrollView(HorizontalScrollView mTouchView) {
		// TODO Auto-generated method stub
		this.mTouchView = mTouchView;
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		DisplayMetrics dm = new DisplayMetrics();
		try {
			this.getWindowManager().getDefaultDisplay().getMetrics(dm);
			if (l == 0) {
				leftOk.setVisibility(View.GONE);
				leftNo.setVisibility(View.VISIBLE);
			} else {
				leftOk.setVisibility(View.VISIBLE);
				leftNo.setVisibility(View.GONE);
			}
			if (l > 1200) {
				rightOk.setVisibility(View.GONE);
				rightNo.setVisibility(View.VISIBLE);
			} else {
				rightOk.setVisibility(View.VISIBLE);
				rightNo.setVisibility(View.GONE);
			}
			for (CHScrollView scrollView : mHScrollViews) {
				// 防止重复滑动
				if (mTouchView != scrollView) {
					scrollView.smoothScrollTo(l, t);
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onScrollChanged(ImageView leftOk, ImageView leftNo,
			ImageView rightOk, ImageView rightNo,
			List<CHScrollView> mHScrollViews) {
		MainActivity.this.leftOk = leftOk;
		MainActivity.this.leftNo = leftNo;
		MainActivity.this.rightOk = rightOk;
		MainActivity.this.rightNo = rightNo;
		MainActivity.this.mHScrollViews = mHScrollViews;

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (timer != null) {
			timer.purge();
			timer.cancel();
		}
//		if (mBound) {
//			unbindService(mConnection);
//		}
		application.ischange = true;
	}

	public interface FragmentActivityResult {
		public void OnActivityResult(int requestCode, int resultCode,
				Intent data);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == 102) {
			((FragmentActivityResult) fragment_flash_main).OnActivityResult(
					arg0, arg1, arg2);
		}
	}

	public Fragment_hq_zx getFragmentZx() {
		return (Fragment_hq_zx) application.getFragmentZx();
	}
}
