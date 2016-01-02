package com.gezelbom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * Fragment to display the camera in the scrollable tabs activity
 *
 * @author Alex
 *
 */
public class FragmentCamera extends Fragment {

    private static final String TAG = "MainActivity";
    private Camera camera;
    private View v;

    public FragmentCamera() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_camera, container, false);

        startCamera();

        // dispatchTakePictureIntent();

        // Add a listener to the Capture button
        ImageButton captureButton = (ImageButton) v
                .findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an image from the camera
                camera.takePicture(null, null, mPicture);
            }
        });

        return v;

    }

    private void startCamera() {
        if (checkCameraHardware(getActivity())) {
            Log.d(TAG, "Camera available");
            camera = getCameraInstance();

        }
        CameraLiveView liveView = new CameraLiveView(getActivity(), camera);
        FrameLayout frame = (FrameLayout) v.findViewById(R.id.frameLayout);
        frame.addView(liveView);
    }


    /**
     * Returns a Filename for storing image, takes date and similar variables in effect
     *
     * @return The file to use when storing the image
     */
    private static File getOutputMediaFile() {

        // First check that the External storage is mounted and read/Write acces
        // is available
        Log.d(TAG,
                "External storage state: "
                        + Environment.getExternalStorageState());
        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
                .getExternalStorageState())) {
            Log.d(TAG, "Inside the if statement");
            File picturesDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "MyCameraApp");

            Log.d(TAG, "Picture dir: " + picturesDir);

            // Create the storage directory if it does not exist
            if (!picturesDir.exists()) {
                Log.d(TAG, "Picture dir does not exist trying to create it");
                if (!picturesDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd-HH:mm:ss",
                    Locale.getDefault()).format(new Date());
            File mediaFile;
            mediaFile = new File(picturesDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

            Log.d(TAG, "Filename: " + mediaFile);
            return mediaFile;

        }
        return null;

    }

    /**
     * Check if this device has a camera
     *
     * @param context
     * @return
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Get an instance of the camera
     *
     * @return
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onDestroy() {
        camera.release();
        super.onDestroy();
    }

    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d(TAG,
                        "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            Intent i = new Intent(getActivity().getApplicationContext(),
                    FullImageActivity.class);
            // passing array index

            i.putExtra("path", pictureFile.toString());
            startActivity(i);
            // liveView.startPreview();
        }

    };

	/*
	 * private void dispatchTakePictureIntent() { Intent takePictureIntent = new
	 * Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	 *
	 * if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	 * startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	 *
	 *
	 * } }
	 *
	 *
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { if (requestCode == REQUEST_IMAGE_CAPTURE &&
	 * resultCode == RESULT_OK) { Bundle extras = data.getExtras(); Bitmap
	 * imageBitmap = (Bitmap) extras.get("data"); ImageView mImageView =
	 * (ImageView) findViewById(R.id.imageView1);
	 * mImageView.setImageBitmap(imageBitmap); } }
	 */

}
