package com.jyh.kxt.customtool;

import com.jyh.fragment.Fragment_hq_zx;
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
public class CHScrollView1 extends HorizontalScrollView {

	MainActivity context;
	//private KXTApplication app;
	private Fragment_hq_zx zx;

	public CHScrollView1(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = (MainActivity) context;
	}

	public CHScrollView1(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (MainActivity) context;
		this.zx = this.context.getFragmentZx();
	}

	public CHScrollView1(Context context) {
		super(context);
		this.context = (MainActivity) context;
		this.zx = this.context.getFragmentZx();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// ���д�����ֵ
		zx.mTouchView = this;
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (zx.mTouchView == this) {
			zx.onScrollChanged(l, t, oldl, oldt);
		} else {
			super.onScrollChanged(l, t, oldl, oldt);
		}
	}

}
