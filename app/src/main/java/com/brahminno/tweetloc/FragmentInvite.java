package com.brahminno.tweetloc;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Shushmit on 28-05-2015.
 * Brahmastra Innovations
 * this is a fragment class to show contacts from the android device....
 */
public class FragmentInvite extends Fragment {

    ListView listView;
    ArrayList<Contact> contactList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactList = new ArrayList<Contact>();
        getContactList();
        Collections.sort(contactList, new Comparator<Contact>() {
            public int compare(Contact a, Contact b) {
                return a.getName().compareTo(b.getName());
            }
        });
        ContactsAdapter contactsAdapter = new ContactsAdapter(getActivity(),contactList );
        listView.setAdapter(contactsAdapter);
    }

    //Method to fetch all contacts from android mobile......
    private void getContactList() {
        //Set URI....
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //Set Projection
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor people = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        people.moveToFirst();
        do {
            String name = people.getString(indexName);
            String number = people.getString(indexNumber);
            contactList.add(new Contact(name,number));

        }
        while (people.moveToNext());
    }
}

