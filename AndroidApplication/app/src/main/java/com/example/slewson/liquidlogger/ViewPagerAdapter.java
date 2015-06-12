package com.example.slewson.liquidlogger;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Marie on 5/21/2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    // Tab Titles
    private String tabtitles[] = new String[] { "Live", "Recipes"};
    Context context;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open FragmentTab1.java
            case 0:
                LiveFragment fragmenttab1 = new LiveFragment();
                return fragmenttab1;

            // Open FragmentTab2.java
            case 1:
                RecipeFragment fragmenttab2 = new RecipeFragment();
                return fragmenttab2;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
