package com.jyh.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jyh.bean.VedioBean;
import com.jyh.bean.VedioType;
import com.jyh.bean.VedioTypeTitle;
import com.jyh.fragment.fragment_dp.DPTask;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.SpListViewAdapter;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;

/**
 * 视频上方item界面
 * 
 * @author PC
 *
 */
@SuppressWarnings("rawtypes")
public class fragment_sp_mainItem extends Fragment implements Datachange {
	private View view;
	public static boolean isLoadingMore = true;
	private SharedPreferences preferences;
	private boolean Isyj = false;
	protected WeakReference<View> mRootView;

	private PullToRefreshListView listView;
	private SpListViewAdapter adapter;
	private RelativeLayout relative_falsh_show, relatview_reload;

	private String vedioMainUrl = "http://appapi.kxt.com/Video/main";

	private List lists = new ArrayList();
	private List<VedioTypeTitle> vedioTypes;
	private RequestQueue queue;
	private Context context;

	public fragment_sp_mainItem() {
		super();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = activity;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		preferences = getActivity().getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (getArguments() != null) {
			ArrayList list = (ArrayList) getArguments().getSerializable("type");
			vedioTypes = (List<VedioTypeTitle>) list.get(0);
		}
		if (Isyj) {
			getActivity().setTheme(R.style.BrowserThemeNight);
		} else {
			getActivity().setTheme(R.style.BrowserThemeDefault);
		}

		if (mRootView == null || mRootView.get() == null) {
			view = inflater.inflate(R.layout.fragment_sp_mainitem, container,
					false);
			if (!NetworkCenter.checkNetworkConnection(context)) {
				InitFind();
				showReload();

			} else {
				InitFind();
				showProgress();
				getVedioMainInfo(vedioMainUrl);

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

	private void InitFind() {
		relatview_reload = (RelativeLayout) view
				.findViewById(R.id.relatview_reload);
		listView = (PullToRefreshListView) view
				.findViewById(R.id.sp_listViewId);
		relative_falsh_show = (RelativeLayout) view
				.findViewById(R.id.relative_falsh_show);

		listView.setVisibility(View.GONE);
		listView.setMode(Mode.PULL_FROM_START);
		relatview_reload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showProgress();
				getVedioMainInfo(vedioMainUrl);
			}
		});
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				getVedioMainInfo(vedioMainUrl);
			}
		});

		relative_falsh_show.setVisibility(View.VISIBLE);
		listView.setFadingEdgeLength(0);
		if (null != ((KXTApplication) getActivity().getApplication())
				.getQueue()) {
			queue = ((KXTApplication) getActivity().getApplication())
					.getQueue();
		} else {
			queue = Volley.newRequestQueue(getActivity());
			((KXTApplication) getActivity().getApplication()).setQueue(queue);
		}
	}

	/**
	 * 显示显示数据view，隐藏其他
	 */
	public void showDataView() {
		relative_falsh_show.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		relatview_reload.setVisibility(View.GONE);
	}

	/**
	 * 显示progerss，隐藏其他
	 */
	public void showProgress() {
		relative_falsh_show.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		relatview_reload.setVisibility(View.GONE);
	}

	/**
	 * 显示重新加载，隐藏其他
	 */
	public void showReload() {
		relative_falsh_show.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);
		relatview_reload.setVisibility(View.VISIBLE);

	}

	@Override
	public void dataChange() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	/**
	 * 获取主界面video数据
	 * 
	 * @param url
	 */
	private void getVedioMainInfo(final String url) {
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onResponse(JSONObject response) {
						try {
							JSONObject object = response.getJSONObject("data");
							if (lists != null && lists.size() > 0)
								lists.clear();
							lists.add(JSON.parseArray(
									object.getJSONArray("toutiao").toString(),
									VedioBean.class));
							JSONArray array = object.getJSONArray("category");
							lists.addAll(JSON.parseArray(array.toString(),
									VedioType.class));
							if (lists != null && lists.size() > 0) {
								if (adapter == null)
									adapter = new SpListViewAdapter(lists,
											getActivity(), true, vedioTypes);
								else
									adapter.notifyDataSetChanged();
								listView.setAdapter(adapter);

								showDataView();
							} else {
								showReload();
							}
							if (listView.isRefreshing()) {
								listView.onRefreshComplete();
							}
						} catch (Exception e) {
							showReload();
							if (listView.isRefreshing()) {
								listView.onRefreshComplete();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// getVedioMainInfo(vedioMainUrl);
						showReload();
						if (listView.isRefreshing()) {
							listView.onRefreshComplete();
						}
					}

				});
		if (null != queue) {
			queue.add(jsObjRequest);
		} else {
			queue = Volley.newRequestQueue(getActivity());
			queue.add(jsObjRequest);
		}
	}
}

interface Datachange {
	public void dataChange();

}
