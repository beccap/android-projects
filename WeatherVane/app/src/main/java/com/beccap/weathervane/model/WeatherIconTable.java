/**
 * Implements a hash table for storing weather icons downloaded from the weather api.
 *
 * All methods are static.
 *
 * findIconBitmap simply looks for a key (given by the weather api) and returns a bitmap if it was
 *    found; otherwise returns null.
 *
 * loadIconBitmap will look the key up in the hash table and load it from the api if it is not
 *    already in the hash table. Should not be called on the UI thread.
 *
 * getIconCount returns the number of icons currently stored in the hash table.
 */
package com.beccap.weathervane.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class WeatherIconTable {

    private static final String TAG = WeatherIconTable.class.toString();

    // place where all the bitmaps we've loaded so far are stored locally
    private static HashMap <String, Bitmap> bitmapTable = new HashMap<String, Bitmap>();

    // This method sets the bitmap from the Hash Table (safe to be called on the UI thread)
    // Will return null if the bitmap isn't there.
    public static Bitmap findIconBitmap(String weatherIconString) {
        return bitmapTable.get(weatherIconString);
    }

    // This method should not be called on the UI thread, as it downloads from the internet.
    public static Bitmap loadIconBitmap(String weatherIconString) {
        Bitmap iconBitmap = findIconBitmap(weatherIconString);
        if (iconBitmap != null) {
            return iconBitmap;
        }

        else try {
            String iconUrl = WeatherAPI.WEATHER_URL +
                    WeatherAPI.WEATHER_ICON_QUERY +
                    weatherIconString + ".png";
            URL readURL = new URL(iconUrl);
            HttpURLConnection conn = (HttpURLConnection) readURL.openConnection();

            // set connection properties
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);

            // connect and read
            conn.connect();
            InputStream is = conn.getInputStream();
            iconBitmap = BitmapFactory.decodeStream(is);

            bitmapTable.put(weatherIconString, iconBitmap);
        }
        catch (IOException e) {
            // Log exception
            Log.e(TAG, "Error reading bitmap: " + e.getMessage());
        }
        return iconBitmap;
    }

    // getter for icon count
    public static int getIconCount() {
        return bitmapTable.size();
    }
}
