package com.jyh.kxt.customtool;

import com.jyh.kxt.MainActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * 
 * @author Administrator
 * 
 */
public class CHScrollView extends HorizontalScrollView {

	MainActivity context;

	public CHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = (MainActivity) context;
	}

	public CHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (MainActivity) context;
	}

	public CHScrollView(Context context) {
		super(context);
		this.context = (MainActivity) context;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		context.mTouchView = this;
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (context.mTouchView == this) {
			context.onScrollChanged(l, t, oldl, oldt);
		} else {
			super.onScrollChanged(l, t, oldl, oldt);
		}

	}
}
