/** 
 * WeatherStatus class
 * 
 * Serialization and getters for WeatherStatus object
 */

package com.beccap.weathervane.model;

import android.net.Uri;
import android.util.Log;

import com.beccap.weathervane.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherStatus implements Serializable
{
	private static final String TAG = WeatherStatus.class.toString();

	public static final long serialVersionUUID = 0L;

	private String _cityName;
	private long   _dateTime;
	private double _lat, _lon;
	private double _currentTemp;
	private double _pressure;
	private int    _humidity;
	private double _windSpeed;
	private double _windDirection;
	private String _weatherDescription;
	private String _weatherIcon;

	private String _formattedDateTime;

	private static final String DEGREE = "\u00b0";
	private static final SimpleDateFormat _dateFormatter =
			new SimpleDateFormat("M/d/yyyy h:mm a", java.util.Locale.getDefault());

	// serialization - input (constructor)
	public WeatherStatus(JSONObject json) throws JSONException {
		JSONObject coord   = json.getJSONObject(WeatherAPI.WEATHER_COORD_TOKEN);
		JSONObject main    = json.getJSONObject(WeatherAPI.WEATHER_MAIN_TOKEN);
		JSONObject wind    = json.getJSONObject(WeatherAPI.WEATHER_WIND_TOKEN);
        JSONObject weather = json.getJSONArray(WeatherAPI.WEATHER_WEATHER_TOKEN).getJSONObject(0);

		_cityName = json.getString(WeatherAPI.WEATHER_CITY_NAME_TOKEN);
		_dateTime = json.getLong(WeatherAPI.WEATHER_DATE_TIME_TOKEN);

		_lat = coord.getDouble(WeatherAPI.COORD_LAT_TOKEN);
		_lon = coord.getDouble(WeatherAPI.COORD_LON_TOKEN);

		_currentTemp = main.getDouble(WeatherAPI.MAIN_TEMP_TOKEN);
		_pressure = main.getDouble(WeatherAPI.MAIN_PRESSURE_TOKEN);
		_humidity = main.getInt(WeatherAPI.MAIN_HUMIDITY_TOKEN);

		_windSpeed = wind.getDouble(WeatherAPI.WIND_SPEED_TOKEN);
		_windDirection = wind.getDouble(WeatherAPI.WIND_DIRECTION_TOKEN);

		_weatherDescription = weather.getString(WeatherAPI.WEATHER_DESCRIPTION_TOKEN);
		_weatherIcon        = weather.getString(WeatherAPI.WEATHER_ICON_TOKEN);

		// create a formatted date-time string by converting ms since epoch to Date
		// (NOTE: _dateTime given in seconds; must convert to millis first)
		_formattedDateTime = _dateFormatter.format(new Date(_dateTime * 1000L));
	}

	// serialization - output
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject coord = new JSONObject();
		JSONObject main  = new JSONObject();
		JSONObject wind  = new JSONObject();
		JSONArray  weatherArray = new JSONArray();
		JSONObject weather = new JSONObject();

		json.put(WeatherAPI.WEATHER_CITY_NAME_TOKEN, _cityName);
		json.put(WeatherAPI.WEATHER_DATE_TIME_TOKEN, _dateTime);

		coord.put(WeatherAPI.COORD_LAT_TOKEN, _lat);
		coord.put(WeatherAPI.COORD_LON_TOKEN, _lon);
		json.put(WeatherAPI.WEATHER_COORD_TOKEN, coord);

		main.put(WeatherAPI.MAIN_TEMP_TOKEN, _currentTemp);
		main.put(WeatherAPI.MAIN_PRESSURE_TOKEN, _pressure);
		main.put(WeatherAPI.MAIN_HUMIDITY_TOKEN, _humidity);
		json.put(WeatherAPI.WEATHER_MAIN_TOKEN, main);

		wind.put(WeatherAPI.WIND_SPEED_TOKEN, _windSpeed);
		wind.put(WeatherAPI.WIND_DIRECTION_TOKEN, _windDirection);
		json.put(WeatherAPI.WEATHER_WIND_TOKEN, wind);

		weather.put(WeatherAPI.WEATHER_DESCRIPTION_TOKEN, _weatherDescription);
		weather.put(WeatherAPI.WEATHER_ICON_TOKEN, _weatherIcon);
		weatherArray.put(0,weather);
		json.put(WeatherAPI.WEATHER_WEATHER_TOKEN, weatherArray);

		return json;
	}

	// getters
	public String getCityName() {
		return _cityName;
	}

	public long getDateTime() {
		return _dateTime;
	}

	public double getLat() {
		return _lat;
	}

	public double getLon() {
		return _lon;
	}

	public double getCurrentTemp() {
		return _currentTemp;
	}

	public double getPressure() {
		return _pressure;
	}

	public int getHumidity() {
		return _humidity;
	}

	public double getWindSpeed() {
		return _windSpeed;
	}

	public double getWindDirection() {
		return _windDirection;
	}

	public String getWeatherDescription() {
		return _weatherDescription;
	}

	public String getFormattedDateTime() {
		return(_formattedDateTime);
	}

	public String getPressureString() {
		return Integer.toString((int)(_pressure + .5)) + " hpa";
	}

	public String getHumidityString() {
		return Integer.toString(_humidity) + "%";
	}

	public String getTemperatureString() {
		return Integer.toString((int)(_currentTemp + .5)) + DEGREE + " F";
	}

	public String getWindSpeedString() {
		return Double.toString(_windSpeed) + " mph";
	}

	public String getWindDirectionString() {
		String desc;

		if (_windDirection < 11.25)       { desc = "N"; }
		else if (_windDirection < 33.75)  { desc = "NNE"; }
		else if (_windDirection < 56.25)  { desc = "NE"; }
		else if (_windDirection < 78.75)  { desc = "ENE"; }
		else if (_windDirection < 101.25) { desc = "E"; }
		else if (_windDirection < 123.75) { desc = "ESE"; }
		else if (_windDirection < 146.25) { desc = "SE"; }
		else if (_windDirection < 168.75) { desc = "SSE"; }
		else if (_windDirection < 191.25) { desc = "S"; }
		else if (_windDirection < 213.75) { desc = "SSW"; }
		else if (_windDirection < 236.25) { desc = "SW"; }
		else if (_windDirection < 258.75) { desc = "WSW"; }
		else if (_windDirection < 281.25) { desc = "W"; }
		else if (_windDirection < 303.75) { desc = "WNW"; }
		else if (_windDirection < 326.25) { desc = "NW"; }
		else if (_windDirection < 348.75) { desc = "NNW"; }
		else                              { desc = "N"; }

		return Integer.toString((int)(_windDirection + .5)) + DEGREE + " (" + desc + ")";
	}

	public Uri getWeatherIconUri() {

		// get path to drawable
		String path = "android.resource://" + MainActivity.PACKAGE_NAME + "/drawable/icon" + _weatherIcon;
		Log.d(TAG, "path to icon: " + path);

		return Uri.parse(path);
	}

	// description (for debugging)
	public String toString() {
		String result = "City Name: " + _cityName + "\n" +
				        "Date/Time: " + getFormattedDateTime() + "\n" +
				        "Lat: " + _lat + "; Lon: " + _lon + "\n" +
				        "Current Temp: " + getTemperatureString() + "\n" +
				        "Pressure: " + getPressureString() + "; Humidity: " + getHumidityString() + "\n" +
						"Wind Speed: " + getWindSpeedString() + "; Wind Direction: " + getWindDirectionString() + "\n" +
						"Description: " + getWeatherDescription() + "; Icon: " + _weatherIcon;
		return result;
	}
}
