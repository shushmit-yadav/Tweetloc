package com.brahminno.tweetloc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Shushmit on 29-06-2015.
 * Brahmastra Innovations Pvt. Ltd.
 * This class is used for SlidingUpPanel google map with listview and chat .....
 */
public class Chat_Main_Fragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SlidingUpPanelLayout.PanelSlideListener, LocationListener {

    private static Marker marker;
    private ListView mListView;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private View mTransparentHeaderView;
    private LatLng mLocation;
    private LatLng destinationLatLng;
    private  LatLng sourceLatLng;
    private Marker mLocationMarker;
    private LocationManager mLocationManager;
    private Location gpsLocation, networkLocation, finalLocation;
    private Double altitude;
    private Double latitude, longitude;
    //these variables are used when user press back button and exit to group, then these values will save to app database as lastLocation...
    private static ArrayList<Double> groupMembersLatitude;
    private static ArrayList<Double> groupMembersLongitude;
    private static ArrayList<String> groupMemberMobNo;
    private static ArrayList<Double> groupMemberAltitude;
    private static ArrayList<Integer> groupMemberSpeed;
    private static ArrayList<Long> groupMemberLastUpdate;
    //---------------------------------------------
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
    private double sourceLatitude, sourceLongitude;
    private double destinationLatitude, destinationLongitude;

    private boolean isTraveling = false;
    private int width, height;
    private LatLngBounds latlngBounds;
    private Polyline newPolyline;

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
        //mListView.setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        //init header view for ListView
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
        //setHasOptionsMenu(true);
        return rootView;
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setHasOptionsMenu(true);
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
        if (finalLocation.hasAltitude()) {
            altitude = finalLocation.getAltitude();
        } else {
            Toast.makeText(getActivity(), "User has not altitude", Toast.LENGTH_SHORT).show();
        }
        if (finalLocation.hasSpeed()) {
            speed = (int) finalLocation.getSpeed();
        } else {
            Toast.makeText(getActivity(), "User is not moving", Toast.LENGTH_SHORT).show();
        }

        timeStamp = finalLocation.getTime();
        Log.i("User Current Location", " Latitude-->" + latitude + " Longitude-->" + longitude + " Altitude-->" + altitude + " Speed-->" + speed + "TimeStamp-->" + timeStamp);
        try {
            groupMemberJsonArray = groupMemberMobileNumber.getJSONArray("userNumbers");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sourceLatitude = mLocation.latitude;
        sourceLongitude = mLocation.longitude;
        Log.i("Source Location is", "----->" + sourceLatitude + " and " + sourceLongitude);
        //new GroupMemberLocationAsync(getActivity(), latitude, longitude, altitude, speed, timeStamp, senderMobileNumber, groupMemberJsonArray).execute();

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
       /* mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSlidingUpPanelLayout.collapsePane();
            }
        });*/

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        getSreenDimanstions();
        setUpMapIfNeeded();

        btnSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                String chatMessage = etMessage.getText().toString();
                etMessage.setText("");
                //new ChatMessageSendAsyncTask(getActivity(), senderMobileNumber, groupName, adminMobileNumber, groupMemberJsonArray, chatMessage, time).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    //in this method get groupmembers location from AsyncTask.......
    public static void getGroupMemberLocationResponse(Context context, JSONArray jsonArray) {
        Log.i("inside....", "Chat_Main_Fragment......" + jsonArray);
        groupMemberMobNo = new ArrayList<>();
        groupMembersLatitude = new ArrayList<>();
        groupMembersLongitude = new ArrayList<>();
        groupMemberAltitude = new ArrayList<>();
        groupMemberSpeed = new ArrayList<>();
        groupMemberLastUpdate = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                groupMemberMobNo.add(jsonObject.getString("userNumber"));
                groupMembersLatitude.add(jsonObject.getDouble("latitude"));
                groupMembersLongitude.add(jsonObject.getDouble("longitude"));
                groupMemberAltitude.add(jsonObject.getDouble("altitude"));
                groupMemberSpeed.add(jsonObject.getInt("speed"));
                groupMemberLastUpdate.add(jsonObject.getLong("lastUpdated"));
                Log.i("Latlog--->", " " + groupMembersLatitude.get(i) + " " + groupMembersLongitude.get(i)+" "+ groupMemberSpeed.get(i)+" "+ groupMemberAltitude.get(i) +" "+groupMemberLastUpdate.get(i)+" "+ groupMemberSpeed.get(i));
                marker = mMap.addMarker(new MarkerOptions().title(groupMemberMobNo.get(i)).position(new LatLng(groupMembersLatitude.get(i), groupMembersLongitude.get(i))));
                Toast.makeText(context, "LatLog...." + groupMembersLatitude.get(i) + "" + groupMembersLongitude.get(i), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                mMap.isBuildingsEnabled();
                mMap.isTrafficEnabled();
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                LatLng update = getLastKnownLocation();
                if (update != null) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 11.0f)));
                }
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        destinationLatLng = marker.getPosition();
                        destinationLatitude = destinationLatLng.latitude;
                        destinationLongitude = destinationLatLng.longitude;
                        Log.i("destinationLatLng...","destinationLatitude"+destinationLatitude+"destinationLongitude"+destinationLongitude);
                        sourceLatLng = new LatLng(sourceLatitude, sourceLongitude);
                        Log.i("sourceLatLng..","sourceLatitude"+sourceLatitude+" sourceLongitude "+ sourceLongitude);
                        //double altitude = getElevatioFromGoogleMap(destinationLatitude,destinationLongitude);
                        String userMobNumber = marker.getTitle();
                        Log.i("userMobNumber on ","marker click is " + userMobNumber);
                        double distance = calculateDistance(sourceLatitude, sourceLongitude,destinationLatitude,destinationLongitude);
                        for(int i = 0; i < groupMemberMobNo.size(); i++){
                            if(userMobNumber.equals(groupMemberMobNo.get(i))){
                                Log.i("inside if loop...."," value is true....");
                                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity().getLayoutInflater(),groupMemberAltitude.get(i),groupMemberSpeed.get(i),groupMemberLastUpdate.get(i),distance));
                            }
                        }
                        if (!isTraveling) {
                            isTraveling = true;
                            findDirections(sourceLatLng.latitude, sourceLatLng.longitude, destinationLatLng.latitude, destinationLatLng.longitude, GMapV2Direction.MODE_DRIVING);
                        } else {
                            isTraveling = false;
                            findDirections(sourceLatLng.latitude, sourceLatLng.longitude, destinationLatLng.latitude, destinationLatLng.longitude, GMapV2Direction.MODE_DRIVING);
                        }
                        return false;
                    }
                });
            }
        }
    }

    //this methos is used to calculate distance......
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }
    //This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    //This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private double getElevatioFromGoogleMap(double latitude,double longitude){
        int cTimeOutMs = 30 * 1000;
        double elevation = 0;
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/elevation/json?locations=" +
                    String.valueOf(latitude) + "," +
                    String.valueOf(longitude));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(cTimeOutMs);
            urlConnection.setReadTimeout(cTimeOutMs);
            urlConnection.setRequestProperty("Accept", "application/json");

            // Set request type
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setDoInput(true);

            try {
                // Check for errors
                int code = urlConnection.getResponseCode();
                if (code != HttpsURLConnection.HTTP_OK)
                    throw new IOException("HTTP error " + urlConnection.getResponseCode());

                // Get response
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null)
                    json.append(line);
                Log.d("response....", json.toString());

                // Decode result
                JSONObject jroot = new JSONObject(json.toString());
                String status = jroot.getString("status");
                if ("OK".equals(status)) {
                    JSONArray results = jroot.getJSONArray("results");
                    if (results.length() > 0) {
                        elevation = results.getJSONObject(0).getDouble("elevation");
                        Log.i("Altitude....", "Elevation " + elevation);
                    } else
                        throw new IOException("JSON no results");
                } else
                    throw new IOException("JSON status " + status);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return elevation;
    }

    @Override
    public void onResume() {
        super.onResume();
        // In case Google Play services has since become available.
        setUpMapIfNeeded();

        /*latlngBounds = createLatLngBoundsObject(sourceLatLng, destinationLatLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 150));*/
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
        Log.i("Cancel Alarm Manager", " onStop......");
        alarmManager.cancel(pendingIntent);
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
        mLocationMarker = mMap.addMarker(new MarkerOptions().title(senderMobileNumber).position(latLng).anchor(0.5f, 0.5f));
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

    public void handleGetDirectionsResult(ArrayList directionPoints) {
        PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.GREEN);

        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add((LatLng) directionPoints.get(i));
        }
        if (newPolyline != null)
        {
            newPolyline.remove();
        }
        newPolyline = mMap.addPolyline(rectLine);
        if (isTraveling)
        {
            latlngBounds = createLatLngBoundsObject(sourceLatLng, destinationLatLng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 150));
        }
        else
        {
            latlngBounds = createLatLngBoundsObject(sourceLatLng, destinationLatLng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 150));
        }

    }

    private LatLngBounds createLatLngBoundsObject(LatLng firstLocation, LatLng secondLocation) {
        if (firstLocation != null && secondLocation != null)
        {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(firstLocation).include(secondLocation);

            return builder.build();
        }
        return null;
    }

    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
    }

    private void getSreenDimanstions(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
    }
   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_group_chat,menu);
        return;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent intent = new Intent(getActivity(), User_ProfileActivity.class);
            startActivity(intent);
            getActivity().finish();
            return true;
        }
        if(id == R.id.action_addNewMember){
            Intent intent = new Intent(getActivity(),AddNewMemberActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("Group Name",groupName);
            startActivity(intent);
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}