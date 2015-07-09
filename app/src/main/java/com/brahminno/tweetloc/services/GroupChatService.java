package com.brahminno.tweetloc.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by Shushmit on 08-07-2015.
 * Brahmastra Innovations Pvt. Ltd.
 * This service will only run when user is using application only......
 */
public class GroupChatService extends Service {


    private final IBinder chatBind = new ChatBinder();

    public class ChatBinder extends Binder {
        GroupChatService getService() {
            return GroupChatService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return chatBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
        //return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
