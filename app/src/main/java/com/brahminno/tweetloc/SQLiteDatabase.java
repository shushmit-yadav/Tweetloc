package com.brahminno.tweetloc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.brahminno.tweetloc.testAdapter.Contacts_Test;

import java.lang.reflect.Array;
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
    public static final String CONTACTS_NUMBER = "Contacts_Table";
    public static final String INVITE_NUMBER_TABLE = "Invite_Contacts_Table";


    public SQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {

        //Create table for storing registration details....
        Log.i("Before table...", "created" + TABLE_NAME);
        db.execSQL("create table " + TABLE_NAME + "(" + COLUMN_NUMBER + " text," + COLUMN_EMAIL + " text," + CLOUMN_DEVICE_ID + " text" + ")");
        Log.i("After table...", "created" + TABLE_NAME);
        //Create table to store groups details........
        Log.i("Before table...", "created" + GROUP_TABLE);
        db.execSQL("create table " + GROUP_TABLE + "(" + GROUPS_NAME + " text," + GROUP_MEMBERS + " text" + ")");
        Log.i("After table...", "created" + GROUP_TABLE);
        //create table for storing mobile mobNum......
        Log.i("Before table...", "created" + CONTACTS_NUMBER);
        db.execSQL("create table " + CONTACTS_NUMBER + "(" + COLUMN_NUMBER + " text," + COLUMN_NAME + " text" + ")");
        Log.i("After table...", "created" + CONTACTS_NUMBER);
        db.execSQL("create table " + INVITE_NUMBER_TABLE + "(" + COLUMN_NAME + " text," + COLUMN_NUMBER + " text" + ")");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_NUMBER);
        db.execSQL("DROP TABLE IF EXISTS " + INVITE_NUMBER_TABLE);
        onCreate(db);

    }

    void insertGroups(String Group_Name, List<String> Group_Members) {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROUPS_NAME, Group_Name);
        contentValues.put(GROUP_MEMBERS, String.valueOf(Group_Members));
        db.insert(GROUP_TABLE, null, contentValues);
        db.close();
    }

    void insertNumberArrayList(List<String> mobile_number, List<String> mobile_name) {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < mobile_number.size(); i++) {
            contentValues.put(COLUMN_NUMBER, mobile_number.get(i));
            contentValues.put(COLUMN_NAME, mobile_name.get(i));
            contentValues.get(COLUMN_NAME);
            Log.i("Mobile Number sqlite...", mobile_number.get(i));
            Log.i("Mobile Name sqlite...", mobile_name.get(i));
            db.insert(CONTACTS_NUMBER, null, contentValues);
            contentValues.clear();
            Log.i("calling deleteInvite...", "called" + mobile_number.get(i));
            //deleteFromInvite(mobile_number.get(i));
            //db.execSQL("DELETE FROM " + INVITE_NUMBER_TABLE + " WHERE Mobile_Number = " + "+919654023769");
            Log.i("Value deleted....", "from invite table" + mobile_number.get(i));
        }
        db.close();
    }

    public ArrayList<String> getAllNumbersFromContactTable() {
        ArrayList<String> numberlist = new ArrayList<>();
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CONTACTS_NUMBER, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            numberlist.add(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)));
            cursor.moveToNext();
        }
        cursor.close();
        return numberlist;
    }

    public ArrayList<String> getAllNamesFromContactTable() {
        ArrayList<String> namelist = new ArrayList<>();
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CONTACTS_NUMBER, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            namelist.add(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        return namelist;
    }

    public ArrayList<String> getAllNumbersInviteTable() {
        ArrayList<String> numberlist = new ArrayList<>();
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + INVITE_NUMBER_TABLE, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            numberlist.add(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)));
            cursor.moveToNext();
        }
        cursor.close();
        return numberlist;
    }

    public ArrayList<String> getAllNamesFromInviteTable() {
        ArrayList<String> namelist = new ArrayList<>();
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + INVITE_NUMBER_TABLE, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            namelist.add(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        return namelist;
    }

    //store all contacts into INVITE TABLE into app db....
    public void insertIntoInvite(String contactName, String contactNumber) {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.i("Array size", "in insertIntoInvite " + contactName + " " + contactNumber);
        contentValues.put(COLUMN_NAME, contactName);
        contentValues.put(COLUMN_NUMBER, contactNumber);
        db.insert(INVITE_NUMBER_TABLE, null, contentValues);
        Log.i("following contact...", "inserted into invite table" + contactName + "--> " + contactNumber);
        //we have to clear the content values. if we will not clear it then it append the data....
        Log.i("Invite Contacts....", "saved successfully");
        db.close();
    }

    public void deleteNumberArrayList() {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Contacts_Table");
        db.close();
    }

    public void deleteInviteTableItems(){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Invite_Contacts_Table");
        db.close();
    }
}
