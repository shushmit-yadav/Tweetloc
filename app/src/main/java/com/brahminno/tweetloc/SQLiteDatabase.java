package com.brahminno.tweetloc;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shushmit on 20-05-2015.
 * Brahmastra Innovations Pvt. Ltd.
 * This is a local android sqlite database class......
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
    private Context context;


    public SQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
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

    public ArrayList<ContactNameWithNumber> getAllMembersUsingGroupNames(String groupName){
        ArrayList<ContactNameWithNumber> groupMembersUsingGroupName = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String MyMobileNumber = prefs.getString("Mobile Number", null);
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select a.Group_name as Group_name,a.Group_Members as Group_Members,a.Is_Accepted as Is_Accepted, b.Contact_Name as Contact_Name from Group_Table a left outer join Contacts_Table b  on a.Group_Members=b.Mobile_Number WHERE a.Group_Name = '" + groupName + "'", null);
        Log.i("Cursor count....", "getAllMembersUsingGroupNames" + cursor.getCount());
        cursor.moveToFirst();
        do{
            ContactNameWithNumber contactNameWithNumber = new ContactNameWithNumber();
            if(cursor.getString(cursor.getColumnIndex("Group_Members")).equals(MyMobileNumber)){
                contactNameWithNumber.setContact_name("You");
            }
            else{
                contactNameWithNumber.setContact_name(cursor.getString(cursor.getColumnIndex("Contact_Name")));
            }
            contactNameWithNumber.setContact_number(cursor.getString(cursor.getColumnIndex("Group_Members")));
            contactNameWithNumber.setMemberAcceptanceStatus(cursor.getString(cursor.getColumnIndex("Is_Accepted")));
            Log.i("Value is....","getAllMembersUsingGroupNames-->"+cursor.getString(cursor.getColumnIndex("Contact_Name"))+"-->"+cursor.getString(cursor.getColumnIndex("Group_Members")));
            groupMembersUsingGroupName.add(contactNameWithNumber);
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
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                groupMemberNumberList.add(cursor.getString(cursor.getColumnIndex(COLUMN_GROUP_MEMBERS)));
                groupNameList.add(cursor.getString(cursor.getColumnIndex(COLUMN_GROUPS_NAME)));
            }
            while(cursor.moveToNext());
            cursor.close();
        }
        GetGroupData getGroupData = new GetGroupData();
        getGroupData.setGroupNameList(groupNameList);
        getGroupData.setGroupMemberNumberList(groupMemberNumberList);
        return getGroupData;
    }

    public void updateGroupTableWithSyncInfo(String isAccepted, String groupName, String groupMemberMobileNumber){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IS_ACCEPTED, isAccepted);
        db.update(GROUP_TABLE,contentValues,"Group_Name = "+"'"+groupName+"'"+" and Group_Members = "+"'"+groupMemberMobileNumber+"'",null);
        Log.i("Value updated...", "succussfully");
        db.close();
    }
    //method to update status when user accept group.......
    public void updateStatusOfGroupMemberIntoGroupTable(String isAccepted,String groupName,String groupMemberMobileNumber){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IS_ACCEPTED, isAccepted);
        db.update(GROUP_TABLE,contentValues,"Group_Name = "+"'"+groupName+"'"+" and Group_Members = "+"'"+groupMemberMobileNumber+"'",null);
        Log.i("Status updated....","succussfully");
        db.close();
    }
    //method to delete group from app db when user reject group........
    public void deleteGroupFromGroupTable(String groupName){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE * FROM " + GROUP_TABLE + " WHERE Group_Name = "+"'"+groupName+"'", null);
        Log.i("Group Deleted.....","succussfully");
        db.close();
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

//this class is used for returning contact_name with mobile number.....
class ContactNameWithNumber{
    String contact_name;
    String contact_number;
    String memberAcceptanceStatus;

    public String getContact_name() {
        return contact_name;
    }

    public String getContact_number() {
        return contact_number;
    }

    public String getMemberAcceptanceStatus(){
        return memberAcceptanceStatus;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public void setMemberAcceptanceStatus(String memberAcceptanceStatus) {
        this.memberAcceptanceStatus = memberAcceptanceStatus;
    }
}