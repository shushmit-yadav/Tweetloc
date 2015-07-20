package com.brahminno.tweetloc;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
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
import com.brahminno.tweetloc.chatReciever.ChatReciever;
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//socket.io imports....
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

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

//this is a AsyncTask class that push location to server in background.....
class LocationAsyncTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private double latitude;
    private double longitude;
    private double altitude;
    private String userNumber;
    private float speed;

    public LocationAsyncTask(Context context, String userNumber, double latitude, double longitude, double altitude, float speed) {
        this.context = context;
        this.userNumber = userNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {//create HttpClient.....
            HttpClient httpClient = new DefaultHttpClient();
            //Http POST request to given url....
            HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/setlocation");
            String json = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userNumber", userNumber);
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
                jsonObject.put("altitude", altitude);
                jsonObject.put("speed", speed);
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
                //convert responseResult into jsonObject...
                JSONObject responseJsonObject = new JSONObject(responseResult);
                Log.i("responseJsonObject...: ", "" + responseJsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
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
    private Context context;
    private String Device_Id;

    public ForgetAsyncTask(Context context, String Device_Id) {
        this.context = context;
        this.Device_Id = Device_Id;
    }

    @Override
    protected String doInBackground(Void... params) {
        //create HttpClient.....
        HttpClient httpClient = new DefaultHttpClient();
        //Http POST request to given url....
        HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/forgetme");
        String json = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceID", Device_Id);
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
            if(responseResult.equals("OK")){
                Log.i("inside if loop...: ", "if status is " + responseResult);
                //Clear all data from shared preference....
                SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
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
        return "";
    }
}

//this is a AsyncClass to send all contact to server and server returns arraylist of numbers presented in server and save it to app local sqlite database....
class NumberSyncFromServer extends AsyncTask<Void, Void, String> {
    private Context context;
    SQLiteDatabase myDB;
    JSONArray contactsNumJsonArray;
    FetchContacts contacts;
    JSONObject jsonObj;
    String responseResult;
    ArrayList<String> contactNumber, contactName, contact_Number, contact_Name;

    public NumberSyncFromServer(Context context, FetchContacts contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i("NumberSyncFromServer...", "is called");
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/synccontact");
        String json = null;
        contactsNumJsonArray = new JSONArray();

        for (int i = 0; i < contacts.getNumber().size(); i++) {
            //obj.put("contactsNum", contacts.getNumber().get(i));
            contactsNumJsonArray.put(contacts.getNumber().get(i));
        }
        try {
            jsonObj = new JSONObject();
            jsonObj.put("usercontact", contactsNumJsonArray);
            Log.i("contacts...", "-->" + jsonObj);
            json = jsonObj.toString();
            StringEntity stringEntity = null;
            stringEntity = new StringEntity(json);
            //set httpPost Entity.....
            httpPost.setEntity(stringEntity);
            //set headers to inform server about type of content.....
            httpPost.setHeader("Content-type", "application/json");
            //execute POST request to the given server.....
            HttpResponse httpResponse = null;

            httpResponse = httpClient.execute(httpPost);
            //receive response from server as inputStream............
            responseResult = EntityUtils.toString(httpResponse.getEntity());
            Log.i("responseResult...: ", responseResult);
            JSONObject responseJsonObj = new JSONObject(responseResult);
            JSONArray responseJsonArray = responseJsonObj.getJSONArray("syncedcontact");
            contactName = new ArrayList<>();
            contactNumber = new ArrayList<>();
            for (int i = 0; i < responseJsonArray.length(); i++) {
                JSONObject jsonObjectFromJsonArray = responseJsonArray.getJSONObject(i);
                String contactNum = jsonObjectFromJsonArray.getString("mobileNo");
                for (int j = 0; j < contacts.getNumber().size(); j++) {
                    if (contacts.getNumber().get(j).equals(contactNum)) {
                        contactName.add(contacts.getName().get(j));
                        contactNumber.add(contacts.getNumber().get(j));
                    }
                }
            }
            //call SQLiteDatabase method to store contacts in database.....
            //save arraylist to sqlite database......
            myDB = new SQLiteDatabase(context);
            //clear data from app db....
            myDB.deleteNumberArrayList();
            Log.i("Contact from server...", contactName.get(0));
            myDB.insertNumberArrayList(contactNumber, contactName);
            //first delete items from Invite_Contacts_Table and then store in.....
            myDB.deleteInviteTableItems();
            for (int i = 0; i < contacts.getNumber().size(); i++) {
                if (!contactNumber.contains(contacts.getNumber().get(i))) {
                    myDB.insertIntoInvite(contacts.getName().get(i), contacts.getNumber().get(i));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

//this class is only used for fetching notification from server if any notification is available.....
//this is testing class.....
class FetchNotificationAsyncTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private String userMobileNumber;
    private String notificationMessage;
    private String noteContext;
    private String groupAdminMobNo;
    private String groupName;
    private String groupMemberMobNo;
    SQLiteDatabase mydb;
    private static final String TAG = "responseJson into NumberSyncFromServer";

    public FetchNotificationAsyncTask(Context context, String userMobileNumber) {
        this.context = context;
        this.userMobileNumber = userMobileNumber;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i("inside...", "FetchNotificationAsyncTask");
        //create HttpClient.....
        HttpClient httpClient = new DefaultHttpClient();
        //Http POST request to given url....
        HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/mynotice");
        String json = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobileNum", userMobileNumber);
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
            //get httpResponse into string.....
            String responseResult = EntityUtils.toString(httpResponse.getEntity());
            //convert responseResult to jsonObject......
            JSONObject responseJsonObject = new JSONObject(responseResult);
            Log.i("ABCD", "" + responseJsonObject);
            JSONArray array = responseJsonObject.getJSONArray("notification");
            Log.i("json array is... :", "" + array.length());
            mydb = new SQLiteDatabase(context);
            for (int i = 0; i < array.length(); i++) {
                JSONObject newJsonObject = (JSONObject) array.get(i);
                notificationMessage = newJsonObject.getString("noteMessage");
                noteContext = newJsonObject.getString("noteContext");
                JSONArray groupMemberNumJsonArray = newJsonObject.getJSONArray("noteAuxilGrpMembers");
                for (int j = 0; j < groupMemberNumJsonArray.length(); j++) {
                    groupName = newJsonObject.getString("noteGroupName");
                    groupAdminMobNo = newJsonObject.getString("noteGroupAdminNo");
                    groupMemberMobNo = groupMemberNumJsonArray.getString(j);
                    boolean isAccepted = false;
                    if (groupMemberMobNo.equals(groupAdminMobNo)) {
                        isAccepted = true;
                    }
                    mydb.insertGroups(groupName, groupAdminMobNo, groupMemberMobNo, Boolean.toString(isAccepted));
                }
            }
            // Prepare intent which is triggered if the
            // notification is selected
            Intent intent = new Intent(context, Chat_Main_Fragment.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Build notification
            // Actions are just fake
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_splash_256)
                    .setContentTitle(notificationMessage).setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            manager.notify(0, mBuilder.build());
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}

//*************************************************************************************

//this is a main class of MyTrail activity where we plot the map and show the location...

public class MyTrail extends ActionBarActivity implements LocationListener, com.google.android.gms.location.LocationListener {

    GoogleMap map;
    double latitude, longitude;
    double altitude;
    float speed;
    String deviceId;
    String userNumber;
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
    FetchContacts contacts;

    Location final_location, gps_location, network_location;

    private int count = 0; //counter to ensure onetimecamerasettouserloc function executes once on location change

    private static final int INTERVAL = 5000;

    //this is a socket connection
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://104.236.27.79:8080");
        } catch (URISyntaxException e) {
        }
    }

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
        //get user mobile number from shared preference.......
        SharedPreferences prefss = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        final String userMobileNumber = prefss.getString("Mobile Number", null);
        //make connection to socket.....
        mSocket.connect();
        //mSocket.emit("Tweet", message);

        Emitter.Listener onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                MyTrail.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String username;
                        String message;
                        try {
                            username = data.getString("message");
                            //message = data.getString("message");
                        } catch (JSONException e) {
                            return;
                        }
                        Log.i("Socket On...", "" + username);
                    }
                });

            }
        };

        mSocket.on("whoru", onNewMessage);
        JSONObject testMessage = new JSONObject();
        try {
            testMessage.put("mobileNum", userMobileNumber);
            mSocket.emit("iam", testMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //below code is for testing purpose to check notification.........
        Emitter.Listener onNotification = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                MyTrail.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        boolean status;
                        String message;
                        try {
                            status = data.getBoolean("notification");
                            if (status == true) {
                                Log.i("status is true..", "...." + status);
                                new FetchNotificationAsyncTask(getApplicationContext(), userMobileNumber).execute();
                            }
                        } catch (JSONException e) {
                            return;
                        }
                        Log.i("Socket On...", "" + status);
                    }
                });

            }
        };

        mSocket.on("isnotification", onNotification);

        //check notification code ends here.......

        /* Retrieve a PendingIntent that will perform a broadcast *//*
        Intent alarmIntent = new Intent(MyTrail.this, ChatReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyTrail.this, 0, alarmIntent, 0);


        //create AlarmManager to hit request on server to get chat messages.....
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL, pendingIntent);

*/
        btnGroup = (Button) findViewById(R.id.btnGroup);
        //this method is used to get countryCode isung telephonyManager....
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        countryCode = telephonyManager.getNetworkCountryIso().toUpperCase();
        /*call NumberSyncFromServer class to sync all contact automatically from server....
        first we fetch all contacts from mobile device and then we execute Async Class.....
        first we check that app install first time on device or not...
        get value from shared preference.......*/
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        isContactSyncFromServer = prefs.getBoolean("isContactSyncFromServer", false);
        userNumber = prefs.getString("Mobile Number", null);
        contacts = new FetchContacts();
        if (!isContactSyncFromServer) {
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("MyPrefs", getApplicationContext().MODE_PRIVATE).edit();
            editor.putBoolean("isContactSyncFromServer", true);
            editor.commit();
            contacts = fetchContact();
            Log.i("size of contacts1..", "" + contacts.getNumber().size());
            Log.i("size of contacts2..", "" + contacts.getName().size());
            try {
                //save all contacts to loacal app db....
                mydb = new SQLiteDatabase(this);
                //mydb.insertIntoInvite(contacts);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //After Successfully fetching all contact mobNum, call AsyncTask class to send this contact to server.......
            new NumberSyncFromServer(getApplicationContext(), contacts).execute();
        }
        //check if gps is available or not.....
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        try {
            //getting GPS Status....
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.i("GPS Status...", "" + isGPSEnabled);

            //getting Network Status....
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.i("Network Status...", "" + isNetworkEnabled);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!isGPSEnabled && !isNetworkEnabled) {
            //if gps and network both will disable.....then show settings to open gps.....
            showGPSSettings();
        } else {
            Log.i("GPS Status", "GPS is already activated!!!!");
        }
        //if gps enabled.....
        if (isGPSEnabled) {
            gps_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (gps_location != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, this);
            }
        }
        //if Network enabled.....
        if (isNetworkEnabled) {
            network_location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (network_location != null) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 10, this);
            }
        }
        if (gps_location != null && network_location != null) {
            if (gps_location.getAccuracy() > network_location.getAccuracy()) {
                final_location = gps_location;
            } else {
                final_location = network_location;
            }
        } else {
            if (gps_location != null) {
                final_location = gps_location;
            } else {
                final_location = network_location;
            }
        }

        if (final_location != null) {
            latitude = final_location.getLatitude();
            longitude = final_location.getLongitude();
            altitude = final_location.getAltitude();
            speed = final_location.getSpeed();
        }

        //Intent to recieve data from previous activity class
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        deviceId = bundle.getString("Device_Id", deviceId);
        Toast.makeText(getApplicationContext(), deviceId, Toast.LENGTH_SHORT).show();
        try {

            //call AsyncTask class to push location on server.....
            new LocationAsyncTask(getApplicationContext(), userNumber, latitude, longitude, altitude, speed).execute();

            initilizeMap();
            new DrawMarkerAsyncTask(getApplicationContext(), latitude, longitude);


        } catch (Exception ex) {
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
        Log.i("LatLog", "latlog is....." + latitude);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();


        mSocket.connect();
        //mSocket.emit("Tweet", message);

        Emitter.Listener onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                MyTrail.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String username;
                        String message;
                        try {
                            username = data.getString("qcmessage");
                            //message = data.getString("message");
                        } catch (JSONException e) {
                            return;
                        }
                        Log.i("Socket On...", "" + username);
                    }
                });

            }
        };

        mSocket.on("hello-there", onNewMessage);
        JSONObject testMessage = new JSONObject();
        try {
            testMessage.put("tweetdata", "Message from tweetloc client app..");
            mSocket.emit("tweet-hello", testMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        speed = location.getSpeed();

        if (count == 0) {
            oneTimeCameraSetToUserLoc(location);
            count = 1;
        }
        //call AsyncTask class to push location on server when location changes.....
        new LocationAsyncTask(getApplicationContext(), userNumber, latitude, longitude, altitude, speed).execute();

        map.clear();
        new DrawMarkerAsyncTask(getApplicationContext(), latitude, longitude);

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
                Toast.makeText(getApplicationContext(), "This app requires GPS for precize location !!!", Toast.LENGTH_SHORT).show();
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
        if (id == R.id.action_contactSync) {
            //first fetch all contact mobNum from device.......
            contacts = fetchContact();

            try {
                //save all contacts to loacal app db....
                mydb = new SQLiteDatabase(this);
                //mydb.insertIntoInvite(contacts);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //After Successfully fetching all contact mobNum, call AsyncTask class to send this contact to server.......
            new NumberSyncFromServer(getApplicationContext(), contacts).execute();
            return true;
        }
        if (id == R.id.action_groupSync) {
            new GroupDetailsAsyncTask(getApplicationContext(), userNumber).execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void forgetMe() {
        // mydb.deleteInfo(new RegistrationInfo(deviceId));
        new ForgetAsyncTask(getApplicationContext(), deviceId).execute();
    }

    //this method is used for inviting contacts through social media android app....
    public void inviteContacts() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "here is the link to download TweetLoc");
        startActivity(intent);
    }

    private FetchContacts fetchContact() {
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
            String standardNumber = standardMobileNumberFormat.getLocale(number, countryCode);
            Log.i("standard number...", "" + standardNumber);
            if (standardNumber != null) {
                nameList.add(name);
                numberList.add(standardNumber);
                Log.i("Inside sync...", "contacts" + name + "-->" + standardNumber);
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
class FetchContacts {
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

//this class is used to redraw marker into map.......
class DrawMarkerAsyncTask extends AsyncTask<Void, Void, String> {

    double latitude, longitude;
    MarkerOptions marker;
    GoogleMap map;
    private Context context;

    public DrawMarkerAsyncTask(Context context, double latitude, double longitude) {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i("LatLog", "latlog is....." + latitude);
        marker = new MarkerOptions().position(new LatLng(latitude, longitude));
        map.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}