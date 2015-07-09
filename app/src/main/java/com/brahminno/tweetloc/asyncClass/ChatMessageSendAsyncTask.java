package com.brahminno.tweetloc.asyncClass;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Shushmit on 09-07-2015.
 */
public class ChatMessageSendAsyncTask extends AsyncTask<Void,Void,String> {
    private Context context;
    private String message;
    private String senderMobileNumber;
    private String adminMobileNumber;
    private String groupName;
    private Long timeStamp;

    public ChatMessageSendAsyncTask(Context context,String senderMobileNumber,String groupName,String adminMobileNumber,String message,Long timeStamp){
        this.context = context;
        this.senderMobileNumber = senderMobileNumber;
        this.groupName = groupName;
        this.adminMobileNumber = adminMobileNumber;
        this.message = message;
        this.timeStamp = timeStamp;
    }
    @Override
    protected String doInBackground(Void... params) {
        Log.i("message : ",message);
        Log.i("senderMobileNumber : ",senderMobileNumber);
        Log.i("adminMobileNumber : ",adminMobileNumber);
        Log.i("groupName : ",groupName);
        Log.i("timeStamp : ",""+timeStamp);
        return null;
    }
}
