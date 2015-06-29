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

    private ArrayList<String> groupNameList;
    private Context context;
    private LayoutInflater groupInflater;

    public GroupsAdapter(Context context,ArrayList<String> groupNameList){
        this.groupNameList = groupNameList;
        groupInflater = groupInflater.from(context);
    }

    @Override
    public int getCount() {
        return groupNameList.size();
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
            convertView = groupInflater.inflate(R.layout.details_contacts2_test, null);
        }
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
        //viewHolder.tvGroupName.setText();
        return convertView;
    }

    private class ViewHolder{
        TextView tvGroupName;
    }
}
