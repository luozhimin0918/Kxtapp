package com.jyh.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jyh.bean.HqDataBean;
import com.jyh.kxt.R;
import com.jyh.kxt.ZXActivity;
import com.jyh.kxt.customtool.CHScrollView1;

public class ZXListAdapter extends BaseAdapter {

	private List<HqDataBean> bean;
	private LayoutInflater inflater;
	private List<CHScrollView1> mHScrollViews;
	private ListView mListView;
	private Context context;

	public ZXListAdapter(Context context, List<CHScrollView1> mHScrollViews,
			ListView mListView, List<HqDataBean> bean) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.mHScrollViews = mHScrollViews;
		this.mListView = mListView;
		this.bean = bean;

	}

	/**
	 * 改变最新价
	 * 
	 * @param holder
	 * @param swing
	 */
	public void changeLast(ViewHolder holder, float swing, HqDataBean data) {
		if (swing < 0) {
			holder.item_data1.setBackground(context.getResources().getDrawable(
					R.drawable.hq_item_btn_green_shap));
			holder.item_data1.setTextColor(context.getResources().getColor(
					R.color.white));
			holder.item_data1.setText(data.getLast());
		} else if (swing > 0) {
			holder.item_data1.setBackground(context.getResources().getDrawable(
					R.drawable.hq_item_btn_red_shap));
			holder.item_data1.setTextColor(context.getResources().getColor(
					R.color.white));
			holder.item_data1.setText(data.getLast());
		}
	}

	/**
	 * 改变涨跌
	 * 
	 * @param holder
	 * @param swing
	 * @param data
	 */
	public void changeSwing(ViewHolder holder, float swing, HqDataBean data) {

		if (swing < 0) {
			holder.item_data2.setText(data.getSwingRange());
			holder.item_data2.setBackground(context.getResources().getDrawable(
					R.drawable.hq_item_btn_green_shap));
			holder.item_data2.setTextColor(context.getResources().getColor(
					R.color.white));
		} else if (swing > 0) {
			holder.item_data2.setText("+" + data.getSwingRange());
			holder.item_data2.setBackground(context.getResources().getDrawable(
					R.drawable.hq_item_btn_red_shap));
			holder.item_data2.setTextColor(context.getResources().getColor(
					R.color.white));
		}
	}

	/**
	 * 改变涨跌幅
	 * 
	 * @param holder
	 * @param swing
	 * @param data
	 */
	public void changeSwingRange(ViewHolder holder, float swing, HqDataBean data) {

		if (swing < 0) {
			holder.item_data3.setText(data.getSwing());
			holder.item_data3.setBackground(context.getResources().getDrawable(
					R.drawable.hq_item_btn_green_shap));
			holder.item_data3.setTextColor(context.getResources().getColor(
					R.color.white));
		} else if (swing > 0) {
			holder.item_data3.setText("+" + data.getSwing());
			holder.item_data3.setBackground(context.getResources().getDrawable(
					R.drawable.hq_item_btn_red_shap));
			holder.item_data3.setTextColor(context.getResources().getColor(
					R.color.white));
		}
	}

	public void updateSingleRow(ListView listView, String code) {

		if (listView != null) {
			int start = listView.getFirstVisiblePosition();
			for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++)
				if (code == ((HqDataBean) listView.getItemAtPosition(i))
						.getCode()) {
					View view = listView.getChildAt(i - start);
					getView(i, view, listView);
					break;
				}
		}
	}

	public void changeBackgroude(final HqDataBean data,
			final ViewHolder holder, final float swing) {
		//
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				/**
				 * 数据刷新跳动
				 */
				List<String> flags = data.getFalgs();

				if (flags != null && flags.size() > 0) {
					for (int i = 0; i < flags.size(); i++) {

						// if (flags.get(i).equals("last")) {// 最新
						changeLast(holder, swing, data);
						// }

						// if (flags.get(i).equals("swingRange")) {// 涨跌幅
						changeSwingRange(holder, swing, data);
						// }
						// if (flags.get(i).equals("swing")) {// 涨跌
						changeSwing(holder, swing, data);
						// }
					}
				}

			}
		});

	}

	public void setBean(List<HqDataBean> bean) {
		this.bean = bean;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bean.size();
	}

	@Override
	public HqDataBean getItem(int position) {
		return bean.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item1, null);

			addHViews((CHScrollView1) convertView
					.findViewById(R.id.item_scroll));
			holder = new ViewHolder();
			holder.item_title = (TextView) convertView
					.findViewById(R.id.item_title);
			holder.item_data1 = (TextView) convertView
					.findViewById(R.id.item_data1);
			holder.item_data2 = (TextView) convertView
					.findViewById(R.id.item_data2);
			holder.item_data3 = (TextView) convertView
					.findViewById(R.id.item_data3);
			holder.item_data4 = (TextView) convertView
					.findViewById(R.id.item_data4);
			holder.item_data5 = (TextView) convertView
					.findViewById(R.id.item_data5);
			holder.item_data6 = (TextView) convertView
					.findViewById(R.id.item_data6);
			holder.item_data7 = (TextView) convertView
					.findViewById(R.id.item_data7);
			holder.item_data8 = (TextView) convertView
					.findViewById(R.id.item_data8);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final HqDataBean data = getItem(position);

		holder.item_title.setText(data.getName());
		holder.item_data1.setText(data.getLast());
		holder.item_data2.setText(data.getSwingRange());
		holder.item_data3.setText(data.getSwing());
		holder.item_data4.setText(data.getOpen());
		holder.item_data5.setText(data.getHigh());
		holder.item_data6.setText(data.getLow());
		holder.item_data7.setText(data.getLastClose());
		holder.item_data8
				.setText(data.getQuoteTime().substring(
						data.getQuoteTime().length() - 8,
						data.getQuoteTime().length()));

		final float swing = Float.valueOf(data.getSwing());

		/**
		 * 控制默认字体颜色
		 */
		if (swing < 0) {
			holder.item_data1.setTextColor(context.getResources().getColor(
					R.color.hq_item_text_green));
			holder.item_data2.setTextColor(context.getResources().getColor(
					R.color.hq_item_text_green));
			holder.item_data3.setTextColor(context.getResources().getColor(
					R.color.hq_item_text_green));
		} else if (swing > 0) {
			holder.item_data1.setTextColor(context.getResources().getColor(
					R.color.hq_item_text_red));
			holder.item_data2.setTextColor(context.getResources().getColor(
					R.color.hq_item_text_red));
			holder.item_data3.setTextColor(context.getResources().getColor(
					R.color.hq_item_text_red));
			holder.item_data2.setText("+" + data.getSwingRange());
			holder.item_data3.setText("+" + data.getSwing());
		} else {// swing=0
			holder.item_data1.setTextColor(context.getResources().getColor(
					R.color.hq_item_text_black));
			holder.item_data2.setTextColor(context.getResources().getColor(
					R.color.hq_item_text_black));
			holder.item_data3.setTextColor(context.getResources().getColor(
					R.color.hq_item_text_black));
		}

		holder.item_title.setOnClickListener(new MyListener(data));
		holder.item_data1.setOnClickListener(new MyListener(data));
		holder.item_data2.setOnClickListener(new MyListener(data));
		holder.item_data3.setOnClickListener(new MyListener(data));
		holder.item_data4.setOnClickListener(new MyListener(data));
		holder.item_data5.setOnClickListener(new MyListener(data));
		holder.item_data6.setOnClickListener(new MyListener(data));
		holder.item_data7.setOnClickListener(new MyListener(data));
		holder.item_data8.setOnClickListener(new MyListener(data));

		holder.item_data1.setBackground(null);
		holder.item_data2.setBackground(null);
		holder.item_data3.setBackground(null);

		changeBackgroude(data, holder, swing);

		return convertView;
	}

	private class ViewHolder {
		private TextView item_title;// 名称
		private TextView item_data1;// 最新
		private TextView item_data2;// 涨跌幅
		private TextView item_data3;// 整跌
		private TextView item_data4;// 开盘价
		private TextView item_data5;// 最高
		private TextView item_data6;// 最低
		private TextView item_data7;// 昨收
		private TextView item_data8;// 时间
	}

	public void addHViews(final CHScrollView1 hScrollView) {
		if (!mHScrollViews.isEmpty()) {
			int size = mHScrollViews.size();
			CHScrollView1 scrollView = mHScrollViews.get(size - 1);
			final int scrollX = scrollView.getScrollX();
			if (scrollX != 0) {
				mListView.post(new Runnable() {
					@Override
					public void run() {
						hScrollView.scrollTo(scrollX, 0);
					}
				});
			}
		}
		mHScrollViews.add(hScrollView);
	}

	private class MyListener implements OnClickListener {
		private HqDataBean data;

		public MyListener(HqDataBean data) {
			this.data = data;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context, ZXActivity.class);
			intent.putExtra("code", data.getCode());
			intent.putExtra("title", data.getName());
			context.startActivity(intent);
		}

	}

}
