package com.jyh.kxt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jyh.bean.DPBean;
import com.jyh.kxt.R;
import com.jyh.kxt.socket.DateTimeUtil;
import com.jyh.kxt.socket.KXTApplication;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.kxt.sqlte.SCDataSqlte;
import com.jyh.tool.UmengTool;
import com.jyh.tool.Utils;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.soexample.commons.Constants;

public class DPWebActivity extends Activity implements OnClickListener {
    private WebView webView;
    private KXTApplication application;
    private LinearLayout ll_dp_back, ll_dp_sc, ll_dp_share, ll_dp_setfont;
    private String intString;// 地址
    private String category;
    private String addtime;
    private String thumb;
    private String title;
    private SCDataSqlte dataSqlte;
    private SQLiteDatabase db;
    private int index = -1;
    private DPBean dpBean;
    private String tags = "";
    private Cursor cursor;
    private String discription;
    private String weburl;
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(Constants.DESCRIPTOR);
    private SnsPostListener mSnsPostListener;
    private static PopupWindow popupWindow;
    // private RelativeLayout dp_web_relative;
    private LinearLayout dp_web_linear;
    private static View popupView;
    private RadioGroup radioGroup;
    private LinearLayout pop_linear;
    private SharedPreferences preferences;
    private boolean Isyj = false;
    private TextView error_tv;
    int i = 1;

    private String id2;

    private static int lastRadioButton = 1;
    private String type = "";
    private String IsSC = "";
    private String NEWS_TITLE = "http://appapi.kxt.com/index.php/page/article_app/id";

    // private Handler handler = new Handler();

    // private SildingFinishRealerLayout dpweb_rl;

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
            Isyj = preferences.getBoolean("yj_btn", false);
            if (Isyj) {
                this.setTheme(R.style.BrowserThemeNight);
            } else {
                this.setTheme(R.style.BrowserThemeDefault);
            }
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_dpweb);
            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    InitFind();
                    registerForContextMenu(webView);
                    UmengTool.configPlatforms(DPWebActivity.this);
                    if (mSnsPostListener == null) {
                        mSnsPostListener = new SnsPostListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onComplete(SHARE_MEDIA platform,
                                                   int stCode, SocializeEntity entity) {
                                if (stCode == 200) {
                                    Toast.makeText(DPWebActivity.this, "分享成功",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(DPWebActivity.this, "分享失败",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        };
                        mController.registerListener(mSnsPostListener);
                    }
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void InitFind() {
        ll_dp_back = (LinearLayout) findViewById(R.id.ll_dp_back);
        ll_dp_sc = (LinearLayout) findViewById(R.id.ll_dp_sc);
        ll_dp_share = (LinearLayout) findViewById(R.id.ll_dp_share);
        ll_dp_setfont = (LinearLayout) findViewById(R.id.ll_dp_setfont);
        error_tv = (TextView) findViewById(R.id.error_tv);
        webView = (WebView) findViewById(R.id.webView1);

        webView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                return true;
            }
        });

        webView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                try {
                    // 震动100毫秒
                    Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(100);
                } catch (Exception e) {
                }
                // 显示复制框
                try {
                    if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR) {
                        Method method = WebView.class.getMethod(
                                "emulateShiftHeld", Boolean.TYPE);
                        method.invoke(view, false);
                    } else {
                        Method method = WebView.class
                                .getMethod("emulateShiftHeld");
                        method.invoke(view);
                    }
                } catch (Exception e) {
                    KeyEvent shiftPressEvent = new KeyEvent(0, 0,
                            KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT,
                            0, 0);
                    shiftPressEvent.dispatch(view, null, null);
                }
                return false;
            }
        });
        dp_web_linear = (LinearLayout) findViewById(R.id.dp_web_linear);
        if (Isyj) {
            dp_web_linear.setBackgroundColor(Color.parseColor("#04274c"));
        } else {
            dp_web_linear.setBackgroundColor(Color.parseColor("#116bcc"));
        }
        popupView = getLayoutInflater().inflate(R.layout.popupwindow_setfont,
                null);
        radioGroup = (RadioGroup) popupView
                .findViewById(R.id.radiogroup_setting);
        pop_linear = (LinearLayout) popupView.findViewById(R.id.pop_linear);

        application = (KXTApplication) getApplication();
        application.addAct(this);
        dataSqlte = new SCDataSqlte(this);
        Intent intent = getIntent();
        if (intent.getData() == null) {
            IsSC = intent.getStringExtra("SCActivity");
            if (null != intent.getStringExtra("url")) {
                intString = intent.getStringExtra("url");
                type = intent.getStringExtra("type");
            } else {
                if (intent.getSerializableExtra("weburi") instanceof DPBean) {
                    dpBean = (DPBean) intent.getSerializableExtra("weburi");
                    intString = dpBean.getUrl();// 地址
                } else {
                    intString = intent.getStringExtra("weburi");
                }
            }
        } else {
            intString = NEWS_TITLE + intent.getData().getPath();
            type = "news";
        }
        InitData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }

    public void intWeb() {
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }
    }

    @SuppressWarnings("deprecation")
    private void InitData() {
        ll_dp_back.setOnClickListener(this);
        ll_dp_sc.setOnClickListener(this);
        ll_dp_share.setOnClickListener(this);
        ll_dp_setfont.setOnClickListener(this);
        pop_linear.setAlpha(0.0f);
        initPopupWindwo(ll_dp_setfont);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setBuiltInZoomControls(true);//
        webView.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//
        // 排版适应屏幕
        webView.getSettings().setUseWideViewPort(true);// 可任意比例缩放
        webView.getSettings().setLoadWithOverviewMode(true);//
        // setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        intWeb();
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) { // 表示按返回键
                        webView.goBack(); // 后退
                        return true; // 已处理
                    }
                }
                return false;
            }
        });
        if (Isyj) {
            webView.loadUrl(intString + "?yejian=1");
        } else {
            webView.loadUrl(intString);
        }
        webView.setWebChromeClient(new WebChromeClient() {
            private String type;

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {

                // TODO Auto-generated method stub
                if (null == message) {
                    return true;
                }
                try {
                    JSONObject object = new JSONObject(message);
                    type = object.getString("type");
                    id2 = object.getString("id");
                    intString = object.getString("url");
                    addtime = DateTimeUtil.DateStr(Long.parseLong(object
                            .getString("addtime") + "000"));
                    title = object.getString("title");
                    thumb = object.getString("thumb");
                    discription = object.getString("description");
                    weburl = object.getString("weburl");
                    category = object.getString("category_id");
                    JSONArray _tags = object.getJSONArray("tags");
                    tags = null;
                    for (int i = 0; i < _tags.length(); i++) {
                        tags = tags + "," + _tags.getString(i);
                    }

                    if (!type.equals("dianping")) {
                        index = 2;
                    } else {
                        index = 1;
                    }
                    db = dataSqlte.getReadableDatabase();
                    cursor = db.query("data", null, "id=? and category=?",
                            new String[]{id2, category}, null, null, null);
                    boolean b = cursor.moveToFirst();
                    if (b == true) {
                        ll_dp_sc.setSelected(true);
                    } else {
                        ll_dp_sc.setSelected(false);
                    }
                    UmengTool.setShareContent(mController, DPWebActivity.this,
                            title, weburl, discription, thumb);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    index = -1;
                }
                result.confirm();
                return true;
            }
        });
        InitOther();
    }

    // js通信接口
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        public void openImage(String img) {
            Intent intent = new Intent();
            intent.putExtra("image", img);
            intent.setClass(context, ShowWebImageActivity.class);
            context.startActivity(intent);
            // System.out.println(img);
        }
    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementById('article').getElementsByTagName('img');"
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "    objs[i].onclick=function()  " + "    {  "
                + "        window.imagelistner.openImage(this.src);  "
                + "    }  " + "}" + "})()");
    }

    private void InitOther() {
        pop_linear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        radioGroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        for (int i = 0; i < group.getChildCount(); i++) {
                            RadioButton radioButton = (RadioButton) group
                                    .getChildAt(i);
                            if (radioButton.isChecked()) {
                                lastRadioButton = i;
                                radioButton.setTextColor(getResources()
                                        .getColor(R.color.white));
                                changeTextSizeListener(i);
                            } else {
                                radioButton.setTextColor(getResources()
                                        .getColor(R.color.title));
                            }
                        }
                    }
                });
        webView.addJavascriptInterface(new JavascriptInterface(this),
                "imagelistner");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (null != type) {
                if (type.equals("dianping")) {
                    Intent intent = new Intent(DPWebActivity.this,
                            MainActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);

                } else if (type.equals("news")) {
                    Intent intent = new Intent(DPWebActivity.this,
                            MainActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                } else if (null != IsSC && IsSC.contains("yes")
                        && !IsSC.equals("")) {
                    setResult(282);
                }
                type = "";
            }
            IsSC = null;
            intString = "";
            dismiss();
            finish();
            webView.getSettings().setBuiltInZoomControls(false);
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_SCROLL_LOCK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Isyj) {
                view.loadUrl(url + "?yejian=1");
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // dp_web_relative.setVisibility(View.VISIBLE);
            dp_web_linear.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // webView.getSettings().setBuiltInZoomControls(true);
            if (!webView.getSettings().getLoadsImagesAutomatically()) {
                webView.getSettings().setLoadsImagesAutomatically(true);
            }
            // dp_web_relative.setVisibility(View.INVISIBLE);
            dp_web_linear.setVisibility(View.VISIBLE);
            preferences = getSharedPreferences("websetup", Context.MODE_PRIVATE);
            String textsize = preferences.getString("size", "中");
            if (textsize.contains("中")) {
                webView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
            } else if (textsize.contains("小")) {
                webView.getSettings().setTextSize(WebSettings.TextSize.SMALLER);
            } else if (textsize.contains("大")) {
                webView.getSettings().setTextSize(WebSettings.TextSize.LARGER);
            }
            view.loadUrl("javascript:alert( $('#app_data').text() )");
            addImageClickListner();
            webView.getSettings().setBlockNetworkImage(false);

        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(DPWebActivity.this, "数据加载失败", Toast.LENGTH_SHORT)
                    .show();
            webView.setVisibility(View.GONE);
            error_tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.ll_dp_back:
                if (null != type) {
                    if (type.equals("dianping")) {
                        Intent intent = new Intent(DPWebActivity.this,
                                MainActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                    } else if (type.equals("news")) {
                        Intent intent = new Intent(DPWebActivity.this,
                                MainActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                    } else if (null != IsSC && IsSC.contains("yes")
                            && !IsSC.equals("")) {
                        setResult(282);
                    }
                    type = "";
                }
                IsSC = null;
                intString = "";
                dismiss();
                finish();
                webView.getSettings().setBuiltInZoomControls(false);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;
            case R.id.ll_dp_sc:
                db = dataSqlte.getWritableDatabase();
                if (ll_dp_sc.isSelected()) {
                    if (db != null) {
                        if (index == 1) {
                            db.delete("data", "id=? and category=?", new String[]{
                                    id2, category});
                        } else if (index == 2) {
                            db.delete("data", "id=? and category=?", new String[]{
                                    id2, category});
                        }
                        ll_dp_sc.setSelected(false);
                        Toast.makeText(this, "取消收藏", Toast.LENGTH_LONG).show();
                    } else {
                        ll_dp_sc.setSelected(false);
                        Toast.makeText(this, "操作失败", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (db == null) {
                        return;
                    }
                    if (index == -1) {
                        try {
                            Thread.sleep(1 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ll_dp_sc.setSelected(false);
                        Toast.makeText(this, "收藏失败", Toast.LENGTH_LONG).show();
                    } else if (index == 1) {
                        ContentValues values = new ContentValues();
                        values.put("url", intString);
                        values.put("category", category);
                        values.put("addtime", addtime);
                        values.put("id", id2);
                        values.put("thumb", thumb);
                        values.put("title", title);
                        values.put("tag", "");
                        values.put("isdp", "true");
                        values.put("weburl", weburl);
                        values.put("discription", discription);
                        db.insert("data", null, values);
                        Toast.makeText(this, "收藏成功", Toast.LENGTH_LONG).show();
                    } else if (index == 2) {
                        ContentValues values = new ContentValues();
                        values.put("url", intString);
                        values.put("category", category);
                        values.put("addtime", addtime);
                        values.put("id", id2);
                        values.put("thumb", thumb);
                        values.put("title", title);
                        values.put("tag", tags);
                        values.put("isdp", "false");
                        values.put("weburl", weburl);
                        values.put("discription", discription);
                        db.insert("data", null, values);
                        Toast.makeText(this, "收藏成功", Toast.LENGTH_LONG).show();
                    }
                    ll_dp_sc.setSelected(true);
                }
                break;
            case R.id.ll_dp_share:
                if (NetworkCenter.checkNetwork_JYH(this)) {
                    if (title == null || discription == null || weburl == null) {
                        Toast.makeText(getApplication(), "数据加载中，请稍候再试",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UmengTool.addCustomPlatforms(this, mController);
                } else {
                    Toast.makeText(getApplication(), "网络异常", Toast.LENGTH_SHORT)
                            .show();
                }
                break;

            case R.id.ll_dp_setfont:// 显示设置字体大小
                showPopupWindow(arg0);
                break;

        }
    }

    // 初始化设置字体大小的PopupWindwo
    public void initPopupWindwo(View v) {

        popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(0x000000);
        popupWindow.setFocusable(true); // 可以聚焦
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow
                .setAnimationStyle(R.style.umeng_socialize_shareboard_animation);
    }

    // 显示的状态(true代表显示了，false没有显示)
    public static boolean isShowing() {
        return popupWindow != null && popupWindow.isShowing() ? true : false;
    }

    // 显示PopupWindow
    public void showPopupWindow(View v) {

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
            if (i == lastRadioButton) {
                switch (i) {
                    case 0:
                        rb.setBackground(getResources().getDrawable(
                                R.drawable.setting_small_seletor));
                        break;
                    case 1:
                        rb.setBackground(getResources().getDrawable(
                                R.drawable.setting_between_big_seletor));
                        break;
                    case 2:
                        rb.setBackground(getResources().getDrawable(
                                R.drawable.setting_bigbig_seletor));
                        break;
                }
                rb.setChecked(true);
            } else {
                rb.setChecked(false);
            }
        }
        if (!isShowing()) {
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        }

    }

    // 取消popupWindow()
    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) { // 不为null,并且正在显示中
            popupWindow.dismiss();
            WindowManager.LayoutParams params = this.getWindow()
                    .getAttributes();
            params.alpha = 1f;
            this.getWindow().setAttributes(params);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
        if (null != mSnsPostListener) {
            mController.unregisterListener(mSnsPostListener);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 根据requestCode获取对应的SsoHandler
        UmengTool.setSSORollBack(mController, requestCode, resultCode, data);
    }

    private String imgurl = "";

    /***
     * 功能：长按图片保存到手机
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "保存到手机") {
                    new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
                } else {
                    return false;
                }
                return true;
            }
        };
        if (v instanceof WebView) {
            WebView.HitTestResult result = ((WebView) v).getHitTestResult();
            if (result != null) {
                int type = result.getType();
                if (type == WebView.HitTestResult.IMAGE_TYPE
                        || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    imgurl = result.getExtra();
                    menu.setHeaderTitle("提示");
                    menu.add(0, v.getId(), 0, "保存到手机")
                            .setOnMenuItemClickListener(handler);
                }
            }
        }
    }

    /***
     * 功能：用线程保存图片
     *
     * @author wangyp
     */
    private class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String sdcard = Environment.getExternalStorageDirectory()
                        .toString();
                File file = new File(sdcard + "/Download");
                if (!file.exists()) {
                    file.mkdirs();
                }
                int idx = imgurl.lastIndexOf(".");
                String ext = imgurl.substring(idx);
                file = new File(sdcard + "/Download/" + new Date().getTime()
                        + ext);
                InputStream inputStream = null;
                URL url = new URL(imgurl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len = 0;
                FileOutputStream outStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                outStream.close();
                result = "图片已保存至：" + file.getAbsolutePath();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(file)));
            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Utils.showToast(DPWebActivity.this, result);
        }
    }

    /**
     * 改变字体的大小
     *
     * @param index
     */
    public void changeTextSizeListener(int index) {
        switch (index) {
            case 0:
                Editor editor = preferences.edit();
                editor.putString("size", "小").commit();
                webView.getSettings().setTextSize(WebSettings.TextSize.SMALLER);
                webView.getSettings().setDefaultFontSize(75);
                break;
            case 1:
                editor = preferences.edit();
                editor.putString("size", "中").commit();
                webView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
                break;
            case 2:
                editor = preferences.edit();
                editor.putString("size", "大").commit();
                webView.getSettings().setTextSize(WebSettings.TextSize.LARGER);
                webView.setInitialScale(39);
                webView.getSettings().setBuiltInZoomControls(true);
                break;
        }

        webView.setInitialScale(39);
        webView.getSettings().setBuiltInZoomControls(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {

        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
