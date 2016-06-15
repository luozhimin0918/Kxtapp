package com.jyh.kxt.socket;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * 提高推送服务存活率
 * 
 * @author PC
 *
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
	private static AlarmManager am;
	private PendingIntent sender;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				if (intent.getAction().equals("MyBroadcastReceiver")) {
					// TODO Auto-generated method stub
					InitTSSocket(context);
					String clsName = TopRAIntentService.class.getName();
					String clsName2 = RAIntentService.class.getName();

					if (!KXTApplication.IsOut) {
						if (!ServiceUtil.isServiceRunning(context, clsName)) {
							InitTopRAIntent(context);
						}
						if (!ServiceUtil.isServiceRunning(context, clsName2)) {
							InitRAIntent(context);
						}
					}
					InitData(context);
				} else if (Intent.ACTION_MEDIA_MOUNTED.equals(intent
						.getAction())) {
					// InitTSSocket(context);
				} else if (Intent.ACTION_POWER_CONNECTED.equals(intent
						.getAction())) {
					// InitTSSocket(context);
				} else if (Intent.ACTION_POWER_DISCONNECTED.equals(intent
						.getAction())) {
					// InitTSSocket(context);
				}
			};
		}.start();

	}

	private void InitTSSocket(final Context context) {
		Intent intent;
		String TestTZ = TestTZIntentService.class.getName();
		if (!ServiceUtil.isServiceRunning(context, TestTZ)) {
			intent = new Intent(context, TestTZIntentService.class);
			context.startService(intent);
		}
	}

	private void InitData(Context context) {
		if (null == am) {
			Intent intent = new Intent();
			intent.setAction("MyBroadcastReceiver");
			sender = PendingIntent.getBroadcast(context, 0, intent, 0);
			// 开始时间
			long firstime = SystemClock.elapsedRealtime() + 5 * 1000;
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.setRepeating(AlarmManager.RTC_WAKEUP, firstime, 5 * 1000, sender);
		}
	}

	private void InitRAIntent(Context context) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, RAIntentService.class);
		context.startService(intent);
	}

	private void InitTopRAIntent(Context context) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, TopRAIntentService.class);
		context.startService(intent);
	}

}
