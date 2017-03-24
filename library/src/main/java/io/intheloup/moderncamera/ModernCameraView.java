package io.intheloup.moderncamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import io.intheloup.moderncamera.editor.EditorView;

/**
 * Created by Lukasz - lukasz.pili@gmail.com on 27/02/2017.
 */
public class ModernCameraView extends FrameLayout {

    private CameraView cameraView;
    private CameraControlView controlView;
    public EditorView editorView;

    private boolean isCameraReady = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    public ModernCameraView(Context context) {
        super(context);
        init(context);
    }

    public ModernCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ModernCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        setBackgroundColor(Color.BLACK);
        View view = LayoutInflater.from(context).inflate(R.layout.moderncamera_view, this, true);
        cameraView = (CameraView) view.findViewById(R.id.camera);
        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onCameraOpened() {
                isCameraReady = true;
            }

            @Override
            public void onCameraClosed() {
                isCameraReady = false;
            }

            @Override
            public void onPictureTaken(byte[] jpeg) {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bindPicture(bitmap);
                    }
                });
            }
        });

        controlView = (CameraControlView) view.findViewById(R.id.camera_control);
        controlView.takePictureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCameraReady) return;
                cameraView.captureImage();
            }
        });

        controlView.switchCameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCameraReady) return;
                cameraView.toggleFacing();
                cameraView.setFocus(CameraKit.Constants.FOCUS_CONTINUOUS);
            }
        });

        editorView = (EditorView) view.findViewById(R.id.editor);
    }

    void bindPicture(Bitmap bitmap) {
        Log.d(getClass().getSimpleName(), "bindPicture: ");
        editorView.setPicture(bitmap);
        editorView.setVisibility(VISIBLE);

        cameraView.setVisibility(GONE);
        controlView.setVisibility(GONE);
    }

    void bindCamera() {
        Log.d(getClass().getSimpleName(), "bindCamera: ");
        editorView.clear();
        editorView.setVisibility(GONE);

        cameraView.setVisibility(VISIBLE);
        controlView.setVisibility(VISIBLE);
    }

    public boolean onBackPressed() {
        if (editorView.getVisibility() == VISIBLE) {
            bindCamera();
            return true;
        }

        return false;
    }

    public void start() {
        cameraView.start();
    }

    public void stop() {
        cameraView.stop();
    }
}
