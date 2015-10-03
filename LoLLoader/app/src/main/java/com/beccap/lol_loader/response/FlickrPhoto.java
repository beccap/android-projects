package com.beccap.lol_loader.response;

/**
 * Created by beccap on 10/3/15.
 */
public class FlickrPhoto {
    // https://farm{farm}.staticflickr.com/{server}/{id}_{secret}_[mstzb].jpg
    private static final String SIZE = "z";

    private String farm;
    private String server;
    private String id;
    private String secret;
    private String title;

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toURL() {
        // https://farm{farm}.staticflickr.com/{server}/{id}_{secret}_[mstzb].jpg
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_"
                + secret + "_" + SIZE + ".jpg";
    }
}
