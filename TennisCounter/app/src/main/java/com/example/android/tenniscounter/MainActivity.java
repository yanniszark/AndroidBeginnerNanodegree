package com.example.android.tenniscounter;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.PopupWindow;

import static android.R.attr.max;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class MainActivity extends AppCompatActivity {

    private static final int ADVANTAGE = 41;
    /* Variables to track in-game, game and set scores */
    int scorePlayer1 = 0, scorePlayer2 = 0;
    int gamesPlayer1 = 0, gamesPlayer2 = 0;
    int setsPlayer1 = 0, setsPlayer2 = 0;
    /* Variable to track serving player */
    int currServe = 1;
    /* Initializing some TextViews as globals to limit use of findViewbyId */
    TextView scoreView1, scoreView2;
    TextView serveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoreView1 = (TextView) findViewById(R.id.player_1_score);
        scoreView2 = (TextView) findViewById(R.id.player_2_score);
        serveView = (TextView) findViewById(R.id.serving_player);


        setupEditText((EditText) findViewById(R.id.player_1));
        setupEditText((EditText) findViewById(R.id.player_2));


    }

    protected void setupEditText(final EditText editText){
        editText.getBackground().clearColorFilter();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editText.setCursorVisible(false);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });

        editText.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View v){
                editText.setCursorVisible(true);
            }
        });

    }

    /**
    Displays score of player whose tag is given by playerNumber
     */
    protected void displayScore(int playerNumber){
        if (playerNumber == 1){
            if (scorePlayer1 == ADVANTAGE)
                scoreView1.setText(getString(R.string.advantage).toUpperCase());
            else
            scoreView1.setText(String.valueOf(scorePlayer1));
        }
        else{
            if (scorePlayer2 == ADVANTAGE)
                scoreView2.setText(getString(R.string.advantage).toUpperCase());
            else
                scoreView2.setText(String.valueOf(scorePlayer2));
        }
    }

    /**
     * Displays the game score of the player given in the playerNumber parameter.
     * @param playerNumber
     */
    protected void displayGames(int playerNumber){
        if (playerNumber == 1) {
            TextView scoreView = (TextView) findViewById(R.id.game_count_1);
            scoreView.setText(String.valueOf(gamesPlayer1));
        } else {
            TextView scoreView = (TextView) findViewById(R.id.game_count_2);
            scoreView.setText(String.valueOf(gamesPlayer2));
        }
    }

    /**
     * Displays the set score of the player given in the playerNumber parameter.
     * @param playerNumber
     */
    protected void displaySets(int playerNumber){
        if (playerNumber == 1) {
            TextView scoreView = (TextView) findViewById(R.id.set_count_1);
            scoreView.setText(String.valueOf(setsPlayer1));
        } else {
            TextView scoreView = (TextView) findViewById(R.id.set_count_2);
            scoreView.setText(String.valueOf(setsPlayer2));
        }
    }

    /**
     Displays serving player
     */
    protected void displayServe(){
        serveView.setText(getString(R.string.player) + " " + currServe);
    }

    /**
     * Returns the number of the non-serving player
     */
    protected int reverseServe(){
        if (currServe == 1)
            return(2);
        else
            return(1);
    }


    /**
     * Method to display score = 15 to the corresponding player, who is found by the
     * view tag.
     * @param v
     */
    public void fifteenScore(View v){
        if (v.getTag() == null)
            return;
        int playerNumber = Integer.parseInt((String) v.getTag());
        if (playerNumber == 1){
            if (scorePlayer1 == 15)
                return;
            scorePlayer1 = 15;
            displayScore(1);

        }
        else{
            if (scorePlayer2 == 15)
                return;
            scorePlayer2 = 15;
            displayScore(2);
        }
        displayServe();
    }

    /**
     * Method to display score = 30 to the corresponding player, who is found by the
     * view tag.
     * @param v
     */
    public void thirtyScore(View v){
        if (v.getTag() == null)
            return;
        int playerNumber = Integer.parseInt((String) v.getTag());
        if (playerNumber == 1){
            if (scorePlayer1 == 30)
                return;
            scorePlayer1 = 30;
            displayScore(1);

        }
        else{
            if (scorePlayer2 == 30)
                return;
            scorePlayer2 = 30;
            displayScore(2);
        }
        displayServe();
    }

    /**
     * Method to display score = 40 to the corresponding player, who is found by the
     * view tag.
     * @param v
     */
    public void fortyScore(View v){
        if (v.getTag() == null)
            return;
        int playerNumber = Integer.parseInt((String) v.getTag());
        if (playerNumber == 1){
            if (scorePlayer1 == 40)
                return;
            scorePlayer1 = 40;
            displayScore(1);

        }
        else{
            if (scorePlayer2 == 40)
                return;
            scorePlayer2 = 40;
            displayScore(2);
        }
        displayServe();
    }

    /**
     * Method to display score = ADV to the corresponding player, who is found by the
     * view tag.
     * @param v
     */
    public void advantageScore(View v){
        if (v.getTag() == null)
            return;
        int playerNumber = Integer.parseInt((String) v.getTag());
        if (playerNumber == 1){
            if (scorePlayer1 == ADVANTAGE || scorePlayer2 != 40)
                return;
            scorePlayer1 = ADVANTAGE;
        }
        else{
            if (scorePlayer2 == ADVANTAGE || scorePlayer1 != 40)
                return;
            scorePlayer2 = ADVANTAGE;
        }
        displayScore(playerNumber);
    }

    /**
     * Method to increase game score of the corresponding player (found by view tag) by 1.
     * Checks if game score >= 6 and score difference >= 2 and updates set score if necessary.
     * @param v
     */
    public void gameScore(View v){
        if (v.getTag() == null)
            return;
        int playerNumber = Integer.parseInt((String) v.getTag());
        resetScore();
        currServe = reverseServe();
        displayServe();
        if (playerNumber == 1){
            gamesPlayer1 += 1;
            if (gamesPlayer1 >= 6 && gamesPlayer1 - gamesPlayer2 >= 2) {
                resetGames();
                setScore(v);
            }
            else
                displayGames(playerNumber);
        }
        else{
            gamesPlayer2 += 1;
            if (gamesPlayer2 >= 6 && gamesPlayer2 - gamesPlayer1 >= 2) {
                resetGames();
                setScore(v);
            }
            else
                displayGames(playerNumber);
        }
    }

    /**
     * Method to increase set score of the corresponding player (found by view tag) by 1.
     * It also checks if the game has ended and declares the winner if necessary.
     * @param v
     */
    public void setScore(View v){
        if (v.getTag() == null)
            return;
        int playerNumber = Integer.parseInt((String) v.getTag());
        resetGames();
        if (playerNumber == 1){
            setsPlayer1 += 1;
            if ((setsPlayer1 > setsPlayer2) && (setsPlayer1 >= 3)){
                declareWinner(1);
            }
            else
                displaySets(playerNumber);
        }
        else{
            setsPlayer2 += 1;
            if ((setsPlayer2 > setsPlayer1) && (setsPlayer2 >= 3)){
                declareWinner(2);
            }
            else
                displaySets(playerNumber);
        }
    }

    public void reset(View v){
        resetSets();
    }

    /**
     * Resets in-game score count.
     */
    protected void resetScore(){
        scorePlayer1 = scorePlayer2 = 0;
        displayScore(1);
        displayScore(2);
    }

    /**
     * Resets game count.
     */
    protected void resetGames() {
        gamesPlayer1 = gamesPlayer2 = 0;
        displayGames(1);
        displayGames(2);
        resetScore();
    }

    /**
     * Resets set count.
     */
    protected void resetSets(){
        setsPlayer1 = setsPlayer2 = 0;
        displaySets(1);
        displaySets(2);
        resetGames();
    }

    /**
     * Method to display the popup message in order to declare winner.
     * @param playerNumber
     */
    protected void declareWinner (int playerNumber){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View winnerView = inflater.inflate(R.layout.winner_popup,null);
        final PopupWindow mPopupWindow = new PopupWindow(
                winnerView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(6.0f);
        }
        Button resetButton = (Button) winnerView.findViewById(R.id.dismiss);
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                resetSets();
                mPopupWindow.dismiss();
            }
        });
        TextView winnerText = (TextView) winnerView.findViewById(R.id.winner_textview);
        if (playerNumber == 1)
            winnerText.setText(getString(R.string.player1_wins));
        else
            winnerText.setText(getString(R.string.player2_wins));
        mPopupWindow.showAtLocation((RelativeLayout) findViewById(R.id.activity_main), Gravity.CENTER,0,0);
    }
}




