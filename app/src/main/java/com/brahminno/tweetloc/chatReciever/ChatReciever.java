package com.brahminno.tweetloc.chatReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.brahminno.tweetloc.MyTrail;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by Shushmit on 13-07-2015.
 */
public class ChatReciever extends BroadcastReceiver {

    //this is a socket connection
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://104.236.27.79:8080");
        } catch (URISyntaxException e) {}
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
