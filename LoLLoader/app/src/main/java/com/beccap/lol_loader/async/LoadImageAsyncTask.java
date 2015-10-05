package com.beccap.lol_loader.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.beccap.lol_loader.model.LoLImage;
import com.beccap.lol_loader.response.FlickrPage;
import com.beccap.lol_loader.response.FlickrPhoto;
import com.beccap.lol_loader.response.FlickrResponse;
import com.beccap.lol_loader.util.FlickrUrlBuilder;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.beccap.lol_loader.util.Random;
import java.util.logging.Logger;

/**
 * Created by beccap on 10/3/15.
 */
public class LoadImageAsyncTask extends AsyncTask<Void, Integer, LoLImage> {
    private static final String TAG = LoadImageAsyncTask.class.getSimpleName();

    private ImageLoadedListener callback;

    public LoadImageAsyncTask(ImageLoadedListener callback) {
        this.callback = callback;
    }

    public interface ImageLoadedListener {
        void onImageLoaded(LoLImage loLImage);
    }

    @Override
    protected LoLImage doInBackground(Void... params) {
        // query the Flickr API
        String jsonResponse = doFlickrSearchQuery();
        Log.d(TAG, "jsonResponse: " + jsonResponse);
        // parse the JSON to get the page of photos
        FlickrPage page = parseFlickrResponse(jsonResponse);
        Log.d(TAG, "page: " + page);
        Log.d(TAG, "perpage: " + page.getPerPage());
        Log.d(TAG, "total: " + page.getTotal());
        Log.d(TAG, "photo count: " + page.getPhotoCount());
        Log.d(TAG, "photos array: " + page.getPhotos());
        // select a random photo from what was returned;
        FlickrPhoto photo = randomPhotoFromPage(page);
        Log.d(TAG, "photo: " + photo);
        // build a LoLImage from photo data
        return readImageData(photo);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(LoLImage loLImage) {
        if (callback != null) {
            callback.onImageLoaded(loLImage);
        }
        super.onPostExecute(loLImage);
    }

    private String doFlickrSearchQuery() {
        try {
            String urlString = new FlickrUrlBuilder().withMaxPhotos(100).build();
            URL readURL = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) readURL.openConnection();

            // set connection properties
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setDoInput(true);

            // connect and read
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            // read the contents of URL into a string for parsing
            StringBuilder jsonStringBldr = new StringBuilder();
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                jsonStringBldr.append(currentLine);
            }

            return jsonStringBldr.toString();
        }
        catch (Exception e) {
            Log.e(TAG, "Error reading JSON: " + e.getMessage());
            return null;
        }
    }

    private FlickrPage parseFlickrResponse(String jsonString) {
        if (jsonString == null) {
            return null;
        }
        Gson gson = new Gson();

        FlickrResponse flickrResponse = gson.fromJson(jsonString, FlickrResponse.class);
        if (flickrResponse == null) {
            return null;
        }
        return flickrResponse.getPage();
    }

    private FlickrPhoto randomPhotoFromPage(FlickrPage page) {
        if (page == null) {
            return null;
        }

        List<FlickrPhoto> photos = page.getPhotos();
        int photoCount = page.getPhotoCount();

        if (photos != null) {
            int index = Random.getInt(photos.size());
            Log.d(TAG, "size: " + photos.size());
            return photos.get(index);
        }
        else {
            Log.e(TAG, "Photos array is NULL");
            return null;
        }
    }

    private LoLImage readImageData(FlickrPhoto photo) {
        if (photo != null) {
            String photoUrl = photo.toURL();

            Log.d(TAG, "Reading from photoUrl: " + photoUrl);
            try {
                URL readURL = new URL(photoUrl);
                HttpURLConnection conn = (HttpURLConnection) readURL.openConnection();

                // set connection properties
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                conn.setDoInput(true);

                // connect and read
                conn.connect();
                InputStream is = conn.getInputStream();

                Bitmap imageBitmap = BitmapFactory.decodeStream(is);

                // return new image data object
                Log.d(TAG, "success!");
                return new LoLImage(imageBitmap, photo.getStrippedTitle());
            }
            catch (Exception e) {
                Log.e(TAG, "Error reading Bitmap: " + e.getMessage());
            }
        }
        return null;
    }
}