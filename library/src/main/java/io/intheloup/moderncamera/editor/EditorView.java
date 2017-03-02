package io.intheloup.moderncamera.editor;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import io.intheloup.moderncamera.R;

/**
 * Created by Lukasz - lukasz.pili@gmail.com on 01/03/2017.
 */

public class EditorView extends FrameLayout {

    private ImageView pictureImageView;
    private ViewGroup panelView;
    private Button submitButton;
    private Button textButton;

    private OnSubmitPictureCallback onSubmitPictureCallback;

    private final EditorPresenter presenter = new EditorPresenter(this);

    public EditorView(Context context) {
        super(context);
        init(context);
    }

    public EditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.moderncamera_editor_view, this);
        pictureImageView = (ImageView) view.findViewById(R.id.moderncamera_editor_picture_imageview);
        panelView = (ViewGroup) view.findViewById(R.id.moderncamera_editor_panel_view);
        submitButton = (Button) view.findViewById(R.id.moderncamera_editor_submit_button);
        textButton = (Button) view.findViewById(R.id.moderncamera_editor_text_button);

        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                panelView.setVisibility(GONE);
                presenter.didSubmitPicture();
            }
        });
    }

    void bindFinalPicture(Uri uri) {
        onSubmitPictureCallback.onSubmitPicture(uri);
    }

    public void setPicture(Uri uri) {
        pictureImageView.setImageURI(uri);
    }

    public void clearPicture() {
        pictureImageView.setImageURI(null);
    }

    public void onSubmitPicture(OnSubmitPictureCallback onSubmitPictureCallback) {
        this.onSubmitPictureCallback = onSubmitPictureCallback;
    }

    public interface OnSubmitPictureCallback {
        void onSubmitPicture(Uri uri);
    }
}
