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
import android.database.sqlite.SQLiteDatabase;
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

public class UserListFragment extends ListFragment{

	private String selectedUser;
	private String selectedId;
	private OnListItemSelectedListener listListener;
	HttpClient httpclient;
	HttpClientTask task;
	DatabaseHandler handler;
	Context context;
	SQLiteDatabase db;
	String[] userArr;  
	String[] idArr;
	TextView networkStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();

		handler = new DatabaseHandler(context);
		handler.open();

		//Check if online and display user list from server.
		if(isOnline())
		{
			Log.i("onCreate","Display user list from server");
			String userAgent = null;
			httpclient = AndroidHttpClient.newInstance(userAgent);
			task = new HttpClientTask();
			String url = "http://bismarck.sdsu.edu/photoserver/userlist";
			task.execute(url);
		}
		//If not online, check if user details are available offline in db and display the user list.
		else
		{
			Log.i("onCreate", "Display user list from db");
			boolean tableExist = handler.isTableExists("userTable");

			Log.i("tableExit", ""+tableExist);
			List<String> userList = new ArrayList<String>();
			List<String> userIdList = new ArrayList<String>();

			if(tableExist)
			{
				Log.i("if", "if");
				Cursor cursor = handler.fetchUsers();
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						do {        	
							userList.add(cursor.getString(0));
							userIdList.add(cursor.getString(0));
						} while (cursor.moveToNext());

					}
				}
				cursor.close();

				//Storing user names and ids to class variables userArr and idArr respectively to use in onListItemClick().
				int x = userList.size();
				Log.i("x",""+x);
				userArr = new String[x];
				userArr = userList.toArray(userArr);
				Log.i("userArr length", ""+userArr.length);

				idArr = new String[x];
				idArr = userIdList.toArray(idArr);

				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userList);
				setListAdapter(dataAdapter);
			}		
		}
	}

	public interface OnListItemSelectedListener {
		public void onListFinished(String selectedId);
	}

	class HttpClientTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {
			try {
				ResponseHandler<String> responseHandler =
						new BasicResponseHandler();
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
				Log.i("toast","toast");
				networkStatus.setVisibility(View.VISIBLE);
				return;
			}*/
			if(result != null)
			{
				try {
					//Display the user list. Also save the user details in the db.
					handler.createUserTable();
					JSONArray data = new JSONArray(result);
					int size = data.length();
					userArr = new String[size];
					idArr = new String[size];
					for(int i=0; i<size; i++){
						JSONObject firstUser = (JSONObject) data.get(i);
						userArr[i] = firstUser.getString("name");
						idArr[i] = firstUser.getString("id");
						handler.insertUser(userArr[i], idArr[i]);
					}

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userArr);
					setListAdapter(dataAdapter);

				} catch (JSONException e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(context, "Error! Could not display the list!", Toast.LENGTH_LONG).show();
						}
					});

					//Toast.makeText(context, "Error occurred! Could not display the list.", Toast.LENGTH_LONG).show();
					Log.e("UserListFragment","An Exception occurred", e);
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

		Log.i("UserListFragment", "onResume. Display user list from db");

		//In onResume always display user list from db, if available.
		//If the user comes back to this screen after pressing Home button or navigating to next screen
		//(in which case onCreate() is not called, no need to fetch data from server.
		boolean tableExist = handler.isTableExists("userTable");

		Log.i("UserListFragment", "tableExit(): "+ tableExist);
		List<String> userList = new ArrayList<String>();
		List<String> userIdList = new ArrayList<String>();

		if(tableExist)
		{
			Cursor cursor = handler.fetchUsers();
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {        	
						//Log.i("cursor.getString(0)",""+cursor.getString(0));
						userList.add(cursor.getString(0));
						userIdList.add(cursor.getString(1));
					} while (cursor.moveToNext());

				}
			}
			cursor.close();

			int x = userList.size();
			userArr = new String[x];
			userArr = userList.toArray(userArr);
			Log.i("UserListFragment", "userArr.length: "+userArr.length);

			idArr = new String[x];
			idArr = userIdList.toArray(idArr);

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userList);
			setListAdapter(dataAdapter);
		}

		//If user details were not in db(in onCreate(), if data was not stored properly in db), check if online and fetch data from server.
		else if(isOnline())
		{
			Log.i("onResume", "Display user list from server");
			String userAgent = null;
			httpclient = AndroidHttpClient.newInstance(userAgent);
			task = new HttpClientTask();
			String url = "http://bismarck.sdsu.edu/photoserver/userlist";
			task.execute(url);
		}
		//handler.close();
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

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id){
		selectedUser = getListView().getItemAtPosition(position).toString();
		Log.i("selectedUser",selectedUser);
		int size = userArr.length;
		for(int i=0; i<size; i++)
		{
			Log.i("userArr[i]", userArr[i]);
			Log.i("idArr[i]", idArr[i]);
			if(userArr[i].equals(selectedUser))
			{
				selectedId = idArr[i];
				break;
			}
		}
		//Calling onEditFinished method of the abstract class OnListItemSelectedListener on the activity object
		listListener.onListFinished(selectedId);
	}

	//http://stackoverflow.com/questions/17503422/how-to-implement-internet-check-in-asynctask-in-android
	//This method check internet connection. True for connection enabled, false otherwise.
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
		Log.i("onCreateView", "onCreateView is called");
		View listView = inflater.inflate(R.layout.fragment_user_list, container, false);
		networkStatus = (TextView) listView.findViewById(R.id.network_status);
		return listView;
	}
}
