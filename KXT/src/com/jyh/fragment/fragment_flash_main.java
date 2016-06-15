package com.jyh.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.MainActivity.FragmentActivityResult;
import com.jyh.kxt.customtool.SegmentView;
import com.jyh.kxt.customtool.SegmentView.onSegmentViewClickListener;

public class fragment_flash_main extends Fragment implements
		FragmentActivityResult {
	private View view;
	private fragment_flash flash;// 快讯界面
	private fragment_dp fragment_dp;// 点评界面
	private fragment_sp fragment_sp;// 视听界面
	private ViewPager viewPager;
	private DemoAdapter adapter;
	private SharedPreferences preferences;
	private boolean Isyj = false;
	private Context context;
	private SegmentView segmentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		savedInstanceState = null;
		preferences = context.getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			context.setTheme(R.style.BrowserThemeNight);
		} else {
			context.setTheme(R.style.BrowserThemeDefault);
		}
		view = inflater.inflate(R.layout.fragment_flash, container, false);
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		segmentView = (SegmentView) view.findViewById(R.id.segment_flash);
		segmentView.setSegmentText("直播", 0);// 设置头部标签文本
		segmentView.setSegmentText("点评", 1);
		segmentView.setSegmentText("视频", 2);
		segmentView.changeBackGruond(Isyj);
		flash = new fragment_flash();
		fragment_dp = new fragment_dp();
		fragment_sp = new fragment_sp();
		FragmentManager fm = getChildFragmentManager();// 切记 不用这个子的fragment不会显示
		adapter = new DemoAdapter(fm);
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(0);
		segmentView
				.setOnSegmentViewClickListener(new onSegmentViewClickListener() {

					@Override
					public void onSegmentViewClick(View v, int position) {
						// TODO Auto-generated method stub
						viewPager.setCurrentItem(position);
					}
				});
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				segmentView.setCurtunSegment(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		return view;
	}

	class DemoAdapter extends FragmentPagerAdapter {
		public DemoAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return flash;
			case 1:
				
				return new fragment_dp();
			case 2:
				return fragment_sp;
			}
			return null;
		}

		@Override
		public int getCount() {
			// 多少页
			return 3;
		}

	}

	// 给三个imageview添加监听
	class MyListener implements View.OnClickListener {

		private int index = 0;

		public MyListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}

	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				viewPager.setCurrentItem(1);
				break;
			case 2:
				viewPager.setCurrentItem(2);
				break;
			case 0:
				viewPager.setCurrentItem(0);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = activity;
	}

	@Override
	public void OnActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (resultCode == 102)
			((FragmentActivityResult) fragment_sp).OnActivityResult(
					requestCode, resultCode, data);
	}
}
