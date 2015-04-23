/** 
 * WeatherStatus class
 * 
 * Serialization and getters for WeatherStatus object
 */

package com.beccap.weathervane.model;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherStatus implements Serializable
{
	public static final long serialVersionUUID = 0L;

	private String _cityName;
	private double _lat, _lon;
	private double _currentTemp;
	private double _minTemp, _maxTemp;
	private double _pressure;
	private double _humidity;
	private double _windSpeed;
	private double _windDirection;
	private String _weatherDescription;

	// serialization - input (constructor)
	public WeatherStatus(JSONObject json) throws JSONException {
		JSONObject coord   = json.getJSONObject(WeatherAPI.WEATHER_COORD_TOKEN);
		JSONObject main    = json.getJSONObject(WeatherAPI.WEATHER_MAIN_TOKEN);
		JSONObject wind    = json.getJSONObject(WeatherAPI.WEATHER_WIND_TOKEN);
        JSONObject weather = json.getJSONArray(WeatherAPI.WEATHER_WEATHER_TOKEN).getJSONObject(0);

		_cityName = json.getString(WeatherAPI.WEATHER_CITY_NAME_TOKEN);

		_lat = coord.getDouble(WeatherAPI.COORD_LAT_TOKEN);
		_lon = coord.getDouble(WeatherAPI.COORD_LON_TOKEN);

		_currentTemp = main.getDouble(WeatherAPI.MAIN_TEMP_TOKEN);
		_minTemp     = main.getDouble(WeatherAPI.MAIN_MIN_TEMP_TOKEN);
		_maxTemp     = main.getDouble(WeatherAPI.MAIN_MAX_TEMP_TOKEN);
		_pressure    = main.getDouble(WeatherAPI.MAIN_PRESSURE_TOKEN);
		_humidity    = main.getDouble(WeatherAPI.MAIN_HUMIDITY_TOKEN);

		_windSpeed   = wind.getDouble(WeatherAPI.WIND_SPEED_TOKEN);
		_windDirection = wind.getDouble(WeatherAPI.WIND_DIRECTION_TOKEN);

		_weatherDescription = weather.getString(WeatherAPI.WEATHER_DESCRIPTION_TOKEN);
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

		coord.put(WeatherAPI.COORD_LAT_TOKEN, _lat);
		coord.put(WeatherAPI.COORD_LON_TOKEN, _lon);
		json.put(WeatherAPI.WEATHER_COORD_TOKEN, coord);

		main.put(WeatherAPI.MAIN_TEMP_TOKEN, _currentTemp);
		main.put(WeatherAPI.MAIN_MIN_TEMP_TOKEN, _minTemp);
		main.put(WeatherAPI.MAIN_MAX_TEMP_TOKEN, _maxTemp);
		main.put(WeatherAPI.MAIN_PRESSURE_TOKEN, _pressure);
		main.put(WeatherAPI.MAIN_HUMIDITY_TOKEN, _humidity);
		json.put(WeatherAPI.WEATHER_MAIN_TOKEN, main);

		wind.put(WeatherAPI.WIND_SPEED_TOKEN, _windSpeed);
		wind.put(WeatherAPI.WIND_DIRECTION_TOKEN, _windDirection);
		json.put(WeatherAPI.WEATHER_WIND_TOKEN, wind);

		weather.put(WeatherAPI.WEATHER_DESCRIPTION_TOKEN, _weatherDescription);
		weatherArray.put(0,weather);
		json.put(WeatherAPI.WEATHER_WEATHER_TOKEN, weatherArray);

		return json;
	}

	// getters
	public String getCityName() {
		return _cityName;
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

	public double getMinTemp() {
		return _minTemp;
	}

	public double getMaxTemp() {
		return _maxTemp;
	}

	public double getPressure() {
		return _pressure;
	}

	public double getHumidity() {
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

	// description (for debugging)
	public String toString() {
		String result = "City Name: " + _cityName + "\n" +
				        "Lat: " + _lat + "; Lon: " + _lon + "\n" +
				        "Current Temp: " + _currentTemp + "; Min: " + _minTemp + "; Max: " + _maxTemp + "\n" +
				        "Pressure: " + _pressure + "; Humidity: " + _humidity + "\n" +
						"Wind Speed: " + _windSpeed + "; Wind Direction: " + _windDirection + "\n" +
						"Description: " + _weatherDescription;
		return result;
	}
}
