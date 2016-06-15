package com.jyh.kxt.customtool;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;

public class Test_mian_ZDY extends LinearLayout {
	private ImageView imageView;
	private TextView text_date;
	private TextView text_week;
	private TextView text_month;
	private View view;

	public Test_mian_ZDY(Context context, boolean isnaight) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.test_main_item, this);
		imageView = (ImageView) view.findViewById(R.id.hq_self_img);
		text_date = (TextView) view.findViewById(R.id.text_date);
		text_week = (TextView) view.findViewById(R.id.text_week);
		text_month = (TextView) view.findViewById(R.id.text_month);
		if (isnaight) {
			imageView.setBackgroundResource(R.drawable.hq_self_bg);
		} else {
			imageView.setBackgroundResource(R.drawable.hq_self);
		}
	}

	public void setImageResource(int resId) {
		imageView.setBackgroundResource(resId);
	}

	public void setTextDate(String text, int size, ColorStateList colors) {
		text_date.setText(text);
		text_date.setTextSize(size);
		// text_date.setTextColor(colors);
	}

	public void setTextWeek(String text, int size, ColorStateList colors) {
		text_week.setText(text);
		text_week.setTextSize(size);
		// text_week.setTextColor(colors);
	}

	public void setTextMonth(String text, int size, ColorStateList colors) {
		text_month.setText(text);
		text_month.setTextSize(size);
		// text_month.setTextColor(colors);
	}

	public void setBackGrand(int resid) {
		imageView.setBackgroundResource(resid);
	}

	public String getTextDate() {
		return (String) text_date.getText();
	}

	public String getTextWeek() {
		return (String) text_week.getText();
	}

	public String getTextMonth() {
		return (String) text_month.getText();
	}
}
