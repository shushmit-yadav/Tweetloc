package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

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
        /*//generate SHA1 key.....
        try{
            Log.i("Inside PackageInfo","....");
            PackageInfo info = getPackageManager().getPackageInfo("com.brahminno.tweetloc", PackageManager.GET_SIGNATURES);
            for(android.content.pm.Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());
                Log.i("SHA1 Key is...", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String deviceId = prefs.getString("Device Id", null);
                String userNumber = prefs.getString("Mobile Number", null);
                Log.i("SplashActivity", "Device id .." + deviceId);
                if(userNumber != null){
                    new GroupDetailsAsyncTask(getApplicationContext(), userNumber).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                if(deviceId == null){
                    Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),MyTrail.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Device_Id",deviceId);
                    bundle.putString("User_Mob_No",userNumber);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }
}
