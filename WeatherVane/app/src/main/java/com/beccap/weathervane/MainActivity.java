package com.beccap.weathervane;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.beccap.weathervane.model.WeatherStatus;

import org.json.JSONException;

public class MainActivity extends ActionBarActivity implements WeatherListFragment.OnWeatherStatusSelectedListener {
	
	private static final String TAG = MainActivity.class.toString();

	public static String PACKAGE_NAME;
	public static final long TEN_MINUTES = 600000;

	private boolean _isTwoPane;

	// Life Cycle
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PACKAGE_NAME = getApplicationContext().getPackageName();

		// initialize alarm scheduler and cancel any alarms that may have been set
		AlarmScheduler.initialize(this);
		AlarmScheduler.cancelScheduledAlarms();

		// set up fragments
		FragmentManager fm = getSupportFragmentManager();
		WeatherListFragment listFragment = (WeatherListFragment)fm.findFragmentById(R.id.fragment_container);

        Log.d(TAG, "loading list fragment");
		// always load the list fragment
		// note: it retains itself
		if (listFragment == null) { 
			Log.d(TAG, "adding new ListFragment");
			listFragment = new WeatherListFragment();
			fm.beginTransaction()
				.add(R.id.fragment_container, listFragment)
				.commit();
		}
		
		// if the detail view is visible, then make sure it is showing the right content
        _isTwoPane = getResources().getBoolean(R.bool.is_two_pane);
        Log.d(TAG, "in onCreate, about to test to see if we need to add detail fragment");

        if (_isTwoPane) {
            View detailView = findViewById(R.id.fragment_detail);
            if ((detailView != null) && (detailView.getVisibility() == View.VISIBLE)) {
                WeatherStatusFragment detailFragment = (WeatherStatusFragment) fm.findFragmentById(R.id.fragment_detail);
                WeatherStatus weatherStatus = listFragment.getSelectedWeatherStatus();
                if (detailFragment == null) {
                    detailFragment = WeatherStatusFragment.newInstance(weatherStatus);
                    fm.beginTransaction()
                            .add(R.id.fragment_detail, detailFragment)
                            .commit();
                }
            }
        }
		Log.d(TAG, "in onCreate setting twoPane to " + _isTwoPane);
	}

	@Override
	protected void onDestroy() {
		// Schedule alarm notification to remind user to check the weather
		AlarmScheduler.scheduleAlarm(TEN_MINUTES);
		super.onDestroy();
	}
	
	// WeatherListFragment.OnWeatherStatusSelectedListener interface
	// - called when list item is selected
	public void onWeatherStatusSelected(WeatherStatus weatherStatus)
	{
		if (_isTwoPane) {
			// we're in 2-pane layout, so update fragment
			Log.d(TAG, "item selected; isTwoPane, updating detailFragment");
			FragmentManager fm = getSupportFragmentManager();
			WeatherStatusFragment detailFragment = (WeatherStatusFragment)fm.findFragmentById(R.id.fragment_detail);
			if (detailFragment != null) {
				detailFragment.update(weatherStatus);
			}
			else {
				Log.e(TAG, "null detail fragment in onWeatherStatusSelected");
			}
		}
		else { // single-pane, so launch activity with weather status bundled up
			try {
				if (weatherStatus != null) {
					Log.d(TAG, "item selected; is single pane, launching activity");
					Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
					intent.putExtra(WeatherStatusFragment.WEATHER_KEY, weatherStatus.toJSON().toString());
					startActivity(intent);
				}
			}
			catch (JSONException e) {
				Log.e(TAG, "Error serializing WeatherStatus in onWeatherStatusSelected(): " + e.getMessage());
			}
		}
	}
}
