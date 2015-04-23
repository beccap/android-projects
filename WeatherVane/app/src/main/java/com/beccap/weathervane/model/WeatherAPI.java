/**
 * Weather API
 * 
 * URL, Token names, and other information about the Weather API
 */

package com.beccap.weathervane.model;

public class WeatherAPI {
	// URL and arguments
	public static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/find?units=imperial";
	public static final String WEATHER_URL_GET_LAT   = "&lat=";
	public static final String WEATHER_URL_GET_LON   = "&lon=";
	public static final String WEATHER_URL_GET_COUNT = "&cnt=";

	// primary weather list token
	public static final String WEATHER_ARRAY_TOKEN       = "list";

	// fields and objects within individual weather status record
	public static final String WEATHER_CITY_NAME_TOKEN   = "name";
	public static final String WEATHER_DATE_TIME_TOKEN   = "dt";
	public static final String WEATHER_COORD_TOKEN       = "coord";
	public static final String WEATHER_MAIN_TOKEN        = "main";
	public static final String WEATHER_WIND_TOKEN        = "wind";
	public static final String WEATHER_WEATHER_TOKEN     = "weather";

	// fields within "coord" object
	public static final String COORD_LAT_TOKEN           = "lat";
	public static final String COORD_LON_TOKEN           = "lon";

	// fields within "main" object
	public static final String MAIN_TEMP_TOKEN           = "temp";
	public static final String MAIN_PRESSURE_TOKEN       = "pressure";
	public static final String MAIN_HUMIDITY_TOKEN       = "humidity";

	// fields within "wind" object
	public static final String WIND_SPEED_TOKEN          = "speed";
	public static final String WIND_DIRECTION_TOKEN      = "deg";

	// field within "weather" array (within weather status) - only read first entry in weather array
	public static final String WEATHER_DESCRIPTION_TOKEN = "description";
}
