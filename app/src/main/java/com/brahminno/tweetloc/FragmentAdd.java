package com.brahminno.tweetloc;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
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

import org.json.JSONArray;

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
    JSONArray Group_Member;
    String deviceId, Mobile_Number;
    Context context;
    String countryCode;
    SQLiteDatabase mydb;
    ListView listViewAddContact;
    TelephonyManager manager;
    AddContactsAdapter addContactsAdapter;
    //Store fetch contact from mobile device...
    ArrayList<Contacts_Test> contactList;
    ArrayList<String> mydbContactNumberList;
    ArrayList<String> mydbContactNameList;
    ArrayList<String> groupMemberArrayList;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        btnAddSelectedContact = (Button) rootView.findViewById(R.id.btnAddSelectedContact);
        listViewAddContact = (ListView) rootView.findViewById(R.id.listViewAddContact);
        //get string value from intent using bundle.....
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
        manager = (TelephonyManager) getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
        countryCode = manager.getNetworkCountryIso().toUpperCase();
        //init Group_Member JSONArray.....
        Group_Member = new JSONArray();
        groupMemberArrayList = new ArrayList<>();
        try {
            //Initialization of app local sqlite database.....
            mydb = new SQLiteDatabase(getActivity());
            //get arraylist from sqlite database.....
            mydbContactNumberList = new ArrayList<>();
            mydbContactNumberList = mydb.getAllNumbersFromContactTable();
            //get all names from database.....
            mydbContactNameList = new ArrayList<>();
            mydbContactNameList = mydb.getAllNamesFromContactTable();
            //Log.i("Numberlist...", String.valueOf(mydbMobileNumberArrayList.get(1)));
            contactList = new ArrayList<>();
            for(int i = 0; i < mydbContactNumberList.size(); i++){
                String contact_Name = mydbContactNameList.get(i);
                String contact_Number = mydbContactNumberList.get(i);
                contactList.add(new Contacts_Test(contact_Name,contact_Number));
            }
            //Log.i("Final List...", mydbMobileNumberArrayList.get(1));
            Collections.sort(contactList, new Comparator<Contacts_Test>() {
                public int compare(Contacts_Test a, Contacts_Test b) {
                    return a.getName().compareTo(b.getName());
                }
            });
            addContactsAdapter = new AddContactsAdapter(getActivity(), contactList);
            listViewAddContact.setAdapter(addContactsAdapter);

            //on button click event.....
            btnAddSelectedContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        //get JsonArray of members from adapter class.....
                        groupMemberArrayList = addContactsAdapter.getGroupMemberArrayList();
                        for(int i = 0; i < groupMemberArrayList.size(); i++){
                            Group_Member.put(groupMemberArrayList.get(i));
                        }
                        Log.i("JsonArray is..."," "+Group_Member);
                        Group_Member.put(Mobile_Number);
                        Log.i("JsonArray is...", " " + Group_Member);
                        Log.i("Member Number", String.valueOf(Group_Member.get(1)));
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                    Log.i("GroupAsyncTask call...", "now");
                    //Call AsyncTask to upload data on server.....
                    new GroupAsyncTask(getActivity().getApplicationContext(), deviceId, Group_Name, Group_Member, Mobile_Number).execute();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
