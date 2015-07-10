package com.brahminno.tweetloc.testAdapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.brahminno.tweetloc.R;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Shushmit on 12-06-2015.
 * Brahmastra Innovatons.....
 */
public class AddContactsAdapter extends BaseAdapter {
    private ArrayList<Contacts_Test> addContactList;
    private LayoutInflater addContactInflater;
    static JSONArray groupMemberJsonArray;
    Context context;
    boolean[] itemChecked;

    public AddContactsAdapter(Context context, ArrayList<Contacts_Test> addContactList) {
        this.addContactList = addContactList;
        addContactInflater = addContactInflater.from(context);
        this.context = context;
        itemChecked = new boolean[addContactList.size()];
        groupMemberJsonArray = new JSONArray();
    }

    private class ViewHolder {
        TextView tvContactsNametest;
        TextView tvContactsNumbertest;
        CheckBox checkBox;
    }

    @Override
    public int getCount() {
        return addContactList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        //map to details_contact layout.....
        if (addContactInflater == null) {
            addContactInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = addContactInflater.inflate(R.layout.details_contacts2_test, null);
        }
        ViewHolder vh = new ViewHolder();
        // LinearLayout addContactLayout = (LinearLayout) addContactInflater.inflate(R.layout.details_contacts2_test,parent,false);
        vh.tvContactsNametest = (TextView) convertView.findViewById(R.id.tvContactsNametest);
        vh.tvContactsNumbertest = (TextView) convertView.findViewById(R.id.tvContactsNumbertest);
        vh.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);

        final Contacts_Test currentContact = addContactList.get(position);
        vh.tvContactsNametest.setText(currentContact.getName());
        vh.tvContactsNumbertest.setText(currentContact.getNumber());
        vh.checkBox.setTag(position);
        vh.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox) v;
                if (c.isChecked()) {
                    Log.i("checkbox", "checked");
                    int position = (int) v.getTag();
                    Log.i("checkbox", "position .." + position);
                    groupMemberJsonArray.put(currentContact.getNumber());
                    Log.i("ContactList", "group .." + groupMemberJsonArray);
                }
                if (!c.isChecked()) {
                    int position = (int) v.getTag();
                    Log.i("uncheckbox", "position .." + position);
                    groupMemberJsonArray.remove(position);
                    Log.i("ContactList", "group remove .." + groupMemberJsonArray);
                }
            }
        });
        return convertView;
    }
    //this method returns JsonArray.......
    public static JSONArray getJsonArrayList(){
        return groupMemberJsonArray;
    }

}