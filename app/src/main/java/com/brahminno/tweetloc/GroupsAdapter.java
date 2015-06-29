package com.brahminno.tweetloc;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shushmit on 10-06-2015.
 */
public class GroupsAdapter extends BaseAdapter{

    private ArrayList<ContactNameWithNumber> groupMembersList;
    private Context context;
    private LayoutInflater groupInflater;

    public GroupsAdapter(Context context,ArrayList<ContactNameWithNumber> groupMembersList){
        this.groupMembersList = groupMembersList;
        groupInflater = groupInflater.from(context);
    }

    @Override
    public int getCount() {
        return groupMembersList.size();
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
        if (groupInflater == null) {
            groupInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = groupInflater.inflate(R.layout.group_members_name, null);
        }
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tvGroupMemberName = (TextView) convertView.findViewById(R.id.tvGroupMembersName);
        viewHolder.tvGroupMemberNumber = (TextView) convertView.findViewById(R.id.tvGroupMemberNumber);
        viewHolder.tvGroupMemberName.setText(groupMembersList.get(position).getContact_name());
        viewHolder.tvGroupMemberNumber.setText(groupMembersList.get(position).getContact_number());
        return convertView;
    }

    private class ViewHolder{
        TextView tvGroupMemberName,tvGroupMemberNumber;
    }
}
