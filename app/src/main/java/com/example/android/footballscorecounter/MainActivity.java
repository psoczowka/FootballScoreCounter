package com.example.android.footballscorecounter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnDragListener;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.SystemClock.elapsedRealtime;
import static com.example.android.footballscorecounter.R.id.timerValue;

public class MainActivity extends Activity implements OnDragListener, View.OnLongClickListener {

    // chronometer variables and buttons
    private Chronometer chronometer;
    private Button startButton;
    private Button pauseButton;
    long pausedTime = 0;

    //score variables
    int scoreTeamA = 0;
    int scoreTeamB = 0;
    //foul variables
    int faulTeamA = 0;
    int faulTeamB = 0;

    // variable for displaying message when start button is clicked
    boolean tutorial = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // timer and buttons
        chronometer = (Chronometer) findViewById(timerValue);
        startButton = (Button) findViewById(R.id.startButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);

        // set on long click listener for ball view
        findViewById(R.id.ball_button).setOnLongClickListener(this);

        // set drag listener for goal_A area
        findViewById(R.id.goal_area_A).setOnDragListener(this);
        // set drag listener for goal_B area
        findViewById(R.id.goal_area_B).setOnDragListener(this);

        // set on click listener for start button
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //add paused time to timer and start timer
                chronometer.setBase(elapsedRealtime() + pausedTime);
                chronometer.start();

                //display tutorial when start button is pressed first time
                if (tutorial == true) {
                    //display tutorial message as toast
                    Toast.makeText(MainActivity.this, "Drag and drop ball to score a goal", Toast.LENGTH_LONG).show();
                    //set tutorial to false (its no longer needed)
                    tutorial = false;
                } else {
                    return;
                }
            }
        });

        // set on click listener for pause button
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

               //get elapsed time, save it to pause time and stop timer
                pausedTime = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();
            }
        });
    }

    // display score of team A
    private void displayScoreA(int score) {
        TextView bTeamScoreTextView = (TextView) findViewById(R.id.score_Team_A);
        bTeamScoreTextView.setText(String.valueOf(score));
    }

    // display score of team B
    private void displayScoreB(int score) {
        TextView bTeamScoreTextView = (TextView) findViewById(R.id.score_Team_B);
        bTeamScoreTextView.setText(String.valueOf(score));
    }

    //display fouls of team A
    private void displayFaulA(int faulTeamA) {
        TextView aTeamFaulTextView = (TextView) findViewById(R.id.fauls_team_a);
        aTeamFaulTextView.setText(String.valueOf(faulTeamA));
    }

    //display fouls of team B
    private void displayFaulB(int faulTeamB) {
        TextView aTeamFaulTextView = (TextView) findViewById(R.id.fauls_team_b);
        aTeamFaulTextView.setText(String.valueOf(faulTeamB));
    }

    public void faulsTeamA(View view) {
        faulTeamA++;
        displayFaulA(faulTeamA);
    }

    public void faulsTeamB(View view) {
        faulTeamB++;
        displayFaulB(faulTeamB);
    }

    // 'reset game' button
    public void reset(View view) {
        //reset score
        scoreTeamA = 0;
        scoreTeamB = 0;
        //display updated score
        displayScoreA(scoreTeamA);
        displayScoreB(scoreTeamB);

        //reset fauls
        faulTeamA = 0;
        faulTeamB = 0;
        //display updated fauls
        displayFaulA(faulTeamA);
        displayFaulB(faulTeamB);

        //reset EditText view for team A
        EditText addNameTeamAText = (EditText) findViewById(R.id.team_a_text);
        addNameTeamAText.setText("Team A");

        //reset EditText view for team B
        EditText addNameTeamBText = (EditText) findViewById(R.id.team_b_text);
        addNameTeamBText.setText("Team B");

        //stop timer and set its value to 0
        chronometer.stop();
        chronometer.setBase(elapsedRealtime());
        pausedTime = 0;

    }

    //add drag and drop functionality
    // call onLongClick method when ball has been touched and held
    @Override
    public boolean onLongClick(View imageView) {

        // the ball has been touched
        // create clip data holding data of the type MIMETYPE_TEXT_PLAIN
        ClipData clipData = ClipData.newPlainText("", "");

        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);
            /*start the drag - contains the data to be dragged,
            metadata for this data and callback for drawing shadow*/
        imageView.startDrag(clipData, shadowBuilder, imageView, 0);
        //make ball ImageView invisible while dragging view
        imageView.setVisibility(View.INVISIBLE);

        return true;
    }

    @Override
    public boolean onDrag(View View, final DragEvent dragEvent) {

        // Defines a variable to store the action type for the incoming event
        final View draggedImageView = (View) dragEvent.getLocalState();

        // Handles each of the expected events
        switch (dragEvent.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:

                // Determines if this View can accept the dragged data
                if (dragEvent.getClipDescription()
                        .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                    // returns true to indicate that the View can accept the dragged data.
                    return true;

                }

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                //ignore the event
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                //ignore the event
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                //ignore the event
                return false;

            case DragEvent.ACTION_DROP:
            // the action only sent here if ACTION_DRAG_STARTED returned true
            // return true if successfully handled the drop

                if (View.getId() == R.id.goal_area_A) {
                    //add +1 to team B score
                    scoreTeamB++;
                    //display team B score
                    displayScoreB(scoreTeamB);

                    //get name of team B in order to display message while team scores a goal
                    EditText getNameB = (EditText) findViewById(R.id.team_b_text);
                    String nameOfTeamB = getNameB.getText().toString();

                    //toast for showing who scored goal
                    Toast.makeText(MainActivity.this, "Team " + nameOfTeamB + " scores", Toast.LENGTH_SHORT).show();

                    //after scoring goal set ball ImageView back to visible
                    draggedImageView.setVisibility(View.VISIBLE);

                    return true;

                } else if (View.getId() == R.id.goal_area_B) {

                    //add +1 to team A score
                    scoreTeamA++;
                    //display team A score
                    displayScoreA(scoreTeamA);

                    //get name of team A in order to display message while team scores a goal
                    EditText getNameA = (EditText) findViewById(R.id.team_a_text);
                    String nameOfTeamA = getNameA.getText().toString();

                    //toast for showing who scored goal
                    Toast.makeText(MainActivity.this, "Team " + nameOfTeamA + " scores", Toast.LENGTH_SHORT).show();

                    //after scoring goal set ball ImageView back to visible
                    draggedImageView.setVisibility(View.VISIBLE);

                    return true;

                }

            case DragEvent.ACTION_DRAG_ENDED:
                //   if the drop was not successful, set the ball to visible
                if (!dragEvent.getResult()) {

                    // to prevent app from crashing on api lower than 24
                    // set draggedImageView as runnable
                    draggedImageView.post(new Runnable() {
                        @Override
                        public void run() {
                            draggedImageView.setVisibility(ImageView.VISIBLE);
                        }
                    });
                }

                return true;
            // An unknown action type was received.
            default:
                Log.e("DragDrop Example","Unknown action type received by OnDragListener.");
                break;
        }

        return false;
    }
}