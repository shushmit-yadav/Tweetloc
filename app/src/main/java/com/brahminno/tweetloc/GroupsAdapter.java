package com.brahminno.tweetloc;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Shushmit on 10-06-2015.
 */
public class GroupsAdapter extends BaseExpandableListAdapter {

    private Context context;
    // header titles...
    private List<String> listGroupsName;

    // child data in format of header title, child title
    private HashMap<String, List<String>> listGroupMembersName;

    public GroupsAdapter(Context context, List<String> listGroupsName,
                                 HashMap<String, List<String>> listGroupMembersName) {
        this.context = context;
        this.listGroupsName = listGroupsName;
        this.listGroupMembersName = listGroupMembersName;
    }
    @Override
    public int getGroupCount() {
        return this.listGroupsName.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listGroupsName.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listGroupMembersName.get(this.listGroupsName.get(groupPosition))
                .get(childPosition);
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
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_name,null);
        }
        TextView group_name = (TextView) convertView.findViewById(R.id.tvGroupName);

        group_name.setTypeface(null, Typeface.BOLD);
        group_name.setText(groupTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
