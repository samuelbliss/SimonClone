package com.samuelbliss.simonclone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.samuelbliss.simonclone.GameActivity;
import com.samuelbliss.simonclone.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.play_button);
        button.setOnClickListener(this);

        Button button1 = (Button) findViewById(R.id.about_button);
        button1.setOnClickListener(this);

        Button button2 = (Button) findViewById(R.id.scores_button);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.play_button) {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.about_button) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.scores_button) {
            Intent intent = new Intent(this, ScoresActivity.class);
            startActivity(intent);
        }
    }
}