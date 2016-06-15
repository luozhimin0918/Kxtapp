package com.jyh.kxt;

import org.json.JSONException;
import org.json.JSONObject;

import com.jyh.kxt.customtool.ProgressWebView;
import com.jyh.kxt.socket.NetworkCenter;
import com.jyh.tool.UmengTool;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.soexample.commons.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 广告界面
 *
 * @author PC
 */
public class AdWebActivity extends Activity implements OnClickListener {

    private ProgressWebView webview;
    private SharedPreferences preferences;
    private boolean Isyj = false;
    private TextView title_tv;
    private ImageView adweb_img_back, adweb_ima_share;
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(Constants.DESCRIPTOR);
    private String url;
    private String title;
    private String image;
    private LinearLayout adweb_zt_color;
    private String share;
    private String type;
    private UMImage urlImage;
    private View view_ad;
    private Intent intent;
    private String adweb;
    private String description = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getSharedPreferences("setup", Context.MODE_PRIVATE);
        Isyj = preferences.getBoolean("yj_btn", false);
        if (Isyj) {
            this.setTheme(R.style.BrowserThemeNight);
        } else {
            this.setTheme(R.style.BrowserThemeDefault);
        }
        // 透明状态栏
        getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明导航栏
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.commom_web);
        adweb_zt_color = (LinearLayout) findViewById(R.id.adweb_zt_color);
        if (Isyj) {
            adweb_zt_color.setBackgroundColor(Color.parseColor("#04274c"));
        } else {
            adweb_zt_color.setBackgroundColor(Color.parseColor("#116bcc"));
        }
        view_ad = (View) findViewById(R.id.view_ad);
        title_tv = (TextView) findViewById(R.id.adweb_title_tv);
        adweb_ima_share = (ImageView) findViewById(R.id.adweb_ima_share);
        adweb_img_back = (ImageView) findViewById(R.id.adweb_img_back);
        intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("image");
        share = intent.getStringExtra("shareurl");
        adweb = intent.getStringExtra("adweb");
        type = intent.getStringExtra("type");
        // ~~~ 绑定控件
        webview = (ProgressWebView) findViewById(R.id.webview);
        if (!NetworkCenter.checkNetworkConnection(this)) {
            webview.setVisibility(View.GONE);
            view_ad.setVisibility(View.VISIBLE);
            Toast.makeText(AdWebActivity.this, "网络异常", Toast.LENGTH_SHORT)
                    .show();
        }
        // ~~~ 设置数据
        // titleText.setText(name);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBlockNetworkImage(true);
        webview.getSettings().setBuiltInZoomControls(true);//
        webview.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBlockNetworkImage(true);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//
        // 排版适应屏幕
        webview.getSettings().setUseWideViewPort(true);// 可任意比例缩放
        webview.getSettings().setLoadWithOverviewMode(true);//
        // setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                view.loadUrl("javascript:alert( $('#app_data').html() )");
            }

        });
        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                // TODO Auto-generated method stub
                if (message != null && !message.equals("")) {

                    try {
                        JSONObject object = new JSONObject(message);
                        title = object.getString("title");
                        share = object.getString("weburl");
                        image = object.getString("thumb");
                        description = object.getString("description");
                        UmengTool.setShareContent(mController,
                                AdWebActivity.this, title, share, description,
                                image);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                result.confirm();
                return true;
            }

        });
        adweb_ima_share.setOnClickListener(this);
        adweb_img_back.setOnClickListener(this);
        title_tv.setText(title);
        webview.loadUrl(url);
        UmengTool.configPlatforms(this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.adweb_ima_share:
                if (null == adweb || adweb.equals("")) {
                    title = null;
                    share = null;
                    urlImage = null;
                    webview.loadUrl("javascript:alert( $('#app_data').html() )");
                }
                if (title == null || share == null || urlImage == null) {
                    Toast.makeText(getApplication(), "数据加载中，请稍候再试",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                UmengTool.addCustomPlatforms(this, mController);
                break;
            case R.id.adweb_img_back:
                if (null == type) {
                    finish();
                } else if (type.contains("dian")) {
                    Intent intent = new Intent(AdWebActivity.this,
                            MainActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                } else if (type.contains("new")) {
                    Intent intent = new Intent(AdWebActivity.this,
                            MainActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 根据requestCode获取对应的SsoHandler
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
                requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(AdWebActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        // 拦截MENU按钮点击事件，让他无任何操作
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}