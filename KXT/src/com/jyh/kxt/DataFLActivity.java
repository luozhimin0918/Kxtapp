package com.jyh.kxt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.DataFLAdpter;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.tool.HttpUtils;
import com.jyh.bean.DataBean;
import com.jyh.bean.VedioTypeTitle;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DataFLActivity extends Activity {
	private ListView data_ls_fl;
	private DataFLAdpter adapter;
	private ArrayList<DataBean> list;
	private ImageView data_img_tag;
	private SharedPreferences preferences;
	private boolean Isyj = false;
	// 手指上下滑动时的最小速度
	private static final int YSPEED_MIN = 1000;

	// 手指向右滑动时的最小距离
	private static final int XDISTANCE_MIN = 50;

	// 手指向上滑或下滑时的最小距离
	private static final int YDISTANCE_MIN = 100;

	// 记录手指按下时的横坐标。
	private float xDown;

	// 记录手指按下时的纵坐标。
	private float yDown;

	// 记录手指移动时的横坐标。
	private float xMove;

	// 记录手指移动时的纵坐标。
	private float yMove;

	// 用于计算手指滑动的速度。
	private VelocityTracker mVelocityTracker;
	private LinearLayout datafl_zt_color;

	private String dataString = "http://appapi.kxt.com/data/datas";
	private boolean isSp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		preferences = this.getSharedPreferences("setup", Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			this.setTheme(R.style.BrowserThemeNight);
		} else {
			this.setTheme(R.style.BrowserThemeDefault);
		}
		// 透明状态栏
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		setContentView(R.layout.activity_data_fl);

		datafl_zt_color = (LinearLayout) findViewById(R.id.datafl_zt_color);
		if (Isyj) {
			datafl_zt_color.setBackgroundColor(Color.parseColor("#04274c"));
		} else {
			datafl_zt_color.setBackgroundColor(Color.parseColor("#1177e0"));
		}

		data_ls_fl = (ListView) findViewById(R.id.data_ls_fl);
		if (null != getIntent() && null != getIntent().getStringExtra("sp")) {
			isSp = true;
			List<VedioTypeTitle> vedioTypeTitles = ((KXTApplication) getApplication())
					.getVedioTypes();
			List<String> result = new ArrayList<String>();
			if (vedioTypeTitles == null) {
				new GetVedioTitles().execute(getIntent().getStringExtra("sp"));
			} else {
				for (VedioTypeTitle vedioTypeTitle : vedioTypeTitles) {
					result.add(vedioTypeTitle.getCat_name());
				}
				MyAdapter adapter = new MyAdapter(result, DataFLActivity.this);
				data_ls_fl.setAdapter(adapter);
			}
		} else {
			isSp = false;
			new DataLoad().execute(dataString);

		}
		data_img_tag = (ImageView) findViewById(R.id.data_img_tag);
		data_ls_fl.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				if (isSp) {
					intent.putExtra("title",
							data_ls_fl.getAdapter().getItem(arg2).toString());
					setResult(102, intent);
				} else {
					intent.putExtra("uri", list.get(arg2).getUrl());
					intent.putExtra("name", list.get(arg2).getName());
					setResult(101, intent);
				}
				finish();

			}
		});
		data_img_tag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	class DataLoad extends AsyncTask<String, Void, List<DataBean>> {

		@Override
		protected List<DataBean> doInBackground(String... arg0) {
			list = new ArrayList<DataBean>();
			DataBean bean;

			String data = HttpUtils.getGetData(arg0[0], "utf-8");
			if (null != data) {
				try {
					JSONArray array = new JSONObject(data).getJSONArray("data");

					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						@SuppressWarnings("rawtypes")
						Iterator iterator = object.keys();
						String key = null;
						String value = null;
						while (iterator.hasNext()) {
							key = (String) iterator.next();
							value = object.getString(key);
							bean = new DataBean();
							bean.setName(key);
							bean.setUrl(value);
							list.add(bean);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return list;
		}

		@Override
		protected void onPostExecute(List<DataBean> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter = new DataFLAdpter(DataFLActivity.this, result);
			data_ls_fl.setAdapter(adapter);
		}
	}

	/*
	 * 获取item title
	 */
	class GetVedioTitles extends AsyncTask<String, Void, List<String>> {

		@Override
		protected List<String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<String> titleString = new ArrayList<String>();
			List<VedioTypeTitle> titles = new ArrayList<VedioTypeTitle>();

			String data = HttpUtils.getGetData(params[0], "utf-8");
			try {
				JSONObject object = new JSONObject(data);
				JSONArray array = object.getJSONArray("data");
				titles = JSON
						.parseArray(array.toString(), VedioTypeTitle.class);
				for (VedioTypeTitle vedioTypeTitle : titles) {
					titleString.add(vedioTypeTitle.getCat_name());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return titleString;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				MyAdapter adapter = new MyAdapter(result, DataFLActivity.this);
				data_ls_fl.setAdapter(adapter);
			}else{
				Toast.makeText(getApplicationContext(), "网络异常，请检查网络", 0).show();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 创建VelocityTracker对象，并将触摸界面的滑动事件加入到VelocityTracker当中。
	 *
	 * @param event
	 *
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	/**
	 * 回收VelocityTracker对象。
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}

	/**
	 *
	 * @return 滑动速度，以每秒钟移动了多少像素值为单位。
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getYVelocity();
		return Math.abs(velocity);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		createVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = event.getRawX();
			yDown = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			xMove = event.getRawX();
			yMove = event.getRawY();
			// 滑动的距离
			int distanceX = (int) (xMove - xDown);
			int distanceY = (int) (yMove - yDown);
			// 获取顺时速度
			int ySpeed = getScrollVelocity();
			// 关闭Activity需满足以下条件：
			// 1.x轴滑动的距离>XDISTANCE_MIN
			// 2.y轴滑动的距离在YDISTANCE_MIN范围内
			// 3.y轴上（即上下滑动的速度）<XSPEED_MIN，如果大于，则认为用户意图是在上下滑动而非左滑结束Activity
			if (distanceX > XDISTANCE_MIN
					&& (distanceY < YDISTANCE_MIN && distanceY > -YDISTANCE_MIN)
					&& ySpeed < YSPEED_MIN) {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
			break;
		case MotionEvent.ACTION_UP:
			recycleVelocityTracker();
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	class MyAdapter extends BaseAdapter {

		private List<String> data;
		private Context context;

		public MyAdapter(List<String> data, Context context) {
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
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.data_fl_item, null);
				holder.tv = (TextView) convertView
						.findViewById(R.id.data_fl_tv_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText(data.get(position));
			return convertView;
		}

		class ViewHolder {
			private TextView tv;
		}
	}
}