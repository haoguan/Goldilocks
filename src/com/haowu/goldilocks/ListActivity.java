package com.haowu.goldilocks;

import java.util.ArrayList;

import com.haowu.goldilocks.adapter.ListViewResultsAdapter;
import com.haowu.goldilocks.vo.YelpObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ListActivity extends Activity implements OnItemClickListener{
	
	ListView lvResults;
	ListViewResultsAdapter lvAdapter;
	ArrayList<YelpObject> yelpResults; //need to get from home activity.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_activity);
		
		Intent i = getIntent();
		yelpResults = i.getParcelableArrayListExtra("yelp_objs");
		
		lvResults = (ListView) findViewById(R.id.lv_yelp_results);
		lvAdapter = new ListViewResultsAdapter(this, R.layout.yelp_result, yelpResults);
		lvResults.setAdapter(lvAdapter);
		lvResults.setOnItemClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> g, View v, int pos, long id) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		i.putExtra("position", pos);
		setResult(RESULT_OK, i);	
		finish();
		
	}
}
