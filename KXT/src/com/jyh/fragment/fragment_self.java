package com.jyh.fragment;

import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jyh.kxt.AboutActivity;
import com.jyh.kxt.AnimtionActivity;
import com.jyh.kxt.FKActivity;
import com.jyh.kxt.NewSCActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.TJActivity;
import com.jyh.kxt.customtool.BaseAnimatorSet;
import com.jyh.kxt.customtool.BounceTopEnter;
import com.jyh.kxt.customtool.NormalDialog;
import com.jyh.kxt.customtool.OnBtnClickL;
import com.jyh.kxt.customtool.SlideBottomExit;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.KXTSocketService;
import com.jyh.tool.Utils;
/**
 * 我  界面
 * @author PC
 *
 */
public class fragment_self extends Fragment implements OnClickListener {
	private KXTApplication application;
	private SharedPreferences sp;
	private boolean push_btn;
	private boolean push_sound;
	private boolean yj_btn;
	private ImageView self_img_ts;
	private ImageView self_img_sound;
	private ImageView self_img_yj;
	private Editor editor;
	private SharedPreferences preferences;
	private boolean Isyj = false;
	protected WeakReference<View> mRootView;
	private Context context;
	private BaseAnimatorSet bas_in;
	private BaseAnimatorSet bas_out;
	private LinearLayout self_ll_fk, self_ll_clear, self_ll_about, self_ll_out,
			self_ll_tj, self_ll_sc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		savedInstanceState=null;
		preferences = context.getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		if (Isyj) {
			context.setTheme(R.style.BrowserThemeNight);
		} else {
			context.setTheme(R.style.BrowserThemeDefault);
		}
		if (mRootView == null || mRootView.get() == null) {
			View view = inflater.inflate(R.layout.fragment_self, container,
					false);
			InitFind(view);
			Init();
			mRootView = new WeakReference<View>(view);
		} else {
			ViewGroup parent = (ViewGroup) mRootView.get().getParent();
			if (parent != null) {
				parent.removeView(mRootView.get());
			}
		}
		return mRootView.get();
	}

	private void Init() {
		self_ll_about.setOnClickListener(this);
		self_ll_sc.setOnClickListener(this);
		self_ll_clear.setOnClickListener(this);
		self_ll_fk.setOnClickListener(this);
		self_ll_out.setOnClickListener(this);
		self_img_ts.setOnClickListener(this);
		self_img_sound.setOnClickListener(this);
		self_img_yj.setOnClickListener(this);
		self_ll_tj.setOnClickListener(this);
	}

	private void InitFind(View view) {
		bas_in = new BounceTopEnter();
		bas_out = new SlideBottomExit();
		self_ll_about = (LinearLayout) view.findViewById(R.id.self_ll_about);
		self_ll_sc = (LinearLayout) view.findViewById(R.id.self_ll_sc);
		self_ll_clear = (LinearLayout) view.findViewById(R.id.self_ll_clear);
		self_ll_fk = (LinearLayout) view.findViewById(R.id.self_ll_fk);
		self_ll_out = (LinearLayout) view.findViewById(R.id.self_ll_out);
		self_img_ts = (ImageView) view.findViewById(R.id.self_img_ts);
		self_img_sound = (ImageView) view.findViewById(R.id.self_img_sound);
		self_img_yj = (ImageView) view.findViewById(R.id.self_img_yj);
		self_ll_tj = (LinearLayout) view.findViewById(R.id.self_ll_tj);
		application = (KXTApplication) ((Activity) context).getApplication();
		sp = context.getSharedPreferences("setup", Context.MODE_PRIVATE);
		push_btn = sp.getBoolean("push", true);
		push_sound = sp.getBoolean("sound", true);
		yj_btn = sp.getBoolean("yj_btn", false);
		if (push_btn == true) {
			self_img_ts.setSelected(true);
		}
		if (push_sound == true) {
			self_img_sound.setSelected(true);
		}
		if (yj_btn == true) {
			self_img_yj.setSelected(true);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 100:
				Utils.showToast(application, "清理缓存");
				SharedPreferences preferences = getActivity()
						.getSharedPreferences("wzdata", Context.MODE_PRIVATE);
				preferences.edit().clear().commit();
				handler.sendEmptyMessageDelayed(101, 2 * 1000);
				break;
			case 101:
				Utils.showToast(application, "清理成功");
				break;
			case 102:
				Utils.showToast(application, "没有缓存");
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View arg0) {
		Intent intent;
		switch (arg0.getId()) {
		case R.id.self_ll_about://关于界面
			intent = new Intent(context, AboutActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;
		case R.id.self_ll_sc://收藏界面
			intent = new Intent(context, NewSCActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;
		case R.id.self_ll_clear://清除缓存界面
			Utils.showToast(application, "检测缓存中");
			handler.sendEmptyMessage(100);
			break;
		case R.id.self_ll_fk://反馈界面
			intent = new Intent(context, FKActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;
		case R.id.self_ll_out://退出按钮
			final NormalDialog dialog = new NormalDialog(context);
			dialog.isTitleShow(false)
					// 设置背景颜色
					.bgColor(Color.parseColor("#383838"))
					// 设置dialog角度
					.cornerRadius(5)
					// 设置内容
					.content("是否确定退出程序?")
					// 设置居中
					.contentGravity(Gravity.CENTER)
					// 设置内容字体颜色
					.contentTextColor(Color.parseColor("#ffffff"))
					// 设置线的颜色
					.dividerColor(Color.parseColor("#222222"))
					// 设置字体
					.btnTextSize(15.5f, 15.5f)
					// 设置取消确定颜色
					.btnTextColor(Color.parseColor("#ffffff"),
							Color.parseColor("#ffffff"))//
					.btnPressColor(Color.parseColor("#2B2B2B"))//
					.widthScale(0.85f)//
					.showAnim(bas_in)//
					.dismissAnim(bas_out)//
					.show();

			dialog.setOnBtnClickL(new OnBtnClickL() {
				@Override
				public void onBtnClick() {
					dialog.dismiss();
				}
			}, new OnBtnClickL() {
				@Override
				public void onBtnClick() {
					dialog.dismiss();
					application.exitAppAll();
				}
			});
			break;
		case R.id.self_img_ts://推送界面
			editor = sp.edit();
			if (self_img_ts.isSelected()) {
				self_img_ts.setSelected(false);
				editor.putBoolean("push", false).commit();
				Toast.makeText(context, "推送已经关闭", Toast.LENGTH_SHORT).show();
			} else {
				self_img_ts.setSelected(true);
				editor.putBoolean("push", true).commit();
				Toast.makeText(context, "推送已经开启", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.self_img_sound://是否开启推送提醒
			editor = sp.edit();
			if (self_img_sound.isSelected()) {
				self_img_sound.setSelected(false);
				editor.putBoolean("sound", false).commit();
				Toast.makeText(context, "提示音已经关闭", Toast.LENGTH_SHORT).show();
			} else {
				self_img_sound.setSelected(true);
				editor.putBoolean("sound", true).commit();
				Toast.makeText(context, "提示音已经开启", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.self_img_yj://夜间模式
			editor = sp.edit();
			application.ischange = true;
			if (self_img_yj.isSelected()) {
				self_img_yj.setSelected(false);
				editor.putBoolean("yj_btn", false).commit();
				intent = new Intent(context, AnimtionActivity.class);
				context.startActivity(intent);
				((Activity) context).finish();
			} else {
				self_img_yj.setSelected(true);
				editor.putBoolean("yj_btn", true).commit();
				intent = new Intent(context, AnimtionActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(intent);
				((Activity) context).finish();
			}
			break;
		case R.id.self_ll_tj://推荐界面
			intent = new Intent(context, TJActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unused")
	private void InitSocketIp() {
		// TODO Auto-generated method stub
		HttpGet get = new HttpGet("http://appapi.kxt.com/info/index");
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String string = EntityUtils.toString(response.getEntity());
				Intent intent = new Intent(context, KXTSocketService.class);
				intent.putExtra("token", string);
				intent.putExtra("isopen", "1");
				context.startService(intent);
				Toast.makeText(context, "推送开启成功", Toast.LENGTH_LONG).show();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (client != null && client.getConnectionManager() != null) {
				client.getConnectionManager().shutdown();
			}
		}
	}

	// private OnFragmentListener mListener;

	// @Override
	// public void onAttach(Activity activity) {
	//
	// super.onAttach(activity);
	//
	// try {
	//
	// mListener = (OnFragmentListener) activity;
	//
	// } catch (ClassCastException e) {
	//
	// e.printStackTrace();
	// }
	//
	// }

	public interface OnFragmentListener {

		public void onFragmentAction();

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = activity;
	}
}
