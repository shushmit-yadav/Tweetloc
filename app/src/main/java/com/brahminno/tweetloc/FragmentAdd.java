package com.brahminno.tweetloc;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.brahminno.tweetloc.testAdapter.AddContactsAdapter;
import com.brahminno.tweetloc.testAdapter.Contacts_Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Shushmit on 28-05-2015.
 * Brahmastra Innovations....
 * this class is used for showing numbers in listview which have already installed this app....
 */
public class FragmentAdd extends Fragment {


    Button btnAddSelectedContact;

    EditText etGroupName;

    String Group_Name;
    ArrayList<String> Group_Member;
    String deviceId, Mobile_Number;
    Context context;
    SQLiteDatabase mydb;
    ListView listViewAddContact;
    AddContactsAdapter addContactsAdapter;
    //Store fetch contact from mobile device...
    ArrayList<Contacts_Test> contactList;
    ArrayList<String> mydbMobileNumberArrayList;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        btnAddSelectedContact = (Button) rootView.findViewById(R.id.btnAddSelectedContact);
        listViewAddContact = (ListView) rootView.findViewById(R.id.listViewAddContact);
        Bundle bundle = getActivity().getIntent().getExtras();
        Group_Name = bundle.getString("Group Name");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        deviceId = prefs.getString("Device Id", null);
        Mobile_Number = prefs.getString("Mobile Number", null);
        Group_Member = new ArrayList<>();
        //on button click event.....
        btnAddSelectedContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get arraylist of members from adapter class....
                Group_Member = addContactsAdapter.getArrayList();
                Log.i("Member Number", Group_Member.get(1));
                Log.i("GroupAsyncTask call...", "now");
                //Call AsyncTask to upload data on server.....
                new GroupAsyncTask(getActivity().getApplicationContext(), deviceId, Group_Name, Group_Member, Mobile_Number).execute();
            }
        });
        try {
            //Initialization of app local sqlite database.....
            mydb = new SQLiteDatabase(getActivity());
            //get arraylist from sqlite database.....
            mydbMobileNumberArrayList = new ArrayList<>();
            mydbMobileNumberArrayList = mydb.getAllNumbers();
            //Log.i("Numberlist...", String.valueOf(mydbMobileNumberArrayList.get(1)));
            contactList = new ArrayList<>();
            getAddContactList(mydbMobileNumberArrayList);
            //Log.i("Final List...", mydbMobileNumberArrayList.get(1));
            Collections.sort(contactList, new Comparator<Contacts_Test>() {
                public int compare(Contacts_Test a, Contacts_Test b) {
                    return a.getName().compareTo(b.getName());
                }
            });
            addContactsAdapter = new AddContactsAdapter(getActivity(), contactList);
            listViewAddContact.setAdapter(addContactsAdapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Method to fetch all contacts from android mobile......
    private void getAddContactList(ArrayList<String> mydbMobileNumberArrayList) {
        Log.i("Inside ...", "getAddContactList..." + true);
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
            //Log.i("Inside...","do While loop");
            String name = people.getString(indexName);
            String number = people.getString(indexNumber);
            for (int i = 0; i < mydbMobileNumberArrayList.size(); i++) {
                String num = mydbMobileNumberArrayList.get(i);
                Log.i("num.....", num);
                Log.i("number.....", number);
                if (number.equals(num)) {
                    Log.i("Inside...", "if condition.." + true);
                    Log.i("number.....", number);
                    contactList.add(new Contacts_Test(name, number));
                }
            }
        }
        while (people.moveToNext());
    }
}
