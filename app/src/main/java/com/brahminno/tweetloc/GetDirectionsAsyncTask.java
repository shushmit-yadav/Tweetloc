package com.brahminno.tweetloc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Shushmit Yadav on 22-07-2015.
 */
public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList<LatLng>>{
    public static final String USER_CURRENT_LAT = "user_current_lat";
    public static final String USER_CURRENT_LONG = "user_current_long";
    public static final String DESTINATION_LAT = "destination_lat";
    public static final String DESTINATION_LONG = "destination_long";
    public static final String DIRECTIONS_MODE = "directions_mode";
    private Chat_Main_Fragment activity1;
    private Exception exception;
    private ProgressDialog progressDialog;

    public GetDirectionsAsyncTask(Chat_Main_Fragment activity)
    {
        super();
        this.activity1 = activity;
        progressDialog = new ProgressDialog(activity1.getActivity());
    }

    public void onPreExecute()
    {
        progressDialog.setMessage("Calculating directions");
        progressDialog.show();
    }

    @Override
    protected ArrayList<LatLng> doInBackground(Map<String, String>... params) {
        Map<String, String> paramMap = params[0];
        try
        {
            LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
            LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
            GMapV2Direction md = new GMapV2Direction();
            Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
            ArrayList<LatLng> directionPoints = md.getDirection(doc);
            return directionPoints;
        }
        catch (Exception e)
        {
            exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> result) {
        progressDialog.dismiss();
        if (exception == null)
        {
            activity1.handleGetDirectionsResult(result);
        }
        else
        {
            processException();
        }
    }

    private void processException()
    {
        Toast.makeText(activity1.getActivity(), "Error retriving data", Toast.LENGTH_SHORT).show();
    }
}
