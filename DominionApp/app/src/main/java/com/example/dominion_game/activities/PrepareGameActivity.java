/**
 * PrepareGameActivity is the activity before starting the game which handles
 * the players connected to the game. The creator of the game have also options
 * for the game (for example, if the game is rated or not), and it is also the
 * activity of the end of the game when the winner of the game is shown.
 */
package com.example.dominion_game.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.GameManagerBeforeStart;
import com.example.dominion_game.classes.GameRequests;
import com.example.dominion_game.classes.Help;
import com.example.dominion_game.classes.MusicService;

public class PrepareGameActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    GameManagerBeforeStart gameManagerBeforeStart;
    Button btnReady, btnLeaveTable;
    TextView tvP1, tvP2;
    TextView tvIsRated;
    TextView tvWinner, tvP1VP, tvP2VP;
    Switch sw;
    ImageView background;
    ProgressDialog progressDialog;
    ImageView sound;
    SharedPreferences sharedPreferences;

    /**
     * A function that is called at the start of the activity
     * and handles all references.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_game);
        sound = findViewById(R.id.sound);
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("sound", "").equals("on"))
            sound.setImageResource(R.mipmap.sound_on);
        else
            sound.setImageResource(R.mipmap.sound_off);
        tvIsRated = findViewById(R.id.tvIsRated);
        sw = findViewById(R.id.sw);
        btnReady = findViewById(R.id.btnReady);
        btnReady.setVisibility(View.VISIBLE);
        btnLeaveTable = findViewById(R.id.btnLeaveTable);
        tvP1 = findViewById(R.id.tvP1);
        tvP2 = findViewById(R.id.tvP2);
        tvP1.setTextColor(getResources().getColor(R.color.red));
        tvP2.setTextColor(getResources().getColor(R.color.red));

        tvWinner = findViewById(R.id.tvWinner);
        tvP1VP = findViewById(R.id.tvP1VP);
        tvP2VP = findViewById(R.id.tvP2VP);

        background = findViewById(R.id.background);
        Intent intent = getIntent();
        gameManagerBeforeStart = (GameManagerBeforeStart)intent.getExtras().getSerializable("gameManagerBeforeStart");
        tvP1.setText(String.valueOf(gameManagerBeforeStart.getIdP1()));
        tvP2.setText(String.valueOf(gameManagerBeforeStart.getIdP2()));
        tvWinner.setVisibility(View.INVISIBLE);
        tvP1VP.setVisibility(View.INVISIBLE);
        tvP2VP.setVisibility(View.INVISIBLE);
        if (gameManagerBeforeStart.isCreator()) {
            tvIsRated.setVisibility(View.VISIBLE);
            sw.setVisibility(View.VISIBLE);
            sw.setOnCheckedChangeListener(this);
            background.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Creating Table");
            progressDialog.setCancelable(false);
            progressDialog.show();
            GameRequests.creator_start(this);
        }
        else {
            tvIsRated.setVisibility(View.GONE);
            sw.setVisibility(View.GONE);
            background.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Joining Table");
            progressDialog.setCancelable(false);
            progressDialog.show();
            GameRequests.non_creator_start(this);
        }

        btnReady.setOnClickListener(this);
        btnLeaveTable.setOnClickListener(this);
        btnLeaveTable.setClickable(true);
        sound.setOnClickListener(this);
    }

    /**
     * A function which handles back press to leave the table.
     */
    @Override
    public void onBackPressed() {
        btnLeaveTable.setClickable(false);
        prepareToLeave();
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
        else if (btnReady == view)
        {
            boolean setTo = true;
            if (btnReady.getText().equals("Ready"))
                btnReady.setText("Not Ready");
            else if (btnReady.getText().equals("Not Ready")) {
                setTo = false;
                btnReady.setText("Ready");
            }
            if (this.gameManagerBeforeStart.isCreator())
                this.gameManagerBeforeStart.setReady1(setTo);
            else
                this.gameManagerBeforeStart.setReady2(setTo);
            updateUI(true);
            GameRequests.updateReady(this);
        }
        else if (btnLeaveTable == view) {
            btnLeaveTable.setClickable(false);
            prepareToLeave();
        }
    }

    /**
     * A function that updates on screen the data from gameManagerBeforeStart.
     * @param onlyMe A Boolean which is true if the function should update if the other player
     *               is ready or only for the player
     */
    public void updateUI(boolean onlyMe) {
        tvP1.setText(String.valueOf(gameManagerBeforeStart.getIdP1()));
        tvP2.setText(String.valueOf(gameManagerBeforeStart.getIdP2()));
        if (this.gameManagerBeforeStart.isCreator() || !onlyMe) {
            if (this.gameManagerBeforeStart.isReady1())
                this.tvP1.setTextColor(getResources().getColor(R.color.green));
            else
                this.tvP1.setTextColor(getResources().getColor(R.color.red));
        }

        if (!this.gameManagerBeforeStart.isCreator() || !onlyMe) {
            if (this.gameManagerBeforeStart.isReady2())
                this.tvP2.setTextColor(getResources().getColor(R.color.green));
            else
                this.tvP2.setTextColor(getResources().getColor(R.color.red));
        }

        if (this.gameManagerBeforeStart.isReady1() && this.gameManagerBeforeStart.isReady2())
            btnReady.setVisibility(View.INVISIBLE);
        else
            btnReady.setVisibility(View.VISIBLE);
    }

    public GameManagerBeforeStart getGameManagerBeforeStart() {
        return this.gameManagerBeforeStart;
    }

    /**
     * A function that finishes the intent and returns to OnlineGameActivity
     */
    public void leaveTable() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * A function that deletes the table if the player is the creator
     * and delete the player from the table if the player is not the creator.
     */
    public void prepareToLeave() {
        if(gameManagerBeforeStart.isCreator()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Deleting Table");
            progressDialog.setCancelable(false);
            progressDialog.show();
            GameRequests.delete_game_manager_before_start(this);
        }
        else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Exit Table");
            progressDialog.setCancelable(false);
            progressDialog.show();
            GameRequests.deleteP2(this);
        }
    }

    /**
     * A function that starts the game by starting GameActivity.
     */
    public void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameManagerBeforeStart", gameManagerBeforeStart);
        startActivityForResult(intent, 0);
    }

    /**
     * A function which is called when the game is ended and updates the result of the game.
     * @param requestCode
     * @param resultCode An Integer which is the resultCode of the intent
     *                   - RESULT_OK or RESULT_CANCELED
     * @param intent An Intent to get the extras
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == 0 && resultCode == RESULT_OK) {
            btnLeaveTable.setClickable(true);
            btnReady.setVisibility(View.VISIBLE);
            tvWinner.setVisibility(View.VISIBLE);

            btnReady.setText("Ready");
            if (intent.getExtras().getString("result").equals("resign")) {
                tvP1VP.setVisibility(View.INVISIBLE);
                tvP2VP.setVisibility(View.INVISIBLE);
                tvWinner.setText(intent.getExtras().getString("player").concat(" resigned"));
            }
            else if (intent.getExtras().getString("result").equals("win") || intent.getExtras().getString("result").equals("draw")) {
                tvP1VP.setVisibility(View.VISIBLE);
                tvP2VP.setVisibility(View.VISIBLE);
                tvP1VP.setText(gameManagerBeforeStart.getIdP1() + ": " + intent.getExtras().getInt("P1VP"));
                tvP2VP.setText(gameManagerBeforeStart.getIdP2() + ": " + intent.getExtras().getInt("P2VP"));
                if (intent.getExtras().getString("result").equals("win"))
                    tvWinner.setText("The winner is ".concat(intent.getExtras().getString("player")));
                else if (intent.getExtras().getString("result").equals("draw"))
                    tvWinner.setText("Draw");
            }

            gameManagerBeforeStart.restartReady();
            updateUI(false);
            GameRequests.waitForStartGame(gameManagerBeforeStart.isCreator(), this, 0);
        }
    }

    /**
     * A function that is called when the switch has changed
     * and updates it on gameManagerBeforeStart.
     * @param compoundButton
     * @param isRated A Boolean which is true if the state is checked and false if not
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isRated) {
        gameManagerBeforeStart.setRated(isRated);
    }

    /**
     * A function that ends the progress bar.
     */
    public void endProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            background.setVisibility(View.INVISIBLE);
        }
    }
}
