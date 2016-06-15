package com.jyh.kxt.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jyh.bean.ChannelItem;
import com.jyh.kxt.R;

/** 
 * @author  beginner 
 * @date 创建时间：2015-7-14 上午11:51:00 
 * @version 1.0  
 */
public class GridViewAdapter extends BaseAdapter {
	
	private Context context;
	private List<ChannelItem> datas;
	
	public GridViewAdapter(Context context, List<ChannelItem> datas) {
		super();
		this.context = context;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return Long.parseLong(datas.get(arg0).getId());
	}

	@Override
	public View getView(int position, View contenter, ViewGroup arg2) {
		ViewHolder holder = new ViewHolder();
		if (contenter == null) {
			contenter = LayoutInflater.from(context).inflate(
					R.layout.activity_fl_item, null, false);
			holder.textView = (TextView) contenter
					.findViewById(R.id.textViewId);
			contenter.setTag(holder);
		} else {
			holder = (ViewHolder) contenter.getTag();
		}
		holder.textView.setText(datas.get(position).getName());
		return contenter;
	}
	class ViewHolder{
		TextView textView;
	}

}
