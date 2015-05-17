package com.android.arthlimchiu.where;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Clarence on 5/3/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    String[] titles;
    int numOfTabs;

    public ViewPagerAdapter(FragmentManager fm, String[] titles, int numOfTabs) {
        super(fm);
        this.titles = titles;
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            WhereTrackListFragment tab1 = new WhereTrackListFragment();
            return tab1;
        } else if (position == 1) {
            WhereTrackListFragment tab2 = new WhereTrackListFragment();
            return tab2;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
