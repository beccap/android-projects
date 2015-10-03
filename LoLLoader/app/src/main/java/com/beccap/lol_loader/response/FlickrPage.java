package com.beccap.lol_loader.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by beccap on 10/3/15.
 */
public class FlickrPage {
    @SerializedName("perpage")
    private String perPage; // pictures per page
    private String total; // total number of pictures
    @SerializedName("photos")
    private FlickrPhoto[] photos;

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

    public FlickrPhoto[] getPhotos() {
        return photos;
    }

    public void setPhotos(FlickrPhoto[] photos) {
        this.photos = photos;
    }

    public int getPhotoCount() {
        if (perPage == null) {
            return 0;
        }
        int perPageCount = Integer.parseInt(perPage);
        int totalCount   = Integer.parseInt(total);

        return(Math.min(perPageCount,totalCount));
    }
}
