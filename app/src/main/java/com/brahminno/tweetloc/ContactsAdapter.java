package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Shushmit on 04-06-2015.
 * Brahmastra Innovations
 * This is a Custom Adapter class which inflate layout and bind contacts in list view....
 */
public class ContactsAdapter extends BaseAdapter {
    private ArrayList<Contact> inviteContactList;
    private LayoutInflater contactInflater;
    Context context;

    public ContactsAdapter(Context context,ArrayList<Contact> inviteContactList){
        this.inviteContactList = inviteContactList;
        contactInflater = contactInflater.from(context);
        this.context = context;
    }
    @Override
    public int getCount() {
        return inviteContactList.size();
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
        RelativeLayout contactLayout = (RelativeLayout) contactInflater.inflate(R.layout.details_contact,parent,false);
        TextView tvContactsName = (TextView) contactLayout.findViewById(R.id.tvContactsName);
        Button btnInvite = (Button) contactLayout.findViewById(R.id.btnInvite);

        Contact currentContact = inviteContactList.get(position);

        tvContactsName.setText(currentContact.getName());
        //on Button click event....
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "here is the link to download TweetLoc");
                //context.startActivityForResult(Intent.createChooser(intent, "Share It!!!"));
                context.startActivity(intent);
            }
        });

        return contactLayout;
    }
}