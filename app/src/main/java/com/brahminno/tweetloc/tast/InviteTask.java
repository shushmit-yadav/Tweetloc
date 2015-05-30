package com.brahminno.tweetloc.tast;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.brahminno.tweetloc.AddMemberActivity;
import com.brahminno.tweetloc.data.ContactItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Shushmit on 30-05-2015.
 */
public class InviteTask extends AsyncTask<Void, Void, List<ContactItem>> {

    private FragmentTransaction ft;
    private ActionBarActivity activity;

    public InviteTask(Activity activity, FragmentTransaction ft) {
        //this.activity = activity;
        this.ft = ft;
    }
    public InviteTask() {
    }
    @Override
    protected List<ContactItem> doInBackground(Void... params) {
        Cursor c = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[] {ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Data.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.LABEL},
                ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",
                null,
                ContactsContract.Data.DISPLAY_NAME);

        int count = c.getCount();
        boolean b = c.moveToFirst();
        String[] columnNames = c.getColumnNames();
        int displayNameColIndex = c.getColumnIndex("display_name");
        int idColIndex = c.getColumnIndex("_id");
        //int contactIdColIndex = c.getColumnIndex("contact_id");
        int col2Index = c.getColumnIndex(columnNames[2]);
        int col3Index = c.getColumnIndex(columnNames[3]);
        int col4Index = c.getColumnIndex(columnNames[4]);
        List<ContactItem> contactItemList = new LinkedList<ContactItem>();
        for(int i = 0; i < count ; i ++) {
            String displayName = c.getString(displayNameColIndex);
            String phoneNumber = c.getString(col2Index);
            int contactId = c.getInt(col3Index);
            String phoneType = c.getString(col4Index);

            long _id = c.getLong(idColIndex);
            ContactItem contactItem = new ContactItem();
            contactItem.mId= _id;
            contactItem.mContactId = contactId;
            contactItem.mDisplayName = displayName;
            contactItem.mPhone = phoneNumber;
            contactItemList.add(contactItem);
            boolean b2 = c.moveToNext();
        }
        c.close();
        return contactItemList;
    }
    protected void onPostExecute(List<ContactItem> result) {
        ((AddMemberActivity)activity).addContactListFragment(ft, result);

    }
}
