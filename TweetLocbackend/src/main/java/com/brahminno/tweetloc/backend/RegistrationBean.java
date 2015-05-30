package com.brahminno.tweetloc.backend;

import java.lang.String;

/**
 * The object model for the data we are sending through endpoints
 */
public class RegistrationBean {

    private String Mobile_Number;
    private String Email_Id;
    private String Device_Id;


    public String getMobile_Number() {
        return Mobile_Number;
    }
    public String getEmail_Id(){
        return Email_Id;
    }

    public String getDevice_Id() {
        return Device_Id;
    }

    public void setMobile_Number(String mobile_Number) {
        Mobile_Number = mobile_Number;
    }

    public void setEmail_Id(String email_Id) {
        Email_Id = email_Id;
    }

    public void setDevice_Id(String device_Id) {
        Device_Id = device_Id;
    }

}