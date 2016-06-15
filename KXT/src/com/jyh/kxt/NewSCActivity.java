package com.jyh.kxt;

import com.jyh.fragment.ArticFragment;
import com.jyh.fragment.PlayerFragment;
import com.jyh.fragment.ScFlashFragment;
import com.jyh.kxt.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewSCActivity extends FragmentActivity implements OnClickListener {
	private SharedPreferences preferences;
	private boolean IsYj;
	private LinearLayout frag_sc_player, frag_sc_artic, frag_sc_flash,
			sc_img_frag, sc_linear_del;
	private TextView sc_edit;
	private ViewPager sc_pager;
	private ImageView frag_sc_tag;
	private PlayerFragment playerfragment;
	private ArticFragment articfragment;
	private ScFlashFragment flashFrament;
	private ScAdapter scAdapter;
	private static Handler playerhandler;
	private static Handler artichandler;
	private static Handler flashhandler;
	private int CurrentItem = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
		IsYj = preferences.getBoolean("yj_btn", false);
		if (IsYj) {
			this.setTheme(R.style.BrowserThemeNight);
		} else {
			this.setTheme(R.style.BrowserThemeDefault);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_sc);

		InitFind();

	}

	private void InitFind() {
		// TODO Auto-generated method stub
		frag_sc_player = (LinearLayout) findViewById(R.id.frag_sc_player);// 视频按钮
		frag_sc_artic = (LinearLayout) findViewById(R.id.frag_sc_artic);// 文章按钮
		frag_sc_flash = (LinearLayout) findViewById(R.id.frag_sc_flash);// 快讯按钮
		sc_img_frag = (LinearLayout) findViewById(R.id.sc_img_frag);// 退出按钮
		sc_linear_del = (LinearLayout) findViewById(R.id.sc_linear_del);
		sc_edit = (TextView) findViewById(R.id.sc_edit);// 编辑按钮
		sc_pager = (ViewPager) findViewById(R.id.sc_pager);
		frag_sc_tag = (ImageView) findViewById(R.id.frag_sc_tag);
		FragmentManager fm = getSupportFragmentManager();
		scAdapter = new ScAdapter(fm);
		sc_pager.setAdapter(scAdapter);
		InitOnclick();
	}

	private void InitOnclick() {
		// TODO Auto-generated method stub
		frag_sc_artic.setOnClickListener(this);
		frag_sc_player.setOnClickListener(this);
		frag_sc_flash.setOnClickListener(this);
		sc_edit.setOnClickListener(this);
		sc_img_frag.setOnClickListener(this);
		sc_linear_del.setOnClickListener(this);
		sc_pager.setCurrentItem(0);
		sc_pager.setOffscreenPageLimit(2);
		sc_pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				SendGone();
				sc_linear_del.setVisibility(View.VISIBLE);
				sc_edit.setVisibility(View.GONE);
				CurrentItem = arg0;
				switch (arg0) {
				case 0:
					if (IsYj) {
						frag_sc_tag
								.setBackgroundResource(R.drawable.sc_st_select_night);
					} else {
						frag_sc_tag
								.setBackgroundResource(R.drawable.sc_st_select);
					}
					break;
				case 1:
					if (IsYj) {
						frag_sc_tag
								.setBackgroundResource(R.drawable.sc_wz_select_night);
					} else {
						frag_sc_tag
								.setBackgroundResource(R.drawable.sc_wz_select);
					}
					break;
				case 2:
					if (IsYj) {
						frag_sc_tag
								.setBackgroundResource(R.drawable.sc_kx_select_night);
					} else {
						frag_sc_tag
								.setBackgroundResource(R.drawable.sc_kx_select);
					}
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void SendGone() {
		// TODO Auto-generated method stub

		if (null != playerhandler) {
			playerhandler.sendEmptyMessage(90);
		}
		if (null != artichandler) {
			artichandler.sendEmptyMessage(90);
		}
		if (null != flashhandler) {
			flashhandler.sendEmptyMessage(90);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.frag_sc_artic:
			sc_pager.setCurrentItem(1);
			CurrentItem = 1;
			break;
		case R.id.frag_sc_player:
			sc_pager.setCurrentItem(0);
			CurrentItem = 0;
			break;
		case R.id.frag_sc_flash:
			sc_pager.setCurrentItem(2);
			CurrentItem = 2;
			break;
		case R.id.sc_edit:
			sc_linear_del.setVisibility(View.VISIBLE);
			sc_edit.setVisibility(View.GONE);
			switch (CurrentItem) {
			case 0:
				playerhandler.sendEmptyMessage(100);
				break;
			case 1:
				artichandler.sendEmptyMessage(100);
				break;
			case 2:
				flashhandler.sendEmptyMessage(100);
				break;
			default:
				break;
			}
			break;
		case R.id.sc_img_frag:
			playerfragment = null;
			articfragment = null;
			playerfragment = null;
			articfragment = null;
			flashFrament = null;
			finish();
			break;
		case R.id.sc_linear_del:
			sc_linear_del.setVisibility(View.GONE);
			sc_edit.setVisibility(View.VISIBLE);
			switch (CurrentItem) {
			case 0:
				playerhandler.sendEmptyMessage(100);
				break;
			case 1:
				artichandler.sendEmptyMessage(100);
				break;
			case 2:
				flashhandler.sendEmptyMessage(100);
				break;

			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	class ScAdapter extends FragmentPagerAdapter {
		public ScAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (null == playerfragment) {
					playerfragment = new PlayerFragment();
				}
				playerhandler = playerfragment.handler;
				return playerfragment;
			case 1:
				if (null == articfragment) {
					articfragment = new ArticFragment();
				}
				artichandler = articfragment.handler;
				return articfragment;
			case 2:
				if (null == flashFrament) {
					flashFrament = new ScFlashFragment();
				}
				flashhandler = flashFrament.handler;
				return flashFrament;
			}
			return null;
		}

		@Override
		public int getCount() {
			// 多少页
			return 3;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 130 && resultCode == 130) {
			artichandler.sendEmptyMessage(101);
		}
		if (null != data && resultCode == 100
				&& "wz".equals(data.getStringExtra("value"))) {
			if (null != artichandler) {
				artichandler.sendEmptyMessage(101);
			}
		}

	}

	/**
	 * 改变右上角按钮样式
	 */
	public void changeButton(boolean isShowImg) {
		if (isShowImg) {
			sc_edit.setVisibility(View.GONE);
			sc_linear_del.setVisibility(View.VISIBLE);
		} else {
			sc_edit.setVisibility(View.VISIBLE);
			sc_linear_del.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		artichandler = null;
		playerhandler = null;
		flashhandler = null;
	}
}
