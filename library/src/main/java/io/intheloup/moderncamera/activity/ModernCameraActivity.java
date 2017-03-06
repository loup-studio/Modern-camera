package io.intheloup.moderncamera.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import io.intheloup.moderncamera.ModernCameraView;

public class ModernCameraActivity extends AppCompatActivity {

    ModernCameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraView = new ModernCameraView(this);
        setContentView(cameraView);

        cameraView.onTakePicture(new ModernCameraView.OnTakePictureCallback() {
            @Override
            public void onTakePicture(Uri uri) {
                Intent intent = new Intent();
                intent.putExtra("uri", uri);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        startCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 10) {
            return;
        }

        boolean isValid = true;
        for (int i = 0; i < grantResults.length; ++i) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isValid = false;
                break;
            }
        }

        if (isValid) {
            startCamera();
        }
    }

    @Override
    public void onBackPressed() {
        if (cameraView.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }


    private void startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 10);
            return;
        }

        cameraView.start();
    }
}
