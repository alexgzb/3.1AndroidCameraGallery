package com.gezelbom;

import java.io.File;
import java.util.Arrays;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Fragment to display a gallery of the images in the path
 *
 * @author Alex
 */
public class FragmentGallery extends Fragment {

    private static String TAG = "GalleryFragment";
    private Cursor cursor;
    String[] arrPath;
    private int dataColumnIndex;
    private int thumbColumnIndex;
    private int idColumnIndex;
    private int thumb[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "OnCreateView Launched");
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        loadGallery();
        GridView grid = (GridView) v.findViewById(R.id.gridView1);
        grid.setAdapter(new ImageAdapter(getActivity()));

        // Set up a anonymous inner- click listener
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                // Get the data location of the image
                cursor.moveToPosition(position);
                // Get image filepath from array loaded in loadGallery()
                String imagePath = cursor.getString(dataColumnIndex);
                // Use this path to do further processing, i.e. full screen
                // Sending image id to FullScreenActivity
                Intent i = new Intent(getActivity().getApplicationContext(),
                        FullImageActivity.class);
                // passing array index
                i.putExtra("path", imagePath);
                startActivity(i);

            }
        });

        return v;

    }

    /**
     * Loads the gallery into the cursor with columns for Image_data, Image_id and thumb_id
     */
    public void loadGallery() {

        final String[] columns;
        columns = new String[]{MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID, MediaStore.Images.Thumbnails._ID};

        final String uri = MediaStore.Images.Media.DATA;
        File path = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");

        //Make sure media files are loaded
        scanForFiles(path);


        //final String condition = uri + " like  '%" + path.toString() + "%'";
        final String condition = uri + " like ?";
        final String orderBy = MediaStore.Images.Media._ID;
        Log.d(TAG, "Columns " + Arrays.toString(columns));
        Log.d(TAG, "Uri " + uri);
        Log.d(TAG, "Path " + path);
        Log.d(TAG, "Condition " + condition);
        Log.d(TAG, "OrderBy " + orderBy);

        // Stores all the images from the gallery in Cursor
        cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                condition, new String[]{"%" + path.toString() + "%"}, orderBy);

        //Get the columnIndexes of thumbs and images
        dataColumnIndex = cursor
                .getColumnIndex(MediaStore.Images.Media.DATA);
        thumbColumnIndex = cursor
                .getColumnIndex(MediaStore.Images.Thumbnails._ID);
        idColumnIndex = cursor
                .getColumnIndex(MediaStore.Images.Media._ID);

        // Total number of images
        int count = cursor.getCount();
//        System.out.println("Count in cursor " + count);
//        System.out.println("Count in path " + path.listFiles().length);

        //The following is used for debugging purposes, currently not needed

        // Create an array to store path to all the images
        arrPath = new String[count];
        thumb = new int[count];
        int id[] = new int[count];


        //Loop through all images available in the cursor and store path, thumb, id in separate
        //arrays.
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            // Store the path of the image
            arrPath[i] = cursor.getString(dataColumnIndex);
            thumb[i] = cursor.getInt(thumbColumnIndex);
            id[i] = cursor.getInt(idColumnIndex);
            //Log.d(TAG, "Thumb, ID: " + thumb[i] );
            //Log.d(TAG, "ID: " + id[i] );
            //Log.d(TAG, "PATH" + arrPath[i]);
        }

    }

    /**
     * * Scan through the specific folder for all files. And the update the MediaStore for each file
     *
     * @param path The path to scan
     */
    private void scanForFiles(File path) {

        // Only if the folder is not empty
        if (path.listFiles() != null) {
            // List all files
            File[] files = path.listFiles();
            // Convert to StringArray since MediaScanner needs it that way
            String[] filesString = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                filesString[i] = files[i].toString();
            }

            // MediaScanner.scanFile to force the MediaStore to update a specific file to the db
            MediaScannerConnection.scanFile(getActivity(), filesString, null, new MediaScannerConnection.OnScanCompletedListener() {

                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                }
            });

        }


    }

    /**
     * Adapter for our image files.
     */

    private class ImageAdapter extends BaseAdapter {

        // Default unmodified methods of BaseAdapter
        private Context context;

        public ImageAdapter(Context localContext) {
            context = localContext;
        }

        public int getCount() {
            System.out.println("GetCount called with count " + thumb.length);
            return thumb.length;
        }

        public Object getItem(int position)  {
            System.out.println("GetItem called with position " + position);
            return thumb[position];
            //return position;
        }

        public long getItemId(int position) {
            return position;
        }

        // Custom getView Method
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);

                System.out.println("Current position = " + position);
                System.out.println("Current convertView = " + convertView);
                // Move cursor to current position
                //cursor.moveToPosition(position);
                // Get the current value for the requested column
                //int imageID = cursor.getInt(thumbColumnIndex);
                int imageID = thumb[position];
                // Set the content of the image based on the provided URI
                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                        getActivity().getContentResolver(), imageID,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null);

                picturesView.setImageBitmap(bitmap);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                picturesView = (ImageView) convertView;
            }
            return picturesView;
        }
    }
}
