/**
 * This Fragment manages the list.
 */

package com.beccap.weathervane;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beccap.weathervane.model.WeatherLoader;
import com.beccap.weathervane.model.WeatherStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class WeatherListFragment extends ListFragment
		implements WeatherLoader.OnWeatherLoadedListener,
		ConnectionCallbacks, OnConnectionFailedListener {
	
	private static final String TAG = WeatherListFragment.class.toString();

	public interface OnWeatherStatusSelectedListener {
		void onWeatherStatusSelected(WeatherStatus weatherStatus);
	}

	private OnWeatherStatusSelectedListener _onSelectedListener;
	private WeatherStatus                   _selectedWeatherStatus = null;

	private WeatherListAdapter _adapter;

	private GoogleApiClient _googleApiClient;
	private Location        _currentLocation = null;

	//============ Life Cycle =====================================================================
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true); // ensures we don't have to reload the json upon rotation
		setHasOptionsMenu(true);
		
		Log.d(TAG, "in onCreate, connecting to google api services");
		_googleApiClient = buildGoogleApiClient();
		_googleApiClient.connect();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// since fragment is retained while activity is not,
		// set listener here so that our connection stays fresh
		
		try {
			_onSelectedListener = (OnWeatherStatusSelectedListener) activity;
		}
		catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + e.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		_googleApiClient.disconnect();
		super.onDestroy();
	}

	//============ Menu Options ===================================================================

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_refresh:
				refreshLocationAndWeather();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	//============ Getters ========================================================================
	// Getter for currently selected item
	public WeatherStatus getSelectedWeatherStatus()
	{
		return _selectedWeatherStatus;
	}

	//============ ListAdapter callbacks ==========================================================
	@Override
	public void onListItemClick(ListView listView, View view, int pos, long id)
	{
		WeatherStatus weatherStatus = (WeatherStatus)getListAdapter().getItem(pos);
		_selectedWeatherStatus = weatherStatus;
		_onSelectedListener.onWeatherStatusSelected(weatherStatus);
        _adapter.notifyDataSetChanged();
    }
	
	// WeatherLoader.OnWeatherLoadedListener callback
	// Weather finished loading (icons may not have completed)
	public void onWeatherLoaded(ArrayList<WeatherStatus> weatherList)
	{
		testWeatherList(weatherList);
		if (weatherList == null) {
			Log.e(TAG, "null Weather List passed to onWeatherLoaded");
			return;
		}
		_adapter = new WeatherListAdapter(weatherList);
		setListAdapter(_adapter);
	}

	//============ Custom List Adapter ============================================================
	private class WeatherListAdapter extends ArrayAdapter<WeatherStatus>
	{
		public WeatherListAdapter(ArrayList<WeatherStatus> weatherList)
		{
			super(getActivity(), 0, weatherList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View newView = convertView;
			ViewHolder holder;

			WeatherStatus curr = getItem(position);

			// create list-item-view layout and retain views in holder
			if (convertView == null) {
				newView = getActivity().getLayoutInflater()
						.inflate(R.layout.listitem_weather_status, parent, false);
				holder = new ViewHolder();
				holder.cityNameTextView    = (TextView)newView.findViewById(R.id.cityTextView);
				holder.temperatureTextView = (TextView)newView.findViewById(R.id.temperatureTextView);
				holder.weatherIconView     = (ImageView)newView.findViewById(R.id.weatherIconView);
				newView.setTag(holder);

			}
			else {
				holder = (ViewHolder) newView.getTag();
			}

			// update views here
			holder.cityNameTextView.setText(curr.getCityName());
			holder.temperatureTextView.setText(curr.getTemperatureString());
			holder.weatherIconView.setImageBitmap(curr.getWeatherIconBitmap());

			return newView;
		}

		// view holder pattern
		private class ViewHolder {
			TextView  cityNameTextView;
			TextView  temperatureTextView;
			ImageView weatherIconView;
		}
	}

	//============ Location Handling ==============================================================
	// update location and weather based on current data
	private void refreshLocationAndWeather() {
		if (_adapter != null) {
			_adapter.clear();
			_adapter.notifyDataSetChanged();
		}

		_currentLocation = getGoogleLocation();
		Log.d(TAG, "currentLocation: " + _currentLocation.toString());
		if (_currentLocation != null) {
			Log.d(TAG, "loading weather");
			new WeatherLoader(this).startLoading(_currentLocation, 10);
		}
	}

	// get current location
	private Location getGoogleLocation() {
		Location location = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
		return location;
	}

	// ============ Google API Client =============================================================
	protected synchronized GoogleApiClient buildGoogleApiClient() {
		Log.d(TAG, "building api client");
		return new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
	}

	// ============ Google API Callbacks ==========================================================
	@Override
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "onConnected");
		refreshLocationAndWeather();
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.e(TAG, "Connection to Google API Client suspended.");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.e(TAG, "Connection to Google API Client failed.");
		// TODO: Set up more sophisticated error handling

		// for now, just load weather from an arbitrary point
		_currentLocation = new Location("dummy provider");
		_currentLocation.setLatitude(45.715915);
		_currentLocation.setLongitude(-122.868617);
		new WeatherLoader(this).startLoading(_currentLocation, 10);
	}

	// ============ Debugging =====================================================================
	private void testWeatherList(ArrayList<WeatherStatus> weatherList)
	{
		if (weatherList == null) {
			Log.e(TAG, "null WeatherList passed to testWeatherList");
			return;
		}
		Log.d(TAG,"WeatherList (size = " + weatherList.size() + "):\n");
		for (int i = 0; i < weatherList.size(); ++i) {
			Log.d(TAG, weatherList.get(i).toString() + "\n");
		}
	}
}
