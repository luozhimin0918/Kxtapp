package com.jyh.kxt.adapter;

import java.util.List;

import org.json.JSONArray;

import com.jyh.kxt.R;
import com.jyh.tool.ImageLoaderUtil;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

/**
 * 广告轮播adapter
 *
 * @author dong
 * @data 2015年3月8日下午3:46:35
 * @contance dong854163@163.com
 */
public class AdvertisementAdapter extends PagerAdapter {

	private Context context;
	private List<View> views;
	JSONArray advertiseArray;

	public AdvertisementAdapter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AdvertisementAdapter(Context context, List<View> views,
			JSONArray advertiseArray) {
		this.context = context;
		this.views = views;
		this.advertiseArray = advertiseArray;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views.get(position), 0);
		View view = views.get(position);
		try {
			String head_img = advertiseArray.optJSONObject(position).optString(
					"head_img");
			ImageView ivAdvertise = (ImageView) view
					.findViewById(R.id.ivAdvertise);
			ImageLoaderUtil.getImage(context, ivAdvertise, head_img,
					R.drawable.ic_launcher, R.drawable.ic_launcher, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}
}
