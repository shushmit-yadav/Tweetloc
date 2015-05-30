package com.brahminno.tweetloc;

import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

/**
 * Created by Shushmit on 28-05-2015.
 * Brahmastra Innovations
 */
public class TabListener implements ActionBar.TabListener {

    Fragment fragment;

    public TabListener(Fragment fragment){
        this.fragment = fragment;
    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        //ft.replace(R.id.fragment_container,fragment);

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        //ft.remove(fragment);

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
