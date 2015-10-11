package edu.sdsu.cs.chinnu.multipleactivitiesproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity {

	private Button submitButton;
	private Spinner activitiesSpinner;
	private EditText editTextMain;
	private String selectedItem;
	private String textEntered;
	private static final int INTENT_LISTACTIVITY_REQUEST = 111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		activitiesSpinner = (Spinner) findViewById(R.id.activities_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.activities_array, android.R.layout.simple_list_item_1);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		activitiesSpinner.setAdapter(adapter);
		
		editTextMain = (EditText) findViewById(R.id.editTextMain);

		//Implementing onClick listener for Submit button
		submitButton = (Button) findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {				
				selectedItem = activitiesSpinner.getSelectedItem().toString();
				textEntered = editTextMain.getText().toString();
				if(selectedItem.equals("Editor"))
				{
					Intent go = new Intent(MainActivity.this,KeyboardActivity.class);
					go.putExtra("editTextMain",textEntered);
					startActivity(go);
				}
				else if(selectedItem.equals("Browser"))
				{
					Intent go = new Intent(MainActivity.this,WebActivity.class);
					startActivity(go);
				}
				else if(selectedItem.equals("Version List"))
				{
					Intent go = new Intent(MainActivity.this,ListActivity.class);
					startActivityForResult(go,INTENT_LISTACTIVITY_REQUEST);
				}
			}
		});
	}

	//Display selected android version received back from list activity in the editbox.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != INTENT_LISTACTIVITY_REQUEST) {
			return;
		}
		switch (resultCode) {
		case RESULT_OK:
			String selectedVersion = data.getStringExtra("selectedVersion");
			editTextMain = (EditText) findViewById(R.id.editTextMain);
			editTextMain.setText(selectedVersion);
			break;
		case RESULT_CANCELED:
			break;
		}
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

		case R.id.menu_item_keyboard_activity:
			textEntered = editTextMain.getText().toString();
			Intent goKeyboard = new Intent(this,KeyboardActivity.class);
			goKeyboard.putExtra("editTextMain",textEntered);
			startActivity(goKeyboard);
			return true;

		case R.id.menu_item_web_activity:
			Intent goWeb = new Intent(this,WebActivity.class);
			startActivity(goWeb);
			return true;

		case R.id.menu_item_list_activity:
			Intent goList = new Intent(this,ListActivity.class);
			startActivityForResult(goList,INTENT_LISTACTIVITY_REQUEST);
			return true;

		default:
			return super.onOptionsItemSelected(item);

		}

	}

}
