package com.jyh.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jyh.bean.ChannelItem;
import com.jyh.kxt.FLActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.adapter.NewsFragmentPagerAdapter;
import com.jyh.kxt.customtool.ColumnHorizontalScrollView;
import com.jyh.kxt.customtool.JW_mian_ZDY;
import com.jyh.kxt.jwitem.Item_Fragment;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.tool.BaseTools;

/**
 * 要闻界面
 *
 * @author PC
 */
public class fragment_yw extends Fragment {
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    LinearLayout mRadioGroup_content;
    LinearLayout ll_more_columns;
    RelativeLayout rl_column;
    private ViewPager mViewPager;
    private ImageView button_more_columns;
    private ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();//要闻数据列表
    /**
     * 当前选中的栏目
     */
    private int columnSelectIndex = 0;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth = 0;
    /**
     * Item宽度
     */
    private int mItemWidth = 0;
    private JW_mian_ZDY columnTextView;
    private View view;
    public final static int CHANNELREQUEST = 1;
    /**
     * 调整返回的RESULTCODE
     */
    public final static int CHANNELRESULT = 10;

    /**
     * jw_fragment_item
     */
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();//存放子fragment
    private KXTApplication application;
    private SharedPreferences preferences;
    private boolean Isyj = false;
    protected WeakReference<View> mRootView;
    private RelativeLayout yw_frame;
    private TextView text_refresh;
    private LinearLayout jw_mian_ll;
    private RequestQueue queue;
    private static final String TagURL = "http://appapi.kxt.com/data/tags";// 获取要闻栏目

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("setup",
                Context.MODE_PRIVATE);
        Isyj = preferences.getBoolean("yj_btn", false);
        if (Isyj) {
            getActivity().setTheme(R.style.BrowserThemeNight);
        } else {
            getActivity().setTheme(R.style.BrowserThemeDefault);
        }
        mScreenWidth = BaseTools.getWindowsWidth(getActivity());
        mItemWidth = mScreenWidth / 16;// 一个Item宽度为屏幕的1/16
        if (mRootView == null || mRootView.get() == null) {
            view = inflater.inflate(R.layout.fragment_yw, null);
            initView();
            mRootView = new WeakReference<View>(view);
        } else {
            ViewGroup parent = (ViewGroup) mRootView.get().getParent();
            if (parent != null) {
                parent.removeView(mRootView.get());
            }
        }
        return mRootView.get();
    }

    private void initView() {
        jw_mian_ll = (LinearLayout) view.findViewById(R.id.jw_mian_ll);
        yw_frame = (RelativeLayout) view.findViewById(R.id.yw_frame);
        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) view
                .findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout) view
                .findViewById(R.id.mRadioGroup_content);
        ll_more_columns = (LinearLayout) view
                .findViewById(R.id.ll_more_columns);
        rl_column = (RelativeLayout) view.findViewById(R.id.rl_column);
        button_more_columns = (ImageView) view
                .findViewById(R.id.button_more_columns);
        application = (KXTApplication) getActivity().getApplication();
        if (null != application.getQueue()) {
            queue = application.getQueue();
        } else {
            queue = Volley.newRequestQueue(getActivity());
        }
        mViewPager = (ViewPager) view.findViewById(R.id.mViewPager);
        text_refresh = (TextView) view.findViewById(R.id.text_refresh);
        text_refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TagsTask(TagURL);
            }
        });
        button_more_columns.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FLActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, 100);
            }
        });
        initColumnData();
    }

    /**
     * 获取Column栏目 数据
     */
    private void initColumnData() {
        if (null == application.getChannelItems()) {
            jw_mian_ll.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            yw_frame.setVisibility(View.VISIBLE);
            TagsTask(TagURL);
        } else {
            userChannelList = (ArrayList<ChannelItem>) application
                    .getChannelItems();
            initTabColumn();
            initFragment();
        }
    }

    /**
     * 获取column栏数据
     *
     * @param url
     */
    private void TagsTask(final String url) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("data");
                            for (int k = 0; k < array.length(); k++) {
                                JSONObject object = (JSONObject) array.get(k);
                                ChannelItem channelItem = new ChannelItem();
                                channelItem.setId(object.getString("id"));
                                channelItem.setName(object.getString("name"));
                                userChannelList.add(channelItem);
                            }
                            ChannelItem channelItem = new ChannelItem("0", "全部");
                            userChannelList.add(0, channelItem);
                            application.setChannelItems(userChannelList);
                            yw_frame.setVisibility(View.GONE);
                            mViewPager.setVisibility(View.VISIBLE);
                            jw_mian_ll.setVisibility(View.VISIBLE);
                            initTabColumn();
                            initFragment();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                jw_mian_ll.setVisibility(View.GONE);
                mViewPager.setVisibility(View.GONE);
                yw_frame.setVisibility(View.VISIBLE);
            }
        });
        queue.add(jsObjRequest);
    }

    /**
     * 初始化Column栏目项
     */
    private void initTabColumn() {
        mRadioGroup_content.removeAllViews();
        int count = userChannelList.size();
        mColumnHorizontalScrollView.setParam(getActivity(), mScreenWidth,
                mRadioGroup_content, null, null, ll_more_columns, rl_column);
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = mItemWidth;
            columnTextView = new JW_mian_ZDY(getActivity(), Isyj);
            columnTextView.setTextViewText(userChannelList.get(i).getName());
            columnTextView.setTextSize(16);
            if (Isyj) {
                columnTextView.setTextColor(getResources().getColorStateList(
                        R.color.zdy_color_neight));
            } else {
                columnTextView.setTextColor(getResources().getColorStateList(
                        R.color.zdy_color));
            }
            if (columnSelectIndex == i) {
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v)
                            localView.setSelected(false);
                        else {
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                        }
                    }
                }
            });
            mRadioGroup_content.addView(columnTextView, i, params);
        }
    }

    /**
     * 选择的Column里面的Tab
     */
    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
            View checkView = mRadioGroup_content.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
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

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        fragments.clear();// 清空
        int count = userChannelList.size();
        for (int i = 0; i < count; i++) {
            Bundle data = new Bundle();
            data.putString("tagId", userChannelList.get(i).getId());
            Item_Fragment newfragment = new Item_Fragment();
            newfragment.setArguments(data);
            fragments.add(newfragment);
        }
        NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(
                getChildFragmentManager(), fragments);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mAdapetr);
        mViewPager.setOnPageChangeListener(pageListener);
    }

    /**
     * ViewPager切换监听方法
     */
    public OnPageChangeListener pageListener = new OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            application.setCunrentfragment(Integer.parseInt(userChannelList
                    .get(position).getId()));
            mViewPager.setCurrentItem(position);
            selectTab(position);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 111) {
            columnSelectIndex = Integer.parseInt(data.getStringExtra("select"));
            mViewPager.setCurrentItem(columnSelectIndex);
            selectTab(columnSelectIndex);
        }
    }
}
