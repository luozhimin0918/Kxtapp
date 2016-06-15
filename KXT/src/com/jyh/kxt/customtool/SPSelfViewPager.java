package com.jyh.kxt.customtool;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SPSelfViewPager extends ViewPager {
	PointF downP = new PointF();
	PointF curP = new PointF();
	private boolean IsLanj=true;
	OnSingleTouchListener onSingleTouchListener;

	public SPSelfViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public SPSelfViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return IsLanj;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		// 每次进行onTouch事件都记录当前的按下的坐标
		curP.x = arg0.getX();
		curP.y = arg0.getY();
		if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
			// 记录按下时候的坐标
			// 切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
			downP.x = arg0.getX();
			downP.y = arg0.getY();
			// 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
			getParent().requestDisallowInterceptTouchEvent(true);
		}

		if (arg0.getAction() == MotionEvent.ACTION_MOVE) {
			// 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
			getParent().requestDisallowInterceptTouchEvent(true);
		}

		if (arg0.getAction() == MotionEvent.ACTION_UP) {
			// 在up时判断是否按下和松手的坐标为一个点
			// 如果是一个点，将执行点击事件，这是我自己写的点击事件，而不是onclick
			if (downP.x == curP.x && downP.y == curP.y) {
				onSingleTouch();
				return false;
			}
			if (downP.x < curP.x || curP.x < downP.x) {
				float xend = downP.x - curP.x;
				if (xend > 0 && xend < 10) {
					onSingleTouch();
					return false;
				} else if (xend < 0 && xend > -10) {
					onSingleTouch();
					return false;
				}
			}
			if (downP.y < curP.y || curP.y < downP.y) {
				float yend = downP.x - curP.x;
				if (yend > 0 && yend < 10) {
					onSingleTouch();
					return false;
				} else if (yend < 0 && yend > -10) {
					onSingleTouch();
					return false;
				}
			}
		}
		return super.onTouchEvent(arg0);
	}

	public void onSingleTouch() {
		if (onSingleTouchListener != null) {
			onSingleTouchListener.onSingleTouch();
		} else {
		}
	}

	public interface OnSingleTouchListener {
		public void onSingleTouch();
	}

	public void setOnSingleTouchListener(
			OnSingleTouchListener onSingleTouchListener) {
		this.onSingleTouchListener = onSingleTouchListener;
	}

	public boolean isIsLanj() {
		return IsLanj;
	}

	public void setIsLanj(boolean isLanj) {
		IsLanj = isLanj;
	}
}
