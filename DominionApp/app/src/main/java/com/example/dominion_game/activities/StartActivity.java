/**
 * StartActivity is the main activity when the user can choose what to do.
 */
package com.example.dominion_game.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.MusicService;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnOnlineGame, btnSettings;
    SharedPreferences sharedPreferences;
    ImageView sound;

    /**
     * A function that is called at the start of the activity
     * and handles all references.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        sound = findViewById(R.id.sound);
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("sound", "").equals("on"))
            sound.setImageResource(R.mipmap.sound_on);
        else
            sound.setImageResource(R.mipmap.sound_off);

        btnOnlineGame = findViewById(R.id.btnOnlineGame);
        btnSettings = findViewById(R.id.btnSettings);
        btnOnlineGame.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        sound.setOnClickListener(this);
    }

    /**
     * A function which handles back press to do nothing.
     */
    @Override
    public void onBackPressed() {

    }

    /**
     * A function that handles all button presses.
     * @param view A View which is the view that was pressed
     */
    @Override
    public void onClick(View view) {
        if (view == sound) {
            if (sharedPreferences.getString("sound", "").equals("on")) {
                stopService(new Intent(this, MusicService.class)); // stops music
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sound", "off");
                editor.apply();
                sound.setImageResource(R.mipmap.sound_off);
            }
            else if (sharedPreferences.getString("sound", "").equals("off")) {
                startService(new Intent(this, MusicService.class)); // plays music
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sound", "on");
                editor.apply();
                sound.setImageResource(R.mipmap.sound_on);
            }
        }
        else if (view == btnOnlineGame) {
            Intent intent = new Intent(this, OnlineGameActivity.class);
            startActivity(intent);
        }
        else if (view == btnSettings) {
            // logs out and delete the data in the sharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.remove("staySignedIn");
            editor.apply();

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
