package com.haowu.goldilocks.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.haowu.goldilocks.R;
import com.haowu.goldilocks.utils.ImageLoader;
import com.haowu.goldilocks.vo.YelpObject;

public class ListViewResultsAdapter extends ArrayAdapter<YelpObject> {

	Context context;
	ArrayList<YelpObject> results;
	ImageLoader imgLoader = new ImageLoader(getContext());
	DecimalFormat df = new DecimalFormat("#.##");

	public ListViewResultsAdapter(Context context, int resource, ArrayList<YelpObject> results) {
		super(context, resource, results);
		this.results = results;
		this.context = context;
	}

	// override how we retrieve row information and how to set them up properly.
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		YelpHolder holder = null;

		// create new row using xml layout
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.yelp_result, null);
			
			holder = new YelpHolder();
			holder.name = (TextView) v.findViewById(R.id.tv_yelp_result_name);
			holder.icon = (ImageView) v.findViewById(R.id.iv_yelp_result_image);
			holder.ratings = (ImageView) v.findViewById(R.id.iv_yelp_result_rating);
			holder.reviews = (TextView) v.findViewById(R.id.tv_yelp_result_reviews);
			holder.address = (TextView) v.findViewById(R.id.tv_yelp_result_address);
			holder.categories = (TextView) v.findViewById(R.id.tv_yelp_result_categories);
			holder.distances = (TextView) v.findViewById(R.id.tv_yelp_distance);
			v.setTag(holder);
		}
		else {
			holder = (YelpHolder) v.getTag();
		}
		
		
		YelpObject result = results.get(position);
		if (result != null) {
			holder.name.setText(result.getName());
			holder.position = position;
			holder.icon.setTag(result.getIconUrl());
			imgLoader.DisplayImage(result.getIconUrl(), holder.icon);
			holder.ratings.setTag(result.getRatingsUrl());
			if (!result.getRatingsUrl().equals("")) {
				imgLoader.DisplayImage(result.getRatingsUrl(), holder.ratings);
			}
			else {
				holder.ratings.setVisibility(View.GONE);
			}
			holder.reviews.setText(result.getReviewCount() + " Reviews");
			holder.address.setText(result.getAddress());
			String categoryText = "";
			for (int i = 0; i < result.getCategories().length - 1; i++) {
				categoryText = categoryText.concat(result.getCategories()[i] + ", ");
			}
			categoryText = categoryText.concat(result.getCategories()[result.getCategories().length - 1]);
			holder.categories.setText(categoryText);
			holder.distances.setText(df.format(result.getDistance()) + " mi");
		}
		return v;
	}
	
	static class YelpHolder {
		TextView name;
		ImageView icon;
		ImageView ratings;
		TextView reviews;
		TextView address;
		TextView categories;
		int position;
		TextView distances;
	}
}
