package com.jyh.kxt;

import com.jyh.kxt.R;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FlashLinkActiviy extends Activity {
	private WebView flash_webView;
	private TextView flash_link_text;
	private RelativeLayout out_rel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flash_link);
		out_rel = (RelativeLayout) findViewById(R.id.out_rel);
		flash_webView = (WebView) findViewById(R.id.link_web);
		flash_link_text = (TextView) findViewById(R.id.title_link);
		WebSettings settings = flash_webView.getSettings();
		settings.setBlockNetworkImage(true);
		flash_webView.setWebViewClient(new FlashLinkClient());
		WebChromeClient wvcc = new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				flash_link_text.setText(title);
			}

		};
		out_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FlashLinkActiviy.this.finish();
			}
		});
		flash_webView.setWebChromeClient(wvcc);
		if (null != getIntent().getStringExtra("code")) {
			flash_webView.loadUrl(getIntent().getStringExtra("code"));
		} else {
			flash_webView.loadUrl("http://www.kxt.com/");
		}
	}

	public class FlashLinkClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView arg0, String arg1) {
			arg0.getSettings().setBlockNetworkImage(false);
			super.onPageFinished(arg0, arg1);
		}

		@Override
		public void onReceivedError(WebView arg0, int arg1, String arg2,
				String arg3) {
			// TODO Auto-generated method stub
			super.onReceivedError(arg0, arg1, arg2, arg3);
			flash_webView.setVisibility(View.GONE);
			Toast.makeText(FlashLinkActiviy.this, "数据加载失败", Toast.LENGTH_LONG)
					.show();
		}
	}
}
