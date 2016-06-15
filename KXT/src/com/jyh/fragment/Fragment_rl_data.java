package com.jyh.fragment;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jyh.gson.bean.CjrlDataInt;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.CjrlDateAdpater;
import com.jyh.kxt.socket.KXTApplication;

/**
 * 财经日历 数据
 * 
 * @author Administrator
 *
 */
public class Fragment_rl_data extends Fragment {
	private View view;
	private List<CjrlDataInt> datas;
	private PullToRefreshListView listView;
	private CjrlDateAdpater adpater;
	private LinearLayout linear_data;
	private SharedPreferences preferences;
	private boolean isYj = false;
	private Context context;
	protected WeakReference<View> mRootView;
	private Handler handler;
	private Timer timer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mRootView == null || mRootView.get() == null) {
			view = inflater.inflate(R.layout.fragment_rl_date, null);
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
	 * 初始化控件
	 */
	public void findViews() {
		preferences = getActivity().getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		isYj = preferences.getBoolean("yj_btn", false);

		listView = (PullToRefreshListView) view
				.findViewById(R.id.listview_rl_date);
		linear_data = (LinearLayout) view.findViewById(R.id.linear_rl_data);

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				String time = sdf.format(date);
				if (time.equals("00:01")) {
					handler.sendEmptyMessage(30);
				}
			}
		}, 0, 1000);
	}

	/**
	 * 设置数据源
	 * 
	 * @param datas
	 */
	public void setDatas(List<CjrlDataInt> datas, Handler thandler) {
		this.handler = thandler;
		if (null != this.datas) {
			this.datas = null;
		}
		if (null != datas && datas.size() > 0) {
			this.datas = datas;
			initDatas();
			adpater.setDatas(datas);
			adpater.notifyDataSetChanged();
			listView.onRefreshComplete();
		} else {
			if (null != listView) {
				listView.setVisibility(View.GONE);
			}
			if (null != linear_data) {
				linear_data.setVisibility(View.VISIBLE);
			}
		}

	}

	/**
	 * 初始化数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initDatas() {
		findViews();
		listView.setVisibility(View.VISIBLE);
		linear_data.setVisibility(View.GONE);
		if (adpater == null) {
			adpater = new CjrlDateAdpater(context, datas, isYj,
					(KXTApplication) getActivity().getApplication());
			listView.setAdapter(adpater);
			listView.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh(PullToRefreshBase refreshView) {
					// TODO Auto-generated method stub
					// 下拉刷新
					handler.sendEmptyMessage(30);
				}

			});
		}
		setListViewSelection();
	}

	/**
	 * 设置listView的选中项
	 */
	public void setListViewSelection() {
		Date now = new Date();

		for (int i = 0; i < datas.size(); i++) {
			String pTime = datas.get(i).getPredictTime();
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
			String ntime = sf.format(now);

			try {
				Date d1 = sf.parse(pTime);
				Date d2 = sf.parse(ntime);
				if (d1.getTime() == d2.getTime()) {
					listView.getRefreshableView().setSelection(i);

				} else {
					if (d1.getTime() < d2.getTime()) {
						listView.getRefreshableView().setSelection(i);
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		adpater.notifyDataSetChanged();
		listView.onRefreshComplete();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.context = activity;
	}

	public CjrlDateAdpater getAdpater() {
		return adpater;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
