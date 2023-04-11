/**
 * LoginRequests is a class that handles all login and register requests from the server.
 */
package com.example.dominion_game.classes;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dominion_game.activities.LoginActivity;
import com.example.dominion_game.activities.RegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginRequests {
    public static String getServerIP() {
        return "192.168.1.182";
    }

    public static String getPort() {
        return "8888";
    }

    /**
     * A function that sends POST http request with registerRequest to the server.
     * @param registerActivity A reference to registerActivity
     * @param registerRequest A JSONObject of the register details
     */
    public static void register(final RegisterActivity registerActivity, JSONObject registerRequest) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/register",
                registerRequest,
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the registerRequest.
                     * @param response A JSONObject with the key success which is true or false
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        registerActivity.endProgressBar();
                        try {
                            if (!response.getBoolean("success"))
                                Toast.makeText(registerActivity, "There is already a user with this name", Toast.LENGTH_SHORT).show();
                            else
                                registerActivity.registerSuccess(response.getString("username"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(registerActivity).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with loginRequest to the server.
     * @param loginActivity A reference to loginActivity
     * @param loginRequest A JSONObject of the login details
     */
    public static void login(final LoginActivity loginActivity, JSONObject loginRequest) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/login",
                loginRequest,
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the loginRequest.
                     * @param response A JSONObject with the key success which is true or false
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        loginActivity.endProgressBar();
                        try {
                            if (!response.getBoolean("success"))
                                Toast.makeText(loginActivity, "Wrong username or password", Toast.LENGTH_SHORT).show();
                            else
                                loginActivity.loginSuccess(response.getString("username"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(loginActivity).addToRequestQueue(jsonObjectRequest);
    }
}
