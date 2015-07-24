package com.brahminno.tweetloc;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
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
    SQLiteDatabase mydb;
    ArrayList<String> mydbNameList;
    ArrayList<String> mydbNumberList;

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
        if(isNetworkAvailable()){
            //initialize SQLiteDatabase....
            mydb = new SQLiteDatabase(getActivity());
            mydbNameList = new ArrayList<>();
            mydbNameList = mydb.getAllNamesFromInviteTable();
            mydbNumberList = new ArrayList<>();
            mydbNumberList = mydb.getAllNumbersInviteTable();
            Log.i("size of array...",""+mydbNumberList.size());
            contactList = new ArrayList<Contact>();
            for(int i =0; i < mydbNumberList.size(); i++){
                String name = mydbNameList.get(i);
                String number = mydbNumberList.get(i);
                Log.i("Contacts in Invite...",""+name + "--->" + number);
                contactList.add(new Contact(name,number));
            }

            Collections.sort(contactList, new Comparator<Contact>() {
                public int compare(Contact a, Contact b) {
                    return a.getName().compareTo(b.getName());
                }
            });
            ContactsAdapter contactsAdapter = new ContactsAdapter(getActivity(),contactList );
            listView.setAdapter(contactsAdapter);
        }
        else{
            Toast.makeText(getActivity(),"Please connect to Internet!!!!!",Toast.LENGTH_SHORT).show();
        }
    }

    //Check Internet
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}

