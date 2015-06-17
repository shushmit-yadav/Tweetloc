package com.brahminno.tweetloc.backend;

import java.util.ArrayList;

/**
 * Created by Shushmit on 13-06-2015.
 */
public class ContactSyncBean {

    private String MobileNumber;

    private ArrayList<String> number;

    public ArrayList<String> getNumber() {
        return number;
    }

    public void setNumber(ArrayList<String> number) {
        this.number = number;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }


}
