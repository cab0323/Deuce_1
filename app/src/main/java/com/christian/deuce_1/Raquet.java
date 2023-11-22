package com.christian.deuce_1;

import android.graphics.RectF;

public class Raquet {

    private int directionMoving;
    private RectF racketLocation;
    private int screenSizeX, screenSizeY;
    private int racketSizeHorizontal;
    private int racketSizeVertical;
    private int racketMovingDirection;

    //variables will keep location of the rackets left and top locations
    private float leftLocation;
    private float topLocation;

    private float racketSpeed;
    private int upperLimit;

    private final int RACKET_MOVING_UP = 1;
    private final int RACKET_MOVING_DOWN = -1;
    private final int RACKET_NOT_MOVING = 0;

    public Raquet(int screenSizeX, int screenSizeY, int leftRight){
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;

        racketSizeHorizontal = (int)(this.screenSizeX * .025);
        racketSizeVertical = (int)(this.screenSizeY * .15);

        racketSpeed = screenSizeY;

        //int fourths = screenSizeX * (1/4);
        if(leftRight == 1){
            //put the bat on the right hand side of the screen
            leftLocation = screenSizeX - (racketSizeHorizontal * 2);
        }
        else {
            //put the bat on the left hand side of the screen
            leftLocation = racketSizeHorizontal;
        }

        topLocation= screenSizeY / 2;

        //set the upper limit for the racket, can't go above this point. Will be the bottom of the HUD screen. HUD will always be 20% of screen vertical size
        upperLimit = (int)(screenSizeY * .20);

        racketLocation = new RectF(leftLocation, topLocation, leftLocation + racketSizeHorizontal,
                topLocation + racketSizeVertical);

    }

    public void setRacketMovingDirection(int movingDirection){
        racketMovingDirection = movingDirection;
    }

    public RectF getRacketLocation() {
        return racketLocation;
    }

    public void update(long fps){
        // Move the bat based on the mBatMoving variable
        // and the speed of the previous frame
        if(racketMovingDirection == RACKET_MOVING_UP){
            topLocation = topLocation - racketSpeed / fps;
        }
        if(racketMovingDirection == RACKET_MOVING_DOWN){
            topLocation = topLocation + racketSpeed / fps;
        }

        // Stop the bat going off the screen
        if(topLocation < upperLimit){
            topLocation = upperLimit;
        }
        else if(topLocation + racketSizeVertical > screenSizeY){
            topLocation = screenSizeY - racketSizeVertical;
        }

        // Update mRect based on the results from
        // the previous code in update
        racketLocation.top = topLocation;
        racketLocation.bottom = topLocation + racketSizeVertical;
    }
}
