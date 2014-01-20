package com.haowu.goldilocks.fragment;

import com.haowu.goldilocks.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class OptionsFromLocationFragment extends Fragment{
	
	EditText etFromSearchBar;
	Button bFromSearchX;
	
	OnFromSearchXClickedListener mFromSearchXClickedListener;
	OnFromSearchConditionsListener mFromSearchConditionsListener;
	OnFromTextChangedListener mFromTextChangedListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.options_from_search_activity, container, false);

		etFromSearchBar = (EditText) view.findViewById(R.id.et_from_search_bar);
		bFromSearchX = (Button) view.findViewById(R.id.b_from_search_bar_x);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		etFromSearchBar.addTextChangedListener(filterTextWatcher);
		
		etFromSearchBar.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean onChange) {
				// TODO Auto-generated method stub
				if (onChange) {
					mFromSearchConditionsListener.onFromSearchFocus();

					if (!etFromSearchBar.getText().toString().equals("")) {
						bFromSearchX.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		etFromSearchBar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mFromSearchConditionsListener.onFromSearchInit();

					etFromSearchBar.clearFocus();

					// hide the softkeyboard manually after focus has been cleared.
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(etFromSearchBar.getWindowToken(), 0);
				}

				return false;
			}
		});

		bFromSearchX.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mFromSearchXClickedListener.onFromSearchXClicked();
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
	    	mFromTextChangedListener.onFromSearchTextChanged(count, s);
	    }

	};
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    etFromSearchBar.removeTextChangedListener(filterTextWatcher);
	}

	public void onListItemClick(String text) {
		// TODO Auto-generated method stub
		//set edit text to text.
		etFromSearchBar.setText(text);
		mFromSearchConditionsListener.onFromSearchInit();
		
		etFromSearchBar.clearFocus();

		// hide the softkeyboard manually after focus has been cleared.
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etFromSearchBar.getWindowToken(), 0);
	}
	
	//activity calls this function to delete text from etSearch.
	public void searchXClicked() {
		etFromSearchBar.setText("");
	}
	
	
	//must have home activity implement this method.
	public interface OnFromTextChangedListener {
		public void onFromSearchTextChanged(int count, CharSequence s);
	}

	// must have home activity implement this method.
	public interface OnFromSearchXClickedListener {
		public void onFromSearchXClicked();
	}
	
	// must have home activity implement this method.
	public interface OnFromSearchConditionsListener {
		public void onFromSearchFocus();
		public void onFromSearchInit();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mFromSearchXClickedListener = (OnFromSearchXClickedListener) activity;
			mFromSearchConditionsListener = (OnFromSearchConditionsListener) activity;
			mFromTextChangedListener = (OnFromTextChangedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement missing listeners");
		}
	}
	
	//takes View.Visible and others.
	public void setXVisibility(int visibility) {
		bFromSearchX.setVisibility(visibility);
	}
	
	public String getStringDest() {
		return etFromSearchBar.getText().toString();
	}
	
	public void setStringDest(String dest) {
		etFromSearchBar.setText(dest);
	}
	
	public void clearFocus() {
		etFromSearchBar.clearFocus();
	}
	

}
