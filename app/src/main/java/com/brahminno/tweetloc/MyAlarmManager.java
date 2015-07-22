package com.brahminno.tweetloc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
        String groupMemberList = bundle.getString("GroupMemberList");
        Log.i("size...","---> "+ groupMemberList);
        //Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_SHORT).show();
        new GetGroupMemberLocation(context,groupMemberList).execute();
    }
}
