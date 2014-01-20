package com.haowu.goldilocks;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.haowu.goldilocks.fragment.EditSearchFragment;
import com.haowu.goldilocks.fragment.EditSearchFragment.OnListViewLocationSelectListener;
import com.haowu.goldilocks.fragment.FooterFragment;
import com.haowu.goldilocks.fragment.FooterFragment.OnButtonListener;
import com.haowu.goldilocks.fragment.FooterFragment.OnCheckLocationListener;
import com.haowu.goldilocks.fragment.FooterFragment.OnPageListener;
import com.haowu.goldilocks.fragment.FooterFragment.OnSliderChangeListener;
import com.haowu.goldilocks.fragment.FooterFragment.OnSliderPauseListener;
import com.haowu.goldilocks.fragment.SearchBarFragment;
import com.haowu.goldilocks.fragment.SearchBarFragment.OnListViewClickedListener;
import com.haowu.goldilocks.fragment.SearchBarFragment.OnOptionsClickedListener;
import com.haowu.goldilocks.fragment.SearchBarFragment.OnSearchConditionsListener;
import com.haowu.goldilocks.fragment.SearchBarFragment.OnSearchXClickedListener;
import com.haowu.goldilocks.fragment.SearchBarFragment.OnTextChangedListener;
import com.haowu.goldilocks.fragment.SearchFragment;
import com.haowu.goldilocks.utils.ImageLoader;
import com.haowu.goldilocks.vo.YelpObject;
import com.haowu.goldilocks.widget.Calculation;
import com.haowu.goldilocks.widget.GMapV2Direction;
import com.haowu.goldilocks.widget.Yelp;
import com.haowu.goldilocks.widget.YelpParser;

public class HomeActivity extends Activity implements LocationListener, OnTextChangedListener, OnSearchXClickedListener, OnSearchConditionsListener, OnListViewLocationSelectListener,
		OnOptionsClickedListener, OnListViewClickedListener, OnSliderChangeListener, OnSliderPauseListener, OnButtonListener, OnPageListener, OnCheckLocationListener {

	SearchBarFragment searchBarFrag;
	SearchFragment searchPageFrag;
	EditSearchFragment editSearchFrag;
	FooterFragment footerFrag;
	MapFragment mapFrag;
	GoogleMap map;
	Bitmap bmImg;
	Bitmap bmImg_2;
	ImageLoader imgLoader = new ImageLoader(getBaseContext());
	GMapV2Direction md = new GMapV2Direction();

	Circle circle;
	CircleOptions circleOptions;
	Calculation calculation;

	LocationManager manager;
	String providerName;
	Location lastKnownLocation;

	double from_lat;
	double from_long;
	double to_lat;
	double to_long;

	boolean circleCreated = false;
	boolean isSearch = false;

	LatLng geoDest;
	LatLng fromPosition;
	LatLng toPosition;
	LatLng midpoint = null;
	LatLng prevMp = null;
	LatLng startLocation;
	LatLng endLocation;

	String consumerKey = "o3GcHbIbdpgF-t9dCcxpmQ";
	String consumerSecret = "pXVw3qbwq1RIgRVU83mSZAa-xmU";
	String token = "GB1PEbr7ebldbKA1Y6JV0Symf-B3A1pv";
	String tokenSecret = "UZ4MmK-Kph7ZTxeRs1m1ITMXMpQ";

	ArrayList<LatLng> yelpLocations;
	ArrayList<String> yelpNames;
	ArrayList<String> yelpImages;
	ArrayList<String> yelpRating;
	ArrayList<Integer> yelpReviewCounts;
	ArrayList<Double> yelpDistances;
	ArrayList<String> yelpAddresses;
	ArrayList<String[]> yelpCategories;
	ArrayList<String> yelpIds;

	ArrayList<Marker> markerList = new ArrayList<Marker>();
	ArrayList<YelpObject> yelpObjs = new ArrayList<YelpObject>();

	private static final int MARKER_A = 0;
	private static final int MARKER_B = 1;
	private static final int MARKER_MP = 2;
	private static final int MARKER_POI = 3;

	private static final int OPTIONS_REQUEST = 0x11;
	private static final int LIST_REQUEST = 0x12;

	// values received from options screen.
	String directionsType = "driving";
	String fromLocation = "";
	String toLocation = "";
	ArrayList<String> checkedBoxes;
	String otherOption = "";
	boolean useCurrentLocation = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_activity);
		checkLocation();
		searchBarFrag = (SearchBarFragment) getFragmentManager().findFragmentById(R.id.frag_search_bar);
		searchPageFrag = (SearchFragment) getFragmentManager().findFragmentById(R.id.frag_search_page);
		editSearchFrag = (EditSearchFragment) getFragmentManager().findFragmentById(R.id.frag_search_edit_page);
		footerFrag = (FooterFragment) getFragmentManager().findFragmentById(R.id.frag_footer);
		mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.frag_map);
		map = mapFrag.getMap();
		checkedBoxes = new ArrayList<String>();
		footerFrag.getView().setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		// handle return from the SetLocationActivity.
		if (requestCode == OPTIONS_REQUEST) {
			if (resultCode == RESULT_OK) {
				directionsType = data.getStringExtra("directions_type");
				setEndpointAddresses(data.getStringExtra("from_location"), data.getStringExtra("to_location"));
				checkedBoxes = data.getStringArrayListExtra("checked_boxes");
				otherOption = data.getStringExtra("other_option");
				useCurrentLocation = data.getBooleanExtra("use_curr_location", true);
				// set search bar to destination
				searchBarFrag.setStringDest(toLocation);
				revealMap();
				searchBarFrag.setXVisibility(View.GONE);
				searchBarFrag.setOptionsVisibility(View.VISIBLE);
				if (!toLocation.equals("")) {
					searchBarFrag.setListViewVisibility(View.VISIBLE);
					startFindDirections();
					//make sure go back to seek bar
					footerFrag.setYelpPagerVisibility(View.GONE);
					footerFrag.setSeekBarVisibility(View.VISIBLE);
					footerFrag.setFindShopsVisibility(View.VISIBLE);
					footerFrag.getView().setVisibility(View.VISIBLE);
				}
			}
		}
		if(requestCode == LIST_REQUEST){
			if (resultCode == RESULT_OK) {
				int position = data.getIntExtra("position", 0);
				setFooterInfo();
				footerFrag.setPagerCurrentItem(position);
				footerFrag.setSeekBarVisibility(View.GONE);
				footerFrag.setYelpPagerVisibility(View.VISIBLE);
				drawDirections(position);
				
			}
		}
	}

	/*** TextChangedListener Functions ***/

	@Override
	public void onSearchTextChanged(int count, CharSequence s) {
		// TODO Auto-generated method stub
		if (count > 0) {
			revealEditPage();
			searchBarFrag.setXVisibility(View.VISIBLE);
			searchBarFrag.setOptionsVisibility(View.GONE);

		} else if (count == 0) {
			revealSearchPage();
			searchBarFrag.setXVisibility(View.GONE);
		}
		editSearchFrag.setAdapterFilter(s);
	}

	/*** SearchXClickedListener Functions ***/

	@Override
	public void onSearchXClicked() {
		// TODO Auto-generated method stub
		searchBarFrag.searchXClicked();
	}

	/*** SearchConditionsListener Functions ***/

	@Override
	public void onSearchFocus() {
		// TODO Auto-generated method stub
		revealSearchPage();
		footerFrag.getView().setVisibility(View.GONE);
//		footerFrag.setFooterVisibility(View.INVISIBLE);

		// set padding
		searchBarFrag.setBarPadding(50);

		searchPageFrag.getView().setVisibility(View.VISIBLE);
		editSearchFrag.getView().setVisibility(View.GONE);
		mapFrag.getView().setVisibility(View.GONE);
	}

	@Override
	public void onSearchInit() {
//		footerFrag.setFooterMeasure(footerFrag.getFooterHeight());
		footerFrag.setFooterVisibility(View.VISIBLE);
		revealMap();
		toLocation = searchBarFrag.getStringDest();
		startFindDirections();

		// remove X button and add options menu and list view icon.
		searchBarFrag.setXVisibility(View.GONE);
		searchBarFrag.setOptionsVisibility(View.VISIBLE);
		if (!searchBarFrag.getStringDest().equals("")) {
			// set padding
			searchBarFrag.setBarPadding(80);
			searchBarFrag.setListViewVisibility(View.VISIBLE);
			// set footer visible.
			footerFrag.getView().setVisibility(View.VISIBLE);
			
			//make sure go back to seek bar
			footerFrag.setYelpPagerVisibility(View.GONE);
			footerFrag.setSeekBarVisibility(View.VISIBLE);
			footerFrag.setFindShopsVisibility(View.VISIBLE);
			isSearch = true;
		}

	}

	/*** ListViewLocationSelectListener Functions ***/

	@Override
	public void onListViewLocationSelect(String text) {
		// TODO Auto-generated method stub
		searchBarFrag.onListItemClick(text);
	}

	/*** OptionsClickedListener Functions ***/

	@Override
	public void onOptionsClicked() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, OptionsActivity.class);
		i.putExtra("directions_type", directionsType);
		i.putExtra("from_location", fromLocation);
		i.putExtra("to_location", searchBarFrag.getStringDest());
		i.putExtra("other_option", otherOption);
		i.putStringArrayListExtra("check_boxes", checkedBoxes);
		startActivityForResult(i, OPTIONS_REQUEST);
	}

	/*** ListViewClickedListener Functions ***/

	@Override
	public void onListViewClicked() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, ListActivity.class);
		i.putParcelableArrayListExtra("yelp_objs", yelpObjs);
		startActivityForResult(i, LIST_REQUEST);
		
	}

	private void startFindDirections() {
		checkLocation();
		new FindDirections(this).execute();
	}

	/*** Back Button Behavior ***/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (footerFrag.getYelpPagerVisibility() == View.VISIBLE) {
				footerFrag.setYelpPagerVisibility(View.GONE);
				footerFrag.setSeekBarVisibility(View.VISIBLE);
				footerFrag.setFindShopsVisibility(View.VISIBLE);
				footerFrag.setExpanded(false);
				startFindDirections();
			}
			else if (searchPageFrag.getView().getVisibility() == View.VISIBLE || editSearchFrag.getView().getVisibility() == View.VISIBLE) {
				// go back to homeactivity map.
				revealMap();

				// remove X button and add options menu and list view icon.
				searchBarFrag.setXVisibility(View.GONE);
				searchBarFrag.setOptionsVisibility(View.VISIBLE);

				// clear focus
				searchBarFrag.clearFocus();
				
				if (isSearch) {
					searchBarFrag.setListViewVisibility(View.VISIBLE);
					// set footer visible.
					footerFrag.getView().setVisibility(View.VISIBLE);
				}

				return true;
			} else {
				// goes to main android home.
				// Intent intent = new Intent(Intent.ACTION_MAIN);
				// intent.addCategory(Intent.CATEGORY_HOME);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(intent);
				// return true;
				super.onKeyDown(keyCode, event);
				return true;
			}
		}
		return false;
	}
	
	/*** OnLocationListener Behavior ***/

	@Override
	public boolean isCurrentLocation() {
		// TODO Auto-generated method stub
		return useCurrentLocation;
	}

	@Override
	public LatLng getCurrentLocation() {
		// TODO Auto-generated method stub
		return fromPosition;
	}

	@Override
	public String getInputLocation() {
		// TODO Auto-generated method stub
		return fromLocation;
	}

	/*** Location Finder Helper Functions ***/

	public void checkLocation() {

		// initialize location manager
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// check if GPS is enabled
		// if not, notify user with a toast
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "GPS is disabled.", Toast.LENGTH_SHORT).show();
		} else {
			// get a location provider from location manager empty criteria
			// searches through all providers and returns the best one
			providerName = manager.getBestProvider(new Criteria(), true);
			lastKnownLocation = manager.getLastKnownLocation(providerName);
			from_lat = lastKnownLocation.getLatitude();
			from_long = lastKnownLocation.getLongitude();
			fromPosition = new LatLng(from_lat, from_long);
//
			if (lastKnownLocation != null) {
				Log.d("LOCATION LAT", String.valueOf(lastKnownLocation.getLatitude()));
				Log.d("LOCATION LONG", String.valueOf(lastKnownLocation.getLongitude()));
			}
			manager.requestLocationUpdates(providerName, 15000, 1, this);
		}
	}

	private void addMarker(GoogleMap map, double lat, double lon, String string, String string2, int idx) {
		Marker marker;
		switch (idx) {
		case MARKER_A:
			map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_a)).position(new LatLng(lat, lon)).title(string).snippet(string2));
			break;
		case MARKER_B:
			map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_b)).position(new LatLng(lat, lon)).title(string).snippet(string2));
			break;
		case MARKER_MP:
			map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_mp)).position(new LatLng(lat, lon)).title(string).snippet(string2));
			break;
		case MARKER_POI:
			marker = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_poi)).position(new LatLng(lat, lon)).title(string).snippet(string2));
			markerList.add(marker);
			break;
		}
	}

	class FindDirections extends AsyncTask<Void, Void, ArrayList<LatLng>> {

		Context mContext;

		FindDirections(Context context) {
			super();
			mContext = context;
		}

		protected ArrayList<LatLng> doInBackground(Void... params) {
			// does everything to get the correct lat longs.
			if (!toLocation.equals("")) {
				GMapV2Direction md = new GMapV2Direction();
				Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
				Document doc;
				if (useCurrentLocation) {
					doc = md.getDocument(fromPosition, toLocation, directionsType);
				} else {
					doc = md.getDocument(fromLocation, toLocation, directionsType);
				}
				calculation = new Calculation(doc);
				midpoint = calculation.calcStep();
				ArrayList<LatLng> directionPoint = md.getDirection(doc);

				// find all search terms
				String searchTerms = "";
				for (int i = 0; i < checkedBoxes.size(); i++) {
					searchTerms = searchTerms.concat(checkedBoxes.get(i) + ", ");
				}
				// add other option if necessary.
				searchTerms = searchTerms.concat(otherOption);
				String response = yelp.search(searchTerms, midpoint.latitude, midpoint.longitude);
				YelpParser yParser = new YelpParser(response);
				yelpLocations = yParser.getLocation();
				yelpNames = yParser.getName();
				yelpImages = yParser.getImages();
				yelpRating = yParser.getRatingImages();
				yelpReviewCounts = yParser.getReviewCount();
				yelpAddresses = yParser.getAddress();
				yelpCategories = yParser.getCategories();
				yelpDistances = yParser.getDistance();
				yelpIds = yParser.getIds();

				// set the slider max value
				if (calculation.getHalfDistance() / 2 > 16093 / 2) {
					footerFrag.setSliderMax(16093 / 2);
				} else {
					footerFrag.setSliderMax((int) calculation.getHalfDistance() / 2);
				}
				return directionPoint;
			}
			return null;
		}

		protected void onPostExecute(ArrayList<LatLng> points) {

			// checks to make sure no quota issues. still displays the page.
			if (!toLocation.equals("")) {
				// reset the map.
				map.clear();
				// Jonwu2
				startLocation = new LatLng(points.get(0).latitude, points.get(0).longitude);
				endLocation = new LatLng(points.get(points.size() - 1).latitude, points.get(points.size() - 1).longitude);
				// add markers and draw polylines for route
				from_lat = points.get(0).latitude;
				from_long = points.get(0).longitude;
				to_lat = points.get(points.size() - 1).latitude;
				to_long = points.get(points.size() - 1).longitude;
				addMarker(map, from_lat, from_long, "Start", "My Location", MARKER_A);
				addMarker(map, to_lat, to_long, "End", "Destination", MARKER_B);

				addMarker(map, midpoint.latitude, midpoint.longitude, "Goldilocks", "The Midpoint", MARKER_MP);

				for (int i = 0; i < yelpLocations.size(); i++) {
					addMarker(map, yelpLocations.get(i).latitude, yelpLocations.get(i).longitude, yelpNames.get(i), "Cool Place", MARKER_POI);
				}

				// correct the zoom level
				LatLngBounds.Builder b = new LatLngBounds.Builder();
				b.include(points.get(0));
				b.include(midpoint);
				b.include(points.get(points.size() - 1));

				LatLngBounds bounds = b.build();

				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 900, 900, 5);
				map.animateCamera(cu);

				// draw the route path.
				PolylineOptions rectLine = new PolylineOptions().width(15).color(getResources().getColor(R.color.goldilocksRoutePurple));

				for (int i = 0; i < points.size(); i++) {
					rectLine.add(points.get(i));
				}

				map.addPolyline(rectLine);

				initCircle();
				initPOIMarkers();

//				// reset stringDest
//				toLocation = "";
			}

		}

	}

	public void setFooterInfo() {
		footerFrag.setYelpFooter(imgLoader, yelpObjs);
	}

	public void initCircle() {
		if (circleCreated && prevMp != midpoint) {
			circleCreated = false;
		}

		if (midpoint != null && !circleCreated) {
			circleOptions = new CircleOptions().center(midpoint).radius(footerFrag.getProgress()).fillColor(Color.argb(100, 255, 0, 0)).strokeColor(Color.RED).strokeWidth(2); // In
																																												// meters
			circle = map.addCircle(circleOptions);
			prevMp = midpoint;
			circleCreated = true;
		}

	}

	// jonwu
	public void initPOIMarkers() {
		for (int i = 0; i < markerList.size(); i++) {
			markerList.get(i).remove();
		}
		markerList.clear();
		yelpObjs.clear();
		if (midpoint != null) {
			for (int i = 0; i < yelpLocations.size(); i++) {
				if (footerFrag.getProgress() >= calculation.calcDistance(midpoint.latitude, yelpLocations.get(i).latitude, midpoint.longitude, yelpLocations.get(i).longitude)) {
					YelpObject yelpObj = new YelpObject();
					// set yelp object here.
					yelpObj.setAddress(yelpAddresses.get(i));
					yelpObj.setName(yelpNames.get(i));
					yelpObj.setLocation(yelpLocations.get(i));
					if (yelpImages.size() > i) {
						yelpObj.setIconUrl(yelpImages.get(i));
					}
					if (yelpRating.size() > i) {
						yelpObj.setRatingsUrl(yelpRating.get(i));
					}
					yelpObj.setReviewCount(yelpReviewCounts.get(i));
					
					yelpObj.setCategories(yelpCategories.get(i));
					yelpObj.setBusinessId(yelpIds.get(i));
					yelpObj.setDistance(calculation.metersToMiles(yelpDistances.get(i)));
					
					yelpObjs.add(yelpObj);
					addMarker(map, yelpLocations.get(i).latitude, yelpLocations.get(i).longitude, yelpNames.get(i), yelpNames.get(i), MARKER_POI);
				}
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lastKnownLocation = location;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	// jonwu
	@Override
	public void onSliderChange(int meter) {
		// TODO Auto-generated method stub

		if (midpoint != null) {
			// Get back the mutable Circle
			if (circleCreated) {
				circle.setRadius(meter);
			}
		}
	}

	// jonwu
	@Override
	public void onSliderPause() {
		initPOIMarkers();
	}

	@Override
	public void onButtonListener() {
		// TODO Auto-generated method stub
		if (yelpObjs != null && yelpObjs.size() > 0) {
			setFooterInfo();
			footerFrag.setSeekBarVisibility(View.GONE);
			footerFrag.setYelpPagerVisibility(View.VISIBLE);
			drawDirections(0);
		}
	}
	
	public void drawDirections(int pos){
		
		LatLng start = startLocation;
		LatLng end = endLocation;
		LatLng destination = yelpObjs.get(pos).getLocation();
		
		map.clear();
		from_lat = start.latitude;
		from_long = start.longitude;
		to_lat = end.latitude;
		to_long = end.longitude;

		addMarker(map, from_lat, from_long, "Start", "My Location", MARKER_A);
		addMarker(map, to_lat, to_long, "End", "Destination", MARKER_B);
		addMarker(map, destination.latitude, destination.longitude, yelpObjs
				.get(pos).getName(), yelpObjs.get(pos).getName(), MARKER_POI);

		new FindDirections_2(this, start, destination, getResources().getColor(
				R.color.goldilocksRouteBlue)).execute();
		new FindDirections_2(this, end, destination, getResources().getColor(
				R.color.goldilocksRouteRed)).execute();
	}
	
	class FindDirections_2 extends AsyncTask<Void, Void, ArrayList<LatLng>> {

		Context mContext;
		LatLng start;
		LatLng end;
		int color;

		FindDirections_2(Context context, LatLng start, LatLng end,
				int color) {
			super();
			this.start = start;
			this.end = end;
			this.color = color;
			mContext = context;
		}

		protected ArrayList<LatLng> doInBackground(Void... params) {
			// does everything to get the correct lat longs.
			Document doc;
			doc = md.getDocument(start, end, directionsType);
			ArrayList<LatLng> directionPoint = md.getDirection(doc);
			return directionPoint;

		}

		protected void onPostExecute(ArrayList<LatLng> points) {

			PolylineOptions rectLine = new PolylineOptions().width(15)
					.color(color);

			for (int i = 0; i < points.size(); i++) {
				rectLine.add(points.get(i));
			}
			map.addPolyline(rectLine);
		}

	}

	private void setEndpointAddresses(String from, String to) {
		fromLocation = from;
		toLocation = to;
	}
	
	@Override
	public void onPage(int position) {
		drawDirections(position);
	}
	
	public void revealMap() {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams newMapParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
		LinearLayout.LayoutParams newSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newEditSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);

		searchPageFrag.getView().setLayoutParams(newSearchParams);
		editSearchFrag.getView().setLayoutParams(newEditSearchParams);
		mapFrag.getView().setLayoutParams(newMapParams);

		// visibility for the check later.
		searchPageFrag.getView().setVisibility(View.GONE);
		editSearchFrag.getView().setVisibility(View.GONE);
		mapFrag.getView().setVisibility(View.VISIBLE);
	}
	
	public void revealEditPage() {
		LinearLayout.LayoutParams newMapParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newEditSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);

		searchPageFrag.getView().setLayoutParams(newSearchParams);
		editSearchFrag.getView().setLayoutParams(newEditSearchParams);
		mapFrag.getView().setLayoutParams(newMapParams);

		// visibility for the check later.
		searchPageFrag.getView().setVisibility(View.GONE);
		editSearchFrag.getView().setVisibility(View.VISIBLE);
		mapFrag.getView().setVisibility(View.GONE);
	}
	
	public void revealSearchPage() {
		LinearLayout.LayoutParams newMapParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);
		LinearLayout.LayoutParams newSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
		LinearLayout.LayoutParams newEditSearchParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0);

		searchPageFrag.getView().setLayoutParams(newSearchParams);
		editSearchFrag.getView().setLayoutParams(newEditSearchParams);
		mapFrag.getView().setLayoutParams(newMapParams);

		// visibility for the check later.
		searchPageFrag.getView().setVisibility(View.VISIBLE);
		editSearchFrag.getView().setVisibility(View.GONE);
		mapFrag.getView().setVisibility(View.GONE);
	}
}
