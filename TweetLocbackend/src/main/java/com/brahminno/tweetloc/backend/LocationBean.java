package com.brahminno.tweetloc.backend;

/**
 * Created by Shushmit on 30-05-2015.
 */
public class LocationBean {
    private double latitude;
    private double longitude;
    private String Drvice_Id;

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public String getDrvice_Id() {
        return Drvice_Id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDrvice_Id(String drvice_Id) {
        Drvice_Id = drvice_Id;
    }
}
