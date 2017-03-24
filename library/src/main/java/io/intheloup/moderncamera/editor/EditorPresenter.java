package io.intheloup.moderncamera.editor;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

import io.intheloup.moderncamera.CameraFile;

/**
 * Created by Lukasz - lukasz.pili@gmail.com on 01/03/2017.
 */

public class EditorPresenter {

    private final EditorView view;

    public EditorPresenter(EditorView view) {
        this.view = view;
    }

    void didSubmitPicture() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            view.bindBusy(false);
            return;
        }

        view.bindBusy(true);

        view.contentView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.contentView.getDrawingCache());
        view.contentView.setDrawingCacheEnabled(false);

        CameraFile file = new CameraFile(view.getContext(), "picture_final");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file.getFile());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), "didSubmitPicture: " + e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // silent
                }
            }
        }

        view.bindFinalPicture(file.getUri());
    }
}
