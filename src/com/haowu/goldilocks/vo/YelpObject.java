package com.haowu.goldilocks.vo;

import com.google.android.gms.maps.model.LatLng;

import android.os.Parcel;
import android.os.Parcelable;

public class YelpObject implements Parcelable {
	
	String name;
	String iconUrl;
	String ratingsUrl;
	int reviewCount;
	String address;
	String[] categories;
	LatLng location;
	String businessId;
	double distance;
	
	public YelpObject() {
		
	}
	
	public YelpObject(Parcel in) {
		this.name = in.readString();
		this.iconUrl = in.readString();
		this.ratingsUrl = in.readString();
		this.reviewCount = in.readInt();
		this.address = in.readString();
		this.categories = in.createStringArray();
		this.location = in.readParcelable(getClass().getClassLoader());
		this.businessId = in.readString();
		this.distance = in.readDouble();
	}
	public double getDistance(){
		return distance;
	}
	public void setDistance(double distance){
		this.distance = distance;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String icon) {
		this.iconUrl = icon;
	}

	public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public String getRatingsUrl() {
		return ratingsUrl;
	}

	public void setRatingsUrl(String ratingsUrl) {
		this.ratingsUrl = ratingsUrl;
	}
	
	public LatLng getLocation(){
		return location;
	}
	public void setLocation(LatLng location){
		this.location = location;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String id) {
		businessId = id;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeString(name);
		out.writeString(iconUrl);
		out.writeString(ratingsUrl);
		out.writeInt(reviewCount);
		out.writeString(address);
		out.writeStringArray(categories);
		out.writeParcelable(location, flags);
		out.writeString(businessId);
		out.writeDouble(distance);
	}
	
	public static final Parcelable.Creator<YelpObject> CREATOR = new Parcelable.Creator<YelpObject>() {

		@Override
		public YelpObject createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new YelpObject(source);
		}

		@Override
		public YelpObject[] newArray(int size) {
			// TODO Auto-generated method stub
			return new YelpObject[size];
		}
		
	};
	
}
