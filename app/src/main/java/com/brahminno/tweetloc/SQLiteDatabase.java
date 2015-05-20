package com.brahminno.tweetloc;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shushmit on 20-05-2015.
 */
public class SQLiteDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TweetLocDB.db";
    public static final String TABLE_NAME = "InfoTable";
    public  static final String COLUMN_NUMBER = "Mobile_Number";
    public  static final String COLUMN_EMAIL = "Email";
    public static final String CLOUMN_DEVICE_ID = "Device_ID";




    public SQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {

        db.execSQL("create table InfoTable" + "(_id integer primary key AUTOINCREMENT,Mobile Number text,Email text,Device_ID text)");

    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS InfoTable");
        onCreate(db);

    }

    public boolean insertInfo(String Mobile_Number,String Email,String Device_ID){
        android.database.sqlite.SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Mobile_Number",Mobile_Number);
        contentValues.put("Email",Email);
        contentValues.put("Device_ID",Device_ID);

        db.insert("InfoTable",null,contentValues);
        return true;
    }
}
