package com.brahminno.tweetloc;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shushmit on 10-06-2015.
 */
public class GroupsAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;
    private ArrayList<GroupDetails> groupDetails;

    public GroupsAdapter(Context context,ArrayList<GroupDetails> groupDetails){
        this.inflater = LayoutInflater.from(context);
        this.groupDetails = groupDetails;
    }


    @Override
    public int getGroupCount() {
        return groupDetails.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupDetails.get(groupPosition).getGroupMembers().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupDetails.get(groupPosition).getGroupName();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupDetails.get(groupPosition).getGroupMembers().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    //view Holder class......
    protected class ViewHolder {
        protected int childPosition;
        protected int groupPosition;
        //protected Button button;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.groupPosition = groupPosition;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.group_name, parent,false);
        }
        TextView tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
        tvGroupName.setText(getGroup(groupPosition).toString());
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.childPosition = childPosition;
        holder.groupPosition = groupPosition;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.group_members_name, parent,false);
        }
        TextView tvGroupMembers = (TextView) convertView.findViewById(R.id.tvGroupMembersName);
        tvGroupMembers.setText(groupDetails.get(groupPosition).getGroupMembers().get(childPosition));
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }
}
