/**
 * WeatherStatusFragment
 * 
 * Displays the detail pane for a weather status.
 * 
 * WeatherStatus is passed in as JSON through arguments bundle.
 */

package com.beccap.weathervane;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beccap.weathervane.model.WeatherStatus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherStatusFragment extends Fragment implements OnMapReadyCallback{

	private static final String TAG = WeatherStatusFragment.class.toString();

	public static final String WEATHER_KEY   = "key_weatherStatus";
	public static final String LATITUDE_KEY  = "key_latitude";
	public static final String LONGITUDE_KEY = "key_longitude";

	private static final int MAP_ZOOM_LEVEL = 9;

	private WeatherStatus _weatherStatus = null;
	private Location      _currentLocation = null;

	private MapView   _mapView;
	private TextView  _cityView;
	private TextView  _temperatureView;
	private ImageView _weatherIconView;
	private TextView  _windView;
	private TextView  _pressureView;
	private TextView  _humidityView;
	private TextView  _lastUpdatedView;

	private GoogleMap _googleMap = null;
	
	public static WeatherStatusFragment newInstance(WeatherStatus weatherStatus, Location currentLocation)
	{
		WeatherStatusFragment weatherStatusFragment = new WeatherStatusFragment();
		weatherStatusFragment._weatherStatus = weatherStatus;
		weatherStatusFragment._currentLocation = currentLocation;

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

		MapsInitializer.initialize(getActivity());
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState)
    {
    	Log.d(TAG, "onCreateView");
    	super.onCreateView(inflater, parent, savedInstanceState);
    	
        View view = inflater.inflate(R.layout.fragment_detail, parent, false);

		_mapView = (MapView)view.findViewById(R.id.map_view);
		Log.d(TAG,"calling mapView.onCreate()");
		_mapView.onCreate(savedInstanceState);
		_mapView.getMapAsync(this);

		_cityView        = (TextView)view.findViewById(R.id.detail_text_city);
		_temperatureView = (TextView)view.findViewById(R.id.detail_text_temperature);
		_windView        = (TextView)view.findViewById(R.id.detail_text_wind);
		_pressureView    = (TextView)view.findViewById(R.id.detail_text_pressure);
		_humidityView    = (TextView)view.findViewById(R.id.detail_text_humidity);
		_lastUpdatedView = (TextView)view.findViewById(R.id.detail_text_last_updated);

		_weatherIconView = (ImageView)view.findViewById(R.id.detail_weather_icon);

		Log.d(TAG, "calling updateView()");
        updateView(view);

		Log.d(TAG,"onCreateView() end");
        return view;
    }

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG,"calling onResume");
		_mapView.onResume();
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
		_mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		_mapView.onLowMemory();
	}

	@Override
	public void onPause() {
		_mapView.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		_mapView.onDestroy();
		super.onDestroy();
	}

	//============ OnMapReadyCallback =============================================================
	@Override
	public void onMapReady(GoogleMap googleMap) {
		Log.d(TAG, "onMapReady, setting up map");
		_googleMap = googleMap;
		_googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		_googleMap.setIndoorEnabled(false);
		Log.d(TAG, "calling updateMap from onMapReady()");
		updateMap();
	}

	//============ Update methods =================================================================
    // public method to update the contents of view based on current weather status
    public void update(WeatherStatus weatherStatus, Location currentLocation)
    {
    	_weatherStatus   = weatherStatus;
		_currentLocation = currentLocation;
    	updateView(getView());
		Log.d(TAG, "calling updateMap from update()");
		updateMap();
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
			_weatherStatus = weatherStatus;

			View view = getView();
			if (view != null) {
				updateView(view);
			}
		}
		catch (JSONException e) {
			Log.e(TAG, "Error parsing JSON String while updating WeatherStatus: " + e.getMessage());
		}
	}

	private void updateMap() {
		// make sure the map has been initialized and that we have a location
		if (_googleMap == null || _currentLocation == null) {
			return;
		}
		Log.d(TAG,"setting up camera");
		// center map on current location
		LatLng latlng = new LatLng(_currentLocation.getLatitude(), _currentLocation.getLongitude());
		_googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, MAP_ZOOM_LEVEL));

		Log.d(TAG,"setting up first marker");

		// add markers
		// current location
		_googleMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title("You are here")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

		// weather status location
		if (_weatherStatus != null) {
			_googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(_weatherStatus.getLat(), _weatherStatus.getLon()))
					.title(_weatherStatus.getCityName())
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
		}
		Log.d(TAG,"setting mapview visibility");
		_mapView.setVisibility(View.VISIBLE);
		Log.d(TAG,"end of updateMap");
	}

    // update UI based on this weather status
    private void updateView(View view)
    {
		ViewGroup viewGroup = (ViewGroup)view;

    	Log.d(TAG, "updateView");
		int subviewVisibility = View.GONE;

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
		else {
			_cityView.setText(R.string.detail_not_selected);
		}
		for (int i = 0; i < viewGroup.getChildCount(); ++i) {
			viewGroup.getChildAt(i).setVisibility(subviewVisibility);
		}
		_cityView.setVisibility(View.VISIBLE);
    }
}
