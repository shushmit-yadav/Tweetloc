package com.brahminno.tweetloc;

/**
 * Created by Shushmit on 05-06-2015.
 */
public class Contact {
    String name;
    String number;

    public Contact(String name,String number){
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
