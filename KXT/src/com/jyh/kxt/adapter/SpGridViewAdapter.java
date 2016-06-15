package com.jyh.kxt.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jyh.bean.VedioBean;
import com.jyh.kxt.R;
import com.jyh.tool.DateTimeUtil;
import com.jyh.tool.ImageLoaderUtil;

/**
 * @author beginner
 * @date 创建时间：2015年10月14日 下午3:36:47
 * @version 1.0
 */
public class SpGridViewAdapter extends BaseAdapter {

	private List<VedioBean> data;
	private Context context;

	public SpGridViewAdapter() {
		// TODO Auto-generated constructor stub
	}

	public SpGridViewAdapter(List<VedioBean> data, Context context) {
		super();
		this.data = data;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_sp_gridview, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
			holder.img1 = (ImageView) convertView.findViewById(R.id.sp_img);
			holder.timelong1 = (TextView) convertView
					.findViewById(R.id.sp_timelong);
			holder.title1 = (TextView) convertView.findViewById(R.id.sp_title);
			holder.times1 = (TextView) convertView.findViewById(R.id.sp_times);
			holder.type1 = (TextView) convertView.findViewById(R.id.sp_type);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		VedioBean bean = data.get(position);
		ImageLoaderUtil.getImage(context, holder.img1, bean.getPicture(),
				R.drawable.empty_photo, R.drawable.empty_photo);
		if (null != bean.getPublish_time()
				&& !"".equals(bean.getPublish_time())) {
			holder.timelong1.setText("  "
					+ DateTimeUtil.parseMillis(bean.getPublish_time()));
			holder.timelong1.setVisibility(View.VISIBLE);
		} else
			holder.timelong1.setVisibility(View.GONE);
		holder.title1.setText(bean.getTitle());
		holder.times1.setText("  " + bean.getPlay_count());
		holder.type1.setVisibility(View.GONE);
		return convertView;
	}

	class ViewHolder {
		private ImageView img1;
		private TextView title1, timelong1, times1, type1;
	}

	public void setdata(List<VedioBean> datas) {
		this.data = datas;
		notifyDataSetChanged();
	}
}
