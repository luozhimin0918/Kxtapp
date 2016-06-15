package com.jyh.kxt.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.jyh.gson.bean.CjrlDataInt;
import com.jyh.kxt.CjrlSelectTimeActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.socket.ConstantValue;
import com.jyh.kxt.socket.KXTApplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CjrlDateAdpater extends BaseAdapter {

	private boolean bool_dollor; // 美元
	private boolean bool_gold_silver; // 金银
	private boolean bool_oil; // 石油
	public static final String EFFECT_DOLLOR = "美元";
	public static final String EFFECT_GOLD_SILVER = "金银";
	public static final String EFFECT_OIL = "石油";

	private List<CjrlDataInt> datas;
	private Context context;
	private boolean isYj;

	private static String calanderURL = "content://com.android.calendar/calendars";
	private static String calanderEventURL = "content://com.android.calendar/events";
	private static String calanderRemiderURL = "content://com.android.calendar/reminders";

	private SharedPreferences preferences;
	private Editor editor;
	@SuppressWarnings("unused")
	private KXTApplication app;
	private List<String> rlSjcodes;

	public CjrlDateAdpater(Context context, List<CjrlDataInt> datas, boolean isYj,
			KXTApplication app) {

		this.context = context;
		this.datas = datas;
		this.isYj = isYj;
		this.app = app;
		init();
	}

	private void init() {
		preferences = context.getSharedPreferences("clickIds",
				Context.MODE_PRIVATE);
		bool_dollor = true;
		bool_gold_silver = true;
		bool_oil = true;

	}

	public List<CjrlDataInt> getDatas() {
		return datas;
	}

	public void setDatas(List<CjrlDataInt> datas) {
		this.datas = datas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void findView(MyHolder holder, View cView) {
		holder.tv_nfi = (TextView) cView.findViewById(R.id.text_nfi);
		holder.tv_time = (TextView) cView.findViewById(R.id.text_cjrl_time);
		holder.tv_before = (TextView) cView.findViewById(R.id.text_cjrl_before);

		holder.tv_forecast = (TextView) cView
				.findViewById(R.id.text_cjrl_forecast);
		holder.tv_reality = (TextView) cView
				.findViewById(R.id.text_cjrl_gongbu);
		holder.tv_effect = (TextView) cView.findViewById(R.id.text_cjrl_effect);
		holder.iv_state = (ImageView) cView.findViewById(R.id.img_cjrl_state); // 国旗
		holder.iv_nature = (ImageView) cView.findViewById(R.id.img_cjrl_nature); // 重要性
		holder.ll_effect_good_bad = (LinearLayout) cView
				.findViewById(R.id.linear_good_bad);
		holder.tv_good = (TextView) cView
				.findViewById(R.id.text_cjrl_effectgood_good);
		holder.tv_bad = (TextView) cView.findViewById(R.id.text_cjrl_effectbad);
		// 利多利空中性
		holder.ll_effect_good = (LinearLayout) cView
				.findViewById(R.id.linear_effectgood);
		holder.ll_effect_mid = (LinearLayout) cView
				.findViewById(R.id.linear_effectmid);
		holder.tv_mind = (TextView) cView
				.findViewById(R.id.text_cjrl_effectgood);
		holder.ll_effect_bad = (LinearLayout) cView
				.findViewById(R.id.linear_effectbad);

		holder.img_published = (LinearLayout) cView
				.findViewById(R.id.linear_cjrl_published);
		holder.img_publish = (ImageView) cView
				.findViewById(R.id.img_cjrl_publish);

		holder.ll_timer = (LinearLayout) cView
				.findViewById(R.id.linear_cjrl_timer);

		holder.tv_timer = (TextView) cView.findViewById(R.id.text_cjrl_timer);
		holder.tv_timer.setTag("default");
		if (isYj) {
			// 变色
			holder.tv_timer.setTextColor(context.getResources().getColor(
					R.color.cjrl_color_blue));
			holder.tv_timer.setBackground(context.getResources().getDrawable(
					R.drawable.cjrl_timer_default_shap_night));

			Drawable drawable = context.getResources().getDrawable(
					R.drawable.clock_blue);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			holder.tv_timer.setCompoundDrawables(drawable, null, null, null);
		} else {
			// 变色
			holder.tv_timer.setTextColor(context.getResources().getColor(
					R.color.cjrl_color_blue));
			holder.tv_timer.setBackground(context.getResources().getDrawable(
					R.drawable.cjrl_timer_default_shap));

			Drawable drawable = context.getResources().getDrawable(
					R.drawable.clock_blue);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			holder.tv_timer.setCompoundDrawables(drawable, null, null, null);
		}

	}

	/**
	 * 设值
	 */
	public void setData(final MyHolder holder, int position) {

		final CjrlDataInt data = datas.get(position);
		holder.tv_nfi.setText(data.getState() + data.getNFI());
		holder.tv_time.setText(data.getPredictTime());
		holder.tv_before.setText(data.getBefore());
		holder.tv_forecast.setText(data.getForecast());
		holder.tv_reality.setText("" + data.getReality());
		holder.tv_timer.setText(data.getPredictTime());
		readSj();

		if (rlSjcodes != null && rlSjcodes.size() > 0) {

			if (rlSjcodes.contains(data.getAutoID() + "")) {
				defaultBg(holder.tv_timer);
				holder.tv_timer.setTag("click");
			} else {
				editor = preferences.edit();
				editor.remove(data.getAutoID() + "").commit();
				clickBg(holder.tv_timer);
				holder.tv_timer.setTag("default");
			}
		} else {
			editor = preferences.edit();
			editor.remove(data.getAutoID() + "").commit();
			clickBg(holder.tv_timer);
			holder.tv_timer.setTag("default");
		}

		if (-1 == preferences.getInt(data.getAutoID() + "", -1)) {

			clickBg(holder.tv_timer);
			holder.tv_timer.setTag("default");
		} else if (preferences.getInt(data.getAutoID() + "", -1) == data
				.getAutoID()) {
			defaultBg(holder.tv_timer);
			holder.tv_timer.setTag("click");

		}

		holder.tv_timer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tab = (String) v.getTag();
				if (tab.equals("default")) {
					showSelectView(data);

				} else {
					v.setTag("default");
					clickBg(v);
					deleteSj(data);
					editor = preferences.edit();
					editor.remove(data.getAutoID() + "").commit();
				}
			}
		});

		String state = data.getState(); // 国家
		if (state.equals(ConstantValue.STATE_CHINA)) {
			holder.iv_state.setImageResource(R.drawable.state_china);
		} else if (state.equals(ConstantValue.STATE_AMERICAN)) {
			holder.iv_state.setImageResource(R.drawable.state_american);
		} else if (state.equals(ConstantValue.STATE_GERMAN)) {
			holder.iv_state.setImageResource(R.drawable.state_german);
		} else if (state.equals(ConstantValue.STATE_ENGLAND)) {
			holder.iv_state.setImageResource(R.drawable.state_england);
		} else if (state.equals(ConstantValue.STATE_FRANCE)) {
			holder.iv_state.setImageResource(R.drawable.state_france);
		} else if (state.equals(ConstantValue.STATE_AUSTRALIA)) {
			holder.iv_state.setImageResource(R.drawable.state_australia);
		} else if (state.equals(ConstantValue.STATE_JAPAN)) {
			holder.iv_state.setImageResource(R.drawable.state_japan);
		} else if (state.equals(ConstantValue.STATE_KOREA)) {
			holder.iv_state.setImageResource(R.drawable.state_korea);
		} else if (state.equals(ConstantValue.STATE_CANADA)) {
			holder.iv_state.setImageResource(R.drawable.state_canada);
		} else if (state.equals(ConstantValue.STATE_HONGKONG)) {
			holder.iv_state.setImageResource(R.drawable.state_hongkong);
		} else if (state.equals(ConstantValue.STATE_SWITZERLAND)) {
			holder.iv_state.setImageResource(R.drawable.state_swizerland);
		} else if (state.equals(ConstantValue.STATE_ITALY)) {
			holder.iv_state.setImageResource(R.drawable.state_italy);
		} else if (state.equals(ConstantValue.STATE_EURO_AREA)) {
			holder.iv_state.setImageResource(R.drawable.state_europe_area);
		} else if (state.equals(ConstantValue.STATE_NEW_ZEALAND)) {
			holder.iv_state.setImageResource(R.drawable.state_newzealand);
		} else if (state.equals(ConstantValue.STATE_TAIWAN)) {
			holder.iv_state.setImageResource(R.drawable.state_taiwan);
		} else if (state.equals(ConstantValue.STATE_SPANISH)) {
			holder.iv_state.setImageResource(R.drawable.state_spanish);
		} else if (state.equals(ConstantValue.STATE_SINGAPORE)) {
			holder.iv_state.setImageResource(R.drawable.state_singapore);
		} else if (state.equals(ConstantValue.STATE_BRAZIL)) {
			holder.iv_state.setImageResource(R.drawable.state_brazil);
		} else if (state.equals(ConstantValue.STATE_SOUTH_AFRICA)) {
			holder.iv_state.setImageResource(R.drawable.state_south_africa);
		} else if (state.equals(ConstantValue.STATE_INDIA)) {
			holder.iv_state.setImageResource(R.drawable.state_india);
		} else if (state.equals(ConstantValue.STATE_INDONESIA)) {
			holder.iv_state.setImageResource(R.drawable.state_indonesia);
		} else if (state.equals(ConstantValue.STATE_RUSSIA)) {
			holder.iv_state.setImageResource(R.drawable.state_russia);
		} else if (state.equals(ConstantValue.STATE_GREECE)) {
			holder.iv_state.setImageResource(R.drawable.state_greece);
		} else if (state.equals(ConstantValue.STATE_ISRAEL)) {
			holder.iv_state.setImageResource(R.drawable.state_israel);
		} else {
			holder.iv_state.setImageResource(R.drawable.state_default);
			// 没有就设一个默认的
		}

		String nature = data.getNature(); // 重要性
		if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_HIGH)) {
			holder.iv_nature.setImageResource(R.drawable.nature_high);
		} else if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_MID)) {
			holder.iv_nature.setImageResource(R.drawable.nature_mid);
		} else if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_LOW)) {
			holder.iv_nature.setImageResource(R.drawable.nature_low);
		}

		// 利多利空-初始化时两个都设为View.GONE
		holder.ll_effect_good_bad.setVisibility(View.GONE);
		holder.tv_effect.setVisibility(View.GONE);
		holder.ll_effect_good.setVisibility(View.GONE);
		holder.ll_effect_mid.setVisibility(View.GONE);
		holder.tv_mind.setVisibility(View.GONE);
		holder.ll_effect_bad.setVisibility(View.GONE);

		holder.img_published.setVisibility(View.GONE);
		holder.img_publish.setVisibility(View.GONE);
		holder.ll_timer.setVisibility(View.GONE);

		// 值没错，写上去的时候出错了
		String effect = data.getEffect();
		String effectMid = null;
		String effectGood = null;
		String effectBad = null;

		String[] titles = effect.split("\\|");
		if (titles.length > 0 && titles.length == 1) {
			effectGood = titles[0];// 利空
		} else if (titles.length > 0 && titles.length == 2) {
			effectBad = titles[1];// 利多
			effectGood = titles[0];// 利空
		} else {
			effectMid = "影响较小";
		}
		if (null != data.getReality() && "" != data.getReality()
				&& "--".equals(data.getReality())) {
			// 没有公布值
			holder.ll_effect_good_bad.setVisibility(View.GONE);
			holder.img_published.setVisibility(View.GONE);
			holder.img_publish.setVisibility(View.VISIBLE);
			holder.ll_timer.setVisibility(View.VISIBLE);

			// ---------------------------

		} else if (null != data.getReality() && "" != data.getReality()
				&& !"--".equals(data.getReality())) {
			// 有公布值
			holder.img_published.setVisibility(View.VISIBLE);
			holder.img_publish.setVisibility(View.GONE);
			holder.ll_timer.setVisibility(View.GONE);

			if (!TextUtils.isEmpty(effectMid)) {
				// 给mid设置

				holder.ll_effect_good_bad.setVisibility(View.VISIBLE);
				holder.ll_effect_mid.setVisibility(View.VISIBLE);
				holder.tv_mind.setVisibility(View.VISIBLE);
				holder.tv_mind.setText(effectMid);

			} else if ((!TextUtils.isEmpty(effectGood))
					|| (!TextUtils.isEmpty(effectBad))) {
				if (!TextUtils.isEmpty(effectGood)) {
					if (effectGood.contains(EFFECT_DOLLOR) && bool_dollor) {
						holder.ll_effect_good_bad.setVisibility(View.VISIBLE);
						holder.ll_effect_good.setVisibility(View.VISIBLE);
						holder.tv_good.setText(effectGood);
					} else if (effectGood.contains(EFFECT_GOLD_SILVER)
							&& effectGood.contains(EFFECT_OIL)) {
						if (bool_gold_silver && bool_oil) {
							holder.ll_effect_good_bad
									.setVisibility(View.VISIBLE);
							holder.ll_effect_good.setVisibility(View.VISIBLE);
							holder.tv_good.setText(effectGood);
						} else {
							// 啥也不做..
						}
					} else {
						// 啥也不做
					}
				}

				if (!TextUtils.isEmpty(effectBad)) {
					if (effectBad.contains(EFFECT_DOLLOR) && bool_dollor) {
						holder.ll_effect_good_bad.setVisibility(View.VISIBLE);
						holder.ll_effect_bad.setVisibility(View.VISIBLE);
						holder.tv_bad.setText(effectBad);
					} else if (effectBad.contains(EFFECT_GOLD_SILVER)
							&& effectBad.contains(EFFECT_OIL)) {
						if (bool_gold_silver && bool_oil) {
							holder.ll_effect_good_bad
									.setVisibility(View.VISIBLE);
							holder.ll_effect_bad.setVisibility(View.VISIBLE);
							holder.tv_bad.setText(effectBad);
						} else {
							// 啥也不做..
						}
					} else if (effectBad.contains(EFFECT_GOLD_SILVER)
							&& !effectBad.contains(EFFECT_OIL)
							&& bool_gold_silver) {
						holder.ll_effect_good_bad.setVisibility(View.VISIBLE);
						holder.ll_effect_bad.setVisibility(View.VISIBLE);
						holder.tv_bad.setText(EFFECT_GOLD_SILVER);
					} else if (!effectGood.contains(EFFECT_GOLD_SILVER)
							&& effectGood.contains(EFFECT_OIL) && bool_oil) {
						holder.ll_effect_good_bad.setVisibility(View.VISIBLE);
						holder.ll_effect_bad.setVisibility(View.VISIBLE);
						holder.tv_bad.setText(EFFECT_OIL);
					} else {
						// 啥也不做
					}
				}

			} else {

				holder.tv_effect.setVisibility(View.VISIBLE);
				String effect1 = data.getEffect(); // 利多利空
				if (effect1.contains("利空")) {
					holder.tv_effect.setText("" + effect1);
					holder.tv_effect.setTextColor(Color.rgb(138, 194, 119));
					holder.tv_effect
							.setBackgroundResource(R.drawable.cjrl_effect_bg_green);
				} else if (effect1.contains("利多")) {
					holder.tv_effect.setText("" + effect1);
					holder.tv_effect.setTextColor(Color.rgb(202, 70, 57));
					holder.tv_effect
							.setBackgroundResource(R.drawable.cjrl_effect_bg_red);
				} else if (effect1.length() > 0) {
					holder.tv_effect.setText("" + effect1);
					holder.tv_effect.setTextColor(Color.rgb(243, 176, 86));
					holder.tv_effect
							.setBackgroundResource(R.drawable.cjrl_effect_bg_yellow);
				} else {
					holder.tv_effect.setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	public View getView(int position, View cView, ViewGroup parent) {
		// TODO Auto-generated method stub

		MyHolder holder;
		if (cView == null) {
			holder = new MyHolder();
			cView = View.inflate(context, R.layout.view_cjrl_date_item, null);

			cView.setTag(holder);
		} else {
			holder = (MyHolder) cView.getTag();
		}
		findView(holder, cView);
		setData(holder, position);
		return cView;
	}

	private class MyHolder {
		TextView tv_nfi; // 指标名称
		TextView tv_time; // 时间
		TextView tv_before; // 前值
		TextView tv_forecast; // 预测值
		TextView tv_reality; // 公布值
		TextView tv_effect;

		// 增
		LinearLayout ll_effect_good_bad;
		LinearLayout ll_effect_good;
		LinearLayout ll_effect_mid;
		LinearLayout ll_effect_bad;
		LinearLayout ll_timer;// 定时

		TextView tv_good; // 利多
		TextView tv_bad; // 利空
		TextView tv_mind;// 影响较小
		TextView tv_timer;

		ImageView iv_state; // 国旗
		ImageView iv_nature; // 重要性
		LinearLayout img_published;// 已公布
		ImageView img_publish;// 未公布
	}

	// 添加账户
	private void initCalendars(CjrlDataInt data) {

		TimeZone timeZone = TimeZone.getDefault();
		ContentValues value = new ContentValues();
		value.put(Calendars.NAME, "yy");

		value.put(Calendars.ACCOUNT_NAME, data.getAutoID());
		value.put(Calendars.ACCOUNT_TYPE, "com.android.exchange");
		value.put(Calendars.CALENDAR_DISPLAY_NAME, "mytt");
		value.put(Calendars.VISIBLE, 1);
		value.put(Calendars.CALENDAR_COLOR, -9206951);
		value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		value.put(Calendars.SYNC_EVENTS, 1);
		value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
		value.put(Calendars.OWNER_ACCOUNT, data.getAutoID() + "@gmail.com");
		value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

		Uri calendarUri = Calendars.CONTENT_URI;
		calendarUri = calendarUri
				.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,
						"true")
				.appendQueryParameter(Calendars.ACCOUNT_NAME,
						data.getAutoID() + "@gmail.com")
				.appendQueryParameter(Calendars.ACCOUNT_TYPE,
						"com.android.exchange").build();

		context.getContentResolver().insert(calendarUri, value);
	}

	/**
	 * 读取日历中的事件
	 */
	public void readSj() {
		rlSjcodes = new ArrayList<String>();
		Cursor eventCursor = context.getContentResolver().query(
				Uri.parse(calanderEventURL), null, null, null, null);
		if (null != eventCursor && eventCursor.getCount() > 0) {
			while (eventCursor.moveToNext()) {
				String eventTitle = eventCursor.getString(eventCursor
						.getColumnIndex("account_name"));
				rlSjcodes.add(eventTitle);
			}

		}

	}

	/**
	 * 删除提醒事件
	 */
	public void deleteSj(CjrlDataInt data) {
		// int rownum = context.getContentResolver().delete(
		// Uri.parse(calanderURL), "account_name=" + data.getAutoID(),
		// null); // 注意：会全部删除所有账户，新添加的账户一般从id=1开始，
		// // 可以令_id=你添加账户的id，以此删除你添加的账户
		Toast.makeText(context, "取消提醒", Toast.LENGTH_LONG).show();
	}

	/**
	 * 添加提醒事件
	 */
	public void addSj(CjrlDataInt data, int minTimer, View v) {
		// 获取要出入的gmail账户的id
		String calId = "";
		int cMin = 0;
		switch (minTimer) {
		case 0:
			cMin = 5;
			break;
		case 1:
			cMin = 10;
			break;
		case 2:
			cMin = 15;
			break;
		case 3:
			cMin = 30;
			break;

		default:
			break;
		}

		initCalendars(data);
		Cursor userCursor = context.getContentResolver().query(
				Uri.parse(calanderURL), null, null, null, null);
		if (null != userCursor && userCursor.getCount() > 0) {
			userCursor.moveToLast(); // 注意：是向最后一个账户添加，开发者可以根据需要改变添加事件 的账户
			calId = userCursor.getString(userCursor.getColumnIndex("_id"));

			// String name = userCursor.getString(userCursor
			// .getColumnIndex("account_name"));
		} else {

			return;
		}

		ContentValues event = new ContentValues();
		event.put("title", data.getNFI());
		event.put("description", data.getState() + data.getNFI());
		// 插入账户
		event.put("calendar_id", calId);
		event.put("eventLocation", data.getState());

		Calendar mCalendar = Calendar.getInstance();

		int year = Integer.valueOf(data.getDate().substring(0, 4));
		int month = Integer.valueOf(data.getDate().substring(5, 7));
		int day = Integer.valueOf(data.getDate().substring(8, 10));
		mCalendar.set(year, month - 1, day);
		int hour = Integer.valueOf(data.getPredictTime().substring(0, 2));
		int min = Integer.valueOf(data.getPredictTime().substring(3, 5));
		mCalendar.set(Calendar.HOUR_OF_DAY, hour);
		mCalendar.set(Calendar.MINUTE, min - cMin);

		long start = mCalendar.getTime().getTime();
		mCalendar.set(Calendar.HOUR_OF_DAY, hour);
		mCalendar.set(Calendar.MINUTE, min);
		long end = mCalendar.getTime().getTime();
		event.put("dtstart", start);
		event.put("dtend", end);
		event.put("hasAlarm", 1);

		event.put(Events.EVENT_TIMEZONE, "Asia/Shanghai"); // 这个是时区，必须有，
		// 添加事件
		Uri newEvent = context.getContentResolver().insert(
				Uri.parse(calanderEventURL), event);
		// 事件提醒的设定
		long id = Long.parseLong(newEvent.getLastPathSegment());
		ContentValues values = new ContentValues();
		values.put("event_id", id);

		// 提前10分钟有提醒
		values.put("minutes", cMin);
		context.getContentResolver().insert(Uri.parse(calanderRemiderURL),
				values);

	}

	/**
	 * 默认的背景
	 */
	public void defaultBg(View v) {
		if (isYj) {
			// 变色
			((TextView) v).setTextColor(context.getResources().getColor(
					R.color.rl_top_scroll_color_default_night));
			((TextView) v).setBackground(context.getResources().getDrawable(
					R.drawable.cjrl_timer_shap_selected_night));

			Drawable drawable = context.getResources().getDrawable(
					R.drawable.clock_night);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			((TextView) v).setCompoundDrawables(drawable, null, null, null);
		} else {
			// 变色
			((TextView) v).setTextColor(context.getResources().getColor(
					R.color.white));
			((TextView) v).setBackground(context.getResources().getDrawable(
					R.drawable.cjrl_timer_shap_selected));

			Drawable drawable = context.getResources().getDrawable(
					R.drawable.clock_write);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			((TextView) v).setCompoundDrawables(drawable, null, null, null);
		}
	}

	/**
	 * 选择后的背景
	 */
	public void clickBg(View v) {
		if (isYj) {
			((TextView) v).setTextColor(context.getResources().getColor(
					R.color.cjrl_color_blue));
			((TextView) v).setBackground(context.getResources().getDrawable(
					R.drawable.cjrl_timer_default_shap_night));
			Drawable drawable = context.getResources().getDrawable(
					R.drawable.clock_blue);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			((TextView) v).setCompoundDrawables(drawable, null, null, null);
		} else {
			((TextView) v).setTextColor(context.getResources().getColor(
					R.color.cjrl_color_blue));
			((TextView) v).setBackground(context.getResources().getDrawable(
					R.drawable.cjrl_timer_default_shap));
			Drawable drawable = context.getResources().getDrawable(
					R.drawable.clock_blue);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			((TextView) v).setCompoundDrawables(drawable, null, null, null);
		}
	}

	/**
	 * 弹出PopWindow
	 */
	public void showSelectView(CjrlDataInt data) {

		Intent intent = new Intent(context, CjrlSelectTimeActivity.class);
		intent.putExtra("data", data);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

	}

}
