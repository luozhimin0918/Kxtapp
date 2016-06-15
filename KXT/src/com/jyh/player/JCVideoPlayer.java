package com.jyh.player;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.jyh.kxt.R;
import com.jyh.tool.DensityUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>
 * 节操视频播放器，库的外面所有使用的接口也在这里
 * </p>
 * <p>
 * Jiecao video player，all outside the library interface is here
 * </p>
 *
 * @see <a
 *      href="https://github.com/lipangit/jiecaovideoplayer">JiecaoVideoplayer
 *      Github</a> Created by Nathen On 2015/11/30 11:59
 */
public class JCVideoPlayer extends FrameLayout implements View.OnClickListener,
		SurfaceHolder.Callback, View.OnTouchListener,
		JCMediaManager.JCMediaPlayerListener, OnGestureListener {

	public ImageView ivStart;
	ProgressBar pbLoading, pbBottom;
	public ImageView ivFullScreen;
	SeekBar skProgress;
	TextView tvTimeCurrent, tvTimeTotal;
	ResizeSurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	TextView tvTitle;
	public ImageView ivThumb, back;
	RelativeLayout rlParent, bgView;
	LinearLayout llTitleContainer, llBottomControl;
	ImageView ivCover;

	private String url;
	private String title;
	private HashMap<String, String> header;
	public boolean ifFullScreen = false;
	private boolean ifShowTitle = false;
	private boolean mIsPrepared = false;
	private boolean ifMp3 = false;

	private int enlargRecId = 0;
	private int shrinkRecId = 0;

	private int surfaceId;

	public int CURRENT_STATE = -1;// -1相当于null
	public static final int CURRENT_STATE_PREPAREING = 0;
	public static final int CURRENT_STATE_PAUSE = 1;
	public static final int CURRENT_STATE_PLAYING = 2;
	public static final int CURRENT_STATE_OVER = 3;
	public static final int CURRENT_STATE_NORMAL = 4;
	public static final int CURRENT_STATE_ERROR = 5;

	private OnTouchListener mSeekbarOnTouchListener;
	private static Timer mDismissControlViewTimer;
	private static Timer mUpdateProgressTimer;
	private static long clickfullscreentime;
	private static final int FULL_SCREEN_NORMAL_DELAY = 5000;

	private boolean touchingProgressBar = false;
	public static boolean isClickFullscreen = false;// 一会调试一下，看是不是需要这个
	public boolean isFullscreenFromNormal = false;

	private static ImageView.ScaleType speScalType = null;

	private static JCBuriedPoint JC_BURIED_POINT;
	private Handler handler;
	private Context context;
	private RelativeLayout root_layout;
	private GestureDetector gestureDetector;
	private AudioManager audiomanager;
	private int maxVolume;
	private int currentVolume;
	private RelativeLayout gesture_volume_layout;// 音量控制布局
	private TextView geture_tv_volume_percentage;// 音量百分比
	private ImageView gesture_iv_player_volume;// 音量图标
	private RelativeLayout gesture_progress_layout;// 进度图标
	private TextView geture_tv_progress_time;// 播放时间进度
	private ImageView gesture_iv_progress;// 快进或快退标志
	private static final float STEP_PROGRESS = 2f;// 设定进度滑动时的步长，避免每次滑动都改变，导致改变过快
	private static final float STEP_VOLUME = 2f;// 协调音量滑动时的步长，避免每次滑动都改变，导致改变过快
	private boolean firstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
	private int GESTURE_FLAG = 0;// 1,调节进度，2，调节音量
	private static final int GESTURE_MODIFY_PROGRESS = 1;
	private static final int GESTURE_MODIFY_VOLUME = 2;
	private long palyerCurrentPosition = 0;// 模拟进度播放的当前标志，毫秒
	private long playerDuration = 0;// 模拟播放资源总时长，毫秒
	private boolean isOnline = false;
	private android.widget.RelativeLayout.LayoutParams layoutParams;
	private final static int HIDE_CONTROLER = 1;
	private final static int TIME = 6868;
	private final static int PROGRESS_CHANGED = 0;
	private final static int SCREEN_FULL = 0;
	private final static int SCREEN_DEFAULT = 1;
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private boolean isHd = false;// 是否有滑动屏幕

	public JCVideoPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mIsPrepared = false;
		this.context = context;
		View.inflate(context, R.layout.video_control_view, this);
		ivStart = (ImageView) findViewById(R.id.start);
		pbLoading = (ProgressBar) findViewById(R.id.loading);
		pbBottom = (ProgressBar) findViewById(R.id.bottom_progressbar);
		ivFullScreen = (ImageView) findViewById(R.id.fullscreen);
		back = (ImageView) findViewById(R.id.back);
		skProgress = (SeekBar) findViewById(R.id.progress);
		skProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		tvTimeCurrent = (TextView) findViewById(R.id.current);
		tvTimeTotal = (TextView) findViewById(R.id.total);
		llBottomControl = (LinearLayout) findViewById(R.id.bottom_control);
		tvTitle = (TextView) findViewById(R.id.title);
		ivThumb = (ImageView) findViewById(R.id.thumb);
		rlParent = (RelativeLayout) findViewById(R.id.parentview);
		bgView = (RelativeLayout) findViewById(R.id.bgview);

		llTitleContainer = (LinearLayout) findViewById(R.id.title_container);
		ivCover = (ImageView) findViewById(R.id.cover);
		ivStart.setOnClickListener(this);
		llTitleContainer.setOnClickListener(this);
		ivThumb.setOnClickListener(this);
		ivFullScreen.setOnClickListener(this);
		llBottomControl.setOnClickListener(this);
		rlParent.setOnClickListener(this);
		skProgress.setOnTouchListener(this);
		if (speScalType != null) {
			ivThumb.setScaleType(speScalType);
		}

		//
		root_layout = (RelativeLayout) findViewById(R.id.course_surfaceview_layout__md);
		root_layout.setOnClickListener(this);
		gesture_volume_layout = (RelativeLayout) findViewById(R.id.gesture_volume_layout);
		gesture_progress_layout = (RelativeLayout) findViewById(R.id.gesture_progress_layout);
		geture_tv_progress_time = (TextView) findViewById(R.id.geture_tv_progress_time);
		geture_tv_volume_percentage = (TextView) findViewById(R.id.geture_tv_volume_percentage);
		gesture_iv_progress = (ImageView) findViewById(R.id.gesture_iv_progress);
		gesture_iv_player_volume = (ImageView) findViewById(R.id.gesture_iv_player_volume);

		gestureDetector = new GestureDetector(context, this);
		root_layout.setLongClickable(true);
		gestureDetector.setIsLongpressEnabled(true);
		root_layout.setOnTouchListener(this);
		audiomanager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
		currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
	}

	/**
	 * <p>
	 * 配置要播放的内容
	 * </p>
	 * <p>
	 * Configuring the Content to Play
	 * </p>
	 *
	 * @param url
	 *            视频地址 | Video address
	 * @param title
	 *            标题 | title
	 */
	public void setUp(String url, String title, HashMap<String, String> header,
			Handler handler) {
		setUp(url, title, true, header, handler);
	}

	/**
	 * <p>
	 * 配置要播放的内容
	 * </p>
	 * <p>
	 * Configuring the Content to Play
	 * </p>
	 *
	 * @param url
	 *            视频地址 | Video address
	 * @param title
	 *            标题 | title
	 * @param ifShowTitle
	 *            是否在非全屏下显示标题 | The title is displayed in full-screen under
	 */
	public void setUp(String url, String title, boolean ifShowTitle,
			HashMap<String, String> header, Handler handler) {
		this.ifShowTitle = ifShowTitle;
		if ((System.currentTimeMillis() - clickfullscreentime) < FULL_SCREEN_NORMAL_DELAY)
			return;
		this.url = url;
		this.title = title;
		this.ifFullScreen = false;
		this.header = header;
		this.handler = handler;
		CURRENT_STATE = CURRENT_STATE_NORMAL;
		if (ifFullScreen) {
			ivFullScreen
					.setImageResource(enlargRecId == 0 ? R.drawable.shrink_video
							: enlargRecId);
		} else {
			ivFullScreen
					.setImageResource(shrinkRecId == 0 ? R.drawable.enlarge_video
							: shrinkRecId);
		}
		if (!TextUtils.isEmpty(url) && url.contains(".mp3")) {
			ifMp3 = true;
			ivFullScreen.setVisibility(View.GONE);
		}
		tvTitle.setText(title);

		changeUiToNormal();

		if (JCMediaManager.intance().listener == this) {
			JCMediaManager.intance().mediaPlayer.stop();
		}

	}

	/**
	 * <p>
	 * 只在全全屏中调用的方法
	 * </p>
	 * <p>
	 * Only in fullscreen can call this
	 * </p>
	 *
	 * @param url
	 *            视频地址 | Video address
	 * @param title
	 *            标题 | title
	 */
	public void setUpForFullscreen(String url, String title) {
		this.url = url;
		this.title = title;
		ifShowTitle = true;
		ifFullScreen = true;
		CURRENT_STATE = CURRENT_STATE_NORMAL;
		if (ifFullScreen) {
			ivFullScreen
					.setImageResource(shrinkRecId == 0 ? R.drawable.shrink_video
							: shrinkRecId);
		} else {
			ivFullScreen
					.setImageResource(enlargRecId == 0 ? R.drawable.enlarge_video
							: enlargRecId);
		}
		tvTitle.setText(title);
		if (!TextUtils.isEmpty(url) && url.contains(".mp3")) {
			ifMp3 = true;
		}
		changeUiToNormal();
	}

	/**
	 * <p>
	 * 只在全全屏中调用的方法
	 * </p>
	 * <p>
	 * Only in fullscreen can call this
	 * </p>
	 *
	 * @param state
	 *            int state
	 */
	public void setState(int state) {
		this.CURRENT_STATE = state;
		// 全屏或取消全屏时继续原来的状态
		if (CURRENT_STATE == CURRENT_STATE_PREPAREING) {
			changeUiToShowUiPrepareing();
			setProgressAndTime(0, 0, 0);
			setProgressBuffered(0);
		} else if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
			changeUiToShowUiPlaying();
		} else if (CURRENT_STATE == CURRENT_STATE_PAUSE) {
			changeUiToShowUiPause();
		} else if (CURRENT_STATE == CURRENT_STATE_NORMAL) {
			setSurfaceViewBg();
			changeUiToNormal();
			cancelDismissControlViewTimer();
			cancelProgressTimer();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			surfaceView.setBackgroundColor(Color.TRANSPARENT);
		} else if (CURRENT_STATE == CURRENT_STATE_ERROR) {
			JCMediaManager.intance().mediaPlayer.release();
			changeUiToError();
		}
	}

	/**
	 * 目前认为详细的判断和重复的设置是有相当必要的,也可以包装成方法
	 */
	@Override
	public void onClick(View v) {

		int i = v.getId();
		if (i == R.id.start || i == R.id.thumb) {
			cancelDelayHide();
			isHd = false;
			if (TextUtils.isEmpty(url)) {
				Toast.makeText(getContext(), "视频数据加载失败", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (i == R.id.thumb) {
				if (CURRENT_STATE != CURRENT_STATE_NORMAL) {
					onClickUiToggle();
					return;
				}
			}
			if (CURRENT_STATE == CURRENT_STATE_NORMAL
					|| CURRENT_STATE == CURRENT_STATE_ERROR) {
				bgView.setVisibility(View.VISIBLE);
				rlParent.setVisibility(View.GONE);
				addSurfaceView();

				if (JCMediaManager.intance().listener != null) {
					JCMediaManager.intance().listener.onCompletion();
				}
				JCMediaManager.intance().listener = this;
				JCMediaManager.intance().clearWidthAndHeight();
				CURRENT_STATE = CURRENT_STATE_PREPAREING;
				changeUiToShowUiPrepareing();
				llBottomControl.setVisibility(View.INVISIBLE);
				llTitleContainer.setVisibility(View.INVISIBLE);
				setProgressAndTime(0, 0, 0);
				setProgressBuffered(0);

				JCMediaManager.intance().prepareToPlay(getContext(), url,
						header);
				surfaceView.requestLayout();
				setKeepScreenOn(true);

				if (JC_BURIED_POINT != null
						&& JCMediaManager.intance().listener == this) {

					if (i == R.id.start) {
						JC_BURIED_POINT.POINT_START_ICON(title, url);
					} else {
						JC_BURIED_POINT.POINT_START_THUMB(title, url);
					}
				}
				bgView.setVisibility(View.GONE);
				rlParent.setVisibility(View.VISIBLE);
			} else if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
				CURRENT_STATE = CURRENT_STATE_PAUSE;

				changeUiToShowUiPause();

				JCMediaManager.intance().mediaPlayer.pause();

				setKeepScreenOn(false);
				cancelDismissControlViewTimer();

				if (JC_BURIED_POINT != null
						&& JCMediaManager.intance().listener == this) {
					if (ifFullScreen) {
						JC_BURIED_POINT.POINT_STOP_FULLSCREEN(title, url);
					} else {
						JC_BURIED_POINT.POINT_STOP(title, url);
					}
				}
			} else if (CURRENT_STATE == CURRENT_STATE_PAUSE) {
				CURRENT_STATE = CURRENT_STATE_PLAYING;

				changeUiToShowUiPlaying();
				JCMediaManager.intance().mediaPlayer.start();

				setKeepScreenOn(true);
				startDismissControlViewTimer();

				if (JC_BURIED_POINT != null
						&& JCMediaManager.intance().listener == this) {
					if (ifFullScreen) {
						JC_BURIED_POINT.POINT_RESUME_FULLSCREEN(title, url);
					} else {
						JC_BURIED_POINT.POINT_RESUME(title, url);
					}
				}
			}

		} else if (i == R.id.fullscreen) {
			if (ifFullScreen && handler != null) {
				ifFullScreen = false;
				ifShowTitle = false;
				handler.sendEmptyMessage(3);// 竖屏
			} else {
				ifFullScreen = true;
				ifShowTitle = false;
				handler.sendEmptyMessage(2);// 横屏
			}
			clickfullscreentime = System.currentTimeMillis();
		} else if (i == surfaceId || i == R.id.parentview) {

			if (CURRENT_STATE == CURRENT_STATE_ERROR) {
				ivStart.performClick();
			} else {
				if (isHd) {
					isHd = false;
					changeUiToShowUiPlayingForHd();
					startDismissControlViewTimer();
					return;
				} else {
					onClickUiToggle();
					startDismissControlViewTimer();
				}

			}
		} else if (i == R.id.bottom_control) {
		} else if (i == R.id.title_container) {
			if (ifFullScreen) {
				ivFullScreen.performClick();
			}
		} else {
		}
	}

	public void ToFullScreen() {
		JCMediaManager.intance().mediaPlayer.pause();
		JCMediaManager.intance().mediaPlayer.setDisplay(null);
		JCMediaManager.intance().lastListener = this;
		JCMediaManager.intance().listener = null;
		isClickFullscreen = true;
		clickfullscreentime = System.currentTimeMillis();
	}

	void addSurfaceView() {

		if (root_layout.getChildAt(0) instanceof ResizeSurfaceView) {
			root_layout.removeViewAt(0);
		}
		surfaceView = new ResizeSurfaceView(getContext());
		surfaceView.setBackgroundColor(Color.BLACK);
		surfaceId = surfaceView.getId();
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceView.setOnClickListener(this);
		surfaceView.setOnTouchListener(this);
		layoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		root_layout.addView(surfaceView, 0, layoutParams);
	}

	public void setSurfaceViewBg() {
		if (null != surfaceView) {
			surfaceView.setBackgroundColor(Color.BLACK);
		}
	}

	private void startDismissControlViewTimer() {
		cancelDismissControlViewTimer();
		mDismissControlViewTimer = new Timer();
		mDismissControlViewTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (getContext() != null && getContext() instanceof Activity) {
					((Activity) getContext()).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (CURRENT_STATE != CURRENT_STATE_NORMAL) {
								llBottomControl.setVisibility(View.INVISIBLE);
								pbBottom.setVisibility(View.VISIBLE);
								setTitleVisibility(View.INVISIBLE);
								ivStart.setVisibility(View.INVISIBLE);
							}
						}
					});
				}
			}
		}, 2500);
	}

	private void cancelDismissControlViewTimer() {
		if (mDismissControlViewTimer != null) {
			mDismissControlViewTimer.cancel();
		}
	}

	public void changUiForHd() {

	}

	private void onClickUiToggle() {
		if (CURRENT_STATE == CURRENT_STATE_PREPAREING) {
			if (llBottomControl.getVisibility() == View.VISIBLE) {
				changeUiToClearUiPrepareing();
			} else {
				changeUiToShowUiPrepareing();
			}
		} else if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
			if (llBottomControl.getVisibility() == View.VISIBLE) {
				changeUiToClearUiPlaying();
			} else {
				changeUiToShowUiPlaying();
			}
		} else if (CURRENT_STATE == CURRENT_STATE_PAUSE) {
			if (llBottomControl.getVisibility() == View.VISIBLE) {
				changeUiToClearUiPause();
			} else {
				changeUiToShowUiPause();
			}
		}
	}

	// Unified management Ui
	private void changeUiToNormal() {
		setTitleVisibility(View.VISIBLE);
		llBottomControl.setVisibility(View.INVISIBLE);
		ivStart.setVisibility(View.VISIBLE);
		pbLoading.setVisibility(View.INVISIBLE);
		setThumbVisibility(View.VISIBLE);
		ivCover.setVisibility(View.VISIBLE);
		pbBottom.setVisibility(View.INVISIBLE);
		updateStartImage();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void changeUiToShowUiPrepareing() {
		setTitleVisibility(View.VISIBLE);
		llBottomControl.setVisibility(View.VISIBLE);
		ivStart.setVisibility(View.INVISIBLE);
		pbLoading.setVisibility(View.VISIBLE);
		setThumbVisibility(View.INVISIBLE);
		ivCover.setVisibility(View.VISIBLE);
		pbBottom.setVisibility(View.INVISIBLE);
	}

	private void changeUiToClearUiPrepareing() {
		// changeUiToClearUi();
		setTitleVisibility(View.INVISIBLE);
		llBottomControl.setVisibility(View.INVISIBLE);
		ivStart.setVisibility(View.INVISIBLE);
		setThumbVisibility(View.INVISIBLE);
		pbBottom.setVisibility(View.INVISIBLE);
		pbLoading.setVisibility(View.VISIBLE);
		ivCover.setVisibility(View.VISIBLE);
	}

	private void changeUiToShowUiPlayingForHd() {
		setTitleVisibility(View.VISIBLE);
		llBottomControl.setVisibility(View.VISIBLE);
		ivStart.setVisibility(View.INVISIBLE);
		pbLoading.setVisibility(View.INVISIBLE);
		setThumbVisibility(View.INVISIBLE);
		ivCover.setVisibility(View.INVISIBLE);
		pbBottom.setVisibility(View.INVISIBLE);
		updateStartImage();
	}

	private void changeUiToShowUiPlaying() {
		setTitleVisibility(View.VISIBLE);
		llBottomControl.setVisibility(View.VISIBLE);
		ivStart.setVisibility(View.VISIBLE);
		pbLoading.setVisibility(View.INVISIBLE);
		setThumbVisibility(View.INVISIBLE);
		ivCover.setVisibility(View.INVISIBLE);
		pbBottom.setVisibility(View.INVISIBLE);
		updateStartImage();
	}

	private void changeUiToClearUiPlaying() {
		changeUiToClearUi();
		pbBottom.setVisibility(View.VISIBLE);
	}

	private void changeUiToShowUiPause() {
		setTitleVisibility(View.VISIBLE);
		llBottomControl.setVisibility(View.VISIBLE);
		ivStart.setVisibility(View.VISIBLE);
		pbLoading.setVisibility(View.INVISIBLE);
		setThumbVisibility(View.INVISIBLE);
		ivCover.setVisibility(View.INVISIBLE);
		pbBottom.setVisibility(View.INVISIBLE);
		updateStartImage();
	}

	private void changeUiToClearUiPause() {
		changeUiToClearUi();
		pbBottom.setVisibility(View.VISIBLE);
	}

	private void changeUiToClearUi() {
		setTitleVisibility(View.INVISIBLE);
		llBottomControl.setVisibility(View.INVISIBLE);
		ivStart.setVisibility(View.INVISIBLE);
		pbLoading.setVisibility(View.INVISIBLE);
		setThumbVisibility(View.INVISIBLE);
		ivCover.setVisibility(View.INVISIBLE);
		pbBottom.setVisibility(View.INVISIBLE);
	}

	private void changeUiToError() {
		setTitleVisibility(View.INVISIBLE);
		llBottomControl.setVisibility(View.INVISIBLE);
		ivStart.setVisibility(View.VISIBLE);
		pbLoading.setVisibility(View.INVISIBLE);
		setThumbVisibility(View.INVISIBLE);
		ivCover.setVisibility(View.VISIBLE);
		pbBottom.setVisibility(View.INVISIBLE);
		updateStartImage();
	}

	private void startProgressTimer() {
		cancelProgressTimer();
		mUpdateProgressTimer = new Timer();
		mUpdateProgressTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (getContext() != null && getContext() instanceof Activity) {
					((Activity) getContext()).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
								setProgressAndTimeFromTimer();
							}
						}
					});
				}
			}
		}, 0, 300);
	}

	private void cancelProgressTimer() {
		if (mUpdateProgressTimer != null) {
			mUpdateProgressTimer.cancel();
		}
	}

	// if show title in top level logic
	private void setTitleVisibility(int visable) {
		if (ifShowTitle) {
			llTitleContainer.setVisibility(visable);

		} else {
			if (ifFullScreen) {
				llTitleContainer.setVisibility(visable);
				if (ifFullScreen) {
					back.setVisibility(View.VISIBLE);
				} else {
					back.setVisibility(View.GONE);
				}
			} else {
				llTitleContainer.setVisibility(View.INVISIBLE);
			}
		}
	}

	// if show thumb in top level logic
	private void setThumbVisibility(int visable) {
		if (ifMp3) {
			ivThumb.setVisibility(View.VISIBLE);
		} else {
			ivThumb.setVisibility(visable);
		}
	}

	private void updateStartImage() {
		if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
			ivStart.setImageResource(R.drawable.click_video_pause_selector);
		} else if (CURRENT_STATE == CURRENT_STATE_ERROR) {
			ivStart.setImageResource(R.drawable.click_video_error_selector);
		} else {
			ivStart.setImageResource(R.drawable.click_video_play_selector);
		}
	}

	private void setProgressBuffered(int secProgress) {
		if (secProgress >= 0) {
			skProgress.setSecondaryProgress(secProgress);
			pbBottom.setSecondaryProgress(secProgress);
		}
	}

	private void setProgressAndTimeFromTimer() {
		int position = JCMediaManager.intance().mediaPlayer
				.getCurrentPosition();
		int duration = JCMediaManager.intance().mediaPlayer.getDuration();
		// if duration == 0 (e.g. in HLS streams) avoids ArithmeticException
		int progress = position * 100 / (duration == 0 ? 1 : duration);
		setProgressAndTime(progress, position, duration);
	}

	private void setProgressAndTime(int progress, int currentTime, int totalTime) {
		if (!touchingProgressBar) {
			// skProgress.setProgress(progress);
			pbBottom.setProgress(progress);
		}
		tvTimeCurrent.setText(Utils.stringForTime(currentTime));
		tvTimeTotal.setText(Utils.stringForTime(totalTime));
	}

	public void release() {
		if ((System.currentTimeMillis() - clickfullscreentime) < FULL_SCREEN_NORMAL_DELAY)
			return;
		setState(CURRENT_STATE_NORMAL);
		// 回收surfaceview
	}


	public void quitFullScreen() {
		isClickFullscreen = false;
		// JCFullScreenActivity.manualQuit = true;
		clickfullscreentime = System.currentTimeMillis();
		JCMediaManager.intance().mediaPlayer.pause();
		JCMediaManager.intance().mediaPlayer.setDisplay(null);
		// 这个view释放了，
		JCMediaManager.intance().listener = JCMediaManager.intance().lastListener;
		JCMediaManager.intance().lastState = CURRENT_STATE;
		JCMediaManager.intance().listener.onBackFullscreen();
	}

	private void stopToFullscreenOrQuitFullscreenShowDisplay() {
		if (CURRENT_STATE == CURRENT_STATE_PAUSE) {
			JCMediaManager.intance().mediaPlayer.start();
			CURRENT_STATE = CURRENT_STATE_PLAYING;
			new Thread(new Runnable() {
				@Override
				public void run() {
					((Activity) getContext()).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							JCMediaManager.intance().mediaPlayer.pause();
							CURRENT_STATE = CURRENT_STATE_PAUSE;
						}
					});
				}
			}).start();
			surfaceView.requestLayout();
		} else if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
			JCMediaManager.intance().mediaPlayer.start();
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (ifFullScreen) {
			JCMediaManager.intance().mediaPlayer.setDisplay(surfaceHolder);
			stopToFullscreenOrQuitFullscreenShowDisplay();
		}
		if (CURRENT_STATE != CURRENT_STATE_NORMAL) {
			startDismissControlViewTimer();
			startProgressTimer();
		}

		if (JCMediaManager.intance().lastListener == this) {
			JCMediaManager.intance().mediaPlayer.setDisplay(surfaceHolder);
			stopToFullscreenOrQuitFullscreenShowDisplay();
			startDismissControlViewTimer();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	/**
	 * <p>
	 * 停止所有音频的播放
	 * </p>
	 * <p>
	 * release all videos
	 * </p>
	 */
	public static void releaseAllVideos() {
		if (!isClickFullscreen) {
			JCMediaManager.intance().mediaPlayer.stop();
			if (JCMediaManager.intance().listener != null) {
				JCMediaManager.intance().listener.onCompletion();
			}
			if (mUpdateProgressTimer != null) {
				mUpdateProgressTimer.cancel();
			}
		}
	}

	/**
	 * <p>
	 * 有特殊需要的客户端
	 * </p>
	 * <p>
	 * Clients with special needs
	 * </p>
	 *
	 * @param onClickListener
	 *            开始按钮点击的回调函数 | Click the Start button callback function
	 */
	@Deprecated
	public void setStartListener(OnClickListener onClickListener) {
		if (onClickListener != null) {
			ivStart.setOnClickListener(onClickListener);
			ivThumb.setOnClickListener(onClickListener);
		} else {
			ivStart.setOnClickListener(this);
			ivThumb.setOnClickListener(this);
		}
	}

	public void setSeekbarOnTouchListener(OnTouchListener listener) {
		mSeekbarOnTouchListener = listener;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (R.id.progress == v.getId()) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchingProgressBar = true;
				cancelDismissControlViewTimer();
				cancelProgressTimer();
				break;
			case MotionEvent.ACTION_UP:
				touchingProgressBar = false;
				startDismissControlViewTimer();
				startProgressTimer();
				break;
			}

			if (mSeekbarOnTouchListener != null) {
				mSeekbarOnTouchListener.onTouch(v, event);
			}
			return false;
		} else {
			if (event.getAction() == MotionEvent.ACTION_UP) {

				GESTURE_FLAG = 0;// 手指离开屏幕后，重置调节音量或进度的标志
				gesture_volume_layout.setVisibility(View.INVISIBLE);
				gesture_progress_layout.setVisibility(View.INVISIBLE);
			}
			if (CURRENT_STATE != CURRENT_STATE_PAUSE) {
				return gestureDetector.onTouchEvent(event);
			}
			return false;
		}

	}

	/**
	 * <p>
	 * 默认的缩略图的scaleType是fitCenter，这时候图片如果和屏幕尺寸不同的话左右或上下会有黑边，
	 * 可以根据客户端需要改成fitXY或这其他模式
	 * </p>
	 * <p>
	 * The default thumbnail scaleType is fitCenter, and this time the picture
	 * if different screen sizes up and down or left and right, then there will
	 * be black bars, or it may need to change fitXY other modes based on the
	 * client
	 * </p>
	 *
	 * @param thumbScaleType
	 *            缩略图的scalType | Thumbnail scaleType
	 */
	public static void setThumbImageViewScalType(
			ImageView.ScaleType thumbScaleType) {
		speScalType = thumbScaleType;
	}

	/**
	 * In demo is ok, but in other project This will class not access
	 * exception,How to solve the problem
	 *
	 * @param context
	 *            Context
	 * @param url
	 *            video url
	 * @param title
	 *            video title
	 */

	@Override
	public void onPrepared() {

		setVideoScale(SCREEN_DEFAULT);
		int i = JCMediaManager.intance().mediaPlayer.getDuration();
		playerDuration = i;
		skProgress.setMax(i);
		myHandler.sendEmptyMessage(PROGRESS_CHANGED);

		mIsPrepared = true;
		if (CURRENT_STATE != CURRENT_STATE_PREPAREING)
			return;
		JCMediaManager.intance().mediaPlayer.setDisplay(surfaceHolder);
		JCMediaManager.intance().mediaPlayer.start();
		CURRENT_STATE = CURRENT_STATE_PLAYING;

		ivStart.setVisibility(View.INVISIBLE);
		hideControllerDelay();
		startDismissControlViewTimer();
		startProgressTimer();
		setSurfaceViewBg();

		changeUiToShowUiPlaying();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		surfaceView.setBackgroundColor(Color.TRANSPARENT);

	}

	@Override
	public void onCompletion() {
		CURRENT_STATE = CURRENT_STATE_NORMAL;
		cancelProgressTimer();
		cancelDismissControlViewTimer();
		setKeepScreenOn(false);
		changeUiToNormal();

		if (JC_BURIED_POINT != null
				&& JCMediaManager.intance().listener == this) {
			if (ifFullScreen) {
				JC_BURIED_POINT.POINT_AUTO_COMPLETE_FULLSCREEN(title, url);
			} else {
				JC_BURIED_POINT.POINT_AUTO_COMPLETE(title, url);
			}
		}
		if (isFullscreenFromNormal) {// 如果在进入全屏后播放完就初始化自己非全屏的控件
			isFullscreenFromNormal = false;
			JCMediaManager.intance().lastListener.onCompletion();
		}

	}

	@Override
	public void onBufferingUpdate(int percent) {
		if (CURRENT_STATE != CURRENT_STATE_NORMAL
				|| CURRENT_STATE != CURRENT_STATE_PREPAREING) {
			setProgressBuffered(percent);
		}
	}

	@Override
	public void onSeekComplete() {
		pbLoading.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onError(int what, int extra) {

		isOnline = false;
		if (what != -38) {
			setState(CURRENT_STATE_ERROR);
		}
	}

	@Override
	public void onVideoSizeChanged() {
		int mVideoWidth = JCMediaManager.intance().currentVideoWidth;
		int mVideoHeight = JCMediaManager.intance().currentVideoHeight;
		if (mVideoWidth != 0 && mVideoHeight != 0) {
			surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
			surfaceView.requestLayout();
		}
	}

	@Override
	public void onBackFullscreen() {
		CURRENT_STATE = JCMediaManager.intance().lastState;
		addSurfaceView();
		setState(CURRENT_STATE);
	}

	public static void setJcBuriedPoint(JCBuriedPoint jcBuriedPoint) {
		JC_BURIED_POINT = jcBuriedPoint;
	}

	public void seekTo(int msec) {
		if (JCMediaManager.intance() != null && mIsPrepared) {
			JCMediaManager.intance().mediaPlayer.seekTo(msec);
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		firstScroll = true;// 设定是触摸屏幕后第一次scroll的标志
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		isHd = false;
		// TODO Auto-generated method stub
		if (firstScroll) {// 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
			// 横向的距离变化大则调整进度，纵向的变化大则调整音量
			if (Math.abs(distanceX) >= Math.abs(distanceY)) {
				gesture_volume_layout.setVisibility(View.INVISIBLE);
				gesture_progress_layout.setVisibility(View.VISIBLE);
				GESTURE_FLAG = GESTURE_MODIFY_PROGRESS;
			} else {
				gesture_volume_layout.setVisibility(View.VISIBLE);
				gesture_progress_layout.setVisibility(View.INVISIBLE);
				GESTURE_FLAG = GESTURE_MODIFY_VOLUME;
			}
		}
		// 如果每次触摸屏幕后第一次scroll是调节进度，那之后的scroll事件都处理音量进度，直到离开屏幕执行下一次操作
		if (GESTURE_FLAG == GESTURE_MODIFY_PROGRESS) {
			isHd = true;
			// distanceX=lastScrollPositionX-currentScrollPositionX，因此为正时是快进
			if (Math.abs(distanceX) > Math.abs(distanceY)) {// 横向移动大于纵向移动
				if (distanceX >= DensityUtil.dip2px(context, STEP_PROGRESS)) {// 快退，用步长控制改变速度，可微调
					gesture_iv_progress
							.setImageResource(R.drawable.souhu_player_backward);
					if (palyerCurrentPosition > 3 * 1000) {// 避免为负
						palyerCurrentPosition -= 3 * 1000;// scroll方法执行一次快退3秒
					} else {
						palyerCurrentPosition = 3 * 1000;
					}
				} else if (distanceX <= -DensityUtil.dip2px(context,
						STEP_PROGRESS)) {// 快进

					gesture_iv_progress
							.setImageResource(R.drawable.souhu_player_forward);

					if (palyerCurrentPosition < playerDuration - 16 * 1000) {// 避免超过总时长
						palyerCurrentPosition += 3 * 1000;// scroll执行一次快进3秒
					} else {
						palyerCurrentPosition = playerDuration - 10 * 1000;
					}
				}
			}

			geture_tv_progress_time
					.setText(converLongTimeToStr(palyerCurrentPosition) + "/"
							+ converLongTimeToStr(playerDuration));

			seekTo((int) palyerCurrentPosition);

		}
		// 如果每次触摸屏幕后第一次scroll是调节音量，那之后的scroll事件都处理音量调节，直到离开屏幕执行下一次操作
		else if (GESTURE_FLAG == GESTURE_MODIFY_VOLUME) {
			isHd = true;
			currentVolume = audiomanager
					.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
			if (Math.abs(distanceY) > Math.abs(distanceX)) {// 纵向移动大于横向移动
				if (distanceY >= DensityUtil.dip2px(context, STEP_VOLUME)) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
					if (currentVolume < maxVolume) {// 为避免调节过快，distanceY应大于一个设定值
						currentVolume++;
					}
					gesture_iv_player_volume
							.setImageResource(R.drawable.souhu_player_volume);
				} else if (distanceY <= -DensityUtil.dip2px(context,
						STEP_VOLUME)) {// 音量调小
					if (currentVolume > 0) {
						currentVolume--;
						if (currentVolume == 0) {// 静音，设定静音独有的图片
							gesture_iv_player_volume
									.setImageResource(R.drawable.souhu_player_silence);
						}
					}
				}
				int percentage = (currentVolume * 100) / maxVolume;
				geture_tv_volume_percentage.setText(percentage + "%");
				audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC,
						currentVolume, 0);
			}

		}

		firstScroll = false;// 第一次scroll执行完成，修改标志

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 转换毫秒数成“分、秒”，如“01:53”。若超过60分钟则显示“时、分、秒”，如“01:01:30
	 * 
	 * @param 待转换的毫秒数
	 * */
	private String converLongTimeToStr(long time) {
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;

		long hour = (time) / hh;
		long minute = (time - hour * hh) / mi;
		long second = (time - hour * hh - minute * mi) / ss;

		String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;
		if (hour > 0) {
			return strHour + ":" + strMinute + ":" + strSecond;
		} else {
			return strMinute + ":" + strSecond;
		}
	}

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				if (!isOnline) {
					JCMediaManager.intance().mediaPlayer.seekTo(progress);
					palyerCurrentPosition = progress;
					// pbLoading.setVisibility(View.VISIBLE);
					// ivStart.setVisibility(View.INVISIBLE);
				}
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			myHandler.removeMessages(HIDE_CONTROLER);
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
		}

	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case PROGRESS_CHANGED:

				int i = JCMediaManager.intance().mediaPlayer
						.getCurrentPosition();

				skProgress.setProgress(i);

				if (isOnline) {
					int j = JCMediaManager.intance().getBufferPercentage();
					skProgress.setSecondaryProgress(j * skProgress.getMax()
							/ 100);
				} else {
					skProgress.setSecondaryProgress(0);
				}

				i /= 1000;

				sendEmptyMessageDelayed(PROGRESS_CHANGED, 100);
				break;

			case HIDE_CONTROLER:
				break;
			}

			super.handleMessage(msg);
		}
	};

	public void MyDestroy() {
		myHandler.removeMessages(PROGRESS_CHANGED);
		myHandler.removeMessages(HIDE_CONTROLER);

	}

	private void cancelDelayHide() {
		myHandler.removeMessages(HIDE_CONTROLER);
	}

	private void hideControllerDelay() {
		myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}

	public void setVideoScale(int width, int height) {
		layoutParams.height = height;
		layoutParams.width = width;
		setLayoutParams(layoutParams);
	}

	private void setVideoScale(int flag) {
		switch (flag) {
		case SCREEN_FULL:

			setVideoScale(screenWidth, screenHeight);
			((Activity) context).getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			break;

		case SCREEN_DEFAULT:

			int videoWidth = JCMediaManager.intance().mediaPlayer
					.getVideoWidth();
			int videoHeight = JCMediaManager.intance().mediaPlayer
					.getVideoHeight();
			int mWidth = screenWidth;
			int mHeight = screenHeight - 25;

			if (videoWidth > 0 && videoHeight > 0) {
				if (videoWidth * mHeight > mWidth * videoHeight) {
					mHeight = mWidth * videoHeight / videoWidth;
				} else if (videoWidth * mHeight < mWidth * videoHeight) {
					mWidth = mHeight * videoWidth / videoHeight;
				} else {

				}
			}

			break;
		}
	}

}
