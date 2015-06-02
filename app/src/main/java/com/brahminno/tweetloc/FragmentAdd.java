package com.brahminno.tweetloc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

/**
 * Created by Shushmit on 28-05-2015.
 */
public class FragmentAdd extends Fragment {

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_add,container,false);
        return rootView;
    }
}
