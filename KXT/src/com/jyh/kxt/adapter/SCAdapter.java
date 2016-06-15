package com.jyh.kxt.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.jyh.bean.SCBean;
import com.jyh.kxt.R;
import com.jyh.kxt.customtool.HXListView;
//import com.jyh.tool.ImageDownLoader;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class SCAdapter extends BaseAdapter implements OnScrollListener {
//	private ImageDownLoader loader;
	// 填充数据的list
	private List<SCBean> scBeans;
	// 用来控制CheckBox的选中状况
	private static HashMap<Integer, Boolean> isSelected;
	// 上下文
	private Context context;
	// 用来导入布局
	private LayoutInflater inflater = null;
	/** 判定是否第一次加载 */
	private boolean isFirstEnter = true;
	/** 第一张可见Item下标 */
	private int firstVisibleItem;
	/** 每屏Item可见数 */
	private int visibleItemCount;
	private HXListView listView;
	private boolean IsShow = false;
	public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径
	// 异步加载图片
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;

	// 构造器
	public SCAdapter(Context context, List<SCBean> scBeans, HXListView listView) {
		this.context = context;
		this.scBeans = scBeans;
		this.listView = listView;
		// loader = new ImageDownLoader(context);
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		// 初始化数据
		initDate();
		initImageLoader();
		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.empty_photo)
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY).build();

	}

	private void initImageLoader() {
		File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
				.getOwnCacheDirectory(context.getApplicationContext(),
						IMAGE_CACHE_PATH);

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new LruMemoryCache(12 * 1024 * 1024))
				.memoryCacheSize(12 * 1024 * 1024)
				.discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
	}

	// 初始化isSelected的数据
	public void initDate() {
		for (int i = 0; i < scBeans.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	@Override
	public int getCount() {
		return scBeans.size();
	}

	@Override
	public Object getItem(int position) {
		return scBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			// 获得ViewHolder对象
			holder = new ViewHolder();
			// 导入布局并赋值给convertview
			convertView = inflater.inflate(R.layout.fragmet_all_scitem, null,
					false);
			holder.addtime = (TextView) convertView
					.findViewById(R.id.tv_frag_jwls_time);
			holder.title = (TextView) convertView
					.findViewById(R.id.tv_frag_jwls_title);
			holder.imageurl = (ImageView) convertView
					.findViewById(R.id.img_frag_jwls);
			holder.title_xia1 = (TextView) convertView
					.findViewById(R.id.frame_item_xia1);
			holder.title_xia2 = (TextView) convertView
					.findViewById(R.id.frame_item_xia2);
			holder.linear_xia1 = (LinearLayout) convertView
					.findViewById(R.id.ll_item_xia1);
			holder.sc_yw_lin = (LinearLayout) convertView
					.findViewById(R.id.sc_yw_lin);
			holder.check_sc = (CheckBox) convertView
					.findViewById(R.id.check_sc);
			convertView.setTag(holder);
		} else {
			// 取出holder
			holder = (ViewHolder) convertView.getTag();
		}
		if (!IsShow) {
			holder.sc_yw_lin.setVisibility(View.GONE);
		} else {
			holder.sc_yw_lin.setVisibility(View.VISIBLE);
		}
		holder.addtime.setText(scBeans.get(position).getAddtime());
		holder.title.setText(scBeans.get(position).getTitle());
		holder.imageurl.setTag(scBeans.get(position).getThumb());
		if (scBeans.get(position).getIsdp().equals("true")) {
			holder.title_xia2.setVisibility(View.GONE);
			holder.title_xia1.setVisibility(View.GONE);
			holder.linear_xia1.setVisibility(View.INVISIBLE);
		} else {
			if (scBeans.get(position).getTag().length == 0
					|| scBeans.get(position).getTag().length == 1) {
				holder.title_xia2.setVisibility(View.GONE);
			} else if (scBeans.get(position).getTag().length == 2) {
				holder.title_xia2.setText(scBeans.get(position).getTag()[1]);
			} else if (scBeans.get(position).getTag().length == 3) {

				// 修改了收藏页面不显示tag问题
				if (scBeans.get(position).getTag()[1].equals("")
						|| scBeans.get(position).getTag()[1] == null) {
					holder.title_xia2
							.setText(scBeans.get(position).getTag()[2]);
					holder.title_xia1
							.setText(scBeans.get(position).getTag()[1]);
				} else {
					holder.title_xia1.setVisibility(View.VISIBLE);
					holder.linear_xia1.setVisibility(View.VISIBLE);
					holder.title_xia2
							.setText(scBeans.get(position).getTag()[1]);
					holder.title_xia1
							.setText(scBeans.get(position).getTag()[2]);
				}
			}
		}
		setImageView(holder.imageurl, scBeans.get(position).getThumb());
		// 根据isSelected来设置checkbox的选中状况
		if (getIsSelected().size() > position) {
			if (null != getIsSelected()) {
				holder.check_sc.setChecked(getIsSelected().get(position));
			}
		} else {
			initDate();
			holder.check_sc.setChecked(getIsSelected().get(position));
		}
		return convertView;
	}

	public List<SCBean> getScBeans() {
		return scBeans;
	}

	public void setScBeans(List<SCBean> scBeans) {
		this.scBeans = scBeans;
		initDate();
	}

	private void setImageView(ImageView imageView, String url) {
		// Bitmap bitmap = loader.getBitmapCache(url);
		// if (bitmap != null) {
		// imageView.setImageBitmap(bitmap);
		// } else {
		// imageView.setImageResource(R.drawable.empty_photo);
		// }
		try {
			mImageLoader.displayImage(url, imageView, options);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 当停止滚动时，加载图片
		if (scrollState == SCROLL_STATE_IDLE) {
			loadImage(firstVisibleItem, visibleItemCount);
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
		try {
			for (int i = firstVisibleItem; i < firstVisibleItem
					+ visibleItemCount; i++) {
				String url = scBeans.get(i).getThumb();
				final ImageView imageView = (ImageView) listView
						.findViewWithTag(url);
					mImageLoader.displayImage(url, imageView, options);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		SCAdapter.isSelected = isSelected;
	}

	public boolean isIsShow() {
		return IsShow;
	}

	public void setIsShow(boolean isShow) {
		IsShow = isShow;
	}

	public static class ViewHolder {
		public TextView title;
		public TextView addtime;
		public ImageView imageurl;
		public TextView title_xia1;
		public TextView title_xia2;
		public LinearLayout linear_xia1;
		public LinearLayout sc_yw_lin;
		public CheckBox check_sc;
	}
}