package com.beccap.lol_loader;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beccap.lol_loader.async.LoadImageAsyncTask;
import com.beccap.lol_loader.model.LoLImage;
import com.beccap.lol_loader.util.FlickrUrlBuilder;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoadImageAsyncTask.ImageLoadedListener {

    private static final String IMAGE_KEY = "image_key";
    private static final String TITLE_KEY = "title_key";

    private Bitmap imageBitmap = null;
    private String imageTitle = "";

    private ImageView rectImageView;
    private TextView  titleTextView;
    private TextView  loadStatusTextView;

    private String defaultTitle;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        defaultTitle = getString(R.string.default_title_text);

        // retrieve view data from savedInstanceState (rotation, etc)
        if (savedInstanceState != null) {
            imageBitmap = (Bitmap)savedInstanceState.getParcelable(IMAGE_KEY);
            imageTitle  = savedInstanceState.getString(TITLE_KEY);
        }
        else {
            imageTitle = defaultTitle;
        }

        // inflate layout
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rootView.setBackgroundColor(getResources().getColor(R.color.background_color));

        // get handles to subviews
        rectImageView = (ImageView)rootView.findViewById(R.id.imageRect);
        titleTextView = (TextView)rootView.findViewById(R.id.textTitle);
        loadStatusTextView = (TextView)rootView.findViewById(R.id.textLoadStatus);
        loadStatusTextView.setVisibility(View.GONE);

        // initialize subview content; title only visible if there is an image
        updateSubviews();

        // set onClickListener
        rectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadStatusTextView.setVisibility(View.VISIBLE);
                new LoadImageAsyncTask(MainFragment.this).execute();
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save view data for quick reload on rotation
        if (imageBitmap != null) {
            outState.putParcelable(IMAGE_KEY, imageBitmap);
        }
        outState.putString(TITLE_KEY, imageTitle);
    }

    @Override
    public void onImageLoaded(LoLImage lolImage) {
        loadStatusTextView.setVisibility(View.GONE);

        if (lolImage == null) {
            imageBitmap = null;
            imageTitle  = defaultTitle;
        }
        else {
            imageBitmap = lolImage.getBitmap();
            imageTitle  = lolImage.getTitle();
        }
        updateSubviews();
    }

    private void updateSubviews() {
        int backgroundResId;

        // make sure content is set to reflect the current data
        rectImageView.setImageBitmap(imageBitmap);
        titleTextView.setText(imageTitle);

        // no image means show rectangle; otherwise make background match rootview background
        if (imageBitmap == null) {
            backgroundResId = R.color.rect_color;
        }
        else {
            backgroundResId = R.color.background_color;
        }
        rectImageView.setBackgroundColor(getResources().getColor(backgroundResId));
    }
}
