package com.jyh.kxt.socket;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
/**
 * 服务是否在运行判断类
 * @author PC
 *
 */
public class ServiceUtil {

	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager
				.getRunningServices(400);

		if (null == serviceInfos || serviceInfos.size() < 1) {
			return false;
		}

		for (int i = 0; i < serviceInfos.size(); i++) {
			if (serviceInfos.get(i).service.getClassName().contains(className)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
