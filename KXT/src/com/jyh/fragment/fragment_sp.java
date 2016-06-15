package com.jyh.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.jyh.bean.VedioTypeTitle;
import com.jyh.kxt.DataFLActivity;
import com.jyh.kxt.R;
import com.jyh.kxt.MainActivity.FragmentActivityResult;
import com.jyh.kxt.customtool.ColumnHorizontalScrollView;
import com.jyh.kxt.customtool.HQ_mian_ZDY;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.tool.BaseTools;

/**
 * 视频主界面
 *
 * @author PC
 */
@SuppressWarnings("rawtypes")
public class fragment_sp extends Fragment implements FragmentActivityResult {
    private View view;
    public static boolean isLoadingMore = true;
    private SharedPreferences preferences;
    private boolean Isyj = false;
    protected WeakReference<View> mRootView;
    private ViewPager viewPager;
    private MyAdapter adapter;
    private LinearLayout more;

    private String titleUrl = "http://appapi.kxt.com/Video/nav";// titles的获取地址

    private List<VedioTypeTitle> vedioTypes;

    /**
     * 头部
     */
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    private LinearLayout mRadioGroup_content;
    private LinearLayout ll_more_columns;
    private RelativeLayout rl_column;
    private HQ_mian_ZDY columnTextView;
    private int mScreenWidth = 0;// 屏幕宽度
    private int mItemWidth = 0;// item的宽度
    private int columnSelectIndex = 0;
    private Context context;
    private KXTApplication application;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("setup",
                Context.MODE_PRIVATE);
        application = (KXTApplication) getActivity().getApplication();
        // Log.i("info",
        // "application.getVedioTypes() != null="+(application.getVedioTypes()
        // != null));

        Isyj = preferences.getBoolean("yj_btn", false);
        if (Isyj) {
            getActivity().setTheme(R.style.BrowserThemeNight);
        } else {
            getActivity().setTheme(R.style.BrowserThemeDefault);
        }
        if (mRootView == null || mRootView.get() == null) {
            view = inflater
                    .inflate(R.layout.fragment_sp_main, container, false);

            if (null != application.getVedioTypes()) {
                vedioTypes = application.getVedioTypes();
                InitFind();
            } else {
                new GetVedioTitles().execute(titleUrl);
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

    private void InitFind() {
        viewPager = (ViewPager) view.findViewById(R.id.sp_viewpager);
        more = (LinearLayout) view.findViewById(R.id.ll_more_columns);

        more.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), DataFLActivity.class);
                intent.putExtra("sp", titleUrl);
                startActivityForResult(intent, 100);
            }
        });
        mScreenWidth = BaseTools.getWindowsWidth((Activity) context);
        mItemWidth = mScreenWidth / 16;// 一个Item宽度为屏幕的1/16
        initScrollView();
    }

    /**
     * 初始化mColumnHorizontalScrollView
     */
    public void initScrollView() {

        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) view
                .findViewById(R.id.hq_column_scrollview);
        mRadioGroup_content = (LinearLayout) view
                .findViewById(R.id.mRadioGroup_content);
        ll_more_columns = (LinearLayout) view
                .findViewById(R.id.ll_more_columns);
        rl_column = (RelativeLayout) view.findViewById(R.id.rl_column);

        mColumnHorizontalScrollView.setParam((Activity) context, mScreenWidth,
                mRadioGroup_content, null, null, ll_more_columns, rl_column);

        int count = vedioTypes.size() + 1;
        adapter = new MyAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = mItemWidth;

            columnTextView = new HQ_mian_ZDY(context, Isyj);
            if (i == 0) {
                columnTextView.setTextViewText("推荐");
            } else {
                columnTextView.setTextViewText(vedioTypes.get(i - 1)
                        .getCat_name());
            }
            columnTextView.setTextSize(16);
            columnTextView.setTag(i);

            if (Isyj) {
                columnTextView.setTextColor(getResources().getColorStateList(
                        R.color.hq_color_end));
            } else {
                columnTextView.setTextColor(getResources().getColorStateList(
                        R.color.hq_color_start));
            }

            if (columnSelectIndex == i) {
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onClick(View v) {
                    int tag = (Integer) v.getTag();
                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (tag == i) {
                            localView.setSelected(true);
                            mColumnHorizontalScrollView
                                    .setScrollX((int) localView.getX()
                                            - getActivity().getWindowManager()
                                            .getDefaultDisplay()
                                            .getWidth() / 3);
                            viewPager.setCurrentItem(i);
                        } else {
                            localView.setSelected(false);
                        }
                    }
                }

            });
            mRadioGroup_content.addView(columnTextView, i, params);
        }
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                viewPager.setCurrentItem(arg0);
                selectTab(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
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

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        this.context = activity;
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @SuppressWarnings("unchecked")
        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            if (arg0 == 0) {
                fragment_sp_mainItem fragment_sp_mainItem = new fragment_sp_mainItem();
                Bundle bundle = new Bundle();

                ArrayList list = new ArrayList();
                list.add(vedioTypes);
                bundle.putSerializable("type", list);
                fragment_sp_mainItem.setArguments(bundle);
                return fragment_sp_mainItem;
            } else {
                fragment_sp_item fragment_sp_item = new fragment_sp_item();
                Bundle bundle = new Bundle();
                bundle.putString("type", vedioTypes.get(arg0 - 1).getId());
                fragment_sp_item.setArguments(bundle);
                return fragment_sp_item;
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return vedioTypes.size() + 1;
        }

    }

    /*
     * 获取item title
     */
    class GetVedioTitles extends AsyncTask<String, Void, List<VedioTypeTitle>> {

        @Override
        protected List<VedioTypeTitle> doInBackground(String... params) {
            // TODO Auto-generated method stub
            List<VedioTypeTitle> titles = new ArrayList<VedioTypeTitle>();
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(params[0]);
            try {
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String data = EntityUtils.toString(response.getEntity());
                    JSONObject object = new JSONObject(data);
                    JSONArray array = object.getJSONArray("data");
                    titles = JSON.parseArray(array.toString(),
                            VedioTypeTitle.class);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (client != null && client.getConnectionManager() != null) {
                    client.getConnectionManager().shutdown();
                }
            }
            return titles;
        }

        @Override
        protected void onPostExecute(List<VedioTypeTitle> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                application.setVedioTypes(result);
                vedioTypes = result;
                InitFind();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void OnActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == 102) {
            String t = data.getStringExtra("title");
            if (null != vedioTypes && vedioTypes.size() > 0) {
                int c = vedioTypes.size();
                for (int i = 0; i <= c; i++) {
                    mRadioGroup_content.getChildAt(i).setSelected(false);
                }
                for (int i = 0; i < c; i++) {
                    if (vedioTypes.get(i).getCat_name().equals(t)) {
                        mRadioGroup_content.getChildAt(i + 1).setSelected(true);
                        mColumnHorizontalScrollView
                                .setScrollX((int) mRadioGroup_content.getChildAt(
                                        i + 1).getX()
                                        - getActivity().getWindowManager()
                                        .getDefaultDisplay().getWidth() / 3);
                        viewPager.setCurrentItem(i + 1);
                    }
                }
            }

        }
    }

}