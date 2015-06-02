package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

//Brahmastra Innovations...
//By Shushmit on 20/05/2015
//This class is a splash screen class.it appears for 5 sec only and then registration page will open.

public class Splash_screen extends ActionBarActivity {

    public static final String MyPreferences = "MyDetails";
    SharedPreferences sharedPreferences;

    private static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title...
        getSupportActionBar().hide();
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //SharedPreferences prefs = getSharedPreferences(
                //       "MyPrefs", Context.MODE_PRIVATE);

                //String deviceId = prefs.getString("deviceId",null);
                //Log.i("SplashActivity", "Device id .." + deviceId);

                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
                finish();

            }
        }, SPLASH_TIME_OUT);
    }
}
