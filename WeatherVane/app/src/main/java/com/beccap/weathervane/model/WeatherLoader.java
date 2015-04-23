/**
 * WeatherLoader
 * 
 * Asynchronously reads JSON from URL.
 * 
 * Constructor initializes callback-delegate and URL.
 * startLoading() initiates asynchronous read from URL.
 * onWeatherLoaded() is called on the delegate when the task is complete,
 * and passes the new WeatherStatus list (ArrayList<WeatherStatus>).
 */

package com.beccap.weathervane.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.location.Location;
import android.util.Log;

import com.beccap.weathervane.util.SimpleJSONReader;

public class WeatherLoader implements SimpleJSONReader.Listener
{
	private static final String TAG = WeatherLoader.class.toString();

	public interface Listener {
		// completion callback; will pass NULL if reading was unsuccessful
		void onWeatherLoaded(ArrayList<WeatherStatus> weatherStatusList);
	}
	
	private Listener _listener; // result will be sent to this object via callback

	// constructor
	public WeatherLoader(Listener listener) {
		_listener = listener;
	}

	// call this to start reading the JSON; will conclude by loading the Array with values
	// parsed from the JSON string.
	public void startLoading(double lat, double lon, int count) {

		String weatherURL = WeatherAPI.WEATHER_URL +
							WeatherAPI.WEATHER_URL_GET_LAT + lat +
							WeatherAPI.WEATHER_URL_GET_LON + lon +
							WeatherAPI.WEATHER_URL_GET_COUNT + count;
		Log.d(TAG, "URL: " + weatherURL);
		new SimpleJSONReader(this, weatherURL).execute();
	}

	// callback from SimpleJSONReader (Listener interface)
	public void onReadCompleted(String jsonString) {
		ArrayList<WeatherStatus> weatherStatusList = parseJSON(jsonString);
		_listener.onWeatherLoaded(weatherStatusList);
	}

	// parse the JSON string into a list of WeatherStatuses
	private ArrayList<WeatherStatus> parseJSON(String jsonString) {
		// convert JSON to ArrayList<WeatherStatus>
		ArrayList<WeatherStatus> weatherStatusList = null;

		try {
			weatherStatusList = new ArrayList<WeatherStatus>();
			JSONObject tokenerResult = (JSONObject)new JSONTokener(jsonString).nextValue();
			JSONArray  jsonArray = tokenerResult.getJSONArray(WeatherAPI.WEATHER_ARRAY_TOKEN);
			for (int i = 0; i < jsonArray.length(); ++i) {
				// WeatherStatus knows how to parse its JSON object
				weatherStatusList.add(new WeatherStatus(jsonArray.getJSONObject(i)));
			}
		}
		catch (JSONException e) {
			Log.e(TAG, "Error parsing JSON: " + e.getMessage());
		}

		return weatherStatusList;
	}
}
