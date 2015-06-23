package com.brahminno.tweetloc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.ContactSyncBean;
import com.brahminno.tweetloc.backend.tweetApi.model.LocationBean;
import com.brahminno.tweetloc.backend.tweetApi.model.RegistrationBean;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;

//this is a AsyncTask class that push location to server in background.....
class LocationAsyncTask extends AsyncTask<Void, Void, String> {
    private static TweetApi myTweetApi = null;
    private Context context;
    private String Device_Id;
    private double latitude;
    private double longitude;

    public LocationAsyncTask(Context context, String Device_Id, double latitude, double longitude) {
        this.context = context;
        this.Device_Id = Device_Id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try {

            LocationBean locationBean = new LocationBean();
            locationBean.setDrviceId(Device_Id);
            locationBean.setLatitude(latitude);
            locationBean.setLongitude(longitude);

            myTweetApi.storeLocation(locationBean).execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(s);
    }
}
//AsyncTask ends here....

//This AsynTask is used to delete the information.....
class ForgetAsyncTask extends AsyncTask<Void, Void, String> {
    private static TweetApi myTweetApi = null;
    private Context context;
    private String Device_Id;

    public ForgetAsyncTask(Context context, String Device_Id) {
        context = null;
        this.Device_Id = Device_Id;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try {

            myTweetApi.forgetMe(Device_Id).execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(s);
    }
}

//this is a AsyncClass to send all contact to server and server returns arraylist of numbers presented in server and save it to app local sqlite database....
class NumberSyncFromServer extends AsyncTask<Void, Void, String>{

    private static TweetApi myTweetApi = null;
    private Context context;
    SQLiteDatabase myDB;
    ContactSyncBean contactSyncBean;
    FetchContacts contacts;


    public NumberSyncFromServer(Context context, FetchContacts contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i("NumberSyncFromServer...","is called");
        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try {
            Log.i("Contact Number", contacts.getNumber().get(1));
            Log.i("Contact Name",contacts.getName().get(1));
            ContactSyncBean syncBean = new ContactSyncBean();
            syncBean.setNumber(contacts.getNumber());
            syncBean.setName(contacts.getName());
            contactSyncBean= myTweetApi.contactSync(syncBean).execute();
            Log.i("Recieved response ...", "from server");
            ArrayList<String> MobileNumber = (ArrayList<String>) contactSyncBean.getNumber();
            ArrayList<String> MobileName = (ArrayList<String>) contactSyncBean.getName();
            Log.i("Contact Number", MobileNumber.get(0));
            Log.i("Contact Name", MobileName.get(0));
            Log.i("ContactList size...",""+MobileNumber.size());

            //save arraylist to sqlite database......
            myDB = new SQLiteDatabase(context);
            //clear data from app db....
            myDB.deleteNumberArrayList();
            Log.i("Contact from server...", MobileNumber.get(0));
            myDB.insertNumberArrayList(MobileNumber, MobileName);
            //myDB.deleteAddContactFromInvite(MobileNumber);
            Log.i("Contact size..",""+MobileNumber.size());
            Log.i("Value contains..",""+MobileNumber.contains(contacts.getNumber().get(1)));

            //first delete items from Invite_Contacts_Table and then store in.....
            myDB.deleteInviteTableItems();

            //below codes are using to store contacts in Invite_Contacts_Table........
            ArrayList<String> Mob_Num = contacts.getNumber();
            ArrayList<String> Mob_Name = contacts.getName();
            for(int i = 0; i < Mob_Num.size(); i++){
                Log.i("Inside for loop..",""+Mob_Num.get(i));
                //boolean result = myDB.deleteRow(MobileNumber.get(i));
                Log.i("Result of insertion.."," "+MobileNumber.get(i)+"-->"+MobileName.get(i));
                if(!MobileNumber.contains(Mob_Num.get(i))){
                    Log.i("Inside If loop...",""+!MobileNumber.contains(Mob_Num.get(i)));
                    myDB.insertIntoInvite(Mob_Name.get(i),Mob_Num.get(i));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

//*************************************************************************************

//this is a main class of MyTrail activity where we plot the map and show the location...

public class MyTrail extends ActionBarActivity implements LocationListener, com.google.android.gms.location.LocationListener{

    GoogleMap map;
    double latitude, longitude;
    String deviceId;
    Button btnGroup;
    LocationManager locationManager;
    SQLiteDatabase mydb;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    StandardMobileNumberFormat standardMobileNumberFormat;
    MarkerOptions marker;
    TelephonyManager telephonyManager;
    String countryCode;
    boolean isContactSyncFromServer = false;

    Location final_location,gps_location,network_location;

    private int count = 0; //counter to ensure onetimecamerasettouserloc function executes once on location change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_hdr_mytrail_128);
        //Check that GooglePlayervicesAvailable or not.....
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_my_trail);
        btnGroup = (Button) findViewById(R.id.btnGroup);
        //this method is used to get countryCode isung telephonyManager....
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        countryCode = telephonyManager.getNetworkCountryIso().toUpperCase();
        //call NumberSyncFromServer class to sync all contact automatically from server....
        //first we fetch all contacts from mobile device and then we execute Async Class.....
        //first we check that app install first time on device or not...
        //get value from shared preference.......
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        isContactSyncFromServer = prefs.getBoolean("isContactSyncFromServer", false);
        if(!isContactSyncFromServer){
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("MyPrefs", getApplicationContext().MODE_PRIVATE).edit();
            editor.putBoolean("isContactSyncFromServer",true);
            editor.commit();
            FetchContacts contacts = fetchContact();
            try{
                //save all contacts to loacal app db....
                mydb = new SQLiteDatabase(this);
                //mydb.insertIntoInvite(contacts);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            //After Successfully fetching all contact mobNum, call AsyncTask class to send this contact to server.......
            new NumberSyncFromServer(getApplicationContext(),contacts).execute();
        }
        //check if gps is available or not.....
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        try{
            //getting GPS Status....
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.i("GPS Status...", "" + isGPSEnabled);

            //getting Network Status....
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.i("Network Status...", "" + isNetworkEnabled);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        if (!isGPSEnabled && !isNetworkEnabled) {
            //if gps and network both will disable.....then show settings to open gps.....
            showGPSSettings();
        }
         else{
            Log.i("GPS Status","GPS is already activated!!!!");
        }
        //if gps enabled.....
        if(isGPSEnabled){
            gps_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(gps_location != null){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,20000,10,this);
            }
        }
        //if Network enabled.....
        if(isNetworkEnabled){
            network_location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(network_location != null){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,20000,10,this);
            }
        }
        if(gps_location != null && network_location != null){
            if(gps_location.getAccuracy() > network_location.getAccuracy()){
                final_location = gps_location;
            }
            else{
                final_location = network_location;
            }
        }
        else {
            if (gps_location != null) {
                final_location = gps_location;
            } else {
                final_location = network_location;
            }
        }

        if(final_location != null){
            latitude = final_location.getLatitude();
            longitude = final_location.getLongitude();
        }

        //Intent to recieve data from previous activity class
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        deviceId = bundle.getString("Device_Id", deviceId);
        Toast.makeText(getApplicationContext(), deviceId, Toast.LENGTH_SHORT).show();
        try {

            //call AsyncTask class to push location on server.....
            new LocationAsyncTask(getApplicationContext(), deviceId, latitude, longitude).execute();

            initilizeMap();


        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


        //When Group Button clicks then this method will execute...
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initilizeMap() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.mapFragment)).getMap();
        }
        Log.i("LatLog","latlog is....." + latitude);
        marker = new MarkerOptions().position(new LatLng(latitude, longitude));
        map.addMarker(marker);

        map.setMyLocationEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15).build();


        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        if(count == 0){
            oneTimeCameraSetToUserLoc(location);
            count = 1;
        }
        //call AsyncTask class to push location on server when location changes.....
        new LocationAsyncTask(getApplicationContext(), deviceId, latitude, longitude).execute();

        map.clear();

    }

    public void oneTimeCameraSetToUserLoc(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public void showGPSSettings() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS is not available.");
        builder.setMessage("Do you want to activate GPS?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Launch settings, allowing user to make a change
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                startActivity(i);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //No location service, no Activity
                Toast.makeText(getApplicationContext(),"This app requires GPS for precize location !!!",Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_trail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), User_ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_invte) {
            //call inviteContacts() method to invite the contacts through installed app in device...
            inviteContacts();
            return true;
        }
        if (id == R.id.action_forget_me) {
            forgetMe();
            return true;
        }
        if(id == R.id.action_contactSync){
            //first fetch all contact mobNum from device.......
            FetchContacts contacts = fetchContact();

            try{
                //save all contacts to loacal app db....
                mydb = new SQLiteDatabase(this);
                //mydb.insertIntoInvite(contacts);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            //After Successfully fetching all contact mobNum, call AsyncTask class to send this contact to server.......
            new NumberSyncFromServer(getApplicationContext(),contacts).execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void forgetMe() {
        // mydb.deleteInfo(new RegistrationInfo(deviceId));
        new ForgetAsyncTask(getApplicationContext(), deviceId).execute();

        //Clear all data from shared preference....
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    //this method is used for inviting contacts through social media android app....
    public void inviteContacts() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "here is the link to download TweetLoc");
        startActivity(intent);
    }
    private FetchContacts fetchContact(){
        //Set URI....
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //Set Projection
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        FetchContacts contacts = new FetchContacts();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> numberList = new ArrayList<>();
        Cursor people = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        people.moveToFirst();
        do {
            String name = people.getString(indexName);
            String number = people.getString(indexNumber);
            standardMobileNumberFormat = new StandardMobileNumberFormat();
            String standardNumber = standardMobileNumberFormat.getLocale(number,countryCode);
            Log.i("standard number...", "" + standardNumber);
            if(standardNumber != null){
                nameList.add(name);
                numberList.add(standardNumber);
                Log.i("Inside sync...","contacts"+name +"-->" + standardNumber);
            }
        }
        while (people.moveToNext());
        contacts.setName(nameList);
        contacts.setNumber(numberList);
        return contacts;
    }
}

/**
 * Created by Shushmit Yadav.
 * Brahmastra Innovations Pvt. Ltd.
 * this class is used for model of contacts......
 * this is a fetch contacts class to fetch numbers with name....
*/
class FetchContacts{
    private ArrayList<String> name;
    private ArrayList<String> number;

    public ArrayList<String> getName() {
        return name;
    }

    public ArrayList<String> getNumber() {
        return number;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
    }

    public void setNumber(ArrayList<String> number) {
        this.number = number;
    }
}