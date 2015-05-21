package com.brahminno.tweetloc;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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



    //private SQLiteDatabase mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To hide header...
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registration);
        tvTC = (TextView) findViewById(R.id.tvTC);
        ckBoxTC = (CheckBox) findViewById(R.id.ckBoxTC);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        //mydb = new SQLiteDatabase(this);
        //On Button Click Event....
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                number = strNumber;
                //saveData();
                if (ckBoxTC.isChecked()){
                    if(number == null){
                        strNumber = showInputDialog();
                    }
                    else{

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

            //call method to get email....
            primaryEmail = getEmail();

            //Retrieve User Mobile Number.
            number = getMobileNumber();
            //check if gps is enabled...
            gps = new GPSTracker(this);
            if (gps.isGPSTeackingEnabled()) {

                Toast.makeText(getApplicationContext(),String.valueOf(gps.latitude),Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),String.valueOf(gps.longitude),Toast.LENGTH_SHORT).show();
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
        return strMobileNumber;
    }




    //method to check validation...
    private boolean validate(){
        if(number.trim().equals("")){
            return false;
        }
        else if(primaryEmail.trim().equals("")){
            return false;
        }
        else if (deviceID.trim().equals("")){
            return false;
        }
        else {
            return true;
        }
    }

    /*
    void saveData(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            int Value = extras.getInt("id");
            if(mydb.insertInfo(tvMobileNumber.getText().toString(),tvEmail.getText().toString(),tvDeviceId.getText().toString())){
                Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Not Done",Toast.LENGTH_SHORT).show();
            }

        }
    }

    */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
