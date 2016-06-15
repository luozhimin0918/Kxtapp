//package com.jyh.player;
//
//import com.jyh.kxt.R;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//
///**
// * <p>
// * 全屏的activity
// * </p>
// * <p>
// * fullscreen activity
// * </p>
// * Created by Nathen
// * 
// * On 2015/12/01 11:17
// */
//public class JCFullScreenActivity extends Activity {
//
//	static void toActivityFromNormal(Context context, int state, String url,
//			String title) {
//		STATE = state;
//		URL = url;
//		TITLE = title;
//		start = false;
//		Intent intent = new Intent(context, JCFullScreenActivity.class);
//		context.startActivity(intent);
//	}
//
//	/**
//	 * <p>
//	 * 直接进入全屏播放
//	 * </p>
//	 * <p>
//	 * Full screen play video derictly
//	 * </p>
//	 *
//	 * @param context
//	 *            context
//	 * @param url
//	 *            video url
//	 * @param title
//	 *            video title
//	 */
//	public static void toActivity(Context context, String url, String title) {
//		STATE = JCVideoPlayer.CURRENT_STATE_NORMAL;
//		URL = url;
//		TITLE = title;
//		start = true;
//		Intent intent = new Intent(context, JCFullScreenActivity.class);
//		context.startActivity(intent);
//	}
//
//	JCVideoPlayer jcVideoPlayer;
//	/**
//	 * 刚启动全屏时的播放状态
//	 */
//	public static int STATE = -1;
//	public static String URL;
//	public static String TITLE;
//	public static boolean manualQuit = false;
//	static boolean start = false;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		View decor = this.getWindow().getDecorView();
//		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//		setContentView(R.layout.activity_fullscreen);
//
//		jcVideoPlayer = (JCVideoPlayer) findViewById(R.id.jcvideoplayer);
//		jcVideoPlayer.setUpForFullscreen(URL, TITLE);
//		jcVideoPlayer.setState(STATE);
//		manualQuit = false;
//		if (start) {
//			jcVideoPlayer.ivStart.performClick();
//		} else {
//			jcVideoPlayer.isFullscreenFromNormal = true;
//			jcVideoPlayer.addSurfaceView();
//			if (JCMediaManager.intance().listener != null) {
//				JCMediaManager.intance().listener.onCompletion();
//			}
//			JCMediaManager.intance().listener = jcVideoPlayer;
//		}
//		handler.sendEmptyMessageDelayed(1, 3 * 1000);
//	}
//
//	Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case 1:
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//				break;
//
//			default:
//				break;
//			}
//		};
//	};
//
//	@Override
//	public void onBackPressed() {
//		JCVideoPlayer.isClickFullscreen = false;
//		jcVideoPlayer.quitFullScreen();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		if (!manualQuit) {
//			JCVideoPlayer.isClickFullscreen = false;
//			jcVideoPlayer.quitFullScreen();
//			JCVideoPlayer.releaseAllVideos();
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//			finish();
//
//		}
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		// TODO Auto-generated method stub
//		super.onConfigurationChanged(newConfig);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
//			} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//				jcVideoPlayer.quitFullScreen();
//			}
//		}
//	}
//}
