package com.christian.deuce_1;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class Ball {

    private RectF postion;
    private int sizeOfBall;     //the ball will be perfect square with this variable being size of each side the same
    private float mXvelocity;
    private float mYvelocity;

    private float screenX;
    private float screenY;

    public Ball(int screenSizeX, int screenSizeY){
        //use the screensize to make the ball a percentage of it
        sizeOfBall = (int)(screenSizeX * .01);

        Log.i("Ball", "Ball: screenX:" + screenSizeX + " sizeY: " + screenSizeY);

        //testing speeds for the velocities
        mXvelocity = (screenSizeX / 4);
        mYvelocity = (screenSizeY / 3);

        screenX = screenSizeX;
        screenY = screenSizeY;

        postion = new RectF();

        //call the method that will set the ball in position
        reset();



    }

    public RectF getPosition(){
        return postion;
    }

    //will set the ball in the middle of the screen
    public void reset(){
        postion.left = screenX / 2;
        postion.top = screenY / 2;
        postion.right = postion.left + sizeOfBall;
        postion.bottom = postion.top + sizeOfBall;
    }

    //sets the direction the ball is moving
    public void changeXdirection(){
        mXvelocity = -mXvelocity;
    }

    public void changeYdirection(){
        mYvelocity = -mYvelocity;
    }

    public void update(long fps){
        postion.left = postion.left + (mXvelocity / fps);
        postion.top = postion.top + (mYvelocity / fps);
        postion.right = postion.left + sizeOfBall;
        postion.bottom = postion.top + sizeOfBall;
    }

    //this method will bounce the ball according to where on the racket it hit
    public void racketHit(RectF racketPosition){

        //find the middle of the racket, vertically
        float racketCenter = racketPosition.top + (racketPosition.height() / 2);
        //find the middle of the ball alsoe vertically
        float ballCenter = postion.top + (postion.height() / 2);

        float relativeHit = racketCenter - ballCenter;

        if(relativeHit < 0){
            //go up
            mYvelocity = Math.abs(mYvelocity);
        } else {
            //go down
            mYvelocity = -Math.abs(mYvelocity);
        }

        //reverse the x direction of the ball once we know which way vertically it'll go
        changeXdirection();

    }
}
