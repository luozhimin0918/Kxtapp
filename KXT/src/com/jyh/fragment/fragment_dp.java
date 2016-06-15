package com.jyh.fragment;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jyh.bean.DPBean;
import com.jyh.kxt.DPWebActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.DPAdapter;
import com.jyh.kxt.socket.DateTimeUtil;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;

public class fragment_dp extends Fragment {
	private ListView ls_flash_dp;
	// private RefreshableView rf_flash_dp;
	private PullToRefreshListView pullToRefreshListView;
	private DPAdapter adapter;
	private View view;
	private RelativeLayout relative_falsh_show, relatview_reload;
	private static int markid = 0;// 最后一条数据的id
	private List<DPBean> dpBeans;// 点评数据
	private int currentsize = 10;// 获取多少条数据
	public static boolean isLoadingMore = true;// 是否正在加载更多
	private SharedPreferences preferences;
	private boolean Isyj = false;
	private ArrayList<String> weblist = new ArrayList<String>();// 存放每个item的web地址（好像被遗弃）
	private boolean isLonding = false;// 是否在加载中
	private boolean isLastLonding = false;// 是否是最后一条
	protected WeakReference<View> mRootView;
	private KXTApplication application;
	private static String LOAD_MORE = "http://appapi.kxt.com/data/dianping?num=10&markid=";// 加载更多获取数据地址
	private static String FIRST_URL = "http://appapi.kxt.com/data/dianping?num=";// 第一次获取数据地址
	private Context context;
	private boolean flag = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		preferences = getActivity().getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			getActivity().setTheme(R.style.BrowserThemeNight);
		} else {
			getActivity().setTheme(R.style.BrowserThemeDefault);
		}
		if (mRootView == null || mRootView.get() == null) {
			view = inflater.inflate(R.layout.item_ac_dp, container, false);

			if (!NetworkCenter.checkNetworkConnection(context)) {
				InitFind();
				showReload();

			} else {
				InitFind();
				showProgress();
				new DPTask().execute();

				// onRefreshListener();
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
	 * 显示显示数据view，隐藏其他
	 */
	public void showDataView() {
		relative_falsh_show.setVisibility(View.GONE);
		pullToRefreshListView.setVisibility(View.VISIBLE);
		relatview_reload.setVisibility(View.GONE);
	}

	/**
	 * 显示progerss，隐藏其他
	 */
	public void showProgress() {
		relative_falsh_show.setVisibility(View.VISIBLE);
		pullToRefreshListView.setVisibility(View.GONE);
		relatview_reload.setVisibility(View.GONE);
	}

	/**
	 * 显示重新加载，隐藏其他
	 */
	public void showReload() {
		relative_falsh_show.setVisibility(View.GONE);
		pullToRefreshListView.setVisibility(View.GONE);
		relatview_reload.setVisibility(View.VISIBLE);

	}

	private void InitFind() {
		android.util.Log.i("zml", "333333333");
		relative_falsh_show = (RelativeLayout) view
				.findViewById(R.id.relative_falsh_show);

		relatview_reload = (RelativeLayout) view
				.findViewById(R.id.relatview_reload);
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.rf_flash_dp);
		ls_flash_dp = pullToRefreshListView.getRefreshableView();
		registerForContextMenu(ls_flash_dp);
		application = (KXTApplication) getActivity().getApplication();
		dpBeans = new ArrayList<DPBean>();
		relatview_reload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showProgress();
				new DPTask().execute();
			}
		});
		ls_flash_dp.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView textView = (TextView) arg1
						.findViewById(R.id.tv_frag_dp_title);
				if (Isyj) {
					textView.setTextColor(Color.parseColor("#666666"));

				} else {
					textView.setTextColor(Color.parseColor("#999999"));

				}
				if (!application.dpList.contains(dpBeans.get(arg2 - 1).getId())) {
					application.dpList.add(dpBeans.get(arg2 - 1).getId());
				}
				Intent intent = new Intent(getActivity(), DPWebActivity.class);
				intent.putExtra("weburi", (Serializable) dpBeans.get(arg2 - 1));
				if (!weblist.isEmpty()) {
					weblist.clear();
				}
				for (int i = 0; i < 10; i++) {
					if ((arg2 + i) < dpBeans.size()) {
						weblist.add(dpBeans.get(arg2 + i).getWeburl());
					}
				}
				intent.putStringArrayListExtra("weblist", weblist);
				getActivity().startActivity(intent);
			}
		});

	}

	private void onRefreshListener() {
		// TODO Auto-generated method stub
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (NetworkCenter.isNetworkConnected(getActivity())) {
							if (!isLonding) {
								new DPTask().execute();
								isLonding = true;
							}
						} else {
							hander.sendEmptyMessage(111);
						}
					}
				});
	}

	/**
	 * 初始化点评界面数据
	 * 
	 * @author PC
	 *
	 */
	class DPTask extends AsyncTask<Void, Void, List<DPBean>> {
		HttpClient client;

		@Override
		protected List<DPBean> doInBackground(Void... arg0) {
			client = new DefaultHttpClient();
			HttpGet get = new HttpGet(FIRST_URL + currentsize);
			try {
				HttpResponse response = client.execute(get);
				if (HttpStatus.SC_OK == response.getStatusLine()
						.getStatusCode()) {
					HttpEntity entity = response.getEntity();
					String data = EntityUtils.toString(entity, "GBK");
					JSONArray array = new JSONObject(data).getJSONArray("data");
					if (null != array && array.length() > 0) {
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = (JSONObject) array.get(i);
							DPBean dpBean = new DPBean();
							dpBean.setAddtime(DateTimeUtil.DateStr(Long
									.parseLong(object.getString("addtime")
											+ "000")));
							dpBean.setDiscription(object
									.getString("description"));
							dpBean.setId(object.getString("id"));
							dpBean.setThumb(object.getString("thumb"));
							dpBean.setTitle(object.getString("title"));
							dpBean.setUrl(object.getString("url"));
							dpBean.setWeburl(object.getString("weburl"));
							dpBean.setCategory_id(object
									.getString("category_id"));
							dpBeans.add(dpBean);
							if (i == array.length() - 1) {
								markid = Integer.parseInt(object
										.getString("id"));
							}
						}
					}

				} else {
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (client != null && client.getConnectionManager() != null) {
					client.getConnectionManager().shutdown();
				}
			}
			return dpBeans;
		}

		@Override
		protected void onPostExecute(List<DPBean> result) {
			super.onPostExecute(result);
			if (null != result && result.size() > 0) {
				showDataView();
				flag = true;
				adapter = new DPAdapter(getActivity(), result, ls_flash_dp,
						hander, Isyj, application);
				ls_flash_dp.setAdapter(adapter);
				if (isLonding) {
					pullToRefreshListView.onRefreshComplete();
					isLonding = false;
				}
			} else {
				showReload();
			}

		}

	}

	/**
	 * 加载更多
	 * 
	 * @author PC
	 *
	 */
	class LoadMore extends AsyncTask<Void, Void, List<DPBean>> {
		HttpClient client;

		@Override
		protected List<DPBean> doInBackground(Void... arg0) {
			client = new DefaultHttpClient();
			HttpGet get = new HttpGet(LOAD_MORE + markid);
			try {
				HttpResponse response = client.execute(get);
				if (HttpStatus.SC_OK == response.getStatusLine()
						.getStatusCode()) {
					HttpEntity entity = response.getEntity();
					String data = EntityUtils.toString(entity, "GBK");
					JSONArray array = new JSONObject(data).getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						DPBean dpBean = new DPBean();
						dpBean.setAddtime(DateTimeUtil.DateStr(Long
								.parseLong(object.getString("addtime") + "000")));
						dpBean.setDiscription(object.getString("description"));
						dpBean.setId(object.getString("id"));
						dpBean.setThumb(object.getString("thumb"));
						dpBean.setTitle(object.getString("title"));
						dpBean.setUrl(object.getString("url"));
						dpBean.setWeburl(object.getString("weburl"));
						dpBean.setCategory_id(object.getString("category_id"));
						dpBeans.add(dpBean);
						if (i == array.length() - 1) {
							markid = Integer.parseInt(object.getString("id"));// 存储最后一条数据的id用于获取更多数据
						}
					}
				}
				return dpBeans;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (client != null && client.getConnectionManager() != null) {
					client.getConnectionManager().shutdown();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<DPBean> result) {
			super.onPostExecute(result);
			if (result != null) {
				adapter.setDeviceList((ArrayList<DPBean>) result);
				isLastLonding = false;
				if (result.size() > 200) {
					isLoadingMore = false;
				}
			}
		}

	}

	Handler hander = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:// 加载更多
				if (NetworkCenter.checkNetwork_JYH(getActivity())) {
					if (!isLastLonding && isLoadingMore) {
						new LoadMore().execute();
						isLastLonding = true;
					} else {
						Toast.makeText(getActivity(), "正在加载中",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					pullToRefreshListView.onRefreshComplete();
					isLonding = false;
				}
			case 111:// 加载完成
				pullToRefreshListView.onRefreshComplete();
				isLonding = false;
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = activity;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}
}
