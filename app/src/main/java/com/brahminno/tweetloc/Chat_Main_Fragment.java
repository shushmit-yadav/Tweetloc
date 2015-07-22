package com.brahminno.tweetloc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.brahminno.tweetloc.asyncClass.ChatMessageSendAsyncTask;
import com.brahminno.tweetloc.asyncClass.GroupMemberLocationAsync;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Shushmit on 29-06-2015.
 * Brahmastra Innovations Pvt. Ltd.
 * This class is used for SlidingUpPanel google map with listview and chat .....
 */
public class Chat_Main_Fragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SlidingUpPanelLayout.PanelSlideListener, LocationListener {

    private static Marker marker;
    //private static GoogleMap map;
    private ListView mListView;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private View mTransparentHeaderView;
    private LatLng mLocation;
    private Marker mLocationMarker;
    private LocationManager mLocationManager;
    private Location gpsLocation, networkLocation, finalLocation;
    private Double altitude;
    private Double latitude, longitude;
    private long timeStamp;
    private int speed;
    private String senderMobileNumber;
    private String adminMobileNumber;
    private String groupName;
    private JSONObject groupMemberMobileNumber;
    private JSONArray groupMemberJsonArray;
    private SupportMapFragment mMapFragment;

    private static GoogleMap mMap;
    private boolean mIsNeedLocationUpdate = true;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    EditText etMessage;
    Button btnSendChat;
    static SQLiteDatabase mydb;
    static LatLng groupLocation;

    //private MessagesCustomListAdapter adapter;
    //private List<Message> listMessages;

    public Chat_Main_Fragment() {

    }


    public Chat_Main_Fragment(JSONObject groupMemberMobileNumber, String adminMobileNumber, String groupName) {
        this.groupMemberMobileNumber = groupMemberMobileNumber;
        this.adminMobileNumber = adminMobileNumber;
        this.groupName = groupName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_fragment_main, container, false);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        mListView = (ListView) rootView.findViewById(android.R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // init header view for ListView
        mTransparentHeaderView = inflater.inflate(R.layout.transparent_header, mListView, false);

        //init EditText and Button.....
        etMessage = (EditText) rootView.findViewById(R.id.etMessage);
        btnSendChat = (Button) rootView.findViewById(R.id.btnSendChat);

        collapseMap();

        mSlidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSlidingUpPanelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSlidingUpPanelLayout.onPanelDragged(0);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent intentAlarm = new Intent(getActivity(), MyAlarmManager.class);
        String jsonGroupMemberArrayString = groupMemberMobileNumber.toString();
        Log.i("jsonGroupMemberArrayString", "---->" + jsonGroupMemberArrayString);
        Bundle bundle = new Bundle();
        bundle.putString("GroupMemberList", jsonGroupMemberArrayString);
        intentAlarm.putExtras(bundle);
        // create the object
        alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //set the alarm for particular time
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 20, pendingIntent);
        //...............................................
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        senderMobileNumber = prefs.getString("Mobile Number", null);
        Log.i("user mobile", " number : " + senderMobileNumber);
        mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (gpsLocation != null && networkLocation != null) {
            if (gpsLocation.getAccuracy() > networkLocation.getAccuracy()) {
                finalLocation = gpsLocation;
            } else {
                finalLocation = networkLocation;
            }
        } else {
            if (gpsLocation == null) {
                finalLocation = networkLocation;
            } else {
                finalLocation = gpsLocation;
            }
        }
        mLocation = new LatLng(finalLocation.getLatitude(), finalLocation.getLongitude());
        Log.i("location in ", " chat_main_fragment class : " + finalLocation.getAltitude());
        Log.i("grouplist in ", " chat_main_fragment class : " + groupMemberMobileNumber);
        Log.i("userMobileNumber...", " in chat_main_fragment: " + senderMobileNumber);
        if (mLocation == null) {
            mLocation = getLastKnownLocation(false);
        }
        latitude = mLocation.latitude;
        longitude = mLocation.longitude;
        altitude = finalLocation.getAltitude();
        speed = (int) finalLocation.getSpeed();
        timeStamp = finalLocation.getTime();
        try {
            groupMemberJsonArray = groupMemberMobileNumber.getJSONArray("userNumbers");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GroupMemberLocationAsync(getActivity(), latitude, longitude, altitude, speed, timeStamp, senderMobileNumber, groupMemberJsonArray).execute();

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
        fragmentTransaction.commit();

        //mListView.addHeaderView(mTransparentHeaderView);
       /* listMessages = new ArrayList<Message>();
        adapter = new MessagesCustomListAdapter(getActivity(),listMessages);
        mListView.setAdapter(adapter);*/
        ArrayList<String> testData = new ArrayList<String>(100);
        for (int i = 0; i < 100; i++) {
            testData.add("Item " + i);
        }
        mListView.addHeaderView(mTransparentHeaderView);
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, testData));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSlidingUpPanelLayout.collapsePane();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setUpMapIfNeeded();

        btnSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                String chatMessage = etMessage.getText().toString();
                etMessage.setText("");
                new ChatMessageSendAsyncTask(getActivity(), senderMobileNumber, groupName, adminMobileNumber, groupMemberJsonArray, chatMessage, time).execute();
            }
        });
    }

    //in this method get groupmembers location from AsyncTask.......
    public static void getGroupMemberLocationResponse(Context context, JSONArray jsonArray) {
        Log.i("inside....", "Chat_Main_Fragment......" + jsonArray);
        mydb = new SQLiteDatabase(context);
        for (int i = 0; i < jsonArray.length(); i++)
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String groupUserMobNo = jsonObject.getString("userNumber");
                double latitude = jsonObject.getDouble("latitude");
                double longitude = jsonObject.getDouble("longitude");
                Log.i("Latlog--->", " " + latitude + " " + longitude);
                //mydb.insertGroupMembersLocation(groupUserMobNo,Double.toString(latitude),Double.toString(longitude));
                marker = mMap.addMarker(new MarkerOptions().title(groupUserMobNo).position(new LatLng(latitude,longitude)));
                Toast.makeText(context, "LatLog...." + latitude + "" + longitude, Toast.LENGTH_SHORT).show();
                /*MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(latitude, longitude));*/
                groupLocation = new LatLng(latitude, longitude);
                //map.addMarker(groupLocation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mMapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                LatLng update = getLastKnownLocation();
                if (update != null) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 11.0f)));
                }
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity().getLayoutInflater()));
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // In case Google Play services has since become available.
        setUpMapIfNeeded();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private LatLng getLastKnownLocation() {
        return getLastKnownLocation(true);
    }

    private LatLng getLastKnownLocation(boolean isMoveMarker) {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        String provider = lm.getBestProvider(criteria, true);
        if (provider == null) {
            return null;
        }
        Location loc = lm.getLastKnownLocation(provider);
        if (loc != null) {
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            if (isMoveMarker) {
                moveMarker(latLng);
            }
            return latLng;
        }
        return null;
    }

    private void moveMarker(LatLng latLng) {
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
        mLocationMarker = mMap.addMarker(new MarkerOptions().title("My Location").position(latLng).anchor(0.5f, 0.5f));
    }

    private void moveToLocation(Location location) {
        if (location == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveToLocation(latLng);
    }

    private void moveToLocation(LatLng latLng) {
        moveToLocation(latLng, true);
    }

    private void moveToLocation(LatLng latLng, final boolean moveCamera) {
        if (latLng == null) {
            return;
        }
        moveMarker(latLng);
        mLocation = latLng;
    }

    private void collapseMap() {
        if (mMap != null && mLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 11f), 1000, null);
        }
    }

    private void expandMap() {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
        }
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mIsNeedLocationUpdate) {
            moveToLocation(location);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}