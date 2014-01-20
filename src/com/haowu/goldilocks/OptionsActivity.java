package com.haowu.goldilocks;

import java.util.ArrayList;

import com.haowu.goldilocks.fragment.EditSearchFragment;
import com.haowu.goldilocks.fragment.OptionsFromLocationFragment;
import com.haowu.goldilocks.fragment.EditSearchFragment.OnListViewLocationSelectListener;
import com.haowu.goldilocks.fragment.OptionsFromLocationFragment.OnFromSearchConditionsListener;
import com.haowu.goldilocks.fragment.OptionsFromLocationFragment.OnFromSearchXClickedListener;
import com.haowu.goldilocks.fragment.OptionsFromLocationFragment.OnFromTextChangedListener;
import com.haowu.goldilocks.fragment.OptionsToLocationFragment;
import com.haowu.goldilocks.fragment.OptionsToLocationFragment.OnToSearchConditionsListener;
import com.haowu.goldilocks.fragment.OptionsToLocationFragment.OnToSearchXClickedListener;
import com.haowu.goldilocks.fragment.OptionsToLocationFragment.OnToTextChangedListener;
import com.haowu.goldilocks.fragment.SearchFragment;
import com.haowu.goldilocks.vo.OPString;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class OptionsActivity extends Activity implements OnToSearchXClickedListener, OnToSearchConditionsListener, OnToTextChangedListener, 
	OnFromTextChangedListener, OnFromSearchConditionsListener, OnFromSearchXClickedListener, OnListViewLocationSelectListener {
	
	OptionsFromLocationFragment fromLocationFrag;
	OptionsToLocationFragment toLocationFrag;
	SearchFragment searchPageFrag;
	EditSearchFragment editSearchFrag;
	
	LinearLayout llOptionsSelect;
	LinearLayout llDirectionsType;
	
	//Direction Type
	ImageView ivCar;
	ImageView ivBus;
	ImageView ivBike;
	ImageView ivWalk;
	
	//Options
	CheckBox cbCafes;
	CheckBox cbBars;
	CheckBox cbLibraries;
	CheckBox cbParks;
	EditText etOther;
	
	//helper vars to pass to homeactivity
	String selectedType;
	String startLocation;
	String toLocation;
	
	ArrayList<String> checkedBoxes;
	ArrayList<OPString> opCheckedBoxes;
	
	Button bOptionsConfirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.options_activity);
		
		searchPageFrag = (SearchFragment) getFragmentManager().findFragmentById(R.id.options_search_page_frag);
		editSearchFrag = (EditSearchFragment) getFragmentManager().findFragmentById(R.id.options_edit_search_frag);
		fromLocationFrag = (OptionsFromLocationFragment) getFragmentManager().findFragmentById(R.id.options_frag_from_location);
		toLocationFrag = (OptionsToLocationFragment) getFragmentManager().findFragmentById(R.id.options_frag_destination);
		llOptionsSelect = (LinearLayout) findViewById(R.id.ll_options_filter_page);
		llDirectionsType = (LinearLayout) findViewById(R.id.ll_directions_type);
		
		ivCar = (ImageView) findViewById(R.id.iv_options_car);
		ivBus = (ImageView) findViewById(R.id.iv_options_bus);
		ivBike = (ImageView) findViewById(R.id.iv_options_bike);
		ivWalk = (ImageView) findViewById(R.id.iv_options_walk);
		
		cbCafes = (CheckBox) findViewById(R.id.cb_cafes);
		cbBars = (CheckBox) findViewById(R.id.cb_bars);
		cbLibraries = (CheckBox) findViewById(R.id.cb_libraries);
		cbParks = (CheckBox) findViewById(R.id.cb_parks);
		etOther = (EditText) findViewById(R.id.et_options_other);
		opCheckedBoxes = new ArrayList<OPString>();
		
		bOptionsConfirm = (Button) findViewById(R.id.b_options_confirm);
		
		ivCar.setOnClickListener(new DirectionTypeClickListener());
		ivBus.setOnClickListener(new DirectionTypeClickListener());
		ivBike.setOnClickListener(new DirectionTypeClickListener());
		ivWalk.setOnClickListener(new DirectionTypeClickListener());
		
		cbCafes.setOnCheckedChangeListener(new FilterOptionsCheckChangeListener());
		cbBars.setOnCheckedChangeListener(new FilterOptionsCheckChangeListener());
		cbLibraries.setOnCheckedChangeListener(new FilterOptionsCheckChangeListener());
		cbParks.setOnCheckedChangeListener(new FilterOptionsCheckChangeListener());
		
		bOptionsConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Grab all relevant data from options screen and return to HomeActivity.
				Intent i = new Intent();
				i.putExtra("directions_type", selectedType);
				i.putExtra("from_location", fromLocationFrag.getStringDest());
				if (fromLocationFrag.getStringDest().equals("")) {
					i.putExtra("use_curr_location", true);
				}
				else {
					i.putExtra("use_curr_location", false);
				}
				i.putExtra("to_location", toLocationFrag.getStringDest());
				//convert OPStrings to regular strings
				checkedBoxes =  new ArrayList<String>();
				for (int j = 0; j < opCheckedBoxes.size(); j++) {
					checkedBoxes.add(opCheckedBoxes.get(j).getStr());
				}
				i.putStringArrayListExtra("checked_boxes", checkedBoxes);
				i.putExtra("other_option", etOther.getText().toString());
				setResult(RESULT_OK, i);	
				finish();
			}
		});
		
		//set values from previous intent.
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		if (extras != null) {
			selectedType = extras.getString("directions_type"); 
			startLocation = extras.getString("from_location");
			toLocation = extras.getString("to_location");
			etOther.setText(extras.getString("other_option"));
			checkedBoxes = extras.getStringArrayList("check_boxes");
		}
		
		initLocations(startLocation, toLocation);
		initSelected(selectedType);
		initCheckBoxes(checkedBoxes);
	}
	
	/*** Back Button Behavior ***/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (llDirectionsType.getVisibility() == View.GONE && fromLocationFrag.getView().getVisibility() == View.VISIBLE) {
				revealMainOptions();
				toLocationFrag.getView().setVisibility(View.VISIBLE);
				fromLocationFrag.setXVisibility(View.GONE);
				fromLocationFrag.clearFocus();
				llDirectionsType.setVisibility(View.VISIBLE);
				return true;
			} else if (llDirectionsType.getVisibility() == View.GONE && toLocationFrag.getView().getVisibility() == View.VISIBLE) {
				revealMainOptions();
				fromLocationFrag.getView().setVisibility(View.VISIBLE);
				toLocationFrag.setXVisibility(View.GONE);
				toLocationFrag.clearFocus();
				llDirectionsType.setVisibility(View.VISIBLE);
			}
			else {
				super.onKeyDown(keyCode, event);
				return true;
			}
		}
		return false;
	}
	
	/*** ToTextChangedListener Functions ***/
	
	@Override
	public void onToSearchTextChanged(int count, CharSequence s) {
		// TODO Auto-generated method stub
		if (count > 0) {
			revealToSearchEditPage();
			toLocationFrag.setXVisibility(View.VISIBLE);
		} else if (count == 0) {
			revealToSearchPage();
			toLocationFrag.setXVisibility(View.GONE);
			fromLocationFrag.setXVisibility(View.GONE);
		}
		
		fromLocationFrag.getView().setVisibility(View.GONE);
		toLocationFrag.getView().setVisibility(View.VISIBLE);
		editSearchFrag.setAdapterFilter(s);
	}
	
	/*** ToSearchConditionsListener Functions ***/

	@Override
	public void onToSearchFocus() {
		// only worry about changing the layouts. The X visibility is handled within the fragment.
		revealToSearchPage();
		
		fromLocationFrag.getView().setVisibility(View.GONE);
		toLocationFrag.getView().setVisibility(View.VISIBLE);
		llDirectionsType.setVisibility(View.GONE);
	}

	@Override
	public void onToSearchInit() {
		revealMainOptions();
		fromLocationFrag.getView().setVisibility(View.VISIBLE);
		toLocationFrag.getView().setVisibility(View.VISIBLE);
		toLocationFrag.setXVisibility(View.GONE);
		llDirectionsType.setVisibility(View.VISIBLE);
		
	}
	
	/*** ToSearchXClickedListener Functions ***/


	@Override
	public void onToSearchXClicked() {
		// TODO Auto-generated method stub
		toLocationFrag.searchXClicked();
	}
	

	/*** FromSearchXClickedListener Functions ***/
	@Override
	public void onFromSearchXClicked() {
		// TODO Auto-generated method stub
		fromLocationFrag.searchXClicked();
	}
	
	
	/*** FromSearchConditionsListener Functions ***/

	@Override
	public void onFromSearchFocus() {
		revealFromSearchPage();
		toLocationFrag.getView().setVisibility(View.GONE);
		fromLocationFrag.getView().setVisibility(View.VISIBLE);
		llDirectionsType.setVisibility(View.GONE);
	}

	@Override
	public void onFromSearchInit() {
		revealMainOptions();
		
		fromLocationFrag.getView().setVisibility(View.VISIBLE);
		toLocationFrag.getView().setVisibility(View.VISIBLE);
		fromLocationFrag.setXVisibility(View.GONE);
		llDirectionsType.setVisibility(View.VISIBLE);
	}
	
	/*** FromSearchTextChangedListener Functions ***/

	@Override
	public void onFromSearchTextChanged(int count, CharSequence s) {
		// TODO Auto-generated method stub
		if (count > 0) {
			revealFromSearchEditPage();
			fromLocationFrag.setXVisibility(View.VISIBLE);

		} else if (count == 0) {
			revealFromSearchPage();
			toLocationFrag.setXVisibility(View.GONE);
			fromLocationFrag.setXVisibility(View.GONE);
		}
		
		toLocationFrag.getView().setVisibility(View.GONE);
		fromLocationFrag.getView().setVisibility(View.VISIBLE);
		editSearchFrag.setAdapterFilter(s);
	}
	
	/*** ListViewLocationSelectedListener Functions ***/

	@Override
	public void onListViewLocationSelect(String text) {
		// TODO Auto-generated method stub
		if (fromLocationFrag.getView().getVisibility() == View.VISIBLE) {
			fromLocationFrag.onListItemClick(text);
		}
		else {
			toLocationFrag.onListItemClick(text);
		}
	}
	
	//makes sure that we have the correct boldness at activity reset.
	private void setSelected(View v) {
		ivCar.setEnabled(!v.equals(ivCar));
		ivBus.setEnabled(!v.equals(ivBus));
		ivBike.setEnabled(!v.equals(ivBike));
		ivWalk.setEnabled(!v.equals(ivWalk));
		selectedType = v.getTag().toString();
	}
	
	private class DirectionTypeClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setSelected(v);
		}
		
	}
	
	private class FilterOptionsCheckChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				opCheckedBoxes.add(new OPString(buttonView.getText().toString()));
			}
			else {
				opCheckedBoxes.remove(new OPString(buttonView.getText().toString()));
			}
		}
		
	}
	
	private void initLocations(String startLocation, String endLocation) {
		fromLocationFrag.setStringDest(startLocation);
		toLocationFrag.setStringDest(endLocation);
	}
	
	private void initSelected(String type) {
		if (type.equals("driving")) {
			setSelected(ivCar);
		}
		else if (type.equals("bicycling")) {
			setSelected(ivBike);
		}
		else if (type.equals("transit")) {
			setSelected(ivBus);
		}
		else {
			setSelected(ivWalk);
		}
	}
	
	private void initCheckBoxes(ArrayList<String> cbs) {
		ArrayList<View> tagged; 
		for (int i = 0; i < cbs.size(); i++) {
			tagged = getViewsByTag(llOptionsSelect, cbs.get(i));
			CheckBox cb = (CheckBox)tagged.get(0);
			cb.setChecked(true);
//			((CheckBox)llOptionsSelect.findViewWithTag(cbs.get(i))).setChecked(true);
		}
	}
	
	private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
	    ArrayList<View> views = new ArrayList<View>();
	    final int childCount = root.getChildCount();
	    for (int i = 0; i < childCount; i++) {
	        final View child = root.getChildAt(i);
	        if (child instanceof ViewGroup) {
	            views.addAll(getViewsByTag((ViewGroup) child, tag));
	        }

	        final Object tagObj = child.getTag();
	        if (tagObj != null && tagObj.equals(tag)) {
	            views.add(child);
	        }

	    }
	    return views;
	}
	
	public int convertDPtoPixels(int sizeInDp) {
		float scale = getResources().getDisplayMetrics().density;
		return (int) (sizeInDp*scale + 0.5f);
	}
	
	public void revealToSearchEditPage() {
		LinearLayout.LayoutParams newSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newEditSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 9);
		LinearLayout.LayoutParams newOptionsSelectParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newDirectionsTypeParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newFromSearchBarParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newToSearchBarParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);

		searchPageFrag.getView().setLayoutParams(newSearchParams);
		editSearchFrag.getView().setLayoutParams(newEditSearchParams);
		llOptionsSelect.setLayoutParams(newOptionsSelectParams);
		llDirectionsType.setLayoutParams(newDirectionsTypeParams);
		fromLocationFrag.getView().setLayoutParams(newFromSearchBarParams);
		toLocationFrag.getView().setLayoutParams(newToSearchBarParams);
	}
	
	public void revealToSearchPage() {
		LinearLayout.LayoutParams newSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 9);
		LinearLayout.LayoutParams newEditSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newOptionsSelectParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newDirectionsTypeParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newFromSearchBarParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newToSearchBarParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);

		searchPageFrag.getView().setLayoutParams(newSearchParams);
		editSearchFrag.getView().setLayoutParams(newEditSearchParams);
		llOptionsSelect.setLayoutParams(newOptionsSelectParams);
		llDirectionsType.setLayoutParams(newDirectionsTypeParams);
		fromLocationFrag.getView().setLayoutParams(newFromSearchBarParams);
		toLocationFrag.getView().setLayoutParams(newToSearchBarParams);
	}
	
	public void revealMainOptions() {
		// do not search here. just store it in edit text.
		LinearLayout.LayoutParams newSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newEditSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newDirectionsTypeParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
		LinearLayout.LayoutParams newOptionsSelectParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 7);
//		newOptionsSelectParams.setMargins((int)getResources().getDimension(R.dimen.options_padding), (int)getResources().getDimension(R.dimen.options_padding), 
//				(int)getResources().getDimension(R.dimen.options_padding), (int)getResources().getDimension(R.dimen.options_padding));
		newOptionsSelectParams.setMargins(convertDPtoPixels(15), convertDPtoPixels(15), convertDPtoPixels(15), convertDPtoPixels(15));
		LinearLayout.LayoutParams newFromSearchBarParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
		LinearLayout.LayoutParams newToSearchBarParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);

		searchPageFrag.getView().setLayoutParams(newSearchParams);
		editSearchFrag.getView().setLayoutParams(newEditSearchParams);
		llOptionsSelect.setLayoutParams(newOptionsSelectParams);
		llDirectionsType.setLayoutParams(newDirectionsTypeParams);
		fromLocationFrag.getView().setLayoutParams(newFromSearchBarParams);
		toLocationFrag.getView().setLayoutParams(newToSearchBarParams);
	}
	
	public void revealFromSearchPage() {
		// only worry about changing the layouts. The X visibility is handled within the fragment.
		LinearLayout.LayoutParams newSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 9);
		LinearLayout.LayoutParams newEditSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newOptionsSelectParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newDirectionsTypeParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newFromSearchBarParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
		LinearLayout.LayoutParams newToSearchBarParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);

		searchPageFrag.getView().setLayoutParams(newSearchParams);
		editSearchFrag.getView().setLayoutParams(newEditSearchParams);
		llOptionsSelect.setLayoutParams(newOptionsSelectParams);
		llDirectionsType.setLayoutParams(newDirectionsTypeParams);
		fromLocationFrag.getView().setLayoutParams(newFromSearchBarParams);
		toLocationFrag.getView().setLayoutParams(newToSearchBarParams);
	}
	
	public void revealFromSearchEditPage() {
		LinearLayout.LayoutParams newSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newEditSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 9);
		LinearLayout.LayoutParams newOptionsSelectParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newDirectionsTypeParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newFromSearchBarParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
		LinearLayout.LayoutParams newToSearchBarParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);

		searchPageFrag.getView().setLayoutParams(newSearchParams);
		editSearchFrag.getView().setLayoutParams(newEditSearchParams);
		llOptionsSelect.setLayoutParams(newOptionsSelectParams);
		llDirectionsType.setLayoutParams(newDirectionsTypeParams);
		fromLocationFrag.getView().setLayoutParams(newFromSearchBarParams);
		toLocationFrag.getView().setLayoutParams(newToSearchBarParams);
	}


}
