package edu.sdsu.cs.chinnu.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import edu.sdsu.cs.chinnu.myproject.VideoPlaylistFragment.OnListItemSelectedListener;


public class VideoPlaylistActivity extends FragmentActivity implements OnListItemSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_playlist);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_playlist, menu);
		return true;
	}

	
	//Implementing onListFinished method
	public void onListFinished(String selectedFile, String action) {	
		String videoFile = selectedFile;

		if(action.equals("play"))
		{
			Intent toVideoPlayer = new Intent(this, VideoPlayerActivity.class);
			toVideoPlayer.putExtra("videoFile", videoFile);
			startActivity(toVideoPlayer);
		}
		else if(action.equals("share") )
		{
			Intent toWifiDirect = new Intent(this, WiFiDirectActivity.class);
			toWifiDirect.putExtra("filePath", videoFile);
			startActivity(toWifiDirect);
		}
	}

}
