package com.brahminno.tweetloc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.brahminno.tweetloc.backend.tweetApi.model.ContactSyncBean;

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
    public static final String GROUP_TABLE = "Group Table";
    public static final String GROUPS_NAME = "Group Name";
    public static final String  TABLE_NUMBER = "Mobile Table";


    public SQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {

        //Create table for storing registration details....
        db.execSQL("create table " + TABLE_NAME + "(" + COLUMN_NUMBER + " text," + COLUMN_EMAIL + " text," + CLOUMN_DEVICE_ID + " text" + ")");

        //Create table to store groups details........
        //db.execSQL("create table " + GROUP_TABLE + "(" + GROUPS_NAME + " text" + ")");
        //create table for storing mobile number......
        db.execSQL("create table " + TABLE_NUMBER + "(" + COLUMN_NUMBER + " text" + ")");

    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUMBER);
        onCreate(db);

    }

    void insertInfo(RegistrationInfo info){
        android.database.sqlite.SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NUMBER,info.getMobileNO());
        contentValues.put(COLUMN_EMAIL, info.getEmailID());
        contentValues.put(CLOUMN_DEVICE_ID, info.getDeviceID());

        db.insert(TABLE_NAME, null, contentValues);

        db.close();
        //return true;
    }
    void insertGroups(GroupDetails group){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROUPS_NAME,group.getGroup_Name());
        db.insert(GROUP_TABLE, null, contentValues);
        db.close();
    }

    void deleteInfo(RegistrationInfo info){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        Log.i(TABLE_NAME, "Deleted");

    }

    void insertNumberArrayList(List<String> mobile_number){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NUMBER, String.valueOf(mobile_number));
        db.insert(TABLE_NUMBER, null, contentValues);
        db.close();
    }

}
