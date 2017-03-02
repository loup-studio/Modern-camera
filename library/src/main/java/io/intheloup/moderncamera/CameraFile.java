package io.intheloup.moderncamera;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by Lukasz - lukasz.pili@gmail.com on 01/03/2017.
 */

public class CameraFile {
    private File root;
    private File file;
    private Uri uri;

    public CameraFile(Context context, String filename) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return;
        }

        root = new File(context.getExternalFilesDir(null), "modern-camera");
        if (!root.exists()) {
            root.mkdir();
        }

        file = new File(root, String.format("%s.jpg", filename));
        if (file.exists()) {
            file.delete();
        }

        uri = Uri.fromFile(file);
    }

    public void delete() {
        root.delete();
        uri = null;
    }

    public boolean isValid() {
        return uri != null;
    }

    public Uri getUri() {
        return uri;
    }

    public File getFile() {
        return file;
    }
}
