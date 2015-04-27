/**
 * Simple activity to display the detail fragment in the single-pane case.
 * 
 * Will pass on weather status data to fragment
 */

package com.beccap.weathervane;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.beccap.weathervane.model.WeatherStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends ActionBarActivity {
	
	private static final String TAG = DetailActivity.class.toString();

	public static final String WEATHER_KEY   = "key_weatherStatus";
	public static final String LATITUDE_KEY  = "key_latitude";
	public static final String LONGITUDE_KEY = "key_longitude";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

        // was probably rotated while displaying detail; return to MainActivity to show 2-pane
        if (getResources().getBoolean(R.bool.is_two_pane)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_detail);

		// make sure it's the first time
        if (savedInstanceState == null) {

			WeatherStatus weatherStatus = null; // null is an OK state
			Location currentLocation = null;

			// get data from intent
			Bundle data = getIntent().getExtras();
			if (data != null) {
				// retrieve jsonString from intent and parse
				String jsonString = data.getString(WEATHER_KEY);
				if (jsonString != null) {
					try {
						weatherStatus = new WeatherStatus(new JSONObject(jsonString));
					}
					catch (JSONException e) {
						Log.e(TAG, "Error retrieving weather status from intent: " + e.getMessage());
					}
				}
				// retrieve location data from intent
				if (data.getDouble(LATITUDE_KEY) > 0) {
					currentLocation = new Location("");
					currentLocation.setLatitude(data.getDouble(LATITUDE_KEY));
					currentLocation.setLongitude(data.getDouble(LONGITUDE_KEY));
				}
			}

			// create new fragment for this weather Status
			WeatherStatusFragment detailFragment =
					WeatherStatusFragment.newInstance(weatherStatus, currentLocation);
			
			// pass along weather data to Fragment
			FragmentManager fm = getSupportFragmentManager();
			fm.beginTransaction()
			.replace(R.id.fragment_detail, detailFragment)
			.commit();
		}
	}
}
