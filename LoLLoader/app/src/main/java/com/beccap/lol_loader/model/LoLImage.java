package com.beccap.lol_loader.model;

import android.graphics.Bitmap;

/**
 * Created by beccap on 10/3/15.
 */
public class LoLImage {
    private Bitmap bitmap;
    private String title;

    public LoLImage(Bitmap bitmap, String title) {
        this.bitmap = bitmap;
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
