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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.jyh.bean.ScPlayBean;
import com.jyh.kxt.NewSCActivity;
import com.jyh.kxt.PlayerActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.ScGridAdpater;
import com.jyh.kxt.adapter.ScGridAdpater.MyHolder;
import com.jyh.kxt.sqlte.SCDataSqlte;
import com.jyh.tool.PopupWindowUtils;
/**
 * 收藏视频界面
 * @author PC
 *
 */
public class PlayerFragment extends Fragment implements OnClickListener {
	private View view;
	protected WeakReference<View> mRootView;
	private SharedPreferences preferences;
	private boolean Isyj = false;
	private TextView selete_all, delete_all;
	private ImageView plar_img;
	private GridView sc_player_grid;
	private LinearLayout btm_linear;
	private boolean IsShow = false;
	private SCDataSqlte dataSqlte;
	private SQLiteDatabase db;
	private List<ScPlayBean> scplays = new ArrayList<ScPlayBean>();
	private ScPlayBean scPlayBean;
	private Context context;
	private ScGridAdpater scAdpater;
	private PopupWindowUtils popManager;
	private View cDeleteView;
	private Button btn_cancel;
	private Button btn_ok;
	private TextView text_mess;
	private Cursor cursor;
	private boolean selectAll = false;
	private String playDelete = "";
	private String[] playDate;

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
			view = inflater.inflate(R.layout.fragment_sc_player, null);
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
		selete_all = (TextView) view.findViewById(R.id.select_all);
		delete_all = (TextView) view.findViewById(R.id.delete_all);
		plar_img = (ImageView) view.findViewById(R.id.plar_img);
		sc_player_grid = (GridView) view.findViewById(R.id.sc_player_grid);
		btm_linear = (LinearLayout) view.findViewById(R.id.btm_linear);
		scAdpater = new ScGridAdpater(context, scplays);
		sc_player_grid.setAdapter(scAdpater);

		popManager = new PopupWindowUtils();
		cDeleteView = View.inflate(context, R.layout.pop_counter_delete_view,
				null);
		btn_cancel = (Button) cDeleteView.findViewById(R.id.btn_cancel);
		btn_ok = (Button) cDeleteView.findViewById(R.id.btn_ok);
		text_mess = (TextView) cDeleteView.findViewById(R.id.text_mess);
		text_mess.setText("确定删除视频？");
		selete_all.setOnClickListener(this);
		delete_all.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		sc_player_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View childview,
					int position, long id) {
				// TODO Auto-generated method stub

				if (IsShow) {
					MyHolder holder = (MyHolder) childview.getTag();
					if (holder.sc_grid_delete.isChecked()) {
						holder.sc_grid_delete.setChecked(false);
					} else {
						holder.sc_grid_delete.setChecked(true);
					}
					if (playDelete.contains((position + ","))) {
						playDelete = playDelete.replace(position + ",", "");
					} else {
						playDelete = playDelete + position + ",";
					}
				} else {
					Intent intent = new Intent(getActivity(),
							PlayerActivity.class);
					intent.putExtra("url", scplays.get(position).getUrl());
					intent.putExtra("sc_play", "yes");
					startActivityForResult(intent, 120);
				}
			}
		});

		InitData();

	}
	/**
	 * 初始化数据
	 */
	private void InitData() {
		// TODO Auto-generated method stub
		dataSqlte = new SCDataSqlte(getActivity());
		db = dataSqlte.getReadableDatabase();
		if (db == null) {
			return;
		}
		cursor = db.query("vedio", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			scPlayBean = new ScPlayBean();
			scPlayBean.setDiscription(cursor.getString(cursor
					.getColumnIndex("discription")));
			scPlayBean.setId(cursor.getString(cursor.getColumnIndex("id")));
			scPlayBean.setImagurl(cursor.getString(cursor
					.getColumnIndex("image_url")));
			scPlayBean.setPlay_count(cursor.getString(cursor
					.getColumnIndex("play_count")));
			scPlayBean.setShare_url(cursor.getString(cursor
					.getColumnIndex("share_url")));
			scPlayBean
					.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			scPlayBean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			scplays.add(scPlayBean);
		}
		cursor.close();
		db.close();
		isShowBackground();
	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 100:
				if (IsShow) {// 取消
					playDelete = "";
					btm_linear.setVisibility(View.GONE);
					IsShow = false;
					scAdpater.setType(0);
					scAdpater.notifyDataSetChanged();
				} else {// 删除
					btm_linear.setVisibility(View.VISIBLE);
					IsShow = true;
					scAdpater.setType(1);
					scAdpater.notifyDataSetChanged();
				}
				break;
			case 90:
				playDelete="";
				btm_linear.setVisibility(View.GONE);
				IsShow = false;
				scAdpater.setType(0);
				scAdpater.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.delete_all:// 删除
			popManager.initPopupWindwo(view, cDeleteView,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
					Color.TRANSPARENT, R.style.popwindow_register_animation, 0,
					0);
			break;
		case R.id.select_all:// 全选
			if (selectAll) {
				scAdpater.setType(3);
				selectAll = false;
				playDelete = "";
			} else {
				playDelete = "";
				for (int i = 0; i < scplays.size(); i++) {
					playDelete = playDelete + i + ",";
				}
				scAdpater.setType(2);
				selectAll = true;
			}
			scAdpater.notifyDataSetChanged();
			break;

		case R.id.btn_cancel:// 取消删除
			popManager.dismiss();
			break;
		case R.id.btn_ok:// 确定删除
			popManager.dismiss();
			if (playDelete.length() > 1 && !playDelete.equals("")) {
				db = dataSqlte.getWritableDatabase();
				playDate = playDelete.split(",");
				for (int i = 0; i < playDate.length; i++) {
					db.delete(
							"vedio",
							"id=?",
							new String[] { scplays.get(
									Integer.parseInt(playDate[i])).getId() });
				}
				scplays.clear();
				db.close();
				InitData();
				playDate = null;
				playDelete = "";
				scAdpater.setType(3);
				scAdpater.notifyDataSetChanged();
			} else {
				Toast.makeText(getActivity(), "您暂时未添加删除项", Toast.LENGTH_SHORT)
						.show();
			}
			Boolean isShowImg = false;
			if (null != scplays && scplays.size() > 0) {
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

	// 是否显示背景图片
	public void isShowBackground() {
		if (scplays.size() > 0 && scplays != null) {
			plar_img.setVisibility(View.GONE);
			sc_player_grid.setVisibility(View.VISIBLE);
		} else {
			plar_img.setVisibility(View.VISIBLE);
			sc_player_grid.setVisibility(View.GONE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 120 && resultCode == 191) {
			scplays.clear();
			InitData();
			scAdpater.notifyDataSetChanged();
		}
	}
}
