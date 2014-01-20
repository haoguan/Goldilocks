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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.haowu.goldilocks.R;

public class OptionsToLocationFragment extends Fragment {
	
	OnToSearchXClickedListener mToSearchXClickedListener;
	OnToSearchConditionsListener mToSearchConditionsListener;
	OnToTextChangedListener mToTextChangedListener;
	
	EditText etToSearchBar;
	Button bToSearchX;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.options_to_search_activity, container, false);

		etToSearchBar = (EditText) view.findViewById(R.id.et_to_search_bar);
		bToSearchX = (Button) view.findViewById(R.id.b_to_search_bar_x);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		etToSearchBar.addTextChangedListener(filterTextWatcher);
		
		etToSearchBar.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean onChange) {
				// TODO Auto-generated method stub
				if (onChange) {
					mToSearchConditionsListener.onToSearchFocus();

					// add X button if necessary
					if (!etToSearchBar.getText().toString().equals("")) {
						bToSearchX.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		etToSearchBar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mToSearchConditionsListener.onToSearchInit();

					etToSearchBar.clearFocus();

					// hide the softkeyboard manually after focus has been cleared.
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(etToSearchBar.getWindowToken(), 0);
				}

				return false;
			}
		});
		
		bToSearchX.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mToSearchXClickedListener.onToSearchXClicked();
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
	    	mToTextChangedListener.onToSearchTextChanged(count, s);
	    }

	};
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    etToSearchBar.removeTextChangedListener(filterTextWatcher);
	}

	public void onListItemClick(String text) {
		// TODO Auto-generated method stub
		//set edit text to text.
		etToSearchBar.setText(text);
		mToSearchConditionsListener.onToSearchInit();
		
		etToSearchBar.clearFocus();

		// hide the softkeyboard manually after focus has been cleared.
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etToSearchBar.getWindowToken(), 0);
	}
	
	//activity calls this function to delete text from etSearch.
	public void searchXClicked() {
		etToSearchBar.setText("");
	}
	
	
	//must have home activity implement this method.
	public interface OnToTextChangedListener {
		public void onToSearchTextChanged(int count, CharSequence s);
	}

	// must have home activity implement this method.
	public interface OnToSearchXClickedListener {
		public void onToSearchXClicked();
	}
	
	// must have home activity implement this method.
	public interface OnToSearchConditionsListener {
		public void onToSearchFocus();
		public void onToSearchInit();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mToSearchXClickedListener = (OnToSearchXClickedListener) activity;
			mToSearchConditionsListener = (OnToSearchConditionsListener) activity;
			mToTextChangedListener = (OnToTextChangedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement missing listeners");
		}
	}
	
	//takes View.Visible and others.
	public void setXVisibility(int visibility) {
		bToSearchX.setVisibility(visibility);
	}
	
	public String getStringDest() {
		return etToSearchBar.getText().toString();
	}
	
	public void setStringDest(String dest) {
		etToSearchBar.setText(dest);
	}
	
	public void clearFocus() {
		etToSearchBar.clearFocus();
	}
}
