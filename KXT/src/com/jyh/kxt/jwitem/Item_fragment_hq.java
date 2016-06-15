//package com.jyh.kxt.jwitem;
//
//import com.jyh.kxt.R;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.webkit.WebSettings.RenderPriority;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class Item_fragment_hq extends Fragment {
//	private String uri;
//	private WebView webView;
//	// private Dialog dialog;
//	private ProgressBar progressBar;
//	private SharedPreferences preferences;
//	private RelativeLayout progressBar_bg;
//	private boolean Isyj = false;
//	private TextView hq_tv;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		Bundle args = getArguments();
//		uri = args != null ? args.getString("uri") : "";
//		super.onCreate(savedInstanceState);
//	}
//
//	@SuppressWarnings("deprecation")
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		preferences = getActivity().getSharedPreferences("setup",
//				Context.MODE_PRIVATE);
//		Isyj = preferences.getBoolean("yj_btn", false);
//
//		View view = inflater.inflate(R.layout.fragment_hq_body, null);
//		webView = (WebView) view.findViewById(R.id.webView1);
//		hq_tv = (TextView) view.findViewById(R.id.hq_tv);
//		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
//		progressBar_bg = (RelativeLayout) view
//				.findViewById(R.id.progressBar_bg);
//		webView.setWebViewClient(new MyWebViewClient());
//		webView.getSettings().setJavaScriptEnabled(true);// 是否支持JavaScript
//		webView.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
//		webView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放
//		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
//		webView.getSettings().setBlockNetworkImage(true);
//		if (Isyj) {
//			webView.loadUrl(uri + "?yejian=1");
//		} else {
//			webView.loadUrl(uri);
//		}
//		return view;
//	}
//
//	public class MyWebViewClient extends WebViewClient {
//		@Override
//		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			view.loadUrl(url);
//			return true;
//		}
//
//		@Override
//		public void onPageStarted(WebView view, String url, Bitmap favicon) {
//			super.onPageStarted(view, url, favicon);
//			progressBar_bg.setVisibility(View.VISIBLE);
//			progressBar.setVisibility(View.VISIBLE);
//
//		}
//
//		@Override
//		public void onPageFinished(WebView view, String url) {
//			super.onPageFinished(view, url);
//			webView.getSettings().setBlockNetworkImage(false);
//			progressBar.setVisibility(View.GONE);
//			progressBar_bg.setVisibility(View.GONE);
//		}
//
//		@Override
//		public void onReceivedError(WebView view, int errorCode,
//				String description, String failingUrl) {
//			// TODO Auto-generated method stub
//			super.onReceivedError(view, errorCode, description, failingUrl);
//			webView.setVisibility(View.GONE);
//			hq_tv.setVisibility(View.VISIBLE);
//			Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
//		}
//	}
//
//}
