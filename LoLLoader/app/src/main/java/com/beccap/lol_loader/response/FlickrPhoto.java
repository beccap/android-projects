package com.beccap.lol_loader.response;

/**
 * Response POJO for a single photo; for JSON-to-POJO parsing using gson.
 */
public class FlickrPhoto {
    // https://farm{farm}.staticflickr.com/{server}/{id}_{secret}_[mstzb].jpg
    private static final String SIZE = "z";

    private String farm;
    private String server;
    private String id;
    private String secret;
    private String title;

    /**
     * toUrl()
     *
     * Creates a URL to access the Flickr image, based on the photo data
     *
     * @return URL as String
     */
    public String toUrl() {
        // https://farm{farm}.staticflickr.com/{server}/{id}_{secret}_[mstzb].jpg
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_"
                + secret + "_" + SIZE + ".jpg";
    }

    /**
     * getStrippedTitle()
     *
     * Strips extra quotes out of title String; makes sure it is non-null.
     *
     * @return modified title
     */
    public String getStrippedTitle() {
        if (title != null) {
            // remove quotes
            return title.replace("\"","");
        }
        else {
            return "";
        }
    }
}
