package com.digdream.tasbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoDB extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "notepad_db";
	private final static int DATABASE_VERSION = 1;
	
	public ToDoDB(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建table
		String sql1 = "CREATE TABLE tbl_settings(_id INTEGER primary key autoincrement, item text)";
		db.execSQL(sql1);
		String sql2 = "CREATE TABLE tbl_content(_id INTEGER primary key autoincrement, title text, content text, upt_date text, use_pw text)";
		db.execSQL(sql2);
		String sql3 = "insert into tbl_settings(_id, item) " +
					  "select 1, '' union " +
					  "select 2, 'upt_date' union " +
					  "select 3, 'desc'";
		db.execSQL(sql3);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql1 = "DROP TABLE IF EXISTS tbl_settings";
		db.execSQL(sql1);
		String sql2 = "DROP TABLE IF EXISTS tbl_content";
		db.execSQL(sql2);
		onCreate(db);
	}
	
	public Cursor select(String item, String sort){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("tbl_content", null, null, null, null, null, item + " " + sort);
		return cursor;
	}
	
	public void insert(String title, String content, String use_pw){
		SQLiteDatabase db = this.getWritableDatabase();
		// 将添加的值放入ContentValues
		ContentValues cv = new ContentValues();
		cv.put("title", title);
		cv.put("content", content);
		cv.put("upt_date", date());
		cv.put("use_pw", use_pw);
		db.insert("tbl_content", null, cv);
	}
	
	public void delete(int id){
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "_id = ?";
		String[] whereValue = { Integer.toString(id) };
		db.delete("tbl_content", where, whereValue);
	}
	
	public void update(int id, String title, String content, String use_pw){
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "_id = ?";
		String[] whereValue = { Integer.toString(id) };
		// 将修改的值放入ContentValues
		ContentValues cv = new ContentValues();
		cv.put("title", title);
		cv.put("content", content);
		cv.put("upt_date", date());
		cv.put("use_pw", use_pw);
		db.update("tbl_content", cv, where, whereValue);
	}
	
	public String date(){
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = df.format(date);
		return str;
	}
	
	public Cursor getSettings(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("tbl_settings", null, null, null, null, null, "_id");
		return cursor;
	}
	
	public void setSettings(int id, String item){
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "_id = ?";
		String[] whereValue = { Integer.toString(id) };
		// 将修改的值放入ContentValues
		ContentValues cv = new ContentValues();
		cv.put("item", item);
		db.update("tbl_settings", cv, where, whereValue);
	}
}