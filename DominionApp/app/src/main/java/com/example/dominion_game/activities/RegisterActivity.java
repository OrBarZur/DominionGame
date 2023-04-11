/**
 * RegisterActivity is the activity of the register which handles
 * register to the application.
 */
package com.example.dominion_game.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.LoginRequests;
import com.example.dominion_game.classes.MusicService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUserName, etEmail, etPassword, etRepeatPassword;
    Button btnRegister, btnBack;
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
        setContentView(R.layout.activity_register);
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        sound = findViewById(R.id.sound);
        if (sharedPreferences.getString("sound", "").equals("on"))
            sound.setImageResource(R.mipmap.sound_on);
        else
            sound.setImageResource(R.mipmap.sound_off);
        etUserName = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etRepeatPassword = findViewById(R.id.repeat_password);

        progressDialog = new ProgressDialog(this);

        btnRegister = findViewById(R.id.register);
        btnBack = findViewById(R.id.back);

        btnRegister.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        sound.setOnClickListener(this);
    }

    /**
     * A function which handles back press to return to LoginActivity.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
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
        else if (view == btnRegister) {
            // checks if register is valid in some parameters and if valid, register in the server
            if (etUserName.getText().length() == 0 || etEmail.getText().length() == 0
                    || etPassword.getText().length() == 0 || etRepeatPassword.getText().length() == 0) {
                Toast.makeText(this, "One or more of the fields are empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etUserName.getText().length() > 10) {
                Toast.makeText(this, "Username must be maximum 10 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isEmailValid()) {
                Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!etPassword.getText().toString().equals(etRepeatPassword.getText().toString())) {
                Toast.makeText(this, "Repeated password is different", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.setCancelable(false);
            progressDialog.show();
            LoginRequests.register(this, registerDetailsToJson());
        }
        else if (view == btnBack) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    /**
     * A function that creates a JSONObject from the data that the user entered.
     * @return A JSONObject of the register details
     */
    public JSONObject registerDetailsToJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", etUserName.getText().toString());
            jsonObject.put("email", etEmail.getText().toString());
            jsonObject.put("password", etPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * A function that checks if the email entered is valid.
     * @return A Boolean which is true if the email is valid and false if not
     */
    public boolean isEmailValid()
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(etEmail.getText().toString()).matches();
    }

    /**
     * A function that is called when the register happened.
     * The function finishes the activity by returning to LoginActivity.
     * @param username A String with the username of the user
     */
    public void registerSuccess(String username) {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra("username", username);
        finish();
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
