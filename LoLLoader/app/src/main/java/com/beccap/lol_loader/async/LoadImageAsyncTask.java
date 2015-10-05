package com.beccap.lol_loader.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.beccap.lol_loader.model.LoLImage;
import com.beccap.lol_loader.response.FlickrPage;
import com.beccap.lol_loader.response.FlickrPhoto;
import com.beccap.lol_loader.response.FlickrResponse;
import com.beccap.lol_loader.util.FlickrUrlBuilder;
import com.beccap.lol_loader.util.Random;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * LoadImageAsyncTask
 *
 * Retrieves list of images from Flickr; randomly chooses one image and loads it.
 * On completion, calls ImageLoaderListener callback method with image and title
 * data for display.
 *
 * Optional parameters allow caller to specify the number of pictures to choose from and
 * the search text.
 */
public class LoadImageAsyncTask extends AsyncTask<Void, Integer, LoLImage> {
    private static final String TAG = LoadImageAsyncTask.class.getSimpleName();

    // defaults for optional parameters
    private static final int    DEFAULT_PAGE_SIZE = 100;
    private static final String DEFAULT_SEARCH_TEXT = "lolcat";

    // callback interface
    public interface ImageLoadedListener {
        void onImageLoaded(LoLImage loLImage);
    }

    private ImageLoadedListener callback; // object to notify when done; required

    // optional parameters
    private int    pageSize;   // how many pictures do we want to choose from
    private String searchText; // what term do we want to search with

    // constructor
    public LoadImageAsyncTask(ImageLoadedListener callback) {
        this.callback = callback;
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.searchText = DEFAULT_SEARCH_TEXT;
    }

    // flexible parameter setting, follows build pattern
    LoadImageAsyncTask withPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    LoadImageAsyncTask withSearchText(String searchText) {
        this.searchText = searchText;
        return this;
    }

    // background operations
    @Override
    protected LoLImage doInBackground(Void... params) {
        // query the Flickr API
        String jsonResponse = doFlickrSearchQuery();
//        Log.d(TAG, "jsonResponse: " + jsonResponse);
        // parse the JSON to get the page of photos
        FlickrPage page = parseFlickrResponse(jsonResponse);
        // select a random photo from what was returned;
        FlickrPhoto photo = randomPhotoFromPage(page);
        // build a LoLImage from photo data
        return readImageData(photo);
    }

    // post-processing on main UI thread
    @Override
    protected void onPostExecute(LoLImage loLImage) {
        // let listener know we're done, and pass data back
        if (callback != null) {
            callback.onImageLoaded(loLImage);
        }
        super.onPostExecute(loLImage);
    }

    /**
     * doFlickrSearchQuery()
     * Build URL for query, send query, wait for response
     *
     * @return JSON result as String
     */
    private String doFlickrSearchQuery() {
        try {
            // build url for query
            String urlString = new FlickrUrlBuilder()
                    .withMaxPhotos(pageSize)
                    .withSearchText(searchText)
                    .build();

//            Log.d(TAG,"Query string: " + urlString);
            URL readURL = new URL(urlString);

            // open connection
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

            // return JSON as a String
            return jsonStringBldr.toString();
        }
        catch (Exception e) {
            Log.e(TAG, "Error reading JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * parseFlickrResponse()
     *
     * turns response JSON into FlickrPage object using gson
     *
     * @param jsonString - search response JSON as string
     * @return FlickrPage - search response as POJO
     */
    private FlickrPage parseFlickrResponse(String jsonString) {
        if (jsonString == null) {
            Log.e(TAG,"Null response JSON String");
            return null;
        }
        Gson gson = new Gson();

        FlickrResponse flickrResponse = gson.fromJson(jsonString, FlickrResponse.class);
        if (flickrResponse == null) {
            return null;
        }
        return flickrResponse.getPage();
    }

    /**
     * randomPhotoFromPage()
     *
     * Selects one photo randomly from search response
     *
     * @param page - Flickr search response as POJO
     * @return FlickrPhoto - randomly selected photo (as response POJO)
     */
    private FlickrPhoto randomPhotoFromPage(FlickrPage page) {
        if (page == null) {
            Log.e(TAG, "Null response POJO");
            return null;
        }

        List<FlickrPhoto> photos = page.getPhotos();

        if (photos != null) {
            int index = Random.getInt(photos.size());
            return photos.get(index);
        }
        else {
            Log.e(TAG, "Null photos list");
            return null;
        }
    }

    /**
     * readImageData()
     *
     * Creates URL from response data
     * Reads contents of URL into bitmap
     * Returns LolImage model containing bitmap and title, to be consumed by UI
     *
     * @param photo - response POJO containing data for single image
     * @return LoLImage
     */
    private LoLImage readImageData(FlickrPhoto photo) {
        if (photo != null) {
            String photoUrl = photo.toUrl();
//            Log.d(TAG, "Photo URL: ");

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
                return new LoLImage(imageBitmap, photo.getStrippedTitle());
            }
            catch (Exception e) {
                Log.e(TAG, "Error reading Bitmap: " + e.getMessage());
            }
        }
        return null;
    }
}
