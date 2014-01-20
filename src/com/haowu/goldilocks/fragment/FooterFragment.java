package com.haowu.goldilocks.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.android.gms.maps.model.LatLng;
import com.haowu.goldilocks.HomeActivity;
import com.haowu.goldilocks.R;
import com.haowu.goldilocks.utils.ImageLoader;
import com.haowu.goldilocks.vo.YelpObject;
import com.haowu.goldilocks.widget.CustomViewPager;
import com.haowu.goldilocks.widget.ExtendedLayout;

public class FooterFragment extends Fragment {

	CustomViewPager mPager;
	FooterPagerAdapter mPagerAdapter;
	ImageView ivYelp;
	ImageView ivGoogle;

	SeekBar sliderBar;
	OnSliderChangeListener mSliderChangeListener;
	OnSliderPauseListener mSliderPauseListener;
	OnButtonListener mButtonListener;
	OnPageListener mPageListener;
	OnCheckLocationListener mLocationListener;
	HomeActivity homeActivity;
	Button findShops;
	boolean isFirst = true;
	Animation slideUp;
	ExtendedLayout footer;
	boolean isExpanded = false;

	ArrayList<YelpObject> yelpObjs;


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.footer_fragment, container, false);

		sliderBar = (SeekBar) view.findViewById(R.id.slider_bar);
		findShops = (Button) view.findViewById(R.id.find_shops);
		footer = (ExtendedLayout) view.findViewById(R.id.footer);
		ivYelp = (ImageView) view.findViewById(R.id.iv_yelp_button);
		ivGoogle = (ImageView) view.findViewById(R.id.iv_google_button);

		mPager = (CustomViewPager) view.findViewById(R.id.vp_yelp_pager);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final GestureDetector tapGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				if (isExpanded) {
					collapse();
					isExpanded = false;
					mPager.setPagingEnabled(true);
					return true;
				} else {
					expand();
					isExpanded = true;
					mPager.setPagingEnabled(false);
					return true;
				}
			}
		});

		mPager.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				tapGestureDetector.onTouchEvent(event);
				return false;
			}
		});

		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mPageListener.onPage(mPager.getCurrentItem());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		findShops.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mButtonListener.onButtonListener();
			}
		});
		sliderBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				progressChanged = progress;
				mSliderChangeListener.onSliderChange(progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				mSliderPauseListener.onSliderPause();

			}
		});
		ivYelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (yelpObjs != null) {
					YelpObject obj = yelpObjs.get(mPager.getCurrentItem());
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("yelp4:///biz/" + obj.getBusinessId())));
				}
			}
		});
		ivGoogle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (yelpObjs != null) {
					YelpObject obj = yelpObjs.get(mPager.getCurrentItem());
					if (mLocationListener.isCurrentLocation()) {
						LatLng currLoc = mLocationListener.getCurrentLocation();
						Intent google = new Intent(Intent.ACTION_VIEW, Uri
						        .parse("http://maps.google.com/maps?saddr="
						                + currLoc.latitude + ","
						                + currLoc.longitude + "&daddr="
						                + obj.getLocation().latitude + "," + obj.getLocation().longitude));
						startActivity(google);
					}
					else {
						String currLoc = mLocationListener.getInputLocation().replace(' ', '+');
						Intent google = new Intent(Intent.ACTION_VIEW, Uri
						        .parse("http://maps.google.com/maps?saddr="
						                + currLoc
						        		+ "&daddr="
						                + obj.getLocation().latitude + "," + obj.getLocation().longitude));
						startActivity(google);
					}
				}
			}
		});
	}

	public void expand() {
		// create translation animation
		TranslateAnimation trans = new TranslateAnimation(0, 0, 0, 0, 0, 0, TranslateAnimation.ABSOLUTE, -footer.getHeight() / 2);
		trans.setDuration(500);
		trans.setFillAfter(false);
		trans.setFillEnabled(true);
		trans.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				footer.layout(footer.getLeft(), footer.getTop() - (footer.getHeight() / 2), footer.getRight(), footer.getBottom() - (footer.getHeight() / 2));
			}
		});

		// start our animation
		footer.startAnimation(trans);
	}

	public void collapse() {
		// create translation animation
		TranslateAnimation trans = new TranslateAnimation(0, 0, 0, 0, 0, 0, TranslateAnimation.ABSOLUTE, footer.getHeight() / 2);
		trans.setDuration(500);
		trans.setFillAfter(false);
		trans.setFillEnabled(true);
		trans.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				footer.layout(footer.getLeft(), footer.getTop() + (footer.getHeight() / 2), footer.getRight(), footer.getBottom() + (footer.getHeight() / 2));
			}
		});
		// start our animation
		footer.startAnimation(trans);
	}

	public interface OnButtonListener {
		public void onButtonListener();
	}

	public interface OnSliderChangeListener {
		public void onSliderChange(int meter);
	}

	public interface OnSliderPauseListener {
		public void onSliderPause();
	}

	public interface OnPageListener {
		public void onPage(int position);
	}
	
	public interface OnCheckLocationListener {
		public boolean isCurrentLocation();
		public LatLng getCurrentLocation();
		public String getInputLocation();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mSliderChangeListener = (OnSliderChangeListener) activity;
			mSliderPauseListener = (OnSliderPauseListener) activity;
			mButtonListener = (OnButtonListener) activity;
			mPageListener = (OnPageListener) activity;
			mLocationListener = (OnCheckLocationListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement missing listeners");
		}
	}

	public int getProgress() {
		return sliderBar.getProgress();
	}

	public void setSliderMax(int max) {
		sliderBar.setMax(max);
	}

	public void setSeekBarVisibility(int n) {
		sliderBar.setVisibility(n);
	}

	public void setYelpPagerVisibility(int n) {
		mPager.setVisibility(n);
	}

	public int getYelpPagerVisibility() {
		return mPager.getVisibility();
	}

	public void setYelpPagerHeight() {
		mPager.getLayoutParams().height = mPager.getHeight();
	}

	public void setFindShopsVisibility(int visibility) {
		findShops.setVisibility(visibility);
	}

	public void setYelpFooter(ImageLoader imgLoader, ArrayList<YelpObject> yelpObjs) {
		findShops.setVisibility(View.GONE);
		this.yelpObjs = yelpObjs;
		mPagerAdapter = new FooterPagerAdapter(getFragmentManager(), yelpObjs);
		mPager.setAdapter(mPagerAdapter);
	}
	public void setPagerCurrentItem(int position){
		mPager.setCurrentItem(position);
	}

	public static void expand(final View v, float startWeight, float endWeight) {

		final float mStartWeight = startWeight;
		final float mDeltaWeight = endWeight - startWeight;

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
				lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
				v.setLayoutParams(lp);
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration(500);
		v.startAnimation(a);
	}

	/**
	 * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment}
	 * objects, in sequence.
	 */
	private class FooterPagerAdapter extends FragmentStatePagerAdapter {

		ArrayList<YelpObject> yelpObjs;

		public FooterPagerAdapter(FragmentManager fm, ArrayList<YelpObject> yelpObjs) {
			super(fm);
			this.yelpObjs = yelpObjs;
		}

		@Override
		public Fragment getItem(int position) {
			return YelpFooterFragment.create(position, yelpObjs.get(position));
		}

		@Override
		public int getCount() {
			return yelpObjs.size();
		}

	}

	public void setFooterVisibility(int visibility) {
		footer.setVisibility(visibility);
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

}
