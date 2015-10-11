package edu.sdsu.cs.chinnu.myproject;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class VideoPlaylistFragment extends ListFragment {

	public static final String TAG = "VideoPlaylistFragment";
	private String mRootFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
	private String mFolder = mRootFolder + "/MyProject/Video/";
	private OnListItemSelectedListener listListener;
	private ArrayList<String> fileArr = new ArrayList<String>();
	private ListView videoList;
	private ImageView imageThumbnail;
	private MyThumbnailAdapter dataAdapter;
	private LinearLayout layoutProgressBar;
	private int listItemsCount = 0;
	private int filesCount = 0;
	private String selectedAction;
	private static int pDialogCounter = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_video_playlist, container, false);

		videoList = (ListView) view.findViewById(android.R.id.list);

		// Register a context menu for the listview.
		registerForContextMenu(videoList);

		layoutProgressBar = (LinearLayout) view.findViewById(R.id.layoutProgressBar);

		

		// Create an array of video files to pass to the list adapter.
		File folder = new File(mFolder);
		File[] listOfFiles = folder.listFiles(); 

		String fileName;
		filesCount = listOfFiles.length;

		Log.d("listOfFiles.length", ""+listOfFiles.length);
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			fileName = listOfFiles[i].getName();



			if(fileName.endsWith(".mp4"))
			{

				if(listOfFiles[i].length() == 0)
				{
					listOfFiles[i].delete();
					filesCount--;
				}
				else
				{
					Log.d("fileArr item added", fileName);
					fileArr.add(fileName);
				}

			}
		}     

		Log.d("fileArr length", ""+fileArr.size());

		// Adapter for the listview.
		dataAdapter = new MyThumbnailAdapter(getActivity(), R.layout.row, fileArr);
		
		// Assign adapter to the listview.
		videoList.setAdapter(dataAdapter);
		
		
		// If the play list is empty, show appropriate message.
		if(dataAdapter.isEmpty())
		{
			View empty = view.findViewById(android.R.id.empty);
			videoList.setEmptyView(empty);
		}
		
		return view;
	}

	
	public class MyThumbnailAdapter extends ArrayAdapter<String>{

		public MyThumbnailAdapter(Context context, int textViewResourceId, ArrayList<String> arrList) {
			super(context, textViewResourceId, arrList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(VideoPlaylistFragment.TAG, "Inside getView");

			View row = convertView;
			if(row==null){
				LayoutInflater inflater=getActivity().getLayoutInflater();
				row=inflater.inflate(R.layout.row, parent, false);
			}

			TextView textfilePath = (TextView)row.findViewById(R.id.FileName);
			TextView textfileSize = (TextView)row.findViewById(R.id.FileSize);
			imageThumbnail = (ImageView)row.findViewById(R.id.Thumbnail);
			
			textfilePath.setText(fileArr.get(position));
			
			String mRootFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
			String mFolder = mRootFolder + "/MyProject/Video/";
			String filePath = mFolder + fileArr.get(position);
			File videoFile = new File(filePath);
					
			String size = String.format(Locale.getDefault(), "%.2f", (double)(videoFile.length()/((double)1024 * (double)1024)));	
			textfileSize.setText(" " + size + "MB");
			
			imageThumbnail.setTag(filePath);

			listItemsCount=0;
			new loadThumbnail(imageThumbnail).execute(filePath);

			return row;
		}
	}

	
	class loadThumbnail extends AsyncTask<Object, Void, Bitmap> {

		ProgressDialog dialog;
		private ImageView imv;
		private String path;

		public loadThumbnail(ImageView imv) {
			this.imv = imv;
			this.path = imv.getTag().toString();
			Log.d(VideoPlaylistFragment.TAG, "Inside AsyncTask. File: " + path);
		}


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			layoutProgressBar.setVisibility(View.VISIBLE);
			videoList.setVisibility(View.GONE);
			Log.d(VideoPlaylistFragment.TAG, "Inside onPreExecute()");
			
			 ++pDialogCounter;
		}

		protected Bitmap doInBackground(Object... params) {

			Bitmap bmThumbnail;
			bmThumbnail = ThumbnailUtils.createVideoThumbnail(path, Thumbnails.MICRO_KIND);
			Log.d(VideoPlaylistFragment.TAG, "Inside doInBackground(). Path: " + path);

			return bmThumbnail;
		}

		protected void onPostExecute(Bitmap result) {

			if (!imv.getTag().toString().equals(path)) {
				/* The path is not same. This means that this
	                     image view is handled by some other async task. 
	                     We don't do anything and return. */
				return;
			}

			if(result != null && imv != null){
				Log.d(VideoPlaylistFragment.TAG, "result");
				imv.setImageBitmap(result);
			}

			listItemsCount++;

			Log.d(VideoPlaylistFragment.TAG, "listItemsCount:" +listItemsCount);
			Log.d(VideoPlaylistFragment.TAG, "filesCount:" +filesCount);

			//if(listItemsCount == filesCount)
			//if ((listItemsCount == filesCount) || (listItemsCount == 7))
			if(--pDialogCounter == 0)
			{
				layoutProgressBar.setVisibility(View.GONE);
				videoList.setVisibility(View.VISIBLE);

			}

			Log.d(VideoPlaylistFragment.TAG, "listItemsCount:" +listItemsCount);
			Log.d(VideoPlaylistFragment.TAG, "filesCount:" +filesCount);
			Log.d(VideoPlaylistFragment.TAG, "Inside onPostExecute()");
		}
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = getActivity().getMenuInflater();
		
		// Inflate the context menu.
		inflater.inflate(R.menu.context_menu_video, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = (int) info.id; 
		String selectedFileName = getListView().getItemAtPosition(position).toString();
		String mFilePath = mFolder + selectedFileName;

		Log.d(VideoPlaylistFragment.TAG,"item.getItemId():"+item.getItemId());

		switch (item.getItemId()){

		// Action for Play context menu.	
		case R.id.context_menu_play:
			listListener = (OnListItemSelectedListener) getActivity();
			selectedAction = "play";
			listListener.onListFinished(mFilePath, selectedAction);
			
			return true;

		// Action for Delete context menu.		
		case R.id.context_menu_delete:
			File videoFile = new File (mFilePath); 
			videoFile.delete();

			fileArr.remove(position);
			filesCount--;
			ArrayAdapter<String> adapter = (ArrayAdapter<String>) videoList.getAdapter();
			adapter.notifyDataSetChanged(); 

			return true;

		// Action for Share context menu.	
		case R.id.context_menu_share:
			listListener = (OnListItemSelectedListener) getActivity();
			selectedAction = "share";
			listListener.onListFinished(mFilePath, selectedAction);	  			

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void onListItemClick(ListView lv, View v, int position, long id){
		String selectedFileName = getListView().getItemAtPosition(position).toString();
		String mFilePath = mRootFolder + "/MyProject/Video/" + selectedFileName;

		// Passing the selected audio file to the activity through OnListItemSelectedListener interface's onListFinished method.
		listListener = (OnListItemSelectedListener) getActivity();
		selectedAction = "play";
		listListener.onListFinished(mFilePath, selectedAction);
	}

	
	// OnListItemSelectedListener interface declaration.
	public interface OnListItemSelectedListener {
		public void onListFinished(String selectedFile, String action);
	}
	
}
