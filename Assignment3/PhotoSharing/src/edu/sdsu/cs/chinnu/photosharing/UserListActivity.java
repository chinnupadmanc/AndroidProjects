package edu.sdsu.cs.chinnu.photosharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import edu.sdsu.cs.chinnu.photosharing.UserListFragment.OnListItemSelectedListener;

public class UserListActivity extends FragmentActivity implements OnListItemSelectedListener {

	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);
	}

	//Implementing onListFinished method
	public void onListFinished(String selectedId) {
		userId = selectedId;
		Log.i("UserListActivity", "userId: " +userId);
		Intent toUserPhotos = new Intent(this, PhotoListActivity.class);
		toUserPhotos.putExtra("id",userId);
		startActivity(toUserPhotos);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_list, menu);
		return true;   	
	}


	//Response for the menu item selected on the action bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("inside onOptionsItemSelected", "onOptionsItemSelected");
		switch (item.getItemId()){

		case R.id.menu_item_upload_photo:
			Log.i("inside case", "upload_photo");
			Intent goToUploadPhoto = new Intent(this, PhotoUploadActivity.class);
			startActivity(goToUploadPhoto);
			return true;

		default:
			return super.onOptionsItemSelected(item);

		}

	}


}
