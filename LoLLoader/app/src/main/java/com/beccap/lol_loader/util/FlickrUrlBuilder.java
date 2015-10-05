package com.beccap.lol_loader.util;

import android.net.Uri;
import android.util.Log;

/**
 * Created by beccap on 10/3/15.
 */
public class FlickrUrlBuilder {

    private static final String TAG = FlickrUrlBuilder.class.getSimpleName();

    // https://api.flickr.com/services/rest/?method=flickr.photos.search
    // &api_key=a938c90023cf489d7b601dd7f6a4e304&tags=lolcat&sort=interestingness-desc&media=photos
    // &per_page=100&page=1&format=json&nojsoncallback=1
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.flickr.com";
    private static final String SERVICES_PATH = "services";
    private static final String REST_PATH = "rest";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final String API_KEY = "6696fb3954076c0881e76a3f1b87b06e";
    private static final String SEARCH_TEXT = "lolcat";
    private static final String SORT_TYPE = "interesting-desc";
    private static final String MEDIA_TYPE = "photos";
    private static final String PAGE_NUMBER = "1";
    private static final String RESPONSE_FORMAT = "json";

    private int maxPhotos = 100;
    private String searchText = SEARCH_TEXT;

    public FlickrUrlBuilder withMaxPhotos(int maxPhotos) {
        this.maxPhotos = maxPhotos;
        return this;
    }

    public FlickrUrlBuilder withSearchText(String searchText) {
        this.searchText = searchText;
        return this;
    }

    public String build() {
        Uri.Builder uriBuilder = new Uri.Builder();

        uriBuilder.scheme(SCHEME)
                  .authority(AUTHORITY)
                  .appendPath(SERVICES_PATH)
                  .appendPath(REST_PATH)
                  .appendQueryParameter("method", SEARCH_METHOD)
                  .appendQueryParameter("api_key", API_KEY)
                  .appendQueryParameter("text", searchText)
                  .appendQueryParameter("sort", SORT_TYPE)
                  .appendQueryParameter("media", MEDIA_TYPE)
                  .appendQueryParameter("per_page", Integer.toString(maxPhotos))
                  .appendQueryParameter("page", PAGE_NUMBER)
                  .appendQueryParameter("format", RESPONSE_FORMAT)
                  .appendQueryParameter("nojsoncallback", "1");

        String url = uriBuilder.build().toString();
        Log.d(TAG, "built url: " + url);
        return url;
    }
}
