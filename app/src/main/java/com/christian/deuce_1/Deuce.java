package com.christian.deuce_1;

import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.os.Bundle;
import android.view.MotionEvent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.widget.ImageView;
import java.util.Random;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintSet;

public class Deuce extends Activity {

    //global variables
    int numberHorizontalPixels;
    int numberVerticalPixels;
    int horizontalPixelTouched;
    int verticalPixelTouched;

    //variables used for drawing
    ImageView gameView;



    DeuceEngine dE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //get the size of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        gameView = new ImageView(this);

        //getting the user selection passed from the previous activity
        Intent intent = getIntent();
        int userSelection = intent.getIntExtra("userSelection", -1); //default position on left


        //initialize the engine
        dE = new DeuceEngine(this, size.x, size.y, userSelection);

        setContentView(dE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        dE.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        dE.pause();
    }
}