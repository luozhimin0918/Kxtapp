package com.jyh.fragment;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.jyh.kxt.R;
import com.jyh.kxt.customtool.FixedSpeedScroller;
import com.jyh.kxt.customtool.MyViewPager;
import com.jyh.kxt.customtool.SegmentView;
import com.jyh.kxt.customtool.SegmentView.onSegmentViewClickListener;
import com.jyh.kxt.socket.KXTApplication;

/**
 * 行情主页面
 * 
 * @author Administrator
 *
 */
public class fragment_kxthq extends Fragment {

	private View view;
	private MyViewPager viewPager;
	private FragmentManager fm;
	private SharedPreferences preferences;
	private Editor editor;
	private boolean Isyj = false;
	private FixedSpeedScroller mScroller;
	// private RefreshListener refListener;
	private Context context;
	protected WeakReference<View> mRootView;
	private SegmentView segmentView;
	private fragment_hq_hq fragment_hq;
	private Fragment_hq_zx fragment_zx;
	private KXTApplication app;
	private MyAdapter adpater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		savedInstanceState = null;
		preferences = context.getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (mRootView == null || mRootView.get() == null) {

			view = inflater.inflate(R.layout.fragmetn_kxthq, null);
			init();
			initSegmentView();
			mRootView = new WeakReference<View>(view);
		} else {
			ViewGroup parent = (ViewGroup) mRootView.get().getParent();
			if (parent != null) {
				parent.removeView(mRootView.get());
			}
		}
		return mRootView.get();
	}

	/**
	 * 初始化SegmentView
	 */
	public void initSegmentView() {
		segmentView = (SegmentView) view.findViewById(R.id.segment_hq);
		segmentView.setSegmentText("行情", 0);
		segmentView.setSegmentText("自选", 2);
		segmentView.setSegmentText(null, 1);
		segmentView.changeBackGruond(Isyj);
		segmentView
				.setOnSegmentViewClickListener(new onSegmentViewClickListener() {

					@Override
					public void onSegmentViewClick(View v, int position) {
						// TODO Auto-generated method stub
						switch (position) {
						case 0:
							if (editor != null) {
								editor.putBoolean("iszx", false).commit();
								viewPager.setCurrentItem(0);
								mScroller.setmDuration(250);
								fragment_hq.Resom();
							}

							break;
						case 2:
							if (editor != null) {

								editor.putBoolean("iszx", true).commit();
								Intent intent = new Intent();
								intent.setAction("行情注册完成");
								intent.putExtra("zx", "ok");
								context.sendBroadcast(intent);
								viewPager.setCurrentItem(1);
								mScroller.setmDuration(250);
							}

							break;
						default:
							break;
						}
					}
				});
	}

	/**
	 * 初始化方法
	 */
	public void init() {

		app = (KXTApplication) getActivity().getApplication();
		fragment_hq = new fragment_hq_hq();
		fragment_zx = new Fragment_hq_zx();
		app.setFragmentHq(fragment_hq);
		app.setFragmentZx(fragment_zx);
		// refListener = (RefreshListener) context;
		fm = getChildFragmentManager();
		preferences = context.getSharedPreferences("hqview",
				Context.MODE_PRIVATE);
		editor = preferences.edit();

		viewPager = (MyViewPager) view.findViewById(R.id.pager_kxthq);

		adpater = new MyAdapter(fm);
		viewPager.setAdapter(adpater);
		viewPager.setOffscreenPageLimit(0);
		try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			mScroller = new FixedSpeedScroller(viewPager.getContext(),
					new AccelerateInterpolator());
			mField.set(viewPager, mScroller);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class MyAdapter extends FragmentPagerAdapter {
		public MyAdapter(FragmentManager fm) {
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
				return fragment_hq;
			case 1:
				return fragment_zx;
			}
			return null;
		}

		@Override
		public int getCount() {
			// 多少页
			return 2;
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}

	public void refreshView() {
		view.invalidate();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = activity;
	}

}
