package com.brahminno.tweetloc.asyncClass;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.github.nkzawa.socketio.client.Url;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Shushmit on 09-07-2015.
 * Brahmastra Innovations Pvt. Ltd.
 * This AsyncTask class will use when user click on sendChat button........
 */
public class ChatMessageSendAsyncTask extends AsyncTask<Void,Void,String> {
    private Context context;
    private String message;
    private String senderMobileNumber;
    private String adminMobileNumber;
    private String groupName;
    private ArrayList<String> groupMemberMobileNumber;
    private Long timeStamp;

    public ChatMessageSendAsyncTask(Context context,String senderMobileNumber,String groupName,String adminMobileNumber,ArrayList<String> groupMemberMobileNumber,String message,Long timeStamp){
        this.context = context;
        this.senderMobileNumber = senderMobileNumber;
        this.groupName = groupName;
        this.adminMobileNumber = adminMobileNumber;
        this.groupMemberMobileNumber = groupMemberMobileNumber;
        this.message = message;
        this.timeStamp = timeStamp;
    }
    @Override
    protected String doInBackground(Void... params) {
        InputStream inputStream = null;
        String result = null;
        //create HttpClient.....
        HttpClient httpClient = new DefaultHttpClient();
        //Http POST request to given url....
        HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/sendchat");
        String json = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("senderMob", senderMobileNumber);
            jsonObject.put("adminMob",adminMobileNumber);
            jsonObject.put("memberList",groupMemberMobileNumber);
            jsonObject.put("groupName",groupName);
            jsonObject.put("message",message);
            jsonObject.put("tStamp",timeStamp);

            //convert jsonObject to json string....
            json = jsonObject.toString();
            //set json to StringEntity....
            StringEntity stringEntity = new StringEntity(json);
            //set httpPost Entity.....
            httpPost.setEntity(stringEntity);
            //set headers to inform server about type of content.....
            httpPost.setHeader("Content-type","application/json");
            //execute POST request to the given server.....
            HttpResponse httpResponse = httpClient.execute(httpPost);
            //receive response from server as inputStream............
            inputStream = httpResponse.getEntity().getContent();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //log json object....
        Log.i("json object...", String.valueOf(json));
        Log.i("message : ",message);
        Log.i("senderMob : ",senderMobileNumber);
        Log.i("adminMob : ",adminMobileNumber);
        Log.i("groupName : ",groupName);
        Log.i("memberList", String.valueOf(groupMemberMobileNumber));
        Log.i("tStamp : ",""+timeStamp);
        return null;
    }
}
