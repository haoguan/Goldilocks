package com.haowu.goldilocks.fragment;

import com.haowu.goldilocks.R;
import com.haowu.goldilocks.utils.ImageLoader;
import com.haowu.goldilocks.vo.YelpObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class YelpFooterFragment extends Fragment{
	
    /**
     * The argument keys for the passed values.
     */
    public static final String ARG_PAGE = "page";
    public static final String ARG_YELP_OBJ = "yelp_obj";
	
	int pageNum;
	YelpObject yelpObj;
	
	ImageLoader imgLoader;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static YelpFooterFragment create(int pageNumber, YelpObject yelpObj) {
        YelpFooterFragment fragment = new YelpFooterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putParcelable(ARG_YELP_OBJ, yelpObj);
        fragment.setArguments(args);
        return fragment;
    }
    
    public YelpFooterFragment(){}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt(ARG_PAGE);
        yelpObj = getArguments().getParcelable(ARG_YELP_OBJ);
        
        imgLoader = new ImageLoader(getActivity());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.yelp_footer_info_fragment, container, false);

        // Set the views
        ((TextView) rootView.findViewById(R.id.yelp_title)).setText(yelpObj.getName());
        imgLoader.DisplayImage(yelpObj.getIconUrl(), (ImageView) rootView.findViewById(R.id.yelp_picture));
        imgLoader.DisplayImage(yelpObj.getRatingsUrl(), (ImageView) rootView.findViewById(R.id.yelp_rating));

        return rootView;
    }
    
    public YelpObject getYelpObject() {
    	return yelpObj;
    }
    
}
