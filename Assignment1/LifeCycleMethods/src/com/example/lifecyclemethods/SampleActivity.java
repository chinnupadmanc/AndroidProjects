package com.example.lifecyclemethods;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SampleActivity extends Activity {
	
	//Variables for GUI elements
	private Button mResetButton;
	private EditText createCountOutput;
	private EditText restartCountOutput;
	private EditText startCountOutput;
	private EditText resumeCountOutput;
	private EditText pauseCountOutput;
	
	//Count variables
	private int createCount = 0;
	private int restartCount = 0;
	private int startCount = 0;
	private int resumeCount = 0;
	private int pauseCount = 0;
	
	//Keys for log statements
	private static final String TAG = "LifeCycleMethods";
	private static final String KEY_CREATE = "CreateCount";
	private static final String KEY_RESTART = "StopCount";
	private static final String KEY_START = "StartCount";
	private static final String KEY_RESUME = "ResumeCount";
	private static final String KEY_PAUSE = "PauseCount";
	
	//Overriding onCreate()
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			createCount = savedInstanceState.getInt(KEY_CREATE, 0);
			restartCount = savedInstanceState.getInt(KEY_RESTART, 0);
			startCount = savedInstanceState.getInt(KEY_START, 0);
			resumeCount = savedInstanceState.getInt(KEY_RESUME, 0);
			pauseCount = savedInstanceState.getInt(KEY_PAUSE, 0);
		}
			
		Log.d(TAG, "called onCreate() method");
		createCount++;
		
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_sample);
		
		updateCountOnUI();
		
		//Implementing onClick() for Reset button
		mResetButton = (Button) findViewById(R.id.resetButton);
		mResetButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				createCount = 0;		
				restartCount = 0;
				startCount = 0;
				resumeCount = 0;
				pauseCount = 0;
				createCountOutput.setText(String.valueOf(createCount));
				restartCountOutput.setText(String.valueOf(restartCount));
				startCountOutput.setText(String.valueOf(startCount));
				resumeCountOutput.setText(String.valueOf(resumeCount));
				pauseCountOutput.setText(String.valueOf(pauseCount));
			}
		});
			
	}
			
	
	//Overriding onRestart()
	@Override
	public void onRestart(){
		super.onRestart();
		Log.d(TAG, "called onRestart() method");
		restartCount++;
		
		updateCountOnUI();
	}
	
	//Overriding onStart()
	@Override
	public void onStart(){
		super.onStart();
		Log.d(TAG, "called onStart() method");
		startCount++;
		
		updateCountOnUI();
	}
	
	//Overriding onResume()
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG, "called onResume() method");
		resumeCount++;
		
		updateCountOnUI();
	}
	
	//Overriding onPause()
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG, "called onPause() method");
		pauseCount++;
		
		updateCountOnUI();
	}
	
	/*@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			createCount = savedInstanceState.getInt(KEY_CREATE, 0);
			restartCount = savedInstanceState.getInt(KEY_RESTART, 0);
			startCount = savedInstanceState.getInt(KEY_START, 0);
			resumeCount = savedInstanceState.getInt(KEY_RESUME, 0);
			pauseCount = savedInstanceState.getInt(KEY_PAUSE, 0);
		}	
	}*/
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		Log.i(TAG, "onSaveInstanceState");
		savedInstanceState.putInt(KEY_CREATE, createCount);	
		savedInstanceState.putInt(KEY_RESTART, restartCount);	
		savedInstanceState.putInt(KEY_START, startCount);	
		savedInstanceState.putInt(KEY_RESUME, resumeCount);	
		savedInstanceState.putInt(KEY_PAUSE, pauseCount);	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sample, menu);
		return true;
	}
	
	//Updates count values on the UI
	public void updateCountOnUI()
	{
		createCountOutput = (EditText) findViewById(R.id.createCountOutput);
		createCountOutput.setText(String.valueOf(createCount));
		
		restartCountOutput = (EditText) findViewById(R.id.restartCountOutput);
		restartCountOutput.setText(String.valueOf(restartCount));
		
		startCountOutput = (EditText) findViewById(R.id.startCountOutput);
		startCountOutput.setText(String.valueOf(startCount));
		
		resumeCountOutput = (EditText) this.findViewById(R.id.resumeCountOutput);
		resumeCountOutput.setText(String.valueOf(resumeCount));
		
		pauseCountOutput = (EditText) findViewById(R.id.pauseCountOutput);
		pauseCountOutput.setText(String.valueOf(pauseCount));
	}

}
