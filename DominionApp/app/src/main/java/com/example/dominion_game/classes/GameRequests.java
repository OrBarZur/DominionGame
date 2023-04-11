/**
 * GameRequests is a class that handles all game requests from the server.
 */
package com.example.dominion_game.classes;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dominion_game.activities.OnlineTablesActivity;
import com.example.dominion_game.activities.PrepareGameActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class GameRequests {

    public static String getServerIP() {
        return "192.168.1.182";
    }

    public static String getPort() {
        return "8888";
    }

    /**
     * A function that sends GET http request to create a table.
     * @param prepareGameActivity A reference to prepareGameActivity
     */
    public static void creator_start(final PrepareGameActivity prepareGameActivity)/* throws IOException*/ {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://" + getServerIP() + ":" + getPort() + "/creator_start",
                null,
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param response A JSONObject with the key success which is true or false
                     *                 and the unique key of the game
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                prepareGameActivity.getGameManagerBeforeStart().setGameId(response.getString("game_id"));
                                uploadGameManagerBeforeStart(prepareGameActivity);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(prepareGameActivity).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart to the server.
     * This function add the non creator player to the table.
     * @param prepareGameActivity A reference to prepareGameActivity
     */
    public static void non_creator_start(final PrepareGameActivity prepareGameActivity) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/non_creator_start",
                prepareGameActivity.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param response A JSONObject with the key success which is true or false
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (!response.getBoolean("success"))
                                prepareGameActivity.prepareToLeave();
                            else
                                GameRequests.waitForStartGame(false, prepareGameActivity, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(prepareGameActivity).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with my relevant data in game to the server
     * and gets the relevant data about the enemy.
     * @param gameManager A reference to gameManager
     */
    public static void uploadAndGetPlayerData(final GameManager gameManager) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/get_and_upload_player_data",
                gameManager.myDataToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param response A JSONObject with the key success which is true or false
                     *                 and the enemy data
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                gameManager.getEnemyData().jsonToEnemyData(response, gameManager);
                                gameManager.getGameActivity().updateCards(false);
                            }

                            if (gameManager.isGameEnded())
                                return;

                            if (gameManager.getEnemyData().isResigned()) {
                                gameManager.setGameEnded(true);
                                gameManager.getGameActivity().endGame();
                                endGame(gameManager);
                                return;
                            }

                            if (gameManager.isResigned()) {
                                gameManager.setGameEnded(true);
                                gameManager.getGameActivity().startPrepareActivity();
                            }
                            GameRequests.uploadAndGetPlayerData(gameManager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(gameManager.getGameActivity()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with the game data to the server.
     * When the game already started, it makes recursion calls while this is my turn
     * and uploads the game data real time.
     * @param isStart A Boolean which is true if the game already started and false if not
     * @param gameManager A reference to gameManager
     */
    public static void uploadDataInGame(final boolean isStart, final GameManager gameManager) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + (isStart ? "/upload_all_data" : "/upload_real_time"),
                isStart ? gameManager.gameManagerToJsonStart() : gameManager.gameManagerToJsonRealTime(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param response A JSONObject with the key success which is true or false
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (isStart) {
                                if (response.getBoolean("success")) {
                                    GameRequests.waitForStartGame(gameManager);
                                    GameRequests.uploadAndGetPlayerData(gameManager);
                                }
                            }
                            else {
                                if (!response.getBoolean("success")) {
                                    if (gameManager.isGameEnded())
                                        return;
                                    GameRequests.uploadDataInGame(false, gameManager);
                                }
                                if (response.has("turn")) {
                                    gameManager.jsonToGameManagerRealTime(response);
                                    if (response.getJSONObject("turn").getString("lastActionCardForWait").equals("")) {
                                        gameManager.getTurn().setWaitingForEnemy(false);
                                        gameManager.getGameActivity().turnUI();
                                        gameManager.endCard();
                                    }

                                    gameManager.getGameActivity().updateCards(true);
                                    GameRequests.uploadDataInGame(false, gameManager);
                                    return;
                                }
                                if (!gameManager.getTurn().isMyTurn(gameManager.getGameManagerBeforeStart())
                                        && response.getString("turnId").equals(gameManager.getTurn().getTurnId())) {
                                    gameManager.getGameActivity().endProgressBar(false);
                                    if (gameManager.isGameEnded())
                                        return;
                                    gameManager.getGameActivity().turnActions();
                                    GameRequests.getDataInGame(false, gameManager);
                                }
                                else {
                                    if (gameManager.isGameEnded())
                                        return;
                                    gameManager.getGameActivity().updateCards(false);
                                    GameRequests.uploadDataInGame(false, gameManager);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(gameManager.getGameActivity()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart to the server.
     * When the game already started, it makes recursion calls while this is not my turn
     * and gets the game data real time.
     * @param isStart A Boolean which is true if the game already started and false if not
     * @param gameManager A reference to gameManager
     */
    public static void getDataInGame(final boolean isStart, final GameManager gameManager)/* throws IOException*/ {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + (isStart ? "/get_all_data" : "/get_real_time"),
                gameManager.isPlaysAttack() ? gameManager.gameManagerToJsonRealTime() :
                        gameManager.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param response A JSONObject with the key success which is true or false
                     *                 and the game data
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (isStart) {
                                if (!response.getBoolean("success")) {
                                    if (gameManager.isGameEnded())
                                        return;
                                    GameRequests.getDataInGame(true, gameManager);
                                }
                                else {
                                    gameManager.jsonToGameManagerStart(response);
                                    gameManager.getGameActivity().beforeStart();
                                    GameRequests.waitForStartGame(gameManager);
                                    GameRequests.uploadAndGetPlayerData(gameManager);
                                }
                            }
                            else {
                                if (!response.getBoolean("success")) {
                                    if (gameManager.isGameEnded())
                                        return;
                                    GameRequests.getDataInGame(false, gameManager);
                                }
                                else {
                                    gameManager.jsonToGameManagerRealTime(response);

                                    if (!gameManager.getTurn().getLastActionCardForWait().equals("")) {
                                        gameManager.setPlaysAttack(true);
                                        Help.nameToCard(gameManager.getTurn().getLastActionCardForWait()).enemyPlay(gameManager, gameManager.getGameActivity());
                                        if (gameManager.getPlayer().containsCard("Moat"))
                                            Help.nameToCard("Moat").reaction(gameManager, gameManager.getGameActivity());
                                        else {
                                            Help.nameToCard(gameManager.getTurn().getLastActionCardForWait()).attack(gameManager, gameManager.getGameActivity());
                                            gameManager.getTurn().removeLastAttack();
                                        }
                                    }

                                    if (gameManager.getTurn().isMyTurn(gameManager.getGameManagerBeforeStart())) {
                                        gameManager.setGameEnded(response.getBoolean("isGameEnded"));
                                        gameManager.getGameActivity().endProgressBar(false);
                                        if (gameManager.isGameEnded()) {
                                            gameManager.getGameActivity().endGame();
                                            endGame(gameManager);
                                            return;
                                        }
                                        GameRequests.uploadDataInGame(false, gameManager);
                                        gameManager.getGameActivity().turnActions();
                                        gameManager.addTurnNumberToLog();
                                    }
                                    else {
                                        if (gameManager.isGameEnded())
                                            return;
                                        gameManager.getGameActivity().updateCards(false);
                                        GameRequests.getDataInGame(false, gameManager);
                                    }
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(gameManager.getGameActivity()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends GET http request to get all tables.
     * The function makes recursion calls while on onlineTablesActivity.
     * @param onlineTablesActivity A reference to onlineTablesActivity
     */
    public static void getTables(final OnlineTablesActivity onlineTablesActivity) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://" + getServerIP() + ":" + getPort() + "/get_tables",
                null,
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param jsonObject A JSONObject with the key success which is true or false
                     *                   and all tables with their ids
                     */
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (onlineTablesActivity.isFinished()) {
                            onlineTablesActivity.prepareToLeave();
                            return;
                        }
                        jsonObject.remove("success");
                        onlineTablesActivity.updateTables(jsonObject);
                        getTables(onlineTablesActivity);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestAdapter.getInstance(onlineTablesActivity).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart to the server.
     * @param prepareGameActivity A reference to prepareGameActivity
     */
    public static void uploadGameManagerBeforeStart(final PrepareGameActivity prepareGameActivity) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/upload_game_manager_before_start",
                prepareGameActivity.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param response A JSONObject with the key success which is true or false
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        prepareGameActivity.endProgressBar();
                        waitForPlayerToEnterTable(prepareGameActivity);
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(prepareGameActivity).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart to the server.
     * This function updates on server that the player is ready.
     * @param prepareGameActivity A reference to prepareGameActivity
     */
    public static void updateReady(final PrepareGameActivity prepareGameActivity) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/update_ready",
                prepareGameActivity.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param response A JSONObject with the key success which is true or false
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(!response.getBoolean("success"))
                                prepareGameActivity.prepareToLeave();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(prepareGameActivity).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart to the server.
     * This function updates on server that the player is ready to start.
     * @param gameManager A reference to gameManager
     */
    public static void updateReadyToStart(final GameManager gameManager) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/update_ready_to_start",
                gameManager.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(gameManager.getGameActivity()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart to the server.
     * The function makes recursion calls while a second player didn't enter the table.
     * @param prepareGameActivity A reference to prepareGameActivity
     */
    public static void waitForPlayerToEnterTable(final PrepareGameActivity prepareGameActivity) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/get_game_manager_before_start",
                prepareGameActivity.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param jsonObject A JSONObject with the key success which is true or false
                     *                   and with gameManagerBeforeStart updated
                     */
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (!jsonObject.getBoolean("success"))
                                prepareGameActivity.leaveTable();
                            else {
                                prepareGameActivity.getGameManagerBeforeStart().jsonToGameManagerBeforeStart(jsonObject, false);
                                prepareGameActivity.updateUI(true);
                                if (prepareGameActivity.getGameManagerBeforeStart().getIdP2().equals(""))
                                    waitForPlayerToEnterTable(prepareGameActivity);
                                else {
                                    waitForStartGame(true, prepareGameActivity, 0);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestAdapter.getInstance(prepareGameActivity).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart to the server.
     * The function makes recursion calls while there is a player that is not ready to start.
     * @param gameManager A reference to gameManager
     */
    public static void waitForStartGame(final GameManager gameManager) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/get_game_manager_before_start",
                gameManager.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param jsonObject A JSONObject with the key success which is true or false
                     *                   and with gameManagerBeforeStart updated
                     */
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        gameManager.getGameManagerBeforeStart().jsonToGameManagerBeforeStart(jsonObject, true);
                        if (!gameManager.getEnemyData().isEmpty()) {
                            gameManager.getGameActivity().endProgressBar(true);
                        }
                        if (gameManager.getGameManagerBeforeStart().isStart1() &&
                                gameManager.getGameManagerBeforeStart().isStart2()) {
                            gameManager.getGameActivity().startGame();
                        }
                        else
                            GameRequests.waitForStartGame(gameManager);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestAdapter.getInstance(gameManager.getGameActivity()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart to the server.
     * The function makes recursion calls while there is a player that is not ready.
     * @param isCreator A Boolean which is true if the player is the creator and false if not
     * @param prepareGameActivity A reference to prepareGameActivity
     * @param countCheck An Integer that ensures that both players are ready and count
     *                   to 5 until the game starts
     */
    public static void waitForStartGame(final boolean isCreator, final PrepareGameActivity prepareGameActivity, final int countCheck) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/get_game_manager_before_start",
                prepareGameActivity.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param jsonObject A JSONObject with the key success which is true or false
                     *                   and with gameManagerBeforeStart updated
                     */
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (!jsonObject.getBoolean("success"))
                                prepareGameActivity.prepareToLeave();
                            else {
                                prepareGameActivity.endProgressBar();
                                prepareGameActivity.getGameManagerBeforeStart().jsonToGameManagerBeforeStart(jsonObject, false);
                                prepareGameActivity.updateUI(false);
                                if (isCreator && jsonObject.getString("idP2").equals(""))
                                    waitForPlayerToEnterTable(prepareGameActivity);
                                else {
                                    if (!prepareGameActivity.getGameManagerBeforeStart().getIdP1().equals("") &&
                                            !prepareGameActivity.getGameManagerBeforeStart().getIdP2().equals("") &&
                                            prepareGameActivity.getGameManagerBeforeStart().isReady1() &&
                                            prepareGameActivity.getGameManagerBeforeStart().isReady2()) // checks if both players are ready
                                        if (countCheck == 5)
                                            prepareGameActivity.startGame();
                                        else if (countCheck < 5)
                                            waitForStartGame(isCreator, prepareGameActivity, countCheck + 1);
                                        else
                                            Log.d("error", String.valueOf(countCheck));
                                    else
                                        waitForStartGame(isCreator, prepareGameActivity, 0);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestAdapter.getInstance(prepareGameActivity).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart
     * to the server and deletes the table.
     * This function is for the creator of the game.
     * @param prepareGameActivity A reference to prepareGameActivity
     */
    public static void delete_game_manager_before_start(final PrepareGameActivity prepareGameActivity) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/delete_game_manager_before_start",
                prepareGameActivity.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param response A JSONObject with the key success which is true or false
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                prepareGameActivity.endProgressBar();
                                prepareGameActivity.leaveTable();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(prepareGameActivity).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart
     * to the server and deletes the player from the table.
     * This function is for the non creator of the game.
     * @param prepareGameActivity A reference to prepareGameActivity
     */
    public static void deleteP2(final PrepareGameActivity prepareGameActivity) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/delete_p2",
                prepareGameActivity.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param response A JSONObject with the key success which is true or false
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")) {
                                prepareGameActivity.endProgressBar();
                                prepareGameActivity.leaveTable();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(prepareGameActivity).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * A function that sends POST http request with gameManagerBeforeStart to the server.
     * @param gameManager A reference to gameManager
     */
    public static void endGame(final GameManager gameManager) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://" + getServerIP() + ":" + getPort() + "/end_game",
                gameManager.getGameManagerBeforeStart().gameManagerBeforeStartToJson(),
                new Response.Listener<JSONObject>() {
                    /**
                     * A function that handles the response from the server for the request.
                     * @param response A JSONObject with the key success which is true or false
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")) {
                                gameManager.getGameActivity().startPrepareActivity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestAdapter.getInstance(gameManager.getGameActivity()).addToRequestQueue(jsonObjectRequest);
    }
}