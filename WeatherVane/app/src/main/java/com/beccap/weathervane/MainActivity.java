package com.beccap.weathervane;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.beccap.weathervane.model.WeatherStatus;
import com.google.android.gms.maps.MapsInitializer;

import org.json.JSONException;

public class MainActivity extends ActionBarActivity implements WeatherListFragment.OnWeatherStatusSelectedListener {
	
	private static final String TAG = MainActivity.class.toString();

	public static String PACKAGE_NAME;
	public static final long TEN_MINUTES = 600000;

	private boolean _isTwoPane;

	//============ Life Cycle =====================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PACKAGE_NAME = getApplicationContext().getPackageName();

		// initialize alarm scheduler and cancel any alarms that may have been set
		AlarmScheduler.initialize(this);
		AlarmScheduler.cancelScheduledAlarms();

		// initialize maps
		MapsInitializer.initialize(this);

		// set up fragments
		FragmentManager fm = getSupportFragmentManager();
		WeatherListFragment listFragment = (WeatherListFragment)fm.findFragmentById(R.id.fragment_container);

		// always load the list fragment
		// note: it retains itself
		if (listFragment == null) { 
			listFragment = new WeatherListFragment();
			fm.beginTransaction()
				.add(R.id.fragment_container, listFragment)
				.commit();
		}
		
		// if the detail fragment is visible, then make sure it is showing the right content
        _isTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        if (_isTwoPane) {
            View detailView = findViewById(R.id.fragment_detail);
            if ((detailView != null) && (detailView.getVisibility() == View.VISIBLE)) {
                WeatherStatus weatherStatus = listFragment.getSelectedWeatherStatus();
				Location currentLocation    = listFragment.getCurrentLocation();

				// update the detail fragment
				WeatherStatusFragment detailFragment = WeatherStatusFragment.newInstance(weatherStatus, currentLocation);
				fm.beginTransaction()
						.replace(R.id.fragment_detail, detailFragment)
						.commit();
            }
        }
	}

	@Override
	protected void onDestroy() {
		// Schedule alarm notification to remind user to check the weather
		AlarmScheduler.scheduleAlarm(TEN_MINUTES);
		super.onDestroy();
	}
	
	//============ WeatherListFragment.OnWeatherStatusSelectedListener ============================
	// called when list item is selected
	public void onWeatherStatusSelected(WeatherStatus weatherStatus, Location currentLocation)
	{
		if (_isTwoPane) {
			// we're in 2-pane layout, so update fragment with new information
			FragmentManager fm = getSupportFragmentManager();
			WeatherStatusFragment detailFragment = (WeatherStatusFragment)fm.findFragmentById(R.id.fragment_detail);
			if (detailFragment != null) {
				detailFragment.update(weatherStatus, currentLocation);
			}
			else {
				Log.e(TAG, "null detail fragment in onWeatherStatusSelected");
			}
		}
		else {
			// single-pane, so launch activity with weather status and location bundled up
			try {
				if (weatherStatus != null) {
					Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
					intent.putExtra(DetailActivity.WEATHER_KEY, weatherStatus.toJSON().toString());
					if (currentLocation != null) {
						intent.putExtra(DetailActivity.LATITUDE_KEY, currentLocation.getLatitude());
						intent.putExtra(DetailActivity.LONGITUDE_KEY, currentLocation.getLongitude());
					}
					startActivity(intent);
				}
			}
			catch (JSONException e) {
				Log.e(TAG, "Error serializing WeatherStatus in onWeatherStatusSelected(): " + e.getMessage());
			}
		}
	}
}
