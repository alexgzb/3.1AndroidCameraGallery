package com.gezelbom;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Class to load and display the image "fullscreen"
 */
public class FullImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        // get intent data
        Intent i = getIntent();

        // Selected image id
        String path = (String)i.getExtras().get("path");

        File file = new File(path);

        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);

        imageView.setImageURI(Uri.fromFile(file));
    }

}