package com.jyh.kxt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.jyh.kxt.R;
import com.jyh.kxt.customtool.OnImageTouchedListener;
import com.jyh.kxt.customtool.ZoomableImageView;
import com.jyh.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class ShowWebImageActivity extends Activity {
	private String imagePath = null;
	private ZoomableImageView imageView = null;
	private RelativeLayout save_btn;
	private boolean IsVis = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_webimage);
		this.imagePath = getIntent().getStringExtra("image");
		imageView = (ZoomableImageView) findViewById(R.id.show_webimage_imageview);
		save_btn = (RelativeLayout) findViewById(R.id.save_btn);
		ImageLoader.getInstance().loadImage(this.imagePath,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
						imageView
								.setImageBitmap(((BitmapDrawable) getResources()
										.getDrawable(R.drawable.empty_photo))
										.getBitmap());
					}

					@Override
					public void onLoadingComplete(String imageUri,
							View view, Bitmap loadedImage) {
						// TODO Auto-generated method stub
						imageView.setImageBitmap(loadedImage);
					}

					@Override
					public void onLoadingCancelled(String imageUri,
							View view) {
						// TODO Auto-generated method stub
						imageView
								.setImageBitmap(((BitmapDrawable) getResources()
										.getDrawable(R.drawable.empty_photo))
										.getBitmap());
					}
				});

		imageView.setOnImageTouchedListener(new OnImageTouchedListener() {

			@Override
			public void onImageTouched() {
				// TODO Auto-generated method stub
				if (!IsVis) {
					handler.sendEmptyMessage(2);
				} else {
					finish();
				}
			}
		});
		save_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != imagePath) {
					new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
				}
			}
		});
		handler.sendEmptyMessageDelayed(1, 3 * 1000);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				IsVis = false;
				save_btn.setVisibility(View.GONE);
				break;
			case 2:
				IsVis = true;
				save_btn.setVisibility(View.VISIBLE);
				handler.sendEmptyMessageDelayed(1, 5 * 1000);
				break;
			default:
				break;
			}
		};
	};

	public Drawable loadImageFromUrl(String url) throws IOException {
		// URL m = new URL(url);
		// InputStream i = (InputStream) m.getContent();
		Drawable d = null;

		return d;
	}

	/***
	 * 功能：用线程保存图片
	 * 
	 * @author wangyp
	 * 
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
				int idx = imagePath.lastIndexOf(".");
				String ext = imagePath.substring(idx);
				file = new File(sdcard + "/Download/" + new Date().getTime()
						+ ext);
				InputStream inputStream = null;
				URL url = new URL(imagePath);
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
			Utils.showToast(ShowWebImageActivity.this, result);
		}
	}
}
