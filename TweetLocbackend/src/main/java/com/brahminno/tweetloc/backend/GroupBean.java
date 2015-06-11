package com.brahminno.tweetloc.backend;

import java.util.ArrayList;

/**
 * Created by Shushmit on 31-05-2015.
 * Brahmastra Innovations
 */
public class GroupBean {
    private String Group_Name;
    private ArrayList<String> Group_Member;
    private String Device_Id;
    private String Mobile_Number;

    public String getGroup_Name() {
        return Group_Name;
    }

    public String getDevice_Id() {
        return Device_Id;
    }

    public ArrayList<String> getGroup_Member() {
        return Group_Member;
    }

    public String getMobile_Number() {
        return Mobile_Number;
    }

    public void setGroup_Name(String group_Name) {
        Group_Name = group_Name;
    }

    public void setDevice_Id(String device_Id) {
        Device_Id = device_Id;
    }

    public void setGroup_Member(ArrayList<String> group_Member) {
        Group_Member = group_Member;
    }

    public void setMobile_Number(String mobile_Number) {
        Mobile_Number = mobile_Number;
    }
}
