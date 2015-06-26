package com.brahminno.tweetloc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.brahminno.tweetloc.backend.tweetApi.model.AcceptanceStatusBean;
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
    public static final String COLUMN_GROUPS_NAME = "Group_Name";
    public static final String COLUMN_GROUP_ADMIN_NUMBER = "Group_Admin_Number";
    public static final String COLUMN_GROUP_MEMBERS = "Group_Members";
    public static final String COLUMN_IS_ACCEPTED = "Is_Accepted";
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
        db.execSQL("create table " + GROUP_TABLE + "(" + COLUMN_GROUPS_NAME + " text," + COLUMN_GROUP_ADMIN_NUMBER + " text," + COLUMN_IS_ACCEPTED + " text," + COLUMN_GROUP_MEMBERS + " text" + ")");
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

    public void insertGroups(String Group_Name, String GroupMember_Admin, String isAccepted, String Group_Members) {
        Log.i("Inside insertGroups...", "table");
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_GROUPS_NAME, Group_Name);
        contentValues.put(COLUMN_GROUP_ADMIN_NUMBER, GroupMember_Admin);
        contentValues.put(COLUMN_IS_ACCEPTED, isAccepted);
        contentValues.put(COLUMN_GROUP_MEMBERS, Group_Members);
        db.insert(GROUP_TABLE, null, contentValues);
        Log.i("Value inserted....", "succussfully" + Group_Name + "-->" + GroupMember_Admin + "-->" + isAccepted + "-->" + Group_Members);
        db.close();
    }

    public void insertNumberArrayList(List<String> mobile_number, List<String> mobile_name) {
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
        Log.i("Cursor size...",""+cursor.getCount());
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

    public void deleteInviteTableItems() {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Invite_Contacts_Table");
        db.close();
    }
    //delete all data from Group_Table before saving new data......
    public void deleteGroupTableItems() {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Group_Table");
        db.close();
    }

    //get all distinct group names from local database.......
    public ArrayList<String> getUniqueGroupNamesFromGroupTable(){
        ArrayList<String> uniqueGroupNames = new ArrayList<>();
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, GROUP_TABLE, new String[]{COLUMN_GROUPS_NAME}, null, null, COLUMN_GROUPS_NAME, null, null, null);
        Log.i("Groups return from ....", "app db.." + cursor.getCount());
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            do{
                uniqueGroupNames.add(cursor.getString(cursor.getColumnIndex(COLUMN_GROUPS_NAME)));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return uniqueGroupNames;
    }

    public ArrayList<String> getAllMembersUsingGroupNames(String groupName){
        ArrayList<String> groupMembersUsingGroupName = new ArrayList<>();
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + GROUP_TABLE + " WHERE Group_Name = '" + groupName + "'", null);
        Log.i("Cursor count....", "getAllMembersUsingGroupNames" + cursor.getCount());
        cursor.moveToFirst();
        do{
            groupMembersUsingGroupName.add(cursor.getString(cursor.getColumnIndex(COLUMN_GROUP_MEMBERS)));
        }
        while(cursor.moveToNext());
        cursor.close();
        return groupMembersUsingGroupName;
    }

    //get all group names and group member's number for acceptanceSync.....
    public GetGroupData getAllMobileNumberForSyncAccpt(){
        ArrayList<String> groupMemberNumberList = new ArrayList<>();
        ArrayList<String> groupNameList = new ArrayList<>();
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + GROUP_TABLE + " WHERE Is_Accepted = 'unknown'", null);
        Log.i("Cursor count....", "getAllMembersUsingGroupNames" + cursor.getCount());
        cursor.moveToFirst();
        do{
            groupMemberNumberList.add(cursor.getString(cursor.getColumnIndex(COLUMN_GROUP_MEMBERS)));
            groupNameList.add(cursor.getString(cursor.getColumnIndex(COLUMN_GROUPS_NAME)));
        }
        while(cursor.moveToNext());
        cursor.close();
        GetGroupData getGroupData = new GetGroupData();
        getGroupData.setGroupNameList(groupNameList);
        getGroupData.setGroupMemberNumberList(groupMemberNumberList);
        return getGroupData;
    }
}


//this class is used for returning arrayList of groupName and groupMobileNumber....
class GetGroupData{
    private ArrayList<String> groupNameList;
    private ArrayList<String> groupMemberNumberList;

    public ArrayList<String> getGroupNameList() {
        return groupNameList;
    }

    public ArrayList<String> getGroupMemberNumberList() {
        return groupMemberNumberList;
    }

    public void setGroupNameList(ArrayList<String> groupNameList) {
        this.groupNameList = groupNameList;
    }

    public void setGroupMemberNumberList(ArrayList<String> groupMemberNumberList) {
        this.groupMemberNumberList = groupMemberNumberList;
    }
}