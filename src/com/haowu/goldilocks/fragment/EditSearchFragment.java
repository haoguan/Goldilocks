package com.haowu.goldilocks.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.haowu.goldilocks.OptionsActivity;
import com.haowu.goldilocks.R;
import com.haowu.goldilocks.adapter.LocationAutoCompleteAdapter;

public class EditSearchFragment extends Fragment implements OnItemClickListener {
	
	OnListViewLocationSelectListener mListViewLocationSelect;
	private LocationAutoCompleteAdapter adapter;
	private ListView locationList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view;
		if (getActivity().getClass().equals(OptionsActivity.class) ) {
			view = inflater.inflate(R.layout.edit_search_activity_no_padding, container, false);
		}
		else {
			view = inflater.inflate(R.layout.edit_search_activity, container, false);
		}
		
		locationList = (ListView) view.findViewById(R.id.lv_locations);
		
		adapter = new LocationAutoCompleteAdapter(view.getContext(), R.layout.location_search_item);
		locationList.setAdapter(adapter);
		locationList.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> g, View v, int pos, long id) {
		// TODO Auto-generated method stub
		String locationText = ((TextView) v.findViewById(R.id.tv_location_in_list)).getText().toString();
		//send edit text to parent activity
		mListViewLocationSelect.onListViewLocationSelect(locationText);
	}
	
	public void setAdapterFilter(CharSequence s) {
        adapter.getFilter().filter(s);
	}
	
	// must have home activity implement this method.
	public interface OnListViewLocationSelectListener {
		public void onListViewLocationSelect(String text);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListViewLocationSelect = (OnListViewLocationSelectListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement missing listeners");
		}
	}
}

