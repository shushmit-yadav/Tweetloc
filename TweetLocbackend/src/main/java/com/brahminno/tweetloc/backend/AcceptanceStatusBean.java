package com.brahminno.tweetloc.backend;

import java.util.ArrayList;

/**
 * Created by Shushmit on 26-06-2015.
 */
public class AcceptanceStatusBean {

    private String MobileNumber_Member;
    private String groupName;
    private String isAccepted;

    public ArrayList<String> getGroupNameList() {
        return groupNameList;
    }

    public void setGroupNameList(ArrayList<String> groupNameList) {
        this.groupNameList = groupNameList;
    }

    private ArrayList<String> groupNameList;

    public ArrayList<String> getGroupMemberNumberList() {
        return groupMemberNumberList;
    }

    public void setGroupMemberNumberList(ArrayList<String> groupMemberNumberList) {
        this.groupMemberNumberList = groupMemberNumberList;
    }

    private ArrayList<String> groupMemberNumberList;

    public String getGroupName() {
        return groupName;
    }

    public String getMobileNumber_Member() {
        return MobileNumber_Member;
    }

    public String getIsAccepted() {
        return isAccepted;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setMobileNumber_Member(String mobileNumber_Member) {
        MobileNumber_Member = mobileNumber_Member;
    }

    public void setIsAccepted(String isAccepted) {
        this.isAccepted = isAccepted;
    }
}
