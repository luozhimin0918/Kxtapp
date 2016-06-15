package com.jyh.kxt.customtool;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;

public class HQ_mian_ZDY extends LinearLayout {
	private ImageView imageView;
	private TextView textView;
	private View view;

	public HQ_mian_ZDY(Context context, boolean isnaight) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.hq_main_item, this);
		imageView = (ImageView) view.findViewById(R.id.hq_self_img);
		textView = (TextView) view.findViewById(R.id.hq_self_tv);
		if (isnaight) {
			imageView.setBackgroundResource(R.drawable.hq_self_bg);
		} else {
			imageView.setBackgroundResource(R.drawable.hq_self);
		}
	}

	public void setImageResource(int resId) {
		imageView.setBackgroundResource(resId);
	}

	public void setTextViewText(String text) {
		textView.setText(text);
	}
	public void setTextSize(int size){
		textView.setTextSize(size);
	}

	public void setTextColor(ColorStateList colorStateList) {
		textView.setTextColor(colorStateList);
	}

	public void setBackGrand(int resid) {
		imageView.setBackgroundResource(resid);
	}
}
