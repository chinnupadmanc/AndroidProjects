package edu.sdsu.cs.chinnu.photosharing;

import java.io.File;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoUploadActivity extends Activity {

	HttpClient httpclient;
	HttpClientTask task;
	String chosenPath;
	String fileName;
	private static final int REQ_CODE_PICK_IMAGE = 100;
	ImageView photoView;
	Button uploadButton;
	LinearLayout layout;
	TextView resultText;
	Bitmap bitmapImage;
	TextView networkStatus;
	Button selectPhotoButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_upload);
		selectPhotoButton = (Button) findViewById(R.id.select_photo);
		photoView = (ImageView) findViewById(R.id.imageView1);
		uploadButton = (Button) findViewById(R.id.upload);
		layout = (LinearLayout) findViewById(R.id.progressbar_view);
		resultText = (TextView) findViewById(R.id.result_text);
		networkStatus = (TextView) findViewById(R.id.network_status);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(!isOnline())
		{
			networkStatus.setVisibility(View.VISIBLE);
			selectPhotoButton.setVisibility(View.GONE);
			return;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(httpclient != null)
		{
			httpclient.getConnectionManager().shutdown();
			((AndroidHttpClient) httpclient).close();
			httpclient = null;
		}
	}

	//http://stackoverflow.com/questions/17503422/how-to-implement-internet-check-in-asynctask-in-android
	//This method check internet connection. True for connection enabled, false otherwise.
	public boolean isOnline() {
		//Log.i("PhotoUploadActivity","inside isonline");
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_upload, menu);
		return true;
	}


	class HttpClientTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			layout.setVisibility(View.VISIBLE);
			uploadButton.setVisibility(View.GONE);
			super.onPreExecute();
		}

		protected String doInBackground(String... urls) {
			try {
				File imgFile = new File(chosenPath);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				HttpPost postMethod = new HttpPost(urls[0]);
				FileEntity photo = new FileEntity(imgFile, "image/jpeg");
				postMethod.setEntity(photo);

				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
				HttpConnectionParams.setSoTimeout(httpParameters, 5000);
				postMethod.setParams(httpParameters);

				//HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 1000);
				String responseBody = httpclient.execute(postMethod, responseHandler);
				Log.i("PhotoUploadActivity","response: " +responseBody);
				return responseBody;
			} catch (Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), "Could not connect to server!", Toast.LENGTH_LONG).show();
					}
				});

				Log.e("PhotoUploadActivity","An Exception occurred", e);
			}
			return null;
		}

		public void onPostExecute(String result) {
			//Offline case
			if(result == null)
			{
				layout.setVisibility(View.GONE);
				networkStatus.setVisibility(View.VISIBLE);
				return;
			}

			//Uploaded successfully
			if(result.indexOf("id") != -1)
			{
				layout.setVisibility(View.GONE);
				resultText.setText("Uploaded successfully");
			}
			//Error in upload
			else
			{
				resultText.setText("Error in Upload");
			}
		}
	}

	//Action for Select Photo button
	public void selectPhoto(View button){
		Intent photoPickerIntent = new Intent();
		photoPickerIntent.setType("image/*");
		photoPickerIntent.setAction(Intent.ACTION_PICK);
		startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE);
	}

	//http://stackoverflow.com/questions/2507898/how-to-pick-an-image-from-gallery-sd-card-for-my-app-in-android
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		try{
			//Log.i("PhotoUploadActivity", "inside onActivityResult");
			switch(requestCode) { 
			case REQ_CODE_PICK_IMAGE:
				if (resultCode == RESULT_OK)
				{
					//Retrieves the path of the image that was chosen from the intent of getting photos from the gallery
					Uri selectedImageUri = data.getData();					
					String fileManagerString = selectedImageUri.getPath();
					String filePath = getImagePath(selectedImageUri);

					if (filePath != null) {
						chosenPath = filePath;
					} else {
						chosenPath = fileManagerString;
					}

					//Get file name from ile path
					File imgFile = new File(chosenPath);
					//fileName = selectedImageUri.getLastPathSegment().toString();
					fileName = imgFile.getName();  
					int index = fileName.lastIndexOf('.');

					if (index != -1) 
					{
						fileName = fileName.substring(0, index); 
					}
					Log.i("PhotoUploadActivity", "fileName: " + fileName);

					Log.i("PhotoUploadActivity", "filePath: " + chosenPath);

					bitmapImage = BitmapFactory.decodeFile(chosenPath);
					photoView.setVisibility(View.VISIBLE);
					photoView.setScaleType(ScaleType.CENTER_CROP);
					photoView.setImageBitmap(bitmapImage);

					resultText.setText("");
					uploadButton.setVisibility(View.VISIBLE);
				}
			}
		}
		catch(Exception e){
			Toast.makeText(getApplicationContext(), "Error occurred!", Toast.LENGTH_LONG).show();
			Log.e("PhotoUploadActivity","An Exception occurred", e);
		}
	}

	//Action for upload button
	public void uploadPhoto(View button){
		String userAgent = null;
		httpclient = AndroidHttpClient.newInstance(userAgent);
		task = new HttpClientTask();
		String userName = "chinnu";
		String userPassword = "1717";
		String url = "http://bismarck.sdsu.edu/photoserver/postphoto/" +userName + "/" + userPassword + "/" + fileName;
		Log.i("PhotoUploadActivity", "url: " + url);
		task.execute(url);
	}

	//Get image path from uri
	public String getImagePath(Uri uri) {
		String selectedImagePath;
		// 1:MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			selectedImagePath = cursor.getString(column_index);
		} else {
			selectedImagePath = null;
		}

		cursor.close();

		if (selectedImagePath == null) {
			// 2:OI FILE Manager --- call method: uri.getPath()
			selectedImagePath = uri.getPath();
		}
		return selectedImagePath;
	}
}
