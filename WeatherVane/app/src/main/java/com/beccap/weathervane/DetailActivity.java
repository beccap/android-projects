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

        if (savedInstanceState == null) {
			Log.d(TAG, "creating new fragment");
			
			// get earthquake data from intent
			WeatherStatus weatherStatus = null; // null is an OK state
			Location currentLocation = null;

			Bundle data = getIntent().getExtras();
			if (data != null) {
				String jsonString = data.getString(WeatherStatusFragment.WEATHER_KEY);
				if (jsonString != null) {
					try {
						// we don't need to listen for changes here because any changes to the
						// weatherStatus will be pushed to this view by the master.
						weatherStatus = new WeatherStatus(new JSONObject(jsonString));
					}
					catch (JSONException e) {
						Log.e(TAG, "Error retrieving earthquake from intent: " + e.getMessage());
					}
				}
				if (data.getDouble(WeatherStatusFragment.LATITUDE_KEY) > 0) {
					currentLocation = new Location("dummy provider");
					currentLocation.setLatitude(data.getDouble(WeatherStatusFragment.LATITUDE_KEY));
					currentLocation.setLongitude(data.getDouble(WeatherStatusFragment.LONGITUDE_KEY));
				}
			}

			// create new fragment for this weather Status
			WeatherStatusFragment detailFragment =
					WeatherStatusFragment.newInstance(weatherStatus, currentLocation);
			
			// pass along earthquakeData to Fragment
			
			FragmentManager fm = getSupportFragmentManager();
			fm.beginTransaction()
			.replace(R.id.fragment_detail, detailFragment)
			.commit();
		}
	}
}
