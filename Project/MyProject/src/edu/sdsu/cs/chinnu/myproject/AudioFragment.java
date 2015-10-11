package edu.sdsu.cs.chinnu.myproject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AudioFragment extends Fragment {

	public static final String TAG = "AudioFragment";
	private ImageButton recordBtn;
	private ImageButton playBtn;
	private ImageButton deleteBtn;
	private Chronometer chronometer;
	private TextView saveNotification;
	private LinearLayout linearlayout_bottom;
	private static String mFileName = null;
	private static String mFilePath = null;
	private String mRootFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
	private String mFolder = mRootFolder +  "/MyProject/Audio/";
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private boolean mStartRecording = true;
	private static final int REQUEST_AUDIO_PLAYER = 101;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_audio, container, false);

		// Retrieve the widgets in the UI.
		recordBtn = (ImageButton) view.findViewById(R.id.recordBtn);
		playBtn = (ImageButton) view.findViewById(R.id.playBtn);
		deleteBtn = (ImageButton) view.findViewById(R.id.deleteBtn);
		chronometer = (Chronometer) view.findViewById(R.id.chronometer1);
		saveNotification = (TextView) view.findViewById(R.id.save_notification);
		linearlayout_bottom = (LinearLayout) view.findViewById(R.id.linearlayout_bottom);

		// Action for Record button.
		recordBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onRecord(v);
			} 
		});

		// Action for Play button.
		playBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onPlay(v);
			} 
		});

		// Action for Delete button.
		deleteBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onDelete(v);
			} 
		});

		Log.d("AudioFragment", "inside onCreate");
		return view;
	}

	
	@Override
	public void onResume() {
		super.onResume();
		
		// To handle the scenario, where user just records one file, goes to Audio Playlist 
		// and deletes that file, comes back to this screen again. 
		// File just recorded should disappear.
		if(mFileName != null) {			
			String mFilePath = mRootFolder +  "/MyProject/Audio/" + mFileName;
			File audioFile = new File (mFilePath); 
			if(audioFile.exists()) {
				saveNotification.setText(mFileName);
				linearlayout_bottom.setVisibility(View.VISIBLE);
			}
			else
			{
				saveNotification.setText("");
				linearlayout_bottom.setVisibility(View.GONE);
			}
		}
		
	}
	
	// To handle screen rotation.
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d("AudioFragment", "inside onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			if(mFileName != null) {						
					savedInstanceState.getString("mFileName");
					saveNotification.setText(mFileName);
					linearlayout_bottom.setVisibility(View.VISIBLE);				
			}
		}
	}


	// Record function
	public void onRecord(View view) {
		if (mStartRecording) {
			chronometer.setBase(SystemClock.elapsedRealtime());
			recordBtn.setImageResource(R.drawable.stoprecord);
			chronometer.start();
			startRecording();

			// Clear recently recorded audio file name, Play and Delete buttons.
			saveNotification.setText("");
			linearlayout_bottom.setVisibility(View.GONE);

		} else {
			recordBtn.setImageResource(R.drawable.record);
			chronometer.stop();
			stopRecording();

			Toast.makeText(getActivity().getApplicationContext(), "Audio File saved.", Toast.LENGTH_SHORT).show();
			
			// Display recently recorded audio file name with Play and Delete buttons.
			saveNotification.setText(mFileName);
			linearlayout_bottom.setVisibility(View.VISIBLE);
		}
		
		mStartRecording = !mStartRecording;
	}

	// Play function
	public void onPlay(View view) {
		Intent toAudioPlayer = new Intent(getActivity(), AudioPlayerActivity.class);
		toAudioPlayer.putExtra("audioFile", mFilePath);
		startActivityForResult(toAudioPlayer, REQUEST_AUDIO_PLAYER);
	}


	// Delete function
	public void onDelete(View view) {
		File audioFile = new File (mFilePath); 
		audioFile.delete();

		mFileName = null;
		saveNotification.setText("");
		linearlayout_bottom.setVisibility(View.GONE);
	}

	
	// startRecording functon
	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

		//Set up file path.
		String timeStamp = new SimpleDateFormat("yyyyMMdd_hhmmss", java.util.Locale.getDefault()).format(new Date());	    	
		mFileName = "audio_" + timeStamp + ".3gp";;
		mFilePath = mFolder + mFileName;

		mRecorder.setOutputFile(mFilePath);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(AudioFragment.TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	
	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	
	@Override
	public void onPause() {
		super.onPause();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

    // To handle screen rotation.
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("mFileName", mFileName);
		super.onSaveInstanceState(outState);
	}

}
