package edu.sdsu.cs.chinnu.myproject;

import java.io.File;
import java.util.ArrayList;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ListView;

public class AudioPlaylistFragment extends ListFragment {

	public static final String TAG = "AudioPlaylistFragment";
	private String mRootFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
	private String mFolder = mRootFolder + "/MyProject/Audio/";
	private OnListItemSelectedListener listListener;
	ArrayList<String> fileArr = new ArrayList<String>();
	ListView audioList;
	ArrayAdapter<String> dataAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_audio_playlist, container, false);

		audioList = (ListView) view.findViewById(android.R.id.list);

		// Register a context menu for the listview.
		registerForContextMenu(audioList);	

		// Create an array of audio files to pass to the list adapter.
		File folder = new File(mFolder);
		File[] listOfFiles = folder.listFiles(); 

		String fileName;

		for (int i = 0; i < listOfFiles.length; i++) 
		{
			fileName = listOfFiles[i].getName();
			if(fileName.endsWith(".3gp"))
			{
				fileArr.add(fileName);
			}
		}     

		// Adapter for the listview.
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, fileArr);
		
		// Assign adapter to the listview.
		audioList.setAdapter(dataAdapter);

		// If the play list is empty, show appropriate message.
		if(dataAdapter.isEmpty())
		{
			View empty = view.findViewById(android.R.id.empty);
			audioList.setEmptyView(empty);
		}
		
		return view;
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = getActivity().getMenuInflater();
		
		// Inflate the context menu.
		inflater.inflate(R.menu.context_menu_audio, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		Log.d(AudioPlaylistFragment.TAG,""+item.getItemId());

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = (int) info.id; 	
		String selectedFileName = getListView().getItemAtPosition(position).toString();
		Log.d(AudioPlaylistFragment.TAG, selectedFileName);
		
		String mFilePath = mFolder + selectedFileName;
		
		switch (item.getItemId()) {

		// Action for Play context menu.
		case R.id.context_menu_play:
			listListener = (OnListItemSelectedListener) getActivity();
			listListener.onListFinished(mFilePath);
			
			return true;

		// Action for Delete context menu.	
		case R.id.context_menu_delete:
			File audioFile = new File (mFilePath); 
			audioFile.delete();
			fileArr.remove(position);
			
			ArrayAdapter<String> adapter = (ArrayAdapter<String>) audioList.getAdapter();
			adapter.notifyDataSetChanged(); 

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id){
		String selectedFileName = getListView().getItemAtPosition(position).toString();		
		String mFilePath = mFolder + selectedFileName;

		// Passing the selected audio file to the activity through OnListItemSelectedListener interface's onListFinished method.
		listListener = (OnListItemSelectedListener) getActivity();
		listListener.onListFinished(mFilePath);
	}

	
	// OnListItemSelectedListener interface declaration.
	public interface OnListItemSelectedListener {
		public void onListFinished(String selectedFile);
	}

}
