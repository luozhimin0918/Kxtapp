package com.jyh.kxt.sqlte;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SCDataSqlte extends SQLiteOpenHelper {

	public SCDataSqlte(Context context) {
		super(context, "data", null, 5);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		arg0.execSQL("create table data (url text,category text,addtime text,id text,thumb text,title text,tag text,isdp text,discription text,weburl text)");
		arg0.execSQL("create table vedio (url text,id text,image_url text,title text,discription text,share_url text,play_count text)");
		
		arg0.execSQL("create table flash (id text,type text,title text,url text,share_url text,"
				+ "data_id text,data_type text,data_title text,data_time text,data_importance text,data_state text,data_predicttime text,"
				+ "data_effect text,data_before text,data_forecast text,data_reality text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		if (arg1 < arg2) {
			arg0.execSQL("DROP TABLE IF EXISTS data");
			arg0.execSQL("DROP TABLE IF EXISTS vedio");
			arg0.execSQL("DROP TABLE IF EXISTS flash");
			onCreate(arg0);
		}
	}

}
