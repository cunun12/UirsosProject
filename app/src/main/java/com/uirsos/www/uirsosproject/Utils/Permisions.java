package com.uirsos.www.uirsosproject.Utils;

import android.Manifest;

/**
 * Created by cunun12
 */

public class Permisions {

    public static final String[] PERMISIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static final String[] CAMERA_PERMISION = {
            Manifest.permission.CAMERA
    };

    public static final String[] READ_STORAGE_PERMISIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static final String[] WRITE_STORAGE_PERMISION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
}
