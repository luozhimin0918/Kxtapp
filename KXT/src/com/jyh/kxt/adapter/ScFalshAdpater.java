package com.jyh.kxt.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.gson.bean.Flash_CjInfo;
import com.jyh.kxt.R;
import com.jyh.kxt.customtool.HXListView;
import com.jyh.kxt.socket.ConstantValue;
import com.jyh.tool.DateTimeUtil;
//import com.jyh.tool.ImageDownLoader;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ScFalshAdpater extends BaseAdapter implements OnScrollListener {

	private static final int TYPE_KX_VIEW = 0;// 快讯
	private static final int TYPE_CJ_VIEW = 1;// 财经
	public static final String EFFECT_DOLLOR = "美元";
	public static final String EFFECT_GOLD_SILVER = "金银";
	public static final String EFFECT_OIL = "石油";
	private boolean bool_dollor; // 美元
	private boolean bool_gold_silver; // 金银
	private boolean bool_oil; // 石油

	// private ImageDownLoader loader;
	// 填充数据的list
	private List<Flash_CjInfo> scBeans;
	// 用来控制CheckBox的选中状况
	private static HashMap<Integer, Boolean> isSelected;
	// 上下文
	private Context context;
	// 用来导入布局
	private LayoutInflater inflater = null;
	/** 判定是否第一次加载 */
	private boolean isFirstEnter = true;
	/** 第一张可见Item下标 */
	private int firstVisibleItem;
	/** 每屏Item可见数 */
	private int visibleItemCount;
	private HXListView listView;
	private boolean IsShow = false;
	private boolean Isyj = false;
	private SharedPreferences preferences;
	public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径
	// 异步加载图片
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;

	// 构造器
	public ScFalshAdpater(Context context, List<Flash_CjInfo> scBeans,
			HXListView listView) {
		this.context = context;
		this.scBeans = scBeans;
		this.listView = listView;
		// loader = new ImageDownLoader(context);
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		preferences = context.getSharedPreferences("setup",
				Context.MODE_PRIVATE);
		Isyj = preferences.getBoolean("yj_btn", false);
		// 初始化数据
		initDate();
		initImageLoader();
		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.empty_photo)
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY).build();
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

	// 初始化isSelected的数据
	public void initDate() {
		bool_dollor = true;
		bool_gold_silver = true;
		bool_oil = true;
		for (int i = 0; i < scBeans.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (scBeans.get(position).getType().equals("2")) {
			return TYPE_CJ_VIEW;
		} else {
			return TYPE_KX_VIEW;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return scBeans.size();
	}

	@Override
	public Object getItem(int position) {
		return scBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		KxViewHolder kxHolder = null;
		CjViewHolder cjHolder = null;
		if (convertView == null) {
			switch (type) {
			case TYPE_KX_VIEW:
				kxHolder = new KxViewHolder();
				convertView = inflater
						.inflate(R.layout.item_sc_kx, null, false);
				kxHolder.title = (TextView) convertView
						.findViewById(R.id.sc_kx_text);
				kxHolder.time = (TextView) convertView
						.findViewById(R.id.sc_kx_time);
				kxHolder.check_sc = (CheckBox) convertView
						.findViewById(R.id.check_sc);
				kxHolder.sc_yw_lin = (LinearLayout) convertView
						.findViewById(R.id.sc_yw_lin);
				convertView.setTag(kxHolder);
				break;
			case TYPE_CJ_VIEW:
				cjHolder = new CjViewHolder();
				convertView = inflater
						.inflate(R.layout.item_sc_cj, null, false);
				cjHolder.tv_nfi = (TextView) convertView
						.findViewById(R.id.calendar_item_listview_version2_nfi);
				cjHolder.tv_time = (TextView) convertView
						.findViewById(R.id.calendar_listview_item_tv_title_time);
				cjHolder.tv_before = (TextView) convertView
						.findViewById(R.id.calendar_item_listview_version2_before);
				cjHolder.tv_forecast = (TextView) convertView
						.findViewById(R.id.calendar_item_listview_version2_forecast);
				cjHolder.tv_reality = (TextView) convertView
						.findViewById(R.id.calendar_item_listview_version2_gongbu);
				cjHolder.tv_effect = (TextView) convertView
						.findViewById(R.id.calendar_item_listview_version2_4main_effect);
				cjHolder.iv_state = (ImageView) convertView
						.findViewById(R.id.calendar_item_img_state); // 国旗
				cjHolder.iv_nature = (ImageView) convertView
						.findViewById(R.id.calendar_item_nature); // 重要性
				cjHolder.ll_effect_good_bad = (LinearLayout) convertView
						.findViewById(R.id.main_cjrl_item_listview_v2_good_bad);
				cjHolder.tv_good = (TextView) convertView
						.findViewById(R.id.main_cjrl_item_listview_v2_effectgood_good);
				cjHolder.tv_bad = (TextView) convertView
						.findViewById(R.id.main_cjrl_item_listview_v2_effectbad);
				// 利多利空中性
				cjHolder.ll_effect_good = (LinearLayout) convertView
						.findViewById(R.id.main_cjrl_item_ll_effectgood);
				cjHolder.ll_effect_mid = (LinearLayout) convertView
						.findViewById(R.id.main_cjrl_item_ll_effectmid);
				cjHolder.tv_mind = (TextView) convertView
						.findViewById(R.id.main_cjrl_item_listview_v2_effectgood);
				cjHolder.ll_effect_bad = (LinearLayout) convertView
						.findViewById(R.id.main_cjrl_item_ll_effectbad);
				cjHolder.check_sc = (CheckBox) convertView
						.findViewById(R.id.check_sc);
				cjHolder.sc_yw_lin = (LinearLayout) convertView
						.findViewById(R.id.sc_yw_lin);
				convertView.setTag(cjHolder);

				break;
			default:
				break;
			}

		} else {
			switch (type) {
			case TYPE_KX_VIEW:
				kxHolder = (KxViewHolder) convertView.getTag();
				break;

			case TYPE_CJ_VIEW:
				cjHolder = (CjViewHolder) convertView.getTag();

				break;

			default:
				break;
			}

		}
		// 设置资源

		switch (type) {

		case TYPE_KX_VIEW:
			if (!IsShow) {
				kxHolder.sc_yw_lin.setVisibility(View.GONE);
			} else {
				kxHolder.sc_yw_lin.setVisibility(View.VISIBLE);
			}
			String isStriess = scBeans.get(position).getImportance();
			if (isStriess.equals("高")) {
				kxHolder.title.setTextColor(Color.RED);
			} else {
				if (Isyj) {
					kxHolder.title.setTextColor(Color.parseColor("#999999"));
				} else {
					kxHolder.title.setTextColor(Color.parseColor("#666666"));
				}
			}
			kxHolder.title.setText(scBeans.get(position).getTitle());
			kxHolder.time.setText(DateTimeUtil.parseMillis2(""+scBeans.get(position).getTime()));
			// 根据isSelected来设置checkbox的选中状况
			if (getIsSelected().size() > position) {
				if (null != getIsSelected()) {
					kxHolder.check_sc.setChecked(getIsSelected().get(position));
				}
			} else {
				initDate();
				kxHolder.check_sc.setChecked(getIsSelected().get(position));
			}
			break;

		case TYPE_CJ_VIEW:

			if (!IsShow) {
				cjHolder.sc_yw_lin.setVisibility(View.GONE);
			} else {
				cjHolder.sc_yw_lin.setVisibility(View.VISIBLE);
			}
			Flash_CjInfo cjrl = scBeans.get(position);
			cjHolder.tv_nfi.setText(cjrl.getTitle());
			cjHolder.tv_time.setText(DateTimeUtil.parseMillis2(""+cjrl.getTime()));
			cjHolder.tv_before.setText("前值:" + cjrl.getBefore());
			cjHolder.tv_forecast.setText("预测:" + cjrl.getForecast());
			cjHolder.tv_reality.setText("" + cjrl.getReality());

			String state = cjrl.getState(); // 国家
			if (state.equals(ConstantValue.STATE_CHINA)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_china);
			} else if (state.equals(ConstantValue.STATE_AMERICAN)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_american);
			} else if (state.equals(ConstantValue.STATE_GERMAN)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_german);
			} else if (state.equals(ConstantValue.STATE_ENGLAND)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_england);
			} else if (state.equals(ConstantValue.STATE_FRANCE)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_france);
			} else if (state.equals(ConstantValue.STATE_AUSTRALIA)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_australia);
			} else if (state.equals(ConstantValue.STATE_JAPAN)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_japan);
			} else if (state.equals(ConstantValue.STATE_KOREA)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_korea);
			} else if (state.equals(ConstantValue.STATE_CANADA)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_canada);
			} else if (state.equals(ConstantValue.STATE_HONGKONG)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_hongkong);
			} else if (state.equals(ConstantValue.STATE_SWITZERLAND)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_swizerland);
			} else if (state.equals(ConstantValue.STATE_ITALY)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_italy);
			} else if (state.equals(ConstantValue.STATE_EURO_AREA)) {
				cjHolder.iv_state
						.setImageResource(R.drawable.state_europe_area);
			} else if (state.equals(ConstantValue.STATE_NEW_ZEALAND)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_newzealand);
			} else if (state.equals(ConstantValue.STATE_TAIWAN)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_taiwan);
			} else if (state.equals(ConstantValue.STATE_SPANISH)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_spanish);
			} else if (state.equals(ConstantValue.STATE_SINGAPORE)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_singapore);
			} else if (state.equals(ConstantValue.STATE_BRAZIL)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_brazil);
			} else if (state.equals(ConstantValue.STATE_SOUTH_AFRICA)) {
				cjHolder.iv_state
						.setImageResource(R.drawable.state_south_africa);
			} else if (state.equals(ConstantValue.STATE_INDIA)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_india);
			} else if (state.equals(ConstantValue.STATE_INDONESIA)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_indonesia);
			} else if (state.equals(ConstantValue.STATE_RUSSIA)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_russia);
			} else if (state.equals(ConstantValue.STATE_GREECE)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_greece);
			} else if (state.equals(ConstantValue.STATE_ISRAEL)) {
				cjHolder.iv_state.setImageResource(R.drawable.state_israel);
			} else {
				cjHolder.iv_state.setImageResource(R.drawable.state_default);
				// 没有就设一个默认的
			}

			String nature = cjrl.getImportance(); // 重要性
			if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_HIGH)) {
				cjHolder.iv_nature.setImageResource(R.drawable.nature_high);
			} else if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_MID)) {
				cjHolder.iv_nature.setImageResource(R.drawable.nature_mid);
			} else if (nature.equals(ConstantValue.CJRL_IMPORTANTANCE_LOW)) {
				cjHolder.iv_nature.setImageResource(R.drawable.nature_low);
			}
			// 利多利空-初始化时两个都设为View.GONE
			cjHolder.ll_effect_good_bad.setVisibility(View.GONE);
			cjHolder.tv_effect.setVisibility(View.GONE);

			cjHolder.ll_effect_good.setVisibility(View.GONE);
			cjHolder.ll_effect_mid.setVisibility(View.GONE);
			cjHolder.tv_mind.setVisibility(View.GONE);
			cjHolder.ll_effect_bad.setVisibility(View.GONE);
			// 值没错，写上去的时候出错了
			String effectMid = cjrl.getEffectMid();
			String effectGood = cjrl.getEffectGood();
			String effectBad = cjrl.getEffectBad();
			if (!TextUtils.isEmpty(effectMid)) {
				// 给mid设置
				cjHolder.ll_effect_good_bad.setVisibility(View.VISIBLE);
				cjHolder.ll_effect_mid.setVisibility(View.VISIBLE);
				cjHolder.tv_mind.setVisibility(View.VISIBLE);
				cjHolder.tv_mind.setText(effectMid);

			} else if ((!TextUtils.isEmpty(effectGood))
					|| (!TextUtils.isEmpty(effectBad))) {
				if (!TextUtils.isEmpty(effectGood)) {
					if (effectGood.contains(EFFECT_DOLLOR) && bool_dollor) {
						cjHolder.ll_effect_good_bad.setVisibility(View.VISIBLE);
						cjHolder.ll_effect_good.setVisibility(View.VISIBLE);
						cjHolder.tv_good.setText(effectGood);
					} else if (effectGood.contains(EFFECT_GOLD_SILVER)
							&& effectGood.contains(EFFECT_OIL)) {
						if (bool_gold_silver && bool_oil) {
							cjHolder.ll_effect_good_bad
									.setVisibility(View.VISIBLE);
							cjHolder.ll_effect_good.setVisibility(View.VISIBLE);
							cjHolder.tv_good.setText(effectGood);
						} else {
							// 啥也不做..
						}
					} else {
						// 啥也不做
					}
				}

				if (!TextUtils.isEmpty(effectBad)) {
					if (effectBad.contains(EFFECT_DOLLOR) && bool_dollor) {
						cjHolder.ll_effect_good_bad.setVisibility(View.VISIBLE);
						cjHolder.ll_effect_bad.setVisibility(View.VISIBLE);
						cjHolder.tv_bad.setText(effectBad);
					} else if (effectBad.contains(EFFECT_GOLD_SILVER)
							&& effectBad.contains(EFFECT_OIL)) {
						if (bool_gold_silver && bool_oil) {
							cjHolder.ll_effect_good_bad
									.setVisibility(View.VISIBLE);
							cjHolder.ll_effect_bad.setVisibility(View.VISIBLE);
							cjHolder.tv_bad.setText(effectBad);
						} else {
							// 啥也不做..
						}
					} else if (effectBad.contains(EFFECT_GOLD_SILVER)
							&& !effectBad.contains(EFFECT_OIL)
							&& bool_gold_silver) {
						cjHolder.ll_effect_good_bad.setVisibility(View.VISIBLE);
						cjHolder.ll_effect_bad.setVisibility(View.VISIBLE);
						cjHolder.tv_bad.setText(EFFECT_GOLD_SILVER);
					} else if (!effectGood.contains(EFFECT_GOLD_SILVER)
							&& effectGood.contains(EFFECT_OIL) && bool_oil) {
						cjHolder.ll_effect_good_bad.setVisibility(View.VISIBLE);
						cjHolder.ll_effect_bad.setVisibility(View.VISIBLE);
						cjHolder.tv_bad.setText(EFFECT_OIL);
					} else {
						// 啥也不做
					}
				}

			} else {
				cjHolder.tv_effect.setVisibility(View.VISIBLE);
				String effect = cjrl.getEffect(); // 利多利空
				if (effect.contains("利空")) {
					cjHolder.tv_effect.setText("" + effect);
					cjHolder.tv_effect.setTextColor(Color.rgb(138, 194, 119));
					cjHolder.tv_effect
							.setBackgroundResource(R.drawable.cjrl_effect_bg_green);
				} else if (effect.contains("利多")) {
					cjHolder.tv_effect.setText("" + effect);
					cjHolder.tv_effect.setTextColor(Color.rgb(202, 70, 57));
					cjHolder.tv_effect
							.setBackgroundResource(R.drawable.cjrl_effect_bg_red);
				} else if (effect.length() > 0) {
					cjHolder.tv_effect.setText("" + effect);
					cjHolder.tv_effect.setTextColor(Color.rgb(243, 176, 86));
					cjHolder.tv_effect
							.setBackgroundResource(R.drawable.cjrl_effect_bg_yellow);
				} else {
					cjHolder.tv_effect.setVisibility(View.GONE);
				}
			}
			if (getIsSelected().size() > position) {
				if (null != getIsSelected()) {
					cjHolder.check_sc.setChecked(getIsSelected().get(position));
				}
			} else {
				initDate();
				cjHolder.check_sc.setChecked(getIsSelected().get(position));
			}

			break;

		default:
			break;
		}

		return convertView;
	}

	public List<Flash_CjInfo> getScBeans() {
		return scBeans;
	}

	public void setScBeans(List<Flash_CjInfo> scBeans) {
		this.scBeans = scBeans;
		initDate();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 当停止滚动时，加载图片
		if (scrollState == SCROLL_STATE_IDLE) {
			loadImage(firstVisibleItem, visibleItemCount);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		this.visibleItemCount = visibleItemCount;
		if (isFirstEnter && visibleItemCount > 0) {
			loadImage(firstVisibleItem, visibleItemCount);
			isFirstEnter = false;
		}
	}

	/**
	 * 加载图片，若缓存中没有，则根据url下载
	 * 
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	private void loadImage(int firstVisibleItem, int visibleItemCount) {
		try {
			for (int i = firstVisibleItem; i < firstVisibleItem
					+ visibleItemCount; i++) {
				String url = null; // = scBeans.get(i).getThumb();
				final ImageView imageView = (ImageView) listView
						.findViewWithTag(url);
				mImageLoader.displayImage(url, imageView, options);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		ScFalshAdpater.isSelected = isSelected;
	}

	public boolean isIsShow() {
		return IsShow;
	}

	public void setIsShow(boolean isShow) {
		IsShow = isShow;
	}

	public static class KxViewHolder {
		public TextView title;
		public TextView time;
		public CheckBox check_sc;
		public LinearLayout sc_yw_lin;

	}

	public static class CjViewHolder {
		public TextView tv_nfi; // 指标名称
		public TextView tv_time; // 时间
		public TextView tv_before; // 前值
		public TextView tv_forecast; // 预测值
		public TextView tv_reality; // 公布值
		public TextView tv_effect;

		// 增
		public LinearLayout ll_effect_good_bad;
		public LinearLayout ll_effect_good;
		public LinearLayout ll_effect_mid;
		public LinearLayout ll_effect_bad;

		public TextView tv_good; // 利多
		public TextView tv_bad; // 利空
		public TextView tv_mind;// 影响较小

		public ImageView iv_state; // 国旗
		public ImageView iv_nature; // 重要性

		public CheckBox check_sc;
		public LinearLayout sc_yw_lin;

	}

}
