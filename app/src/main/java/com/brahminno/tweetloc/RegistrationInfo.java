package com.brahminno.tweetloc;

/**
 * Created by Shushmit on 25-05-2015.
 * Brahmastra Innovations
 */
public class RegistrationInfo {

    String deviceID;
    String mobileNO;
    String emailID;

    public RegistrationInfo(){

    }
    public RegistrationInfo(String deviceID,String mobileNO,String emailID){
        this.deviceID  = deviceID;
        this.mobileNO = mobileNO;
        this.emailID = emailID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getMobileNO() {
        return mobileNO;
    }

    public void setMobileNO(String mobileNO) {
        this.mobileNO = mobileNO;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }
}
