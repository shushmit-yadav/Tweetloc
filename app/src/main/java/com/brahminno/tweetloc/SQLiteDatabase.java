package com.brahminno.tweetloc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.brahminno.tweetloc.testAdapter.Contacts_Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shushmit on 20-05-2015.
 * Brahmastra Innovations Pvt. Ltd.
 */
public class SQLiteDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TweetLocDB.db";
    public static final String TABLE_NAME = "InfoTable";
    public static final String COLUMN_NUMBER = "Mobile_Number";
    public static final String COLUMN_NAME = "Contact_Name";
    public static final String COLUMN_EMAIL = "Email";
    public static final String CLOUMN_DEVICE_ID = "Device_ID";
    public static final String GROUP_TABLE = "Group_Table";
    public static final String GROUPS_NAME = "Group_Name";
    public static final String GROUP_MEMBERS = "Group_Members";
    public static final String  TABLE_NUMBER = "Mobile_Number_Table";


    public SQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {

        //Create table for storing registration details....
        //db.execSQL("create table " + TABLE_NAME + "(" + COLUMN_NUMBER + " text," + COLUMN_EMAIL + " text," + CLOUMN_DEVICE_ID + " text" + ")");

        //Create table to store groups details........
        db.execSQL("create table " + GROUP_TABLE + "(" + GROUPS_NAME + " text," + GROUP_MEMBERS + " test" + " ) ");
        //create table for storing mobile mobNum......
        db.execSQL("create table " + TABLE_NUMBER + " ( " + COLUMN_NUMBER + " text" + " )");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUMBER);
        onCreate(db);

    }

    void insertGroups(String Group_Name,List<String> Group_Members){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROUPS_NAME, Group_Name);
        contentValues.put(GROUP_MEMBERS, String.valueOf(Group_Members));
        db.insert(GROUP_TABLE, null, contentValues);
        db.close();
    }

    void insertNumberArrayList(List<String> mobile_number){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < mobile_number.size(); i++){
            contentValues.put(COLUMN_NUMBER, mobile_number.get(i));
            Log.i("Mobile Number sqlite...",mobile_number.get(i));
            db.insert(TABLE_NUMBER, null, contentValues);
            contentValues.clear();
        }
        db.close();
    }

    public ArrayList<String> getAllNumbers() {
        ArrayList<String> list = new ArrayList<>();
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ TABLE_NUMBER, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            list.add(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public void deleteNumberArrayList(){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Mobile_Number_Table ");
        db.close();
    }
}
