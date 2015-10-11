package edu.sdsu.cs.chinnu.myproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

//FragmentPagerAdapter - Implementation of PagerAdapter that represents each page as a Fragment 
//that is persistently kept in the fragment manager as long as the user can return to the page.
//This adapter provides fragment views to tabs.
public class MyPagerAdapter extends FragmentPagerAdapter {
	
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
    	
        switch (index) {
        case 0:
            // audio fragment activity
            return new AudioFragment();
        case 1:
            // video fragment activity
            return new VideoFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count
    	// i.e number of tabs
        return 2;
    }
 
}
