package com.jyh.kxt.adapter;

import java.util.List;

import com.jyh.bean.DataBean;
import com.jyh.kxt.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DataFLAdpter extends BaseAdapter {
	private Context context;
	private List<DataBean> list;
	public DataFLAdpter(Context context, List<DataBean> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View contenter, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder=new ViewHolder();
		if (contenter == null) {
			contenter = LayoutInflater.from(context).inflate(
					R.layout.data_fl_item, null, false);
			holder.data_fl_tv=(TextView) contenter.findViewById(R.id.data_fl_tv_item);
			contenter.setTag(holder);
		}else {
			holder=(ViewHolder) contenter.getTag();
		}
		holder.data_fl_tv.setText(list.get(position).getName());
		holder.data_fl_tv.setTag(list.get(position).getUrl());
		return contenter;
	}

	class ViewHolder {
		private TextView data_fl_tv;
	}

}
