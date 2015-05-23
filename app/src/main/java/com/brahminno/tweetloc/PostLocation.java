package com.brahminno.tweetloc;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Shushmit on 22-05-2015.
 * Brahmastra Innovations
 * this class is used to post the location to server in background...
 */
public class PostLocation extends AsyncTask<String,Void,Boolean> {
    private Context context;

    public  PostLocation(Context context){
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(String... params) {

        StringBuilder builder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(params[0]);

        try{
            HttpResponse httpResponse = httpClient.execute(httpPost);

            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream content = httpEntity.getContent();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(content));

                String line;
                while((line = bufferedReader.readLine()) != null){
                    builder.append(line);
                }
                return true;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean flag) {
        super.onPostExecute(flag);
    }
}
