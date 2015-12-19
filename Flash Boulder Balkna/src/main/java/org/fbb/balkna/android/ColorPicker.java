package org.fbb.balkna.android;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ColorPicker extends AppCompatActivity {

    public static int pixel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageView fab = (ImageView) findViewById(R.id.colorPickerImage);
        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    Bitmap bitmap = ((BitmapDrawable) fab.getDrawable()).getBitmap();

                    int xCoord = (int)(((float)bitmap.getWidth()/(float)fab.getWidth())*event.getX());
                    int yCoord = (int)(((float)bitmap.getHeight()/(float)fab.getHeight())*event.getY());

                    pixel = bitmap.getPixel(xCoord, yCoord);
                    System.out.println(pixel);
                    String strColor = String.format("#%06X", 0xFFFFFF & pixel);
                    System.out.println(strColor);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                finish();
                return true;
            }
        });
    }

}
