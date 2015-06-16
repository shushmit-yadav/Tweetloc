package com.brahminno.tweetloc.testAdapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brahminno.tweetloc.Contact;
import com.brahminno.tweetloc.R;

import java.util.ArrayList;

/**
 * Created by Shushmit on 12-06-2015.
 */
public class AddContactsAdapter extends BaseAdapter {
    private ArrayList<Contacts_Test> addContactList;
    private LayoutInflater addContactInflater;
    Context context;

    public AddContactsAdapter(Context context,ArrayList<Contacts_Test> addContactList){
        this.addContactList = addContactList;
        addContactInflater = addContactInflater.from(context);
        this.context = context;
    }
    @Override
    public int getCount() {
        return addContactList.size();
        //return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to details_contact layout.....
        LinearLayout addContactLayout = (LinearLayout) addContactInflater.inflate(R.layout.details_contacts2_test,parent,false);
        TextView tvContactsNametest = (TextView) addContactLayout.findViewById(R.id.tvContactsNametest);
        CheckBox checkBox = (CheckBox) addContactLayout.findViewById(R.id.checkBox1);

        final Contacts_Test currentContact = addContactList.get(position);

        tvContactsNametest.setText(currentContact.getName());


        return addContactLayout;
    }
}
