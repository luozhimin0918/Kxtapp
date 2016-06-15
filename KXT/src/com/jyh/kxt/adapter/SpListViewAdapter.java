package com.jyh.kxt.adapter;

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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jyh.bean.VedioBean;
import com.jyh.bean.VedioType;
import com.jyh.bean.VedioTypeTitle;
import com.jyh.kxt.PlayerActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.customtool.SelfGridView;
import com.jyh.kxt.customtool.TypeTextView;
import com.jyh.tool.DateTimeUtil;
import com.jyh.tool.ImageDownLoader;
import com.jyh.tool.ImageDownLoader.AsyncImageLoaderListener;
import com.jyh.tool.ImageLoaderUtil;

/**
 * @author beginner
 * @date 创建时间：2015年10月14日 下午3:36:47
 * @version 1.0
 */
public class SpListViewAdapter extends BaseAdapter {

	public List<?> data;
	private Context context;
	private boolean isFirst;// 判断是否为首页
	private boolean isLoading;
	private List<VedioTypeTitle> vedioTypes;

	private Intent intent;

	private String loadMoreUrl = "http://appapi.kxt.com/Video/list_video?cid=%s&num=4&markid=%s";

	public SpListViewAdapter() {
		// TODO Auto-generated constructor stub
	}

	public SpListViewAdapter(List<?> data, Context context, boolean isFirst,
			List<VedioTypeTitle> vedioTypes) {
		super();
		this.data = data;
		this.context = context;
		this.isFirst = isFirst;
		this.vedioTypes = vedioTypes;
		intent = new Intent(context, PlayerActivity.class);
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
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (position == 0 && isFirst)
			return 0;
		else
			return 1;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		ViewHolder2 holder2 = null;

		int type = getItemViewType(position);
		if (convertView == null) {
			// 初始化布局
			if (type == 1) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_sp_list1, null);
				holder = new ViewHolder();
				holder.gridView = (SelfGridView) convertView
						.findViewById(R.id.sp_gridview);

				holder.more = (TextView) convertView.findViewById(R.id.sp_more);
				holder.type = (TypeTextView) convertView
						.findViewById(R.id.sp_main_type);
				convertView.setTag(holder);
			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_sp_list2, null);

				holder2 = new ViewHolder2();

				View view1, view2, view3;
				view1 = convertView.findViewById(R.id.layout1);
				view2 = convertView.findViewById(R.id.layout2);
				view3 = convertView.findViewById(R.id.layout3);

				holder2.img1 = (ImageView) view1.findViewById(R.id.sp_img);
				holder2.img2 = (ImageView) view2.findViewById(R.id.sp_img);
				holder2.img3 = (ImageView) view3.findViewById(R.id.sp_img);

				holder2.title1 = (TextView) view1.findViewById(R.id.sp_title);
				holder2.title2 = (TextView) view2.findViewById(R.id.sp_title);
				holder2.title3 = (TextView) view3.findViewById(R.id.sp_title);

				holder2.timelong1 = (TextView) view1
						.findViewById(R.id.sp_timelong);
				holder2.timelong2 = (TextView) view2
						.findViewById(R.id.sp_timelong);
				holder2.timelong3 = (TextView) view3
						.findViewById(R.id.sp_timelong);

				holder2.times1 = (TextView) view1.findViewById(R.id.sp_times);
				holder2.times2 = (TextView) view2.findViewById(R.id.sp_times);
				holder2.times3 = (TextView) view3.findViewById(R.id.sp_times);

				holder2.type1 = (TextView) view1.findViewById(R.id.sp_type);
				holder2.type2 = (TextView) view2.findViewById(R.id.sp_type);
				holder2.type3 = (TextView) view3.findViewById(R.id.sp_type);

				convertView.setTag(holder2);
			}
		} else {
			// 赋值
			if (type == 1)
				holder = (ViewHolder) convertView.getTag();
			else
				holder2 = (ViewHolder2) convertView.getTag();
		}

		if (type == 1) {
			VedioType vedioType = (VedioType) data.get(position);
			holder.type.setText(vedioType.getCat_name());
			if (holder.gridView != null) {
				List<VedioBean> beans = vedioType.getList();
				final SpGridViewAdapter adapter = new SpGridViewAdapter(beans,
						context);
				holder.gridView.setAdapter(adapter);
				holder.gridView
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int p, long id) {
								// TODO Auto-generated method stub
								intent.putExtra("url",
										((VedioType) data.get(position))
												.getList().get(p).getUrl());
								context.startActivity(intent);
							}
						});
				final TextView tv = holder.more;
				holder.more.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 加载更多

						VedioBean bean = ((VedioType) data.get(position))
								.getList().get(
										((VedioType) data.get(position))
												.getList().size() - 1);

						if (!isLoading) {
							isLoading = true;
							new loadMoreVedioListInfo(position, adapter, tv).execute(String
									.format(loadMoreUrl, bean.getCategory_id(),
											bean.getId()));
						}
					}
				});
			}
		} else {
			List<VedioBean> vedioBeans = (List<VedioBean>) data.get(position);
			holder2.title1.setText(vedioBeans.get(0).getTitle());
			holder2.title2.setText(vedioBeans.get(1).getTitle());
			holder2.title3.setText(vedioBeans.get(2).getTitle());

			holder2.type1.setText(getVedioTypeName(vedioBeans.get(0)
					.getCategory_id()));
			holder2.type2.setText(getVedioTypeName(vedioBeans.get(1)
					.getCategory_id()));
			holder2.type3.setText(getVedioTypeName(vedioBeans.get(2)
					.getCategory_id()));

			// holder2.timelong1.setText("  "+DateTimeUtil.parseMillis(vedioBeans
			// .get(0).getPublish_time()));
			// holder2.timelong2.setText("  "+DateTimeUtil.parseMillis(vedioBeans
			// .get(1).getPublish_time()));
			// holder2.timelong3.setText("  "+DateTimeUtil.parseMillis(vedioBeans
			// .get(2).getPublish_time()));

			if (null != vedioBeans.get(0).getPublish_time()
					&& !"".equals(vedioBeans.get(0).getPublish_time())) {
				holder2.timelong1.setText("  "
						+ DateTimeUtil.parseMillis(vedioBeans.get(0)
								.getPublish_time()));
				holder2.timelong1.setVisibility(View.VISIBLE);
			} else
				holder2.timelong1.setVisibility(View.GONE);
			if (null != vedioBeans.get(1).getPublish_time()
					&& !"".equals(vedioBeans.get(1).getPublish_time())) {
				holder2.timelong2.setText("  "
						+ DateTimeUtil.parseMillis(vedioBeans.get(1)
								.getPublish_time()));
				holder2.timelong2.setVisibility(View.VISIBLE);
			} else
				holder2.timelong2.setVisibility(View.GONE);
			if (null != vedioBeans.get(2).getPublish_time()
					&& !"".equals(vedioBeans.get(2).getPublish_time())) {
				holder2.timelong3.setText("  "
						+ DateTimeUtil.parseMillis(vedioBeans.get(2)
								.getPublish_time()));
				holder2.timelong3.setVisibility(View.VISIBLE);
			} else
				holder2.timelong3.setVisibility(View.GONE);

			holder2.times1.setText("  " + vedioBeans.get(0).getPlay_count());
			holder2.times2.setText("  " + vedioBeans.get(1).getPlay_count());
			holder2.times3.setText("  " + vedioBeans.get(2).getPlay_count());
			final ImageView imag = holder2.img1;
			// ImageLoaderUtil.getImage(context, holder2.img1, vedioBeans.get(0)
			// .getPicture(), R.drawable.empty_photo,
			// R.drawable.empty_photo);
			new ImageDownLoader(context).loadImage(vedioBeans.get(0)
					.getPicture(), new AsyncImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap) {
					// TODO Auto-generated method stub
					if (null != bitmap) {
						imag.setImageBitmap(bitmap);
					}
				}
			});
			ImageLoaderUtil.getImage(context, holder2.img2, vedioBeans.get(1)
					.getPicture(), R.drawable.empty_photo,
					R.drawable.empty_photo);
			ImageLoaderUtil.getImage(context, holder2.img3, vedioBeans.get(2)
					.getPicture(), R.drawable.empty_photo,
					R.drawable.empty_photo);

			setonclick(holder2, position);
		}
		return convertView;
	}

	/**
	 * 设置点击事件
	 * 
	 * @param holder2
	 * @param position
	 */
	@SuppressWarnings("unchecked")
	private void setonclick(ViewHolder2 holder2, final int position) {
		// TODO Auto-generated method stub
		final List<VedioBean> beans = (List<VedioBean>) data.get(position);
		holder2.img1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent.putExtra("url", beans.get(0).getUrl());
				context.startActivity(intent);
			}
		});
		holder2.img2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent.putExtra("url", beans.get(1).getUrl());
				context.startActivity(intent);
			}
		});
		holder2.img3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent.putExtra("url", beans.get(2).getUrl());
				context.startActivity(intent);
			}
		});
	}

	private class ViewHolder {
		private TypeTextView type;
		private SelfGridView gridView;
		private TextView more;
	}

	private class ViewHolder2 {
		private ImageView img1;
		private TextView title1, timelong1, times1, type1;
		private ImageView img2;
		private TextView title2, timelong2, times2, type2;
		private ImageView img3;
		private TextView title3, timelong3, times3, type3;
	}

	class loadMoreVedioListInfo extends
			AsyncTask<String, Void, List<VedioBean>> {

		private int position;
		private SpGridViewAdapter adapter;
		private TextView tv;

		public loadMoreVedioListInfo(int position, SpGridViewAdapter adapter,
				TextView tv) {
			super();
			this.position = position;
			this.adapter = adapter;
			this.tv = tv;
		}

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
					// lastId = beans.get(beans.size() - 1).getId();
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
				if("没有更多数据了".equals(tv.getText().toString())){
					tv.setText("加载更多");
					Drawable drawable=context.getResources().getDrawable(R.drawable.sp_more);
					drawable.setBounds(0, 0, 26, 15);
					tv.setCompoundDrawables(null, null, drawable, null);
				}
				List<VedioBean> datas = ((VedioType) data.get(position))
						.getList();
				datas.addAll(result);
				adapter.setdata(datas);

			} else {
				tv.setText("没有更多数据了");
				tv.setRight(R.drawable.sp_more);
				tv.setClickable(false);
				tv.setCompoundDrawables(null, null, null, null);
			}
			isLoading = false;
		}
	}

	public String getVedioTypeName(String code) {
		int count = vedioTypes.size();
		for (int i = 0; i < count; i++) {
			VedioTypeTitle title = vedioTypes.get(i);
			if (null != code && null != title) {
				if (code.equals(title.getId()))
					return title.getCat_name();
			}

		}
		return null;
	}

	private ImageDownLoader loader;

	@SuppressWarnings("unused")
	private void setImageView(ImageView imageView, String url) {
		Bitmap bitmap = loader.getBitmapCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.empty_photo);
		}
	}
}
