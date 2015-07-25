package com.brahminno.tweetloc;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.brahminno.tweetloc.chat_reciever_alarmManager.ChatReceiverAlarmManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;

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

import static android.app.PendingIntent.getActivity;

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
    private String responseResult;
    SQLiteDatabase mydb;
    MyTrail myTrail;

    public ForgetAsyncTask(Context context, String Device_Id) {
        this.context = context;
        this.Device_Id = Device_Id;
    }

    @Override
    protected String doInBackground(Void... params) {
        mydb = new SQLiteDatabase(context);
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
            responseResult = EntityUtils.toString(httpResponse.getEntity());
            Log.i("responseResult...: ", responseResult);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i("inside onPostExecute...", ".....");
        try {
            JSONObject jsonResult = new JSONObject(result);
            if(jsonResult.getBoolean("status")){
                //Clear all data from shared preference....
                SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                //delete Application Database......
                context.getApplicationContext().deleteDatabase("TweetLocDB.db");

                MyTrail.getForgetMeResponse(jsonResult.getBoolean("status"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            if (responseJsonArray.length() > 0) {
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
            } else {
                Log.i("Response result..", " No contacts is restoger from TweetLoc app...");
            }
            //call SQLiteDatabase method to store contacts in database.....
            //save arraylist to sqlite database......
            myDB = new SQLiteDatabase(context);
            //clear data from app db....
            myDB.deleteNumberArrayList();
            //Log.i("Contact from server...", contactName.get(0));
            myDB.insertNumberArrayList(contactNumber, contactName);
            //first delete items from Invite_Contacts_Table and then store in.....
            myDB.deleteInviteTableItems();
            if (contactNumber.size() > 0) {
                for (int i = 0; i < contacts.getNumber().size(); i++) {
                    if (!contactNumber.contains(contacts.getNumber().get(i))) {
                        myDB.insertIntoInvite(contacts.getName().get(i), contacts.getNumber().get(i));
                    }
                }
            } else {
                for (int i = 0; i < contacts.getNumber().size(); i++) {
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
    private JSONArray groupMemberNumJsonArray;
    private String noteGroupName;
    private String noteGroupAdminNo;
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
            if(array.length() > 0){
                for (int i = 0; i < array.length(); i++) {
                    JSONObject newJsonObject = (JSONObject) array.get(i);
                    notificationMessage = newJsonObject.getString("noteMessage");
                    noteGroupName = newJsonObject.getString("noteGroupName");
                    noteContext = newJsonObject.getString("noteContext");
                    noteGroupAdminNo = newJsonObject.getString("noteGroupAdminNo");
                    groupMemberNumJsonArray = newJsonObject.getJSONArray("noteAuxilGrpMembers");
                    if(noteContext.equals("notifyGroupDeletion")){
                        mydb.deleteGroupFromGroupTable(noteGroupName,noteGroupAdminNo);
                    }
                    else if(noteContext.equals("notifyGroupCreation")){
                        for (int j = 0; j < groupMemberNumJsonArray.length(); j++) {
                            groupMemberMobNo = groupMemberNumJsonArray.getString(j);
                            boolean isAccepted = false;
                            if (groupMemberMobNo.equals(groupAdminMobNo)) {
                                isAccepted = true;
                            }
                            mydb.insertGroups(noteGroupName, noteGroupAdminNo, groupMemberMobNo, Boolean.toString(isAccepted));
                        }
                    }
                }
                // Prepare intent which is triggered if the
                // notification is selected
                //Intent intent = new Intent(context, Chat_Main_Fragment.class);
                PendingIntent pendingIntent = getActivity(context, 0, null, PendingIntent.FLAG_UPDATE_CURRENT);

                // Build notification
                // Actions are just fake
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_splash_256)
                        .setContentTitle(notificationMessage).setContentIntent(pendingIntent);

                NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                manager.notify(0, mBuilder.build());
            }else{
                Log.i("Notification...","there is not any notification...");
            }
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
    private String userNumber;
    Button btnGroup;
    LocationManager locationManager;
    SQLiteDatabase mydb;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    StandardMobileNumberFormat standardMobileNumberFormat;
    MarkerOptions marker;
    TelephonyManager telephonyManager;
    String countryCode;
    FetchContacts contacts;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    AlarmManager chatAlarmManager;
    PendingIntent getChatPendingIntent;
    Location final_location, gps_location, network_location;

    private int count = 0; //counter to ensure onetimecamerasettouserloc function executes once on location change

    private static final int INTERVAL = 5000;

    static Activity activity = null;

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
                                /* Retrieve a PendingIntent that will perform a broadcast */
                                Intent intentAlarm = new Intent(getApplicationContext(), NotificationAlarmManager.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("User Mobile Number", userMobileNumber);
                                intentAlarm.putExtras(bundle);
                                // create the object
                                alarmManager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
                                pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                //set the alarm for particular time
                                alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 60 * 2, pendingIntent);
                            }
                            else{
                                /* Retrieve a PendingIntent that will perform a broadcast */
                                Intent intentAlarm = new Intent(getApplicationContext(), NotificationAlarmManager.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("User Mobile Number", userMobileNumber);
                                intentAlarm.putExtras(bundle);
                                // create the object
                                alarmManager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
                                pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                //set the alarm for particular time
                                alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 60 * 2, pendingIntent);

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

        // Create Alarm manager that hit on server to get chats......
        Intent intentChatAlarm = new Intent(getApplicationContext(), ChatReceiverAlarmManager.class);
        Bundle chatBundle = new Bundle();
        chatBundle.putString("User Mobile Number", userMobileNumber);
        intentChatAlarm.putExtras(chatBundle);
        chatAlarmManager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
        getChatPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentChatAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //set the alarm for particular time
        chatAlarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 10, getChatPendingIntent);
        //ends of getChatAlarmManager code........

        btnGroup = (Button) findViewById(R.id.btnGroup);
        //this method is used to get countryCode isung telephonyManager....
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        countryCode = telephonyManager.getNetworkCountryIso().toUpperCase();
        /*call NumberSyncFromServer class to sync all contact automatically from server....
        first we fetch all contacts from mobile device and then we execute Async Class.....
        first we check that app install first time on device or not...
        get value from shared preference.......*/
        try {
            //save all contacts to loacal app db....
            mydb = new SQLiteDatabase(this);
            //mydb.insertIntoInvite(contacts);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //this line is used for closing application from static method....
        activity = this;

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("isFirstTime",false);
        if(!isFirstTime){
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isFirstTime",false);
            editor.commit();

            contacts = new FetchContacts();
            contacts = fetchContact();
            Log.i("size of contacts1..", "" + contacts.getNumber().size());
            Log.i("size of contacts2..", "" + contacts.getName().size());
            //After Successfully fetching all contact mobNum, call AsyncTask class to send this contact to server.......
            new NumberSyncFromServer(getApplicationContext(), contacts).execute();
        }

        //this method is used for getting gps location......
        getGPSLocation();
        //Intent to recieve data from previous activity class
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        deviceId = bundle.getString("Device_Id", deviceId);
        userNumber = bundle.getString("User_Mob_No", userNumber);
        Toast.makeText(getApplicationContext(), deviceId, Toast.LENGTH_SHORT).show();
        //When Group Button clicks then this method will execute...
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                startActivity(intent);
            }
        });
        try {
            //call AsyncTask class to push location on server.....
            new LocationAsyncTask(getApplicationContext(), userNumber, latitude, longitude, altitude, speed).execute();
            initilizeMap();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //this method is used for getting GPS or NETWORK location....if GPS is not active then show gpsLOcation settings.....
    private void getGPSLocation() {
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
            //call method to draw marker on map.....
            drawMarkerOnMap(latitude, longitude);
        }
    }

    //this method is used for drawing marker on map........
    private void drawMarkerOnMap(double latitude, double longitude) {
        Log.i("LatLog", "latlog is....." + latitude);
        try {
            marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Location");
            map.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(15).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
    public void onBackPressed() {
        try{
            chatAlarmManager.cancel(getChatPendingIntent);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        super.onBackPressed();
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
        drawMarkerOnMap(latitude, longitude);

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
                startActivityForResult(i, 0);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                getGPSLocation();
            }
            if (resultCode == RESULT_CANCELED) {
                getGPSLocation();
            }
        }
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
            new NumberSyncFromServer(getApplicationContext(), contacts).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return true;
        }
        if (id == R.id.action_groupSync) {
            Log.i("action_groupSync", " clicked---> " + userNumber);
            new GroupDetailsAsyncTask(getApplicationContext(), userNumber).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void forgetMe() {
        // mydb.deleteInfo(new RegistrationInfo(deviceId));
        new ForgetAsyncTask(getApplicationContext(), deviceId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    public static void getForgetMeResponse(boolean result){
        Log.i("Inside ForgetMe.."," response..."+ result);
        activity.finish();
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