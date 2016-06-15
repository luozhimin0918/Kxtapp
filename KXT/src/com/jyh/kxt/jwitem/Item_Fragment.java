package com.jyh.kxt.jwitem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jyh.kxt.DPWebActivity;
import com.jyh.kxt.R;
import com.jyh.bean.AdBean;
import com.jyh.bean.JwAllBean;
import com.jyh.kxt.adapter.Fragment_All_Adapter;
import com.jyh.kxt.socket.DateTimeUtil;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.kxt.view.Advertisements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 要闻子界面
 *
 * @author PC
 */
public class Item_Fragment extends Fragment {
    private List<JwAllBean> allBeans;
    private ListView listView;
    private PullToRefreshListView pullToRefreshListView;
    private View view;
    private Fragment_All_Adapter all_Adapter;
    private int markid = 0;
    public int currentsize = 0;
    public boolean isLoadingMore = false;
    public ArrayList<JwAllBean> allbeJwAllBeans;
    private boolean jxLoadMore = true;
    private String tagId;
    private boolean isRefresh = false;
    private ArrayList<String> weblist = new ArrayList<String>();
    private String url = "http://appapi.kxt.com/data/jianwen?num=10&tagid=";//要闻数据地址
    private RelativeLayout relative_yw_show, yw_frame;
    private KXTApplication application;
    private RequestQueue queue;
    public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径
    private List<AdBean> adList = new ArrayList<AdBean>();
    private LayoutInflater adinflater;
    private String Adurl = "http://appapi.kxt.com/data/jianwen_slide";//要闻轮播图地址
    boolean IsOk = false;
    private View advertisements;
    private boolean Header = false;
    protected WeakReference<View> mRootView;
    private boolean Isyj = false;
    private SharedPreferences preferences;
    private Context context;
    private TextView text_refresh;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("setup",
                Context.MODE_PRIVATE);
        Isyj = preferences.getBoolean("yj_btn", false);
        if (mRootView == null || mRootView.get() == null) {
            view = inflater.inflate(R.layout.item_fragment_gjs, null);
            Bundle bundle = getArguments();
            tagId = bundle.getString("tagId", "0");
            if (!NetworkCenter.checkNetworkConnection(context)) {
                InitFind();
                InitData();
                showReload();
            } else {
                InitFind();
                InitData();
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == true) {
            if (null != all_Adapter) {
                all_Adapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * 显示progress
     */
    public void showProgress() {
        relative_yw_show.setVisibility(View.VISIBLE);
        pullToRefreshListView.setVisibility(View.GONE);
        yw_frame.setVisibility(View.GONE);
    }

    /**
     * 显示重新加载
     */
    public void showReload() {
        relative_yw_show.setVisibility(View.GONE);
        pullToRefreshListView.setVisibility(View.GONE);
        yw_frame.setVisibility(View.VISIBLE);
    }

    /**
     * 显示显示数据的View
     */
    public void showDataView() {
        relative_yw_show.setVisibility(View.GONE);
        pullToRefreshListView.setVisibility(View.VISIBLE);
        yw_frame.setVisibility(View.GONE);
    }

    private void InitFind() {
        application = (KXTApplication) getActivity().getApplication();
        if (application.getQueue() != null) {
            queue = application.getQueue();
        } else {
            queue = Volley.newRequestQueue(getActivity());
            application.setQueue(queue);
        }
        allbeJwAllBeans = new ArrayList<JwAllBean>();
        relative_yw_show = (RelativeLayout) view
                .findViewById(R.id.relative_yw_show);
        pullToRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.item_fragment_gjs_refresh);
        listView = pullToRefreshListView.getRefreshableView();
        registerForContextMenu(listView);

        yw_frame = (RelativeLayout) view.findViewById(R.id.yw_frame);
        text_refresh = (TextView) view.findViewById(R.id.text_refresh);
        text_refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                refreshText();
            }
        });

        if (NetworkCenter.checkNetworkConnection(getActivity())) {
            if (tagId.equals("0")) {
                TagsTask(Adurl);
            } else {
                InitList();
            }
        }
    }

    /**
     * 获取轮播图数据
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
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = (JSONObject) array.get(i);
                                AdBean adBean = new AdBean();
                                adBean.setImage(object.getString("thumb"));
                                adBean.setShareUrl(object.getString("weburl"));
                                adBean.setUrl(object.getString("url"));
                                adBean.setTitle(object.getString("title"));
                                adList.add(adBean);
                            }
                            InitList();
                        } catch (JSONException e) {
                            InitList();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                InitList();
            }
        });
        queue.add(jsObjRequest);
    }

    /**
     * 初始化数据
     */

    public void refreshText() {
        IsOk = false;

        showProgress();
        if (NetworkCenter.checkNetworkConnection(getActivity())) {
            if (tagId.equals("0")) {
                TagsTask(Adurl);
            } else {
                InitList();
            }
        } else {
            Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT)
                    .show();
            handler.sendEmptyMessageDelayed(101, 2 * 1000);
        }
    }

    private void InitData() {
        handler.sendEmptyMessageDelayed(101, 10 * 1000);
        listView.setOnItemClickListener(new OnItemClickListener() {

            private Intent intent = new Intent(getActivity(),
                    DPWebActivity.class);

            @SuppressWarnings("static-access")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                TextView textView = (TextView) arg1
                        .findViewById(R.id.tv_frag_jwls_title);
                if (Isyj) {
                    textView.setTextColor(Color.parseColor("#666666"));

                } else {
                    textView.setTextColor(Color.parseColor("#999999"));

                }

                if (!Header) {
                    if (!application.dpList.contains(allbeJwAllBeans.get(
                            arg2 - 1).getId())) {
                        application.ywList.add(allbeJwAllBeans.get(arg2 - 1)
                                .getId());
                    }
                    intent.putExtra("weburi", allbeJwAllBeans.get(arg2 - 1)
                            .getUrl());
                    intent.putExtra("category", allbeJwAllBeans.get(arg2 - 1)
                            .getCategory_id());
                    intent.putExtra("addtime", allbeJwAllBeans.get(arg2 - 1)
                            .getAddtime());
                    intent.putExtra("id", allbeJwAllBeans.get(arg2 - 1).getId());
                    intent.putExtra("weburl", allbeJwAllBeans.get(arg2 - 1)
                            .getWeburl());
                    intent.putExtra("thumb", allbeJwAllBeans.get(arg2 - 1)
                            .getThumb());
                    intent.putExtra("discription", allbeJwAllBeans
                            .get(arg2 - 1).getDiscription());
                    intent.putExtra("title", allbeJwAllBeans.get(arg2 - 1)
                            .getTitle());
                    intent.putExtra("legth", allbeJwAllBeans.get(arg2 - 1)
                            .getTags().length());
                    for (int i = 0; i < allbeJwAllBeans.get(arg2 - 1).getTags()
                            .length(); i++) {
                        try {
                            intent.putExtra("tag" + i,
                                    allbeJwAllBeans.get(arg2 - 1).getTags()
                                            .getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!weblist.isEmpty()) {
                        weblist.clear();
                    }
                    for (int i = 0; i < 10; i++) {
                        if ((arg2 + i) < allbeJwAllBeans.size()) {
                            weblist.add(allbeJwAllBeans.get(arg2 + i - 1)
                                    .getUrl());
                        }
                    }
                    intent.putStringArrayListExtra("weblist", weblist);
                } else {
                    if (!application.dpList.contains(allbeJwAllBeans.get(
                            arg2 - 2).getId())) {
                        application.ywList.add(allbeJwAllBeans.get(arg2 - 2)
                                .getId());
                    }
                    intent.putExtra("weburi", allbeJwAllBeans.get(arg2 - 2)
                            .getUrl());
                    intent.putExtra("category", allbeJwAllBeans.get(arg2 - 2)
                            .getCategory_id());
                    intent.putExtra("addtime", allbeJwAllBeans.get(arg2 - 2)
                            .getAddtime());
                    intent.putExtra("id", allbeJwAllBeans.get(arg2 - 2).getId());
                    intent.putExtra("weburl", allbeJwAllBeans.get(arg2 - 2)
                            .getWeburl());
                    intent.putExtra("thumb", allbeJwAllBeans.get(arg2 - 2)
                            .getThumb());
                    intent.putExtra("discription", allbeJwAllBeans
                            .get(arg2 - 2).getDiscription());
                    intent.putExtra("title", allbeJwAllBeans.get(arg2 - 2)
                            .getTitle());
                    intent.putExtra("legth", allbeJwAllBeans.get(arg2 - 2)
                            .getTags().length());
                    for (int i = 0; i < allbeJwAllBeans.get(arg2 - 2).getTags()
                            .length(); i++) {
                        try {
                            intent.putExtra("tag" + i,
                                    allbeJwAllBeans.get(arg2 - 2).getTags()
                                            .getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!weblist.isEmpty()) {
                        weblist.clear();
                    }
                    for (int i = 0; i < 10; i++) {
                        if ((arg2 + i) < allbeJwAllBeans.size()) {
                            weblist.add(allbeJwAllBeans.get(arg2 + i - 2)
                                    .getUrl());
                        }
                    }
                    intent.putStringArrayListExtra("weblist", weblist);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }

        });
        pullToRefreshListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // TODO Auto-generated method stub
                        if (NetworkCenter.isNetworkConnected(getActivity())) {
                            if (!isRefresh) {
                                new JwAllDataTask().execute();
                                isRefresh = true;
                            }
                        } else {
                            Toast.makeText(getActivity(), "无网络连接",
                                    Toast.LENGTH_SHORT).show();
                            pullToRefreshListView.onRefreshComplete();
                        }
                    }
                });
        pullToRefreshListView
                .setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

                    @Override
                    public void onLastItemVisible() {
                        // TODO Auto-generated method stub
                        if (NetworkCenter.isNetworkConnected(getActivity())) {
                            if (!isLoadingMore) {
                                handler.sendEmptyMessage(107);
                            }
                        } else {
                            Toast.makeText(getActivity(), "无网络连接",
                                    Toast.LENGTH_SHORT).show();
                            pullToRefreshListView.onRefreshComplete();
                        }
                    }
                });
    }

    /**
     * 初始化listview
     */
    private void InitList() {
        // TODO Auto-generated method stub
        JSONArray advertiseArray = null;
        try {
            // ImageDownLoader downLoader = new ImageDownLoader(application);
            if (adList.size() > 0 && null != adList) {
                advertiseArray = new JSONArray();
                for (int i = 0; i < adList.size(); i++) {
                    advertiseArray.put(new JSONObject().put("head_img", adList
                            .get(i).getImage()));
                    // downLoader.downloadImage(adList.get(i).getImage());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (advertiseArray != null && advertiseArray.length() > 0) {
            adinflater = LayoutInflater.from(getActivity());
            if (advertisements == null) {
                advertisements = new Advertisements(getActivity(), true,
                        adinflater, 3000, adList, true)
                        .initView(advertiseArray);
            }
            if (!Header) {
                listView.addHeaderView(advertisements);
                Header = true;
            }
        }
        new JwAllDataTask().execute();
    }

    /**
     * 加载更多
     *
     * @author PC
     */
    class LoadMroe extends AsyncTask<Void, Void, List<JwAllBean>> {

        @Override
        protected List<JwAllBean> doInBackground(Void... arg0) {
            allBeans = new ArrayList<JwAllBean>();
            HttpClient client = new DefaultHttpClient();
            HttpGet get = null;
            get = new HttpGet(url + tagId + "&markid=" + markid);
            HttpResponse response;
            try {
                response = client.execute(get);

                if (HttpStatus.SC_OK == response.getStatusLine()
                        .getStatusCode()) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity, "GBK");
                    JSONArray array = new JSONObject(data).getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        JwAllBean allBean = new JwAllBean();
                        allBean.setAddtime(DateTimeUtil.DateStr(Long
                                .parseLong(object.getString("addtime") + "000")));
                        allBean.setTags(object.getJSONArray("tags"));
                        allBean.setCategory_id(object.getString("category_id"));
                        allBean.setDiscription(object.getString("description"));
                        allBean.setId(object.getString("id"));
                        allBean.setWeburl(object.getString("weburl"));
                        allBean.setThumb(object.getString("thumb"));
                        allBean.setTitle(object.getString("title"));
                        allBean.setUrl(object.getString("url"));
                        allbeJwAllBeans.add(allbeJwAllBeans.size(), allBean);
                        if (i == array.length() - 1) {
                            markid = Integer.parseInt(object.getString("id"));
                        }
                    }
                    if (allbeJwAllBeans.size() > 300) {
                        jxLoadMore = false;
                        isLoadingMore = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (client != null && client.getConnectionManager() != null) {
                    client.getConnectionManager().shutdown();
                }
            }
            return allBeans;
        }

        @Override
        protected void onPostExecute(List<JwAllBean> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            isLoadingMore = false;
            if (result == null || allbeJwAllBeans.size() == currentsize
                    || allbeJwAllBeans.size() > 250) {
                if (allbeJwAllBeans.size() > 250) {
                    jxLoadMore = false;
                }
            } else {
                currentsize = allbeJwAllBeans.size();
                handler.sendEmptyMessage(125);
            }
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 125:
                    // all_Adapter.setJwAllBeans(allbeJwAllBeans);
                    all_Adapter.setDeviceList(allbeJwAllBeans);
                    break;
                case 107:
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        loadMore();
                    } else {
                        if (jxLoadMore) {
                            Toast.makeText(getActivity(), "正在加载中。。",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case 108:
                    if (jxLoadMore) {
                        new LoadMroe().execute();
                        all_Adapter.setDeviceList(allbeJwAllBeans);
                    } else {
                        Toast.makeText(getActivity(), "无更多数据", Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                case 101:
                    if (!IsOk) {

                        showReload();
                    } else {
                        showDataView();
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 获取当页数据
     *
     * @author PC
     */
    class JwAllDataTask extends AsyncTask<Void, Void, List<JwAllBean>> {

        @Override
        protected List<JwAllBean> doInBackground(Void... arg0) {
            allBeans = new ArrayList<JwAllBean>();
            HttpClient client = new DefaultHttpClient();
            HttpGet get = null;
            get = new HttpGet(url + tagId);
            HttpResponse response;
            try {
                response = client.execute(get);
                if (HttpStatus.SC_OK == response.getStatusLine()
                        .getStatusCode()) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity, "GBK");
                    JSONArray array = new JSONObject(data).getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        JwAllBean allBean = new JwAllBean();
                        allBean.setAddtime(DateTimeUtil.DateStr(Long
                                .parseLong(object.getString("addtime") + "000")));
                        allBean.setTags(object.getJSONArray("tags"));
                        allBean.setCategory_id(object.getString("category_id"));
                        allBean.setDiscription(object.getString("description"));
                        allBean.setId(object.getString("id"));
                        allBean.setWeburl(object.getString("weburl"));
                        allBean.setThumb(object.getString("thumb"));
                        allBean.setTitle(object.getString("title"));
                        allBean.setUrl(object.getString("url"));
                        allBeans.add(allBean);
                        if (i == array.length() - 1) {
                            markid = Integer.parseInt(object.getString("id"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (client != null && client.getConnectionManager() != null) {
                    client.getConnectionManager().shutdown();
                }
            }
            return allBeans;
        }

        @Override
        protected void onPostExecute(List<JwAllBean> result) {
            super.onPostExecute(result);
            allbeJwAllBeans = (ArrayList<JwAllBean>) result;
            all_Adapter = new Fragment_All_Adapter(getActivity(),
                    allbeJwAllBeans, Isyj, application);
            listView.setAdapter(all_Adapter);
            application.getMmp().put("2", "2");

            showDataView();
            IsOk = true;
            if (isRefresh) {
                pullToRefreshListView.onRefreshComplete();
                isRefresh = false;
            }
        }
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        IsOk = false;
        super.onStop();
    }

    /**
     * 加载更多
     */
    private void loadMore() {
        handler.sendEmptyMessage(108);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (allbeJwAllBeans != null && allbeJwAllBeans.size() > 0) {
            application.getMmp().put("2", "2");

        }
    }

}
