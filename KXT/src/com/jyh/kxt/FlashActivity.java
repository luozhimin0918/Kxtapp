package com.jyh.kxt;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jyh.kxt.R;
import com.jyh.kxt.socket.DateTimeUtil;
import com.jyh.kxt.sqlte.SCDataSqlte;
import com.jyh.tool.UmengTool;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.soexample.commons.Constants;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class FlashActivity extends Activity implements OnClickListener {
    private LinearLayout flash_web_linear, ll_flash_share, ll_flash_setfont,
            ll_flash_sc, ll_flash_back;
    private WebView flash_webView1;
    private SharedPreferences preferences;
    private boolean Isyj = false;
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(Constants.DESCRIPTOR);
    private SnsPostListener mSnsPostListener;
    private TextView flash_error_tv;
    private static View popupView;
    private RadioGroup radioGroup;
    private LinearLayout pop_linear;
    private static int lastRadioButton = 1;
    private static PopupWindow popupWindow;
    private SCDataSqlte dataSqlte;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Integer i = 0;
    private boolean IsLoadOk = false;
    // cj字段
    private String data_predicttime = "";
    private String data_state = "";
    private String data_reality = "";
    private String data_before = "";
    private String data_forecast = "";
    private String data_effect = "";
    // zx字段 cj公用
    private String type = "";
    private String id = "";
    private String title = "";
    private String time = "";
    private String data_id = "";
    private String data_type = "";
    private String data_title = "";
    private String data_time = "";
    private String data_importance = "";
    // zx字段

    // 文章字段
    private String url = "";
    private String discription = "";
    private String share = "";
    private String categroy = "";
    private String tags = "";
    private String thumb = "";
    // webview加载地址
    private String loadUrl;
    // 进入界面
    private String enterpage;
    private Intent scIntent = new Intent();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
        Isyj = preferences.getBoolean("yj_btn", false);
        if (Isyj) {
            this.setTheme(R.style.BrowserThemeNight);
        } else {
            this.setTheme(R.style.BrowserThemeDefault);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_flashweb);
        dataSqlte = new SCDataSqlte(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        enterpage = intent.getStringExtra("enterpage");
        type = intent.getStringExtra("type");
        loadUrl = "http://appapi.kxt.com/Kxcj/view2/?id=" + id;
        InitFind();
        registerForContextMenu(flash_webView1);
        UmengTool.configPlatforms(this);
        if (mSnsPostListener == null) {
            mSnsPostListener = new SnsPostListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onComplete(SHARE_MEDIA platform, int stCode,
                                       SocializeEntity entity) {
                    if (stCode == 200) {
                        Toast.makeText(FlashActivity.this, "分享成功",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FlashActivity.this, "分享失败",
                                Toast.LENGTH_LONG).show();
                    }
                }
            };
            mController.registerListener(mSnsPostListener);
        }
    }

    private void InitFind() {
        // TODO Auto-generated method stub
        flash_web_linear = (LinearLayout) findViewById(R.id.flash_web_linear);
        flash_error_tv = (TextView) findViewById(R.id.flash_error_tv);
        ll_flash_back = (LinearLayout) findViewById(R.id.ll_flash_back);
        ll_flash_sc = (LinearLayout) findViewById(R.id.ll_flash_sc);
        ll_flash_setfont = (LinearLayout) findViewById(R.id.ll_flash_setfont);
        flash_webView1 = (WebView) findViewById(R.id.flash_webView1);
        ll_flash_share = (LinearLayout) findViewById(R.id.ll_flash_share);
        popupView = getLayoutInflater().inflate(R.layout.popupwindow_setfont,
                null);
        radioGroup = (RadioGroup) popupView
                .findViewById(R.id.radiogroup_setting);
        pop_linear = (LinearLayout) popupView.findViewById(R.id.pop_linear);
        /**
         *
         */
        flash_webView1.setWebViewClient(new MyWebViewClient());
        flash_webView1.getSettings().setJavaScriptEnabled(true);// 是否支持JavaScript
        // flash_webView1.getSettings().setBuiltInZoomControls(true);//
        flash_webView1.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
        flash_webView1.getSettings().setJavaScriptEnabled(true);
        // flash_webView1.getSettings().setBlockNetworkImage(true);

        flash_webView1.getSettings().setBlockNetworkImage(true);
        flash_webView1.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//
        // 排版适应屏幕
        flash_webView1.getSettings().setUseWideViewPort(true);// 可任意比例缩放
        flash_webView1.getSettings().setLoadWithOverviewMode(true);//
        // setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        flash_webView1.getSettings().setDomStorageEnabled(true);
        flash_webView1.getSettings().setMediaPlaybackRequiresUserGesture(false);
        intWeb();
        ll_flash_back.setOnClickListener(this);
        ll_flash_sc.setOnClickListener(this);
        ll_flash_setfont.setOnClickListener(this);
        ll_flash_share.setOnClickListener(this);
        InitData();
    }

    public void intWeb() {
        if (Build.VERSION.SDK_INT >= 19) {
            flash_webView1.getSettings().setLoadsImagesAutomatically(true);
        } else {
            flash_webView1.getSettings().setLoadsImagesAutomatically(false);
        }
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Isyj) {
                flash_webView1.loadUrl(url + "&yejian=1");
            } else {
                flash_webView1.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // flash_web_relative.setVisibility(View.VISIBLE);
            // flash_web_linear.setVisibility(View.INVISIBLE);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // flash_webView1.getSettings().setBuiltInZoomControls(true);
            // flash_webView1.getSettings().setBlockNetworkImage(false);
            // flash_web_relative.setVisibility(View.INVISIBLE);
            if (!flash_webView1.getSettings().getLoadsImagesAutomatically()) {
                flash_webView1.getSettings().setLoadsImagesAutomatically(true);
            }
            flash_web_linear.setVisibility(View.VISIBLE);
            preferences = getSharedPreferences("websetup", Context.MODE_PRIVATE);
            String textsize = preferences.getString("size", "中");
            if (textsize.contains("中")) {
                flash_webView1.getSettings().setTextSize(
                        WebSettings.TextSize.NORMAL);
            } else if (textsize.contains("小")) {
                flash_webView1.getSettings().setTextSize(
                        WebSettings.TextSize.SMALLER);
            } else if (textsize.contains("大")) {
                flash_webView1.getSettings().setTextSize(
                        WebSettings.TextSize.LARGER);
            }
            view.loadUrl("javascript:alert( $('#app_data').text() )");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(FlashActivity.this, "数据加载失败", Toast.LENGTH_SHORT)
                    .show();
            flash_webView1.setVisibility(View.GONE);
            flash_error_tv.setVisibility(View.VISIBLE);
        }
    }

    private void InitData() {
        // TODO Auto-generated method stub

        flash_webView1.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                // TODO Auto-generated method stub
                try {
                    JSONObject object = new JSONObject(message);
                    if (null != object.getString("type")) {
                        if (object.getString("type").equals("2")) {
                            ResolveCJ(object);
                            i = 1;
                        } else if (object.getString("type").equals("1")) {
                            ResolveZX(object);
                            i = 1;
                        } else if (object.getString("type").equals("news")) {
                            ResolveNews(object);
                            i = 2;
                        }
                    } else {
                        Toast.makeText(FlashActivity.this, "获取分享数据失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                result.confirm();
                return true;
            }
        });

        flash_webView1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && flash_webView1.canGoBack()) { // 表示按返回键
                        flash_webView1.goBack(); // 后退
                        return true; // 已处理
                    }
                }
                return false;
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
        pop_linear.setAlpha(0.0f);
        initPopupWindwo(ll_flash_setfont);
        pop_linear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (Isyj) {
            flash_webView1.loadUrl(loadUrl + "&yejian=1");
        } else {
            flash_webView1.loadUrl(loadUrl);
        }
    }

    protected void ResolveNews(JSONObject object) throws Exception {
        // TODO Auto-generated method stub
        id = object.getString("id");
        type = object.getString("type");
        title = object.getString("title");
        url = object.getString("url");
        share = object.getString("weburl");
        thumb = object.getString("thumb");
        time = DateTimeUtil.DateStr(Long.parseLong(object.getString("addtime")
                + "000"));
        discription = object.getString("description");
        categroy = object.getString("category_id");
        JSONArray _tags = object.getJSONArray("tags");
        tags = "";
        for (int i = 0; i < _tags.length(); i++) {
            tags = tags + "," + _tags.getString(i);
        }
        db = dataSqlte.getReadableDatabase();
        cursor = db.query("data", null, "id=? and category=?", new String[]{
                id, categroy}, null, null, null);
        boolean b = cursor.moveToFirst();
        if (b == true) {
            ll_flash_sc.setSelected(true);
        } else {
            ll_flash_sc.setSelected(false);
        }
        IsLoadOk = true;
        cursor.close();
        db.close();
    }

    protected void ResolveZX(JSONObject object) throws Exception {
        // TODO Auto-generated method stub
        id = object.getString("id");
        type = object.getString("type");
        title = object.getString("title");
        share = object.getString("share_url");
        url = object.getString("url");

        thumb = "";
        JSONObject jsonObject = object.getJSONObject("data");
        data_id = jsonObject.getString("id");
        data_type = jsonObject.getString("type");
        data_title = jsonObject.getString("title");
        data_time = jsonObject.getString("time");
        data_importance = jsonObject.getString("importance");

        db = dataSqlte.getReadableDatabase();
        cursor = db.query("flash", null, "id=?", new String[]{id}, null,
                null, null);
        boolean b = cursor.moveToFirst();
        if (b == true) {
            ll_flash_sc.setSelected(true);
        } else {
            ll_flash_sc.setSelected(false);
        }
        IsLoadOk = true;
        cursor.close();
        db.close();
    }

    protected void ResolveCJ(JSONObject object) throws Exception {
        // TODO Auto-generated method stub
        id = object.getString("id");
        type = object.getString("type");
        title = object.getString("title");
        share = object.getString("share_url");
        url = object.getString("url");
        thumb = "";
        JSONObject jsonObject = object.getJSONObject("data");
        // "state":"日本",
        // "predicttime":1458108000,
        // "effect":"金银 日元 石油||",
        // "before":"-22.6%",
        // "forecast":"-22.6%",
        // "reality":"-22.5%",
        data_id = jsonObject.getString("id");
        data_type = jsonObject.getString("type");
        data_title = jsonObject.getString("title");
        data_time = jsonObject.getString("time");
        data_importance = jsonObject.getString("importance");

        data_forecast = jsonObject.getString("forecast");
        data_predicttime = jsonObject.getString("predicttime");
        data_effect = jsonObject.getString("effect");
        data_state = jsonObject.getString("state");
        data_reality = jsonObject.getString("reality");
        data_before = jsonObject.getString("before");

        db = dataSqlte.getReadableDatabase();
        cursor = db.query("flash", null, "id=?", new String[]{id}, null,
                null, null);
        boolean b = cursor.moveToFirst();
        if (b == true) {
            ll_flash_sc.setSelected(true);
        } else {
            ll_flash_sc.setSelected(false);
        }
        IsLoadOk = true;
        cursor.close();
        db.close();
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

    /**
     * 改变字体的大小
     *
     * @param index
     */
    @SuppressWarnings("deprecation")
    public void changeTextSizeListener(int index) {

        switch (index) {
            case 0:
                Editor editor = preferences.edit();
                editor.putString("size", "小").commit();
                flash_webView1.getSettings().setTextSize(
                        WebSettings.TextSize.SMALLER);
                break;
            case 1:
                editor = preferences.edit();
                editor.putString("size", "中").commit();
                flash_webView1.getSettings().setTextSize(
                        WebSettings.TextSize.NORMAL);

                break;
            case 2:
                editor = preferences.edit();
                editor.putString("size", "大").commit();
                flash_webView1.getSettings().setTextSize(
                        WebSettings.TextSize.LARGER);
                flash_webView1.setInitialScale(39);
                flash_webView1.getSettings().setBuiltInZoomControls(true);
                break;
        }

        flash_webView1.setInitialScale(39);
        flash_webView1.getSettings().setBuiltInZoomControls(true);
    }

    // 初始化设置字体大小的PopupWindwo
    public void initPopupWindwo(View v) {

        popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(-00000);
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.ll_flash_back:
                if (enterpage.equals("flash")) {
                } else if (enterpage.equals("shou")) {
                    // 收藏返回
                    setResult(100, scIntent);
                } else if (enterpage.equals("notification")) {
                    Intent intent = new Intent(FlashActivity.this,
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                finish();
                break;
            case R.id.ll_flash_sc:
                if (IsLoadOk) {
                    db = dataSqlte.getWritableDatabase();
                    if (ll_flash_sc.isSelected()) {
                        if (db != null) {
                            if (i == 2) {
                                db.delete("data", "id=? and category=?",
                                        new String[]{id, categroy});
                                scIntent.putExtra("value", "wz");
                            } else if (i == 1) {
                                db.delete("flash", "id=?", new String[]{id});
                            }
                            ll_flash_sc.setSelected(false);
                            Toast.makeText(this, "取消收藏", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "操作失败", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (db == null) {
                            return;
                        }
                        if (i == 1) {
                            ContentValues values = new ContentValues();

                            // id,type,title,url,share_url data_id data_type
                            // data_title data_time
                            // data_importance data_state data_predicttime
                            // data_effect data_before data_forecast data_reality
                            values.put("id", id);
                            values.put("type", type);
                            values.put("title", title);
                            values.put("url", url);
                            values.put("share_url", share);
                            values.put("data_id", data_id);
                            values.put("data_type", data_type);
                            values.put("data_title", data_title);
                            values.put("data_time", data_time);
                            values.put("data_importance", data_importance);
                            values.put("data_state", data_state);
                            values.put("data_predicttime", data_predicttime);
                            values.put("data_effect", data_effect);
                            values.put("data_before", data_before);
                            values.put("data_forecast", data_forecast);
                            values.put("data_reality", data_reality);
                            db.insert("flash", null, values);
                            Toast.makeText(this, "收藏成功", Toast.LENGTH_LONG).show();
                        } else if (i == 2) {
                            ContentValues values = new ContentValues();
                            values.put("url", url);
                            values.put("category", categroy);
                            values.put("addtime", time);
                            values.put("id", id);
                            values.put("thumb", thumb);
                            values.put("title", title);
                            values.put("tag", tags);
                            values.put("isdp", "false");
                            values.put("weburl", share);
                            values.put("discription", discription);
                            db.insert("data", null, values);
                            Toast.makeText(this, "收藏成功", Toast.LENGTH_LONG).show();
                            scIntent.putExtra("value", "wz");
                        }
                        ll_flash_sc.setSelected(true);
                    }
                } else {
                    Toast.makeText(this, "数据加载中，请稍候再试", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ll_flash_setfont:
                showPopupWindow(v);
                break;
            case R.id.ll_flash_share:
                if (!IsLoadOk) {
                    Toast.makeText(getApplication(), "数据加载中，请稍候再试",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                UmengTool.setShareContent(mController, FlashActivity.this, title,
                        share, discription, thumb);
                UmengTool.addCustomPlatforms(this, mController);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (null != mSnsPostListener) {
            mController.unregisterListener(mSnsPostListener);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (enterpage.equals("flash")) {
            } else if (enterpage.equals("shou")) {
                // 收藏返回
                setResult(100, scIntent);
            } else if (enterpage.equals("notification")) {
                Intent intent = new Intent(FlashActivity.this,
                        MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        UmengTool.setSSORollBack(mController, requestCode, resultCode, data);
    }
}
