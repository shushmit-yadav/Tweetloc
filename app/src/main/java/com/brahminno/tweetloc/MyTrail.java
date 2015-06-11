package com.brahminno.tweetloc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.LocationBean;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

//this is a AsyncTask class that push location to server in background.....
class LocationAsyncTask extends AsyncTask<Void, Void, String> {
    private static TweetApi myTweetApi = null;
    private Context context;
    private String Device_Id;
    private double latitude;
    private double longitude;

    public LocationAsyncTask(Context context, String Device_Id, double latitude, double longitude) {
        context = null;
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

//*************************************************************************************

//this is a main class of MyTrail activity where we plot the map and show the location...

public class MyTrail extends ActionBarActivity implements LocationListener {

    GoogleMap map;
    double latitude, longitude;
    String deviceId;
    Button btnGroup;
    LocationManager locationManager;
    String provider;
    SQLiteDatabase mydb;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;

    MarkerOptions marker;

    Location location;

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
        //check if gps is available or not.....
        LocationManager manager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        try{
            //getting GPS Status....
            isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.i("GPS Status...", "" + isGPSEnabled);

            //getting Network Status....
            isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.i("Network Status...", "" + isNetworkEnabled);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        if (!isGPSEnabled && !isNetworkEnabled) {
            //if gps and network both will disable.....then show settings to open gps.....
            showGPSSettings();
        }

        locationManager = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
        //set Criteria....
        Criteria criteria = new Criteria();
        //get best provider....
        provider = locationManager.getBestProvider(criteria,true);
        //getting location status.....
        location = locationManager.getLastKnownLocation(provider);
        //if location is not null....
        if(location != null){
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider,20000,0,this);
        //Intent to recieve data from previous activity class
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        deviceId = bundle.getString("Device_Id", deviceId);
        Toast.makeText(getApplicationContext(), deviceId, Toast.LENGTH_SHORT).show();
        try {

            //call AsyncTask class to push location on server.....
            new LocationAsyncTask(getApplicationContext(), deviceId, latitude, longitude).execute();

            initilizeMap();


        } catch (Exception ex) {
            ex.printStackTrace();
        }


        //When Group Button clicks then this method will execute...
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                //Bundle bundle = new Bundle();
                //bundle.putString("Device_Id",deviceId);
                //intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private void initilizeMap() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.mapFragment)).getMap();
        }
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

        LatLng latLng = new LatLng(latitude, longitude);
        //add marker.....
        //marker = new MarkerOptions().position(new LatLng(latitude, longitude));


        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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
}
