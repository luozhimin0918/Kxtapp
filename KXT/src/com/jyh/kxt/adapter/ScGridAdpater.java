package com.jyh.kxt.adapter;

import java.util.List;

import com.jyh.bean.ScPlayBean;
import com.jyh.kxt.R;
import com.jyh.tool.ImageLoaderUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 收藏视频的适配类
 * 
 * @author Administrator
 *
 */
public class ScGridAdpater extends BaseAdapter {

	private List<ScPlayBean> scplays;
	private Context context;
	private int type = 0;// 0表示默认显示图片，1表示删除图片,2全选

	public ScGridAdpater(Context context, List<ScPlayBean> scplays) {
		this.context = context;
		this.scplays = scplays;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return scplays.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return scplays.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MyHolder mHolder;
		if (convertView == null) {
			mHolder = new MyHolder();
			convertView = View.inflate(context, R.layout.sc_grid_item, null);
			mHolder.sc_grid_check = (CheckBox) convertView
					.findViewById(R.id.sc_grid_check);
			mHolder.sc_grid_delete = (CheckBox) convertView
					.findViewById(R.id.sc_grid_delete);
			mHolder.sc_grid_item_img = (ImageView) convertView
					.findViewById(R.id.sc_grid_item_img);
			mHolder.sc_pl_title = (TextView) convertView
					.findViewById(R.id.sc_pl_title);
			mHolder.sc_pl_rq = (TextView) convertView
					.findViewById(R.id.sc_pl_rq);
			convertView.setTag(mHolder);
		} else {
			mHolder = (MyHolder) convertView.getTag();
		}
		if (type == 0) {
			mHolder.sc_grid_check.setVisibility(View.VISIBLE);
			mHolder.sc_grid_delete.setVisibility(View.GONE);
			mHolder.sc_grid_delete.setChecked(false);
		} else if (type == 1) {
			mHolder.sc_grid_check.setVisibility(View.GONE);
			mHolder.sc_grid_delete.setVisibility(View.VISIBLE);
		} else if (type == 2) {
			mHolder.sc_grid_delete.setChecked(true);
		} else if (type == 3) {
			mHolder.sc_grid_delete.setChecked(false);
		}
		mHolder.sc_pl_title.setText(scplays.get(position).getTitle());
		mHolder.sc_pl_rq.setText("人气 ： "
				+ scplays.get(position).getPlay_count());
		ImageLoaderUtil.getImage(context, mHolder.sc_grid_item_img, scplays
				.get(position).getImagurl(), R.drawable.empty_photo,
				R.drawable.empty_photo);
		return convertView;
	}

	public class MyHolder {
		public CheckBox sc_grid_check;
		public CheckBox sc_grid_delete;
		public ImageView sc_grid_item_img;
		public TextView sc_pl_title;
		public TextView sc_pl_rq;
	}

}
