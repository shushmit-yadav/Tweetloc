package com.brahminno.tweetloc;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by Shushmit on 20-05-2015.
 * Brahmastra Innovations
 * this is a service class to check gps and update location time to time...
 */
public class GPSTracker extends Service implements LocationListener {

    //Get Class Name...
    private static String TAG = GPSTracker.class.getName();
    private final Context mContext;
    //GPS Status...
    boolean isGPSEnabled = false;
    //Network Status...
    boolean isNetworkEnabled = false;
    //GPS Tracking....
    boolean isGPSTeackingEnabled = false;

    Location location;
    double latitude;
    double longitude;

    int geocoderMaxResults = 1;

    //Minimum distance of change in meter...
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;

    //Minimum time for update location
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 1;

    //Declaring Location Manager..
    protected LocationManager locationManager;

    //Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER info...
    private String provider_info;


    public GPSTracker(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    //Getting current location by GPS or Network Provider...
    public void getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS status..
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //getting network status...
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            //get location if gps enabled...
            if (isGPSEnabled) {
                this.isGPSTeackingEnabled = true;
                Log.d(TAG, "GPS enabled..");

                provider_info = LocationManager.GPS_PROVIDER;
            } else if (isNetworkEnabled) {
                this.isGPSTeackingEnabled = true;
                Log.d(TAG, "Network status is used for location");

                provider_info = LocationManager.NETWORK_PROVIDER;
            }
            if (!provider_info.isEmpty()) {
                locationManager.requestLocationUpdates(provider_info, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(provider_info);
                    updateGPSCoordinates();
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Unable to connect location manager", ex);
        }
    }

    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    //GPSTracker latitude getter and setter
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    //GPSTracker longitude getter and setter..
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    //GPSTracker isGPSTrackingEnabled getter
    public boolean isGPSTeackingEnabled() {
        return this.isGPSTeackingEnabled;
    }

    //Stop using GPS listener...
    //call this method stop using GPS...
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    //function to show settings alert dialog..
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting dialog title...
        alertDialog.setTitle("GPS Settings");
        //setting dialog message
        alertDialog.setMessage("GPS is not enabled. Do you want enable it?");
        //on pressing settings button..
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", true);
                mContext.sendBroadcast(intent);

                String provider = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if(!provider.contains("gps")){ //if gps is disabled
                    final Intent poke = new Intent();
                    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                    poke.setData(Uri.parse("3"));
                    mContext.sendBroadcast(poke);



                }
                */
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
                                //call method to get location again...
            }
        });

        //on pressing cancel button...
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
        getLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        getLatitude();
        getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
