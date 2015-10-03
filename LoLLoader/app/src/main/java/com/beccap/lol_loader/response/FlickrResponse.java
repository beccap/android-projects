package com.beccap.lol_loader.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by beccap on 10/3/15.
 */
public class FlickrResponse {
    @SerializedName("photos")
    private FlickrPage page;

    public FlickrPage getPage() {
        return page;
    }

    public void setPage(FlickrPage page) {
        this.page = page;
    }
}
