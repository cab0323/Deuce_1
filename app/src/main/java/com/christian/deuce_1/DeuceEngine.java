package com.christian.deuce_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import java.util.Random;

import androidx.annotation.NonNull;

public class DeuceEngine extends SurfaceView implements Runnable{

    private int horizontalResolution;
    private int verticalResolution;
    private int horizontalPixelTouched;
    private int verticalPixelTouched;

    //declaring my variables to calculate fps. Milliseconds in a second is final
    private long mFPS;
    private final long MILLIS_IN_SECOND = 1000;
    private volatile boolean mRunning;
    private volatile boolean surfaceCreated = false;
    private boolean mPaused = true;



    private Bitmap myBitmap;

    private ImageView gameView;

    private Bitmap blankBitmap;
    private Canvas canvas;
    private Paint paint;

    //my object variables
    private Ball mBall;
    private Raquet mRaquet;
    private Raquet botRacket;
    private Thread mThread;

    private SurfaceHolder mHolder;
    private final int RACKET_MOVING_UP = 1;
    private final int RACKET_MOVING_DOWN = -1;
    private final int RACKET_NOT_MOVING = 0;
    private final int RACKET_LEFT = -1;
    private final int RACKET_RIGHT = 1;

    public DeuceEngine(Context context, int x, int y, int userSide){
        super(context);

        //set the size of the screen
        horizontalResolution = x;
        verticalResolution = y;


        //initialize the drawing variables
        blankBitmap = Bitmap.createBitmap(horizontalResolution, verticalResolution, Bitmap.Config.ARGB_8888);
        //canvas = new Canvas(blankBitmap);
        mHolder = getHolder();
        paint = new Paint();

        //initialize objects
        mBall = new Ball(x, y);

        //put the rackets on the correct side depending where the user wants theirs
        if(userSide == RACKET_LEFT) {
            //user wants the left side
            mRaquet = new Raquet(x, y, RACKET_LEFT);    //users racket on the left
            botRacket = new Raquet(x, y, RACKET_RIGHT); //AI racket on the right
        }
        else {
            //user wants the right side
            mRaquet = new Raquet(x, y, RACKET_RIGHT);
            botRacket = new Raquet(x, y, RACKET_LEFT);
        }


        mHolder.addCallback(new myCallback(this));

    }

    @Override
    public void run() {
        //while the thread is running
        while(mRunning){
            //if the surface has been created continue like normal,
            //otherwise skip and continue loop until surface has been created
            if(surfaceCreated) {
                //keep track of how long this frame will take
                long frameStartTime = System.currentTimeMillis();

                //if the game is not paused
                if (!mPaused) {
                    //first move the AI bot
                    moveAI();

                    //update the positions of each object
                    update();

                    //detect collisions at new positions
                    detectCollisions();
                }

                //draw the objects
                draw();

                long frameEndTime = System.currentTimeMillis() - frameStartTime;

                if (frameEndTime > 0) {
                    mFPS = MILLIS_IN_SECOND / frameEndTime;
                }
            }
        }
    }

    public void update(){
        mRaquet.update(mFPS);
        botRacket.update(mFPS);
        mBall.update(mFPS);
    }

    public void detectCollisions(){
        //check if there was a racket hit
        //first the user controlled racket
        if(RectF.intersects(mRaquet.getRacketLocation(), mBall.getPosition())){
            mBall.racketHit(mRaquet.getRacketLocation());
        }

        //now the ai racket
        if(RectF.intersects(botRacket.getRacketLocation(), mBall.getPosition())){
            mBall.racketHit(botRacket.getRacketLocation());
        }

        //check if the ball has hit the sides or outside
        //top
        if(mBall.getPosition().top < 1){
            //ball hit the top bounce back down
            mBall.changeYdirection();
        }

        //bottom
        if (mBall.getPosition().bottom > verticalResolution){
            //ball hit bottom bounce back into play
            mBall.changeYdirection();
        }

        /*
        both the left and right side will reset the ball because they are essentially "goals". Since there
        is two rackets each guarding they're side if the ball goes through then reset the ball.
         */
        if(mBall.getPosition().left < 1){
            mBall.reset();
        }

        if(mBall.getPosition().right > horizontalResolution){
            mBall.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction() & MotionEvent.ACTION_MASK){
            //if the player touched the screen
            case (MotionEvent.ACTION_DOWN):

                //get the location on screen where touched
                horizontalPixelTouched = (int)event.getX();
                verticalPixelTouched = (int)event.getY();

                //unpause game and start moving objects
                mPaused = false;

                Log.i("ACTION_DOWN", "onTouchEvent: the screen was touched");

                //if player touched bottom half of screen move racket towards the top
                if(verticalPixelTouched > (verticalResolution / 2)){
                    mRaquet.setRacketMovingDirection(RACKET_MOVING_DOWN);
                    Log.i("MOVE DOWN", "onTouchEvent: the bat is moving down");
                }
                //the user touched top half of screen so move racket down
                else {
                    mRaquet.setRacketMovingDirection(RACKET_MOVING_UP);
                    Log.i("MOVE UP", "onTouchEvent: the bat is moving up");
                }

                break;

            case (MotionEvent.ACTION_UP):

                Log.i("ACTION_UP", "onTouchEvent: player picked finger up");

                //when player lets go of screen stop the racket from moving
                mRaquet.setRacketMovingDirection(RACKET_NOT_MOVING);

                break;

        }

        return true;
    }

    public void draw(){
        //gameView.setImageBitmap(blankBitmap);

        if (mHolder.getSurface().isValid()) {
            //Log.d("DRAW", "draw: mHolder is VALID");

            //lock the canvas so we can be the only ones using it while we draw
            canvas = mHolder.lockCanvas();
            //wipe the screen
            canvas.drawColor(Color.rgb(22, 13, 158));

            //set the color of the paint
            paint.setColor(Color.argb(255, 255, 255, 255));

            //draw the net
            //find the half of the court
            int halfCourtHorizontal = horizontalResolution / 2;
            int halfCourtVertical = verticalResolution / 2;


            //draw the net in the middle
            //first the vertical lines
            canvas.drawLine(halfCourtHorizontal - 20, 0, halfCourtHorizontal - 20, horizontalResolution, paint);
            canvas.drawLine(halfCourtHorizontal + 20, 0, halfCourtHorizontal + 20, verticalResolution, paint);

            //draw horizontal lines
            int netStart = 0;
            for (; netStart < verticalResolution; netStart += 3) {
                canvas.drawLine(halfCourtHorizontal - 20, netStart, halfCourtHorizontal + 20, netStart, paint);
            }


            //draw the ball
            canvas.drawRect(mBall.getPosition(), paint);

            //draw the user controlled racket
            canvas.drawRect(mRaquet.getRacketLocation(), paint);

            //draw the AI racket
            canvas.drawRect(botRacket.getRacketLocation(), paint);

            //unlock the canvas memory and post what we drew
            mHolder.unlockCanvasAndPost(canvas);

        } else {
            Log.d("DRAW", "draw: NOT VALID SURFACE");

            System.exit(1);
        }

    }

    /*
    Call the thread from the activiyt life cycle. That way when the person puts the phone to sleep the thread will
    stop running. IF the thread is called from the surface callback then the surface won't be destroyed when the
    phone goes to sleep therefore the thread would keep going. The surface callback will set the surfaceCreated
    boolean to true which whill let thread begin drawing. The thread will start onResume but won't be able to draw
    until the surfacecallback sets surfaceCreated boolean to true indicating the surface is now created and ready to
    draw on
     */
    public void resume(){
        mRunning = true;

        mThread = new Thread(this);
        mThread.start();
    }

    public void pause(){
        mRunning = false;

        try {
            mThread.join();
        } catch (InterruptedException e){
            Log.d("onPause", "onPause: did not join thread");
        }
    }

    class myCallback implements SurfaceHolder.Callback{
        DeuceEngine d;
        public myCallback(DeuceEngine deuceEngine){
            d = deuceEngine;
        }

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

            surfaceCreated = true;

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            surfaceCreated = false;
        }
    }


    /*
    testing this idea. To make the AI racket move it will align itself with the middle of the ball and always move there.
    I'm thinking i can use the random to generate a number and this will be the chances that the racket does get
    into position.
    so as not to make an unbeatable bot. Other way i haven,t figured out yet is how to estimate the path
    or the trajectory of the ball and move the bat there.
     */
    public void moveAI(){
        //this will only set the AI's move up or move down then the update method will be called after
        //which will actually move it on the screen

        /*
        Time to test ideas on how to make the AI "miss" the ball so as to not make it invincible.
        First idea is to do the random number generator between 0,1 basically a 50/50 chance of weather
        the racket will hit the ball. If the racket will miss than to make it realistic make the
        racket move, if it stays still it will not look realistic, but make it miss. Miss by stopping the
        racket's movement once it gets to a balls height distance away from the ball. If the ball is within the
        rackets "coordinates", meaing it will hit the racket and the racket is supposed to miss then
        don't move racket since it will not look realistic. Only miss if the random nnumber says miss
        and the racket is below or above the ball.
         */
        //calculate the miss
        Random random = new Random();
        boolean miss = false;
        int chanceOfMiss = random.nextInt(4);
        //75% chance of missing
        if(chanceOfMiss <= 2){
            //the racket should miss
            miss = true;
        }
        //first let's calculate the center of both the racket and the ball
        //float racketCenter = botRacket.getRacketLocation().top + (botRacket.getRacketLocation().height() / 2);
        float ballCenter = mBall.getPosition().top + (mBall.getPosition().height() / 2);

/*        //now move the racket accordingly
        if(racketCenter < ballCenter){
            //the racket is above the ball so move down
            botRacket.setRacketMovingDirection(RACKET_MOVING_DOWN);
        }
        else if (racketCenter > ballCenter){
            //the racket is below the ball so move up
            botRacket.setRacketMovingDirection(RACKET_MOVING_UP);
        }
        else {
            //the racket is equal to the ball so stop moving
            botRacket.setRacketMovingDirection(RACKET_NOT_MOVING);
        }*/

        //testing if it might be smoother to line up ball center with whole racket not just the racket center
        if(botRacket.getRacketLocation().top < ballCenter && botRacket.getRacketLocation().bottom > ballCenter){
            //ball will hit racket, so do not move
            botRacket.setRacketMovingDirection(RACKET_NOT_MOVING);
        }
        else if (botRacket.getRacketLocation().top > ballCenter){
            //racket is below the ball
            //first check if the racket should miss
            if(miss){
                //check how far the racket is from the ball
                float distance = botRacket.getRacketLocation().top - mBall.getPosition().bottom;
                distance = Math.abs(distance);

                if(distance > (mBall.getPosition().height() * 2)){
                    botRacket.setRacketMovingDirection(RACKET_MOVING_UP);
                }
                else {
                    botRacket.setRacketMovingDirection(RACKET_NOT_MOVING);
                }
            }
            else {
                //the racket is below the ball so move it up
                botRacket.setRacketMovingDirection(RACKET_MOVING_UP);
            }

        }
        else if (botRacket.getRacketLocation().bottom < ballCenter){
            //racket is above the ball
            //first check if racket should miss
            if(miss){
                //check the distance between the ball and racket
                float distance = mBall.getPosition().top - botRacket.getRacketLocation().bottom;
                distance = Math.abs(distance);

                //now check if the distance is too close, if not then move the racket otherwise stay and have near miss
                if(distance > (mBall.getPosition().height() * 2)){
                    //racket is still far so move until close enough for miss
                    botRacket.setRacketMovingDirection(RACKET_MOVING_DOWN);
                }
                else {
                    //the racket is close enough don't move and have near miss
                    botRacket.setRacketMovingDirection(RACKET_NOT_MOVING);
                }
            }
            else{
                //the racket is above the ball so move down
                botRacket.setRacketMovingDirection(RACKET_MOVING_DOWN);
            }

        }
    }

}
