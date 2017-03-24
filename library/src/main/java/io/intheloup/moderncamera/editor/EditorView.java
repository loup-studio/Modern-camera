package io.intheloup.moderncamera.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import io.intheloup.moderncamera.R;
import team.uptech.motionviews.widget.RichEditText;

/**
 * Created by Lukasz - lukasz.pili@gmail.com on 01/03/2017.
 */

public class EditorView extends FrameLayout {

    private static final int MODE_NOTHING = 0;
    private static final int MODE_TEXT_SIMPLE = 1;
    private static final int MODE_TEXT_RICH = 2;

    View contentView;
    ImageView pictureImageView;
    ViewGroup panelView;
    Button submitButton;
    ImageButton textButton;
    EditText editText;
    TextView simpleTextView;
    VerticalSlideColorPicker colorPicker;
    RichEditText richEditText;

    private int mode = MODE_NOTHING;
    private boolean isBusy = false;

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
        contentView = view.findViewById(R.id.moderncamera_editor_content_view);
        pictureImageView = (ImageView) view.findViewById(R.id.moderncamera_editor_picture_imageview);

        panelView = (ViewGroup) view.findViewById(R.id.moderncamera_editor_panel_view);
        colorPicker = (VerticalSlideColorPicker) view.findViewById(R.id.moderncamera_editor_colorpicker);
        submitButton = (Button) view.findViewById(R.id.moderncamera_editor_submit_button);
        textButton = (ImageButton) view.findViewById(R.id.moderncamera_editor_text_button);

        editText = (EditText) view.findViewById(R.id.moderncamera_editor_edittext);
        simpleTextView = (TextView) view.findViewById(R.id.moderncamera_editor_simple_textview);

        simpleTextView.setOnTouchListener(new OnTouchListener() {

            private long lastDown = 0;
            private boolean hasMoved = false;
            float dY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastDown = System.currentTimeMillis();
                        dY = view.getY() - event.getRawY();
                        return false;

                    case MotionEvent.ACTION_MOVE:
                        if (System.currentTimeMillis() - lastDown < 200) {
                            return false;
                        }

                        hasMoved = true;
                        view.animate()
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();

                        return true;

                    case MotionEvent.ACTION_UP:
                        if (hasMoved) {
                            hasMoved = false;
                            return true;
                        }
                        break;

                }

                return false;
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                commitEdit();
            }
        });

        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBusy) return;

                commitEdit();
                presenter.didSubmitPicture();
            }
        });

        textButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBusy) return;

                switch (mode) {
                    case MODE_NOTHING:
                        mode = MODE_TEXT_SIMPLE;
                        break;

                    case MODE_TEXT_SIMPLE:
                        mode = editText.getText().length() > 0 ? MODE_TEXT_RICH : MODE_NOTHING;
                        break;

                    case MODE_TEXT_RICH:
                        mode = MODE_NOTHING;
                        break;
                }

                refreshTextMode();
            }
        });

        colorPicker.setOnColorChangeListener(new VerticalSlideColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChange(int selectedColor) {
                if (isBusy) return;
                editText.setTextColor(selectedColor);

                if (mode == MODE_TEXT_SIMPLE) {
                    simpleTextView.setTextColor(selectedColor);
                } else if (mode == MODE_TEXT_RICH) {
                    richEditText.updateTextColor(selectedColor);
                }
            }
        });

        simpleTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleTextView.setVisibility(GONE);
                editText.setVisibility(VISIBLE);
                showKeyboard(editText);
            }
        });


        richEditText = (RichEditText) findViewById(R.id.moderncamera_editor_motion_view);
        richEditText.onSelectText(new RichEditText.OnSelectTextCallback() {
            @Override
            public void onSelectText() {
                richEditText.setVisibility(INVISIBLE);
                editText.setVisibility(VISIBLE);
                showKeyboard(editText);
            }
        });
    }

    void bindBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    void bindFinalPicture(Uri uri) {
        onSubmitPictureCallback.onSubmitPicture(uri);
    }

    public void setPicture(Bitmap bitmap) {
        pictureImageView.setImageBitmap(bitmap);
    }

    public void clear() {
        pictureImageView.setImageBitmap(null);

        editText.setText("");
        editText.setTextColor(Color.WHITE);
        editText.setVisibility(GONE);
        simpleTextView.setText("");
        simpleTextView.setTextColor(Color.WHITE);
        simpleTextView.setVisibility(GONE);
        colorPicker.setVisibility(INVISIBLE);
    }

    private void commitEdit() {
        if (isBusy || mode == MODE_NOTHING) return;

        hideKeyboard(editText);

        if (editText.getText().length() > 0) {
            editText.setVisibility(GONE);

            if (mode == MODE_TEXT_SIMPLE) {
                simpleTextView.setText(editText.getText());
                simpleTextView.setVisibility(VISIBLE);
            } else {
                richEditText.updateText(editText.getText().toString());
                richEditText.setVisibility(VISIBLE);
            }
        } else {
            mode = MODE_NOTHING;
            refreshTextMode();
        }
    }

    private void refreshEditText() {
        switch (mode) {
            case MODE_TEXT_SIMPLE:
                editText.setBackgroundColor(0xCC000000);
                editText.setTextSize(25);
                editText.setTypeface(Typeface.DEFAULT);
                break;

            case MODE_TEXT_RICH:
                editText.setBackgroundColor(0x00000000);
                editText.setTextSize(70);
                editText.setTypeface(Typeface.DEFAULT_BOLD);
                break;
        }
    }

    private void refreshTextMode() {
        refreshEditText();

        switch (mode) {
            case MODE_NOTHING:
                simpleTextView.setVisibility(GONE);
                editText.setVisibility(GONE);
                richEditText.setVisibility(INVISIBLE);
                colorPicker.setVisibility(INVISIBLE);
                hideKeyboard(editText);
                break;

            case MODE_TEXT_SIMPLE:
                if (editText.getText().length() > 0) {
                    simpleTextView.setText(editText.getText());
                    simpleTextView.setTextColor(colorPicker.getSelectedColor());
                    simpleTextView.setVisibility(VISIBLE);
                    editText.setVisibility(GONE);
                } else {
                    simpleTextView.setVisibility(GONE);
                    editText.setVisibility(VISIBLE);
                    showKeyboard(editText);
                }

                richEditText.setVisibility(INVISIBLE);
                colorPicker.setVisibility(VISIBLE);
                break;

            case MODE_TEXT_RICH:
                if (editText.getText().length() > 0) {
                    richEditText.updateText(editText.getText().toString());
                    richEditText.updateTextColor(colorPicker.getSelectedColor());
                    richEditText.setVisibility(VISIBLE);
                    editText.setVisibility(GONE);
                    hideKeyboard(editText);
                } else {
                    richEditText.setVisibility(INVISIBLE);
                    editText.setVisibility(VISIBLE);
                    showKeyboard(editText);
                }

                simpleTextView.setVisibility(GONE);
                colorPicker.setVisibility(VISIBLE);
                break;
        }
    }

    private void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeyboard(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public void onSubmitPicture(OnSubmitPictureCallback onSubmitPictureCallback) {
        this.onSubmitPictureCallback = onSubmitPictureCallback;
    }

    public interface OnSubmitPictureCallback {
        void onSubmitPicture(Uri uri);
    }
}
