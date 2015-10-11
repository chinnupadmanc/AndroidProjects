package edu.sdsu.cs.chinnu.multipleactivitiesproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class KeyboardActivity extends Activity {

	private EditText editText1;

	//Method setDisplayHomeAsUpEnabled() is from API level 11.
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keyboard);
		Bundle passedData = getIntent().getExtras();
		String passedText = passedData.getString("editTextMain");

		editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setText(passedText);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//If the current activity does not have parent activity defined, no need to display the left pointing caret.
			if (NavUtils.getParentActivityName(this) != null) {
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
	}

	//Handle Back button to go back to the existing main activity.
	public void backToMain(View button)
	{
		finish();
	}

	//Handle Hide button to hide the soft keyboard.
	public void hideKeyboard(View button)
	{
		editText1 = (EditText) findViewById(R.id.editText1);
		InputMethodManager manager;
		manager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.keyboard, menu);
		return true;
	}

	//Handle going back to the main activity upon clicking on application icon in the action bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case android.R.id.home:
			//If the current activity does not have parent activity defined, no need to navigate up.
			if (NavUtils.getParentActivityName(this) != null) {
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;

		default:
			return super.onOptionsItemSelected(item); 	   
		}
	}

}
