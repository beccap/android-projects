package com.beccap.lol_loader.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * The top level object of the Flickr response. In place for gson parsing of response JSON.
 */
public class FlickrPage {
    @SerializedName("perpage")
    private String perPage; // pictures per page

    private String total; // total number of pictures

    @SerializedName("photo")
    private List<FlickrPhoto> photos;

    // Getters
    public String getPerPage() {
        return perPage;
    }

    public String getTotal() {
        return total;
    }

    public List<FlickrPhoto> getPhotos() {
        return photos;
    }
}
