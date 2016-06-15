package com.jyh.kxt.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jyh.gson.bean.Flash_CjInfo;
import com.jyh.gson.bean.Flash_ZxImgInfo;
import com.jyh.gson.bean.ZxInfo;
import com.jyh.kxt.DPWebActivity;
import com.jyh.kxt.FlashLinkActiviy;
import com.jyh.kxt.PlayerActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.ZXActivity;
import com.jyh.kxt.socket.ConstantValue;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.tool.DateTimeUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class LiveAdapter extends BaseAdapter {
	// 私有常量
	private boolean bool_dollor; // 美元
	private boolean bool_gold_silver; // 金银
	private boolean bool_oil; // 石油
	private ViewHolder_Cjrl holder_Cjrl; // 财经日历的holder
	// 公有常量
	ViewHolder_separator holder_separator;
	public static final String EFFECT_DOLLOR = "美元";
	public static final String EFFECT_GOLD_SILVER = "金银";
	public static final String EFFECT_OIL = "石油";
	private List<Object> list;
	private SharedPreferences preferences;
	Context context;
	private boolean Isyj = false;
	private ViewHolder_Zx holder_Zx;
	private ViewHolder_img1 viewHolder_img1;
	private ViewHolder_img2 viewHolder_img2;
	private ViewHolder_img3 viewHolder_img3;
	private ViewHolder_style viewHolder_style;
	public static String IMAGE_CACHE_PATH = "imageloader/Cache/flash"; // 图片缓存路径
	private String ImageContent;
	private ViewHolder_img4 viewHolder_img4;
	private Handler handler;
	private String NEWS_TITLE = "http://appapi.kxt.com/index.php/page/article_app/id";

	public LiveAdapter(Context context, List<Object> list,
			PullToRefreshListView listView, KXTApplication application) {
		this.context = context;
		this.list = list;
		this.handler = application.getMainHander();
		preferences = context.getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		init();
		initImageLoader();
	}

	private void initImageLoader() {
		File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
				.getOwnCacheDirectory(context.getApplicationContext(),
						IMAGE_CACHE_PATH);

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new LruMemoryCache(12 * 1024 * 1024))
				.memoryCacheSize(12 * 1024 * 1024)
				.discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
	}

	private void init() {
		bool_dollor = true;
		bool_gold_silver = true;
		bool_oil = true;

	}

	public List<Object> getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		if (null != list) {
			return list.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		Object mObj = list.get(position);
		if (mObj instanceof Flash_ZxImgInfo) {
			Flash_ZxImgInfo flash_ZxImgInfo = (Flash_ZxImgInfo) mObj;
			if (flash_ZxImgInfo.getStyle() != null) {
				if (flash_ZxImgInfo.getStyle().equals("1")) {
					if (convertView != null
							&& convertView.getTag() instanceof ViewHolder_img1) {
						viewHolder_img1 = (ViewHolder_img1) convertView
								.getTag();
					} else {
						convertView = LayoutInflater.from(context).inflate(
								R.layout.live_item4, null);
						viewHolder_img1 = new ViewHolder_img1();
						viewHolder_img1.img1 = (SimpleDraweeView) convertView
								.findViewById(R.id.live_img4);
						viewHolder_img1.text1 = (TextView) convertView
								.findViewById(R.id.lv_item_time4);
						viewHolder_img1.title1 = (TextView) convertView
								.findViewById(R.id.lv_item_brief4);
						viewHolder_img1.flash_rel1 = (LinearLayout) convertView
								.findViewById(R.id.flash_click4);
						convertView.setTag(viewHolder_img1);
					}
					if (!TextUtils.isEmpty(flash_ZxImgInfo.getImage())) {
						viewHolder_img1.img1.setTag(flash_ZxImgInfo.getImage());
						LoadImg(flash_ZxImgInfo.getImage(),
								viewHolder_img1.img1);
					} else {
						viewHolder_img1.img1.setVisibility(View.GONE);
					}
					viewHolder_img1.text1.setText(DateTimeUtil
							.FlashparseMillis(flash_ZxImgInfo.getTime() + "")
							.substring(11, 16));
					if (null != flash_ZxImgInfo.getTitle()
							|| null != flash_ZxImgInfo.getContent()) {
						boolean a = ReplaceString(flash_ZxImgInfo);
						// TODO
						viewHolder_img1.title1.setText(Html
								.fromHtml(ImageContent));
						if (!a) {
							LinkUrl(viewHolder_img1.title1, null);
						} else {
							LinkUrl(viewHolder_img1.title1,
									flash_ZxImgInfo.getTitle());
						}
						if (null != flash_ZxImgInfo.getUrl()) {
							SetALLink(viewHolder_img1.flash_rel1,
									flash_ZxImgInfo.getUrl().toString()
											.toLowerCase());
							if (!(flash_ZxImgInfo.getContent() + flash_ZxImgInfo
									.getTitle()).contains("<a")) {
								SetTextLink(viewHolder_img1.title1,
										flash_ZxImgInfo.getUrl().toString()
												.toLowerCase());
							}
						}
					} else {
						viewHolder_img1.text1.setText("(null)");
					}
					if (null != flash_ZxImgInfo.getImportance()
							&& flash_ZxImgInfo.getImportance().equals("高")) {
						viewHolder_img1.text1.setTextColor(Color.rgb(202, 70,
								57));
					}
				} else if (flash_ZxImgInfo.getStyle().equals("2")) {
					if (convertView != null
							&& convertView.getTag() instanceof ViewHolder_img2) {
						viewHolder_img2 = (ViewHolder_img2) convertView
								.getTag();
					} else {
						convertView = LayoutInflater.from(context).inflate(
								R.layout.live_item2, null);
						viewHolder_img2 = new ViewHolder_img2();
						viewHolder_img2.img2 = (SimpleDraweeView) convertView
								.findViewById(R.id.live_img2);
						viewHolder_img2.text2 = (TextView) convertView
								.findViewById(R.id.lv_item_time2);
						viewHolder_img2.title2 = (TextView) convertView
								.findViewById(R.id.lv_item_brief2);
						viewHolder_img2.flash_rel2 = (LinearLayout) convertView
								.findViewById(R.id.flash_click2);
						viewHolder_img2.title2
								.setMovementMethod(new LinkMovementMethod());
						convertView.setTag(viewHolder_img2);
					}

					if (!TextUtils.isEmpty(flash_ZxImgInfo.getImage())) {
						viewHolder_img2.img2.setTag(flash_ZxImgInfo.getImage());
						LoadImg(flash_ZxImgInfo.getImage(),
								viewHolder_img2.img2);
					} else {
						viewHolder_img2.img2.setVisibility(View.GONE);
					}
					viewHolder_img2.text2.setText(DateTimeUtil
							.FlashparseMillis(flash_ZxImgInfo.getTime() + "")
							.substring(11, 16));
					if (null != flash_ZxImgInfo.getTitle()
							|| null != flash_ZxImgInfo.getContent()) {
						boolean a = ReplaceString(flash_ZxImgInfo);
						// TODO
						viewHolder_img2.title2.setText(Html
								.fromHtml(ImageContent));
						if (!a) {
							LinkUrl(viewHolder_img2.title2, null);
						} else {
							LinkUrl(viewHolder_img2.title2,
									flash_ZxImgInfo.getTitle());
						}
						if (null != flash_ZxImgInfo.getUrl()) {
							SetALLink(viewHolder_img2.flash_rel2,
									flash_ZxImgInfo.getUrl().toString()
											.toLowerCase());
							if (!(flash_ZxImgInfo.getContent() + flash_ZxImgInfo
									.getTitle()).contains("<a")) {
								SetTextLink(viewHolder_img2.title2,
										flash_ZxImgInfo.getUrl().toString()
												.toLowerCase());
							}
						}
					} else {
						viewHolder_img2.title2.setText("(null)");
					}
					if (null != flash_ZxImgInfo.getImportance()
							&& flash_ZxImgInfo.getImportance().equals("高")) {
						viewHolder_img2.title2.setTextColor(Color.rgb(202, 70,
								57));
					}
				} else if (flash_ZxImgInfo.getStyle().equals("3")) {
					if (convertView != null
							&& convertView.getTag() instanceof ViewHolder_img3) {
						viewHolder_img3 = (ViewHolder_img3) convertView
								.getTag();
					} else {
						convertView = LayoutInflater.from(context).inflate(
								R.layout.live_item1, null);
						viewHolder_img3 = new ViewHolder_img3();
						viewHolder_img3.img3 = (SimpleDraweeView) convertView
								.findViewById(R.id.live_img1);
						viewHolder_img3.text3 = (TextView) convertView
								.findViewById(R.id.lv_item_time1);
						viewHolder_img3.title3 = (TextView) convertView
								.findViewById(R.id.lv_item_brief1);
						viewHolder_img3.title3
								.setMovementMethod(new LinkMovementMethod());
						viewHolder_img3.flash_rel3 = (LinearLayout) convertView
								.findViewById(R.id.flash_click1);
						convertView.setTag(viewHolder_img3);
					}

					if (!TextUtils.isEmpty(flash_ZxImgInfo.getImage())) {
						viewHolder_img3.img3.setTag(flash_ZxImgInfo.getImage());
						viewHolder_img3.img3.setAlpha(1.33f);
						LoadImg(flash_ZxImgInfo.getImage(),
								viewHolder_img3.img3);
					} else {
						viewHolder_img3.img3.setVisibility(View.GONE);
					}
					viewHolder_img3.text3.setText(DateTimeUtil
							.FlashparseMillis(flash_ZxImgInfo.getTime() + "")
							.substring(11, 16));
					if (null != flash_ZxImgInfo.getTitle()
							|| null != flash_ZxImgInfo.getContent()) {
						boolean a = ReplaceString(flash_ZxImgInfo);
						// TODO
						viewHolder_img3.title3.setText(Html
								.fromHtml(ImageContent));
						if (!a) {
							LinkUrl(viewHolder_img3.title3, null);
						} else {
							LinkUrl(viewHolder_img3.title3,
									flash_ZxImgInfo.getTitle());
						}
						if (null != flash_ZxImgInfo.getUrl()) {
							SetALLink(viewHolder_img3.flash_rel3,
									flash_ZxImgInfo.getUrl().toString()
											.toLowerCase());
							if (!(flash_ZxImgInfo.getContent() + flash_ZxImgInfo
									.getTitle()).contains("<a")) {
								SetTextLink(viewHolder_img3.title3,
										flash_ZxImgInfo.getUrl().toString()
												.toLowerCase());
							}
						}
					} else {
						viewHolder_img3.title3.setText("(null)");
					}
					if (null != flash_ZxImgInfo.getImportance()
							&& flash_ZxImgInfo.getImportance().equals("高")) {
						viewHolder_img3.title3.setTextColor(Color.rgb(202, 70,
								57));
					}
				} else if (flash_ZxImgInfo.getStyle().equals("4")) {
					if (convertView != null
							&& convertView.getTag() instanceof ViewHolder_img4) {
						viewHolder_img4 = (ViewHolder_img4) convertView
								.getTag();
					} else {
						convertView = LayoutInflater.from(context).inflate(
								R.layout.live_item3, null);
						viewHolder_img4 = new ViewHolder_img4();
						viewHolder_img4.img4 = (SimpleDraweeView) convertView
								.findViewById(R.id.live_img3);
						viewHolder_img4.text4 = (TextView) convertView
								.findViewById(R.id.lv_item_time3);
						viewHolder_img4.title4 = (TextView) convertView
								.findViewById(R.id.lv_item_brief3);
						viewHolder_img4.flash_rel4 = (LinearLayout) convertView
								.findViewById(R.id.flash_click3);
						viewHolder_img4.title4
								.setMovementMethod(new LinkMovementMethod());
						convertView.setTag(viewHolder_img4);
					}

					if (!TextUtils.isEmpty(flash_ZxImgInfo.getImage())) {
						viewHolder_img4.img4.setTag(flash_ZxImgInfo.getImage());
						viewHolder_img4.img4.setAlpha(1.33f);
						LoadImg(flash_ZxImgInfo.getImage(),
								viewHolder_img4.img4);
					} else {
						viewHolder_img4.img4.setVisibility(View.GONE);
					}
					viewHolder_img4.text4.setText(DateTimeUtil
							.FlashparseMillis(flash_ZxImgInfo.getTime() + "")
							.substring(11, 16));
					if (null != flash_ZxImgInfo.getTitle()
							|| null != flash_ZxImgInfo.getContent()) {
						boolean a = ReplaceString(flash_ZxImgInfo);
						// TODO
						viewHolder_img4.title4.setText(Html
								.fromHtml(ImageContent));
						if (!a) {
							LinkUrl(viewHolder_img4.title4, null);
						} else {
							LinkUrl(viewHolder_img4.title4,
									flash_ZxImgInfo.getTitle());
						}
						if (null != flash_ZxImgInfo.getUrl()) {
							SetALLink(viewHolder_img4.flash_rel4,
									flash_ZxImgInfo.getUrl().toString()
											.toLowerCase());
							if (!(flash_ZxImgInfo.getContent() + flash_ZxImgInfo
									.getTitle()).contains("<a")) {
								SetTextLink(viewHolder_img4.title4,
										flash_ZxImgInfo.getUrl().toString()
												.toLowerCase());
							}
						}
					} else {
						viewHolder_img4.title4.setText("(null)");
					}
					if (null != flash_ZxImgInfo.getImportance()
							&& flash_ZxImgInfo.getImportance().equals("高")) {
						viewHolder_img4.title4.setTextColor(Color.rgb(202, 70,
								57));
					}
				}

			} else {
				if (convertView != null
						&& convertView.getTag() instanceof ViewHolder_style) {
					viewHolder_style = (ViewHolder_style) convertView.getTag();
				} else {
					convertView = LayoutInflater.from(context).inflate(
							R.layout.fragment_flash_item2, null);
					viewHolder_style = new ViewHolder_style();
					viewHolder_style.date = (TextView) convertView
							.findViewById(R.id.lv_item_time); // findViewById()也会耗时！
					viewHolder_style.title = (TextView) convertView
							.findViewById(R.id.lv_item_brief);
					viewHolder_style.flash_rel_style = (LinearLayout) convertView
							.findViewById(R.id.flash_click_style);
					convertView.setTag(viewHolder_style); // 把这个实体类Bean绑过去
				}
				viewHolder_style.date.setText(DateTimeUtil.FlashparseMillis(
						flash_ZxImgInfo.getTime() + "").substring(11, 16));
				if (null != flash_ZxImgInfo.getTitle()
						|| null != flash_ZxImgInfo.getContent()) {
					boolean a = ReplaceString(flash_ZxImgInfo);
					// TODO
					viewHolder_style.title.setText(Html
							.fromHtml(ImageContent));
					if (!a) {
						LinkUrl(viewHolder_style.title, null);
					} else {
						LinkUrl(viewHolder_style.title,
								flash_ZxImgInfo.getTitle());
					}
					if (null != flash_ZxImgInfo.getUrl()) {
						SetALLink(viewHolder_style.flash_rel_style,
								flash_ZxImgInfo.getUrl().toString()
										.toLowerCase());
						if (!(flash_ZxImgInfo.getContent() + flash_ZxImgInfo
								.getTitle()).contains("<a")) {
							SetTextLink(viewHolder_style.title, flash_ZxImgInfo
									.getUrl().toString().toLowerCase());
						}
					}
				} else {
					viewHolder_style.title.setText("(null)");
				}
				if (null != flash_ZxImgInfo.getImportance()
						&& flash_ZxImgInfo.getImportance().equals("高")) {
					viewHolder_style.title.setTextColor(Color.rgb(202, 70, 57));
				}
			}
		} else if (mObj instanceof ZxInfo) {
			if (convertView != null
					&& convertView.getTag() instanceof ViewHolder_Zx) { // 这里根本就不会执行:
				holder_Zx = (ViewHolder_Zx) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.fragment_flash_item2, null);
				holder_Zx = new ViewHolder_Zx();
				holder_Zx.date = (TextView) convertView
						.findViewById(R.id.lv_item_time); // findViewById()也会耗时！
				holder_Zx.title = (TextView) convertView
						.findViewById(R.id.lv_item_brief);
				convertView.setTag(holder_Zx); // 把这个实体类Bean绑过去
			}
			// 赋值
			ZxInfo zx = (ZxInfo) mObj;
			holder_Zx.date.setText(DateTimeUtil.FlashparseMillis(
					zx.getTime() + "").substring(11, 16));
			boolean isStress = false;
			if (zx.getImportance().equals("高")) {
				isStress = true;
			}
			String title = zx.getTitle().replace("<br />", "\n"); // 标题title
			if (title.length() > 0) {
				holder_Zx.title.setText(title);
				if (isStress) {
					holder_Zx.title.setText(title);
					holder_Zx.title.setTextColor(Color.RED);
				} else {
					holder_Zx.title.setText(title);
					if (Isyj) {
						holder_Zx.title.setTextColor(Color
								.parseColor("#999999"));
					} else {
						holder_Zx.title.setTextColor(Color
								.parseColor("#666666"));
					}
				}
			} else {
				holder_Zx.title.setText("");
			}
		} else if (mObj instanceof Flash_CjInfo) {
			// 适配财经日历
			if (convertView != null
					&& convertView.getTag() instanceof ViewHolder_Cjrl) { // 先不用缓存
				holder_Cjrl = (ViewHolder_Cjrl) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.fragment_flash_main, null);
				holder_Cjrl = new ViewHolder_Cjrl();
				holder_Cjrl.tv_nfi = (TextView) convertView
						.findViewById(R.id.calendar_item_listview_version2_nfi);
				holder_Cjrl.tv_time = (TextView) convertView
						.findViewById(R.id.calendar_listview_item_tv_title_time);
				holder_Cjrl.tv_before = (TextView) convertView
						.findViewById(R.id.calendar_item_listview_version2_before);
				holder_Cjrl.tv_forecast = (TextView) convertView
						.findViewById(R.id.calendar_item_listview_version2_forecast);
				holder_Cjrl.tv_reality = (TextView) convertView
						.findViewById(R.id.calendar_item_listview_version2_gongbu);
				holder_Cjrl.tv_effect = (TextView) convertView
						.findViewById(R.id.calendar_item_listview_version2_4main_effect);
				holder_Cjrl.iv_state = (ImageView) convertView
						.findViewById(R.id.calendar_item_img_state); // 国旗
				holder_Cjrl.iv_nature = (ImageView) convertView
						.findViewById(R.id.calendar_item_nature); // 重要性
				holder_Cjrl.ll_effect_good_bad = (LinearLayout) convertView
						.findViewById(R.id.main_cjrl_item_listview_v2_good_bad);
				holder_Cjrl.tv_good = (TextView) convertView
						.findViewById(R.id.main_cjrl_item_listview_v2_effectgood_good);
				holder_Cjrl.tv_bad = (TextView) convertView
						.findViewById(R.id.main_cjrl_item_listview_v2_effectbad);
				// 利多利空中性
				holder_Cjrl.ll_effect_good = (LinearLayout) convertView
						.findViewById(R.id.main_cjrl_item_ll_effectgood);
				holder_Cjrl.ll_effect_mid = (LinearLayout) convertView
						.findViewById(R.id.main_cjrl_item_ll_effectmid);
				holder_Cjrl.tv_mind = (TextView) convertView
						.findViewById(R.id.main_cjrl_item_listview_v2_effectgood);
				holder_Cjrl.ll_effect_bad = (LinearLayout) convertView
						.findViewById(R.id.main_cjrl_item_ll_effectbad);
				convertView.setTag(holder_Cjrl);
			}

			// 设值
			Flash_CjInfo cjrl = (Flash_CjInfo) mObj;
			holder_Cjrl.tv_nfi.setText(cjrl.getState() + cjrl.getTitle());
			holder_Cjrl.tv_time.setText(DateTimeUtil.FlashparseMillis(
					"" + cjrl.getTime()).substring(11, 16));
			holder_Cjrl.tv_before.setText("前值:" + cjrl.getBefore());
			holder_Cjrl.tv_forecast.setText("预测:" + cjrl.getForecast());
			holder_Cjrl.tv_reality.setText("" + cjrl.getReality());

			String state = cjrl.getState(); // 国家
			if (state.equals(ConstantValue.STATE_CHINA)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_china);
			} else if (state.equals(ConstantValue.STATE_AMERICAN)) {
				holder_Cjrl.iv_state
						.setImageResource(R.drawable.state_american);
			} else if (state.equals(ConstantValue.STATE_GERMAN)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_german);
			} else if (state.equals(ConstantValue.STATE_ENGLAND)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_england);
			} else if (state.equals(ConstantValue.STATE_FRANCE)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_france);
			} else if (state.equals(ConstantValue.STATE_AUSTRALIA)) {
				holder_Cjrl.iv_state
						.setImageResource(R.drawable.state_australia);
			} else if (state.equals(ConstantValue.STATE_JAPAN)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_japan);
			} else if (state.equals(ConstantValue.STATE_KOREA)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_korea);
			} else if (state.equals(ConstantValue.STATE_CANADA)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_canada);
			} else if (state.equals(ConstantValue.STATE_HONGKONG)) {
				holder_Cjrl.iv_state
						.setImageResource(R.drawable.state_hongkong);
			} else if (state.equals(ConstantValue.STATE_SWITZERLAND)) {
				holder_Cjrl.iv_state
						.setImageResource(R.drawable.state_swizerland);
			} else if (state.equals(ConstantValue.STATE_ITALY)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_italy);
			} else if (state.equals(ConstantValue.STATE_EURO_AREA)) {
				holder_Cjrl.iv_state
						.setImageResource(R.drawable.state_europe_area);
			} else if (state.equals(ConstantValue.STATE_NEW_ZEALAND)) {
				holder_Cjrl.iv_state
						.setImageResource(R.drawable.state_newzealand);
			} else if (state.equals(ConstantValue.STATE_TAIWAN)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_taiwan);
			} else if (state.equals(ConstantValue.STATE_SPANISH)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_spanish);
			} else if (state.equals(ConstantValue.STATE_SINGAPORE)) {
				holder_Cjrl.iv_state
						.setImageResource(R.drawable.state_singapore);
			} else if (state.equals(ConstantValue.STATE_BRAZIL)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_brazil);
			} else if (state.equals(ConstantValue.STATE_SOUTH_AFRICA)) {
				holder_Cjrl.iv_state
						.setImageResource(R.drawable.state_south_africa);
			} else if (state.equals(ConstantValue.STATE_INDIA)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_india);
			} else if (state.equals(ConstantValue.STATE_INDONESIA)) {
				holder_Cjrl.iv_state
						.setImageResource(R.drawable.state_indonesia);
			} else if (state.equals(ConstantValue.STATE_RUSSIA)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_russia);
			} else if (state.equals(ConstantValue.STATE_GREECE)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_greece);
			} else if (state.equals(ConstantValue.STATE_ISRAEL)) {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_israel);
			} else {
				holder_Cjrl.iv_state.setImageResource(R.drawable.state_default);
				// 没有就设一个默认的
			}

			String nature = cjrl.getImportance(); // 重要性
			if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_HIGH)) {
				holder_Cjrl.iv_nature.setImageResource(R.drawable.nature_high);
			} else if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_MID)) {
				holder_Cjrl.iv_nature.setImageResource(R.drawable.nature_mid);
			} else if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_LOW)) {
				holder_Cjrl.iv_nature.setImageResource(R.drawable.nature_low);
			}
			// 利多利空-初始化时两个都设为View.GONE
			holder_Cjrl.ll_effect_good_bad.setVisibility(View.GONE);
			holder_Cjrl.tv_effect.setVisibility(View.GONE);
			holder_Cjrl.ll_effect_good.setVisibility(View.GONE);
			holder_Cjrl.ll_effect_mid.setVisibility(View.GONE);
			holder_Cjrl.tv_mind.setVisibility(View.GONE);
			holder_Cjrl.ll_effect_bad.setVisibility(View.GONE);
			// 值没错，写上去的时候出错了 aa ss|ss|
			String effectdata = cjrl.getEffect();
			String effectMid = "";
			String effectGood = "";
			String effectBad = "";
			if (cjrl.getEffect().equals("||")) {
				effectMid = "影响较小";
			} else {
				effectGood = effectdata.substring(0, effectdata.indexOf("|"));
				String b = effectdata.substring(effectdata.indexOf("|") + 1);
				effectBad = b.substring(0, b.indexOf("|"));
			}
			if (!TextUtils.isEmpty(effectMid)) {
				// 给mid设置
				holder_Cjrl.ll_effect_good_bad.setVisibility(View.VISIBLE);
				holder_Cjrl.ll_effect_mid.setVisibility(View.VISIBLE);
				holder_Cjrl.tv_mind.setVisibility(View.VISIBLE);
				holder_Cjrl.tv_mind.setText(effectMid);
			} else if ((!TextUtils.isEmpty(effectGood))
					|| (!TextUtils.isEmpty(effectBad))) {
				if (!TextUtils.isEmpty(effectGood)) {
					if (effectGood.contains(EFFECT_DOLLOR) && bool_dollor) {
						holder_Cjrl.ll_effect_good_bad
								.setVisibility(View.VISIBLE);
						holder_Cjrl.ll_effect_good.setVisibility(View.VISIBLE);
						holder_Cjrl.tv_good.setText(effectGood);
					} else if (effectGood.contains(EFFECT_GOLD_SILVER)
							&& effectGood.contains(EFFECT_OIL)) {
						if (bool_gold_silver && bool_oil) {
							holder_Cjrl.ll_effect_good_bad
									.setVisibility(View.VISIBLE);
							holder_Cjrl.ll_effect_good
									.setVisibility(View.VISIBLE);
							holder_Cjrl.tv_good.setText(effectGood);
						} else {
							// 啥也不做..
						}
					} else {
						// 啥也不做
					}
				}
				if (!TextUtils.isEmpty(effectBad)) {
					if (effectBad.contains(EFFECT_DOLLOR) && bool_dollor) {
						holder_Cjrl.ll_effect_good_bad
								.setVisibility(View.VISIBLE);
						holder_Cjrl.ll_effect_bad.setVisibility(View.VISIBLE);
						holder_Cjrl.tv_bad.setText(effectBad);
					} else if (effectBad.contains(EFFECT_GOLD_SILVER)
							&& effectBad.contains(EFFECT_OIL)) {
						if (bool_gold_silver && bool_oil) {
							holder_Cjrl.ll_effect_good_bad
									.setVisibility(View.VISIBLE);
							holder_Cjrl.ll_effect_bad
									.setVisibility(View.VISIBLE);
							holder_Cjrl.tv_bad.setText(effectBad);
						} else {
							// 啥也不做..
						}
					} else if (effectBad.contains(EFFECT_GOLD_SILVER)
							&& !effectBad.contains(EFFECT_OIL)
							&& bool_gold_silver) {
						holder_Cjrl.ll_effect_good_bad
								.setVisibility(View.VISIBLE);
						holder_Cjrl.ll_effect_bad.setVisibility(View.VISIBLE);
						holder_Cjrl.tv_bad.setText(EFFECT_GOLD_SILVER);
					} else if (!effectGood.contains(EFFECT_GOLD_SILVER)
							&& effectGood.contains(EFFECT_OIL) && bool_oil) {
						holder_Cjrl.ll_effect_good_bad
								.setVisibility(View.VISIBLE);
						holder_Cjrl.ll_effect_bad.setVisibility(View.VISIBLE);
						holder_Cjrl.tv_bad.setText(EFFECT_OIL);
					} else {
						// 啥也不做
					}
				}

			} else {
				holder_Cjrl.tv_effect.setVisibility(View.VISIBLE);
				String effect = "利空"; // 利多利空
				if (effect.contains("利空")) {
					holder_Cjrl.tv_effect.setText("" + effect);
					holder_Cjrl.tv_effect
							.setTextColor(Color.rgb(138, 194, 119));
					holder_Cjrl.tv_effect
							.setBackgroundResource(R.drawable.cjrl_effect_bg_green);
				} else if (effect.contains("利多")) {
					holder_Cjrl.tv_effect.setText("" + effect);
					holder_Cjrl.tv_effect.setTextColor(Color.rgb(202, 70, 57));
					holder_Cjrl.tv_effect
							.setBackgroundResource(R.drawable.cjrl_effect_bg_red);
				} else if (effect.length() > 0) {
					holder_Cjrl.tv_effect.setText("" + effect);
					holder_Cjrl.tv_effect.setTextColor(Color.rgb(243, 176, 86));
					holder_Cjrl.tv_effect
							.setBackgroundResource(R.drawable.cjrl_effect_bg_yellow);
				} else {
					holder_Cjrl.tv_effect.setVisibility(View.GONE);
				}
			}

		} else if (mObj instanceof String) { // 日期分隔符2014-08-20
			if (convertView != null
					&& convertView.getTag() instanceof ViewHolder_separator) { // 先不用缓存
				holder_separator = (ViewHolder_separator) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.fragment_flash_item_separator, null);
				holder_separator = new ViewHolder_separator();
				holder_separator.separator = (TextView) convertView
						.findViewById(R.id.main_separator_line_tv_date);
				convertView.setTag(mObj);
			}
			// 赋值
			String date_separator = (String) mObj;
			if (date_separator.length() > 3)
				holder_separator.separator.setText("以下资讯为：" + date_separator);
		}
		return convertView;
	}

	private void SetTextLink(TextView flash_rel, final String info) {
		flash_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (info.toLowerCase().contains("news")) {
					Intent intent = new Intent(context, DPWebActivity.class);
					intent.putExtra("url",
							NEWS_TITLE + info.replace("kxt://news", ""));
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("type", "news");
					context.startActivity(intent);
					handler.sendEmptyMessage(1);
				} else if (info.toLowerCase().contains("video")) {
					Intent intent = new Intent(context, PlayerActivity.class);
					intent.putExtra(
							"url",
							PlayerActivity.VideoUrl
									+ info.replace("kxt://video/", ""));
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					context.startActivity(intent);
					handler.sendEmptyMessage(4);
				} else if (info.toLowerCase().contains("chart")) {
					Intent intent = new Intent(context, ZXActivity.class);
					intent.putExtra("code", info.replace("kxt://chart/", ""));
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					context.startActivity(intent);
					handler.sendEmptyMessage(2);
				} else if (info.toLowerCase().contains("quotes")) {
					handler.sendEmptyMessage(2);
				} else if (info.toLowerCase().contains("rili")) {
					handler.sendEmptyMessage(3);
				} else if (info.toLowerCase().contains("http")) {
					Intent intent = new Intent(context, FlashLinkActiviy.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("code", info);
					context.startActivity(intent);
				}
			}
		});
	}

	private void SetALLink(LinearLayout flash_rel, final String info) {
		// TODO Auto-generated method stub
		flash_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (info.toLowerCase().contains("news")) {
					Intent intent = new Intent(context, DPWebActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("url",
							NEWS_TITLE + info.replace("kxt://news", ""));
					intent.putExtra("type", "news");
					context.startActivity(intent);
					handler.sendEmptyMessage(1);
				} else if (info.toLowerCase().contains("video")) {
					Intent intent = new Intent(context, PlayerActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra(
							"url",
							PlayerActivity.VideoUrl
									+ info.replace("kxt://video/", ""));
					context.startActivity(intent);
					handler.sendEmptyMessage(4);
				} else if (info.toLowerCase().contains("chart")) {
					Intent intent = new Intent(context, ZXActivity.class);
					intent.putExtra("code", info.replace("kxt://chart/", ""));
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					context.startActivity(intent);
					handler.sendEmptyMessage(2);
				} else if (info.toLowerCase().contains("quotes")) {
					handler.sendEmptyMessage(2);
				} else if (info.toLowerCase().contains("rili")) {
					handler.sendEmptyMessage(3);
				} else if (info.toLowerCase().contains("http")) {
					Intent intent = new Intent(context, FlashLinkActiviy.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("code", info);
					context.startActivity(intent);
				}
			}
		});
	}

	private void LoadImg(String url, SimpleDraweeView img) {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse(url);
		DraweeController draweeController = Fresco.newDraweeControllerBuilder()
				.setUri(uri).setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
				.build();
		((DraweeView<GenericDraweeHierarchy>) img)
				.setController(draweeController);
	}

	private boolean ReplaceString(Flash_ZxImgInfo flash_ZxImgInfo) {
		ImageContent = (flash_ZxImgInfo.getTitle() + "<br />" + flash_ZxImgInfo
				.getContent()).replace("<p>", "<br />");
		ImageContent = ImageContent.replace("</p>", "");
		if (flash_ZxImgInfo.getTitle().contains("<b>")) {
			return true;
		} else {
			return false;
		}
	}

	private void LinkUrl(TextView mTVText, String string) {
		mTVText.setMovementMethod(LinkMovementMethod.getInstance());
		CharSequence text = mTVText.getText();
		if (text instanceof Spannable) {
			int end = text.length();
			Spannable sp = (Spannable) mTVText.getText();
			URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
			SpannableStringBuilder style = new SpannableStringBuilder(text);
			style.clearSpans();// should clear old spans
			// style.clear();
			for (URLSpan url : urls) {
				MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
				style.setSpan(myURLSpan, sp.getSpanStart(url),
						sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (string != null) {
				style.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
						string.length()-7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			mTVText.setText(style);
		}
	}

	private class MyURLSpan extends ClickableSpan {

		private String mUrl;

		MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void onClick(View widget) {
			// TODO Auto-generated method stub
			if (mUrl.toLowerCase().contains("news")) {
				Intent intent = new Intent(context, DPWebActivity.class);
				intent.putExtra("url",
						NEWS_TITLE + mUrl.replace("kxt://news", ""));
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("type", "news");
				context.startActivity(intent);
				handler.sendEmptyMessage(1);
			} else if (mUrl.toLowerCase().contains("video")) {
				Intent intent = new Intent(context, PlayerActivity.class);
				intent.putExtra(
						"url",
						PlayerActivity.VideoUrl
								+ mUrl.replace("kxt://video/", ""));
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				context.startActivity(intent);
				handler.sendEmptyMessage(4);
			} else if (mUrl.toLowerCase().contains("chart")) {
				Intent intent = new Intent(context, ZXActivity.class);
				intent.putExtra("code", mUrl.replace("kxt://chart/", ""));
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				context.startActivity(intent);
				handler.sendEmptyMessage(2);
			} else if (mUrl.toLowerCase().contains("quotes")) {
				handler.sendEmptyMessage(2);
			} else if (mUrl.toLowerCase().contains("rili")) {
				handler.sendEmptyMessage(3);
			} else if (mUrl.toLowerCase().contains("http")) {
				Intent intent = new Intent(context, FlashLinkActiviy.class);
				intent.putExtra("code", mUrl);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				context.startActivity(intent);
			}
		}
	}
	class ViewHolder_separator {
		TextView separator; // 日期
	}

	class ViewHolder_Cjrl {
		TextView tv_nfi; // 指标名称
		TextView tv_time; // 时间
		TextView tv_before; // 前值
		TextView tv_forecast; // 预测值
		TextView tv_reality; // 公布值
		TextView tv_effect;

		// 增
		LinearLayout ll_effect_good_bad;
		LinearLayout ll_effect_good;
		LinearLayout ll_effect_mid;
		LinearLayout ll_effect_bad;

		TextView tv_good; // 利多
		TextView tv_bad; // 利空
		TextView tv_mind;// 影响较小

		ImageView iv_state; // 国旗
		ImageView iv_nature; // 重要性
	}

	class ViewHolder_Zx {
		TextView date; // 时间
		TextView title; // 新闻摘要
	}

	class ViewHolder_img1 {
		SimpleDraweeView img1;
		TextView text1;
		TextView title1;
		LinearLayout flash_rel1;
	}

	class ViewHolder_img2 {
		SimpleDraweeView img2;
		TextView title2;
		TextView text2;
		LinearLayout flash_rel2;
	}

	class ViewHolder_img3 {
		SimpleDraweeView img3;
		TextView text3;
		TextView title3;
		LinearLayout flash_rel3;
	}

	class ViewHolder_img4 {
		SimpleDraweeView img4;
		TextView text4;
		TextView title4;
		LinearLayout flash_rel4;
	}

	class ViewHolder_style {
		TextView date; // 时间
		TextView title; // 新闻摘要
		LinearLayout flash_rel_style;
	}
}
