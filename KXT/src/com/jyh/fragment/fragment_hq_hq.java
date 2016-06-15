package com.jyh.fragment;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jyh.bean.HqBeanData;
import com.jyh.bean.HqChildren;
import com.jyh.bean.TopBean;
import com.jyh.kxt.HQCenterActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.customtool.ColumnHorizontalScrollView;
import com.jyh.kxt.customtool.HQ_mian_ZDY;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.tool.BaseTools;

/**
 * 行情详细信页面
 *
 * @author Administrator
 */
public class fragment_hq_hq extends Fragment {
    private View view;
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;// 头部滚动条
    private LinearLayout mRadioGroup_content, ll_more_columns,
            jw_main_all_view;
    private RelativeLayout rl_column, relatview_reload, relative_hq_show;
    private int mScreenWidth = 0;// 屏幕宽度
    private int mItemWidth = 0;// item的宽度
    /**
     * 用户选择的新闻分类列表
     */
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private ImageView button_more_columns;
    private boolean Isyj = false;
    private SharedPreferences preferences, preferences1;
    private HQ_mian_ZDY columnTextView;
    private int columnSelectIndex = 0;
    /**
     * 当前选中的栏目
     */
    private KXTApplication application;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    protected WeakReference<View> mRootView;
    private Context context;
    private List<HqBeanData> datas = new ArrayList<HqBeanData>();
    private RequestQueue queue;
    private static final String QuotesURL = "http://appapi.kxt.com/Data/quotes_list";// 行情列表地址
    private HashMap<String, Integer> hq_item_hash = new HashMap<String, Integer>();
    private Integer hq_item;
    private TextView text_refresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = context.getSharedPreferences("setup",
                Context.MODE_PRIVATE);
        Isyj = preferences.getBoolean("yj_btn", false);
        if (Isyj) {
            context.setTheme(R.style.BrowserThemeNight);
        } else {
            context.setTheme(R.style.BrowserThemeDefault);
        }
        application = (KXTApplication) ((Activity) context).getApplication();
        if (null != application.getQueue()) {
            queue = application.getQueue();
        } else {
            queue = Volley.newRequestQueue(getActivity());
        }
        if (application.ischange && null != mRootView) {
            mRootView.clear();
        }
        if (mRootView == null || mRootView.get() == null) {
            view = inflater.inflate(R.layout.fragment_hq_hq, null);
            if (!NetworkCenter.checkNetworkConnection(context)) {
                init();
                showReloadView();
            } else {
                init();
                InitDate();
            }
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
     * 初始化方法
     */
    public void init() {
        jw_main_all_view = (LinearLayout) view
                .findViewById(R.id.jw_main_all_view);
        preferences = context.getSharedPreferences("setup",
                Context.MODE_PRIVATE);
        Isyj = preferences.getBoolean("yj_btn", false);
        preferences1 = context.getSharedPreferences("isHQCenter",
                Context.MODE_PRIVATE);
        columnTextView = new HQ_mian_ZDY(context, Isyj);
        manager = getChildFragmentManager();
        mScreenWidth = BaseTools.getWindowsWidth((Activity) context);
        mItemWidth = mScreenWidth / 16;// 一个Item宽度为屏幕的1/16
        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) view
                .findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout) view
                .findViewById(R.id.mRadioGroup_content);
        ll_more_columns = (LinearLayout) view
                .findViewById(R.id.ll_more_columns);
        rl_column = (RelativeLayout) view.findViewById(R.id.rl_column);
        button_more_columns = (ImageView) view.findViewById(R.id.btn_hq_frag);
        button_more_columns.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, HQCenterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, 130);
            }
        });

        relative_hq_show = (RelativeLayout) view
                .findViewById(R.id.relative_hq_show);
        relatview_reload = (RelativeLayout) view
                .findViewById(R.id.relatview_reload);
        text_refresh = (TextView) view.findViewById(R.id.text_refresh);
        text_refresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showProgress();
                InitDate();

            }
        });

        showProgress();

    }

    /**
     * 显示 显示数据的view,隐藏其他的view
     */
    public void showDataView() {
        relative_hq_show.setVisibility(View.GONE);
        relatview_reload.setVisibility(View.GONE);
        jw_main_all_view.setVisibility(View.VISIBLE);
    }

    /**
     * 显示 重新加载view,隐藏其他的view
     */
    public void showReloadView() {
        relative_hq_show.setVisibility(View.GONE);
        relatview_reload.setVisibility(View.VISIBLE);
        jw_main_all_view.setVisibility(View.GONE);
    }

    /**
     * 显示progress,隐藏其他的view
     */
    public void showProgress() {
        relative_hq_show.setVisibility(View.VISIBLE);
        relatview_reload.setVisibility(View.GONE);
        jw_main_all_view.setVisibility(View.GONE);
    }

    private void InitDate() {
        initColumnData();
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        fragments.clear();// 清空
        if (null != datas && datas.size() > 0) {
            int count = datas.size();
            for (int i = 0; i < count; i++) {
                Bundle data = new Bundle();
                data.putString("tagId", "" + i);
                HqItem_fragment newfragment = new HqItem_fragment();
                newfragment.setArguments(data);
                fragments.add(newfragment);
            }
        }
        transaction = manager.beginTransaction();
        if (null != fragments && fragments.size() > 0) {
            if (!fragments.get(0).isAdded()) {
                transaction.add(R.id.frame_hq, fragments.get(0))
                        .commitAllowingStateLoss();
            }
        }
    }

    /**
     * 获取行情头列表
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
                            List<TopBean> topBeans = new ArrayList<TopBean>();
                            List<HqBeanData> hqBeanDatas = new ArrayList<HqBeanData>();
                            JSONArray array = response.getJSONArray("data");
                            if (null != array && array.length() > 0) {
                                for (int k = 0; k < array.length(); k++) {
                                    List<HqChildren> hqChildrens = new ArrayList<HqChildren>();
                                    JSONObject object = (JSONObject) array
                                            .get(k);
                                    HqBeanData hqBeanData = new HqBeanData();
                                    if (k == 0) {
                                        if (null != object.getJSONArray("top")) {
                                            JSONArray topArray = object
                                                    .getJSONArray("top");
                                            for (int t = 0; t < topArray
                                                    .length(); t++) {
                                                JSONObject topObject = (JSONObject) topArray
                                                        .get(t);
                                                TopBean topBean = new TopBean();
                                                topBean.setCode(topObject
                                                        .getString("code"));
                                                topBean.setName(topObject
                                                        .getString("name"));
                                                topBeans.add(topBean);
                                            }
                                        }
                                    }
                                    if (null != object.getString("children")) {
                                        JSONArray childrenArray = object
                                                .getJSONArray("children");
                                        for (int z = 0; z < childrenArray
                                                .length(); z++) {
                                            JSONObject childrenObject = (JSONObject) childrenArray
                                                    .get(z);
                                            HqChildren hqChildren = new HqChildren();
                                            hqChildren.setCode(childrenObject
                                                    .getString("code"));
                                            hqChildren.setName(childrenObject
                                                    .getString("name"));
                                            hqChildrens.add(hqChildren);
                                        }
                                    }
                                    hqBeanData.setCode(object.getString("code"));
                                    hqBeanData.setName(object.getString("name"));
                                    hq_item_hash.put(object.getString("code"),
                                            k);
                                    hqBeanData.setChildren(hqChildrens);
                                    if (null != topBeans) {
                                        hqBeanData.setTopBeans(topBeans);
                                    }
                                    hqBeanDatas.add(hqBeanData);
                                }
                            }

                            application.setHqBeanDatas(hqBeanDatas);
                            datas.addAll(hqBeanDatas);
                            if (datas.size() > 0) {
                                showDataView();
                                initScrollView();
                                initFragment();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block

                            showReloadView();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                showReloadView();
            }

        });
        queue.add(jsObjRequest);
    }

    /**
     * 初始化mColumnHorizontalScrollView
     */
    public void initScrollView() {

        mColumnHorizontalScrollView.setParam((Activity) context, mScreenWidth,
                mRadioGroup_content, null, null, ll_more_columns, rl_column);

        if (null != datas && datas.size() > 0) {
            int count = datas.size();
            for (int i = 0; i < count; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.leftMargin = 5;
                params.rightMargin = mItemWidth;
                columnTextView = new HQ_mian_ZDY(context, Isyj);
                columnTextView.setTextViewText(datas.get(i).getName());
                columnTextView.setTextSize(16);
                if (Isyj) {
                    columnTextView.setTextColor(getResources()
                            .getColorStateList(R.color.hq_color_end));
                } else {
                    columnTextView.setTextColor(getResources()
                            .getColorStateList(R.color.hq_color_start));
                }
                if (columnSelectIndex == i) {
                    columnTextView.setSelected(true);
                }
                columnTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        preferences1.edit().putBoolean("isHqSelect", false)
                                .commit();

                        preferences1.edit().putBoolean("isChange", true)
                                .commit();

                        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                            View localView = mRadioGroup_content.getChildAt(i);
                            if (localView != v) {
                                localView.setSelected(false);
                            } else {
                                localView.setSelected(true);
                                getIndex(i);
                            }
                        }
                    }
                });
                mRadioGroup_content.addView(columnTextView, i, params);
            }
        }
        if (application.getHq_item() != null) {
            if (hq_item_hash.get(application.getHq_item()) != null) {
                hq_item = hq_item_hash.get(application.getHq_item());
                if (hq_item != null) {
                    handler.sendEmptyMessage(20);
                }
            }
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 20:
                    selectTab(hq_item);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    /**
     * 获取行情头文件数据
     */
    private void initColumnData() {
        if (null == application.getHqBeanDatas()
                || application.getHqBeanDatas().size() < 2) {
            showProgress();
            TagsTask(QuotesURL);
        } else {

            showDataView();
            datas = application.getHqBeanDatas();
            initScrollView();
            initFragment();
        }
    }

    /**
     * scorllview点击事件
     *
     * @param tab_postion
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
     * 重新获取被选项 切换fragment
     *
     * @param index
     */
    public void getIndex(int index) {

        preferences1.edit().putBoolean("isHqSelect", true).commit();
        preferences1.edit().putBoolean("isChange", true).commit();
        if (columnSelectIndex != index) {
            columnSelectIndex = index;
            hideAllFragment(index);
            transaction = manager.beginTransaction();
            if (fragments.get(index).isAdded()) {

                transaction.show(fragments.get(index))
                        .commitAllowingStateLoss();
                ((HqItem_fragment) fragments.get(index)).setResumeInfo();
            } else {
                // TODO 隐藏所有fragment
                if (!fragments.get(index).isAdded()) {
                    transaction.add(R.id.frame_hq, fragments.get(index))
                            .commitAllowingStateLoss();
                }
            }
            selectTab(columnSelectIndex);
            application.setIndex(columnSelectIndex);
            application.setHqDatabean(null);
        }
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 130 && resultCode == 111) {
            columnSelectIndex = Integer.parseInt(data.getStringExtra("index"));
            transaction = manager.beginTransaction();
            transaction
                    .replace(R.id.frame_hq, fragments.get(columnSelectIndex))
                    .commitAllowingStateLoss();
            selectTab(columnSelectIndex);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (NetworkCenter.checkNetworkConnection(context)) {
        } else {
            Toast.makeText(context, "网络异常，获取数据失败", Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    // public interface RefreshListener {
    // public void refreshFragment();
    // // public void startService();// 启动行情服务
    // }

    /**
     * 隐藏所有行情页所有子Fragmnet
     */
    public void hideAllFragment(int index) {
        for (int i = 0; i < fragments.size(); i++) {
            transaction = manager.beginTransaction();
            if (i == index) {
                transaction.show(fragments.get(i)).commitAllowingStateLoss();
            } else {
                transaction.hide(fragments.get(i)).commitAllowingStateLoss();

            }
        }

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        context = activity;
    }

    /**
     * 重新获取数据
     */
    public void Resom() {
        ((HqItem_fragment) fragments.get(columnSelectIndex)).handle
                .sendEmptyMessage(30);
    }
}
