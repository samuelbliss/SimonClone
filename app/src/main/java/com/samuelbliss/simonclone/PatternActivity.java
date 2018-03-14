package com.samuelbliss.simonclone;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class PatternActivity extends AppCompatActivity implements View.OnTouchListener {

    // Declaring all the variables needed to use throughout
    Vector<Integer> playerPattern = new Vector<>(), simonPattern = new Vector<>();
    private int tempo, count, playerChoice, numChoices, score;
    private int colorBtns[], colorImg[], pressedImg[], soundID[];
    private Simon simon;
    private CountDown countDown;
    private SoundPool soundPool;
    private Set<Integer> soundsLoaded;
    private boolean lockBtns;
    public TextView scoreTV, turnTV, modeTV;
    public ImageButton greenB, blueB, yellowB, redB;
    public Button replayB;
    public boolean match;
    private String highScore1key = "key1", highScore2key = "key2", highScore3key = "key3", highScore4key = "key4", highScore5key = "key5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        soundsLoaded = new HashSet<Integer>();

    }

    /**
     * Initiates all variables so and function is called when variables are needed
     */
    private void initiateVariables() {
        count = 0;
        score = 0;
        simonPattern.clear();
        playerPattern.clear();
        tempo = 1000;
        lockBtns = true;

        colorBtns = new int[]{R.id.greenButton, R.id.blueButton, R.id.yellowButton, R.id.redButton};
        colorImg = new int[]{R.drawable.green_rectangle, R.drawable.blue_rectangle, R.drawable.yellow_rectangle, R.drawable.red_rectangle};
        pressedImg = new int[]{R.drawable.green_pressed, R.drawable.blue_pressed, R.drawable.yellow_pressed, R.drawable.red_pressed};
        soundID = new int[]{soundPool.load(this, R.raw.simon1, 1), soundPool.load(this, R.raw.simon2, 1), soundPool.load(this, R.raw.simon3, 1), soundPool.load(this, R.raw.simon4, 1), soundPool.load(this, R.raw.countdown, 1), soundPool.load(this, R.raw.start, 1)};

        scoreTV = (TextView) findViewById(R.id.score_textView);
        modeTV = (TextView) findViewById(R.id.mode_textView);
        turnTV = (TextView) findViewById(R.id.turn_textView);

        greenB = (ImageButton) findViewById(R.id.greenButton);
        greenB.setOnTouchListener(this);
        blueB = (ImageButton) findViewById(R.id.blueButton);
        blueB.setOnTouchListener(this);
        yellowB = (ImageButton) findViewById(R.id.yellowButton);
        yellowB.setOnTouchListener(this);
        redB = (ImageButton) findViewById(R.id.redButton);
        redB.setOnTouchListener(this);
        replayB = (Button) findViewById(R.id.replayButton);
        replayB.setOnTouchListener(this);

    }

    /**
     * onPause makes sure the sounds are stopped and threads are cleared so nothing is played in the background
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            soundsLoaded.clear();
        }
        clearThreads();
    }

    /**
     * onResume makes sure the sounds are reloaded for the game mode
     */
    @Override
    protected void onResume() {
        super.onResume();

        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setUsage(AudioAttributes.USAGE_GAME);

        final SoundPool.Builder spBuilder = new SoundPool.Builder();
        spBuilder.setAudioAttributes(attrBuilder.build());
        spBuilder.setMaxStreams(2);
        soundPool = spBuilder.build();
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundsLoaded.add(sampleId);
            }
        });
        gameMode();
    }

    /**
     * patternMode uses a set pattern for Simon to use
     */
    public int patternMode() {
        int index;
        if ((count * count) % 11 > 5) {
            index = (count * count * 3) % 4;
        } else {
            index = (count * count * count) % 3;
        }
        count++;
        return index;
    }

    /**
     * gameMode determines what game mode is chosen from the menu.
     */
    private void gameMode() {
        initiateVariables();

        setModeTextView("Pattern Mode");
        countDown = new PatternActivity.CountDown();
        countDown.execute();
    }

    /**
     * simonsTurn calls Simons turn
     */
    private void simonsTurn() {
        simonPattern.add(patternMode());
        simon = new PatternActivity.Simon();
        simon.execute();
    }

    /**
     * scoreUpdate updates the score.
     */
    private void scoreUpdate() {
        String scoreTextView;
        score = score + 1;
        if (score < 10) scoreTextView = "0" + score;
        else if(score >= 99){
            score = 99;
            scoreTextView = String.valueOf(score);
            gameOver();
        }else {scoreTextView = String.valueOf(score);}
            scoreTV.setText(scoreTextView);
    }

    /**
     * playerTurn checks the players input to make sure it matches Simons turn
     */
    private void playerTurn() {
        lockBtns = false;
        if (numChoices > 0) {
            if (numChoices < simonPattern.size()) {
                if (simonPattern.elementAt(numChoices - 1).equals(playerChoice)) {
                    match = true;
                } else {
                    match = false;
                    gameOver();
                }
            }else if (numChoices == simonPattern.size()) {
                lockBtns = true;
                if (simonPattern.elementAt(numChoices - 1).equals(playerChoice)) {
                    match = true;
                } else {
                    match = false;
                    gameOver();
                }
                if (match) {
                    numChoices = 0;
                    scoreUpdate();
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    simonsTurn();
                }
            }
        }
    }

    /**
     * gameOver ends the game and resets choices
     */
    private void gameOver() {
        checkHighScore();
        numChoices = 0;
        lockBtns = true;
        turnTV.setText("GAMEOVER!!");
        replayB.setVisibility(View.VISIBLE);
    }

    /**
     * checkHighScore compares the players score with the five highest previous scores
     * and adjusts the scores accordingly.
     */

    private void checkHighScore() {
        SharedPreferences prefs = this.getSharedPreferences("patternHighScores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (score >= prefs.getInt(highScore1key, 0)) {
            editor.putInt(highScore1key, score);
            editor.putInt(highScore2key, prefs.getInt(highScore1key, 0));
            editor.putInt(highScore3key, prefs.getInt(highScore2key, 0));
            editor.putInt(highScore4key, prefs.getInt(highScore3key, 0));
            editor.putInt(highScore5key, prefs.getInt(highScore4key, 0));
            editor.commit();
        } else if (score >= prefs.getInt(highScore2key, 0)) {
            editor.putInt(highScore2key, score);
            editor.putInt(highScore3key, prefs.getInt(highScore2key, 0));
            editor.putInt(highScore4key, prefs.getInt(highScore3key, 0));
            editor.putInt(highScore5key, prefs.getInt(highScore4key, 0));
            editor.commit();
        } else if (score >= prefs.getInt(highScore3key, 0)) {
            editor.putInt(highScore3key, score);
            editor.putInt(highScore4key, prefs.getInt(highScore3key, 0));
            editor.putInt(highScore5key, prefs.getInt(highScore4key, 0));
            editor.commit();
        } else if (score >= prefs.getInt(highScore4key, 0)) {
            editor.putInt(highScore4key, score);
            editor.putInt(highScore5key, prefs.getInt(highScore4key, 0));
            editor.commit();
        } else if (score > prefs.getInt(highScore5key, 0)) {
            editor.putInt(highScore5key, score);
            editor.commit();
        }
    }

    /**
     * Simon class plays the sounds and switches images during Simon's turn
     */
    class Simon extends AsyncTask<Void, Void, Void> {

        /**
         * doInBackground loads the sounds and images for Simon's pattern
         */
        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < simonPattern.size(); i++) {
                final int y = simonPattern.get(i);
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageButton flash = (ImageButton) findViewById(colorBtns[y]);
                            flash.setImageResource(pressedImg[y]);
                        }
                    });
                    if (soundsLoaded.contains(soundID[y])) {
                        soundPool.play(soundID[y], 1.0f, 1.0f, 0, 0, 1.0f);
                    }
                    if (tempo > 320) {
                        tempo -= 20;
                    }
                    Thread.sleep(tempo);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageButton flash = (ImageButton) findViewById(colorBtns[y]);
                            flash.setImageResource(colorImg[y]);
                        }
                    });
                } catch (InterruptedException e) {

                }
            }
            return null;
        }

        /**
         * onPostExecute calls the players turn after Simon's
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            setTurn("Your turn");
            playerTurn();
        }

        /**
         * onPreExecute sets the textView to Simon's turn
         */
        @Override
        protected void onPreExecute() {
            setTurn("Simon's Turn");
        }
    }

    /**
     * CountDown counts down to 0 to make sure the player is ready
     */
    class CountDown extends AsyncTask<Void, String, Void> {

        int cdSound = soundID[4];
        int time = 1000;

        /**
         * onPreExecute sets up the thread
         */
        @Override
        protected void onPreExecute() {
            try {
                Thread.sleep(1250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * doInBackground plays the countdown sounds
         */
        @Override
        protected Void doInBackground(Void... voids) {
            String textview;
            try {
                for (int i = 3; i >= 0; i--) {
                    if(isCancelled()) return null;
                    if (i == 0) {
                        textview = "Go";
                        cdSound = soundID[5];
                    } else textview = "0" + i;

                    publishProgress(textview);
                    Thread.sleep(time);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * onProgressUpdates plays the countdown sounds and updates the score textView
         */
        @Override
        protected void onProgressUpdate(String... values) {
            String display = values[0];
            scoreTV.setText(display);
            if (soundsLoaded.contains(cdSound)) {
                soundPool.play(cdSound, 1.0f, 1.0f, 0, 0, 1.0f);
            }
        }

        /**
         * onPostExecute is called when the game ends.
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            scoreTV.setText("00");
            simonsTurn();
        }
    }

    /**
     * clearThreads is called when the game ends and clears the threads.
     */
    public void clearThreads(){
        if(countDown != null){
            countDown.cancel(true);
            countDown = null;
        }
        if(simon != null){
            simon.cancel(true);
            simon = null;
        }
    }

    /**
     * playerPressed plays associated soundID and flashes pressed image
     */
    private void playerPressed(int id) {
        ImageButton imageButton = (ImageButton) findViewById(colorBtns[id]);
        imageButton.setImageResource(pressedImg[id]);
        if (soundsLoaded.contains(soundID[id])) {
            soundPool.play(soundID[id], 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    /**
     * playerReset resets the ImageButton and counts number of clicks
     */
    private void playerReset(int id) {
        ImageButton imageButton = (ImageButton) findViewById(colorBtns[id]);
        imageButton.setImageResource(colorImg[id]);
        playerChoice = id;
        numChoices++;
        playerTurn();
    }

    /**
     *  resetGame resets the game to allow the player to play again.
     */
    private void resetGame () {
        gameMode();
        replayB.setVisibility(View.GONE);
        lockBtns = false;
        turnTV.setText("");
    }

    private void setModeTextView(String s){
        modeTV.setText(s);
    }

    /**
     * setTurn changes the turn textview
     */
    private void setTurn(String s){
        turnTV.setText(s);
    }

    /**
     * onTouch monitors which button the player pressed
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (view.getId() == colorBtns[0] && !lockBtns) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                playerPressed(0);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                playerReset(0);
            }
        } else if (view.getId() == colorBtns[1] && !lockBtns) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                playerPressed(1);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                playerReset(1);
            }
        } else if (view.getId() == colorBtns[2] && !lockBtns) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                playerPressed(2);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                playerReset(2);
            }
        } else if (view.getId() == colorBtns[3] && !lockBtns) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                playerPressed(3);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                playerReset(3);
            }
        } else if (view.getId() == R.id.replayButton && lockBtns) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                replayB.setPressed(true);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                replayB.setPressed(false);
                resetGame();
            }
        }
        return false;
    }
}
