package com.haowu.goldilocks.widget;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.model.LatLng;

public class YelpParser {
	JSONObject obj;
	ArrayList<String> name = new ArrayList<String>();
	ArrayList<String> iconImages = new ArrayList<String>();
	ArrayList<String> address = new ArrayList<String>();
	ArrayList<String> phone = new ArrayList<String>();
	ArrayList<String> ratingsImages = new ArrayList<String>();
	ArrayList<String> ratings = new ArrayList<String>();
	ArrayList<String> mobile_url = new ArrayList<String>();
	ArrayList<Double> distance = new ArrayList<Double>();
	ArrayList<LatLng> location = new ArrayList<LatLng>();
	ArrayList<Integer> reviewCount = new ArrayList<Integer>();
	ArrayList<String[]> categories = new ArrayList<String[]>();
	ArrayList<String> ids = new ArrayList<String>();

	public YelpParser(String response) {
		try {
			obj = new JSONObject(response);
			JSONArray businesses = obj.getJSONArray("businesses");
			for (int i = 0; i < businesses.length(); i++) {
				JSONObject locationObj = businesses.getJSONObject(i).getJSONObject("location");
				JSONObject coordObj = locationObj.getJSONObject("coordinate");
				double lat = Double.parseDouble(coordObj.get("latitude").toString());
				double lon = Double.parseDouble(coordObj.get("longitude").toString());
				
				JSONArray addressObj = locationObj.getJSONArray("address");
				String locationAddress = "";
				for (int a = 0; a < addressObj.length(); a++) {
					locationAddress = locationAddress.concat(addressObj.getString(a) + ", ");
				}
				locationAddress = locationAddress.concat(locationObj.getString("city"));
				address.add(locationAddress);

				name.add(businesses.getJSONObject(i).get("name").toString());
				if (businesses.getJSONObject(i).opt("rating") != null) {
					ratings.add(businesses.getJSONObject(i).get("rating").toString());
				}
				if (businesses.getJSONObject(i).opt("display_phone") != null) {
					phone.add(businesses.getJSONObject(i).get("display_phone").toString());
				}
				if (businesses.getJSONObject(i).opt("mobile_url") != null) {
					mobile_url.add(businesses.getJSONObject(i).get("mobile_url").toString());
				}
				if (businesses.getJSONObject(i).opt("distance") != null) {
					distance.add(Double.parseDouble(businesses.getJSONObject(i).get("distance").toString()));
				}
				if (businesses.getJSONObject(i).opt("review_count") != null) {
					reviewCount.add(Integer.parseInt(businesses.getJSONObject(i).get("review_count").toString()));
				}
				
				//check if image icon is available.
				if (businesses.getJSONObject(i).opt("image_url") != null) {
					iconImages.add(businesses.getJSONObject(i).get("image_url").toString());
				}
				else {
					iconImages.add("android.resource://com.haowu.goldilocks/drawable/stub");
				}
				
				//check if ratings image is available.
				if (businesses.getJSONObject(i).opt("rating_img_url_large") != null) {
					ratingsImages.add(businesses.getJSONObject(i).get("rating_img_url_large").toString());
				}
				else {
					ratingsImages.add("");
				}
				
				if (businesses.getJSONObject(i).opt("categories") != null) {
					JSONArray categoryArr = businesses.getJSONObject(i).getJSONArray("categories");
					String[] categoriesArr = new String[categoryArr.length()];
					for (int j = 0; j < categoryArr.length(); j++) {
						categoriesArr[j] = categoryArr.getJSONArray(j).get(0).toString();
					}
					categories.add(categoriesArr);
				}
				
				if (businesses.getJSONObject(i).opt("id") != null) {
					ids.add(businesses.getJSONObject(i).get("id").toString());
				}

				location.add(new LatLng(lat, lon));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> getName() {
		return name;
	}

	public ArrayList<String> getImages() {
		return iconImages;
	}

	public ArrayList<String> getAddress() {
		return address;
	}

	public ArrayList<String> getPhone() {
		return phone;
	}

	public ArrayList<String> getRatingImages() {
		return ratingsImages;
	}

	public ArrayList<String> getRatings() {
		return ratings;
	}

	public ArrayList<String> getMobile_url() {
		return mobile_url;
	}

	public ArrayList<Double> getDistance() {
		return distance;
	}

	public ArrayList<LatLng> getLocation() {
		return location;
	}

	public ArrayList<Integer> getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(ArrayList<Integer> reviewCount) {
		this.reviewCount = reviewCount;
	}

	public ArrayList<String[]> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String[]> categories) {
		this.categories = categories;
	}

	public ArrayList<String> getIds() {
		return ids;
	}

	public void setIds(ArrayList<String> ids) {
		this.ids = ids;
	}
	
}
