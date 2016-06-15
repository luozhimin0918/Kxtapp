package com.jyh.kxt.adapter;

import java.util.List;

import com.jyh.gson.bean.CjrlDataStr;
import com.jyh.kxt.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CjrlJqAdpater extends BaseAdapter {

	private List<CjrlDataStr> datas;
	private Context context;
	private int type = 1;// 1有数据 0没有数据

	public CjrlJqAdpater(List<CjrlDataStr> datas, Context context) {
		this.context = context;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (null != datas && datas.size() > 0) {
			type = 1;
			return datas.size();
		} else {
			type = 0;
			return 1;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View cView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MyHolder holder;
		if (cView == null) {
			holder = new MyHolder();
			cView = View.inflate(context, R.layout.view_cjrl_sj_item, null);

			holder.linear_data = (LinearLayout) cView
					.findViewById(R.id.linear_sj_data);
			holder.text_nature = (TextView) cView
					.findViewById(R.id.text_sj_nature);
			holder.text_nodata = (TextView) cView
					.findViewById(R.id.text_sj_nodata);
			holder.text_ptime = (TextView) cView
					.findViewById(R.id.text_sj_ptime);
			holder.text_site = (TextView) cView.findViewById(R.id.text_sj_site);
			holder.text_state = (TextView) cView
					.findViewById(R.id.text_sj_state);
			holder.text_tilte = (TextView) cView
					.findViewById(R.id.text_sj_title);

			cView.setTag(holder);
		} else {
			holder = (MyHolder) cView.getTag();
		}
		holder.text_nodata.setText("今日无重要假期预告公布！");

		if (type == 0) {
			holder.linear_data.setVisibility(View.GONE);
			holder.text_nodata.setVisibility(View.VISIBLE);
		} else {
			holder.linear_data.setVisibility(View.VISIBLE);
			holder.text_nodata.setVisibility(View.GONE);

			holder.text_ptime.setText(datas.get(position).getPredictTime());
			holder.text_tilte.setText(datas.get(position).getNFI());
			holder.text_nature.setText(datas.get(position).getNature());
			holder.text_state.setText(datas.get(position).getState());
			holder.text_site.setText(datas.get(position).getSite());
			String nature = datas.get(position).getNature();
			if (nature.equals("低")) {
				holder.text_nature.setBackground(context.getResources()
						.getDrawable(R.drawable.cjrl_nature_samll_shap));
			} else if (nature.equals("中")) {
				holder.text_nature.setBackground(context.getResources()
						.getDrawable(R.drawable.cjrl_nature_mind_shap));
			} else if (nature.equals("高")) {
				holder.text_nature.setBackground(context.getResources()
						.getDrawable(R.drawable.cjrl_nature_hight_shap));
			} else {
				holder.text_nature.setBackground(null);
			}
		}
		return cView;
	}

	private class MyHolder {

		private LinearLayout linear_data;// 显示数据的布局
		private TextView text_nodata;// 提示没有数据
		private TextView text_ptime;// 公布时间
		private TextView text_tilte;// 标题
		private TextView text_nature;// 影响
		private TextView text_state;// 国家
		private TextView text_site;// 地区

	}

}
