package com.jyh.kxt.customtool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class ColumnHorizontalScrollView extends HorizontalScrollView {
	private View ll_content;
	private View ll_more;
	private View rl_column;
	private int mScreenWitdh = 0;
	private Activity activity;

	private Runnable scrollerTask;
	private int intitPosition;
	private int newCheck = 100;
	private int childWidth = 0;
	private OnScrollStopListner onScrollstopListner;

	public ColumnHorizontalScrollView(Context context) {
		super(context);
		initScroll();
	}

	public ColumnHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initScroll();
	}

	public ColumnHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initScroll();
	}

	public void initScroll() {
		scrollerTask = new Runnable() {
			@Override
			public void run() {
				int newPosition = getScrollX();
				if (intitPosition - newPosition == 0) {
					if (onScrollstopListner == null) {
						return;
					}
					onScrollstopListner.onScrollStoped();
					Rect outRect = new Rect();
					getDrawingRect(outRect);
					if (getScrollX() == 0) {
						onScrollstopListner.onScrollToLeftEdge();
					} else if (childWidth + getPaddingLeft()
							+ getPaddingRight() == outRect.right) {
						onScrollstopListner.onScrollToRightEdge();
					} else {
						onScrollstopListner.onScrollToMiddle();
					}
				} else {
					intitPosition = getScrollX();
					postDelayed(scrollerTask, newCheck);
				}
			}
		};
	}

	@Override
	protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		// TODO Auto-generated method stub
		super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
		shade_ShowOrHide();
		if (!activity.isFinishing() && ll_content != null && ll_more != null
				&& rl_column != null) {
			if (ll_content.getWidth() <= mScreenWitdh) {
			}
		} else {
			return;
		}
		if (paramInt1 == 0) {
			return;
		}
		if (ll_content.getWidth() - paramInt1 + ll_more.getWidth()
				+ rl_column.getLeft() == mScreenWitdh) {
			return;
		}
	}

	public void setParam(Activity activity, int mScreenWitdh, View paramView1,
			ImageView paramView2, ImageView paramView3, View paramView4,
			View paramView5) {
		this.activity = activity;
		this.mScreenWitdh = mScreenWitdh;
		ll_content = paramView1;
		ll_more = paramView4;
		rl_column = paramView5;
	}

	public void shade_ShowOrHide() {
		if (!activity.isFinishing() && ll_content != null) {
			measure(0, 0);
			if (mScreenWitdh >= getMeasuredWidth()) {
			}
		} else {
			return;
		}
		if (getLeft() == 0) {
			return;
		}
		if (getRight() == getMeasuredWidth() - mScreenWitdh) {
			return;
		}
	}

	public void setOnScrollStopListner(OnScrollStopListner listner) {
		onScrollstopListner = listner;
	}

	public void startScrollerTask() {
		intitPosition = getScrollX();
		postDelayed(scrollerTask, newCheck);
		checkTotalWidth();
	}

	private void checkTotalWidth() {
		if (childWidth > 0) {
			return;
		}
		for (int i = 0; i < getChildCount(); i++) {
			childWidth += getChildAt(i).getWidth();
		}
	}

	public interface OnScrollStopListner {
		/**
		 * scroll have stoped
		 */
		void onScrollStoped();

		/**
		 * scroll have stoped, and is at left edge
		 */
		void onScrollToLeftEdge();

		/**
		 * scroll have stoped, and is at right edge
		 */
		void onScrollToRightEdge();

		/**
		 * scroll have stoped, and is at middle
		 */
		void onScrollToMiddle();
	}
}
