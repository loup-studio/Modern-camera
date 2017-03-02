package io.intheloup.moderncamera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * Created by Lukasz - lukasz.pili@gmail.com on 01/03/2017.
 */

class CameraControlView extends FrameLayout {

    ImageButton takePictureButton;
    ImageButton switchCameraButton;

    public CameraControlView(Context context) {
        super(context);
        init(context);
    }

    public CameraControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.moderncamera_control_view, this);
        takePictureButton = (ImageButton) view.findViewById(R.id.moderncamera_control_take_picture_button);
        switchCameraButton = (ImageButton) view.findViewById(R.id.moderncamera_control_switch_camera_button);
    }
}
