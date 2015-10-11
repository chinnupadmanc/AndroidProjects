package edu.sdsu.cs.chinnu.myproject;

import java.io.File;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayerActivity extends Activity {

	private VideoView mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);	

		mVideoView = (VideoView) findViewById(R.id.videoView);

		//Get selected video file path
		Bundle passedData = getIntent().getExtras();
		String videoFilePath = passedData.getString("videoFile");

		// Play selected video file.
		playVideo(videoFilePath);

		mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mVideoView.stopPlayback();
				mp.release();
				mp = null;
				finish();
			}
		});

	}

	
	// playVideo function
	public void  playVideo(String videoFilePath){
		File videoFile = new File(videoFilePath);
		Uri videoUri = Uri.fromFile(videoFile);
		mVideoView.setVideoURI(videoUri);
		mVideoView.setMediaController(new MediaController(this));
		mVideoView.requestFocus();
		mVideoView.start();
	}


	@Override
	public void onDestroy(){
		super.onDestroy();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_player, menu);
		return true;
	}

}
