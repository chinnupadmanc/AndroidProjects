package edu.sdsu.cs.chinnu.photosharing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShowPhotoFragment extends Fragment {

	HttpClient httpclient;
	HttpClientTask task;
	String photoId;
	String[] idArr;
	ImageView photoView;
	ProgressBar progressBar;
	LinearLayout layout;
	Context context;
	int currentPhotoIndex;
	int arrayLen;
	TextView networkStatus;


	// swipe gesture constants
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	//private GestureDetector gestureDetector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("ShowPhotoFragment", "onCreate()");
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			Bundle bundle = getArguments();
			arrayLen = bundle.getStringArray("idArr").length;
			idArr = new String[arrayLen];
			idArr =  bundle.getStringArray("idArr");

			currentPhotoIndex = savedInstanceState.getInt("currentPhotoIndex");
			photoId = idArr[currentPhotoIndex];
			Log.i("ShowPhotoFragment", "onCreate(). photoId:"+photoId);
			Log.i("ShowPhotoFragment", "onCreate. currentPhotoIndex: "+currentPhotoIndex);
		}
		else
		{
			//Getting selected photo id and photo id array
			Bundle bundle = getArguments();
			photoId = bundle.getString("photoId");
			arrayLen = bundle.getStringArray("idArr").length;
			idArr = new String[arrayLen];
			idArr =  bundle.getStringArray("idArr");

			for(int i=0; i<arrayLen; i++)
			{
				if(idArr[i].equals(photoId))
				{
					currentPhotoIndex = i;
					break;
				}
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//To handle screen orientation change. Save currentPhotoIndex in OnSaveInstanceState() and restore here.
		//Need to use onActivityCreated, (not onCreate or onCreateView),  in this case as photoId is passed in the onCreate() of parent activity ShowPhotoActivity.
		//We need to replace it with the photo id corresponding to the saved currentPhotoIndex.
		//If we restore this value any time before onCreate() of parent activity is not done, it will get replaced by the photoId passed earlier(the one corresponding to the item selected in PhotListActivity).
		if (savedInstanceState != null) {
			Bundle bundle = getArguments();
			arrayLen = bundle.getStringArray("idArr").length;
			idArr = new String[arrayLen];
			idArr =  bundle.getStringArray("idArr");

			currentPhotoIndex = savedInstanceState.getInt("currentPhotoIndex");
			photoId = idArr[currentPhotoIndex];

			Log.i("ShowPhotoFragment", "onActivityCreated. currentPhotoIndex: "+currentPhotoIndex);
			Log.i("ShowPhotoFragment", "onActivityCreated(). photoId:"+photoId);
		}
	}

	private void handleSwipeLeftToRight() {
		// TODO: implement the business logic here
		Log.i("ShowPhotoFragment", "handleSwipeLeftToRight. currentPhotoIndex:"+currentPhotoIndex);
		if(currentPhotoIndex > 0)
		{
			photoId = idArr[--currentPhotoIndex];

			File root = Environment.getExternalStorageDirectory();
			File imgFile = new File(root, photoId + ".jpg");
			if(imgFile.exists())
			{
				layout.setVisibility(View.GONE);
				Log.i("ShowPhotoFragment","image from sd card");
				BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), imgFile.getAbsolutePath());
				photoView.setScaleType(ScaleType.CENTER_CROP);
				photoView.setImageDrawable(bitmapDrawable);
			}
			else
			{
				task = new HttpClientTask();
				if(httpclient == null)
				{
					httpclient = AndroidHttpClient.newInstance(null);
				}
				String url = "http://bismarck.sdsu.edu/photoserver/photo/" + photoId;
				task.execute(url);
			}
		}
	}

	private void handleSwipeRightToLeft() {
		// TODO: implement the business logic here
		Log.i("ShowPhotoFragment", "handleSwipeRightToLeft. currentPhotoIndex:"+ currentPhotoIndex);
		if(currentPhotoIndex < arrayLen-1)
		{
			photoId = idArr[++currentPhotoIndex];

			File root = Environment.getExternalStorageDirectory();
			File imgFile = new File(root, photoId + ".jpg");
			if(imgFile.exists())
			{
				layout.setVisibility(View.GONE);
				Log.i("ShowPhotoFragment","image from sd card");
				BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), imgFile.getAbsolutePath());
				photoView.setScaleType(ScaleType.CENTER_CROP);
				photoView.setImageDrawable(bitmapDrawable);
			}
			else
			{	
				task = new HttpClientTask();
				if(httpclient == null)
				{
					httpclient = AndroidHttpClient.newInstance(null);
				}
				String url = "http://bismarck.sdsu.edu/photoserver/photo/" + photoId;
				Log.i("ShowPhotoFragment","URL: " + url);
				task.execute(url);
			}
		}
	}



	@Override
	public void onSaveInstanceState(Bundle outData) {
		Log.i("ShowPhotoFragment", "onSaveInstanceState()");
		super.onSaveInstanceState(outData);
		outData.putInt("currentPhotoIndex", currentPhotoIndex);
		Log.i("ShowPhotoFragment", "onSaveInstanceState. currentPhotoIndex: "+currentPhotoIndex);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_show_photo, container, false);
		photoView = (ImageView) view.findViewById(R.id.imageView);
		networkStatus = (TextView) view.findViewById(R.id.network_status);

		//http://stackoverflow.com/questions/11421368/android-fragment-oncreateview-with-gestures
		final GestureDetector gesture = new GestureDetector(getActivity(),
				new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, 
					float velocityX, float velocityY) {
				float deltaX = e2.getX() - e1.getX();
				if ((Math.abs(deltaX) < SWIPE_MIN_DISTANCE) 
						|| (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY)) {
					return false; // insignificant swipe
				} else {
					if (deltaX < 0) { // right to left
						handleSwipeRightToLeft();
					} else { // left to right
						handleSwipeLeftToRight();

					}
				}
				return true;
			}
		});

		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gesture.onTouchEvent(event);
			}
		});

		layout = (LinearLayout) view.findViewById(R.id.progressbar_view);
		return view;
	}

	class HttpClientTask extends AsyncTask<String, Void, Bitmap> {

		Exception ex;

		@Override
		protected void onPreExecute() {
			layout.setVisibility(View.VISIBLE);
			photoView.setVisibility(View.GONE);
			super.onPreExecute();
		}

		protected Bitmap doInBackground(String... urls) {
			ex = null;
			try {
				HttpGet getRequest = new HttpGet(urls[0]);
				HttpResponse response = httpclient.execute(getRequest);
				Log.i("response","response");
				HttpEntity entity = getHttpEntity(response);
				Log.i("entity",""+entity);
				//if (getRequest != null) { getRequest.abort();}
				if (entity == null) 
				{
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(context, "Error! Selected photo does not exist on the server.", Toast.LENGTH_LONG).show();
						}
					});
					return null;
				}
				return bitmapFromEntity(entity);
			} /*catch (NullPointerException e) {
				ex = e;
				Log.i("Exception","Exception");
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(context, "An Error occurred!", Toast.LENGTH_LONG).show();
					}
				});
				Log.e("ShowPhotoFragment","An NullPointerException occurred", e);
			}*/
			catch (Exception e)  {
				Log.e("ShowPhotoFragment","An Exception occurred", e);
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						networkStatus.setVisibility(View.VISIBLE);
					}
				});	
			}
			finally {
				//((AndroidHttpClient) httpclient).close();
			}
			return null;
		}

		public void onPostExecute(Bitmap photo) {
			layout.setVisibility(View.GONE);

			/*if((ex instanceof NullPointerException) || (ex instanceof Exception))
			{
				Log.i("ex", "ex");
				ex = null;
				return;
			} 

			if(photo == null)
			{
				Log.i("toast","toast");
				networkStatus.setVisibility(View.VISIBLE);
				//Toast.makeText(context, "No network connection", Toast.LENGTH_LONG).show();
				return;
			}*/

			if(photo != null)
			{
				FileOutputStream fileOutputStream = null;
				try
				{
					File root = Environment.getExternalStorageDirectory();
					Log.i("root",""+root);
					File imgFile = new File(root, photoId + ".jpg");
					imgFile.createNewFile();
					fileOutputStream = new FileOutputStream(imgFile);
					photo.compress(Bitmap.CompressFormat.JPEG,50,fileOutputStream);

					photoView.setVisibility(View.VISIBLE);
					photoView.setScaleType(ScaleType.CENTER_CROP);
					photoView.setImageBitmap(photo);
				}catch(IOException e)
				{
					Log.i("ShowPhotoFragment", "An exception occurred", e);
				}
				finally {

					try{
						fileOutputStream.close();

					}catch (IOException e) {
						Log.i("ShowPhotoFragment", "An exception occurred", e);
					}
				}
			}

		}

		private HttpEntity getHttpEntity(HttpResponse response) {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			return entity;
		}

		private Bitmap bitmapFromEntity(HttpEntity entity) throws Exception{
			InputStream inputStream = null;
			try {
				Log.i("ShowPhotoFragment", "Inside bitmapFromEntity");

				inputStream = entity.getContent();
				Bitmap photo = BitmapFactory.decodeStream(inputStream);			
				inputStream.close();
				Log.i("ShowPhotoFragment", "inputStream close");
				return photo;
			} catch (Exception e) {
				Log.i("IOException","IOException");
				/*getActivity().runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(context, "An Error occurred!", Toast.LENGTH_LONG).show();
					}
				});*/
				Log.i("ShowPhotoFragment", "An exception occurred", e);

				throw new Exception("An exception occurred");
				//throw new IllegalArgumentException("Unable to load", e);
				//return null;
			} 
			finally {
				try{
					entity.consumeContent();
				}catch (IOException e) {
					Log.i("ShowPhotoFragment", "An exception occurred", e);
				}
			}
		}

	}
	@Override
	public void onResume () {
		super.onResume();

		Log.i("ShowPhotoragment","onResume()");
		Log.i("ShowPhotoragment","photoId: " + photoId);

		File root = Environment.getExternalStorageDirectory();
		File imgFile = new File(root, photoId + ".jpg");
		if(imgFile.exists())
		{
			layout.setVisibility(View.GONE);
			Log.i("ShowPhotoragment","image from sd card");
			BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), imgFile.getAbsolutePath());
			photoView.setScaleType(ScaleType.CENTER_CROP);
			photoView.setImageDrawable(bitmapDrawable);
		}

		else
		{
			context = getActivity();
			httpclient = AndroidHttpClient.newInstance(null);
			task = new HttpClientTask();
			String url = "http://bismarck.sdsu.edu/photoserver/photo/" + photoId;
			Log.i("url",url);
			task.execute(url);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (httpclient != null)
		{
			httpclient.getConnectionManager().shutdown();
			((AndroidHttpClient) httpclient).close();
			httpclient = null;
		}
	}
}
