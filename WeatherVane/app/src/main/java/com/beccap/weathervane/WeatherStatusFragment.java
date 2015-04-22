/**
 * WeatherStatusFragment
 * 
 * Displays the detail pane for a weather status.
 * 
 * WeatherStatus is passed in as JSON through arguments bundle.
 */

package com.beccap.weathervane;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beccap.weathervane.model.WeatherStatus;

public class WeatherStatusFragment extends Fragment {

	public static final String WEATHER_KEY = "weatherStatus_record";
	private static final String TAG = WeatherStatusFragment.class.toString();
	
	private WeatherStatus _weatherStatus = null;
	
	public static WeatherStatusFragment newInstance(WeatherStatus weatherStatus)
	{
		WeatherStatusFragment weatherStatusFragment = new WeatherStatusFragment();
		weatherStatusFragment._weatherStatus = weatherStatus;
		
		return weatherStatusFragment;
	}

    // Life Cycle
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		
		// restore weather data from savedInstanceState
		if (savedInstanceState != null) { // restarted with saved bundle
			String jsonString; // json representation of WeatherStatus
			jsonString = savedInstanceState.getString(WEATHER_KEY);
			try {
                if (jsonString != null) {
                    _weatherStatus = new WeatherStatus(new JSONObject(jsonString));
                }
                else {
                    _weatherStatus = null;
                }
			}
			catch (JSONException e) {
				Log.e(TAG, "Error loading savedInstanceState: " + e.getMessage());
			}
		}
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState)
    {
    	Log.d(TAG,"onCreateView");
    	super.onCreateView(inflater, parent, savedInstanceState);
    	
        View view = inflater.inflate(R.layout.fragment_detail, parent, false);
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
	
    
    // public method to update the contents of view based on current weather status
    public void update(WeatherStatus weatherStatus)
    {
    	_weatherStatus = weatherStatus;
    	updateView(getView());
    }
    
    // update UI based on this weather status
    private void updateView(View view)
    {
    	Log.d(TAG, "updateView");
    	TextView titleText = (TextView)view.findViewById(R.id.detail_text_title);
    	TextView locationLabel = (TextView)view.findViewById(R.id.detail_label_location);
        TextView locationText = (TextView)view.findViewById(R.id.detail_text_location);
      
        if (_weatherStatus != null) {
        	// set title text
        	titleText.setText("WeatherStatus: ");
        	
//        	String locationString = "(" + _weatherStatus.getLatitude() + ", " + _weatherStatus.getLongitude() + ")";
//        	locationText.setText(locationString);
        	locationLabel.setVisibility(View.VISIBLE);
        	locationText.setVisibility(View.VISIBLE);
        }
        else {
        	titleText.setVisibility(View.GONE);
        	locationLabel.setVisibility(View.GONE);
        	locationText.setVisibility(View.GONE);
        }
    }
}
