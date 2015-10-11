package edu.sdsu.cs.chinnu.photosharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.widget.FrameLayout;
import edu.sdsu.cs.chinnu.photosharing.PhotoListFragment.OnListItemSelectedListener;

public class PhotoListActivity extends FragmentActivity implements OnListItemSelectedListener{
	String photoId;
	String[] idArr;
	boolean fragmentExist = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_list);

		//Get selected user id from UserListActivity
		Bundle passedData = getIntent().getExtras();
		String userId = passedData.getString("id"); 
		Log.i("PhotoListActivity","userId: "+userId );

		//Pass selected user id to PhotoListFragment
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		PhotoListFragment photoListFragment = (PhotoListFragment) fragmentManager.findFragmentById(R.id.photo_list_fragment);
		photoListFragment.passUserId(userId);	

		//Check if fragment holder for displaying photo exists in the layout. It is true for the tablet layout.
		//Handle tablet screen based on this check.
		if((FrameLayout) findViewById(R.id.show_photo_fragment_holder) != null)
		{
			fragmentExist = true;
		}
	}

	//To get selected photo id and and photo id array of all photos from PhotoListFragment.
	//Implementing onListFinished method
	public void onListFinished(String selectedId, String[] idArray) {

		photoId = selectedId;
		idArr = idArray;

		//For hand set
		//Call next screen(ShowPhotoActivity) to display photo
		if(!fragmentExist)
		{
			Intent toShowPhoto = new Intent(this, ShowPhotoActivity.class);
			toShowPhoto.putExtra("photoId",photoId);
			toShowPhoto.putExtra("idArr",idArr);
			startActivity(toShowPhoto);
		}
		//For tablet
		//Display photo in showPhotoFragment
		else
		{
			Bundle passToShowPhoto = new Bundle();
			passToShowPhoto.putString("photoId", photoId);
			passToShowPhoto.putStringArray("idArr",idArr);
			FragmentManager fragmentManager = this.getSupportFragmentManager();
			ShowPhotoFragment showPhotoFragment = new ShowPhotoFragment();
			showPhotoFragment.setArguments(passToShowPhoto);
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.show_photo_fragment_holder, showPhotoFragment);
			fragmentTransaction.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_list, menu);
		return true;
	}

}
