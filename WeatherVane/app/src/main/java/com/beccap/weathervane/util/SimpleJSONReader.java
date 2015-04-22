package com.beccap.weathervane.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * SimpleJSONReader
 *
 * Given a URL for an API, download and read a JSON string and send it to a listener when complete.
 */
public class SimpleJSONReader extends AsyncTask<Void, Void, String>
{
    public interface Listener {
        void onReadCompleted(String jsonString);
    }

    private static final String TAG = SimpleJSONReader.class.toString();

    private Listener _listener;
    private String   _stringUrl;

    public SimpleJSONReader(Listener listener, String stringUrl) {
        _listener  = listener;
        _stringUrl = stringUrl;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        // read in a string representing a one-dimensional array of JSON objects
        try {
            URL readURL = new URL(_stringUrl);
            HttpURLConnection conn = (HttpURLConnection) readURL.openConnection();

            // set connection properties
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);

            // connect and read
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            // read the contents of URL into a string for later parsing
            StringBuilder jsonStringBldr = new StringBuilder();
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                jsonStringBldr.append(currentLine);
            }

            // send the string to onPostExecute
            return jsonStringBldr.toString();
        }
        catch (Exception e) {
            Log.e(TAG,"Error reading JSON: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        // pass result back to listener
        _listener.onReadCompleted(jsonString);
    }
}

