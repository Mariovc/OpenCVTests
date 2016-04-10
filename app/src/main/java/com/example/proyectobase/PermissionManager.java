package com.example.proyectobase;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Author: Mario Velasco Casquero
 * Date: 11/04/2016
 * Email: m3ario@gmail.com
 */
public class PermissionManager {

    private static final int SOLICITUD_PERMISO_CAMERA = 0;

    public static void checkCameraPermission(final Activity activity) {
        View vista = activity.findViewById(android.R.id.content);
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {
                Snackbar.make(vista, "Se necesita permiso para usar la cámara.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{ Manifest.permission.CAMERA },
                                SOLICITUD_PERMISO_CAMERA);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA},
                        SOLICITUD_PERMISO_CAMERA);
            }
        }
    }
}
