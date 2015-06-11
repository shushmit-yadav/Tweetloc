package com.brahminno.tweetloc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by Shushmit on 28-05-2015.
 */
public class FragmentAdd extends Fragment {


    Button btnAddSelectedContact;

    EditText etGroupName;

    String Group_Name;
    ArrayList<String> Group_Member;
    String deviceId,Mobile_Number;
    Context context;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        btnAddSelectedContact = (Button) rootView.findViewById(R.id.btnAddSelectedContact);
        Bundle bundle = getActivity().getIntent().getExtras();
        Group_Name = bundle.getString("Group Name");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        deviceId = prefs.getString("Device Id", null);
        Mobile_Number = prefs.getString("Mobile Number",null);

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
    }
}
