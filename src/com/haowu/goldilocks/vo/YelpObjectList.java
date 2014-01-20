package com.haowu.goldilocks.vo;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.os.Parcel;
import android.os.Parcelable;

public class YelpObjectList extends ArrayList<YelpObject> implements Parcelable {

	private static final long serialVersionUID = 1L;

	public YelpObjectList() {
	}

	@SuppressWarnings("unused")
	public YelpObjectList(Parcel in) {
		this();
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		this.clear();

		// First we have to read the list size
		int size = in.readInt();

		for (int i = 0; i < size; i++) {
			YelpObject yelpObj = new YelpObject();
			yelpObj.setName(in.readString());
			yelpObj.setIconUrl(in.readString());
			yelpObj.setRatingsUrl(in.readString());
			yelpObj.setReviewCount(in.readInt());
			yelpObj.setAddress(in.readString());
			yelpObj.setCategories(in.createStringArray());
			yelpObj.setLocation((LatLng)in.readParcelable(getClass().getClassLoader()));
			yelpObj.setBusinessId(in.readString());
			yelpObj.setDistance(in.readDouble());
			this.add(yelpObj);
		}
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		int size = this.size();

		dest.writeInt(size);

		for (int i = 0; i < size; i++) {
			YelpObject yelp = this.get(i);

			dest.writeString(yelp.name);
			dest.writeString(yelp.iconUrl);
			dest.writeString(yelp.ratingsUrl);
			dest.writeInt(yelp.reviewCount);
			dest.writeString(yelp.address);
			dest.writeStringArray(yelp.categories);
			dest.writeParcelable(yelp.location, flags);
			dest.writeString(yelp.businessId);
			dest.writeDouble(yelp.distance);
		}
	}

	public final Parcelable.Creator<YelpObjectList> CREATOR = new Parcelable.Creator<YelpObjectList>() {
		public YelpObjectList createFromParcel(Parcel in) {
			return new YelpObjectList(in);
		}

		public YelpObjectList[] newArray(int size) {
			return new YelpObjectList[size];
		}
	};

}
