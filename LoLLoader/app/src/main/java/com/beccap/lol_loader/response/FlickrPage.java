package com.beccap.lol_loader.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by beccap on 10/3/15.
 */
public class FlickrPage {
    @SerializedName("perpage")
    private String perPage; // pictures per page
    private String total; // total number of pictures
    @SerializedName("photo")
    private List<FlickrPhoto> photos;

    public String getPerPage() {
        return perPage;
    }

    public void setPerPage(String perPage) {
        this.perPage = perPage;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<FlickrPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<FlickrPhoto> photos) {
        this.photos = photos;
    }
}
