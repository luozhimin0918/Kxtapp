package com.jyh.kxt;

import com.jyh.fragment.Fragment_rl_data;
import com.jyh.gson.bean.CjrlDataInt;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.ArrayWheelAdapter;
import com.jyh.kxt.customtool.WheelView;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.tool.OnWheelScrollListener;
import com.jyh.tool.PopupWindowUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class CjrlSelectTimeActivity extends Activity {

	private Button botton;
	private TextView textview;
	private boolean Isyj = false;
	private SharedPreferences preferences;
	private SharedPreferences preferences1;
	private Editor editor;
	private WheelView yearView;
	private CjrlDataInt data;
	private String[] strs = { "提前5分钟", "提前10分钟", "提前15分钟", "提前30分钟" };
	private int curIndex = 0;
	private KXTApplication app;
	private LinearLayout sc_img_frag;
	private PopupWindowUtils popManager;
	private View cjrlView;
	private Button btnOk;
	private Fragment_rl_data fragment_data;
	private View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		data = (CjrlDataInt) intent.getSerializableExtra("data");

		app = (KXTApplication) getApplication();
		fragment_data = (Fragment_rl_data) app.getFragment();
		popManager = new PopupWindowUtils();
		preferences = this.getSharedPreferences("setup", Context.MODE_PRIVATE);
		preferences1 = this.getSharedPreferences("clickIds",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			this.setTheme(R.style.BrowserThemeNight);
		} else {
			this.setTheme(R.style.BrowserThemeDefault);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		view = View.inflate(this, R.layout.pop_cjrl_select_time, null);
		setContentView(view);

		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		yearView = (WheelView) findViewById(R.id.year);
		botton = (Button) findViewById(R.id.btn_s);
		textview = (TextView) findViewById(R.id.text_s_data);
		textview.setText(data.getDate() + " " + data.getPredictTime());
		sc_img_frag = (LinearLayout) findViewById(R.id.sc_img_frag);
		cjrlView = View.inflate(this, R.layout.pop_cjrl_timer, null);
		btnOk = (Button) cjrlView.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popManager.dismiss();
				finish();
				// Fragment_rl_data.getInstance().getAdpater()
				// .notifyDataSetChanged();
				// Fragment_rl_data.getInstance().getAdpater()
				// .addSj(data, curIndex, v);

				fragment_data.getAdpater().notifyDataSetChanged();
				fragment_data.getAdpater().addSj(data, curIndex, v);
				editor = preferences1.edit();
				editor.putInt(data.getAutoID() + "", data.getAutoID()).commit();
			}
		});
		botton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				app.setCjrlMin(curIndex);
				popManager.dismiss();
				popManager.initPopupWindwo(v, cjrlView,

				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
						Color.TRANSPARENT,
						R.style.popwindow_register_animation, 0, 0);

			}
		});

		sc_img_frag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				// Fragment_rl_data.getInstance().getAdpater()
				// .notifyDataSetChanged();
				fragment_data.getAdpater().notifyDataSetChanged();

			}
		});

		initWheel();
	}

	private void initWheel() {
		// TODO Auto-generated method stub

		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				strs);
		yearView.setViewAdapter(adapter);
		yearView.setCyclic(false);
		yearView.addScrollingListener(scrollListener);
		yearView.setCurrentItem(0);
		yearView.setVisibleItems(3);// 设置显示行数
		if (Isyj) {
			yearView.setShadowsColors(new int[] { 0x00000000, 0x00000000,
					0x00000000 });
		} else {
			yearView.setShadowsColors(new int[] { 0x0fFFFFFF, 0x0eFFFFFF,
					0x3fE9E9E9 });
		}

	}

	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			curIndex = yearView.getCurrentItem();
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			finish();
			// Fragment_rl_data.getInstance().getAdpater().notifyDataSetChanged();
			fragment_data.getAdpater().notifyDataSetChanged();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 测试网络是否连接
	public boolean testNetWork() {

		if (!NetworkCenter.checkNetworkConnection(this)) {
			return false;
		} else {
			return true;
		}
	}

}
