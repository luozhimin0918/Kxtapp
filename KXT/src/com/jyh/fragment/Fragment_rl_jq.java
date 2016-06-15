package com.jyh.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jyh.gson.bean.CjrlDataStr;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.CjrlJqAdpater;

/**
 * 财经日历 假期
 * 
 * @author Administrator
 *
 */
public class Fragment_rl_jq extends Fragment  {
	private Context context;

	public Fragment_rl_jq() {
	}

	public static Fragment_rl_jq fragment = new Fragment_rl_jq();

	public static Fragment_rl_jq getInstance() {
		return fragment;
	}

	private View view;
	private List<CjrlDataStr> datas;
	private ListView listView;
	private boolean flag = false;
	private CjrlJqAdpater adpater;
	private View headView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.fragment_rl_jq, null);
		headView = View.inflate(context, R.layout.view_cjrl_headview, null);
		findViews();
		return view;

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		flag = false;
	}

	/**
	 * 查找控件
	 */
	public void findViews() {
		listView = (ListView) view.findViewById(R.id.listview_rl_jq);
		TextView text = (TextView) headView.findViewById(R.id.text_head);
		text.setText("假期");
	}

	public void setDatas(List<CjrlDataStr> datas) {
		if (null != this.datas) {
			this.datas = null;
		}
		this.datas = datas;
		initDagas();

	}

	/**
	 * 初始化数据
	 */
	public void initDagas() {
		adpater = new CjrlJqAdpater(datas, context);
		if (!flag) {
			if (listView.getHeaderViewsCount() < 1)
				listView.addHeaderView(headView);
			flag = true;
		}
		listView.setAdapter(adpater);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}
}
