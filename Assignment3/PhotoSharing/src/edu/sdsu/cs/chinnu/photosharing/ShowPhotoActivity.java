package edu.sdsu.cs.chinnu.photosharing;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;

public class ShowPhotoActivity extends FragmentActivity {

	String photoId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_photo);

		//Getting selected photo id and photo id array
		Bundle passedData = getIntent().getExtras();
		String photoId = passedData.getString("photoId"); 
		Log.i("ShowPhotoActivity", "photo id: "+ photoId );

		////Display photo in showPhotoFragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		ShowPhotoFragment showPhotoFragment = new ShowPhotoFragment();
		showPhotoFragment.setArguments(passedData);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.show_photo_fragment_holder, showPhotoFragment);
		fragmentTransaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_photo, menu);
		return true;
	}

}
