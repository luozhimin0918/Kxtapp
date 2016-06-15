package com.jyh.kxt;

import com.jyh.kxt.R;
import com.jyh.kxt.socket.CjInfo;
import com.jyh.kxt.socket.ConstantValue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 
 * @author PC
 *
 */
public class CjInfoActicity extends Activity {
	private CjInfo cInfo;
	public static final String EFFECT_DOLLOR = "美元";
	public static final String EFFECT_GOLD_SILVER = "金银";
	public static final String EFFECT_OIL = "石油";
	private TextView calendar_item_listview_version2_nfi;// 标题
	private ImageView calendar_item_nature;// 重要性
	private ImageView calendar_item_img_state;// 国家
	private TextView calendar_item_listview_version2_before;// 前值
	private TextView calendar_item_listview_version2_gongbu;// 公布值
	private TextView calendar_item_listview_version2_forecast;// 后值
	private TextView calendar_listview_item_tv_title_time;// 时间
	private TextView main_cjrl_item_listview_v2_effectbad;// 利空
	private TextView main_cjrl_item_listview_v2_effectgood;// 影响
	private LinearLayout main_cjrl_item_ll_effectgood;
	private TextView calendar_item_listview_version2_4main_effect;// 利多
	private LinearLayout main_cjrl_item_ll_effectbad;

	private boolean Isyj = false;
	private SharedPreferences preferences;
	private LinearLayout linear_cj_info;
	private LinearLayout iamge_cj_info;
	private LinearLayout main_cjrl_item_ll_changegood;
	private TextView main_cjrl_item_listview_v2_change_good;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cInfo = (CjInfo) getIntent().getSerializableExtra("cInfo");
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
		setContentView(R.layout.activity_cj_show);

		linear_cj_info = (LinearLayout) findViewById(R.id.linear_cj_info);
		if (Isyj) {
			linear_cj_info.setBackgroundColor(Color.parseColor("#04274c"));
		} else {
			linear_cj_info.setBackgroundColor(Color.parseColor("#116bcc"));
		}

		iamge_cj_info = (LinearLayout) findViewById(R.id.image_cj_info);
		iamge_cj_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CjInfoActicity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
		});
		init();
	}

	public void init() {
		main_cjrl_item_listview_v2_change_good = (TextView) findViewById(R.id.main_cjrl_item_listview_v2_change_good);
		main_cjrl_item_ll_changegood = (LinearLayout) findViewById(R.id.main_cjrl_item_ll_changegood);
		calendar_item_listview_version2_nfi = (TextView) findViewById(R.id.calendar_item_listview_version2_nfi);
		calendar_item_listview_version2_before = (TextView) findViewById(R.id.calendar_item_listview_version2_before);
		calendar_item_listview_version2_gongbu = (TextView) findViewById(R.id.calendar_item_listview_version2_gongbu);
		calendar_item_listview_version2_forecast = (TextView) findViewById(R.id.calendar_item_listview_version2_forecast);
		main_cjrl_item_listview_v2_effectgood = (TextView) findViewById(R.id.main_cjrl_item_listview_v2_effectgood);
		calendar_listview_item_tv_title_time = (TextView) findViewById(R.id.calendar_listview_item_tv_title_time);
		calendar_item_listview_version2_4main_effect = (TextView) findViewById(R.id.main_cjrl_item_listview_v2_effectgood_good);
		calendar_item_nature = (ImageView) findViewById(R.id.calendar_item_nature);
		calendar_item_img_state = (ImageView) findViewById(R.id.calendar_item_img_state);
		main_cjrl_item_ll_effectgood = (LinearLayout) findViewById(R.id.main_cjrl_item_ll_effectgood);
		main_cjrl_item_ll_effectbad = (LinearLayout) findViewById(R.id.main_cjrl_item_ll_effectbad);
		main_cjrl_item_listview_v2_effectbad = (TextView) findViewById(R.id.main_cjrl_item_listview_v2_effectbad);
		calendar_item_listview_version2_nfi.setText(cInfo.getState()
				+ cInfo.getNfi());
		calendar_listview_item_tv_title_time.setText(cInfo.getPredictTime());
		calendar_item_listview_version2_before.setText("前值:"
				+ cInfo.getBefore());
		calendar_item_listview_version2_forecast.setText("预测:"
				+ cInfo.getForecast());
		calendar_item_listview_version2_gongbu.setText("" + cInfo.getReality());

		String state = cInfo.getState(); // 国家
		if (state.equals(ConstantValue.STATE_CHINA)) {
			calendar_item_img_state.setImageResource(R.drawable.state_china);
		} else if (state.equals(ConstantValue.STATE_AMERICAN)) {
			calendar_item_img_state.setImageResource(R.drawable.state_american);
		} else if (state.equals(ConstantValue.STATE_GERMAN)) {
			calendar_item_img_state.setImageResource(R.drawable.state_german);
		} else if (state.equals(ConstantValue.STATE_ENGLAND)) {
			calendar_item_img_state.setImageResource(R.drawable.state_england);
		} else if (state.equals(ConstantValue.STATE_FRANCE)) {
			calendar_item_img_state.setImageResource(R.drawable.state_france);
		} else if (state.equals(ConstantValue.STATE_AUSTRALIA)) {
			calendar_item_img_state
					.setImageResource(R.drawable.state_australia);
		} else if (state.equals(ConstantValue.STATE_JAPAN)) {
			calendar_item_img_state.setImageResource(R.drawable.state_japan);
		} else if (state.equals(ConstantValue.STATE_KOREA)) {
			calendar_item_img_state.setImageResource(R.drawable.state_korea);
		} else if (state.equals(ConstantValue.STATE_CANADA)) {
			calendar_item_img_state.setImageResource(R.drawable.state_canada);
		} else if (state.equals(ConstantValue.STATE_HONGKONG)) {
			calendar_item_img_state.setImageResource(R.drawable.state_hongkong);
		} else if (state.equals(ConstantValue.STATE_SWITZERLAND)) {
			calendar_item_img_state
					.setImageResource(R.drawable.state_swizerland);
		} else if (state.equals(ConstantValue.STATE_ITALY)) {
			calendar_item_img_state.setImageResource(R.drawable.state_italy);
		} else if (state.equals(ConstantValue.STATE_EURO_AREA)) {
			calendar_item_img_state
					.setImageResource(R.drawable.state_europe_area);
		} else if (state.equals(ConstantValue.STATE_NEW_ZEALAND)) {
			calendar_item_img_state
					.setImageResource(R.drawable.state_newzealand);
		} else if (state.equals(ConstantValue.STATE_TAIWAN)) {
			calendar_item_img_state.setImageResource(R.drawable.state_taiwan);
		} else if (state.equals(ConstantValue.STATE_SPANISH)) {
			calendar_item_img_state.setImageResource(R.drawable.state_spanish);
		} else if (state.equals(ConstantValue.STATE_SINGAPORE)) {
			calendar_item_img_state
					.setImageResource(R.drawable.state_singapore);
		} else if (state.equals(ConstantValue.STATE_BRAZIL)) {
			calendar_item_img_state.setImageResource(R.drawable.state_brazil);
		} else if (state.equals(ConstantValue.STATE_SOUTH_AFRICA)) {
			calendar_item_img_state
					.setImageResource(R.drawable.state_south_africa);
		} else if (state.equals(ConstantValue.STATE_INDIA)) {
			calendar_item_img_state.setImageResource(R.drawable.state_india);
		} else if (state.equals(ConstantValue.STATE_INDONESIA)) {
			calendar_item_img_state
					.setImageResource(R.drawable.state_indonesia);
		} else if (state.equals(ConstantValue.STATE_RUSSIA)) {
			calendar_item_img_state.setImageResource(R.drawable.state_russia);
		} else if (state.equals(ConstantValue.STATE_GREECE)) {
			calendar_item_img_state.setImageResource(R.drawable.state_greece);
		} else if (state.equals(ConstantValue.STATE_ISRAEL)) {
			calendar_item_img_state.setImageResource(R.drawable.state_israel);
		} else {
			calendar_item_img_state.setImageResource(R.drawable.state_default);
			// 没有就设一个默认的
		}

		String nature = cInfo.getNature(); // 重要性
		if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_HIGH)) {
			calendar_item_nature.setImageResource(R.drawable.nature_high);
		} else if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_MID)) {
			calendar_item_nature.setImageResource(R.drawable.nature_mid);
		} else if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_LOW)) {
			calendar_item_nature.setImageResource(R.drawable.nature_low);
		}

		// 值没错，写上去的时候出错了
		String effectMid = cInfo.getEffectMid();
		String effectGood = cInfo.getEffectGood();
		String effectBad = cInfo.getEffectBad();

		main_cjrl_item_ll_effectbad.setVisibility(View.GONE);
		main_cjrl_item_listview_v2_effectbad.setVisibility(View.GONE);
		main_cjrl_item_ll_effectgood.setVisibility(View.GONE);
		calendar_item_listview_version2_4main_effect.setVisibility(View.GONE);
		main_cjrl_item_listview_v2_effectgood.setVisibility(View.GONE);
		main_cjrl_item_ll_changegood.setVisibility(View.GONE);
		// 利空显示
		if (null != effectBad && !"".equals(effectBad)) {
			main_cjrl_item_ll_effectbad.setVisibility(View.VISIBLE);
			main_cjrl_item_listview_v2_effectbad.setVisibility(View.VISIBLE);
			main_cjrl_item_listview_v2_effectbad.setText("" + effectBad);
			main_cjrl_item_listview_v2_effectbad.setTextColor(Color.rgb(138,
					194, 119));
			main_cjrl_item_listview_v2_effectbad
					.setBackgroundResource(R.drawable.cjrl_effect_bg_green);
			if (null != effectGood && !effectGood.equals("")) {
				main_cjrl_item_ll_effectgood.setVisibility(View.INVISIBLE);
			}
		}
		// 利多显示
		if (null != effectBad && !"".equals(effectBad)) {
			if (null != effectGood && !effectGood.equals("")) {
				// main_cjrl_item_ll_effectgood.setVisibility(View.VISIBLE);
				main_cjrl_item_ll_changegood.setVisibility(View.VISIBLE);
				main_cjrl_item_listview_v2_change_good.setText("" + effectGood);
				main_cjrl_item_listview_v2_change_good.setTextColor(Color.rgb(
						202, 70, 57));
				main_cjrl_item_listview_v2_change_good
						.setBackgroundResource(R.drawable.cjrl_effect_bg_red);
			}
		} else {
			if (null != effectGood && !effectGood.equals("")) {
				main_cjrl_item_ll_effectgood.setVisibility(View.VISIBLE);
				calendar_item_listview_version2_4main_effect
						.setVisibility(View.VISIBLE);
				calendar_item_listview_version2_4main_effect.setText(""
						+ effectGood);
				calendar_item_listview_version2_4main_effect.setTextColor(Color
						.rgb(202, 70, 57));
				calendar_item_listview_version2_4main_effect
						.setBackgroundResource(R.drawable.cjrl_effect_bg_red);
			}
		}
		// 影响较小显示
		if (null != effectMid && !effectMid.equals("")) {
			main_cjrl_item_listview_v2_effectgood.setVisibility(View.VISIBLE);
			main_cjrl_item_listview_v2_effectgood.setText("" + effectMid);
			main_cjrl_item_listview_v2_effectgood.setTextColor(Color.rgb(243,
					176, 86));
			main_cjrl_item_listview_v2_effectgood
					.setBackgroundResource(R.drawable.cjrl_effect_bg_yellow);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(CjInfoActicity.this,
					MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
			return true;
		}
		// 拦截MENU按钮点击事件，让他无任何操作
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
