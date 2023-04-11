/**
 * OnlineTablesActivity is the activity when the user
 * see a list view of all online tables that he can join and play.
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
import android.widget.ImageView;
import android.widget.ListView;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.GameRequests;
import com.example.dominion_game.classes.MusicService;
import com.example.dominion_game.classes.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OnlineTablesActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnBack;
    ListView lv;
    ArrayList<Table> tableList;
    TablesAdapter tablesAdapter;
    ProgressDialog progressDialog;
    ImageView background;
    boolean isFinished;
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
        setContentView(R.layout.activity_online_tables);
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        sound = findViewById(R.id.sound);
        if (sharedPreferences.getString("sound", "").equals("on"))
            sound.setImageResource(R.mipmap.sound_on);
        else
            sound.setImageResource(R.mipmap.sound_off);
        sound.setOnClickListener(this);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnBack.setClickable(true);

        background = findViewById(R.id.background);
        tableList = new ArrayList<>();
        tableList.add(new Table("Tables", "")); // this item is the title
        background.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Tables");
        progressDialog.setCancelable(false);
        progressDialog.show();
        lv = findViewById(R.id.lvTables);
        isFinished = false;
        uploadTables();
        GameRequests.getTables(this);
        endProgressBar();

    }

    /**
     * A function which handles back press to set isFinished to true
     * and return to OnlineGameActivity.
     */
    @Override
    public void onBackPressed() {
        isFinished = true;
        btnBack.setClickable(false);
    }

    /**
     * A function that handles all button presses.
     * @param view A View which is the view that was pressed
     */
    @Override
    public void onClick(View view) {
        if (view == sound) {
            if (sharedPreferences.getString("sound", "").equals("on")) {
                stopService(new Intent(this, MusicService.class));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sound", "off");
                editor.apply();
                sound.setImageResource(R.mipmap.sound_off);
            }
            else if (sharedPreferences.getString("sound", "").equals("off")) {
                startService(new Intent(this, MusicService.class));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sound", "on");
                editor.apply();
                sound.setImageResource(R.mipmap.sound_on);
            }
        }
        else if (btnBack == view) {
            isFinished = true;
            btnBack.setClickable(false);
        }
    }

    /**
     * A function that finishes the intent and returns to OnlineGameActivity
     */
    public void prepareToLeave() {
        finish();
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    /**
     * A function that creates the tables adapter and sets this as the ListView adapter
     */
    public void uploadTables() {
        tablesAdapter = new TablesAdapter(this,0,0, tableList, this);
        lv.setAdapter(tablesAdapter);
    }

    /**
     * A function that updates the tables on real time.
     * @param response A JSONObject that is given from the server and keeps all tables online
     */
    public void updateTables(JSONObject response) {
        while (tableList.size() > 1)
            tableList.remove(1);
        JSONArray keys = response.names();
        if (keys != null)
            for (int i = 0; i < keys.length(); i++) {
                try {
                    String creator = response.getJSONObject(keys.getString(i)).getString("idP1");
                    tableList.add(new Table(creator, keys.getString(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        tablesAdapter.notifyDataSetChanged();
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
