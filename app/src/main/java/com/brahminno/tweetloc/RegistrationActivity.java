package com.brahminno.tweetloc;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Pattern;

//This class is for user registration.....
//By Shushmit on 20-05-2015.
//Brahmastra Innovations.
public class RegistrationActivity extends ActionBarActivity {

    //TextView tvDeviceId,tvEmail,tvMobileNumber;
    TextView tvTC;
    Button btnRegister;
    CheckBox ckBoxTC;

    GPSTracker gps;

    String number;
    String primaryEmail;
    String primaryEmailID;
    String strNumber;
    String deviceID;
    SQLiteDatabase mydb;
    double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To hide header...
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registration);
        tvTC = (TextView) findViewById(R.id.tvTC);
        ckBoxTC = (CheckBox) findViewById(R.id.ckBoxTC);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        mydb = new SQLiteDatabase(this);
        //On Button Click Event....
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //number = strNumber;
                //saveData();
                if (ckBoxTC.isChecked()){
                    if(number == null){
                        strNumber = showInputDialog();
                        number = strNumber;
                    }
                    else{
                        //Call AsynTask to perform network operation 
                        new HttpAsynTask().execute("https://qcapp-prateeksonkar.rhcloud.com/register");

                        mydb.insertInfo(new RegistrationInfo(deviceID,number,primaryEmail));
                        //Toast.makeText(getApplicationContext(),number,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(),MyTrail.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("Latitude",latitude);
                        bundle.putDouble("Longitude",longitude);
                        intent.putExtras(bundle);
                        startActivity(intent);

                        finish();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Accept Terms and Conditions",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Check Internet if yes then goto else
        if (!isNetworkAvailable()){
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        else{
            //Retrieve Device Id...
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            deviceID = getDeviceID(telephonyManager);
            Toast.makeText(getApplicationContext(),deviceID,Toast.LENGTH_SHORT).show();

            //call method to get email....
            primaryEmail = getEmail();
            Toast.makeText(getApplicationContext(),primaryEmail,Toast.LENGTH_SHORT).show();

            //Retrieve User Mobile Number.
            number = getMobileNumber();
            //check if gps is enabled...
            gps = new GPSTracker(this);
            if (gps.isGPSTeackingEnabled()) {
                Toast.makeText(getApplicationContext(),String.valueOf(gps.latitude),Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),String.valueOf(gps.longitude),Toast.LENGTH_SHORT).show();
                latitude = gps.latitude;
                longitude = gps.longitude;
            }
            else{
                gps.showSettingsAlert();
            }
        }
    }

    //Check Internet
    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }

    //To get the Unique Device Id...
    String getDeviceID(TelephonyManager manager){
        String id = manager.getDeviceId();
        if(id == null){
            id = "Not Available";
        }
        int phoneType = manager.getPhoneType();
        switch(phoneType){
            case TelephonyManager.PHONE_TYPE_NONE:
                return  "" + id;

            case TelephonyManager.PHONE_TYPE_GSM:
                return "" + id;

            case TelephonyManager.PHONE_TYPE_CDMA:
                return "" + id;

            default:
                return "" +id;
        }
    }

    //To get Email Id....
    public String getEmail(){

        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType("com.google");
        for (Account account : accounts){
            if(emailPattern.matcher(account.name).matches()){
                primaryEmailID = account.name;
            }
        }
        return primaryEmailID;
    }

    //This method is for obtaining mobile number...
    public String getMobileNumber(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String strMobileNumber = manager.getLine1Number();
        Toast.makeText(getApplicationContext(),strMobileNumber,Toast.LENGTH_SHORT).show();
        return strMobileNumber;
    }

    //If Mobile Number does not fetch automatically.....
    public String showInputDialog(){

        //get prompt.xml view...
        LayoutInflater inflater = LayoutInflater.from(RegistrationActivity.this);
        View promptView = inflater.inflate(R.layout.input_number, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegistrationActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText etNumber = (EditText) promptView.findViewById(R.id.etNumber);

        //set dialog window...
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strNumber = etNumber.getText().toString();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
        return strNumber;
    }

    //post method to post json on server....
    public   String makePostRequest(String url){
        InputStream inputStream = null;
        String result = "";

        try {

            //create HttpClient...
            HttpClient httpClient = new DefaultHttpClient();
            //make post reguest to given url..
            HttpPost httpPost = new HttpPost(url);

            String json = "";
            //build jsonObject...
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("Mobile Number",number);
            jsonObject.put("Email",primaryEmail);
            jsonObject.put("Device id",deviceID);

            //convert jsonObject to JSON to String...
            json = jsonObject.toString();
            //set json to StringEntity..
            StringEntity stringEntity = new StringEntity(json);

            //set Header to inform server about the type of content..
            httpPost.setHeader("Content-type","application/json");

            //execute POST request to given url..
            HttpResponse httpResponse = httpClient.execute(httpPost);

            //recieve response as InputStream...
            inputStream = httpResponse.getEntity().getContent();
            //convert inputStream to String...
            if ((inputStream != null)){
                result = convertInputStreamToString(inputStream);
            }
            else {
                result = "Did not work";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return result..
        return result;
    }
    //AsynTask to perform operation in background...
    private class HttpAsynTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            return makePostRequest(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getBaseContext(),result,Toast.LENGTH_SHORT).show();
        }
    }


    //Method to convert inputStream to String....
    private String convertInputStreamToString (InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";
        String strResult = "";
        while((line = bufferedReader.readLine()) != null){
            strResult += line;
        }
        inputStream.close();
        return strResult;
    }
}
