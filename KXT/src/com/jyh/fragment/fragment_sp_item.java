package com.jyh.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.jyh.bean.VedioBean;
import com.jyh.kxt.PlayerActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.SpGridViewAdapter;
import com.jyh.kxt.socket.NetworkCenter;

/**
 * 视讯(非首页)
 * 
 * @author beginner
 * @date 创建时间：2015年11月16日 下午3:44:32
 * @version 1.0
 */
public class fragment_sp_item extends Fragment {

	private View view;

	private PullToRefreshGridView gridView;
	private SpGridViewAdapter adapter;
	private List<VedioBean> datas;
	private RelativeLayout relative_falsh_show;
	private String vedioItemUrlBase = "http://appapi.kxt.com/Video/list_video?cid=%s&num=10";//视频数据地址
	private String vedioItemUrlMore;
	private String lastId;// 最后一条数据的id，用以刷新
	private boolean isLonding;

	private ProgressBar pro_bar;

	private TextView show_text;

	// public fragment_sp_item(String type) {
	// super();
	// vedioItemUrlBase = String.format(vedioItemUrlBase, type);
	// vedioItemUrlMore = vedioItemUrlBase + "&markid=%s";
	// Log.i("info", vedioItemUrlBase);
	// }

	public fragment_sp_item() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_sp_item, null);
		if (getArguments() != null) {
			vedioItemUrlBase = String.format(vedioItemUrlBase, getArguments()
					.getString("type"));
			vedioItemUrlMore = vedioItemUrlBase + "&markid=%s";
		}
		relative_falsh_show = (RelativeLayout) view
				.findViewById(R.id.relative_falsh_show);
		pro_bar = (ProgressBar) view.findViewById(R.id.pro_bar);
		show_text = (TextView) view.findViewById(R.id.show_text);
		datas = new ArrayList<VedioBean>();
		gridView = (PullToRefreshGridView) view
				.findViewById(R.id.sp_item_gridview);
		relative_falsh_show.setVisibility(View.VISIBLE);
		gridView.setVisibility(View.GONE);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), PlayerActivity.class);
				intent.putExtra("url", datas.get(position).getUrl());
				startActivity(intent);
			}
		});
		gridView.setMode(Mode.BOTH);
		gridView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				if (NetworkCenter.isNetworkConnected(getActivity())) {
					if (!isLonding) {
						new getVedioListInfo().execute(vedioItemUrlBase);
						isLonding = true;
					}
				} else {
					gridView.onRefreshComplete();
					isLonding = false;
					Toast.makeText(getActivity(), "当前无网络", Toast.LENGTH_SHORT)
							.show();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				if (NetworkCenter.isNetworkConnected(getActivity())) {
					if (!isLonding) {
						new loadMoreVedioListInfo().execute(String.format(
								vedioItemUrlMore, lastId));
						isLonding = true;
					}

				} else {
					gridView.onRefreshComplete();
					isLonding = false;
					Toast.makeText(getActivity(), "当前无网络", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		new getVedioListInfo().execute(vedioItemUrlBase);
		return view;
	}
	/**
	 * 获取video列表数据
	 * @author PC
	 *
	 */
	class getVedioListInfo extends AsyncTask<String, Void, List<VedioBean>> {

		@Override
		protected List<VedioBean> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<VedioBean> beans = new ArrayList<VedioBean>();
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			HttpGet get = new HttpGet(params[0]);
			try {
				HttpResponse httpResponse = client.execute(get);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					JSONArray array = new JSONObject(
							EntityUtils.toString(httpResponse.getEntity()))
							.getJSONArray("data");
					beans = JSON.parseArray(array.toString(), VedioBean.class);
					lastId = beans.get(beans.size() - 1).getId();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (client != null && client.getConnectionManager() != null) {
					client.getConnectionManager().shutdown();
				}
			}
			return beans;
		}

		@Override
		protected void onPostExecute(List<VedioBean> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null && result.size() > 0) {
				adapter = new SpGridViewAdapter(result, getActivity());
				gridView.setAdapter(adapter);
				datas.clear();
				datas.addAll(result);
				relative_falsh_show.setVisibility(View.GONE);
				gridView.setVisibility(View.VISIBLE);
			} else {
				pro_bar.setVisibility(View.GONE);
				show_text.setText("点击重新刷新");
				relative_falsh_show.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (show_text.getText().equals("点击重新刷新")) {
							new getVedioListInfo().execute(vedioItemUrlBase);
							pro_bar.setVisibility(View.VISIBLE);
							show_text.setText("数据加载中...");
						}
					}
				});
			}
			if (isLonding) {
				gridView.onRefreshComplete();
				isLonding = false;
			}
		}
	}
	/**
	 * 加载更多数据
	 * @author PC
	 *
	 */
	class loadMoreVedioListInfo extends
			AsyncTask<String, Void, List<VedioBean>> {

		@Override
		protected List<VedioBean> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<VedioBean> beans = new ArrayList<VedioBean>();
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			HttpGet get = new HttpGet(params[0]);
			try {
				HttpResponse httpResponse = client.execute(get);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					JSONArray array = new JSONObject(
							EntityUtils.toString(httpResponse.getEntity()))
							.getJSONArray("data");
					beans = JSON.parseArray(array.toString(), VedioBean.class);
					lastId = beans.get(beans.size() - 1).getId();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (client != null && client.getConnectionManager() != null) {
					client.getConnectionManager().shutdown();
				}
			}
			return beans;
		}

		@Override
		protected void onPostExecute(List<VedioBean> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null && result.size() > 0) {
				datas.addAll(result);
				adapter.setdata(datas);
			}
			if (isLonding) {
				gridView.onRefreshComplete();
				isLonding = false;
			}
		}
	}

}
