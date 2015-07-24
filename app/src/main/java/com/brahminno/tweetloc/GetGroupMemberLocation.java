package com.brahminno.tweetloc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Created by Shushmit Yadav on 21-07-2015.
 */
public class GetGroupMemberLocation extends AsyncTask<Void,Void,JSONArray> {
    private Context context;
    private String groupMemberMobNo;
    private JSONArray responseJsonArray;

    public GetGroupMemberLocation(Context context,String groupMemberMobNo) {
        this.context = context;
        this.groupMemberMobNo = groupMemberMobNo;
    }

    //Check Internet
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        if(isNetworkAvailable()){
            try{
                Log.i("groupMemberMobNo","---> "+ groupMemberMobNo);
                Log.i("inside GetGroupMemberLocation...", "GetGroupMemberLocation...");
                //create HttpClient.....
                HttpClient httpClient = new DefaultHttpClient();
                //Http POST request to given url....
                HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/getlocation");
                String json = null;
                try {
                    json = groupMemberMobNo;
                    //set json to StringEntity....
                    Log.i("inside json......", json);
                    StringEntity stringEntity = new StringEntity(json);
                    //set httpPost Entity.....
                    httpPost.setEntity(stringEntity);
                    //set headers to inform server about type of content.....
                    httpPost.setHeader("Content-type", "application/json");
                    //execute POST request to the given server.....
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    String responseResult = EntityUtils.toString(httpResponse.getEntity());
                    //convert responseResult to jsonObject....
                    JSONObject obj = new JSONObject(responseResult);
                    responseJsonArray = obj.getJSONArray("membersLocation");
                    Log.i("responseJsonArray...: ", "--> " + responseJsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        else{
            Toast.makeText(context.getApplicationContext(),"Please connect to internet!!!",Toast.LENGTH_SHORT).show();
        }
        return responseJsonArray;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        if(jsonArray.length() > 0){
            Chat_Main_Fragment.getGroupMemberLocationResponse(context,jsonArray);
        }else{
            Log.i("there is no response"," from user location...");
        }
    }
}
