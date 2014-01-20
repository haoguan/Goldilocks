package com.haowu.goldilocks.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.haowu.goldilocks.R;

public class SearchBarFragment extends Fragment{
	
	OnSearchXClickedListener mSearchXClickedListener;
	OnSearchConditionsListener mSearchConditionsListener;
	OnTextChangedListener mTextChangedListener;
	OnOptionsClickedListener mOptionsClickedListener;
	OnListViewClickedListener mListViewClickedListener;

	EditText etSearchBar;
	Button bSearchX;
	Button bSearchOptions;
	Button bSearchListView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_bar_activity, container, false);

		etSearchBar = (EditText) view.findViewById(R.id.et_search_bar);
		bSearchOptions = (Button) view.findViewById(R.id.b_search_bar_options);
		bSearchX = (Button) view.findViewById(R.id.b_search_bar_x);
		bSearchListView = (Button) view.findViewById(R.id.b_search_bar_list);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		etSearchBar.addTextChangedListener(filterTextWatcher);

		etSearchBar.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean onChange) {
				// TODO Auto-generated method stub
				if (onChange) {
					mSearchConditionsListener.onSearchFocus();

					// add X button if necessary and remove options.
					bSearchOptions.setVisibility(View.GONE);
					bSearchListView.setVisibility(View.GONE);
					if (!etSearchBar.getText().toString().equals("")) {
						bSearchX.setVisibility(View.VISIBLE);
					}
					
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(etSearchBar, InputMethodManager.SHOW_IMPLICIT);
				}
			}
		});

		etSearchBar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (!etSearchBar.getText().toString().equals("")) {
						mSearchConditionsListener.onSearchInit();
					}

					etSearchBar.clearFocus();

					// hide the softkeyboard manually after focus has been cleared.
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(etSearchBar.getWindowToken(), 0);
				}

				return false;
			}
		});

		bSearchX.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSearchXClickedListener.onSearchXClicked();
			}
		});
		
		bSearchOptions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mOptionsClickedListener.onOptionsClicked();
			}
		});
		
		bSearchListView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mListViewClickedListener.onListViewClicked();
			}
		});
	}
	
	private TextWatcher filterTextWatcher = new TextWatcher() {

	    public void afterTextChanged(Editable s) {
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count,
	            int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before,
	            int count) {
	    	mTextChangedListener.onSearchTextChanged(count, s);
	    }

	};
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    etSearchBar.removeTextChangedListener(filterTextWatcher);
	}

	public void onListItemClick(String text) {
		//set edit text to text.
		etSearchBar.setText(text);
		if (!text.equals("")) {
			mSearchConditionsListener.onSearchInit();
		}

		etSearchBar.clearFocus();

		// hide the softkeyboard manually after focus has been cleared.
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etSearchBar.getWindowToken(), 0);
	}
	
	//activity calls this function to delete text from etSearch.
	public void searchXClicked() {
		etSearchBar.setText("");
	}
	
	
	//must have home activity implement this method.
	public interface OnTextChangedListener {
		public void onSearchTextChanged(int count, CharSequence s);
	}

	// must have home activity implement this method.
	public interface OnSearchXClickedListener {
		public void onSearchXClicked();
	}
	
	// must have home activity implement this method.
	public interface OnOptionsClickedListener {
		public void onOptionsClicked();
	}
	
	// must have home activity implement this method.
	public interface OnListViewClickedListener {
		public void onListViewClicked();
	}
	
	// must have home activity implement this method.
	public interface OnSearchConditionsListener {
		public void onSearchFocus();
		public void onSearchInit();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mSearchXClickedListener = (OnSearchXClickedListener) activity;
			mSearchConditionsListener = (OnSearchConditionsListener) activity;
			mTextChangedListener = (OnTextChangedListener) activity;
			mOptionsClickedListener = (OnOptionsClickedListener) activity;
			mListViewClickedListener = (OnListViewClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement missing listeners");
		}
	}
	
	//takes View.Visible and others.
	public void setXVisibility(int visibility) {
		bSearchX.setVisibility(visibility);
	}
	
	public void setOptionsVisibility(int visibility) {
		bSearchOptions.setVisibility(visibility);
	}
	
	public void setListViewVisibility(int visibility) {
		bSearchListView.setVisibility(visibility);
	}
	
	public String getStringDest() {
		return etSearchBar.getText().toString();
	}
	
	public void setStringDest(String dest) {
		etSearchBar.setText(dest);
	}
	
	public void setBarPadding(int right) {
		etSearchBar.setPadding(convertDPtoPixels(10), convertDPtoPixels(10), convertDPtoPixels(right), convertDPtoPixels(10));
	}
	
	public int convertDPtoPixels(int sizeInDp) {
		float scale = getResources().getDisplayMetrics().density;
		return (int) (sizeInDp*scale + 0.5f);
	}
	
	public void clearFocus() {
		etSearchBar.clearFocus();
	}

}