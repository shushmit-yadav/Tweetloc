package com.brahminno.tweetloc.backend;

import java.util.ArrayList;

/**
 * Created by Shushmit on 13-06-2015.
 */
public class ContactSyncBean {

    private String MobileNumber;

    private ArrayList<String> number;

    private ArrayList<String> name;



    public ArrayList<String> getNumber() {
        return number;
    }

    public ArrayList<String> getName() {
        return name;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
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
