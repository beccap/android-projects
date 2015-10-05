package com.beccap.lol_loader;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beccap.lol_loader.async.LoadImageAsyncTask;
import com.beccap.lol_loader.model.LoLImage;

/**
 * MainFragment handles most of the UI for displaying images and responding to user clicks.
 *
 * Is a listener for LoadImageAsyncTask, and will be notified whenever a new image is ready to
 * be displayed.
 */
public class MainFragment extends Fragment implements LoadImageAsyncTask.ImageLoadedListener {

    private static final String IMAGE_KEY = "image_key";
    private static final String TITLE_KEY = "title_key";

    private Bitmap imageBitmap = null;
    private String imageTitle;

    private ImageView rectImageView;
    private TextView  titleTextView;
    private TextView  loadStatusTextView;

    private String promptText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // retrieve prompt text
        promptText = getString(R.string.prompt_text);

        // retrieve view data from savedInstanceState (rotation, etc)
        if (savedInstanceState != null) {
            imageBitmap = savedInstanceState.getParcelable(IMAGE_KEY);
            imageTitle  = savedInstanceState.getString(TITLE_KEY);
        }
        else {
            imageTitle = promptText;
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

    // save data here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save view data for quick reload on rotation
        if (imageBitmap != null) {
            outState.putParcelable(IMAGE_KEY, imageBitmap);
        }
        outState.putString(TITLE_KEY, imageTitle);
    }

    /**
     * onImageLoaded()
     *
     * Callback method for LoadImageAsyncTask.ImageLoadedListener
     * Updates views and data based on results of image loader.
     *
     * @param lolImage - object containing image and title
     */
    @Override
    public void onImageLoaded(LoLImage lolImage) {
        loadStatusTextView.setVisibility(View.GONE);

        // update image and title instance variables
        if (lolImage == null) {
            imageBitmap = null;
            imageTitle  = promptText;
        }
        else {
            imageBitmap = lolImage.getBitmap();
            imageTitle  = lolImage.getTitle();
        }

        // update views based on new data
        updateSubviews();
    }

    /**
     * updateSubviews()
     *
     * Make sure what is contained in subviews accurately reflects current data.
     */
    private void updateSubviews() {
        int backgroundResId;

        // make sure subview content is set to reflect the current data
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
