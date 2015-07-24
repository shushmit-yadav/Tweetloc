package com.brahminno.tweetloc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shushmit Yadav on 20-07-2015.
 */
public class MyAlarmManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Inside alarmmanager"," class.....");
        Bundle bundle = intent.getExtras();
        String groupMembersList = bundle.getString("GroupMemberList");
        Log.i("size...","---> "+ groupMembersList);
        //Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_SHORT).show();
        if(!groupMembersList.isEmpty()){
            new GetGroupMemberLocation(context,groupMembersList).execute();
        }else{
            Log.i("size...","there is not any group member....");
        }
    }
}
