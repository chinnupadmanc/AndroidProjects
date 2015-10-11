package edu.sdsu.cs.chinnu.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import edu.sdsu.cs.chinnu.myproject.AudioPlaylistFragment.OnListItemSelectedListener;

public class AudioPlaylistActivity extends FragmentActivity implements OnListItemSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_playlist);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.audio_playlist, menu);
		return true;
	}


	//Implementing onListFinished method
	public void onListFinished(String selectedFile) {	
		String audioFile = selectedFile;
		
		// Go to AudioPlayerActivity passing selected audio file.
		Intent toAudioPlayer = new Intent(this, AudioPlayerActivity.class);
		toAudioPlayer.putExtra("audioFile", audioFile);
		startActivity(toAudioPlayer);     
	}

}
