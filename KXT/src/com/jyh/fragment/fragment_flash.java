package com.jyh.fragment;

import hprose.client.HproseHttpClient;
import hprose.common.HproseCallback1;
import hprose.common.HproseErrorEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jyh.fragment.fragment_dp.DPTask;
import com.jyh.gson.bean.Flash_CjInfo;
import com.jyh.gson.bean.Flash_ZxImgInfo;
import com.jyh.gson.bean.ZxInfo;
import com.jyh.kxt.FlashActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.LiveAdapter;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.tool.DateTimeUtil;

public class fragment_flash extends Fragment {
	private SharedPreferences preferences;
	private boolean Isyj = false;
	private View view;
	protected WeakReference<View> mRootView;
	private RelativeLayout relative_falsh_show, main_rl_listview_to_top_arrow,
			relatview_reload;
	private PullToRefreshListView refreshable_view;
	private HashMap<String, Object> hashmap = new HashMap<String, Object>();// 发给服务器的条件
	// private String DATA_URL = "http://192.168.4.9/Server/Choose";// 数据接口
	private String DATA_URL = "http://120.27.198.149/Server/Choose";// 数据接口
	private List<Object> datas = new ArrayList<Object>();// 快讯所有数据
	private List<Long> times = new ArrayList<Long>();// 快讯时间数据用于数据排序
	private List<String> IDList = new ArrayList<String>();// 快讯的id
															// list用于数据删除数据替换
	private String LAST_DATA = "";
	private Gson gson;
	private long TODAY_TIME = 0;// 今天时间
	private long LAST_TIME = 0;// 昨天时间
	private LiveAdapter liveAdapter;
	private KXTApplication application;
	private MyFlashBroadCast myBroadCast;
	private ZxInfo zx_brod;// 资讯bean
	private Flash_CjInfo cj_brod;// 财经bean
	private Flash_ZxImgInfo img_brod;// 快讯图文bean
	private static int z = 0;
	private Activity content;
	private static boolean IsStop = false;// 快讯界面是否处于stop状态

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		application = (KXTApplication) getActivity().getApplication();
		preferences = getActivity().getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			getActivity().setTheme(R.style.BrowserThemeNight);
		} else {
			getActivity().setTheme(R.style.BrowserThemeDefault);
		}
		if (mRootView == null || mRootView.get() == null) {
			view = inflater.inflate(R.layout.flash_frag_zb, null);
			if (!NetworkCenter.checkNetworkConnection(content)) {
				InitFind();
				showReload();

			} else {
				InitFind();
				showProgress();
				GetData();
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

	/**
	 * hprose获取历史数据
	 */
	private void GetData() {
		// TODO Auto-generated method stub
		HproseHttpClient client = new HproseHttpClient(DATA_URL);
		client.invoke("getMessage", new Object[] { hashmap },
				new HproseCallback1<String>() {
					@Override
					public void handler(final String s) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (null != datas) {
									datas.clear();
								}
								if (null != times) {
									times.clear();
								}
								if (null != IDList) {
									IDList.clear();
								}
								try {
									JSONObject jsonObject = new JSONObject(s);
									LAST_DATA = jsonObject
											.getString("lasttime");
									JSONArray jsonArray = new JSONObject(s)
											.getJSONArray("list");
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject object = (JSONObject) jsonArray
												.get(i);
										if (null != object.getString("title")
												|| null != object
														.getString("content")) {
											if (object.getInt("time") < LAST_TIME) {
												datas.add(DateTimeUtil
														.parseMillis(object
																.getInt("time")
																+ ""));
												times.add(LAST_TIME);
												IDList.add("zzzzzzzzz");
												for (int w = 0; w < 100; w++) {
													LAST_TIME = getTimeOf12(-w)
															.getTime() / 1000;
													if (object.getInt("time") > LAST_TIME) {
														LAST_TIME = getTimeOf12(
																-w).getTime() / 1000;
														break;
													}
												}
											}
											if (IDList.size() < 20) {
												IDList.add(object
														.getString("id"));
											}
											if (times.size() < 20) {
												times.add((long) object
														.getInt("time"));
											}
											if (object.get("type").equals("1")) {
												ZxInfo zxInfo = gson.fromJson(
														object.toString(),
														ZxInfo.class);
												datas.add(zxInfo);
											} else if (object.get("type")
													.equals("2")) {
												Flash_CjInfo flash_CjInfo = gson.fromJson(
														object.toString(),
														Flash_CjInfo.class);
												datas.add(flash_CjInfo);
											} else if (object.get("type")
													.equals("1001")) {
												Flash_ZxImgInfo flash_ZxImgInfo = gson.fromJson(
														object.toString(),
														Flash_ZxImgInfo.class);
												datas.add(flash_ZxImgInfo);
											}
										}
										z = 0;
									}
									handler.sendEmptyMessage(1);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									showReload();
								}
							}
						});
					}

				}, new HproseErrorEvent() {

					@Override
					public void handler(String arg0, Throwable arg1) {
						// TODO Auto-generated method stub
						z++;
						if (z < 10) {
							GetData();
						} else {
							handler.sendEmptyMessage(4);
						}
					}
				});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (IsStop) {
			if (hashmap.get("lasttime") != null) {
				hashmap.remove("lasttime");
			}
			GetData();
		}
		IsStop = false;
	}

	/**
	 * 显示显示数据view，隐藏其他
	 */
	public void showDataView() {
		relative_falsh_show.setVisibility(View.GONE);
		refreshable_view.setVisibility(View.VISIBLE);
		relatview_reload.setVisibility(View.GONE);
	}

	/**
	 * 显示progerss，隐藏其他
	 */
	public void showProgress() {
		relative_falsh_show.setVisibility(View.VISIBLE);
		refreshable_view.setVisibility(View.GONE);
		relatview_reload.setVisibility(View.GONE);
	}

	/**
	 * 显示重新加载，隐藏其他
	 */
	public void showReload() {
		relative_falsh_show.setVisibility(View.GONE);
		refreshable_view.setVisibility(View.GONE);
		relatview_reload.setVisibility(View.VISIBLE);

	}

	private void InitFind() {
		if (myBroadCast == null) {
			myBroadCast = new MyFlashBroadCast();
			IntentFilter filter = new IntentFilter();
			filter.addAction("newflashData");
			content.registerReceiver(myBroadCast, filter);
		}
		relative_falsh_show = (RelativeLayout) view
				.findViewById(R.id.relative_falsh_show);
		relatview_reload = (RelativeLayout) view
				.findViewById(R.id.relatview_reload);

		relatview_reload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showProgress();
				z = 0;
				GetData();
			}
		});
		main_rl_listview_to_top_arrow = (RelativeLayout) view
				.findViewById(R.id.main_rl_listview_to_top_arrow);
		refreshable_view = (PullToRefreshListView) view
				.findViewById(R.id.refreshable_view);

		gson = new Gson();
		hashmap.put("app", "kxt");
		hashmap.put("device", "ios");
		hashmap.put("version", 0);
		TODAY_TIME = getTimeOf12(1).getTime() / 1000;
		LAST_TIME = getTimeOf12(0).getTime() / 1000;

		refreshable_view.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (firstVisibleItem > 5) {
					main_rl_listview_to_top_arrow.setVisibility(View.VISIBLE);
				} else if (firstVisibleItem < 5) {
					main_rl_listview_to_top_arrow.setVisibility(View.GONE);
				}
			}
		});
		main_rl_listview_to_top_arrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				refreshable_view.getRefreshableView().setSelection(0);
			}
		});
		refreshable_view
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub

						if (hashmap.get("lasttime") != null) {
							hashmap.remove("lasttime");
						}
						handler.sendEmptyMessageDelayed(5, 5 * 1000);
						GetData();
					}
				});
		refreshable_view
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub
						hashmap.put("lasttime", LAST_DATA);
						LoadMore();
					}
				});
		refreshable_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (datas.get(position - 1) instanceof ZxInfo) {
					ZxInfo info = (ZxInfo) datas.get(position - 1);
					Intent intent = new Intent(getActivity(),
							FlashActivity.class);
					intent.putExtra("id", info.getId());
					intent.putExtra("enterpage", "flash");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} else if (datas.get(position - 1) instanceof Flash_CjInfo) {
					Flash_CjInfo info = (Flash_CjInfo) datas.get(position - 1);
					Intent intent = new Intent(getActivity(),
							FlashActivity.class);
					intent.putExtra("id", info.getId());
					intent.putExtra("enterpage", "flash");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);

				} else if (datas.get(position - 1) instanceof Flash_ZxImgInfo) {
				} else if (datas.get(position - 1) instanceof String) {
				} else {
				}
			}
		});
	}

	/**
	 * 加载更多
	 */
	protected void LoadMore() {
		// TODO Auto-generated method stub
		HproseHttpClient client = new HproseHttpClient(DATA_URL);
		client.invoke("getMessage", new Object[] { hashmap },
				new HproseCallback1<String>() {
					@Override
					public void handler(final String s) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// textview.setText(s);
								try {
									JSONObject jsonObject = new JSONObject(s);
									LAST_DATA = jsonObject
											.getString("lasttime");
									JSONArray jsonArray = new JSONObject(s)
											.getJSONArray("list");
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject object = (JSONObject) jsonArray
												.get(i);
										if (null != object.getString("title")
												|| null != object
														.getString("content")) {
											if (object.getInt("time") < LAST_TIME) {
												datas.add(DateTimeUtil
														.parseMillis(object
																.getInt("time")
																+ ""));
												times.add(LAST_TIME);
												for (int w = 0; w < 100; w++) {
													LAST_TIME = getTimeOf12(-w)
															.getTime() / 1000;
													if (object.getInt("time") > LAST_TIME) {
														LAST_TIME = getTimeOf12(
																-w).getTime() / 1000;
														break;
													}
												}
											}
											times.add((long) object
													.getInt("time"));
											if (object.get("type").equals("1")) {
												ZxInfo zxInfo = gson.fromJson(
														object.toString(),
														ZxInfo.class);
												datas.add(zxInfo);
											} else if (object.get("type")
													.equals("2")) {
												Flash_CjInfo flash_CjInfo = gson.fromJson(
														object.toString(),
														Flash_CjInfo.class);
												datas.add(flash_CjInfo);
											} else if (object.get("type")
													.equals("1001")) {
												Flash_ZxImgInfo flash_ZxImgInfo = gson.fromJson(
														object.toString(),
														Flash_ZxImgInfo.class);
												datas.add(flash_ZxImgInfo);
											}
										}
										if (datas.get(0) instanceof String) {
											datas.remove(0);
										}
									}
									handler.sendEmptyMessage(1);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}

				}, new HproseErrorEvent() {

					@Override
					public void handler(String arg0, Throwable arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(2);
					}
				});
	}

	// 获取莫一天的日期
	private Date getTimeOf12(int i) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_MONTH, i);
		return cal.getTime();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:// 初始化listview
				showDataView();
				if (null == liveAdapter) {
					liveAdapter = new LiveAdapter(getActivity(), datas,
							refreshable_view, application);
					refreshable_view.setAdapter(liveAdapter);
					relative_falsh_show.setVisibility(View.GONE);
				} else {
					refreshable_view.requestLayout();
					liveAdapter.notifyDataSetChanged();
					refreshable_view.onRefreshComplete();
				}
				break;
			case 5:// 数据获取异常
				Toast.makeText(content, "数据获取异常", Toast.LENGTH_SHORT).show();
				refreshable_view.onRefreshComplete();
				break;
			case 2:// 数据加载到了一定数量处理
				Toast.makeText(content, "无更多数据", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				if (liveAdapter != null) {
					refreshable_view.requestLayout();
					liveAdapter.notifyDataSetChanged();
				} else {
					relative_falsh_show.setVisibility(View.GONE);
					liveAdapter = new LiveAdapter(getActivity(), datas,
							refreshable_view, application);
					refreshable_view.setAdapter(liveAdapter);
				}
				break;
			case 4:// 数据加载异常处理
				refreshable_view.onRefreshComplete();
				Toast.makeText(content, "数据获取异常", Toast.LENGTH_SHORT).show();
				showReload();
				break;
			case 6:// 获取初始化数据
				GetData();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 广播用于接受服务中传输过来的数据
	 * 
	 * @author PC
	 *
	 */
	public class MyFlashBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("newflashData")) {

				if (intent.getStringExtra("type").equals("zx")) {
					zx_brod = (ZxInfo) intent.getSerializableExtra("zxinfo");
					if (!IDList.contains(zx_brod.getId())) {
						if (zx_brod.getTime() > TODAY_TIME) {
							datas.add(
									0,
									DateTimeUtil.FlashparseMillis(TODAY_TIME
											+ ""));
							times.add(0, TODAY_TIME);
							IDList.add(0, "zzzz");
							TODAY_TIME = getTimeOf12(1).getTime() / 1000;
						}
						times.add((long) zx_brod.getTime());
						Collections.sort(times, Collections.reverseOrder());
						IDList.add(times.indexOf((long) zx_brod.getTime()),
								zx_brod.getId());
						datas.add(times.indexOf((long) zx_brod.getTime()),
								zx_brod);
					} else {
						datas.set(IDList.indexOf(zx_brod.getId()), zx_brod);
					}
					handler.sendEmptyMessage(3);
				} else if (intent.getStringExtra("type").equals("img")) {
					img_brod = (Flash_ZxImgInfo) intent
							.getSerializableExtra("flash_ZxImgInfo");
					if (!IDList.contains(img_brod.getId())) {
						if (img_brod.getTime() > TODAY_TIME) {
							datas.add(
									0,
									DateTimeUtil.FlashparseMillis(TODAY_TIME
											+ ""));
							times.add(0, TODAY_TIME);
							IDList.add(0, "zzzz");
							TODAY_TIME = getTimeOf12(1).getTime() / 1000;
						}
						times.add((long) img_brod.getTime());
						Collections.sort(times, Collections.reverseOrder());
						IDList.add(times.indexOf((long) img_brod.getTime()),
								img_brod.getId());
						datas.add(times.indexOf((long) img_brod.getTime()),
								img_brod);
					} else {
						datas.set(IDList.indexOf(img_brod.getId()), img_brod);
					}
					handler.sendEmptyMessage(3);
				} else if (intent.getStringExtra("type").equals("cj")) {
					cj_brod = (Flash_CjInfo) intent
							.getSerializableExtra("flash_CjInfo");
					if (!IDList.contains(cj_brod.getId())) {
						if (cj_brod.getTime() > TODAY_TIME) {
							datas.add(
									0,
									DateTimeUtil.FlashparseMillis(TODAY_TIME
											+ ""));
							times.add(0, TODAY_TIME);
							IDList.add(0, "zzzz");
							TODAY_TIME = getTimeOf12(1).getTime() / 1000;
						}
						times.add((long) cj_brod.getTime());
						Collections.sort(times, Collections.reverseOrder());
						IDList.add(times.indexOf((long) cj_brod.getTime()),
								cj_brod.getId());
						datas.add(times.indexOf((long) cj_brod.getTime()),
								cj_brod);
					} else {
						datas.set(IDList.indexOf(cj_brod.getId()), cj_brod);
					}
					handler.sendEmptyMessage(3);
				} else if (intent.getStringExtra("type").equals("del")) {
					try {
						datas.remove(IDList.indexOf(intent
								.getStringExtra("del")));
						times.remove(IDList.indexOf(intent
								.getStringExtra("del")));
						IDList.remove(intent.getStringExtra("del"));
						handler.sendEmptyMessage(3);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		z = 0;
		IsStop = true;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		content = activity;
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		if (myBroadCast != null) {
			content.unregisterReceiver(myBroadCast);
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		IsStop = false;
	}
}
