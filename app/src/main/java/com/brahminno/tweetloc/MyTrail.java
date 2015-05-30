package com.brahminno.tweetloc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MyTrail extends ActionBarActivity {

    GoogleMap map;
    GPSTracker gps;
    double latitude,longitude;
    Button btnGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_hdr_mytrail_128);
        setContentView(R.layout.activity_my_trail);
        btnGroup = (Button) findViewById(R.id.btnGroup);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        latitude = bundle.getDouble("Latitude");
        longitude = bundle.getDouble("Longitude");
        try {
            initilizeMap();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),GroupActivity.class);
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

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(10).build();
        map.setMyLocationEnabled(true);

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
}
