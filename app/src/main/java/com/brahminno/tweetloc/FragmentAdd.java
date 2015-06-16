package com.brahminno.tweetloc;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
 */
public class FragmentAdd extends Fragment {


    Button btnAddSelectedContact;

    EditText etGroupName;

    String Group_Name;
    ArrayList<String> Group_Member;
    String deviceId, Mobile_Number;
    Context context;

    ListView listViewAddContact;

    ArrayList<Contacts_Test> addContactList;


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

        //Hard Coded to group member...
        // -------------------------------------------------------------------------------------
        Group_Member = new ArrayList<String>();
        Group_Member.add("Shushmit");
        Group_Member.add("Manish");
        Group_Member.add("Prateek");
        //----------------------------------------------------------------------------

        btnAddSelectedContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Call AsyncTask to upload data on server.....
                new GroupAsyncTask(getActivity().getApplicationContext(), deviceId, Group_Name, Group_Member, Mobile_Number).execute();
            }
        });

        addContactList = new ArrayList<Contacts_Test>();
        getAddContactList();
        Collections.sort(addContactList, new Comparator<Contacts_Test>() {
            public int compare(Contacts_Test a, Contacts_Test b) {
                return a.getName().compareTo(b.getName());
            }
        });
        AddContactsAdapter addContactsAdapter = new AddContactsAdapter(getActivity(), addContactList);
        listViewAddContact.setAdapter(addContactsAdapter);
    }

    //Method to fetch all contacts from android mobile......
    private void getAddContactList() {
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
            addContactList.add(new Contacts_Test(name, number));

        }
        while (people.moveToNext());
    }


}
