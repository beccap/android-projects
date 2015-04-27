/**
 * WeatherStatusFragment
 * 
 * Displays the detail pane for a weather status and current location.
 * 
 * Both WeatherStatus and CurrentLocation can be set using newInstance when created.
 * They can also be updated by calling the public method update().
 *
 */

package com.beccap.weathervane;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beccap.weathervane.model.WeatherStatus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class WeatherStatusFragment extends Fragment implements OnMapReadyCallback{

	private static final String TAG = WeatherStatusFragment.class.toString();

	private static final int MAP_ZOOM_LEVEL = 8;

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
	private Marker    _currentLocMarker = null;
	private Marker    _weatherStatusMarker = null;
	
	public static WeatherStatusFragment newInstance(WeatherStatus weatherStatus, Location currentLocation)
	{
		WeatherStatusFragment weatherStatusFragment = new WeatherStatusFragment();
		weatherStatusFragment._weatherStatus = weatherStatus;
		weatherStatusFragment._currentLocation = currentLocation;

		return weatherStatusFragment;
	}

    //============ Life Cycle =====================================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState)
    {
    	super.onCreateView(inflater, parent, savedInstanceState);
    	
        View view = inflater.inflate(R.layout.fragment_detail, parent, false);

		// initialize map view handle and load
		_mapView = (MapView)view.findViewById(R.id.map_view);
		_mapView.onCreate(savedInstanceState);
		_mapView.getMapAsync(this);

		// initialize changeable text and image view handles
		_cityView        = (TextView)view.findViewById(R.id.detail_text_city);
		_temperatureView = (TextView)view.findViewById(R.id.detail_text_temperature);
		_windView        = (TextView)view.findViewById(R.id.detail_text_wind);
		_pressureView    = (TextView)view.findViewById(R.id.detail_text_pressure);
		_humidityView    = (TextView)view.findViewById(R.id.detail_text_humidity);
		_lastUpdatedView = (TextView)view.findViewById(R.id.detail_text_last_updated);

		_weatherIconView = (ImageView)view.findViewById(R.id.detail_weather_icon);

		// set text and images corresponding to WeatherStatus
        updateView(view);

        return view;
    }

	@Override
	public void onResume() {
		super.onResume();
		_mapView.onResume();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
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
		_googleMap = googleMap;
		_googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		_googleMap.setIndoorEnabled(false); // turns off floor-plan views for high zoom levels
		updateMap();
	}

	//============ Update methods =================================================================
    // public method to update the contents of view based on current weather status
    public void update(WeatherStatus weatherStatus, Location currentLocation)
    {
    	_weatherStatus   = weatherStatus;
		_currentLocation = currentLocation;
    	updateView(getView());
		updateMap();
    }

    // update detail view based on selected weather status
    private void updateView(View view)
    {
		ViewGroup viewGroup = (ViewGroup)view;

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

		// set visibility for detail text - make it go away if
		// there is no selected weather status
		for (int i = 0; i < viewGroup.getChildCount(); ++i) {
			viewGroup.getChildAt(i).setVisibility(subviewVisibility);
		}
    }

	// update the map based on the current location and selected weather status
	private void updateMap() {

		// make sure the map has been initialized and that we have a location
		if (_googleMap == null || _currentLocation == null) {
			return;
		}

		// center map on current location
		LatLng latlng = new LatLng(_currentLocation.getLatitude(), _currentLocation.getLongitude());
		_googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, MAP_ZOOM_LEVEL));

		// ADD MARKERS
		// current location
		if (_currentLocMarker != null) {
			_currentLocMarker.remove();
		}
		_currentLocMarker = _googleMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title(getResources().getString(R.string.marker_current_location_label))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		// weather status location
		if (_weatherStatusMarker != null) {
			_weatherStatusMarker.remove();
		}
		if (_weatherStatus != null) {
			_weatherStatusMarker = _googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(_weatherStatus.getLat(), _weatherStatus.getLon()))
					.title(_weatherStatus.getCityName())
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
		}
		_mapView.setVisibility(View.VISIBLE);
	}
}
