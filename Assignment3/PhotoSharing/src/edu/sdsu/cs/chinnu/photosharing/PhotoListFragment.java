package edu.sdsu.cs.chinnu.photosharing;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoListFragment extends ListFragment{

	private String selectedId;
	private String selectedPhoto;
	private OnListItemSelectedListener listListener;
	HttpClient httpclient;
	HttpClientTask task;
	String userId;
	String[] photoArr;
	String[] idArr;
	Context context;
	DatabaseHandler handler;
	TextView networkStatus;

	//Need to use onActivityCreated since userId is passed in onCreate() of the parent activity.
	//Fragment will get the value of userId only after onCreate() of the parent activity is completed.
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		context = getActivity();
		handler = new DatabaseHandler(context);
		handler.open();

		//Check if online and display photo list from server.
		if(isOnline())
		{
			Log.i("PhotoListFragment","onActivityCreated(). Display photo list from server");
			String userAgent = null;
			httpclient = AndroidHttpClient.newInstance(userAgent);
			task = new HttpClientTask();
			String url = "http://bismarck.sdsu.edu/photoserver/userphotos/" + userId;
			Log.i("url", url);
			task.execute(url);
		}
		//If not online, check if photo details are available offline in db and display the photo list.
		else
		{
			Log.i("PhotoListFragment","onActivityCreated(). Display photo list from db");
			boolean tableExist = handler.isTableExists("photoTable");

			Log.i("PhotoListFragment","onActivityCreated(). tableExist(): " +tableExist);
			List<String> photoList = new ArrayList<String>();
			List<String> photoIdList = new ArrayList<String>();

			if(tableExist)
			{
				Cursor cursor = handler.fetchPhotos(userId);
				if (cursor != null) {

					if(!(cursor.moveToFirst()))
					{
						//Log.i("PhotoListFragment", "!(cursor.moveToFirst()): "+!(cursor.moveToFirst()));
						networkStatus.setVisibility(View.VISIBLE);
					}
					else {
						do {        	
							photoList.add(cursor.getString(0));
							photoIdList.add(cursor.getString(1));
						} while (cursor.moveToNext());

						//Storing photo names and ids to class variables photoArr and idArr respectively to use in onListItemClick().
						int x = photoList.size();
						photoArr = new String[x];
						photoArr = photoList.toArray(photoArr);
						Log.i("PhotoListFragment", "photoArr.length: "+photoArr.length);

						idArr = new String[x];
						idArr = photoIdList.toArray(idArr);

						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, photoList);
						dataAdapter.notifyDataSetChanged();
						setListAdapter(dataAdapter);
					}
				}
				cursor.close();
			}
		}
	}

	public interface OnListItemSelectedListener {
		public void onListFinished(String selectedId, String[] idArray);
	}

	class HttpClientTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {
			try {
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				HttpGet getMethod = new HttpGet(urls[0]);
				String responseBody = httpclient.execute(getMethod,	responseHandler);
				getMethod.abort();
				return responseBody;
			} catch (Exception e) {
				Log.e("UserListFragment","An Exception occurred", e);
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						networkStatus.setVisibility(View.VISIBLE);
					}
				});	
			}
			return null;
		}

		public void onPostExecute(String result) {

			//result is null if Data Enabled is true in your mobile networks setting, but you do not have data plan.
			//Let the user know there is no Internet connection. 
			//isOnline() is true even if you do not have data plan, but Data Enabled is checked.
			/*if(result == null)
			{
				networkStatus.setVisibility(View.VISIBLE);
				return;
			}*/

			if(result != null)
			{
				try {

					boolean tableExist = handler.isTableExists("photoTable");
					if(!tableExist)
					{
						handler.createPhotoTable();
					}
					else
					{
						handler.deletePhotos(userId);
					}

					JSONArray data = new JSONArray(result);
					int size = data.length();
					photoArr = new String[size];
					idArr = new String[size];
					for(int i=0; i<size; i++){
						JSONObject firstUser = (JSONObject) data.get(i);
						photoArr[i] = firstUser.getString("name");
						idArr[i] = firstUser.getString("id");
						handler.insertPhoto(userId, photoArr[i], idArr[i]);
					}

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, photoArr);
					setListAdapter(dataAdapter);

				} catch (JSONException e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(context, "Error occurred! Could not display the list.", Toast.LENGTH_LONG).show();
						}
					});

					Log.e("PhotoListFragment","An Exception occurred", e);
					//throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public void onAttach (Activity activity) {
		super.onAttach(activity);
		//Type casting activity object to OnListItemSelectedListener abstract class.
		listListener = (OnListItemSelectedListener) activity;
	}

	@Override
	public void onResume () {
		super.onResume();

		if(networkStatus.getVisibility() == View.GONE)
		{
			if(!isOnline()){
				Log.i("isOnline","not online");
				networkStatus.setVisibility(View.VISIBLE);
			}
		}

		//In onResume always display user list from db, if available.
		//If the user comes back to this screen after pressing Home button or navigating to next screen
		//(in which case onCreate() is not called, no need to fetch data from server.
		boolean tableExist = handler.isTableExists("photoTable");

		Log.i("PhotoListFragment", "tableExist(): "+tableExist);
		List<String> photoList = new ArrayList<String>();
		List<String> photoIdList = new ArrayList<String>();

		if(tableExist)
		{
			//Log.i("if", "if" + "USERID" + userId);
			Log.i("PhotoListFragment", "onResume. Display photo list from db");
			Cursor cursor = handler.fetchPhotos(userId);
			if (cursor != null) {
				Log.i("if", "cursor != null");

				if(!(cursor.moveToFirst()))
				{
					if(!isOnline()){
						networkStatus.setVisibility(View.VISIBLE);
					}
				}
				else  {
					do {        	
						photoList.add(cursor.getString(0));
						//Log.i("cursor.getString(0)",cursor.getString(0));
						photoIdList.add(cursor.getString(1));
					} while (cursor.moveToNext());



					int x = photoList.size();
					photoArr = new String[x];
					photoArr = photoList.toArray(photoArr);
					Log.i("photoArr length", ""+photoArr.length);

					idArr = new String[x];
					idArr = photoIdList.toArray(idArr);



					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, photoList);
					setListAdapter(dataAdapter);
				}
				cursor.close();
			}
		}
		//If user details were not in db(in onCreate(), if data was not stored properly in db), check if online and fetch data from server.
		else if(isOnline())
		{
			Log.i("PhotoListFragment", "onResume. Display photo list from server");
			String userAgent = null;
			httpclient = AndroidHttpClient.newInstance(userAgent);
			task = new HttpClientTask();
			String url = "http://bismarck.sdsu.edu/photoserver/userphotos/" + userId;
			task.execute(url);
		}
	}

	public void onPause() {
		super.onPause();
		if(httpclient != null)
		{
			httpclient.getConnectionManager().shutdown();
			((AndroidHttpClient) httpclient).close();
			httpclient = null;
		}
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id){
		selectedPhoto = getListView().getItemAtPosition(position).toString();
		int size = photoArr.length;
		for(int i=0; i<size; i++)
		{
			if(photoArr[i].equals(selectedPhoto))
			{
				selectedId = idArr[i];
			}
		}

		//Calling onEditFinished method of the abstract class OnListItemSelectedListener on the activity object
		listListener.onListFinished(selectedId,idArr);
	}

	//http://stackoverflow.com/questions/17503422/how-to-implement-internet-check-in-asynctask-in-android
	// This method check internet connection. True for connection enabled, false otherwise.
	public boolean isOnline() {
		Log.i("inside isonline","inside isonline");
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View listView = inflater.inflate(R.layout.fragment_photo_list, container, false);
		networkStatus = (TextView) listView.findViewById(R.id.network_status);
		return listView;
	}

	public void passUserId(String id)
	{
		userId = id;
	}
}
