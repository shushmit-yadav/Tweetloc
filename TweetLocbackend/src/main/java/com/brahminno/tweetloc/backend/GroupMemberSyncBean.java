package com.brahminno.tweetloc.backend;

import java.util.ArrayList;

/**
 * Created by Shushmit on 23-06-2015.
 */
public class GroupMemberSyncBean {
    private String groupAdminNumber;
    private String groupName;
    private String compositeGroupKey;


    public String getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(String isAccepted) {
        this.isAccepted = isAccepted;
    }

    private String isAccepted;
    private ArrayList<String> groupMember;

    public ArrayList<String> getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(ArrayList<String> groupMember) {
        this.groupMember = groupMember;
    }

    public String getGroupAdminNumber() {
        return groupAdminNumber;
    }

    public void setGroupAdminNumber(String groupAdminNumber) {
        this.groupAdminNumber = groupAdminNumber;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCompositeGroupKey() {
        return compositeGroupKey;
    }

    public void setCompositeGroupKey(String compositeGroupKey) {
        this.compositeGroupKey = compositeGroupKey;
    }
}
