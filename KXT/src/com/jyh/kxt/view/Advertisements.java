package com.jyh.kxt.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.bean.AdBean;
import com.jyh.kxt.AdWebActivity;
import com.jyh.kxt.DPWebActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.AdvertisementAdapter;
import com.jyh.kxt.customtool.SelfViewPager;
import com.jyh.kxt.customtool.SelfViewPager.OnSingleTouchListener;

/**
 * @author dong
 * @version 2015-3-8下午4:33:39
 */

public class Advertisements implements OnPageChangeListener {

	private SelfViewPager vpAdvertise;
	private Context context;
	private LayoutInflater inflater;
	private boolean fitXY;
	private int timeDratioin;// 多长时间切换一次pager
	private List<AdBean> adBeans;
	List<View> views;
	// 底部小点图片
	private ImageView[] dots;
	
	// 记录当前选中位置
	private int currentIndex;
	Timer timer;
	TimerTask task;
	int count = 0;
	TextView textView;
	private boolean IsYw = false;
	private Handler runHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x01:
				int currentPage = (Integer) msg.obj;
				setCurrentDot(currentPage);
				vpAdvertise.setCurrentItem(currentPage);
				break;
			}
		};
	};
	public Advertisements(FragmentActivity activity, boolean fitXY,
			LayoutInflater adinflater, int timeDratioin, List<AdBean> adList,
			boolean IsYw) {
		// TODO Auto-generated constructor stub
		this.context = activity;
		this.fitXY = fitXY;
		this.inflater = adinflater;
		this.timeDratioin = timeDratioin;
		this.adBeans = adList;
		this.IsYw = IsYw;
	}

	public View initView(final JSONArray advertiseArray) {
		View view = inflater.inflate(R.layout.advertisement_board, null);
		vpAdvertise = (SelfViewPager) view.findViewById(R.id.vpAdvertise);
		vpAdvertise.setOnPageChangeListener(this);
		views = new ArrayList<View>();
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll);// 获取轮播图片的点的parent，用于动态添加要显示的点
		textView = (TextView) view.findViewById(R.id.text);
		for (int i = 0; i < advertiseArray.length(); i++) {
			if (fitXY) {
				views.add(inflater.inflate(R.layout.advertisement_item_fitxy,
						null));
			} else {
				views.add(inflater.inflate(
						R.layout.advertisement_item_fitcenter, null));
			}
			ll.addView(inflater.inflate(R.layout.advertisement_board_dot, null),30,30);
		}
		initDots(view, ll);

		AdvertisementAdapter adapter = new AdvertisementAdapter(context, views,
				advertiseArray);
		vpAdvertise.setOffscreenPageLimit(3);
		vpAdvertise.setAdapter(adapter);
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				int currentPage = count % advertiseArray.length();
				count++;
				Message msg = Message.obtain();
				msg.what = 0x01;
				msg.obj = currentPage;
				runHandler.sendMessage(msg);
			}
		};
		timer.schedule(task, 0, timeDratioin);
		vpAdvertise.setOnSingleTouchListener(new OnSingleTouchListener() {

			@Override
			public void onSingleTouch() {
				// TODO Auto-generated method stub
				if (!IsYw) {
					Intent intent = new Intent(context, AdWebActivity.class);
					intent.putExtra("url", adBeans.get(currentIndex).getUrl());
					intent.putExtra("title", adBeans.get(currentIndex)
							.getTitle());
					intent.putExtra("image", adBeans.get(currentIndex)
							.getImage());
					intent.putExtra("shareurl", adBeans.get(currentIndex)
							.getShareUrl());
					intent.putExtra("adweb", "advert");
					context.startActivity(intent);
				} else {
					Intent intent = new Intent(context, DPWebActivity.class);
					intent.putExtra("url", adBeans.get(currentIndex).getUrl());
					context.startActivity(intent);
				}
			}
		});
		return view;
	}

	private void initDots(View view, LinearLayout ll) {

		dots = new ImageView[views.size()];
		try {

		} catch (Exception e) {
			// TODO: handle exception
		}
		// 循环取得小点图片
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// 都设为灰色
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为黄色，即选中状态
		try {

			textView.setText(adBeans.get(currentIndex).getTitle());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > views.size() - 1
				|| currentIndex == position) {
			return;
		}

		dots[position].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = position;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		count = position;
		setCurrentDot(position);
		try {
			textView.setText(adBeans.get(currentIndex).getTitle());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
