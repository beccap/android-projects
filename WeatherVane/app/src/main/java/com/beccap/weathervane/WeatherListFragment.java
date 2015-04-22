/**
 * This Fragment manages the list.
 */

package com.beccap.weathervane;

import com.beccap.weathervane.model.WeatherLoader;

import java.util.ArrayList;

import android.app.Activity;
//import android.graphics.Color;
//import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beccap.weathervane.model.WeatherLoader;
import com.beccap.weathervane.model.WeatherStatus;

public class WeatherListFragment extends ListFragment implements WeatherLoader.Listener {
	
	private static final String TAG = WeatherListFragment.class.toString();
	
	public interface OnWeatherStatusSelectedListener {
		void onWeatherStatusSelected(WeatherStatus weatherStatus);
	}
	
	private OnWeatherStatusSelectedListener _onSelectedListener;
	private WeatherStatus                   _selectedWeatherStatus = null;
    private int                             _currentPos = -1;

	// Life Cycle
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true); // ensures we don't have to reload the json upon rotation
		
		Log.d(TAG, "in onCreate");
//		new WeatherLoader(this).startLoading(location);
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
	
	@Override
	public void onListItemClick(ListView listView, View view, int pos, long id)
	{
		WeatherStatus weatherStatus = (WeatherStatus)getListAdapter().getItem(pos);
		_selectedWeatherStatus = weatherStatus;
		_onSelectedListener.onWeatherStatusSelected(weatherStatus);
        _currentPos = pos;
        ((WeatherListAdapter)getListAdapter()).notifyDataSetChanged();
    }
	
	// WeatherLoader.Listener callback
	public void onWeatherLoaded(ArrayList<WeatherStatus> weatherList)
	{
//		testWeatherList(weatherList);
		if (weatherList == null) {
			Log.e(TAG, "null Weather List passed to onWeatherLoaded");
			return;
		}
		WeatherListAdapter adapter = new WeatherListAdapter(weatherList);
		setListAdapter(adapter);
	}
    
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
			return convertView;
		}
	}
	
	// Debugging
//	private void testWeatherList(ArrayList<WeatherStatus> weatherList)
//	{
//		if (weatherList == null) {
//			Log.e(TAG, "null WeatherList passed to testWeatherList");
//			return;
//		}
//		Log.d(TAG,"WeatherList (size = " + weatherList.size() + "):\n");
//		for (int i = 0; i < weatherList.size(); ++i) {
//			Log.d(TAG, weatherList.get(i).toString() + "\n");
//		}
//	}
}
