package io.intheloup.moderncamera.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import io.intheloup.moderncamera.activity.ModernCameraActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.take_pic);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, ModernCameraActivity.class), 1);
            }
        });

        imageView = (ImageView) findViewById(R.id.picture);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            imageView.setImageURI(null);
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(data.getExtras().<Uri>getParcelable("uri"));
            }
        }
    }
}
