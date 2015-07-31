package com.brahminno.tweetloc;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Shushmit on 08-07-2015.
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    LayoutInflater inflater;
    Context context;
    private double altitude;
    private int speed;
    private long lastUpdate;
    private double distance;
    public CustomInfoWindowAdapter(LayoutInflater inflater,double altitude,int speed,long lastUpdate,double distance){
        this.inflater = inflater;
        this.altitude = altitude;
        this.speed = speed;
        this.lastUpdate = lastUpdate;
        this.distance = distance;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = inflater.inflate(R.layout.custom_info_window_for_chat_into_map,null);
        TextView tvSpeed = (TextView) view.findViewById(R.id.tvSpeed);
        TextView tvSpeedValue = (TextView) view.findViewById(R.id.tvSpeed);
        TextView tvAltitude = (TextView) view.findViewById(R.id.tvAltitude);
        TextView tvAltitudeValue = (TextView) view.findViewById(R.id.tvAltitudeValue);
        TextView tvLastUpdate = (TextView) view.findViewById(R.id.tvLastUpdatedTime);
        TextView tvLastUpdateValue = (TextView) view.findViewById(R.id.tvLastUpdateValue);
        TextView tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        TextView tvDistanceValue = (TextView) view.findViewById(R.id.tvDistanceValue);

        tvSpeedValue.setText(""+speed);
        Log.i("speed is ", " " + speed);
        tvAltitudeValue.setText("" + altitude);
        Log.i("altitude is ", " " + altitude);
        tvDistanceValue.setText("" + distance);
        Log.i("distance is ", " " + distance);
        tvLastUpdateValue.setText("" + lastUpdate);
        Log.i("lastUpdate is ", " " + lastUpdate);
        return view;
    }
}
