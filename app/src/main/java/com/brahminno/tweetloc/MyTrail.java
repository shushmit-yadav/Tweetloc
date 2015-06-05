package com.brahminno.tweetloc;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.LocationBean;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;
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

    public LocationAsyncTask(Context contect, String Device_Id, double latitude, double longitude) {
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
        }
        catch (IOException ex) {
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
//*************************************************************************************

//this is a main class of MyTrail activity where we plot the map and show the location...

public class MyTrail extends ActionBarActivity implements LocationListener{

    GoogleMap map;
    Marker marker = null;
    double latitude, longitude;
    String deviceId;
    Button btnGroup;
    LocationManager locationManager;
    Context context;
    SQLiteDatabase mydb;
    private static final int CHANGE_LOCATION_TIME_SECONDS = 2000;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    Location final_location = null,gps_loaction = null,network_location = null;

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
        locationManager = (LocationManager) getApplicationContext().getSystemService(context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            showGPSSettings();
        }
        else {
            getLocation();
        }
        //Intent to recieve data from previous activity class
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        deviceId = bundle.getString("Device_Id", deviceId);
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

    public void getLocation(){

        try{
            //getting GPS Status....
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //getting Network Status....
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(isGPSEnabled){
                if(locationManager != null) {
                    gps_loaction = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(gps_loaction != null){
                        latitude = gps_loaction.getLatitude();
                        longitude = gps_loaction.getLongitude();
                    }
                }
            }
            if(isNetworkEnabled){
                if(locationManager != null){
                    network_location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(network_location != null){
                        latitude = network_location.getLatitude();
                        longitude = network_location.getLongitude();
                    }
                }
            }
            if(gps_loaction != null && network_location != null){
                if(gps_loaction.getAccuracy() >= network_location.getAccuracy()){
                    final_location = gps_loaction;
                }
                else {
                    final_location = network_location;
                }
            }
            else{
                if(gps_loaction != null){
                    final_location = network_location;
                }
                else if(network_location != null){
                    final_location = gps_loaction;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initilizeMap() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.mapFragment)).getMap();
        }
        //MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude));
        //map.addMarker(marker);

        map.setMyLocationEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(10).build();


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

        LatLng latLng = new LatLng(latitude,longitude);

        //call AsyncTask class to push location on server when location changes.....
        new LocationAsyncTask(getApplicationContext(), deviceId, latitude, longitude).execute();

        map.clear();

    }

    public void cameraSetToCurrentLocation(Location location){
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude,longitude);
        marker = map.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
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

    public void showGPSSettings(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS is not available.");
        builder.setMessage("Do you want to activate GPS?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Launch settings, allowing user to make a change
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                startActivityForResult(i,0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //No location service, no Activity
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                getLocation();
            }
            else if(resultCode == RESULT_CANCELED){
                getLocation();
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
        if(id == R.id.action_settings){
            Intent intent = new Intent(getApplicationContext(),User_ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_forget_me){
            //forgetMe();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //public void forgetMe(){
      //  mydb.deleteInfo(new RegistrationInfo(deviceId));
    //}
}
