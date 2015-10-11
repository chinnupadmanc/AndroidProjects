package edu.sdsu.cs.chinnu.myproject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VideoFragment extends Fragment{

	private ImageButton recordBtn;
	private ImageButton playBtn;
	private ImageButton deleteBtn;
	private ImageButton shareBtn;
	private TextView saveNotification;
	private LinearLayout linearlayout_bottom;
	private static final int REQUEST_VIDEO_CAPTURE = 1;
	private Uri videoUri;
	private static String mFileName = null;
	private static String mFilePath = null;
	private String mRootFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
	private String mFolder = mRootFolder +  "/MyProject/Video/";


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_video, container, false);

		// Retrieve the widgets in the UI.
		recordBtn = (ImageButton) view.findViewById(R.id.recordBtn);
		playBtn = (ImageButton) view.findViewById(R.id.playBtn);
		deleteBtn = (ImageButton) view.findViewById(R.id.deleteBtn);
		shareBtn = (ImageButton) view.findViewById(R.id.shareBtn);
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

		// Action for Share button.
		shareBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onShare(v);
			} 
		});

		return view;
	}

	
	@Override
	public void onResume() {
		super.onResume();
		
		// To handle the scenario, where user just records one file, goes to Video Playlist 
		// and deletes that file, comes back to this screen again. 
		// File just recorded should disappear.
		if(mFileName != null) {			
			String mFilePath = mFolder + mFileName;
			File videoFile = new File (mFilePath); 
			if(videoFile.exists()) {
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
		saveNotification.setText("");
		linearlayout_bottom.setVisibility(View.GONE);

		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

		String timeStamp = new SimpleDateFormat("yyyyMMdd_hhmmss", java.util.Locale.getDefault()).format(new Date());	    	
		mFileName = "video_" + timeStamp+ ".mp4";;
		mFilePath = mFolder + mFileName;
		File videoFile = new File(mFilePath);
		videoUri = Uri.fromFile(videoFile);
		takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);

		//resolveActivity() returns the first activity component that can handle the intent.
		//Important because if you call startActivityForResult() using an intent that no app can handle, your app will crash.
		if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
		}
	}

	// Play function
	public void onPlay(View view) {
		Intent toVideoPlayer = new Intent(getActivity(), VideoPlayerActivity.class);
		toVideoPlayer.putExtra("videoFile", mFilePath);
		startActivity(toVideoPlayer);
	}

	
	// Delete function
	public void onDelete(View view) {
		File videoFile = new File (mFilePath); 
		videoFile.delete();

		mFileName = null;
		saveNotification.setText("");
		linearlayout_bottom.setVisibility(View.GONE);
	}
	
	
	// Share function
	public void onShare(View view) {
		Intent toWifiDirect = new Intent(getActivity(), WiFiDirectActivity.class);
		toWifiDirect.putExtra("filePath", mFilePath);
		startActivity(toWifiDirect);
	}


	// To handle screen rotation.
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("mFileName", mFileName);
		super.onSaveInstanceState(outState);
	}

}