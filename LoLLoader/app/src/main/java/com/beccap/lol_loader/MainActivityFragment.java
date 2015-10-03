package com.beccap.lol_loader;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String IMAGE_KEY = "image_key";
    private static final String TITLE_KEY = "title_key";

    private Bitmap imageBitmap = null;
    private String imageTitle;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // retrieve view data from savedInstanceState (rotation, etc)
        if (savedInstanceState != null) {
            imageBitmap = (Bitmap)savedInstanceState.getParcelable(IMAGE_KEY);
            imageTitle  = savedInstanceState.getString(TITLE_KEY);
        }

        // inflate layout
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // get handles to subviews
        ImageView rectImageView = (ImageView)rootView.findViewById(R.id.imageRect);
        TextView titleTextView  = (TextView)rootView.findViewById(R.id.textTitle);

        // initialize subview content
        if (imageBitmap != null) {
            rectImageView.setImageBitmap(imageBitmap);
            titleTextView.setText(imageTitle);
            titleTextView.setVisibility(View.VISIBLE);
        }
        else {
            titleTextView.setVisibility(View.INVISIBLE);
        }

        // set onClickListener
        rectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
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
            outState.putString(TITLE_KEY, imageTitle);
        }
    }
}
