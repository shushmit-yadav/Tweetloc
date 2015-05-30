package com.brahminno.tweetloc;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.brahminno.tweetloc.data.ContactItem;
import com.brahminno.tweetloc.tast.InviteTask;

import java.util.List;

public class AddMemberActivity extends ActionBarActivity {

    static final String TAG = AddMemberActivity.class.getName();
    ActionBar actionBar;
    TabListener tabListener;
    ActionBar.Tab Add;
    private FragmentInvite contactListFragment;
    private ActionBar.Tab Invite;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        tabListener = new TabListener(this);

        Add = actionBar.newTab().setText("Add").setTabListener(tabListener);
        actionBar.addTab(Add);

        Invite = actionBar.newTab().setText("Invite").setTabListener(tabListener);
        actionBar.addTab(Invite);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }
    public void addContactListFragment(FragmentTransaction ft,
                                       List<ContactItem> result) {
        contactListFragment = (FragmentInvite) getFragmentManager().
                findFragmentByTag("ContactList");
        if(contactListFragment == null){
            contactListFragment = new FragmentInvite();
        }
       // FragmentTransaction replace = ft.replace(R.id.fragment_container, contactListFragment);
        ft.commit();
        contactListFragment.setDataList(result);
        contactListFragment.taskRun = true;
    }
    class TabListener implements ActionBar.TabListener {
        String lastTab = null;
        private ActionBarActivity activity;
        public TabListener(ActionBarActivity activity) {
            this.activity = activity;
        }
        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            CharSequence tabText = tab.getText();
            Log.i(TAG, tabText.toString());
            if (tabText.equals("Profile")) {
                //ListProfileTask task = new ListProfileTask(activity, ft);
                //task.execute();
            } else if (tabText.equals("Contacts")) {
                InviteTask task = new InviteTask(activity, ft);
                task.execute();
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    }

}
