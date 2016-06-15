package com.jyh.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.jyh.bean.SCBean;
import com.jyh.kxt.DPWebActivity;
import com.jyh.kxt.NewSCActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.SCAdapter;
import com.jyh.kxt.adapter.SCAdapter.ViewHolder;
import com.jyh.kxt.customtool.HXListView;
import com.jyh.kxt.customtool.HXListView.DelButtonClickListener;
import com.jyh.kxt.sqlte.SCDataSqlte;
import com.jyh.tool.PopupWindowUtils;

/**
 * 收藏文章界面
 * 
 * @author PC
 *
 */
public class ArticFragment extends Fragment implements OnClickListener {
	private View view;
	protected WeakReference<View> mRootView;
	private SharedPreferences preferences;
	private boolean Isyj = false;
	private TextView select_all, delete_all;
	private HXListView listView;
	private SCDataSqlte dataSqlte;
	private SQLiteDatabase db;
	private List<SCBean> scBeans;
	private SCBean bean;
	private SCAdapter all_Adapter;
	private ImageView image_favorite_bg;
	private LinearLayout btm_linear;
	private boolean IsShow = false;// 判断是否是删除状态
	private boolean isSelect = false;// 判断是否被选中
	private Context context;
	private PopupWindowUtils popManager;
	private View cDeleteView;
	private Button btn_cancel;
	private Button btn_ok;
	private String deleteDate = "";
	private String[] Date;
	private Cursor cursor;
	private boolean needRefrash = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		preferences = getActivity().getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			getActivity().setTheme(R.style.BrowserThemeNight);
		} else {
			getActivity().setTheme(R.style.BrowserThemeDefault);
		}
		if (mRootView == null || mRootView.get() == null) {
			view = inflater.inflate(R.layout.activity_sc, null);
			InitFind();
			mRootView = new WeakReference<View>(view);
		} else {
			ViewGroup parent = (ViewGroup) mRootView.get().getParent();
			if (parent != null) {
				parent.removeView(mRootView.get());
			}
		}
		return mRootView.get();
	}

	private void InitFind() {
		// TODO Auto-generated method stub
		btm_linear = (LinearLayout) view.findViewById(R.id.btm_linear);
		listView = (HXListView) view.findViewById(R.id.sc_ls_activity);
		image_favorite_bg = (ImageView) view
				.findViewById(R.id.image_favorite_bg);
		select_all = (TextView) view.findViewById(R.id.select_all);
		delete_all = (TextView) view.findViewById(R.id.delete_all);

		cDeleteView = View.inflate(context, R.layout.pop_counter_delete_view,
				null);
		btn_cancel = (Button) cDeleteView.findViewById(R.id.btn_cancel);
		btn_ok = (Button) cDeleteView.findViewById(R.id.btn_ok);
		btn_cancel.setOnClickListener(this);
		btn_ok.setOnClickListener(this);

		popManager = new PopupWindowUtils();

		InitData();

	}

	private void InitData() {

		dataSqlte = new SCDataSqlte(getActivity());
		scBeans = new ArrayList<SCBean>();
		InitSCbean();
		isShowBackground();
		all_Adapter = new SCAdapter(getActivity(), scBeans, listView);
		// try {
		// Thread.sleep(1 * 1000);
		listView.setAdapter(all_Adapter);
		// } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (IsShow) {
					// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
					ViewHolder holder = (ViewHolder) arg1.getTag();
					// 改变CheckBox的状态
					holder.check_sc.toggle();
					// 将CheckBox的选中状况记录下来
					SCAdapter.getIsSelected().put(arg2,
							holder.check_sc.isChecked());
					// 调整选定条目
					if (holder.check_sc.isChecked() == true) {
						if (!deleteDate.contains(arg2 + "")) {
							deleteDate = deleteDate + arg2 + ",";
						}
					} else {
						if (deleteDate.contains(arg2 + "")) {
							deleteDate = deleteDate.replace(arg2 + ",", "");
						}
					}
					// 用TextView显示
				} else {
					Intent intent = new Intent(getActivity(),
							DPWebActivity.class);
					intent.putExtra("weburi", scBeans.get(arg2).getUrl());
					intent.putExtra("category", scBeans.get(arg2).getCategory());
					intent.putExtra("addtime", scBeans.get(arg2).getAddtime());
					intent.putExtra("id", scBeans.get(arg2).getId());
					intent.putExtra("thumb", scBeans.get(arg2).getThumb());
					intent.putExtra("title", scBeans.get(arg2).getTitle());
					intent.putExtra("weburl", scBeans.get(arg2).getWeburl());
					intent.putExtra("discription", scBeans.get(arg2)
							.getDiscription());
					intent.putExtra("legth", scBeans.get(arg2).getTag().length);
					for (int i = 0; i < scBeans.get(arg2).getTag().length; i++) {
						intent.putExtra("tag" + i,
								scBeans.get(arg2).getTag()[i]);
					}
					intent.putExtra("SCActivity", "yes");
					needRefrash = true;
					startActivityForResult(intent, 188);
				}
			}
		});
		listView.setDelButtonClickListener(new DelButtonClickListener() {

			@Override
			public void clickHappend(int position) {
				db = dataSqlte.getWritableDatabase();
				db.delete("data", "id=? and category=?", new String[] {
						scBeans.get(position).getId(),
						scBeans.get(position).getCategory() });
				handler.sendEmptyMessage(101);
			}
		});
		select_all.setOnClickListener(this);
		delete_all.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (needRefrash) {
			scBeans.clear();
			InitSCbean();
			all_Adapter.notifyDataSetChanged();
			needRefrash = false;
		}
	}

	/**
	 * 初始化数据源
	 */
	private void InitSCbean() {
		db = dataSqlte.getReadableDatabase();
		if (db == null) {
			return;
		}
		cursor = db.query("data", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			bean = new SCBean();
			bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			bean.setAddtime(cursor.getString(cursor.getColumnIndex("addtime")));
			bean.setCategory(cursor.getString(cursor.getColumnIndex("category")));
			bean.setId(cursor.getString(cursor.getColumnIndex("id")));
			bean.setIsdp(cursor.getString(cursor.getColumnIndex("isdp")));
			bean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			bean.setThumb(cursor.getString(cursor.getColumnIndex("thumb")));
			bean.setWeburl(cursor.getString(cursor.getColumnIndex("weburl")));
			bean.setDiscription(cursor.getString(cursor
					.getColumnIndex("discription")));
			bean.setTag(cursor.getString(cursor.getColumnIndex("tag")).split(
					","));
			scBeans.add(bean);
		}
		cursor.close();
		db.close();
	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 100:// 通过点击NewSCActivity里面的删除图标发送过来的handler改变状态
				if (IsShow) {
					btm_linear.setVisibility(View.GONE);
					all_Adapter.setIsShow(false);
					RollBack();
					IsShow = false;
				} else {
					all_Adapter.setIsShow(true);
					RollBack();
					btm_linear.setVisibility(View.VISIBLE);
					IsShow = true;
				}
				break;
			case 90://还原状态
				btm_linear.setVisibility(View.GONE);
				IsShow = false;
				all_Adapter.setIsShow(false);
				RollBack();
				break;
			case 101://重新获取数据
				scBeans.clear();
				db = dataSqlte.getReadableDatabase();
				if (db == null) {
					return;
				}
				Cursor cursor = db.query("data", null, null, null, null, null,
						null);
				while (cursor.moveToNext()) {
					bean = new SCBean();
					bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
					bean.setAddtime(cursor.getString(cursor
							.getColumnIndex("addtime")));
					bean.setCategory(cursor.getString(cursor
							.getColumnIndex("category")));
					bean.setId(cursor.getString(cursor.getColumnIndex("id")));
					bean.setIsdp(cursor.getString(cursor.getColumnIndex("isdp")));
					bean.setTitle(cursor.getString(cursor
							.getColumnIndex("title")));
					bean.setThumb(cursor.getString(cursor
							.getColumnIndex("thumb")));
					bean.setWeburl(cursor.getString(cursor
							.getColumnIndex("weburl")));
					bean.setTag(cursor.getString(cursor.getColumnIndex("tag"))
							.split(","));
					scBeans.add(bean);
				}
				all_Adapter.setScBeans(scBeans);
				isShowBackground();
				all_Adapter.notifyDataSetChanged();
				break;
			case 111:// 弹出确认删除对话框
				popManager.initPopupWindwo(view, cDeleteView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
						Color.TRANSPARENT,
						R.style.popwindow_register_animation, 0, 0);

				break;
			default:
				break;
			}
		};
	};

	// 是否显示背景图片
	public void isShowBackground() {
		if (scBeans.size() > 0 && scBeans != null) {
			image_favorite_bg.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		} else {
			image_favorite_bg.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.select_all:
			if (!isSelect) {

				for (int i = 0; i < scBeans.size(); i++) {
					SCAdapter.getIsSelected().put(i, true);
					if (!deleteDate.contains((i + ","))) {
						deleteDate = deleteDate + i + ",";
					}
				}
				// 数量设为list的长度
				// 刷新listview和TextView的显示
				dataChanged();
				isSelect = true;
			} else {
				deleteDate = "";
				RollBack();
			}
			break;
		case R.id.delete_all://删除全部
			handler.sendEmptyMessage(111);
			break;
		case R.id.btn_cancel:// 取消删除
			popManager.dismiss();
			RollBack();
			break;
		case R.id.btn_ok:// 确定删除
			popManager.dismiss();
			if (deleteDate.length() > 1 && !deleteDate.equals("")) {
				db = dataSqlte.getWritableDatabase();
				Date = deleteDate.split(",");
				for (int i = 0; i < Date.length; i++) {
					db.delete("data", "id=? and category=?", new String[] {
							scBeans.get(Integer.parseInt(Date[i])).getId(),
							scBeans.get(Integer.parseInt(Date[i]))
									.getCategory() });
				}
				scBeans.clear();
				db.close();
				InitSCbean();
				Date = null;
				RollBack();
			} else {
				Toast.makeText(getActivity(), "您暂时未添加删除项", Toast.LENGTH_SHORT)
						.show();
			}
			Boolean isShowImg = false;
			if (null != scBeans && scBeans.size() > 0) {
				isShowImg = false;
			} else {
				isShowImg = true;
			}
			((NewSCActivity) context).changeButton(isShowImg);
			isShowBackground();
			break;
		default:
			break;
		}
	}

	/**
	 * 还原初始状态
	 */
	private void RollBack() {
		for (int i = 0; i < scBeans.size(); i++) {
			if (SCAdapter.getIsSelected().get(i)) {
				SCAdapter.getIsSelected().put(i, false);
			}
		}
		deleteDate = "";
		// 刷新listview和TextView的显示
		dataChanged();
		isSelect = false;
	}

	private void dataChanged() {
		// 通知listView刷新
		all_Adapter.notifyDataSetChanged();
		// TextView显示最新的选中数目
	};

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.context = activity;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 188 && resultCode == 282) {
			handler.sendEmptyMessage(101);
		}
	}
}
