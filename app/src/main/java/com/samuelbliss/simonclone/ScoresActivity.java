package com.samuelbliss.simonclone;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class ScoresActivity extends Activity{

    public String highScore1key = "key1", highScore2key = "key2",
            highScore3key = "key3", highScore4key = "key4", highScore5key = "key5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        TextView tv = (TextView) findViewById(R.id.hsView);
        SharedPreferences prefs = this.getSharedPreferences("highScores", Context.MODE_PRIVATE);
        tv.setText(Integer.toString(prefs.getInt(highScore1key, 0)));
        tv = (TextView) findViewById(R.id.hsView2);
        tv.setText(Integer.toString(prefs.getInt(highScore2key, 0)));
        tv = (TextView) findViewById(R.id.hsView3);
        tv.setText(Integer.toString(prefs.getInt(highScore3key, 0)));
        tv = (TextView) findViewById(R.id.hsView4);
        tv.setText(Integer.toString(prefs.getInt(highScore4key, 0)));
        tv = (TextView) findViewById(R.id.hsView5);
        tv.setText(Integer.toString(prefs.getInt(highScore5key, 0)));

    }
}
