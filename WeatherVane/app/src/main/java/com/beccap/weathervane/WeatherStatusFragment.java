/**
 * WeatherStatusFragment
 * 
 * Displays the detail pane for a weather status.
 * 
 * WeatherStatus is passed in as JSON through arguments bundle.
 */

package com.beccap.weathervane;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beccap.weathervane.model.WeatherStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherStatusFragment extends Fragment {

	private static final String TAG = WeatherStatusFragment.class.toString();

	public static final String WEATHER_KEY = "weatherStatus_record";

	private WeatherStatus _weatherStatus = null;

	private TextView  _cityView;
	private TextView  _temperatureView;
	private ImageView _weatherIconView;
	private TextView  _windView;
	private TextView  _pressureView;
	private TextView  _humidityView;
	private TextView  _lastUpdatedView;
	
	public static WeatherStatusFragment newInstance(WeatherStatus weatherStatus)
	{
		WeatherStatusFragment weatherStatusFragment = new WeatherStatusFragment();
		weatherStatusFragment._weatherStatus = weatherStatus;
		
		return weatherStatusFragment;
	}

    //============ Life Cycle =====================================================================
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		
		// restore weather data from savedInstanceState
		if (savedInstanceState != null) { // restarted with saved bundle
			String jsonString; // json representation of WeatherStatus
			jsonString = savedInstanceState.getString(WEATHER_KEY);
			update(jsonString);
		}
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState)
    {
    	Log.d(TAG, "onCreateView");
    	super.onCreateView(inflater, parent, savedInstanceState);
    	
        View view = inflater.inflate(R.layout.fragment_detail, parent, false);

		_cityView        = (TextView)view.findViewById(R.id.detail_text_city);
		_temperatureView = (TextView)view.findViewById(R.id.detail_text_temperature);
		_windView        = (TextView)view.findViewById(R.id.detail_text_wind);
		_pressureView    = (TextView)view.findViewById(R.id.detail_text_pressure);
		_humidityView    = (TextView)view.findViewById(R.id.detail_text_humidity);
		_lastUpdatedView = (TextView)view.findViewById(R.id.detail_text_last_updated);

		_weatherIconView = (ImageView)view.findViewById(R.id.detail_weather_icon);

        updateView(view);
        
        return view;
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		Log.d(TAG, "onSaveInstanceState");
		super.onSaveInstanceState(outState);
		try {
			// save earthquake
			if (_weatherStatus != null) {
				outState.putString(WEATHER_KEY, _weatherStatus.toJSON().toString());
			}
		}
		catch (JSONException e) {
			Log.e(TAG, "Error saving instance state: " + e.getMessage());
		}
	}

	//============ Update methods =================================================================
    // public method to update the contents of view based on current weather status
    public void update(WeatherStatus weatherStatus)
    {
    	_weatherStatus = weatherStatus;
    	updateView(getView());
    }

	// alternative form of update that accepts a JSON String as a parameter
	public void update(String jsonString) {
		try {
			WeatherStatus weatherStatus;
			if (jsonString != null) {
				weatherStatus = new WeatherStatus(new JSONObject(jsonString));
			}
			else {
				weatherStatus = null;
			}
			update(weatherStatus);
		}
		catch (JSONException e) {
			Log.e(TAG, "Error parsing JSON String while updating WeatherStatus: " + e.getMessage());
		}
	}
    
    // update UI based on this weather status
    private void updateView(View view)
    {
		ViewGroup viewGroup = (ViewGroup)view;

    	Log.d(TAG, "updateView");
		int subviewVisibility = View.INVISIBLE;

        if (_weatherStatus != null) {
			_cityView.setText(_weatherStatus.getCityName());
			_temperatureView.setText(_weatherStatus.getTemperatureString());
			_weatherIconView.setImageBitmap(_weatherStatus.getWeatherIconBitmap());
			_windView.setText(_weatherStatus.getWindString());
			_pressureView.setText(_weatherStatus.getPressureString());
			_humidityView.setText(_weatherStatus.getHumidityString());
			_lastUpdatedView.setText(_weatherStatus.getFormattedDateTime());

			subviewVisibility = View.VISIBLE;
        }
		for (int i = 0; i < viewGroup.getChildCount(); ++i) {
			viewGroup.getChildAt(i).setVisibility(subviewVisibility);
		}
    }
}
