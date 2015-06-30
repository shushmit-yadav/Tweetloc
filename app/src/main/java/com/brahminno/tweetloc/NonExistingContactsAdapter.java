package com.brahminno.tweetloc;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.brahminno.tweetloc.testAdapter.Contacts_Test;

import java.util.ArrayList;

/**
 * Created by Shushmit on 30-06-2015.
 */
public class NonExistingContactsAdapter extends BaseAdapter {

    private ArrayList<ContactNameWithNumber> nonExistingContactsList;
    private LayoutInflater nonExistingContactInflater;
    static ArrayList<String> Group_Member;
    Context context;
    boolean[] itemChecked;

    public NonExistingContactsAdapter(Context context, ArrayList<ContactNameWithNumber> nonExistingContactsList) {
        this.nonExistingContactsList = nonExistingContactsList;
        nonExistingContactInflater = nonExistingContactInflater.from(context);
        this.context = context;
        itemChecked = new boolean[nonExistingContactsList.size()];
        Group_Member = new ArrayList<>();
    }

    private class ViewHolder {
        TextView tvContactsNametest;
        TextView tvContactsNumbertest;
        CheckBox checkBox;
    }
    @Override
    public int getCount() {
        return nonExistingContactsList.size();
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
        if (nonExistingContactInflater == null) {
            nonExistingContactInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = nonExistingContactInflater.inflate(R.layout.details_contacts2_test, null);
        }
        ViewHolder vh = new ViewHolder();
        // LinearLayout addContactLayout = (LinearLayout) addContactInflater.inflate(R.layout.details_contacts2_test,parent,false);
        vh.tvContactsNametest = (TextView) convertView.findViewById(R.id.tvContactsNametest);
        vh.tvContactsNumbertest = (TextView) convertView.findViewById(R.id.tvContactsNumbertest);
        vh.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);

        final ContactNameWithNumber currentContact = nonExistingContactsList.get(position);
        vh.tvContactsNametest.setText(currentContact.getContact_name());
        vh.tvContactsNumbertest.setText(currentContact.getContact_number());
        vh.checkBox.setTag(position);
        vh.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox) v;
                if (c.isChecked()) {
                    Log.i("checkbox", "checked");
                    int position = (int) v.getTag();
                    Log.i("checkbox", "position .." + position);
                    Group_Member.add(currentContact.getContact_number());
                    Log.i("ContactList", "group .." + Group_Member);
                }
                if (!c.isChecked()) {
                    int position = (int) v.getTag();
                    Log.i("uncheckbox", "position .." + position);
                    Group_Member.remove(currentContact.getContact_number());
                    Log.i("ContactList", "group remove .." + Group_Member);
                }
            }
        });
        return convertView;
    }

    public static ArrayList<String> getArrayList(){
        return Group_Member;
    }
}
