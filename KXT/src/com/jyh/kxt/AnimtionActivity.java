package com.jyh.kxt;

import com.jyh.kxt.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
/**
 * 切换界面
 * @author PC
 *
 */
public class AnimtionActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dh);
		Intent intent = new Intent(AnimtionActivity.this, MainActivity.class);
		intent.putExtra("enter", "enter");
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}
}
