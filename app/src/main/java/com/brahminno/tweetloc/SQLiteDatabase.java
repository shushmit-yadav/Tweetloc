package com.brahminno.tweetloc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shushmit on 20-05-2015.
 * Brahmastra Innovations Pvt. Ltd.
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

        db.execSQL("create table " + TABLE_NAME + "(" + COLUMN_NUMBER + " text," + COLUMN_EMAIL + " text," + CLOUMN_DEVICE_ID + " text" + ")");

    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS InfoTable");
        onCreate(db);

    }

    void insertInfo(RegistrationInfo info){
        android.database.sqlite.SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NUMBER,info.getMobileNO());
        contentValues.put(COLUMN_EMAIL,info.getEmailID());
        contentValues.put(CLOUMN_DEVICE_ID,info.getDeviceID());

        db.insert(TABLE_NAME, null, contentValues);

        db.close();
        //return true;
    }

    void deleteInfo(RegistrationInfo info){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        Log.i(TABLE_NAME,"Deleted");

    }

}
