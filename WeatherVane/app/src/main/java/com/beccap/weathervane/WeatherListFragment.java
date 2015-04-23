/**
 * This Fragment manages the list.
 */

package com.beccap.weathervane;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.provider.MediaStore.Images.Media;

import com.beccap.weathervane.model.WeatherLoader;
import com.beccap.weathervane.model.WeatherStatus;

import java.util.ArrayList;

//import android.graphics.Color;
//import android.graphics.Typeface;

public class WeatherListFragment extends ListFragment implements WeatherLoader.Listener {
	
	private static final String TAG = WeatherListFragment.class.toString();
	
	public interface OnWeatherStatusSelectedListener {
		void onWeatherStatusSelected(WeatherStatus weatherStatus);
	}
	
	private OnWeatherStatusSelectedListener _onSelectedListener;
	private WeatherStatus                   _selectedWeatherStatus = null;

	// Life Cycle
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true); // ensures we don't have to reload the json upon rotation
		
		Log.d(TAG, "in onCreate, loading weather");
		new WeatherLoader(this).startLoading(45.715406, -122.872803, 10);
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
			throw new ClassCastException(activity.toString() + " missing required implementation of OnWeatherStatusSelectedListener");
		}
	}

	// ListAdapter callbacks
	@Override
	public void onListItemClick(ListView listView, View view, int pos, long id)
	{
		WeatherStatus weatherStatus = (WeatherStatus)getListAdapter().getItem(pos);
		_selectedWeatherStatus = weatherStatus;
		_onSelectedListener.onWeatherStatusSelected(weatherStatus);
        ((WeatherListAdapter)getListAdapter()).notifyDataSetChanged();
    }
	
	// WeatherLoader.Listener callback
	public void onWeatherLoaded(ArrayList<WeatherStatus> weatherList)
	{
		testWeatherList(weatherList);
		if (weatherList == null) {
			Log.e(TAG, "null Weather List passed to onWeatherLoaded");
			return;
		}
		WeatherListAdapter adapter = new WeatherListAdapter(weatherList);
		setListAdapter(adapter);
	}

	// Getter for currently selected item
    public WeatherStatus getSelectedWeatherStatus()
    {
    	return _selectedWeatherStatus;
    }
	
	// Custom adapter
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
			Uri imageUri = curr.getWeatherIconUri();
			try {
				holder.weatherIconView.setImageBitmap(Media.getBitmap(getActivity().getContentResolver(), imageUri));
			}
			catch (Exception e) {
				Log.e(TAG, "Error reading bitmap: " + e.getMessage());
			}

			return newView;
		}

		private class ViewHolder {

			TextView  cityNameTextView;
			TextView  temperatureTextView;
			ImageView weatherIconView;
		}
	}
	
	// Debugging
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
