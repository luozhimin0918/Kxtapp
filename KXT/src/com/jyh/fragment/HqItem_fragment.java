package com.jyh.fragment;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jyh.bean.HqBeanData;
import com.jyh.bean.HqChildren;
import com.jyh.bean.HqDataBean;
import com.jyh.bean.TopBean;
import com.jyh.kxt.R;
import com.jyh.kxt.ZXActivity;
import com.jyh.kxt.customtool.CHScrollView;
import com.jyh.kxt.customtool.MyListView;
import com.jyh.kxt.customtool.SelfLinearLayout;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;
/**
 * 行情子界面
 * @author PC
 *
 */
public class HqItem_fragment extends LazyFragment {

	public HorizontalScrollView mTouchView;
	private List<CHScrollView> mHScrollViews = new ArrayList<CHScrollView>();// 装入所有的HScrollView
	private ImageView leftOk, rightOk, leftNo, rightNo;
	private MyListView mListView;
	private HqInterface hqInterface;
	private GridView gridview_hq;
	private SelfLinearLayout relative_hq_show;
	private List<HqDataBean> gridList = new ArrayList<HqDataBean>();// gridView数据集合
	private static List<HqDataBean> listList = new ArrayList<HqDataBean>();;// ListView
	List<String> codes = new ArrayList<String>();// 所有数据的codes
	// 数据集合
	List<String> topCodes = new ArrayList<String>();
	private KXTApplication application;
	private HqDataBean newData;// 需要刷新的数据
	private static List<HqDataBean> newDatas = new ArrayList<HqDataBean>();//
	private List<HqDataBean> topNewDatas = new ArrayList<HqDataBean>();
	private static ListAdapter adapter;
	private MyBroadCast myBroadCast;
	private int data = 0;
	private static MyAdpater gridAdpater;
	private View view;
	private boolean isAccept = false;
	private boolean isOne = true;
	private Timer datatimer;
	private Timer listtimer;
	private Context context;
	private static HashMap<String, Integer> hasList = new HashMap<String, Integer>();
	private HashMap<String, Integer> topHasList = new HashMap<String, Integer>();
	private int firstItem;// listview可见第一条
	private int visibitem;// listview可见条数
	private SharedPreferences preferences;
	private boolean Isyj = false;
	private HandlerThread handThread;
	public Handler handle;
	private SharedPreferences preferences1;
	private boolean isPrepared;
	private Timer timer;
	private CHScrollView headerScroll;
	protected WeakReference<View> mRootView;
	private Fragment_hq_zx fragment_zx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		HandlerThread hThread = new HandlerThread("");
		hThread.start();
		handle = new Handler(hThread.getLooper(), new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {

				case 0:// 刷新数据
					handleRefresData();

					break;
				case 1:// 恢复list默认数据
					handleResetListData();

					break;
				case 2:// 恢复grid默认数据
					handleResetGridData();

					break;
				case 3:// 刷新界面
					view.invalidate(); // 刷新界面
					break;
				case 4:// 请求list数据
					handleRequsetListData();

					break;
				case 7:
					// setResumeInfo();
					break;
				case 10:// 请求grid数据
					handleRequsetGridData();

					break;
				case 11:
					if (!isAccept) {
						if (isOne) {
							isOne = false;
						}
					}
					break;
				case 30:

					List<String> codes = new ArrayList<String>();
					try {
						if (listList.size() > 12) {
							for (int i = 0; i < 13; i++) {
								codes.add(listList.get(i).getCode());
							}
							if (null != codes && codes.size() > 0) {
								Intent intent = new Intent();
								intent.putExtra("init", "init");
								intent.putExtra("codes", (Serializable) codes);//
								intent.setAction("行情注册完成");
								context.sendBroadcast(intent);
								isAccept = false;
							}
						} else {
							for (int i = 0; i < listList.size(); i++) {
								codes.add(listList.get(i).getCode());
							}
							if (null != codes && codes.size() > 0) {
								Intent intent = new Intent();
								intent.putExtra("init", "init");
								intent.putExtra("codes", (Serializable) codes);//
								intent.setAction("行情注册完成");
								context.sendBroadcast(intent);
								isAccept = false;
							}
						}
					} catch (Exception e) {
					}
					isAccept = false;
					handle.sendEmptyMessageDelayed(11, 8 * 1000);
					break;
				case 111:
					break;
				case 211:
					try {
						if (!isAccept) {
							Toast.makeText(context, "数据获取失败",
									Toast.LENGTH_SHORT).show();
						}

						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								relative_hq_show.setVisibility(View.GONE);
							}
						});
					} catch (Exception e) {
						// TODO: handle exception
					}
					break;
				}
				return true;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_hq_item, null);
		Bundle bdata = getArguments();
		data = Integer.parseInt(bdata.getString("tagId"));

		preferences1 = context.getSharedPreferences("isHQCenter",
				Context.MODE_PRIVATE);
		preferences = context.getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);

		setUserVisibleHint(true);
		isAccept = false;

		initViews();
		initObject();
		initDefautDatas();
		fristVisableDatas();
		isPrepared = true;
		lazyLoad();
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					Thread.sleep(4 * 1000);
					if (isAccept) {
						handle.sendEmptyMessage(30);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		return view;
	}

	/**
	 * 请求默认显示数据
	 */
	private void fristVisableDatas() {
		headerScroll.smoothScrollTo(0, 0);
		headerScroll.setScrollX(0);
		headerScroll.setScrollY(0);

		if (data == 0) {
			if (null != topCodes && topCodes.size() > 0) {
				topCodes.clear();
			}
			//
			if (null != application.getHqBeanDatas()
					&& application.getHqBeanDatas().size() > 0) {
				List<TopBean> tops = application.getHqBeanDatas().get(0)
						.getTopBeans();
				if (null != tops) {
					for (int i = 0; i < tops.size(); i++) {
						topCodes.add(tops.get(i).getCode());
					}
				}
				datatimer = new Timer();
				datatimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (NetworkCenter.checkNetworkConnection(context)) {
							if (!isAccept) {
								handle.sendEmptyMessage(10);
							}
						} else {
							handle.sendEmptyMessage(11);
						}
					}
				}, 0, 5 * 1000);
			} else {
				application.isAppcet = false;
			}
		}

	}

	/**
	 * 初始化对象
	 */
	private void initObject() {
		gridList.clear();

		adapter = new ListAdapter(context, mHScrollViews, mListView, listList,
				Isyj);

		mListView.setAdapter(adapter);
		mListView.setFlag(false);
		adapter.setBean(listList);
		adapter.notifyDataSetChanged();
		gridAdpater = new MyAdpater(gridList);
		gridview_hq.setAdapter(gridAdpater);
		gridAdpater.notifyDataSetChanged();
	}

	private void ClearData() {
		if (null != listList) {
			listList.clear();
		} else {
			listList = new ArrayList<HqDataBean>();
		}
		if (null != hasList) {
			hasList.clear();
		} else {
			hasList = new HashMap<String, Integer>();
		}
		if (null != topHasList) {
			topHasList.clear();
		} else {
			topHasList = new HashMap<String, Integer>();
		}
		if (null != newDatas) {
			newDatas.clear();
		} else {
			newDatas = new ArrayList<HqDataBean>();
		}
	}

	/**
	 * 初始化默认list中数据
	 */
	public void initDefautDatas() {
		ClearData();
		if (data == 0) {
			if (null != gridList && gridList.size() > 0) {
				gridList.clear();
			}
			if (null != application.getHqBeanDatas()
					&& application.getHqBeanDatas().size() > 0) {
				List<TopBean> tops = application.getHqBeanDatas().get(0)
						.getTopBeans();
				List<HqDataBean> tempTopList = new ArrayList<HqDataBean>();
				for (int i = 0; i < tops.size(); i++) {
					HqDataBean data = new HqDataBean();
					data.setCode(tops.get(i).getCode());
					data.setName(tops.get(i).getName());
					data.setLast("0.00");
					data.setSwing("0.00");
					data.setSwingRange("0.00");
					data.setOpen("0.00");
					data.setHigh("0.00");
					data.setLow("0.00");
					data.setLastClose("0.00");//
					data.setVolume("0.00");
					data.setQuoteTime("00:00:00");
					topHasList.put(tops.get(i).getCode(), i);
					tempTopList.add(data);
				}
				gridList.addAll(tempTopList);
				gridAdpater.setGridbean(gridList);
				gridAdpater.notifyDataSetChanged();
			}

		}

		List<HqBeanData> list = application.getHqBeanDatas();
		if (null != list && list.size() > 0) {
			HqBeanData bean = list.get(data);
			List<HqChildren> childern = bean.getChildren();
			for (int i = 0; i < childern.size(); i++) {
				HqDataBean data = new HqDataBean();
				data.setCode(childern.get(i).getCode());
				data.setName(childern.get(i).getName());
				data.setLast("0.00");
				data.setSwing("0.00");
				data.setSwingRange("0.00");
				data.setOpen("0.00");
				data.setHigh("0.00");
				data.setLow("0.00");
				data.setLastClose("0.00");
				data.setVolume("0.00");
				data.setQuoteTime("00:00:00");
				hasList.put(childern.get(i).getCode(), i);
				listList.add(data);

			}
			adapter.setBean(listList);
			adapter.notifyDataSetChanged();
		}
	}

	public void setResumeInfo() {
		setUserVisibleHint(true);
		isAccept = false;

		// initViews();
		if (null != hqInterface) {
			hqInterface.getHorizontalScrollView(mTouchView);
			hqInterface.onScrollChanged(leftOk, leftNo, rightOk, rightNo,
					mHScrollViews);
		} else {
			hqInterface = (HqInterface) context;
			hqInterface.getHorizontalScrollView(mTouchView);
			hqInterface.onScrollChanged(leftOk, leftNo, rightOk, rightNo,
					mHScrollViews);
		}
		initObject();
		initDefautDatas();
		fristVisableDatas();
		handle.sendEmptyMessage(4);
		isPrepared = true;
		lazyLoad();
	}

	/**
	 * 初始化布局控件
	 */
	private void initViews() {
		application = (KXTApplication) getActivity().getApplication();
		fragment_zx = (Fragment_hq_zx) application.getFragmentZx();
		relative_hq_show = (SelfLinearLayout) view
				.findViewById(R.id.relative_hq_show);

		leftOk = (ImageView) view.findViewById(R.id.iv_left_ok);
		leftNo = (ImageView) view.findViewById(R.id.iv_left_no);
		rightOk = (ImageView) view.findViewById(R.id.iv_right_ok);
		rightNo = (ImageView) view.findViewById(R.id.iv_right_no);
		headerScroll = (CHScrollView) view.findViewById(R.id.item_scroll_title);
		mHScrollViews.add(headerScroll);

		hqInterface = (HqInterface) context;
		hqInterface.getHorizontalScrollView(mTouchView);
		hqInterface.onScrollChanged(leftOk, leftNo, rightOk, rightNo,
				mHScrollViews);

		mListView = (MyListView) view.findViewById(R.id.scroll_list);

		gridview_hq = (GridView) view.findViewById(R.id.gridview_hq);
		if (data == 0) {
			gridview_hq.setVisibility(View.VISIBLE);
		} else {
			gridview_hq.setVisibility(View.GONE);
		}
		try {
			setGridOnClick();
		} catch (Exception e) {
			// TODO: handle exception
		}
		mListView.setOnScrollListener(new OnScrollListener() {
			int cunrt = 0;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// if (mListView.getDy() <= -50 || mListView.getDy() >= -10) {
				// return;
				// }
				switch (scrollState) {
				case SCROLL_STATE_IDLE:// 静止
					if (cunrt != firstItem) {
						cunrt = firstItem;
						relative_hq_show.setVisibility(View.VISIBLE);
						try {
							codes.clear();
							for (int i = firstItem; i <= visibitem - 1; i++) {
								codes.add(listList.get(i).getCode());
							}
							if (null != codes && codes.size() > 0) {
								Intent intent = new Intent();
								intent.putExtra("init", "init");
								intent.putExtra("codes", (Serializable) codes);//
								intent.setAction("行情注册完成");
								context.sendBroadcast(intent);
								isAccept = false;
								handle.sendEmptyMessageDelayed(211, 6 * 1000);
							}
						} catch (Exception e) {
							handle.sendEmptyMessage(211);
						}
					}
					break;
				case SCROLL_STATE_TOUCH_SCROLL:// 滚动
					break;
				case SCROLL_STATE_FLING:// 手指离开了还在滚动
					break;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				firstItem = firstVisibleItem;
				if (visibleItemCount + firstVisibleItem == totalItemCount) {
					visibitem = totalItemCount;
				} else {
					visibitem = visibleItemCount + firstVisibleItem;
				}
			}
		});
	}

	/**
	 * GridView 的适配器
	 * 
	 * @author Administrator
	 *
	 */
	private class MyAdpater extends BaseAdapter {

		private List<HqDataBean> gridbean;

		public MyAdpater(List<HqDataBean> gridList) {
			this.gridbean = gridList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return gridList.size();
		}

		public void setGridbean(List<HqDataBean> gridbean) {
			this.gridbean = null;
			this.gridbean = gridbean;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return gridList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		/**
		 * 改变gridview中最新价
		 * 
		 * @param holder
		 * @param swing
		 */
		public void changeLast(MyHolder holder, float swing, HqDataBean data) {
			try {
				if (swing < 0) {
					holder.text_last.setBackground(context.getResources()
							.getDrawable(R.drawable.hq_item_btn_green_shap));
					holder.text_last.setTextColor(context.getResources()
							.getColor(R.color.white));
					holder.text_last.setText(data.getLast());
				} else if (swing > 0) {
					holder.text_last.setBackground(context.getResources()
							.getDrawable(R.drawable.hq_item_btn_red_shap));
					holder.text_last.setTextColor(context.getResources()
							.getColor(R.color.white));
					holder.text_last.setText(data.getLast());
				}
			} catch (NullPointerException e) {
				// TODO: handle exception
			}

		}

		/**
		 * 改变gridview背景
		 * 
		 * @param data
		 * @param holder
		 * @param swing
		 */
		public void changeBackgroude(final HqDataBean data,
				final MyHolder holder, final float swing) {
			//
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					/**
					 * 数据刷新跳动
					 */
					List<String> flags = data.getFalgs();

					if (flags != null && flags.size() > 0) {
						for (int i = 0; i < flags.size(); i++) {
							changeLast(holder, swing, data);
						}
					}

				}
			});

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			MyHolder holder;
			if (convertView == null) {
				holder = new MyHolder();
				convertView = View.inflate(context,
						R.layout.view_gridview_item, null);
				holder.text_name = (TextView) convertView
						.findViewById(R.id.text_gridview_name);
				holder.text_last = (TextView) convertView
						.findViewById(R.id.text_gridview_last);
				holder.text_swing = (TextView) convertView
						.findViewById(R.id.text_gridview_swing);
				holder.text_swingrange = (TextView) convertView
						.findViewById(R.id.text_gridview_swingrange);

				convertView.setTag(holder);
			} else {
				holder = (MyHolder) convertView.getTag();
			}
			HqDataBean hqDataBean = gridbean.get(position);

			holder.text_name.setText(hqDataBean.getName());
			holder.text_last.setText(hqDataBean.getLast());
			holder.text_swing.setText(hqDataBean.getSwing());
			holder.text_swingrange.setText(hqDataBean.getSwingRange());

			float swing = Float.valueOf(hqDataBean.getSwing());

			if (swing < 0) {
				holder.text_last.setTextColor(context.getResources().getColor(
						R.color.hq_item_text_green));

			} else if (swing > 0) {
				holder.text_last.setTextColor(context.getResources().getColor(
						R.color.hq_item_text_red));
				holder.text_swing.setText("+" + hqDataBean.getSwing());
				holder.text_swingrange
						.setText("+" + hqDataBean.getSwingRange());
			} else {// swing=0
				if (Isyj) {
					holder.text_last.setTextColor(context.getResources()
							.getColor(R.color.zdy_color_neight));
				} else {
					holder.text_last.setTextColor(context.getResources()
							.getColor(R.color.hq_item_text_black));
				}

			}
			holder.text_last.setBackground(null);
			changeBackgroude(hqDataBean, holder, swing);

			return convertView;
		}

		private class MyHolder {

			private TextView text_name;// 名称
			private TextView text_last;// 最新
			private TextView text_swing;// 涨跌
			private TextView text_swingrange;// 涨跌幅

		}

	}

	/**
	 * 接收数据及数据更新的广播
	 * 
	 * @author Administrator
	 *
	 */

	public class MyBroadCast extends BroadcastReceiver {

		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("newData")) {
				try {

					String mess = intent.getStringExtra("mess");
					boolean isZx = intent.getBooleanExtra("isZx", false);
					if (null != mess && !"".equals(mess)) {

						if (mess.equals("top")) {
							List<HqDataBean> datas = (List<HqDataBean>) intent
									.getSerializableExtra("datas");

							if (null != gridList && gridList.size() > 0) {
								gridList.clear();
							}
							if (datas.size() == 3 || datas.size() == 6) {
								if (null != datatimer) {
									datatimer.purge();
									datatimer.cancel();
								}
								gridList.addAll(datas);
								gridAdpater.setGridbean(gridList);
								gridAdpater.notifyDataSetChanged();
								if (null != topNewDatas
										&& topNewDatas.size() > 0) {
									topNewDatas.clear();
								}
								datas = null;
								application.isAppcet = true;
							} else {
								datas = null;
								handle.sendEmptyMessage(10);
							}
						} else if (mess.equals("add")) {// 每次切换添加数据
							isAccept = true;
							if (null != listtimer) {
								listtimer.purge();
								listtimer.cancel();
							}
							if (null != datatimer) {
								datatimer.purge();
								datatimer.cancel();
							}
							List<HqDataBean> datas = (List<HqDataBean>) intent
									.getSerializableExtra("datas");

							if (isZx) {
								fragment_zx.setHqDataBean(datas);
							} else {
								if (null != datas && datas.size() > 0) {
									try {
										int datasIndex = 0;
										for (int i = firstItem; i < visibitem; i++) {
											listList.get(i).setLast(
													datas.get(datasIndex)
															.getLast());
											listList.get(i).setOpen(
													datas.get(datasIndex)
															.getOpen());
											listList.get(i).setLastClose(
													datas.get(datasIndex)
															.getLastClose());
											listList.get(i).setHigh(
													datas.get(datasIndex)
															.getHigh());
											listList.get(i).setLow(
													datas.get(datasIndex)
															.getLow());
											listList.get(i).setSwing(
													datas.get(datasIndex)
															.getSwing());
											listList.get(i).setSwingRange(
													datas.get(datasIndex)
															.getSwingRange());
											listList.get(i).setQuoteTime(
													datas.get(datasIndex)
															.getQuoteTime());
											listList.get(i).setVolume(
													datas.get(datasIndex)
															.getVolume());

											datasIndex++;

										}
										adapter.setBean(listList);
										adapter.notifyDataSetChanged();
										relative_hq_show
												.setVisibility(View.GONE);
										application.getMmp().put("3", "3");
										application.HqIsOk = true;
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
										datas = null;
									}
								}
							}
						} else if (mess.equals("update")) {// 有数据更新过来
							if (null != intent.getSerializableExtra("newData")) {

								newData = (HqDataBean) intent
										.getSerializableExtra("newData");

								newDatas.add(newData);
								handle.sendEmptyMessage(0);
								fragment_zx.setNewData(newData);
							}

						} else if (mess.equals("clear") && isZx) {// 清除自选数据
							fragment_zx.clearList();
						} else if (mess.equals("link")) {
							handle.sendEmptyMessage(10);
						} else if (mess.equals("topUpdate")) {
							if (null != intent.getSerializableExtra("newData")) {
								newData = (HqDataBean) intent
										.getSerializableExtra("newData");
								topNewDatas.add(newData);
								handle.sendEmptyMessage(0);
								fragment_zx.setNewData(newData);
							}
						} else if (mess.equals("isConn")) {// 有网络
							handle.sendEmptyMessageDelayed(10, 3000);
						} else if (mess.equals("unConn")) {// 没有网络
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	/**
	 * 提供给MainActivity实现接口 listView滚动效果改变
	 * 
	 * @author Administrator
	 *
	 */
	public interface HqInterface {

		public void getHorizontalScrollView(HorizontalScrollView mTouchView);

		public void onScrollChanged(ImageView leftOk, ImageView leftNo,
				ImageView rightOk, ImageView rightNo,
				List<CHScrollView> mHScrollViews);
	}

	public void setGridOnClick() {
		if (null != gridview_hq) {
			try {
				gridview_hq.invalidate();
				gridview_hq.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context, ZXActivity.class);
						intent.putExtra("code", gridList.get(position)
								.getCode());
						intent.putExtra("title", gridList.get(position)
								.getName());
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);

					}
				});
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	/**
	 * handle中刷新数据
	 */
	public void handleRefresData() {
		try {
			if (data == 0 && gridList != null && gridList.size() > 0) {

				List<String> gflags = new ArrayList<String>();
				if (null != topNewDatas && topNewDatas.size() > 0) {
					for (int j = 0; j < topNewDatas.size(); j++) {
						HqDataBean bean = gridList.get(topHasList
								.get(topNewDatas.get(j).getCode()));
						gflags.add("last");
						gflags.add("swing");
						gflags.add("swingRange");
						bean.setLast(topNewDatas.get(j).getLast());
						bean.setSwing(topNewDatas.get(j).getSwing());
						bean.setSwingRange(topNewDatas.get(j).getSwingRange());
						bean.setOpen(topNewDatas.get(j).getOpen());
						bean.setHigh(topNewDatas.get(j).getHigh());
						bean.setLow(topNewDatas.get(j).getLow());
						bean.setLastClose(topNewDatas.get(j).getLastClose());
						bean.setQuoteTime(topNewDatas.get(j).getQuoteTime());
						bean.setVolume(topNewDatas.get(j).getVolume());
						bean.setFalgs(gflags);
						topNewDatas.remove(j);

					}

					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							gridview_hq.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method
									// stub
									gridAdpater.setGridbean(gridList);
									gridAdpater.notifyDataSetChanged();
									handle.sendEmptyMessageDelayed(2, 250);
								}
							});
						}
					}).start();

				}

			}

			if (null != listList && listList.size() > 0) {
				List<String> flags = new ArrayList<String>();
				if (null != newDatas && newDatas.size() > 0) {
					for (int j = 0; j < newDatas.size(); j++) {
						if (null != hasList && hasList.size() > 0) {
							HqDataBean bean = listList.get(hasList.get(newDatas
									.get(j).getCode()));

							flags.add("last");

							flags.add("swing");

							flags.add("swingRange");

							bean.setLast(newDatas.get(j).getLast());
							bean.setSwing(newDatas.get(j).getSwing());
							bean.setSwingRange(newDatas.get(j).getSwingRange());
							bean.setOpen(newDatas.get(j).getOpen());
							bean.setHigh(newDatas.get(j).getHigh());
							bean.setLow(newDatas.get(j).getLow());
							bean.setLastClose(newDatas.get(j).getLastClose());
							bean.setQuoteTime(newDatas.get(j).getQuoteTime());
							bean.setVolume(newDatas.get(j).getVolume());
							bean.setFalgs(flags);
							newDatas.remove(j);
						}
					}

					new Thread(new Runnable() {

						@Override
						public void run() {

							// TODO Auto-generated method stub
							mListView.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method
									// stub

									if (null == adapter) {

										setResumeInfo();
									}
									adapter.setBean(listList);
									adapter.notifyDataSetChanged();
									handle.sendEmptyMessageDelayed(1, 1000);
								}
							});
						}
					}).start();
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			newDatas.clear();
		}
	}

	/**
	 * handle重置list数据
	 */
	public void handleResetListData() {
		if (listList != null && listList.size() > 0) {
			for (int i = 0; i < listList.size(); i++) {
				HqDataBean bean = listList.get(i);
				bean.setFalgs(null);
			}
			new Thread(new Runnable() {
				public void run() {
					mListView.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							if (null != adapter) {
								adapter.setBean(listList);
								adapter.notifyDataSetChanged();
							}

						}

					});
				}
			}).start();

		}
	}

	/**
	 * handle重置grid数据
	 */
	public void handleResetGridData() {
		for (int i = 0; i < gridList.size(); i++) {
			HqDataBean bean = gridList.get(i);
			bean.setFalgs(null);
		}

		new Thread(new Runnable() {
			public void run() {
				gridview_hq.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						gridAdpater.setGridbean(gridList);
						gridAdpater.notifyDataSetChanged();
					}

				});
			}
		}).start();
	}

	/**
	 * handle请求list数据
	 */
	public void handleRequsetListData() {
		if (!isAccept) {
			handle.sendEmptyMessageDelayed(11, 8 * 1000);
		}
		try {

			if (listList.size() > 12) {
				for (int i = 0; i < 13; i++) {
					codes.add(listList.get(i).getCode());
				}
				if (null != codes && codes.size() > 0) {
					Intent intent = new Intent();
					intent.putExtra("init", "init");
					intent.putExtra("codes", (Serializable) codes);//
					intent.setAction("行情注册完成");
					context.sendBroadcast(intent);
					isAccept = false;
				}
			} else {
				for (int i = 0; i < listList.size(); i++) {
					codes.add(listList.get(i).getCode());
				}
				if (null != codes && codes.size() > 0) {
					Intent intent = new Intent();
					intent.putExtra("init", "init");
					intent.putExtra("codes", (Serializable) codes);//
					intent.setAction("行情注册完成");
					context.sendBroadcast(intent);
					isAccept = false;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * handle请求grid数据
	 */
	public void handleRequsetGridData() {
		if (data == 0) {
			handle.sendEmptyMessageDelayed(11, 8 * 1000);
		}
		// checkService(false);
		try {
			if (null != topCodes && topCodes.size() > 0) {
				Intent intent = new Intent();
				intent.putExtra("isTop", "isTop");
				intent.putExtra("topCodes", (Serializable) topCodes);//
				intent.putExtra("isAdd", true);
				intent.setAction("topAction");
				context.sendBroadcast(intent);
			} else {
			}
			isAccept = false;
		} catch (Exception e) {
			Toast.makeText(application.getApplicationContext(), "获取服务数据异常",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if (null != listList && listList.size() > 0) {
			mListView.setSelection(0);
		}
		isPrepared = true;
		isAccept = false;
		handle.sendEmptyMessage(30);
		setGridOnClick();

		if (data == 0) {
			if (null != listList && listList.size() > 0 && null != gridList
					&& gridList.size() > 0) {
				application.getMmp().put("3", "3");
			}
		} else {
			if (null != listList && listList.size() > 0) {
				application.getMmp().put("3", "3");
			}
		}
		super.onResume();

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (!NetworkCenter.checkNetworkConnection(context)) {
			application.ischange = true;
		} else {
			boolean boo = preferences1.getBoolean("isHqSelect", false);
			if (!boo) {
				application.setIndex(data);
			}
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (listtimer != null) {
			listtimer.cancel();
			listtimer.purge();
		}
		if (null != datatimer) {
			datatimer.cancel();
			datatimer.purge();
		}

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (handThread != null) {
			handThread.quit();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		newDatas.clear();
		adapter = null;
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub

		if (!isPrepared || !isVisible) {
			return;
		}

		if (myBroadCast == null) {
			myBroadCast = new MyBroadCast();
			IntentFilter filter = new IntentFilter();
			filter.addAction("newData");
			application.registerReceiver(myBroadCast, filter);
			application.setRecevier(myBroadCast);
		}
		// 给view控件填充数据
		isPrepared = false;// 加载完数据后 设置为false，不然 界面来回切换时，数据会重复性加载。
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

}
