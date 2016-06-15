package com.jyh.kxt.socket;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.jyh.bean.NoticBean;
import com.jyh.kxt.DPWebActivity;
import com.jyh.kxt.FlashActivity;
import com.jyh.kxt.MainActivity;
import com.jyh.kxt.PlayerActivity;
//import com.jyh.kxt.ActivityZxInfo;
//import com.jyh.kxt.CjInfoActicity;
import com.jyh.kxt.R;

/**
 * 通知类
 * 
 * @author PC
 *
 */
public class NotificationKXT {

	NotificationManager manager;
	NotificationCompat.Builder notification;
	private PendingIntent contentIntent;
	private static int count = 0;

	public static final int SDK_VERSION_CURR = 14;

	@SuppressWarnings("deprecation")
	public static int getSDKVersionNumber() {
		int sdkVersion;
		try {
			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			sdkVersion = 0;
		}
		return sdkVersion;
	}

	public void sendNoification(Context context, CjInfo cInfo, boolean pushsound) {

		boolean PRE_CUPCAKE = getSDKVersionNumber() < SDK_VERSION_CURR ? true
				: false;
		if (PRE_CUPCAKE) {
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification();
			notification.icon = R.drawable.ic_launcher;// 图标
			notification.tickerText = cInfo.getNfi();
			notification.when = System.currentTimeMillis();
			notification.defaults = Notification.DEFAULT_VIBRATE;
			if (pushsound) {
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.kxt_notify);
			}
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					R.layout.text_notification);
			findViews(contentView, cInfo, context);
			notification.contentView = contentView;// 通知显示的布局
			// Intent intent = new Intent(context, MainActivity.class); //
			// 跳到MainActivity
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
			// Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.putExtra("data", "data");

			Intent intent = new Intent(context, FlashActivity.class);

			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
			// | Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("cInfo", cInfo);
			intent.putExtra("id", cInfo.getId());
			intent.putExtra("enterpage", "notification");// shou
			intent.putExtra("type", "2");
			PendingIntent contentIntent = PendingIntent.getActivity(context,
					Integer.valueOf(cInfo.getId()), intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			notification.contentIntent = contentIntent;// 点击的事件
			notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后自动消失
			nm.notify(Integer.valueOf(cInfo.getId()), notification);
		} else {
			if (manager == null) {
				manager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
			}
			if (notification == null) {
				notification = new NotificationCompat.Builder(context);
			}
			notification.setContentTitle(cInfo.getNfi());
			notification.setSmallIcon(R.drawable.ic_launcher);
			notification.setWhen(System.currentTimeMillis());
			// 振动
			notification.setDefaults(Notification.DEFAULT_VIBRATE);
			// 响铃
			if (pushsound) {
				notification.setSound(Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.kxt_notify));
			}

			Intent notificationIntent = new Intent(context, FlashActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			notificationIntent.putExtra("id", cInfo.getId());
			notificationIntent.putExtra("enterpage", "notification");// shou
			notificationIntent.putExtra("type", "2");
			PendingIntent pendingIntent = PendingIntent.getActivity(context,
					Integer.valueOf(cInfo.getId()), notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.text_notification);
			findViews(views, cInfo, context);
			notification.setContent(views);
			notification.setAutoCancel(true);
			notification.setContentIntent(pendingIntent);
			manager.notify(Integer.valueOf(cInfo.getId()), notification.build());
		}

	}

	public void findViews(RemoteViews view, CjInfo cInfo, Context context) {
		CjInfo cjrl = cInfo;
		view.setTextViewText(R.id.calendar_item_listview_version2_nfi,
				cjrl.getState() + cjrl.getNfi());
		view.setTextViewText(R.id.calendar_listview_item_tv_title_time,
				cjrl.getPredictTime());
		view.setTextViewText(R.id.calendar_item_listview_version2_before, "前值:"
				+ cjrl.getBefore());
		view.setTextViewText(R.id.calendar_item_listview_version2_forecast,
				"预测:" + cjrl.getForecast());
		view.setTextViewText(R.id.calendar_item_listview_version2_gongbu, ""
				+ cjrl.getReality());
		if (cInfo.getNature().equals("高")) {
			view.setImageViewResource(R.id.calendar_item_nature,
					R.drawable.nature_high);
		} else if (cInfo.getNature().equals("低")) {
			view.setImageViewResource(R.id.calendar_item_nature,
					R.drawable.nature_low);
		} else if (cInfo.getNature().equals("中")) {
			view.setImageViewResource(R.id.calendar_item_nature,
					R.drawable.nature_mid);
		} else {
			view.setImageViewResource(R.id.calendar_item_nature,
					R.drawable.nature_high);
		}

		// String state = cjrl.getState(); // 国家
		//
		// if (state.equals(ConstantValue.STATE_CHINA)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_china);
		// } else if (state.equals(ConstantValue.STATE_AMERICAN)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_american);
		// } else if (state.equals(ConstantValue.STATE_GERMAN)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_german);
		// } else if (state.equals(ConstantValue.STATE_ENGLAND)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_england);
		// } else if (state.equals(ConstantValue.STATE_FRANCE)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_france);
		// } else if (state.equals(ConstantValue.STATE_AUSTRALIA)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_australia);
		// } else if (state.equals(ConstantValue.STATE_JAPAN)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_japan);
		// } else if (state.equals(ConstantValue.STATE_KOREA)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_korea);
		// } else if (state.equals(ConstantValue.STATE_CANADA)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_canada);
		// } else if (state.equals(ConstantValue.STATE_HONGKONG)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_hongkong);
		// } else if (state.equals(ConstantValue.STATE_SWITZERLAND)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_swizerland);
		// } else if (state.equals(ConstantValue.STATE_ITALY)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_italy);
		// } else if (state.equals(ConstantValue.STATE_EURO_AREA)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_europe_area);
		// } else if (state.equals(ConstantValue.STATE_NEW_ZEALAND)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_newzealand);
		// } else if (state.equals(ConstantValue.STATE_TAIWAN)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_taiwan);
		// } else if (state.equals(ConstantValue.STATE_SPANISH)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_spanish);
		// } else if (state.equals(ConstantValue.STATE_SINGAPORE)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_singapore);
		// } else if (state.equals(ConstantValue.STATE_BRAZIL)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_brazil);
		// } else if (state.equals(ConstantValue.STATE_SOUTH_AFRICA)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_south_africa);
		// } else if (state.equals(ConstantValue.STATE_INDIA)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_india);
		// } else if (state.equals(ConstantValue.STATE_INDONESIA)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_indonesia);
		// } else if (state.equals(ConstantValue.STATE_RUSSIA)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_russia);
		// } else if (state.equals(ConstantValue.STATE_GREECE)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_greece);
		// } else if (state.equals(ConstantValue.STATE_ISRAEL)) {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_israel);
		// } else {
		// view.setImageViewResource(R.id.calendar_item_img_state,
		// R.drawable.state_default);
		// // 没有就设一个默认的
		// }

	}

	// 13:40
	public void send(Context context, int icon, String title, String content,
			boolean pushsound, int id, NoticBean bean) {
		Intent intent = null;
		// 1 得到通知管理器
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// 2构建通知
		// Notification notification = new Notification(R.drawable.ic_launcher,
		// title, System.currentTimeMillis());
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setDefaults(Notification.DEFAULT_VIBRATE);
		builder.setWhen(System.currentTimeMillis());
		// 振动
		// notification.defaults |= Notification.DEFAULT_VIBRATE;
		// 响铃
		if (pushsound) {
			// notification.sound = Uri.parse("android.resource://"
			// + context.getPackageName() + "/" + R.raw.kxt_notify); //
			// Uri.parse(String

			builder.setSound(Uri.parse("android.resource://"
					+ context.getPackageName() + "/" + R.raw.kxt_notify));
		}
		// 3设置通知的点击事件
		if (null == bean) {
			intent = new Intent(context, FlashActivity.class); // 跳到MainActivity
			intent.putExtra("id", "" + id);
			intent.putExtra("enterpage", "notification");// shou
			intent.putExtra("type", "1");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			contentIntent = PendingIntent.getActivity(context, id, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		} else if (bean.getAction().getType().equals("video")) {
			Log.i("zml", "url=" + bean.getAction().getUrl());
			intent = new Intent(context, PlayerActivity.class);
			intent.putExtra("url", bean.getAction().getUrl());
			intent.putExtra("title", bean.getTitle());
			intent.putExtra("share", bean.getAction().getShareurl());
			intent.putExtra("type", bean.getAction().getType());
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);

			contentIntent = PendingIntent.getActivity(context, count
					+ new Random(100000).nextInt(), intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			count++;
		} else {
			intent = new Intent(context, DPWebActivity.class); // 跳到AdWebActivity
			intent.putExtra("url", bean.getAction().getUrl());
			intent.putExtra("title", bean.getTitle());
			intent.putExtra("share", bean.getAction().getShareurl());
			intent.putExtra("type", bean.getAction().getType());
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			contentIntent = PendingIntent.getActivity(context, count
					+ new Random(100000).nextInt(), intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			count++;
		}

		// notification.setLatestEventInfo(context, title, content,
		// contentIntent);

		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setContentIntent(contentIntent);
		builder.setAutoCancel(true);// 点击通知之后自动消失
		// builder.set
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		// 4发送通知
		nm.notify(count + new Random(100000).nextInt(), builder.build());
	}

	public void sendTitle(Context context, int icon, String title,
			String content, boolean pushsound) {
		Intent intent = null;
		// 1 得到通知管理器
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// 2构建通知
		// Notification notification = new Notification(R.drawable.ic_launcher,
		// title, System.currentTimeMillis());
		// // 振动
		// notification.defaults |= Notification.DEFAULT_VIBRATE;

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setDefaults(Notification.DEFAULT_VIBRATE);
		builder.setWhen(System.currentTimeMillis());
		// 响铃
		if (pushsound) {
			// notification.sound = Uri.parse("android.resource://"
			// + context.getPackageName() + "/" + R.raw.kxt_notify); //
			// Uri.parse(String
			builder.setSound(Uri.parse("android.resource://"
					+ context.getPackageName() + "/" + R.raw.kxt_notify));
		}
		intent = new Intent(context, MainActivity.class); // 跳到MainActivity
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		contentIntent = PendingIntent.getActivity(context,
				Integer.valueOf(content), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		count++;

		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setContentIntent(contentIntent);
		builder.setAutoCancel(true);// 点击通知之后自动消失
		// notification.setLatestEventInfo(context, title, content,
		// contentIntent);
		// // builder.set
		// notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后自动消失
		// 4发送通知
		nm.notify(count + new Random(100000).nextInt(), builder.build());
	}

	public void custom(Context context, int icon, String title, String content) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;// 图标
		notification.tickerText = "拦截到了新的短信";
		RemoteViews contentView = new RemoteViews(context.getPackageName(),
				R.layout.kxt_notification_content);
		notification.contentView = contentView;// 通知显示的布局
		Intent intent = new Intent(context, MainActivity.class); // 跳到MainActivity
		PendingIntent contentIntent = PendingIntent.getActivity(context, 100,
				intent, 0);
		notification.contentIntent = contentIntent;// 点击的事件
		notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后自动消失
		nm.notify(100, notification);
	}

}
