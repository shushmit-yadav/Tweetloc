package com.brahminno.tweetloc.asyncClass;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Shushmit Yadav on 24-07-2015.
 * Brahmastra Innovations Pvt. Ltd.
 * this AsyncTask will call through Alarm Manager in every 2 second.......
 */
public class ChatMessageRecieveAsyncTask extends AsyncTask<Void,Void,JSONArray> {
    private Context context;
    private String userNumber;
    public ChatMessageRecieveAsyncTask(Context context,String userNumber){
        this.context = context;
        this.userNumber = userNumber;
    }
    @Override
    protected JSONArray doInBackground(Void... params) {
        try{
            //create HttpClient.....
            HttpClient httpClient = new DefaultHttpClient();
            //Http POST request to given url....
            HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/getchat");
            String json = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userNumber",userNumber);
                //convert jsonObject to json string....
                json = jsonObject.toString();
                //set json to StringEntity....
                Log.i("json......", json);
                StringEntity stringEntity = new StringEntity(json);
                //set httpPost Entity.....
                httpPost.setEntity(stringEntity);
                //set headers to inform server about type of content.....
                httpPost.setHeader("Content-type", "application/json");
                //execute POST request to the given server.....
                HttpResponse httpResponse = httpClient.execute(httpPost);
                //receive response from server as inputStream............
                String responseResult = EntityUtils.toString(httpResponse.getEntity());
                Log.i("responseResult...: ", responseResult);
                //JSONObject obj = new JSONObject(responseResult);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        Log.i("inside..."," onPostExecute method of ChatMessageRecieveAsyncTask"+jsonArray);
    }
}
