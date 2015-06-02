package com.brahminno.tweetloc;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;

/**
 * Created by Shushmit on 01-06-2015.
 */
public class MyTabListener<T extends Fragment> implements ActionBar.TabListener {

    private Fragment fragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;

    public MyTabListener(Activity activity, String tag, Class<T> clz) {
        //this.fragment = fragment;
        mActivity = activity;
        mTag = tag;
        mClass = clz;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        //Log.i(TAG, "Tab " + tab.getText() + " selected");
        if(fragment == null){
            fragment = Fragment.instantiate(mActivity, mClass.getName());
            ft.add(android.R.id.content,fragment,mTag);
        }
        else{
            ft.attach(fragment);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        //Log.i(TAG, "Tab " + tab.getText() + " UnSelected");
        if (fragment != null) {
            ft.detach(fragment);
        }

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        //Log.i(TAG, "Tab " + tab.getText() + " ReSelected");
    }
}
