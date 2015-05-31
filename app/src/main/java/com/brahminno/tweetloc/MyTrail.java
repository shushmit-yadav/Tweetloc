package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.LocationBean;
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
    GPSTracker gps;
    Marker marker = null;
    double latitude, longitude;
    String deviceId;
    Button btnGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_hdr_mytrail_128);
        setContentView(R.layout.activity_my_trail);
        btnGroup = (Button) findViewById(R.id.btnGroup);

        //Check first that GPS is Activated or not.....
        gps = new GPSTracker(this);
        if (gps.isGPSTeackingEnabled()) {
            Toast.makeText(getApplicationContext(), String.valueOf(gps.latitude), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),String.valueOf(gps.longitude),Toast.LENGTH_SHORT).show();
            latitude = gps.latitude;
            longitude = gps.longitude;
        }
        else{
            gps.showSettingsAlert();
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

        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        LatLng latLng = new LatLng(latitude,longitude);

        //call AsyncTask class to push location on server when location changes.....
        new LocationAsyncTask(getApplicationContext(), deviceId, latitude, longitude).execute();

        map.clear();

    }

    public void cameraSetToCurrentLocation(Location location){
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        LatLng latLng = new LatLng(latitude,longitude);
        marker = map.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}
