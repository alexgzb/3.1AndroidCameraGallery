package com.gezelbom;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraLiveView extends SurfaceView implements Callback {

    private static final String TAG = "CameraLiveView";
    private SurfaceHolder holder;
    private Camera camera;
    private Context context;

    public CameraLiveView(Context context, Camera camera) {
        super(context);
        this.context = context;
        this.camera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the
        // preview.

        try {
            camera.setPreviewDisplay(holder);
            // camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, "Unexpected exception: " + e.getMessage());
        }

    }

    public void startPreview() {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        Camera.Parameters parameters = camera.getParameters();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int rotation = wm.getDefaultDisplay().getRotation();

		/*
		 * //Get sizes List<Camera.Size> prevSizes =
		 * parameters.getSupportedPreviewSizes(); List<Camera.Size> picSizes =
		 * parameters.getSupportedPictureSizes();
		 * 
		 * Log.d(TAG, "Preview sizes"); for (Camera.Size s : prevSizes) {
		 * Log.d(TAG, "Width " + s.width + " Height " + s.height ); } Log.d(TAG,
		 * "Picture sizes"); for (Camera.Size s : picSizes) { Log.d(TAG,
		 * "Width " + s.width + " Height " + s.height ); }
		 */

        switch (rotation) {
            case Surface.ROTATION_0:
                rotation = 90;
                break;
            case Surface.ROTATION_90:
                rotation = 0;
                break;
            case Surface.ROTATION_180:
                rotation = 0;
                break;
            case Surface.ROTATION_270:
                rotation = 180;
                break;
        }

        camera.setDisplayOrientation(rotation);

        parameters.setPreviewSize(640, 480);
        // parameters.setPictureSize(352, 288);
        camera.setParameters(parameters);

        // start preview with new settings
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
