package edu.sdsu.cs.chinnu.multipleactivitiesproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class VersionListFragment extends ListFragment {

	private String selectedItem = null;
	private OnListItemSelectedListener listListener;

	public interface OnListItemSelectedListener {
		public void onListFinished(String selectedVersion);
	}

	@Override
	public void onAttach (Activity activity) {
		super.onAttach(activity);
		//Type casting activity object to OnListItemSelectedListener abstract class.
		listListener = (OnListItemSelectedListener) activity;
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id){
		selectedItem = getListView().getItemAtPosition(position).toString();
		//Calling onEditFinished method of the abstract class OnListItemSelectedListener on the activity object
		listListener.onListFinished(selectedItem);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View listView = inflater.inflate(R.layout.versionlist_fragment, container, false);
		return listView;
	}

	//Create and set an adapter from the data source ie list items stored in array android_versions in strings.xml
	//This brings the data from the xml to the java code and binds it to the listview.
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.android_versions, android.R.layout.simple_list_item_1);
		setListAdapter(adapter);
	}


}

