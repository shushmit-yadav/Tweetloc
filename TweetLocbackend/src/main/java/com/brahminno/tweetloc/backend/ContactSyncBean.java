package com.brahminno.tweetloc.backend;

import java.util.ArrayList;

/**
 * Created by Shushmit on 13-06-2015.
 */
public class ContactSyncBean {
    private ArrayList<String> number;
    private String Mobile_Number;

    public String getMobile_Number() {
        return Mobile_Number;
    }

    public void setMobile_Number(String mobile_Number) {
        Mobile_Number = mobile_Number;
    }

    public ArrayList<String> getNumber() {
        return number;
    }

    public void setNumber(ArrayList<String> number) {
        this.number = number;
    }
}
