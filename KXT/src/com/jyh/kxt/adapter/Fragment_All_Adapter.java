package com.jyh.kxt.adapter;

import java.util.ArrayList;
import java.util.List;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jyh.kxt.R;
import com.jyh.kxt.customtool.TagLinearView;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.bean.JwAllBean;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Fragment_All_Adapter extends BaseAdapter {
	private Context context;
	private ArrayList<JwAllBean> JwAllBeans;
	private boolean isYj;
	private KXTApplication application;

	public Fragment_All_Adapter(Context context,
			ArrayList<JwAllBean> JwAllBeans, boolean isYj,
			KXTApplication application) {
		this.context = context;
		this.JwAllBeans = JwAllBeans;
		this.isYj = isYj;
		this.application = application;
	}

	public List<JwAllBean> getJwAllBeans() {
		return JwAllBeans;
	}

	public void setJwAllBeans(ArrayList<JwAllBean> jwAllBeans) {
		JwAllBeans = jwAllBeans;
	}

	@SuppressWarnings("unchecked")
	public void setDeviceList(ArrayList<JwAllBean> list) {
		if (list != null) {
			JwAllBeans = (ArrayList<JwAllBean>) list.clone();
			notifyDataSetChanged();
		}
	}

	public void clearDeviceList() {
		if (JwAllBeans != null) {
			JwAllBeans.clear();
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return JwAllBeans == null ? 0 : JwAllBeans.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return JwAllBeans.get(arg0);
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
					R.layout.frament_all_item, null, false);
			holder.imageView = (SimpleDraweeView) contenter
					.findViewById(R.id.img_frag_jwls);
			holder.titletext = (TextView) contenter
					.findViewById(R.id.tv_frag_jwls_title);
			holder.timetext = (TextView) contenter
					.findViewById(R.id.tv_frag_jwls_time);
			holder.tag_lin = (LinearLayout) contenter.findViewById(R.id.tag_ll);
			contenter.setTag(holder);
		} else {
			holder = (ViewHolder) contenter.getTag();
		}
		holder.timetext.setText(JwAllBeans.get(position).getAddtime());
		holder.titletext.setText(JwAllBeans.get(position).getTitle());
		String url = JwAllBeans.get(position).getThumb();
		if (url.contains(".png") || url.contains(".jpg")
				|| url.contains(".gif")) {
			holder.imageView.setImageURI(Uri.parse(url));
		}
		try {
			if (application.ywList.contains(JwAllBeans.get(position).getId())) {
				if (isYj) {
					holder.titletext.setTextColor(Color.parseColor("#666666"));
					holder.timetext.setTextColor(Color.parseColor("#666666"));
				} else {
					holder.titletext.setTextColor(Color.parseColor("#999999"));
				}
			} else {
				if (isYj) {
					holder.titletext.setTextColor(Color.parseColor("#999999"));
					holder.timetext.setTextColor(Color.parseColor("#666666"));
				} else {
					holder.titletext.setTextColor(Color.parseColor("#000000"));
				}
			}
			if (holder.tag_lin.getChildCount() > 0) {
				holder.tag_lin.removeAllViews();
			}
			if (JwAllBeans.get(position).getTags().length() > 0) {
				for (int i = 0; i < JwAllBeans.get(position).getTags().length(); i++) {
					TagLinearView tagLinearView = new TagLinearView(context);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					tagLinearView.setIn_Text(JwAllBeans.get(position).getTags()
							.getString(i));
					params.leftMargin = 8;
					tagLinearView.setTextSize(10f);
					tagLinearView.setIn_textColor("#1166cc");
					if (isYj) {
						tagLinearView.setIn_textBgColor("#04274c");
					} else {
						tagLinearView.setIn_textBgColor("#ffffff");
					}
					holder.tag_lin.addView(tagLinearView.GetView(), params);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return contenter;
	}

	class ViewHolder {
		SimpleDraweeView imageView;
		TextView timetext;
		TextView titletext;
		LinearLayout tag_lin;
	}

}
