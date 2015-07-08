package com.brahminno.tweetloc;

import android.content.Context;
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
    public CustomInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = inflater.inflate(R.layout.custom_info_window_for_chat_into_map,null);
        EditText etChatMessageFromMapMarker = (EditText) view.findViewById(R.id.etChatMessageFromMapMarker);
        Button btnSendChatMessageFromMapMarker = (Button) view.findViewById(R.id.btnSendChatMessageFromMapMarker);
        TextView tvDispalyMessageIntoMap = (TextView) view.findViewById(R.id.tvDispalyMessageIntoMap);
        return view;
    }
}
