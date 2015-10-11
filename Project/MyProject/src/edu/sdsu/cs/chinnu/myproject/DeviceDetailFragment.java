/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.sdsu.cs.chinnu.myproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import edu.sdsu.cs.chinnu.myproject.DeviceListFragment.DeviceActionListener;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	private View mContentView = null;
	private WifiP2pDevice device;
	private static WifiP2pInfo info;
	ProgressDialog progressDialog = null;
	boolean isGroupOwner = true;
	boolean isClient = true;
	static boolean alreadySent = false;
	static ServerSocket serverSocket = null;
	static Socket client;
	FileServerAsyncTask task;
	static String address;
	static String filePath;
	static String TAG = "DeviceDetailFragment";
	boolean isConnected;

	public void setGroupOwnerStatus(boolean isGroupOwner)
	{
		this.isGroupOwner = isGroupOwner;
	}

	public void setClientStatus(boolean isClient)
	{
		this.isClient = isClient;
	}

	public void setFilePath(String filePath)
	{
		DeviceDetailFragment.filePath = filePath;
	}

	public void setConnectionStatus(boolean isConnected)
	{
		this.isConnected = isConnected;
		if(isConnected == false)
		{
			Log.d(DeviceDetailFragment.TAG, "alreadySent in set "+alreadySent);
			alreadySent = false;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			// Restore last state for checked position.
			alreadySent= savedInstanceState.getBoolean("alreadySent");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.d(DeviceDetailFragment.TAG, "onCreateView isalreadysent" + alreadySent);

		mContentView = inflater.inflate(R.layout.device_detail, null);
		
		// Connect action.
		mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;
				config.wps.setup = WpsInfo.PBC;
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
						"Connecting to :" + device.deviceAddress, true, true
						//                        new DialogInterface.OnCancelListener() {
						//
						//                            @Override
						//                            public void onCancel(DialogInterface dialog) {
						//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
						//                            }
						//                        }
						);
				((DeviceActionListener) getActivity()).connect(config);

			}
		});

		// Disconnect action.
		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						try
						{
							if(serverSocket != null)
							{
								serverSocket.close();
							}
						}catch (IOException e) {
							Log.e(DeviceDetailFragment.TAG, e.getMessage());
						}
						((DeviceActionListener) getActivity()).disconnect();
					}
				});

		// Send action.
		mContentView.findViewById(R.id.btn_send).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						// Remove Send button from UI.
						mContentView.findViewById(R.id.btn_send).setVisibility(View.GONE);


						File videoFile = new File(filePath);
						Uri uri = Uri.fromFile(videoFile);

						TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
						statusText.setText("Sending: " + uri);
						Log.d(DeviceDetailFragment.TAG, "Intent----------- " + uri);

						// If not GroupOwner, call FileTransferService.ACTION_SEND_FILE.
						if(!info.isGroupOwner)
						{
							Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
							serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
							serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
							getActivity().startService(serviceIntent);
						}
						
						// If GroupOwner, call sendFile function from async task.
						else
						{
							Log.d(DeviceDetailFragment.TAG, "task.sendFile");
							task.sendFile(uri.toString());
						}

						statusText.setText("File sent: " + uri);

						Toast.makeText(getActivity(), "File sent.", Toast.LENGTH_SHORT).show();
					}
				});

		return mContentView;
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putBoolean("alreadySent", alreadySent);
		super.onSaveInstanceState(savedInstanceState);
	}
	

	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		DeviceDetailFragment.info = info;
		DeviceDetailFragment.address = info.groupOwnerAddress.getHostAddress();      
		Log.d(DeviceDetailFragment.TAG, "address:"+ address);

		this.getView().setVisibility(View.VISIBLE);

		// The owner IP is now known.
		TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(getResources().getString(R.string.group_owner_text)
				+ ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
						: getResources().getString(R.string.no)));

		// InetAddress from WifiP2pInfo struct.
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

		// After the group negotiation, we assign the group owner as the file
		// server. The file server is single threaded, single connection server
		// socket.
		Log.d(DeviceDetailFragment.TAG, "alreadysent: "+alreadySent);
		
		// Execute below code if it is the first send operation.
		if(!alreadySent)
		{
			boolean asyncTaskCalled = false;
			alreadySent = true;
			
			//If GroupOwner, call async task.
			if (info.groupFormed && info.isGroupOwner) {
				Log.d(DeviceDetailFragment.TAG, "before async task call");

				//No need to call async task, it is already called once.
				if(!asyncTaskCalled)
				{
					task = new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text));
					task.update(isClient, mContentView, filePath);
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					Log.d(DeviceDetailFragment.TAG, " async task is called");
					asyncTaskCalled = true;
				}

			} 
			
			// Non GroupOwner always starts the connection.
			else if (info.groupFormed) {
				Log.d(DeviceDetailFragment.TAG, "inside 'else if (info.groupFormed)'");

				Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
				serviceIntent.setAction(FileTransferService.ACTION_CONNECT_SOCKET);			
				serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
				serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
				getActivity().startService(serviceIntent);

				Log.d(DeviceDetailFragment.TAG, "after getact start service");

				// For client, enable Send button.	
				if(isClient)
				{							
					mContentView.findViewById(R.id.btn_send).setVisibility(View.VISIBLE);
					((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
							.getString(R.string.client_text));

				}
				
				// Else receive file.
				else
				{
					Intent serviceIntent1 = new Intent(getActivity(), FileTransferService.class);
					serviceIntent1.setAction(FileTransferService.ACTION_RECEIVE_FILE);
					getActivity().startService(serviceIntent1);
				}
			}
		}
		
		// Hide the connect button
		Log.d(DeviceDetailFragment.TAG, "alreadysent: "+alreadySent);
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
	}


	/**
	 * Updates the UI with device data
	 * 
	 * @param device the device to be displayed
	 */
	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(device.toString());

	}

	/**
	 * Clears the UI fields after a disconnect or direct mode disable operation.
	 */
	public void resetViews() {
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		mContentView.findViewById(R.id.btn_send).setVisibility(View.GONE);
		this.getView().setVisibility(View.GONE);
	}

	/**
	 * A simple server socket that accepts connection and writes some data on
	 * the stream.
	 */
	public static class FileServerAsyncTask extends AsyncTask<Void, Void, String>  {

		private Context context;
		private TextView statusText;
		boolean isClient;
		View  mContentView;
		String filePath;

		/**
		 * @param context
		 * @param statusText
		 */
		public FileServerAsyncTask(Context context, View statusText) {
			this.context = context;
			this.statusText = (TextView) statusText;

		}

		public void update(boolean isClient, View  mContentView, String filePath)
		{
			Log.d(FileTransferService.TAG, "FileServerAsyncTask - update");
			this.isClient= isClient;
			this.mContentView = mContentView;
			this.filePath = filePath;
			Log.d(DeviceDetailFragment.TAG, "filePath: " + filePath);

		}

		// Function for sending file.
		public void sendFile(String fileUri)
		{
			try {
				Log.d(DeviceDetailFragment.TAG, "Client socket - " + client.isConnected());
				OutputStream stream = client.getOutputStream();
				ContentResolver cr = context.getContentResolver();
				InputStream is = null;
				try {
					is = cr.openInputStream(Uri.parse(fileUri));
				} catch (FileNotFoundException e) {
					Log.d(DeviceDetailFragment.TAG, e.toString());
				}

				DeviceDetailFragment.copyFile(is, stream);
				Log.d(DeviceDetailFragment.TAG, "Client: Data written");
			} catch (IOException e) {
				Log.e(FileTransferService.TAG, e.getMessage());
			} 
		}

		@Override
		protected String doInBackground(Void... params) {

			Log.d(DeviceDetailFragment.TAG, "doInBackground");
			
			try {
				serverSocket = new ServerSocket(8988);

				Log.d(DeviceDetailFragment.TAG, "Server: Socket opened");
				client = serverSocket.accept();

				Log.d(DeviceDetailFragment.TAG, "isClient "+isClient);

				// If client, nothing more do do here.
				if(isClient)
				{	
					return null;
				}
				
				
				// Not client which means this is receiving end.
				// File receiving code
				else
				{	
					Log.d(DeviceDetailFragment.TAG, "Server: connection done");
					String timeStamp = new SimpleDateFormat("yyyyMMdd_hhmmss", java.util.Locale.getDefault()).format(new Date());
					String mRootFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
					String videoFilePath= mRootFolder + "/MyProject/Video/video_" + timeStamp + ".mp4";
					final File f = new File(videoFilePath);

					File dirs = new File(f.getParent());
					if (!dirs.exists())
						dirs.mkdirs();
					f.createNewFile();

					Log.d(DeviceDetailFragment.TAG, "server: copying files " + f.toString());
					InputStream inputstream = client.getInputStream();
					try{
						copyFile(inputstream, new FileOutputStream(f));
					}
					catch(Exception e)
					{
						f.delete();
						e.printStackTrace(); 

					}

					client.close();
					serverSocket.close();
					Log.d(DeviceDetailFragment.TAG, "f.getAbsolutePath():" + f.getAbsolutePath());
					return f.getAbsolutePath();
				}
			} catch (IOException e) {
				Log.e(DeviceDetailFragment.TAG, e.getMessage());
				return null;
			}
			catch (Exception e) {
				Log.e(DeviceDetailFragment.TAG, e.getMessage());
				return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			Log.d(DeviceDetailFragment.TAG, "onPostExecute");
			
			// Play Video if it is received
			if (result != null) {

				statusText.setText("File copied - " + result);
				Intent toVideoPlayer = new Intent(context, VideoPlayerActivity.class);
				toVideoPlayer.putExtra("videoFile", result);
				context.startActivity(toVideoPlayer);

			}
			
			else
			{
				//If GroupOwner s the client, display Send button.
				if(isClient)
				{
					mContentView.findViewById(R.id.btn_send).setVisibility(View.VISIBLE);
					((TextView) mContentView.findViewById(R.id.status_text)).setText(context.getResources()
							.getString(R.string.client_text));
				}
			}

		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			statusText.setText("Opening a server socket");
		}

	}

	
	//Function for copying file.
	public static boolean copyFile(InputStream inputStream, OutputStream out) {
		Log.d(DeviceDetailFragment.TAG, "inside copyile");
		byte buf[] = new byte[1024];
		int len;

		Log.d(DeviceDetailFragment.TAG, "InputStream : " + inputStream);
		Log.d(DeviceDetailFragment.TAG, "OutputStream : " + out);

		try {
			while ((len = inputStream.read(buf)) != -1) {
				//Log.d(DeviceDetailFragment.TAG, "inside copyfile while");
				out.write(buf, 0, len);
			}
			out.close();
			inputStream.close();
			Log.d(DeviceDetailFragment.TAG, "end of copyile");
		} catch (IOException e) {
			Log.d(DeviceDetailFragment.TAG, e.toString());
			return false;
		}
		return true;
	}

}
