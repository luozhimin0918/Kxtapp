package com.jyh.fragment;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.gson.bean.CjrlBeanInt;
import com.jyh.gson.bean.CjrlBeanStr;
import com.jyh.kxt.DataFLActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.customtool.ColumnHorizontalScrollView;
import com.jyh.kxt.customtool.SegmentView;
import com.jyh.kxt.customtool.SegmentView.onSegmentViewClickListener;
import com.jyh.kxt.customtool.Test_mian_ZDY;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.tool.BaseTools;
import com.jyh.tool.DateTimePickDialogUtil;
import com.jyh.tool.FastJsonRequest;

/**
 * 日历主界面
 *
 * @author PC
 */
public class fragment_data extends LazyFragment implements
        onSegmentViewClickListener {
    private WebView webView;
    private FrameLayout frame_rl, frame_rl_web;
    private LinearLayout ll_rl_title, ti_ll_data;
    private RelativeLayout out_data, relatview_reload, relative_rl_show;
    private TextView title_rl_tv, text_refresh;
    ;
    private String initEndDateTime = null; // 初始化结束时间
    private View view;
    private ImageView img_data;
    private RelativeLayout image_select_date;
    private SharedPreferences preferences;
    private Editor editor;
    private boolean Isyj = false;
    private SimpleDateFormat dformat;
    private String name;
    private boolean isChange = false;
    protected WeakReference<View> mRootView;
    private Context context;

    // zml
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;// 头部滚动条
    private LinearLayout mRadioGroup_content;
    private LinearLayout ll_more_columns;
    private int mScreenWidth = 0;// 屏幕宽度
    private int mItemWidth = 0;// item的宽度
    private Test_mian_ZDY columnTextView;
    private RelativeLayout rl_column;
    private int columnSelectIndex = 0;
    private int dateOfMonth;
    private int dateOfMonth_leftOrRight;
    private int year;
    private int month;
    private int currentMonth;
    private String monthsStr;
    private String dayStr;
    private int day;
    String[] months = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月",
            "十月", "十一月", "十二月"};
    private String url_date = "http://appapi.kxt.com/cjrl/getData";
    private String url_sj = "http://appapi.kxt.com/cjrl/getEvent";
    private String url_jq = "http://appapi.kxt.com/cjrl/getHoliday";
    private FragmentManager manager;
    private FragmentTransaction transaction;

    private List<Fragment> fragments = new ArrayList<Fragment>();
    private SegmentView segmentView;
    private RequestQueue requestQueue;
    private CjrlBeanStr cjrlBeanStr;
    private CjrlBeanInt cjrlBeanInt;
    private int tabSelect = 0;
    private TextView text_date;
    private Fragment_rl_data fragment_data;
    private KXTApplication app;
    private boolean isPrepared;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 10:
                    title_rl_tv.setText(name);
                    isChange = false;
                    break;
                case 20:
                    selectTab(columnSelectIndex);
                    break;
                case 30:
                    // 日历数据下拉刷新
                    isPrepared = true;
                    getRefresScrollDates();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = null;
        preferences = getActivity().getSharedPreferences("setup",
                Context.MODE_PRIVATE);
        Isyj = preferences.getBoolean("yj_btn", false);
        if (Isyj) {
            getActivity().setTheme(R.style.BrowserThemeNight);
        } else {
            getActivity().setTheme(R.style.BrowserThemeDefault);
        }
        if (mRootView == null || mRootView.get() == null) {
            view = inflater.inflate(R.layout.fragment_rl, null);
            setUserVisibleHint(true);
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
        segmentView = (SegmentView) view.findViewById(R.id.segment_rl);
        // rl_rela = (RelativeLayout) view.findViewById(R.id.rl_data);
        webView = (WebView) view.findViewById(R.id.rl_web);
        segmentView.changeBackGruond(Isyj);
        ll_rl_title = (LinearLayout) view.findViewById(R.id.ll_rl_title);
        frame_rl_web = (FrameLayout) view.findViewById(R.id.frame_rl_web);
        frame_rl = (FrameLayout) view.findViewById(R.id.frame_rl);
        ti_ll_data = (LinearLayout) view.findViewById(R.id.ti_ll_data);
        title_rl_tv = (TextView) view.findViewById(R.id.title_rl_tv);
        image_select_date = (RelativeLayout) view
                .findViewById(R.id.realtive_select_date);
        img_data = (ImageView) view.findViewById(R.id.img_data);
        text_date = (TextView) view.findViewById(R.id.text_cjrl_day);
        relatview_reload = (RelativeLayout) view
                .findViewById(R.id.relatview_reload);
        relative_rl_show = (RelativeLayout) view
                .findViewById(R.id.relative_rl_show);
        out_data = (RelativeLayout) view.findViewById(R.id.out_data);
        preferences = getActivity().getSharedPreferences("newdata",
                Context.MODE_PRIVATE);
        text_refresh = (TextView) view.findViewById(R.id.text_refresh);
        // 点击重新加载数据
        text_refresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showProgress();
                getScrollDates();
            }
        });
        editor = preferences.edit();

        // 设置默认显示progress
        showProgress();

        InitData();
        findScrollView();
    }

    public void findScrollView() {
        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) view
                .findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout) view
                .findViewById(R.id.mRadioGroup_content);
        ll_more_columns = (LinearLayout) view
                .findViewById(R.id.ll_more_columns);
        rl_column = (RelativeLayout) view.findViewById(R.id.rl_column);

        mScreenWidth = BaseTools.getWindowsWidth(getActivity());
        mItemWidth = mScreenWidth / 14;// 一个Item宽度为屏幕的1/16

        // getScrollDates();
        isPrepared = true;
        getScrollDates();
    }

    /**
     * 添加上个月的数据
     */
    @SuppressWarnings("deprecation")
    public void addScrollViewToLeft(final boolean isFirst) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = 5;
        params.rightMargin = mItemWidth;

        int index = 0;
        for (int i = 20; i < dateOfMonth_leftOrRight; i++) {
            columnTextView = new Test_mian_ZDY(getActivity(), false);
            columnTextView.setTextDate("" + (i + 1), 20, getResources()
                    .getColorStateList(R.color.hq_color_start));

            if (isFirst) {
                columnTextView.setTextMonth(months[11], 10, getResources()
                        .getColorStateList(R.color.hq_color_start));
                columnTextView.setTextWeek(getWeek(new Date(year - 1, 11, i)),
                        10,
                        getResources()
                                .getColorStateList(R.color.hq_color_start));

            } else {
                columnTextView.setTextMonth(months[month - 1], 10,
                        getResources()
                                .getColorStateList(R.color.hq_color_start));
                columnTextView.setTextWeek(
                        getWeek(new Date(year, month - 1, i)), 10,
                        getResources()
                                .getColorStateList(R.color.hq_color_start));

            }

            columnTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v) {
                            localView.setSelected(false);
                        } else {
                            localView.setSelected(true);
                            String selectDay = ((Test_mian_ZDY) localView)
                                    .getTextDate();

                            if (testNetWork()) {
                                showProgress();
                                dayStr = ((Test_mian_ZDY) localView)
                                        .getTextDate();
                                monthsStr = ((Test_mian_ZDY) localView)
                                        .getTextMonth();
                                day = Integer.valueOf(selectDay);

                                if (day < 10) {
                                    text_date.setText("0" + day);
                                } else {
                                    text_date.setText("" + day);
                                }
                                if (columnSelectIndex != i) {
                                    columnSelectIndex = i;
                                    selectTab(columnSelectIndex);
                                }
                                if (isFirst && monthsStr.equals("十二月")) {
                                    parserJsonDate(1);
                                } else if (isFirst && !monthsStr.equals("十二月")) {
                                    parserJsonDate(0);
                                } else {
                                    parserJsonDate(0);
                                }
                            } else {
                                showReload();
                            }

                        }
                    }
                }
            });

            mRadioGroup_content.addView(columnTextView, index, params);
            if (i != 0) {
                index++;
            }

        }
    }

    /**
     * 添加下个月的数据
     */
    @SuppressWarnings("deprecation")
    public void addScrollViewToRight(final boolean isLast) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = 5;
        params.rightMargin = mItemWidth;
        for (int i = 0; i <= 10; i++) {
            columnTextView = new Test_mian_ZDY(getActivity(), false);
            columnTextView.setTextDate("" + (i + 1), 20, getResources()
                    .getColorStateList(R.color.hq_color_start));

            if (isLast) {
                columnTextView.setTextMonth(months[0], 10, getResources()
                        .getColorStateList(R.color.hq_color_start));
                columnTextView.setTextWeek(getWeek(new Date(year + 1, 0, i)),
                        10,
                        getResources()
                                .getColorStateList(R.color.hq_color_start));
            } else {
                columnTextView.setTextMonth(months[month + 1], 10,
                        getResources()
                                .getColorStateList(R.color.hq_color_start));
                columnTextView.setTextWeek(
                        getWeek(new Date(year, month + 1, i)), 10,
                        getResources()
                                .getColorStateList(R.color.hq_color_start));
            }

            columnTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v) {
                            localView.setSelected(false);
                        } else {
                            localView.setSelected(true);
                            String selectDay = ((Test_mian_ZDY) localView)
                                    .getTextDate();
                            if (testNetWork()) {
                                showProgress();
                                dayStr = ((Test_mian_ZDY) localView)
                                        .getTextDate();
                                monthsStr = ((Test_mian_ZDY) localView)
                                        .getTextMonth();
                                day = Integer.valueOf(selectDay);

                                if (day < 10) {
                                    text_date.setText("0" + day);
                                } else {
                                    text_date.setText("" + day);
                                }
                                if (columnSelectIndex != i) {
                                    columnSelectIndex = i;
                                    selectTab(columnSelectIndex);
                                }
                                if (isLast && monthsStr.equals("一月")) {
                                    parserJsonDate(12);
                                } else if (isLast && !monthsStr.equals("一月")) {
                                    parserJsonDate(0);
                                } else {
                                    parserJsonDate(0);
                                }

                            } else {
                                showReload();

                            }

                        }
                    }
                }
            });

            mRadioGroup_content.addView(columnTextView, i, params);
        }
    }

    /**
     * 添加本月的数据
     */
    @SuppressWarnings("deprecation")
    public void addScrollView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = 5;
        params.rightMargin = mItemWidth;

        for (int i = 0; i < dateOfMonth; i++) {

            columnTextView = new Test_mian_ZDY(getActivity(), false);
            columnTextView.setId(i);
            columnTextView.setTextDate("" + (i + 1), 20, getResources()
                    .getColorStateList(R.color.hq_color_start));
            columnTextView.setTextMonth(months[month], 10, getResources()
                    .getColorStateList(R.color.hq_color_start));
            columnTextView.setTextWeek(getWeek(new Date(year, month, i)), 10,
                    getResources().getColorStateList(R.color.hq_color_start));

            if ((i + 1) == day && currentMonth == month) {
                columnTextView.setSelected(true);
                columnSelectIndex = i;
            }
            if ((i + 1) == day + 2 && currentMonth == month) {
                columnTextView.setFocusable(true);
                columnTextView.setFocusableInTouchMode(true);
                columnTextView.requestFocus();
            }
            columnTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v) {
                            localView.setSelected(false);
                        } else {
                            localView.setSelected(true);

                            String selectDay = ((Test_mian_ZDY) localView)
                                    .getTextDate();

                            if (testNetWork()) {
                                showProgress();

                                dayStr = ((Test_mian_ZDY) localView)
                                        .getTextDate();
                                monthsStr = ((Test_mian_ZDY) localView)
                                        .getTextMonth();
                                day = Integer.valueOf(selectDay);

                                if (day < 10) {
                                    text_date.setText("0" + day);
                                } else {
                                    text_date.setText("" + day);
                                }
                                if (columnSelectIndex != i) {
                                    columnSelectIndex = i;
                                    selectTab(columnSelectIndex);
                                }
                                parserJsonDate(0);
                            } else {
                                showReload();
                            }

                        }
                    }
                }
            });

            mRadioGroup_content.addView(columnTextView, i, params);
        }

    }

    /**
     * 显示progress,隐藏其他的view
     */
    public void showProgress() {
        frame_rl.setVisibility(View.GONE);
        relatview_reload.setVisibility(View.GONE);
        relative_rl_show.setVisibility(View.VISIBLE);
    }

    /**
     * 显示重新加载。隐藏其他的view
     */
    public void showReload() {
        frame_rl.setVisibility(View.GONE);
        relatview_reload.setVisibility(View.VISIBLE);
        relative_rl_show.setVisibility(View.GONE);
    }

    /**
     * 显示显示数据的View.隐藏其他的view
     */
    public void showDataView() {
        frame_rl.setVisibility(View.VISIBLE);
        relatview_reload.setVisibility(View.GONE);
        relative_rl_show.setVisibility(View.GONE);
    }

    /**
     * 初始化Scrollview
     */
    public void initScrollView() {
        mColumnHorizontalScrollView.setParam(getActivity(), mScreenWidth,
                mRadioGroup_content, null, null, ll_more_columns, rl_column);

        if (null != mRadioGroup_content
                && mRadioGroup_content.getChildCount() > 0) {
            mRadioGroup_content.removeAllViews();

        }
        currentMonth = month;

        if (month <= 0) {
            // 一月份
            if (day <= 10 && day >= 0) {
                // 加上上个月的10天日期
                addScrollView();
                // month--;
                setScrollDatasByMonth(11, year - 1);
                addScrollViewToLeft(true);
            } else if (day >= 20 && day <= 31) {
                // 加上下个月的10天日期
                setScrollDatasByMonth(0, year + 1);
                addScrollViewToRight(false);

                addScrollView();
            } else {
                addScrollView();
            }

        } else if (month >= 11) {
            // 十二月份
            if (day <= 10 && day >= 0) {
                // 加上上个月的10天日期
                addScrollView();
                setScrollDatasByMonth(11, year - 1);
                addScrollViewToLeft(false);
            } else if (day >= 20 && day <= 31) {
                // 加上下个月的10天日期
                setScrollDatasByMonth(0, year + 1);
                addScrollViewToRight(true);
                addScrollView();
            } else {
                addScrollView();
            }
        } else {
            // 其他
            if (day <= 10 && day >= 0) {
                // 加上上个月的10天日期
                addScrollView();
                setScrollDatasByMonth(month - 1, year);
                addScrollViewToLeft(false);
            } else if (day >= 20 && day <= 31) {
                // 加上下个月的10天日期
                setScrollDatasByMonth(month + 1, year);
                addScrollViewToRight(false);
                addScrollView();
            } else {
                addScrollView();
            }
        }
        handler.sendEmptyMessage(20);
    }

    public void setScrollDatas() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, 0);
        dateOfMonth = c.getActualMaximum(Calendar.DATE);
    }

    public void setScrollDatasByMonth(int m, int y) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, y);
        c.set(Calendar.MONTH, m + 1);
        c.set(Calendar.DATE, 0);
        dateOfMonth_leftOrRight = c.getActualMaximum(Calendar.DATE);
    }

    /**
     * 获取滚动条的数据
     */
    public void getScrollDates() {
        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DATE);
        month = cal.get(Calendar.MONTH);
        monthsStr = months[month];
        year = cal.get(Calendar.YEAR);
        dateOfMonth = cal.getActualMaximum(Calendar.DATE);

        dayStr = "" + day;
        monthsStr = months[month];
        if (day < 10) {
            text_date.setText("0" + day);
        } else {
            text_date.setText("" + day);
        }
        initScrollView();
        if (month <= 0) {
            if (monthsStr.equals("十二月")) {
                parserJsonDate(1);
            } else if (!monthsStr.equals("十二月")) {
                parserJsonDate(0);
            }

        } else if (month >= 11) {
            if (monthsStr.equals("一月")) {
                parserJsonDate(12);
            } else if (!monthsStr.equals("一月")) {
                parserJsonDate(0);
            }
        } else {
            parserJsonDate(0);
        }
        initFragmentShow();
    }

    /**
     * 刷新后获取滚动条的数据 。使用选中的时间
     */
    public void getRefresScrollDates() {
        monthsStr = months[month];
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        dateOfMonth = cal.getActualMaximum(Calendar.DATE);

        dayStr = "" + day;
        monthsStr = months[month];
        if (day < 10) {
            text_date.setText("0" + day);
        } else {
            text_date.setText("" + day);
        }
        initScrollView();
        if (month <= 0) {
            if (monthsStr.equals("十二月")) {
                parserJsonDate(1);
            } else if (!monthsStr.equals("十二月")) {
                parserJsonDate(0);
            }

        } else if (month >= 11) {
            if (monthsStr.equals("一月")) {
                parserJsonDate(12);
            } else if (!monthsStr.equals("一月")) {
                parserJsonDate(0);
            }
        } else {
            parserJsonDate(0);
        }
        initFragmentShow();
    }

    /**
     * 根据时间选择器获取滚动条的数据
     *
     * @param dateTimePicKDialog
     */
    public void regScrollDates(DateTimePickDialogUtil dateTimePicKDialog) {

        String dateTime = dateTimePicKDialog.getDateTime();
        year = Integer.valueOf(dateTime.substring(0, 4));
        month = Integer.valueOf(dateTime.substring(5, 7)) - 1;
        monthsStr = months[month];
        day = Integer.valueOf(dateTime.substring(8, 10));
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        dateOfMonth = cal.getActualMaximum(Calendar.DATE);
        dayStr = "" + day;
        monthsStr = months[month];
        if (day < 10) {
            text_date.setText("0" + day);
        } else {
            text_date.setText("" + day);
        }
        initScrollView();

        if (testNetWork()) {
            frame_rl.setVisibility(View.VISIBLE);
            relatview_reload.setVisibility(View.GONE);
            if (month <= 0) {
                if (monthsStr.equals("十二月")) {
                    parserJsonDate(1);
                } else if (!monthsStr.equals("十二月")) {
                    parserJsonDate(0);
                }

            } else if (month >= 11) {
                if (monthsStr.equals("一月")) {
                    parserJsonDate(12);
                } else if (!monthsStr.equals("一月")) {
                    parserJsonDate(0);
                }
            } else {
                parserJsonDate(0);
            }
        } else {
            frame_rl.setVisibility(View.GONE);
            relatview_reload.setVisibility(View.VISIBLE);
        }

    }

    // 根据日期取得星期几
    public String getWeek(Date date) {
        String[] weekDaysName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
                "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    /**
     * 测量View 并显示
     *
     * @param view
     * @param scrollView
     */
    private void measureView(final View view,
                             final ColumnHorizontalScrollView scrollView) {

        view.getViewTreeObserver().addOnGlobalLayoutListener(

                new OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {

                        int mViewWidth = view.getWidth();
                        int mViewLeft = view.getLeft();
                        int i22 = mViewLeft + mViewWidth / 2 - mScreenWidth / 2;// 确定点击后显示位置
                        scrollView.scrollTo(i22, 0);
                        view.setSelected(true);
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    }

                });
    }

    /**
     * scorllview的点击事件
     *
     * @param tab_postion
     */
    private void selectTab(int tab_postion) {

        if (null != mRadioGroup_content
                && mRadioGroup_content.getChildCount() > 0) {
            for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                Test_mian_ZDY cView = (Test_mian_ZDY) mRadioGroup_content
                        .getChildAt(i);

                if (cView.getTextDate().equals(dayStr)
                        && cView.getTextMonth().equals(monthsStr)) {

                    measureView(cView, mColumnHorizontalScrollView);
                    tab_postion = i;
                } else {
                    cView.setSelected(false);
                }

            }
            // 判断是否选中

            for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
                View checkView = mRadioGroup_content.getChildAt(j);
                boolean ischeck;
                if (j == tab_postion) {
                    ischeck = true;
                } else {
                    ischeck = false;
                }
                checkView.setSelected(ischeck);

            }

        }

    }

    private void InitData() {
        app = (KXTApplication) getActivity().getApplication();
        if (app.getQueue() != null) {
            requestQueue = app.getQueue();
        } else {
            requestQueue = Volley.newRequestQueue(getActivity());
        }
        segmentView.setListener(this);
        fragments.clear();
        manager = getActivity().getSupportFragmentManager();
        fragment_data = new Fragment_rl_data();
        app.setFragment(fragment_data);
        fragments.add(fragment_data);
        fragments.add(Fragment_rl_sj.getInstance());
        fragments.add(Fragment_rl_jq.getInstance());
        dformat = new SimpleDateFormat("yyyy-MM-dd");
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);// 是否支持JavaScript
        webView.getSettings().setBuiltInZoomControls(true);//
        webView.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkImage(true);

        dformat.format(new Date());
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy年MM月dd日");
        initEndDateTime = df1.format(new Date());

        image_select_date.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // String lastdata = preferences.getString("calendar",
                // initEndDateTime);

                // if (lastdata.equals(initEndDateTime)) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        getActivity(), fragment_data.this, initEndDateTime,
                        Isyj);
                dateTimePicKDialog.dateTimePicKDialog(editor);

                // } else {
                // DateTimePickDialogUtil dateTimePicKDialog = new
                // DateTimePickDialogUtil(
                // getActivity(), fragment_data.this, lastdata, Isyj);
                // dateTimePicKDialog.dateTimePicKDialog(editor);
                // }
            }
        });

        img_data.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), DataFLActivity.class);
                startActivityForResult(intent, 120);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
            }
        });
        out_data.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                image_select_date.setVisibility(View.VISIBLE);
                ti_ll_data.setVisibility(View.VISIBLE);
                ll_rl_title.setVisibility(View.GONE);
                frame_rl_web.setVisibility(View.GONE);
                rl_column.setVisibility(View.VISIBLE);
                frame_rl.setVisibility(View.VISIBLE);
                out_data.setVisibility(View.GONE);
                img_data.setVisibility(View.VISIBLE);
            }
        });

    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setBlockNetworkImage(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
            webView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 120 & resultCode == 101) {
            name = data.getStringExtra("name");
            String url = data.getStringExtra("uri");
            if (name.equals("财经日历")) {
                image_select_date.setVisibility(View.VISIBLE);
                ti_ll_data.setVisibility(View.VISIBLE);
                ll_rl_title.setVisibility(View.GONE);
                frame_rl_web.setVisibility(View.GONE);
                rl_column.setVisibility(View.VISIBLE);
                frame_rl.setVisibility(View.VISIBLE);
                out_data.setVisibility(View.GONE);
            } else {
                img_data.setVisibility(View.INVISIBLE);
                title_rl_tv.setVisibility(View.VISIBLE);
                ti_ll_data.setVisibility(View.GONE);
                image_select_date.setVisibility(View.GONE);
                out_data.setVisibility(View.VISIBLE);
                ll_rl_title.setVisibility(View.VISIBLE);
                frame_rl_web.setVisibility(View.VISIBLE);
                rl_column.setVisibility(View.GONE);
                frame_rl.setVisibility(View.GONE);
                if (Isyj) {
                    webView.loadUrl(url + "?yejian=1");
                } else {
                    webView.loadUrl(url);
                }
                isChange = true;
            }
        }
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (isChange) {
            title_rl_tv.setText("");
            handler.sendEmptyMessage(10);
        }
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        requestQueue.cancelAll(this);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        tabSelect = 0;

        System.gc();
    }

    @Override
    public void onSegmentViewClick(View v, int position) {
        // TODO Auto-generated method stub
        showProgress();
        switch (position) {
            case 0:// 数据

                chageFragments(0);
                tabSelect = 0;
                break;
            case 1:// 事件
                chageFragments(1);
                tabSelect = 1;
                break;
            case 2:// 假期
                chageFragments(2);
                tabSelect = 2;
                break;
            default:
                break;
        }
        if (month <= 0) {
            if (monthsStr.equals("十二月")) {
                parserJsonDate(1);
            } else if (!monthsStr.equals("十二月")) {
                parserJsonDate(0);
            }

        } else if (month >= 11) {
            if (monthsStr.equals("一月")) {
                parserJsonDate(12);
            } else if (!monthsStr.equals("一月")) {
                parserJsonDate(0);
            }
        } else {
            parserJsonDate(0);
        }

    }

    /**
     * 根据日期解析财经日历的数据 flag 1 代表1月份 12代表12月份
     */
    @SuppressWarnings("deprecation")
    public void parserJsonDate(int flag) {

        String url = null;

        int tMonth = 0;
        int tYear = 0;
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(monthsStr)) {
                tMonth = i;
            }
        }

        switch (flag) {
            case 1:
                tYear = year - 1;
                break;
            case 12:
                tYear = year + 1;
                break;

            case 0:
                tYear = year;
                break;
        }
        switch (tabSelect) {
            case 0:
                url = url_date + "?date="
                        + dformat.format(new Date(tYear - 1900, tMonth, day));

                @SuppressWarnings("rawtypes")
                FastJsonRequest<CjrlBeanInt> fastJsonRequest0 = new FastJsonRequest<CjrlBeanInt>(
                        Request.Method.GET, url, CjrlBeanInt.class,
                        new Response.Listener() {

                            @Override
                            public void onResponse(Object arg0) {
                                // TODO Auto-generated method stub
                                if (null != arg0) {
                                    showDataView();
                                    cjrlBeanInt = (CjrlBeanInt) arg0;
                                    fragment_data.setDatas(cjrlBeanInt.getData(),
                                            handler);

                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        showReload();
                    }
                });
                fastJsonRequest0.setTag(this);
                requestQueue.add(fastJsonRequest0);
                break;
            case 1:
                url = url_sj + "?date="
                        + dformat.format(new Date(tYear - 1900, tMonth, day));

                @SuppressWarnings("rawtypes")
                FastJsonRequest<CjrlBeanStr> fastJsonRequest1 = new FastJsonRequest<CjrlBeanStr>(
                        Request.Method.GET, url, CjrlBeanStr.class,
                        new Response.Listener() {

                            @Override
                            public void onResponse(Object arg0) {
                                // TODO Auto-generated method stub
                                if (null != arg0) {
                                    showDataView();
                                    cjrlBeanStr = (CjrlBeanStr) arg0;
                                    Fragment_rl_sj.getInstance().setDatas(
                                            cjrlBeanStr.getData());

                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        showReload();
                    }
                });
                fastJsonRequest1.setTag(this);
                requestQueue.add(fastJsonRequest1);
                break;
            case 2:
                url = url_jq + "?date="
                        + dformat.format(new Date(tYear - 1900, tMonth, day));
                @SuppressWarnings("rawtypes")
                FastJsonRequest<CjrlBeanStr> fastJsonRequest2 = new FastJsonRequest<CjrlBeanStr>(
                        Request.Method.GET, url, CjrlBeanStr.class,
                        new Response.Listener() {

                            @Override
                            public void onResponse(Object arg0) {
                                // TODO Auto-generated method stub
                                if (null != arg0) {
                                    showDataView();

                                    cjrlBeanStr = (CjrlBeanStr) arg0;
                                    Fragment_rl_jq.getInstance().setDatas(
                                            cjrlBeanStr.getData());

                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        showReload();
                    }
                });
                fastJsonRequest2.setTag(this);
                requestQueue.add(fastJsonRequest2);
                break;
            default:
                break;
        }

    }

    /**
     * 显示默认的fragment
     */
    public void initFragmentShow() {
        transaction = manager.beginTransaction();
        if (null != fragments && fragments.size() > 0) {
            if (!fragments.get(0).isAdded() && !fragments.get(0).isHidden()) {
                transaction.add(R.id.frame_rl, fragments.get(0))
                        .commitAllowingStateLoss();
            }

        }
    }

    /**
     * 切换fragment
     *
     * @param index
     */
    public void chageFragments(int index) {
        for (int i = 0; i < fragments.size(); i++) {

            if (fragments.get(i).isAdded()) {
                transaction = manager.beginTransaction();
                transaction.hide(fragments.get(i)).commitAllowingStateLoss();
            }
        }
        if (fragments.get(index).isAdded() && fragments.get(index).isHidden()) {
            transaction = manager.beginTransaction();
            transaction.show(fragments.get(index)).commitAllowingStateLoss();
        } else if (!fragments.get(index).isAdded()) {

            transaction = manager.beginTransaction();
            transaction.add(R.id.frame_rl, fragments.get(index))
                    .commitAllowingStateLoss();
        }
    }

    // 测试网络是否连接
    public boolean testNetWork() {

        if (!NetworkCenter.checkNetworkConnection(context)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    protected void lazyLoad() {
        // TODO Auto-generated method stub

        if (!isPrepared || !isVisible) {
            return;
        }
        getRefresScrollDates();

        isPrepared = false;
    }

}
