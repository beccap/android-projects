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

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WeatherLoader
{
	private static final String TAG = WeatherLoader.class.toString();

	public interface OnWeatherLoadedListener {
		// completion callback; will pass NULL if reading was unsuccessful
		void onWeatherLoaded(ArrayList<WeatherStatus> weatherStatusList);
	}
	
	private OnWeatherLoadedListener _listener; // result will be sent to this object via callback

	// constructor
	public WeatherLoader(OnWeatherLoadedListener listener) {
		_listener = listener;
	}

	// call this to start reading the JSON; will conclude by loading the Array with values
	// parsed from the JSON string.
	public void startLoading(double lat, double lon, int count) {

		String weatherURL = WeatherAPI.WEATHER_URL +
							WeatherAPI.WEATHER_API_QUERY +
							WeatherAPI.WEATHER_URL_GET_LAT + lat +
							WeatherAPI.WEATHER_URL_GET_LON + lon +
							WeatherAPI.WEATHER_URL_GET_COUNT + count;
		Log.d(TAG, "URL: " + weatherURL);
		new WeatherJSONReader(weatherURL).execute();
	}

//	// callback from SimpleJSONReader.Listener
//	public void onReadCompleted(String jsonString) {
//		ArrayList<WeatherStatus> weatherStatusList = parseJSON(jsonString);
//		_listener.onWeatherLoaded(weatherStatusList);
//	}

//	// callback from WeatherStatus.OnChangedListener
//	public void onWeatherStatusChanged(WeatherStatus weatherStatus) {
//		_listener.onWeatherStatusChanged(weatherStatus);
//	}

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
				WeatherStatus weatherStatus = new WeatherStatus(jsonArray.getJSONObject(i));
				String weatherIconString = weatherStatus.getWeatherIconString();
				WeatherIconTable.loadIconBitmap(weatherIconString);
				weatherStatusList.add(weatherStatus);
			}
			Log.d(TAG, "icon count after loading: " + WeatherIconTable.getIconCount());
		}
		catch (JSONException e) {
			Log.e(TAG, "Error parsing JSON: " + e.getMessage());
		}

		return weatherStatusList;
	}

	private class WeatherJSONReader extends AsyncTask<Void, Void, ArrayList<WeatherStatus>>
	{
		private String _stringUrl;

		public WeatherJSONReader(String stringUrl) {
			_stringUrl = stringUrl;
		}

		@Override
		protected ArrayList<WeatherStatus> doInBackground(Void... params)
		{
			// read in a string representing a one-dimensional array of JSON objects
			try {
				URL readURL = new URL(_stringUrl);
				HttpURLConnection conn = (HttpURLConnection) readURL.openConnection();

				// set connection properties
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(10000);
				conn.setDoInput(true);

				// connect and read
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(is));

				// read the contents of URL into a string for parsing
				StringBuilder jsonStringBldr = new StringBuilder();
				String currentLine;
				while ((currentLine = in.readLine()) != null) {
					jsonStringBldr.append(currentLine);
				}

				// parse the JSON and send the resulting ArrayList to onPostExecute
				return parseJSON(jsonStringBldr.toString());
			}
			catch (Exception e) {
				Log.e(TAG,"Error reading JSON: " + e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<WeatherStatus> weatherList) {
			// pass result back to listener
			_listener.onWeatherLoaded(weatherList);
		}
	}}
