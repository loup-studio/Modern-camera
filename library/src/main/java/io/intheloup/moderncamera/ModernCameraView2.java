//package io.intheloup.moderncamera;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.net.Uri;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.ScaleGestureDetector;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//
//import com.commonsware.cwac.cam2.CameraView;
//
//import java.util.LinkedList;
//
//import io.intheloup.moderncamera.editor.EditorView;
//
///**
// * Created by Lukasz - lukasz.pili@gmail.com on 27/02/2017.
// */
//public class ModernCameraView2 extends FrameLayout {
//
//    private static final int PINCH_ZOOM_DELTA = 20;
//
//    final ViewGroup previewStack = new FrameLayout(getContext());
//    //        final CameraView cameraView = new CameraView(getContext());
//    final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
//    final CameraControlView controlView = new CameraControlView(getContext());
//    final EditorView editorView = new EditorView(getContext());
//
//    private OnTakePictureCallback onTakePictureCallback;
//    private final CameraPresenter presenter = new CameraPresenter(this);
//
//
//    public ModernCameraView2(Context context) {
//        super(context);
//        init(context);
//    }
//
//    public ModernCameraView2(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    public ModernCameraView2(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context);
//    }
//
//    private void init(Context context) {
//        setBackgroundColor(Color.BLACK);
//
//        addView(previewStack, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//        addView(controlView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        controlView.takePictureButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                presenter.didClickTakePicture();
//            }
//        });
//        controlView.switchCameraButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                presenter.didClickSwitchCamera();
//            }
//        });
//
//        addView(editorView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        editorView.setVisibility(GONE);
//        editorView.onSubmitPicture(new EditorView.OnSubmitPictureCallback() {
//            @Override
//            public void onSubmitPicture(Uri uri) {
//                onTakePictureCallback.onTakePicture(uri);
//            }
//        });
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        presenter.onReady();
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        presenter.onDrop();
//        super.onDetachedFromWindow();
//    }
//
//    LinkedList<CameraView> bindCameras(int count) {
//        previewStack.removeAllViews();
//
//        LinkedList<CameraView> cameraViews = new LinkedList<CameraView>();
//        CameraView cv;
//        for (int i = 0; i < count; ++i) {
//            cv = new CameraView(getContext());
//
//            if (i > 0) {
//                cv.setVisibility(View.INVISIBLE);
//            }
//            cv.setMirror(false);
//            previewStack.addView(cv, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            cameraViews.add(cv);
//        }
//
//        return cameraViews;
//    }
//
//    void bindZoom(boolean isEnabled) {
//        if (isEnabled) {
//            previewStack.setOnTouchListener(
//                    new OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            return (scaleDetector.onTouchEvent(event));
//                        }
//                    });
//        } else {
//            previewStack.setOnTouchListener(null);
//        }
//    }
//
//    void bindPicture(Uri uri) {
//        Log.d(getClass().getSimpleName(), "bindPicture: " + uri);
//        editorView.setPicture(uri);
//        editorView.setVisibility(VISIBLE);
//        controlView.setVisibility(GONE);
//    }
//
//    void bindCamera() {
//        Log.d(getClass().getSimpleName(), "bindCamera: ");
//        editorView.clear();
//        editorView.setVisibility(GONE);
//        controlView.setVisibility(VISIBLE);
//    }
//
//    public boolean onBackPressed() {
//        return presenter.onBackPressed();
//    }
//
//    public void start() {
//        presenter.start();
//    }
//
//    public void onTakePicture(OnTakePictureCallback onTakePictureCallback) {
//        this.onTakePictureCallback = onTakePictureCallback;
//    }
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public void onScaleEnd(ScaleGestureDetector detector) {
//            float scale = detector.getScaleFactor();
//            int delta;
//
//            if (scale > 1.0f) {
//                delta = PINCH_ZOOM_DELTA;
//            } else if (scale < 1.0f) {
//                delta = -1 * PINCH_ZOOM_DELTA;
//            } else {
//                return;
//            }
//
//            presenter.didChangeZoom(delta);
//        }
//    }
//
//    public interface OnTakePictureCallback {
//        void onTakePicture(Uri uri);
//    }
//}
