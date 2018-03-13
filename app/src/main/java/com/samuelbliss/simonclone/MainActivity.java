package com.samuelbliss.simonclone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.pattern_button);
        button.setOnClickListener(this);

        Button button1 = (Button) findViewById(R.id.about_button);
        button1.setOnClickListener(this);

        Button button2 = (Button) findViewById(R.id.scores_button);
        button2.setOnClickListener(this);

        Button button3 = (Button) findViewById(R.id.random_button);
        button3.setOnClickListener(this);

        Button button4 = (Button) findViewById(R.id.reverse_button);
        button4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.pattern_button) {
            Intent intent = new Intent(this, PatternActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.about_button) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.scores_button) {
            Intent intent = new Intent(this, ScoresActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.random_button) {
            Intent intent = new Intent(this, RandomActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.reverse_button) {
            Intent intent = new Intent(this, ReverseActivity.class);
            startActivity(intent);
        }
    }
}