package edu.sdsu.cs.chinnu.multipleactivitiesproject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import edu.sdsu.cs.chinnu.multipleactivitiesproject.VersionListFragment.OnListItemSelectedListener;


public class ListActivity extends FragmentActivity implements OnListItemSelectedListener {

	private String selectedVersion;
	
	//Method setDisplayHomeAsUpEnabled() is from API level 11.
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//If the current activity does not have parent activity defined, no need to display the left pointing caret.
			if (NavUtils.getParentActivityName(this) != null) {
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
	}

	//Handle Back button to go back to the existing main activity, also passing the selected android version from the list.
	public void backToMain(View button) {
		Intent toPassBack = getIntent();
		toPassBack.putExtra("selectedVersion",selectedVersion);
		setResult(RESULT_OK, toPassBack);
		finish();
		}
	
	//Implementing onListFinished method
	public void onListFinished(String selectedItem) {
		selectedVersion = selectedItem;
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list, menu);
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
