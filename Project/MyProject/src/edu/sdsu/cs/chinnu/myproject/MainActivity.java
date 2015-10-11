package edu.sdsu.cs.chinnu.myproject;

import java.io.File;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	private ViewPager viewPager;
	
	// When requested, this adapter returns a Fragment, representing an object in the collection.
	private MyPagerAdapter myPagerAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		final ActionBar actionBar = getActionBar();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String mRootFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
		String mFolder = mRootFolder + "/MyProject/Audio/";

		File dirs = new File(mFolder);
		if (!dirs.exists())
			dirs.mkdirs();

		mFolder = mRootFolder + "/MyProject/Video/";
		dirs = new File(mFolder);
		if (!dirs.exists())
			dirs.mkdirs();

		// ViewPager and its adapters use support library fragments, so use getSupportFragmentManager.
		myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.pager);
		//viewPager.setOffscreenPageLimit(myPagerAdapter.getCount()-1);
		viewPager.setAdapter(myPagerAdapter);

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);     

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				// When the tab is selected, switch to the corresponding page in the ViewPager.
				// show respective fragment view.
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// hide the given tab
			}

			@Override
			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// probably ignore this event
			}	

		};

		// Adding tabs
		actionBar.addTab(actionBar.newTab().setText(R.string.audio_tab).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.video_tab).setTabListener(tabListener));

		// Select respective tab when the user swipes between pages with a touch gesture. 
		// Set up this behavior by implementing the ViewPager.OnPageChangeListener interface 
		// to change the current tab each time the page changes.
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			// This method will be invoked when a new page becomes selected.
			public void onPageSelected(int position) {
				// on changing the page
				// When swiping between pages, select the corresponding tab.
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}       

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//Response for the menu item selected on the action bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){

		// Go to Audio Playlist.
		case R.id.menu_item_audio_playlist:
			Intent goToAudioPlaylist = new Intent(this, AudioPlaylistActivity.class);	
			startActivity(goToAudioPlaylist);
			return true;

		// Go to Video Playlist.	
		case R.id.menu_item_video_playlist:
			Intent goToVideoPlaylist = new Intent(this, VideoPlaylistActivity.class);
			startActivity(goToVideoPlaylist);
			return true;

		// Go to WiFi P2P screen.	
		case R.id.menu_item_wifidirect:
			Intent toWifiDirect = new Intent(this, WiFiDirectActivity.class);
			startActivity(toWifiDirect);
			return true;

		default:
			return super.onOptionsItemSelected(item);

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

}
