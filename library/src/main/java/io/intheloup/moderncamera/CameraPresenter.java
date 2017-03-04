package io.intheloup.moderncamera;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.cwac.cam2.AbstractCameraActivity;
import com.commonsware.cwac.cam2.CameraController;
import com.commonsware.cwac.cam2.CameraEngine;
import com.commonsware.cwac.cam2.CameraSelectionCriteria;
import com.commonsware.cwac.cam2.CameraView;
import com.commonsware.cwac.cam2.ErrorConstants;
import com.commonsware.cwac.cam2.Facing;
import com.commonsware.cwac.cam2.FlashMode;
import com.commonsware.cwac.cam2.FocusMode;
import com.commonsware.cwac.cam2.ImageContext;
import com.commonsware.cwac.cam2.PictureTransaction;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Lukasz - lukasz.pili@gmail.com on 27/02/2017.
 */

class CameraPresenter {

    private static final int ERROR_TAKE_PICTURE = ErrorConstants.ERROR_LIST_CAMERAS * 2;

    private final ModernCameraView view;
    private CameraController controller;
    private CameraFile file;

    private boolean isViewReady = false;
    private boolean isStartRequested = false;
    private boolean isStarted = false;
    private boolean isCameraBusy = false;

    private String TAG = getClass().getSimpleName();

    CameraPresenter(ModernCameraView view) {
        this.view = view;
    }

    void onReady() {
        Log.d(TAG, "onReady: ");
        isViewReady = true;

        initController();
        if (isStartRequested) {
            start();
        }
    }

    void onDrop() {
        Log.d(TAG, "onDrop: ");
        isViewReady = false;

        stop();
        clearFile();
        destroyController();
    }

    void start() {
        if (isStarted) return;

        if (!isViewReady) {
            Log.d(TAG, "start: not ready");
            isStartRequested = true;
            return;
        }

        Log.d(TAG, "start: ");
        isStarted = true;

        startController();
        if (controller.getNumberOfCameras() > 0) {
            prepController();
        }

        AbstractCameraActivity.BUS.register(this);
        controller.start();
    }

    void stop() {
        if (!isStarted) return;
        isStarted = false;
        isCameraBusy = false;
        Log.d(TAG, "stop: ");

        AbstractCameraActivity.BUS.unregister(this);

        try {
            controller.stop();
        } catch (Exception e) {
            controller.postError(ErrorConstants.ERROR_STOPPING, e);
            Log.e(getClass().getSimpleName(), "Exception stopping controller", e);
        }
    }

    private void initController() {
        Log.d(TAG, "initController: ");
        if (controller != null) {
            throw new IllegalStateException("check failed: controller must be null");
        }

        controller = new CameraController(FocusMode.CONTINUOUS, new ErrorResultReceiver(), true, false);
    }

    private void destroyController() {
        controller.destroy();
        controller = null;
    }

    boolean onBackPressed() {
        if (file == null) {
            return false;
        }

        clearFile();
        view.bindCamera();
        start();
        return true;
    }

    void didClickSwitchCamera() {
        if (isCameraBusy) return;
        isCameraBusy = true;

        try {
            controller.switchCamera();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Exception switching camera", e);
            controller.postError(ErrorConstants.ERROR_SWITCHING_CAMERAS, e);
            stop();
        }
    }

    void didClickTakePicture() {
        if (isCameraBusy) return;

        file = new CameraFile(view.getContext(), "picture");
        if (!file.isValid()) {
            file = null;
            return;
        }

        isCameraBusy = true;
        PictureTransaction.Builder b = new PictureTransaction.Builder();
        b.toUri(view.getContext(), file.getUri(), false, false);
        controller.takePicture(b.build());
    }

    void didChangeZoom(int delta) {
        controller.changeZoom(delta);
    }

    private void startController() {
        if (controller.getEngine() != null) {
            return;
        }

        CameraEngine engine = CameraEngine.buildInstance(view.getContext(), null);
        engine.setPreferredFlashModes(new ArrayList<FlashMode>());
        controller.setEngine(engine, new CameraSelectionCriteria.Builder()
                .facing(Facing.BACK)
                .build());
        controller.setQuality(1);
    }

    private void prepController() {
        Log.d(getClass().getSimpleName(), "prepController: ");

        LinkedList<CameraView> cameraViews = new LinkedList<CameraView>();
        CameraView cv = view.cameraView;
        cv.setMirror(false);
        cameraViews.add(cv);
        cameraViews.addAll(view.bindCameras(controller.getNumberOfCameras()));
        controller.setCameraViews(cameraViews);
    }

    private void clearFile() {
        if (file == null) {
            return;
        }

        file.delete();
        file = null;
    }

    private void didTakePicture(ImageContext imageContext) {
        if (file == null) {
            return;
        }

        view.bindPicture(file.getUri());
        stop();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CameraEngine.PictureTakenEvent event) {
        isCameraBusy = false;
        if (!isViewReady) return;

        if (event.exception == null) {
            didTakePicture(event.getImageContext());
        } else {
            controller.postError(ERROR_TAKE_PICTURE, event.exception);
            stop();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CameraController.ControllerReadyEvent event) {
        if (!isViewReady) return;

        if (event.isEventForController(controller)) {
            prepController();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CameraEngine.OpenedEvent event) {
        if (!isViewReady) return;

        if (event.exception == null) {
            isCameraBusy = false;
            view.bindZoom(controller.supportsZoom());
        } else {
            controller.postError(ErrorConstants.ERROR_OPEN_CAMERA, event.exception);
            stop();
        }
    }

    @SuppressLint("ParcelCreator")
    private class ErrorResultReceiver extends ResultReceiver {
        public ErrorResultReceiver() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        protected void onReceiveResult(int resultCode,
                                       Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (isViewReady) {
                Toast.makeText(view.getContext(), "We had an error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
