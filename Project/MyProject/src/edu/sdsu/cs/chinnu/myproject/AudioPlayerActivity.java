package edu.sdsu.cs.chinnu.myproject;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

public class AudioPlayerActivity extends Activity implements MediaPlayerControl, OnPreparedListener, OnCompletionListener {

	public static final String TAG = "AudioPlayerActivity";
	private MediaPlayer mp;
	private MusicController controller;
	private Handler handler = new Handler();
	private TextView songTitleLabel;

	private void setController(){
		controller = new MusicController(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_player);

		//Get selected audio file path.
		Bundle passedData = getIntent().getExtras();
		String audioFile = passedData.getString("audioFile"); 

		songTitleLabel = (TextView) findViewById(R.id.songTitle);

		mp = new MediaPlayer();
		mp.setOnPreparedListener(this);
		mp.setOnCompletionListener(this); 

		setController();

		// Play selected audio file.
		playSong(audioFile);
	}

	// Function to play selected audio file.
	public void  playSong(String audioFile) {
		//controller.show(0);
		Log.d(AudioPlayerActivity.TAG,audioFile );
		
		// Play song
		try {
			mp.reset();
			mp.setDataSource(audioFile);
			mp.prepare();
			mp.start();

			// Displaying Song title
			String[] parts = audioFile.split("/");
			String songTitle = parts[parts.length - 1];
			songTitle = songTitle.substring(0, songTitle.length());
			songTitleLabel.setText(songTitle);
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Implementing inherited abstract method from OnCompletionListener.
	@Override
	public void onCompletion(MediaPlayer arg0) {
		mp.stop();
		mp.release();
		mp = null;	
		controller.hide();
		finish();
	}

	
	// Implementing inherited abstract method from OnPreparedListener.
	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		Log.d(AudioPlayerActivity.TAG, "onPrepared");
		controller.setMediaPlayer(this);
		controller.setAnchorView(findViewById(R.id.audio_player));
		
		handler.post(new Runnable() {
			public void run() {
				controller.setEnabled(true);
				controller.show();
			}
		});
	}

	
	@Override
	public void onDestroy(){
		controller.hide();
		  if (mp != null){
	          if(mp.isPlaying())
	              mp.stop();
	          mp.release();
	          mp = null;
	      }
		super.onDestroy();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_audio, menu);
		return true;
	}

	// Implementing the inherited abstract methods from MediaPlayerControl interface.
	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if(mp!=null)
		{
			return mp.getCurrentPosition();
		}
		return 0;
	}

	@Override
	public int getDuration() {
		if(mp!=null)
		{
			return mp.getDuration();
		}
		return 0;
	}

	@Override
	public boolean isPlaying() {
		if(mp!=null)
		{
			return mp.isPlaying();
		}
		return false;
	}

	@Override
	public void pause() {
		mp.pause();
	}

	@Override
	public void seekTo(int pos) {
		mp.seekTo(pos);
	}

	@Override
	public void start() {
		mp.start();
	}

}
