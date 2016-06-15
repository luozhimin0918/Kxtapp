package com.jyh.kxt.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jyh.bean.DPBean;
import com.jyh.kxt.R;
import com.jyh.kxt.socket.KXTApplication;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class DPAdapter extends BaseAdapter implements OnScrollListener {
	private Context context;
	private List<DPBean> dpBeans;
	/** 数据源 */
	// private ImageDownLoader loader;
	/** 判定是否第一次加载 */
	private boolean isFirstEnter = true;
	/** 第一张可见Item下标 */
	private int firstVisibleItem;
	/** 每屏Item可见数 */
	private int visibleItemCount;
	private ListView listView;
	private Handler handler;
	// 异步加载图片
	private ImageLoader mImageLoader;
	private boolean isYj;
	private KXTApplication application;

	public DPAdapter(Context context, List<DPBean> lBeans, ListView listView,
			Handler handler, boolean isYj, KXTApplication application) {
		this.context = context;
		this.dpBeans = lBeans;
		this.listView = listView;
		this.listView.setOnScrollListener(this);
		this.handler = handler;
		this.isYj = isYj;
		this.application = application;
		mImageLoader = ImageLoader.getInstance();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return dpBeans.get(arg0);
	}

	@SuppressWarnings("unchecked")
	public void setDeviceList(ArrayList<DPBean> list) {
		if (list != null) {
			dpBeans = (ArrayList<DPBean>) list.clone();
			notifyDataSetChanged();
		}
	}

	public void clearDeviceList() {
		if (dpBeans != null) {
			dpBeans.clear();
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return dpBeans == null ? 0 : dpBeans.size();
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@SuppressWarnings("static-access")
	@Override
	public View getView(int position, View contenter, ViewGroup arg2) {
		ViewHolder holder = new ViewHolder();
		if (contenter == null) {
			contenter = LayoutInflater.from(context).inflate(
					R.layout.flash_frag_dp_item, null, false);
			holder.addtime = (TextView) contenter
					.findViewById(R.id.tv_frag_dp_addtime);
			holder.title = (TextView) contenter
					.findViewById(R.id.tv_frag_dp_title);
			holder.imageurl = (ImageView) contenter
					.findViewById(R.id.img_frag_dp);
			contenter.setTag(holder);
		} else {
			holder = (ViewHolder) contenter.getTag();
		}
		try {
			if (application.dpList.contains(dpBeans.get(position).getId())) {

				if (isYj) {
					holder.title.setTextColor(Color.parseColor("#666666"));
					holder.addtime.setTextColor(Color.parseColor("#666666"));
				} else {
					holder.title.setTextColor(Color.parseColor("#999999"));

				}

			} else {
				if (isYj) {
					holder.title.setTextColor(Color.parseColor("#999999"));
					holder.addtime.setTextColor(Color.parseColor("#666666"));
				} else {
					holder.title.setTextColor(Color.parseColor("#000000"));

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		holder.addtime.setText(dpBeans.get(position).getAddtime());
		holder.title.setText(dpBeans.get(position).getTitle());
		holder.imageurl.setTag(dpBeans.get(position).getThumb());
		setImageView(holder.imageurl, dpBeans.get(position).getThumb());
		return contenter;
	}

	private void setImageView(ImageView imageView, String url) {
		try {
			mImageLoader.displayImage(url, imageView, application.options);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 当停止滚动时，加载图片
		if (scrollState == SCROLL_STATE_IDLE) {
			if (listView != null
					&& view.getLastVisiblePosition() == view.getCount() - 1) {
				handler.sendEmptyMessage(100);
			} else {
				loadImage(firstVisibleItem, visibleItemCount);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		this.visibleItemCount = visibleItemCount;
		if (isFirstEnter && visibleItemCount > 0) {
			loadImage(firstVisibleItem, visibleItemCount);
			isFirstEnter = false;
		}
	}

	/**
	 * 加载图片，若缓存中没有，则根据url下载
	 * 
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	private void loadImage(int firstVisibleItem, int visibleItemCount) {
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
			try {
				String url = dpBeans.get(i).getThumb();
				final ImageView imageView = (ImageView) listView
						.findViewWithTag(url);
				mImageLoader.displayImage(url, imageView, application.options);
			} catch (Exception ex) {
			}
		}

	}

	class ViewHolder {
		private TextView title;
		private TextView addtime;
		private ImageView imageurl;
	}
}
