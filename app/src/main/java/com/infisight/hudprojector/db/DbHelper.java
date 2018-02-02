package com.infisight.hudprojector.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper
{
	private static final String DB_NAME = "TvtTraffic.db";
	public static final String TABLE_NAME = "TableTraffic";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_TRAFFIC = "traffic";
	
	public DbHelper(Context context) 
	{
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.beginTransaction();
		try 
		{
//			db.execSQL(DbConstants.CREATE_TRAFFIC_TABLE_SQL.toString());
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (_id INTEGER primary key autoincrement,"
			+ " date STRING, type STRING, traffic long)");
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

}
