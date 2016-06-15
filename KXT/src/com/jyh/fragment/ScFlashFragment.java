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

import com.jyh.gson.bean.Flash_CjInfo;
import com.jyh.kxt.FlashActivity;
import com.jyh.kxt.NewSCActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.ScFalshAdpater;
import com.jyh.kxt.adapter.ScFalshAdpater.CjViewHolder;
import com.jyh.kxt.adapter.ScFalshAdpater.KxViewHolder;
import com.jyh.kxt.customtool.HXListView;
import com.jyh.kxt.customtool.HXListView.DelButtonClickListener;
import com.jyh.kxt.sqlte.SCDataSqlte;
import com.jyh.tool.PopupWindowUtils;

/**
 * 收藏界面快讯收藏
 * 
 * @author Administrator
 *
 */
public class ScFlashFragment extends Fragment implements OnClickListener {

	private Context context;
	private SharedPreferences preferences;
	private boolean Isyj;
	private ImageView plar_img;
	private HXListView listView;
	protected WeakReference<View> mRootView;
	private View view;
	private View cDeleteView;// 收藏删除的
	private Button btn_cancel;
	private Button btn_ok;
	private TextView select_all, delete_all;
	private LinearLayout btm_linear;
	private PopupWindowUtils popManager;
	private SCDataSqlte dataSqlte;
	private List<Flash_CjInfo> scBeans;
	private SQLiteDatabase db;
	private Cursor cursor;
	private Flash_CjInfo bean;
	private ScFalshAdpater falshAdpater;
	private boolean IsShow = false;
	private String deleteDate = "";
	private boolean needRefrash = false;
	private boolean isSelect = false;
	private String[] Date;
	private TextView delte_text;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 100://根据收藏界面的删除判断是否显示删除
				if (IsShow) {
					btm_linear.setVisibility(View.GONE);
					falshAdpater.setIsShow(false);
					RollBack();
					IsShow = false;
				} else {
					falshAdpater.setIsShow(true);
					RollBack();
					btm_linear.setVisibility(View.VISIBLE);
					IsShow = true;
				}
				break;
			case 90:
				btm_linear.setVisibility(View.GONE);
				IsShow = false;
				falshAdpater.setIsShow(false);
				RollBack();
				break;
			case 101:
				scBeans.clear();
				db = dataSqlte.getReadableDatabase();
				if (db == null) {
					return;
				}
				Cursor cursor = db.query("flash", null, null, null, null, null,
						null);
				while (cursor.moveToNext()) {
					bean = new Flash_CjInfo();

					bean.setId(cursor.getString(cursor
							.getColumnIndex("data_id")));
					bean.setType(cursor.getString(cursor
							.getColumnIndex("data_type")));
					bean.setTitle(cursor.getString(cursor
							.getColumnIndex("data_title")));
					bean.setTime(Integer.parseInt(cursor.getString(
							cursor.getColumnIndex("data_time")).equals("") ? "0"
							: cursor.getString(cursor
									.getColumnIndex("data_time"))));
					bean.setPredicttime(Integer.parseInt((cursor.getString(
							cursor.getColumnIndex("data_predicttime")).equals(
							"") ? "0" : cursor.getString(cursor
							.getColumnIndex("data_predicttime")))));
					bean.setState(cursor.getString(cursor
							.getColumnIndex("data_state")));
					bean.setReality(cursor.getString(cursor
							.getColumnIndex("data_reality")));
					bean.setBefore(cursor.getString(cursor
							.getColumnIndex("data_before")));
					bean.setForecast(cursor.getString(cursor
							.getColumnIndex("data_forecast")));
					bean.setEffect(cursor.getString(cursor
							.getColumnIndex("data_effect")));
					bean.setImportance(cursor.getString(cursor
							.getColumnIndex("data_importance")));

					String[] titles = cursor.getString(
							cursor.getColumnIndex("data_effect")).split("\\|");

					if (titles.length > 0 && titles.length == 1) {
						bean.setEffectGood(titles[0]);// 利空
					} else if (titles.length > 0 && titles.length == 2) {
						bean.setEffectBad(titles[1]);// 利多
						bean.setEffectGood(titles[0]);// 利空
					} else {
						bean.setEffectMid("影响较小");
					}

					scBeans.add(bean);
				}
				falshAdpater.setScBeans(scBeans);
				isShowBackground();
				falshAdpater.notifyDataSetChanged();
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
		}
	};

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.context = activity;
	}

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
			view = inflater.inflate(R.layout.fragment_sc_flash, null);
			initViews();
			mRootView = new WeakReference<View>(view);
		} else {
			ViewGroup parent = (ViewGroup) mRootView.get().getParent();
			if (parent != null) {
				parent.removeView(mRootView.get());
			}
		}
		return mRootView.get();
	}

	/**
	 * 初始化控件
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		plar_img = (ImageView) view.findViewById(R.id.plar_img);
		listView = (HXListView) view.findViewById(R.id.sc_flash_list);
		select_all = (TextView) view.findViewById(R.id.select_all);
		delete_all = (TextView) view.findViewById(R.id.delete_all);
		btm_linear = (LinearLayout) view.findViewById(R.id.btm_linear);

		cDeleteView = View.inflate(context, R.layout.pop_counter_delete_view,
				null);
		btn_cancel = (Button) cDeleteView.findViewById(R.id.btn_cancel);
		btn_ok = (Button) cDeleteView.findViewById(R.id.btn_ok);
		delte_text = (TextView) cDeleteView.findViewById(R.id.text_mess);
		delte_text.setText("确定删除快讯？");
		btn_cancel.setOnClickListener(this);
		btn_ok.setOnClickListener(this);

		popManager = new PopupWindowUtils();

		initDatas();

	}

	/**
	 * 初始化数据
	 */
	public void initDatas() {
		dataSqlte = new SCDataSqlte(context);
		scBeans = new ArrayList<Flash_CjInfo>();
		initScBean();
		isShowBackground();
		falshAdpater = new ScFalshAdpater(getActivity(), scBeans, listView);
		// try {
		// Thread.sleep(1 * 1000);
		listView.setAdapter(falshAdpater);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int type = falshAdpater.getItemViewType(arg2);
				if (IsShow) {
					switch (type) {
					case 0:// 快讯

						KxViewHolder kxHolder = (KxViewHolder) arg1.getTag();
						kxHolder.check_sc.toggle();

						// 将CheckBox的选中状况记录下来
						ScFalshAdpater.getIsSelected().put(arg2,
								kxHolder.check_sc.isChecked());
						// 调整选定条目
						if (kxHolder.check_sc.isChecked() == true) {
							if (!deleteDate.contains(arg2 + "")) {
								deleteDate = deleteDate + arg2 + ",";
							}
						} else {
							if (deleteDate.contains(arg2 + "")) {
								deleteDate = deleteDate.replace(arg2 + ",", "");
							}
						}
						break;
					case 1:// 财经
						CjViewHolder cjHolder = (CjViewHolder) arg1.getTag();
						cjHolder.check_sc.toggle();

						// 将CheckBox的选中状况记录下来
						ScFalshAdpater.getIsSelected().put(arg2,
								cjHolder.check_sc.isChecked());
						// 调整选定条目
						if (cjHolder.check_sc.isChecked() == true) {
							if (!deleteDate.contains(arg2 + "")) {
								deleteDate = deleteDate + arg2 + ",";
							}
						} else {
							if (deleteDate.contains(arg2 + "")) {
								deleteDate = deleteDate.replace(arg2 + ",", "");
							}
						}
						break;
					default:
						break;
					}

				} else {

					switch (type) {
					case 0:
						Intent intent = new Intent(getActivity(),
								FlashActivity.class);
						intent.putExtra("id", scBeans.get(arg2).getId());
						intent.putExtra("enterpage", "shou");// shou
						intent.putExtra("type", "1");
						startActivityForResult(intent, 2);
						break;
					case 1:
						Intent intent1 = new Intent(getActivity(),
								FlashActivity.class);
						intent1.putExtra("id", scBeans.get(arg2).getId());
						intent1.putExtra("enterpage", "shou");// shou
						intent1.putExtra("type", "2");
						startActivityForResult(intent1, 2);
						break;

					default:
						break;
					}

				}
			}
		});

		listView.setDelButtonClickListener(new DelButtonClickListener() {

			@Override
			public void clickHappend(int position) {
				db = dataSqlte.getWritableDatabase();
				db.delete("flash", "id=? ", new String[] { scBeans
						.get(position).getId() });
				handler.sendEmptyMessage(101);
			}
		});

		select_all.setOnClickListener(this);
		delete_all.setOnClickListener(this);
	}

	/**
	 * 初始化收藏的数据
	 */
	public void initScBean() {
		db = dataSqlte.getReadableDatabase();
		if (db == null) {
			return;
		}
		cursor = db.query("flash", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			bean = new Flash_CjInfo();
			bean.setId(cursor.getString(cursor.getColumnIndex("data_id")));
			bean.setType(cursor.getString(cursor.getColumnIndex("data_type")));
			bean.setTitle(cursor.getString(cursor.getColumnIndex("data_title")));
			bean.setTime(Integer.parseInt(cursor.getString(
					cursor.getColumnIndex("data_time")).equals("") ? "0"
					: cursor.getString(cursor.getColumnIndex("data_time"))));
			bean.setPredicttime(Integer.parseInt((cursor.getString(
					cursor.getColumnIndex("data_predicttime")).equals("") ? "0"
					: cursor.getString(cursor
							.getColumnIndex("data_predicttime")))));
			bean.setState(cursor.getString(cursor.getColumnIndex("data_state")));
			bean.setReality(cursor.getString(cursor
					.getColumnIndex("data_reality")));
			bean.setBefore(cursor.getString(cursor
					.getColumnIndex("data_before")));
			bean.setForecast(cursor.getString(cursor
					.getColumnIndex("data_forecast")));
			bean.setEffect(cursor.getString(cursor
					.getColumnIndex("data_effect")));
			bean.setImportance(cursor.getString(cursor
					.getColumnIndex("data_importance")));
			String[] titles = cursor.getString(
					cursor.getColumnIndex("data_effect")).split("\\|");

			if (titles.length > 0 && titles.length == 1) {
				bean.setEffectGood(titles[0]);// 利空
			} else if (titles.length > 0 && titles.length == 2) {
				bean.setEffectBad(titles[1]);// 利多
				bean.setEffectGood(titles[0]);// 利空
			} else {
				bean.setEffectMid("影响较小");
			}
			scBeans.add(bean);
		}
		cursor.close();
		db.close();
	}

	/**
	 * 是否显示背景
	 */
	public void isShowBackground() {
		if (scBeans.size() > 0 && scBeans != null) {
			plar_img.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		} else {
			plar_img.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (needRefrash) {
			scBeans.clear();
			initScBean();
			falshAdpater.notifyDataSetChanged();
			needRefrash = false;
		}
	}
	/**
	 * 初始化选中
	 */
	private void RollBack() {
		for (int i = 0; i < scBeans.size(); i++) {
			if (ScFalshAdpater.getIsSelected().get(i)) {
				ScFalshAdpater.getIsSelected().put(i, false);
			}
		}
		deleteDate = "";
		// 刷新listview和TextView的显示
		dataChanged();
		isSelect = false;
	}
	
	private void dataChanged() {
		// 通知listView刷新
		falshAdpater.notifyDataSetChanged();
		// TextView显示最新的选中数目
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.select_all://全选
			if (!isSelect) {
				for (int i = 0; i < scBeans.size(); i++) {
					ScFalshAdpater.getIsSelected().put(i, true);
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
		case R.id.delete_all://删除所有
			handler.sendEmptyMessage(111);
			break;
		case R.id.btn_cancel://取消
			popManager.dismiss();
			RollBack();
			break;
		case R.id.btn_ok://删除
			popManager.dismiss();
			if (deleteDate.length() > 1 && !deleteDate.equals("")) {
				db = dataSqlte.getWritableDatabase();
				Date = deleteDate.split(",");
				for (int i = 0; i < Date.length; i++) {
					db.delete(
							"flash",
							"id=? ",
							new String[] { scBeans.get(
									Integer.parseInt(Date[i])).getId() });
				}
				scBeans.clear();
				db.close();
				initScBean();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == 100) {
			handler.sendEmptyMessage(101);
		}
	}
}
