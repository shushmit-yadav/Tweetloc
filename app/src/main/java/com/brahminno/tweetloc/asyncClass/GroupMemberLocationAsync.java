package com.brahminno.tweetloc.asyncClass;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.brahminno.tweetloc.MyTrail;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Shushmit on 08-07-2015.
 */
public class GroupMemberLocationAsync extends AsyncTask<Void,Void,String> {
    private Context context;
    private Double latitide;
    private Double longitude;
    private Double altitude;
    private int speed;
    private String userMobileNumber;
    private JSONArray groupMemberMobileNumber;
    private Long timeStamp;

    public GroupMemberLocationAsync(Context context, Double latitide, Double longitude, Double altitude, int speed, Long timeStamp, String userMobileNumber, JSONArray groupMemberMobileNumber){
        this.context = context;
        this.latitide = latitide;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.timeStamp = timeStamp;
        this.userMobileNumber = userMobileNumber;
        this.groupMemberMobileNumber = groupMemberMobileNumber;
    }

    //this is a socket connection
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://qcapp-prateeksonkar.rhcloud.com");
        } catch (URISyntaxException e) {}
    }

    protected String doInBackground(Void... params) {
        //make connection to socket.....
        if(!mSocket.connected())
            mSocket.connect();

        Emitter.Listener onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        Double latitude,longitude,altitude;
                        float speed;
                        Long timeStamp;
                        String userMobileNumber;
                        JSONArray groupMemberMobileNumber;
                        try {
                            latitude = data.getDouble("latitude");
                            longitude = data.getDouble("longitude");
                            altitude = data.getDouble("altitude");
                            speed = data.getInt("speed");
                            timeStamp = data.getLong("timeStamp");
                            userMobileNumber = data.getString("userMobileNumber");
                            groupMemberMobileNumber = (JSONArray) data.get("groupMemberMobileNumber");
                        } catch (JSONException e) {
                            return;
                        }
                        Log.i("Socket On...", "" + userMobileNumber);
                        try {
                            Log.i("Socket On....."," "+groupMemberMobileNumber.get(0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };


        JSONObject testMessage = new JSONObject();
        try {
            testMessage.put("latitude",latitide);
            testMessage.put("longitude",longitude);
            testMessage.put("altitude",altitude);
            testMessage.put("speed",speed);
            testMessage.put("timeStamp",timeStamp);
            testMessage.put("GroupMemberMobileNumber",groupMemberMobileNumber);
            testMessage.put("userMobileNumber",userMobileNumber);
            mSocket.emit("togroup", testMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.on("fromgroup", onNewMessage);
        return null;
    }
}
