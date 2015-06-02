package com.brahminno.tweetloc;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.util.Log;

public class AddMemberActivity extends ActionBarActivity {

    private static final String TAG = "Tab";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        setUpTabs(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //            save the selected tab's index so it's re-selected on orientation change
        outState.putInt("tabIndex", getSupportActionBar().getSelectedNavigationIndex());
    }

    private void setUpTabs(Bundle savedInstanceState) {
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(actionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayShowTitleEnabled(false);

            ActionBar.Tab tab_Add = actionBar.newTab();
            ActionBar.Tab tab_Invite = actionBar.newTab();

            //Fragment firstFragment = new Fragment();
            tab_Add.setText("Add")
                    .setContentDescription("The first tab")
                    .setTabListener(
                            new MyTabListener<FragmentAdd>(
                                    this, "Add", FragmentAdd.class));

            //Fragment secondFragment = new Fragment();
            tab_Invite.setText("Invite").setContentDescription("The second tab")
                    .setTabListener(
                            new MyTabListener<FragmentInvite>(
                                    this, "Invite", FragmentInvite.class));

            actionBar.addTab(tab_Add);
            actionBar.addTab(tab_Invite);

            if (savedInstanceState != null) {
                Log.i(TAG, "setting selected tab from saved bundle");
//            get the saved selected tab's index and set that tab as selected
                actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tabIndex", 0));

            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
