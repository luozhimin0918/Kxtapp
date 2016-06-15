package com.jyh.kxt.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * 提高推送服务存活率
 * @author PC
 *
 */
public class NotifyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String TestTZ = TestTZIntentService.class.getName();
		if (!ServiceUtil.isServiceRunning(context, TestTZ)) {
			intent = new Intent(context, TestTZIntentService.class);
			context.startService(intent);
		}
	}

}
