/** 
 * WeatherStatus class
 * 
 * Serialization and getters for WeatherStatus object
 */

package com.beccap.weathervane.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherStatus implements Serializable
{
	public static final long serialVersionUUID = 0L;

	// serialization - input
	public WeatherStatus(JSONObject json) throws JSONException {
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		return json;
	}
	

	public String toString() {
		String result = "";
		return result;
	}
}
