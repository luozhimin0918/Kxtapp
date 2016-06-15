package com.jyh.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jyh.bean.HqDataBean;
import com.jyh.kxt.R;
import com.jyh.kxt.customtool.CHScrollView1;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;

/**
 * 自选详细页面
 * 
 * @author Administrator
 *
 */
public class Fragment_hq_zx extends Fragment {

	private List<CHScrollView1> mHScrollViews = new ArrayList<CHScrollView1>();// 装入所有的HScrollView
	private ImageView leftOk, rightOk, leftNo, rightNo;
	private List<HqDataBean> listList = new ArrayList<HqDataBean>();// ListView
																	// 数据集合
	private KXTApplication application;
	private static ZXListAdapter adapter;
	private View view;
	private ListView mListView;
	private HqDataBean newData;
	public HorizontalScrollView mTouchView;
	private LinearLayout linear_zx_show;
	private RelativeLayout image_zx_show;
	private SharedPreferences preferences;
	private boolean isRunTimer = false;// 是否开始运行定时刷新listview
	private Message msg;
	private Timer timer;
	protected WeakReference<View> mRootView;
	private boolean Isyj = false;
	private Context context;
	private CHScrollView1 headerScroll;

	Handler handle = new Handler() {

		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub 

			switch (msg.what) {
			case 0:
				List<String> flags = new ArrayList<String>();
				if (mListView != null) {

					for (int i = 0; i < listList.size(); i++) {
						HqDataBean bean = listList.get(i);
						if (bean.getFalgs() != null
								&& bean.getFalgs().size() > 0) {
							bean.getFalgs().clear();
						}
					}

					for (int i = 0; i < listList.size(); i++) {
						HqDataBean bean = listList.get(i);
						if (bean.getCode().equals(newData.getCode())) {
							if (!bean.getLast().equals(newData.getLast())) {
								flags.add("last");
							}
							if (!bean.getSwing().equals(newData.getSwing())) {
								flags.add("swing");
							}
							if (!bean.getSwing()
									.equals(newData.getSwingRange())) {
								flags.add("swingRange");
							}
							bean.setLast(newData.getLast());
							bean.setSwing(newData.getSwing());
							bean.setSwingRange(newData.getSwingRange());
							bean.setOpen(newData.getOpen());
							bean.setHigh(newData.getHigh());
							bean.setLow(newData.getLow());
							bean.setLastClose(newData.getLastClose());
							bean.setQuoteTime(newData.getQuoteTime());
							bean.setVolume(newData.getVolume());
							bean.setFalgs(flags);
						}
					}
					adapter.notifyDataSetChanged();
				}
				break;
			case 1:// 恢复默认
				if (listList != null && listList.size() > 0) {
					for (int i = 0; i < listList.size(); i++) {
						HqDataBean bean = listList.get(i);
						bean.setFalgs(null);
					}
					adapter.notifyDataSetChanged();
				}
				break;
			case 3:
				view.invalidate();// 刷新界面
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		preferences = context.getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			context.setTheme(R.style.BrowserThemeNight);
		} else {
			context.setTheme(R.style.BrowserThemeDefault);
		}
		application = (KXTApplication) ((Activity) context).getApplication();

		if (application.ischange && null != mRootView) {
			mRootView.clear();
		}
		if (mRootView == null || mRootView.get() == null) {
			view = inflater.inflate(R.layout.fragment_hq_zx, null);
			initView();
			if (!NetworkCenter.checkNetworkConnection(context)) {
				Toast.makeText(context, "网络异常", Toast.LENGTH_LONG).show();
			} else {
				init();
				// fristDefaultDatas();
			}
			mRootView = new WeakReference<View>(view);
		} else {
			ViewGroup parent = (ViewGroup) mRootView.get().getParent();
			if (parent != null) {
				parent.removeView(mRootView.get());
			}
		}
		return mRootView.get();
	}

	public void initView() {

		image_zx_show = (RelativeLayout) view.findViewById(R.id.image_zx_show);
		linear_zx_show = (LinearLayout) view.findViewById(R.id.linear_zx_show);
		image_zx_show.setVisibility(View.GONE);
		linear_zx_show.setVisibility(View.GONE);
		if (this.listList != null && this.listList.size() > 0) {
			image_zx_show.setVisibility(View.GONE);
			linear_zx_show.setVisibility(View.VISIBLE);
		} else {
			image_zx_show.setVisibility(View.VISIBLE);
			linear_zx_show.setVisibility(View.GONE);
		}

		leftOk = (ImageView) view.findViewById(R.id.iv_left_ok);
		leftNo = (ImageView) view.findViewById(R.id.iv_left_no);
		rightOk = (ImageView) view.findViewById(R.id.iv_right_ok);
		rightNo = (ImageView) view.findViewById(R.id.iv_right_no);
		headerScroll = (CHScrollView1) view
				.findViewById(R.id.item_scroll_title_zx);

		mHScrollViews.add(headerScroll);

		mListView = (ListView) view.findViewById(R.id.scroll_list);
		// mListView.setOnScrollListener(new OnScrollListener() {
		//
		// @Override
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		// // TODO Auto-generated method stub
		// List<String> codes = new ArrayList<String>();
		// for (int i = startIndex; i < endIndex; i++) {
		// codes.add(listList.get(i).getCode());
		//
		// }
		//
		//
		// Intent intent = new Intent();
		// intent.putExtra("init", "init");
		// intent.putExtra("codes", (Serializable) codes);//
		// intent.putExtra("zx", "ok");
		// intent.putExtra("isTop", false);
		// intent.setAction("行情注册完成");
		// context.sendBroadcast(intent);
		// }
		//
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		// // TODO Auto-generated method stub
		// startIndex = firstVisibleItem;
		// endIndex = firstVisibleItem + visibleItemCount;
		// }
		// });
	}

	public void init() {
		adapter = new ZXListAdapter(context, mHScrollViews, mListView, listList);

		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isRunTimer && NetworkCenter.checkNetworkConnection(context)) {
					handle.sendEmptyMessage(1);// 每隔两秒刷新一次listview 防止更新数据推送过慢
				}
			}
		}, 0, 2 * 1000);
	}

	//
	// public void fristDefaultDatas(){
	//
	//
	// List<String> codes = new ArrayList<String>();
	//
	// preferences2 =context.getSharedPreferences("hqsetup",
	// Context.MODE_PRIVATE);
	// String value = preferences2.getString("ischeck", "");
	// Log.i("zml","value--"+ value);
	// if (null != value && !"".equals(value)) {
	// String[] ccs = value.split("\\|");
	//
	// startIndex = 0;
	// if (ccs.length >= 10) {
	// endIndex = 10;
	// } else {
	// endIndex = ccs.length;
	// }
	//
	// Log.i("zml","ccs--"+ ccs.toString());
	// Log.i("zml","startIndex--"+startIndex);
	// Log.i("zml","endIndex--"+ endIndex);
	// for (int i = startIndex; i < endIndex; i++) {
	// if (null != ccs[i] && ccs[i].length() > 1) {
	// codes.add(ccs[i]);
	// }
	// }
	// endIndex=ccs.length-1;
	//
	// }
	//
	// Log.i("zml","codes--"+ codes.toString());
	// Intent intent = new Intent();
	// intent.putExtra("init", "init");
	// intent.putExtra("codes", (Serializable) codes);//
	// intent.putExtra("zx", "ok");
	// intent.setAction("行情注册完成");
	// context.sendBroadcast(intent);
	// }

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		preferences = context.getSharedPreferences("hqsetup",
				Context.MODE_PRIVATE);
		String scdata = preferences.getString("ischeck", "");
		if (scdata == null || "".equals(scdata)) {
			image_zx_show.setVisibility(View.VISIBLE);
			linear_zx_show.setVisibility(View.GONE);
		} else {
			if (!NetworkCenter.checkNetworkConnection(context)) {
				image_zx_show.setVisibility(View.VISIBLE);
				linear_zx_show.setVisibility(View.GONE);
				Toast.makeText(context, "网络异常,获取自选数据失败", Toast.LENGTH_LONG)
						.show();
			} else {
				// Log.i("zxapp", ""+application.ischange+"   "+"onCreateView");
				// handle.sendEmptyMessage(3);
				preferences = context.getSharedPreferences("hqview",
						Context.MODE_PRIVATE);
				if (preferences.getBoolean("iszx", false)) {
					Intent intent = new Intent();
					intent.setAction("行情注册完成");
					intent.putExtra("zx", "ok");
					context.sendBroadcast(intent);
				}
				// init();
			}

		}
		super.onResume();
	}

	public void setHqDataBean(List<HqDataBean> listList) {

		if (this.listList != null && this.listList.size() > 0) {
			this.listList.clear();
		}
		this.listList.addAll(listList);
		adapter.notifyDataSetChanged();
		if (this.listList != null && this.listList.size() > 0) {
			image_zx_show.setVisibility(View.GONE);
			linear_zx_show.setVisibility(View.VISIBLE);
		} else {
			image_zx_show.setVisibility(View.VISIBLE);
			linear_zx_show.setVisibility(View.GONE);
		}
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		if (l == 0) {
			leftOk.setVisibility(View.GONE);
			leftNo.setVisibility(View.VISIBLE);
		} else {
			leftOk.setVisibility(View.VISIBLE);
			leftNo.setVisibility(View.GONE);
		}
		if (l > 1200) {
			rightOk.setVisibility(View.GONE);
			rightNo.setVisibility(View.VISIBLE);
		} else {
			rightOk.setVisibility(View.VISIBLE);
			rightNo.setVisibility(View.GONE);
		}
		for (CHScrollView1 scrollView : mHScrollViews) {
			// 防止重复滑动
			if (mTouchView != scrollView) {
				scrollView.smoothScrollTo(l, t);
			}

		}

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (!NetworkCenter.checkNetworkConnection(context)) {
			Toast.makeText(context, "网络异常", Toast.LENGTH_LONG).show();
		}
	}

	public void clearList() {
		if (mListView != null) {
			listList.clear();
			adapter.notifyDataSetChanged();
		}
		image_zx_show.setVisibility(View.VISIBLE);
		linear_zx_show.setVisibility(View.GONE);
	}

	public void setNewData(HqDataBean newData) {
		this.newData = newData;
		isRunTimer = true; 
		msg = handle.obtainMessage();
		msg.what = 0;
		handle.sendMessage(msg);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = activity;
	}
}
