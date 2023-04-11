/**
 * LoginActivity is the activity of the login which handles
 * login to the application.
 */
package com.example.dominion_game.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.LoginRequests;
import com.example.dominion_game.classes.MusicService;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUserName, etPassword;
    Button btnLogin, btnRegister;
    CheckBox staySignedIn;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    ImageView sound;

    /**
     * A function that is called at the start of the activity
     * and handles all references.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sound = findViewById(R.id.sound);

        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("sound", "").equals("on") || sharedPreferences.getString("sound", "").equals("")) {
            startService(new Intent(this, MusicService.class)); // plays music
            if (sharedPreferences.getString("sound", "").equals("")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sound", "on");
                editor.apply();
            }
            sound.setImageResource(R.mipmap.sound_on);
        }
        else
            sound.setImageResource(R.mipmap.sound_off);

        if (sharedPreferences.getBoolean("staySignedIn", false) && sharedPreferences.getString("username", null) != null) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivityForResult(intent, 1);
        }

        progressDialog = new ProgressDialog(this);

        etUserName = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        etPassword.setText("");

        String username = sharedPreferences.getString("username", null);
        if (username != null)
            etUserName.setText(username);
        else
            etUserName.setText("");

        staySignedIn = findViewById(R.id.staySignedIn);

        btnLogin = findViewById(R.id.login);
        btnRegister = findViewById(R.id.register);
        btnLogin.setClickable(true);
        btnRegister.setClickable(true);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
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
        else if (view == btnLogin) {
            if (etUserName.getText().length() == 0 || etPassword.getText().length() == 0) {
                Toast.makeText(this, "One or more of the fields are empty", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.setCancelable(false);
            progressDialog.show();
            LoginRequests.login(this, loginDetailsToJson());
        }
        else if (view == btnRegister) {
            btnRegister.setClickable(false);
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    /**
     * A function which is called when the user logged out.
     * @param requestCode
     * @param resultCode An Integer which is the resultCode of the intent
     *                   - RESULT_OK or RESULT_CANCELED
     * @param intent An Intent to get the extras
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            btnRegister.setClickable(true);
            etUserName.setText(intent.getExtras().getString("username"));
            etPassword.setText("");
        }
        else if (requestCode == 0 && resultCode == RESULT_CANCELED) {
            btnRegister.setClickable(true);
            etUserName.setText("");
            etPassword.setText("");
        }

        else if (requestCode == 1 && resultCode == RESULT_OK) {
            btnLogin.setClickable(true);
            etUserName.setText("");
            etPassword.setText("");
            staySignedIn.setChecked(false);
        }
    }

    /**
     * A function that creates a JSONObject from the data that the user entered.
     * @return A JSONObject of the login details
     */
    public JSONObject loginDetailsToJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", etUserName.getText().toString());
            jsonObject.put("password", etPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * A function that is called when the username and the password are correct.
     * The function saves them to the shared preferences and starts an intent to StartActivity.
     * @param username A String with the username of the user
     */
    public void loginSuccess(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putBoolean("staySignedIn", staySignedIn.isChecked());
        editor.apply();

        Intent intent = new Intent(this, StartActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * A function that ends the progress bar.
     */
    public void endProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
