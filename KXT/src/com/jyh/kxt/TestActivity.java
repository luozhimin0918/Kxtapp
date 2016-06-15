package com.jyh.kxt;

import com.jyh.kxt.R;
import com.jyh.kxt.customtool.ColumnHorizontalScrollView;
import com.jyh.kxt.customtool.Test_mian_ZDY;
import com.jyh.tool.BaseTools;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TestActivity extends FragmentActivity {
	private ColumnHorizontalScrollView mColumnHorizontalScrollView;// 头部滚动条
	private LinearLayout mRadioGroup_content;
	private LinearLayout ll_more_columns;
	private int mScreenWidth = 0;// 屏幕宽度
	private int mItemWidth = 0;// item的宽度
	private Test_mian_ZDY columnTextView;
	private RelativeLayout rl_column;
	private int columnSelectIndex = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_test);

		mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.mColumnHorizontalScrollView);
		mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
		ll_more_columns = (LinearLayout) findViewById(R.id.ll_more_columns);
		rl_column = (RelativeLayout) findViewById(R.id.rl_column);

		mScreenWidth = BaseTools.getWindowsWidth(this);
		mItemWidth = mScreenWidth / 16;// 一个Item宽度为屏幕的1/16
		initScrollView();
	}

	public void initScrollView() {
		mColumnHorizontalScrollView.setParam(this, mScreenWidth,
				mRadioGroup_content, null, null, ll_more_columns, rl_column);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = 5;
		params.rightMargin = mItemWidth;
		for (int i = 0; i < 15; i++) {

			columnTextView = new Test_mian_ZDY(this, false);
			columnTextView.setTextDate("." + i, 20, getResources()
					.getColorStateList(R.color.hq_color_start));
			if (columnSelectIndex == i) {
				columnTextView.setSelected(true);
			}
			columnTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
						View localView = mRadioGroup_content.getChildAt(i);
						if (localView != v) {
							localView.setSelected(false);
						} else {
							localView.setSelected(true);
							if (columnSelectIndex != i) {
								columnSelectIndex = i;
								selectTab(columnSelectIndex);
							}
						}
					}
				}
			});
			mRadioGroup_content.addView(columnTextView, i, params);
		}
	}

	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
		}
		// 判断是否选中
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}
}
