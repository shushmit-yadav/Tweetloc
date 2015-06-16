package com.brahminno.tweetloc;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Shushmit on 12-06-2015.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Top Rated fragment activity
                return new FragmentAdd();
            case 1:
                // Games fragment activity
                return new FragmentInvite();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
