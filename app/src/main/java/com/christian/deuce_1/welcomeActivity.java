package com.christian.deuce_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
Everything seems to be working perfectly for this activity. I could still add some cosmetic things
like a text file popping up if the user does not make a selection or something like that.
 */
public class welcomeActivity extends Activity implements View.OnClickListener{

    //declare all my variables
    private Button startButton;
    private Button rightButton;
    private Button leftButton;
    private TextView welcomeText;
    private TextView sideChosen;
    private boolean rightSelected = false;  //these need to default as false so user selection is only accepted after
    private boolean leftSelected = false;   //the user has chosen one button
    private final int RACKET_ON_LEFT = -1;
    private final int RACKET_ON_RIGHT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //get all my objects created in the XML file
        startButton = (Button) findViewById(R.id.welcomeActivity_button);
        rightButton = findViewById(R.id.welcomeActivity_rightButton);
        leftButton = findViewById(R.id.welcomeActivity_leftButton);
        sideChosen = findViewById(R.id.welcomeActivity_bottomText);

        //set the click listeners of all buttons
        rightButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        startButton.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {

        //declare the two variables that will be used by both scenarios
        Drawable selectedPaint = ContextCompat.getDrawable(getApplicationContext(),R.drawable.test_button);
        Drawable unSelectedPaint = ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_selector);
        Drawable startButtonBackground = ContextCompat.getDrawable(getApplicationContext(), R.drawable.start_button);
        String message;
        //check which button was clicked
        if(view.getId() == R.id.welcomeActivity_rightButton){

            //the right button has been selected

            /*
            Testing idea. When button is selected it should grow a white border around it letting know user
            that this is the selection they made. Based on my understanding to change a button it
            is better through the XMl by using drawables. I already have a selector drawable on the
            left and right buttons that i apply from XML. My test idea is to programmaly apply a new drawable
            to the button from here. The new drawable will only have one item which will be the default
            button look with the white border. Each time another button is selected then the previous
            button will have their drawable switched back to the original selector.
             */
            rightSelected = true;
            rightButton.setBackground(selectedPaint);
            startButton.setEnabled(true);
            startButton.setBackground(startButtonBackground); //give the start button the default drawable

            //change the message showing the user which side they chose
            sideChosen.setEnabled(true);
            message = getResources().getString(R.string.right_side_chosen);
            sideChosen.setText(message);

            //now that we changed the look of the right button change the look of the left
            if(leftSelected){
                /*
                this change only happens if the left has been selected before. This ensures that once the player
                has made a decision the screen will show them which one they have chosen before they start
                the game. If they want to change selection they only have to click on the other button
                and back and froth as many times before clicking start.
                 */
                leftButton.setBackground(unSelectedPaint);
                leftSelected = false; //set it back to false so no confusion
            }
        }
        else if(view.getId() == R.id.welcomeActivity_leftButton){
            //left button has been selected

            Log.i("getID", "onClick: left button was clicked");

            /*
            do the same as when the right button was selected. Change the drawable of the selected button and set
            the boolean to true. finally check if the other button is currently selected and if so
            switch them out
             */
            leftSelected = true;
            leftButton.setBackground(selectedPaint);
            startButton.setEnabled(true);
            startButton.setBackground(startButtonBackground);

            //change the message showing user what side they chose
            sideChosen.setEnabled(true);
            message = getResources().getString(R.string.left_side_chosen);
            sideChosen.setText(message);

            //check if the right is currently selected
            if(rightSelected){
                //set it back to the default and set boolean to false so no confusions
                rightButton.setBackground(unSelectedPaint);
                rightSelected = false;
            }
        }
        else {
            //this is if the start button was selected which will call the other activities

            Log.i("startButton", "onClick: startButton was successfully  clicked");
            Intent startGame = new Intent(welcomeActivity.this, Deuce.class);

            //add an error check if both left and right are selected
            //pass the user selection to the game app, Deuce
            if(leftSelected){
                startGame.putExtra("userSelection", RACKET_ON_LEFT);
            }
            else if(rightSelected){
                startGame.putExtra("userSelection", RACKET_ON_RIGHT);
            }
            else {
                /* no selection has been made some error occured because the code should
                not get to this point until a selection has been made. For example the start
                button will not become available until a selection has been made.
                 */
                Log.d("startGame", "ERROR: no selection made");
            }

            Log.i("onClick", "onClick: the other activity will launch from here");
            //welcomeActivity.this.startActivity(startGame);
            //either one works i think
            startActivity(startGame);

        }
    }
}