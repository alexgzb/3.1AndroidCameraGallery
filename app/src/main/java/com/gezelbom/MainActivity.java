package com.gezelbom;

import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Class that currently only displays a Scrollable tabs view with one fragment containing the Camera
 * And the other fragment contains the gallery
 *
 * @author Alex
 *
 */
public class MainActivity extends Activity {

    // The Adapter} that will provide
    // fragments for each of the sections
    FragmentPagerAdapter fragmentPagerAdapter;

    // The ViewPager that will host the section contents.
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the two
        // primary tabs of the activity.
        FragmentManager fragmentManager = getFragmentManager();
        fragmentPagerAdapter = new fPagerAdapter(fragmentManager);

        // Set up the ViewPager with the adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(fragmentPagerAdapter);

    }

    @Override
    protected void onDestroy() {
        //camera.release();
        super.onDestroy();
    }

    /**
     * A FragmentPagerAdapter that returns a fragment corresponding to one of
     * the sections/tabs/pages.
     */
    public class fPagerAdapter extends FragmentPagerAdapter {

        public fPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Method that gets the current tab by an int index value
         *
         */
        @Override
        public Fragment getItem(int position) {
            // Create a fragment
            Fragment fragment = null;

            // If position 0 create and display the first fragment and so on
            if (position == 0) {
                fragment = new FragmentCamera();
            } else if (position == 1) {
                fragment = new FragmentGallery();
            }
            // Return the fragment to display
            return fragment;
        }

        // total amount of tabs, needed to work properly
        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        /**
         * Uses the same methodology as getItem to display the correct title for each tab fragment
         */
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_fragment1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_fragment2).toUpperCase(l);
            }
            return null;
        }
    }

}
