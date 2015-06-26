package com.brahminno.tweetloc;

import java.util.ArrayList;

/**
 * Created by Shushmit on 10-06-2015.
 */
public class GroupDetails {
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    private String groupName;

    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<String> groupMembers) {
        this.groupMembers = groupMembers;
    }

    private ArrayList<String> groupMembers;


}
