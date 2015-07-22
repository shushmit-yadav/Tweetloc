package com.brahminno.tweetloc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Shushmit Yadav on 21-07-2015.
 * Brahmastra Innovations Pvt. Ltd.
 * This class is used for getting notifications.......
 */
public class NotificationAlarmManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("inside NotificationAlarmManager..",".....");
        Bundle bundle = intent.getExtras();
        String userMobileNumber = bundle.getString("User Mobile Number");
        new FetchNotificationAsyncTask(context, userMobileNumber).execute();
    }
}
