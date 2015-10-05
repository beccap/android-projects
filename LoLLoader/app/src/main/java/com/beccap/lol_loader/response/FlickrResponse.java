package com.beccap.lol_loader.response;

import com.google.gson.annotations.SerializedName;

/**
 * The main response POJO, for gson-to-JSON parsing
 */
public class FlickrResponse {
    @SerializedName("photos")
    private FlickrPage page;

    public FlickrPage getPage() {
        return page;
    }
}
