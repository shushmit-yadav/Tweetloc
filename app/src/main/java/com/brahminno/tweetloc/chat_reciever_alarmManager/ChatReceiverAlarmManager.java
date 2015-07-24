package com.brahminno.tweetloc.chat_reciever_alarmManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.brahminno.tweetloc.asyncClass.ChatMessageRecieveAsyncTask;

/**
 * Created by Shushmit on 13-07-2015.
 */
public class ChatReceiverAlarmManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("inside ChatReceiverAlarmmanager..", ".....");
        Bundle bundle = intent.getExtras();
        String userMobileNumber = bundle.getString("User Mobile Number");
        new ChatMessageRecieveAsyncTask(context, userMobileNumber).execute();
    }
}
