package io.intheloup.moderncamera.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.intheloup.moderncamera.ModernCameraView;
import io.intheloup.moderncamera.editor.EditorView;

public class ModernCameraActivity extends AppCompatActivity {

    ModernCameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraView = new ModernCameraView(this);
        setContentView(cameraView);

        cameraView.editorView.onSubmitPicture(new EditorView.OnSubmitPictureCallback() {
            @Override
            public void onSubmitPicture(Uri uri) {
                Intent intent = new Intent();
                intent.putExtra("uri", uri);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode != 10) {
//            return;
//        }
//
//        boolean isValid = true;
//        for (int i = 0; i < grantResults.length; ++i) {
//            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                isValid = false;
//                break;
//            }
//        }
//
//        if (isValid) {
//            cameraView.start();
//        }
//    }

    @Override
    public void onBackPressed() {
        if (cameraView.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }


    private void startCamera() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//            }, 10);
//            return;
//        }

        cameraView.start();
    }
}
