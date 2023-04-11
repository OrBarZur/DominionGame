/**
 * OnlineGameActivity is the activity when the user can choose
 * between creating a table and joining a table.
 */
package com.example.dominion_game.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.GameManagerBeforeStart;
import com.example.dominion_game.classes.MusicService;

public class OnlineGameActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnTables, btnCreateTable;
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
        setContentView(R.layout.activity_online_game);
        sound = findViewById(R.id.sound);
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("sound", "").equals("on"))
            sound.setImageResource(R.mipmap.sound_on);
        else
            sound.setImageResource(R.mipmap.sound_off);
        btnTables = findViewById(R.id.btnTables);
        btnCreateTable = findViewById(R.id.btnCreateTable);
        btnTables.setOnClickListener(this);
        btnCreateTable.setOnClickListener(this);
        btnTables.setClickable(true);
        btnCreateTable.setClickable(true);
        sound.setOnClickListener(this);
    }

    /**
     * A function which handles back press to return to StartActivity.
     */
    @Override
    public void onBackPressed() {
        finish();
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
        else if (btnTables == view)
        {
            btnTables.setClickable(false);
            Intent intent = new Intent(this, OnlineTablesActivity.class);
            startActivity(intent);
            btnTables.setClickable(true);
        }
        else if (btnCreateTable == view)
        {
            btnCreateTable.setClickable(false);
            Intent intent = new Intent(this, PrepareGameActivity.class);
            intent.putExtra("gameManagerBeforeStart", new GameManagerBeforeStart(sharedPreferences.getString("username", "")));
            this.startActivityForResult(intent, 0);
        }
    }

    /**
     * A function which is called when the user returned from
     * OnlineTablesActivity or from PrepareGameActivity.
     * @param requestCode
     * @param resultCode An Integer which is the resultCode of the intent
     *                   - RESULT_OK or RESULT_CANCELED
     * @param intent An Intent to get the extras
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0 && resultCode == RESULT_OK)
            btnCreateTable.setClickable(true);
    }
}
