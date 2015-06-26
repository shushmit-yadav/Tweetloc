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

    public ArrayList<ContactNameWithNumber> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<ContactNameWithNumber> groupMembers) {
        this.groupMembers = groupMembers;
    }

    private ArrayList<ContactNameWithNumber> groupMembers;


}
