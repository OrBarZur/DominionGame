/**
 * GameActivity is the activity of the game which handles
 * all game plays and clicks until the end.
 */
package com.example.dominion_game.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.*;

import java.util.ArrayList;
import java.util.HashMap;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    GameManager game;
    Button btnStart, btnEnd, btnAutoplay;
    ArrayList<Button> btnActions;
    TextView tvTurn;
    ConstraintLayout clTurn;
    HashMap<String, View> victoryCards;
    HashMap<String, View> treasureCards;
    HashMap<String, View> actionCards;
    HashMap<String, View> actionBigCards;
    Dialog cardDialog; // The dialog which is shown in long click to show the card bigger
    View myDeck, enemyHand, enemyDeck;
    ImageView ivEnemyDiscard, ivMyDiscard;
    TextView tvMyVP, tvEnemyVP, tvMyName, tvEnemyName;

    ListView lvLog;
    LogAdapter logAdapter;
    androidx.recyclerview.widget.RecyclerView rvTrash;
    TrashAdapter trashAdapter;
    TextView tvInfoTitle;

    androidx.recyclerview.widget.RecyclerView rvHand;
    HandAdapter handAdapter;

    androidx.recyclerview.widget.RecyclerView rvActionCardsPlaying;
    ActionCardPlayingAdapter actionCardsPlayingAdapter;
    ConstraintLayout clActionCardsPlaying;
    ImageView ivArrowActionCardsPlaying;
    String positionOfRVActionCardsPlaying;

    ImageView background;
    ProgressDialog progressDialog;

    Button btnKingdomOrPlayArea, btnTrashOrLog, btnResign;
    ConstraintLayout clPlayArea, clKingdom;
    RelativeLayout rlButtonsInPlay;

    PhoneCallReceiver phoneCallReceiver; // broadcast receiver

    /**
     * A function that is called at the start of the activity
     * and handles all references and creates the gameManager.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_designed);
        phoneCallReceiver = new PhoneCallReceiver();
        this.checkAndRequestPermissions();

        btnStart = findViewById(R.id.btnStart);
        btnEnd = findViewById(R.id.btnEnd);
        btnAutoplay = findViewById(R.id.btnAutoplay);

        btnActions = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            int resID = getResources().getIdentifier(("btnAction" + (i+1)), "id", getPackageName());
            btnActions.add((Button)findViewById(resID));
            btnActions.get(i).setVisibility(View.GONE);
        }

        tvTurn = findViewById(R.id.tvTurn);
        clTurn = findViewById(R.id.clTurn);

        victoryCards = new HashMap<>();
        treasureCards = new HashMap<>();
        actionCards = new HashMap<>();
        actionBigCards = new HashMap<>();

        // Creates the card dialog to be cancelable and with no title
        cardDialog = new Dialog(this);
        cardDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cardDialog.setContentView(R.layout.card_dialog);
        cardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cardDialog.getWindow().setLayout((int)(getResources().getDisplayMetrics().heightPixels*0.6), (int)(getResources().getDisplayMetrics().heightPixels*0.9));
        cardDialog.setCancelable(true);

        ivMyDiscard = findViewById(R.id.ivMyDiscard);
        myDeck = findViewById(R.id.myDeck);
        enemyHand = findViewById(R.id.enemyHand);
        enemyDeck = findViewById(R.id.enemyDeck);
        ivEnemyDiscard = findViewById(R.id.ivEnemyDiscard);
        tvMyName = findViewById(R.id.tvMyName);
        tvMyVP = findViewById(R.id.tvMyVP);
        tvEnemyName = findViewById(R.id.tvEnemyName);
        tvEnemyVP = findViewById(R.id.tvEnemyVP);

        enemyHand.setVisibility(View.VISIBLE);
        tvMyName.setVisibility(View.VISIBLE);
        tvEnemyName.setVisibility(View.VISIBLE);
        myDeck.setVisibility(View.INVISIBLE);
        enemyDeck.setVisibility(View.INVISIBLE);
        ivEnemyDiscard.setVisibility(View.INVISIBLE);
        ivMyDiscard.setVisibility(View.INVISIBLE);
        tvMyVP.setVisibility(View.INVISIBLE);
        tvEnemyVP.setVisibility(View.INVISIBLE);
        tvTurn.setVisibility(View.INVISIBLE);
        clTurn.setVisibility(View.INVISIBLE);

        lvLog = findViewById(R.id.lvLog);
        rvTrash = findViewById(R.id.rvTrash);
        rvHand = findViewById(R.id.rvHand);
        rvActionCardsPlaying = findViewById(R.id.rvActionCardsPlaying);
        clActionCardsPlaying = findViewById(R.id.clActionCardsPlaying);
        clActionCardsPlaying.setVisibility(View.INVISIBLE);
        ivArrowActionCardsPlaying = findViewById(R.id.ivArrowActionCardsPlaying);
        this.positionOfRVActionCardsPlaying = "up";
        ivArrowActionCardsPlaying.setOnClickListener(this);
        tvInfoTitle = findViewById(R.id.tvInfoTitle);

        // Gets the GameManagerBeforeStart from the PrepareGameActivity and creates the gameManager
        Intent intent = getIntent();
        GameManagerBeforeStart gameManagerBeforeStart = (GameManagerBeforeStart)intent.getExtras().getSerializable("gameManagerBeforeStart");
        game = new GameManager(gameManagerBeforeStart, this);

        btnKingdomOrPlayArea = findViewById(R.id.btnKingdomOrPlayArea);
        btnTrashOrLog = findViewById(R.id.btnTrashOrLog);
        btnResign = findViewById(R.id.btnResign);

        clPlayArea = findViewById(R.id.clPlayArea);
        clKingdom = findViewById(R.id.clKingdom);
        clPlayArea.setVisibility(View.VISIBLE);
        clKingdom.setVisibility(View.GONE);
        btnKingdomOrPlayArea.setText("Kingdom");
        btnTrashOrLog.setText("Trash");
        rlButtonsInPlay = findViewById(R.id.rlButtonsInPlay);

        background = findViewById(R.id.background);
        background.setVisibility(View.VISIBLE);
        // Shows a progress dialog for start game until starting to get or upload the game data
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Starting Game");
        progressDialog.setCancelable(false);
        progressDialog.show();
        game.startUploadAndGet();
    }

    /**
     * A function that checks the permissions: READ_PHONE_STATE, SEND_SMS, READ_CALL_LOG
     * for the broadcast phoneCallReceiver and asks the permissions that haven't been
     * granted already.
     * If all permissions are granted already it registers phoneCallReceiver.
     */
    public void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS)
                        == PackageManager.PERMISSION_GRANTED) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PHONE_STATE");
            registerReceiver(phoneCallReceiver, filter);
            return;
        }

        ArrayList<String> permissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // asks the permission
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // asks the permission
            permissions.add(Manifest.permission.SEND_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            // asks the permission
            permissions.add(Manifest.permission.READ_CALL_LOG);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS)
                != PackageManager.PERMISSION_GRANTED) {
            // asks the permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissions.add(Manifest.permission.ANSWER_PHONE_CALLS);
            }
        }
        ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 1);
    }

    /**
     * A function that is called when all permissions were denied or granted.
     * @param requestCode An Integer with the request code passed in requestPermissions
     * @param permissions A String array with the requested permissions
     * @param grantResults An Integer Array with the grant results for the corresponding
     *                     permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS)
                        != PackageManager.PERMISSION_GRANTED)
            return;

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(phoneCallReceiver, filter);
    }

    /**
     * A function that is called when this intent is destroyed and unregisters phoneCallReceiver.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(phoneCallReceiver);
    }

    /**
     * A function that handles back press to show a dialog.
     */
    @Override
    public void onBackPressed() {
        // Shows an alert dialog to ask if he is he wants sure to resign
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to resign?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            /**
             * Handles yes answer and ends the dialog and the game.
             * @param dialogInterface
             * @param i
             */
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GameActivity.this.endGame();
                game.setResigned(true);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            /**
             * Handles no answer and ends the dialog.
             * @param dialogInterface
             * @param i
             */
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * A function that handles the long click of a card.
     * The function is called when a card when any card with a card image is pressed long.
     * The function shows a dialog of the card pressed in big.
     * @param v A View which is the view that was pressed long
     * @return A Boolean which is true always
     */
    @Override
    public boolean onLongClick(View v) {
        ImageView resourceIv;
        // v is an imageView only when ivMyDiscard or ivEnemyDiscard is pressed
        if (v instanceof ImageView)
            resourceIv = (ImageView) v;
        else
            resourceIv = v.findViewById(R.id.ivAction);
        ImageView iv = cardDialog.findViewById(R.id.ivAction);
        if (resourceIv.getContentDescription() != null) {
            Card card = Help.nameToCard(resourceIv.getContentDescription().toString());
            if (card != null) {
                iv.setImageResource(card.getImageSource());
                cardDialog.show();
            }
        }
        return true;
    }

    /**
     * A function that is called when a card from hand is pressed and can be played.
     * The function calls a function useCard from gameManager.
     * @param cardName A String which is the name of the card which should be used
     */
    public void useCard(String cardName) {
        if (game.getTimes().peek() != 1) // when the card should be played more than once
            game.useCard(cardName, game.getTimes().peek(), false, false);
        else
            game.useCard(cardName, 1, false, false);

        turnUI();
    }

    /**
     * A function that is called when a card from board is pressed and can be bought.
     * The function calls a function buyCard from gameManager and automatically changes
     * the turn if the player doesn't have more buys in turn.
     * @param cardName A string which is the name of the card which the player wants to buy
     */
    public void buyCard(String cardName) {
        game.buyCard(cardName);
        if (game.getTurn().getBuys() == 0) {
            btnEnd.setVisibility(View.GONE);
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.show();
            game.changeTurn();
        }

        turnUI();
        updateCards(true);
    }

    /**
     * A function that handles all presses on board image views or buttons.
     * @param view A View which is the view that was pressed
     */
    @Override
    public void onClick(View view) {
        if (view == btnStart) {
            btnStart.setVisibility(View.GONE);
            if (game.getGameManagerBeforeStart().isCreator())
                game.getGameManagerBeforeStart().setStart1(true);
            else
                game.getGameManagerBeforeStart().setStart2(true);
            GameRequests.updateReadyToStart(game);
        }
        else if (view == btnAutoplay) {
            game.autoPlayTreasures();
            turnUI();
            updateCards(true);
            btnAutoplay.setVisibility(View.GONE);
        }
        else if (view == btnEnd) {
            // force end of actions or buys
            if (btnEnd.getText().toString().equals("End Actions")) {
                game.getTurn().setForcedActionEnd(true);
                turnUI();
                this.handAdapter.notifyDataSetChanged();
            }
            else if (btnEnd.getText().toString().equals("End Buys")) {
                btnEnd.setVisibility(View.GONE);
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.show();
                game.changeTurn();
            }
        }

        else if (this.victoryCards.containsValue(view) || this.treasureCards.containsValue(view) || this.actionCards.containsValue(view)) {
            if ((!game.getTurn().getPhase().contains("buy") && !game.getTurn().getWaitForFunction().isWaitingForBoard())
                    || game.getTurn().isWaitingForEnemy()
                    || game.getTurn().getWaitForFunction().isWaitingForHand()
                    || game.getTurn().getWaitForFunction().isWaitingForButtonsOnly()
                    || game.getTurn().getWaitForFunction().isWaitingForActionCardsDialog())
                return;

            ImageView ivAction = view.findViewById(R.id.ivAction);
            if (game.getTurn().getWaitForFunction().isWaitingForBoard()) {
                if (game.getBoard().get(ivAction.getContentDescription().toString()) == 0
                        || !Help.nameToCard(game.getTurn().getWaitForFunction().getCardName())
                        .isCardToGetFromBoard(ivAction.getContentDescription().toString(), game, this))
                    return;

                game.getTurn().getWaitForFunction().insertCardSelectedInHand(ivAction.getContentDescription().toString());
                Help.nameToCard(game.getTurn().getWaitForFunction().getCardName()).clickOnBoard(ivAction.getContentDescription().toString(), game, this);
            }
            else if (game.getTurn().isMyTurn(game.getGameManagerBeforeStart())){
                game.getTurn().setPhase("buy");
                this.buyCard(ivAction.getContentDescription().toString());
            }
        }

        else if (view == btnKingdomOrPlayArea) {
            // Switches the middle layout between kingdom to play area
            if (btnKingdomOrPlayArea.getText().toString().equals("Kingdom")) {
                btnKingdomOrPlayArea.setText("Play Area");
                clPlayArea.setVisibility(View.GONE);
                clKingdom.setVisibility(View.VISIBLE);
            }
            else if (btnKingdomOrPlayArea.getText().toString().equals("Play Area")) {
                btnKingdomOrPlayArea.setText("Kingdom");
                clPlayArea.setVisibility(View.VISIBLE);
                clKingdom.setVisibility(View.GONE);
            }
        }
        else if (view == btnTrashOrLog) {
            // Switches the right middle layout between trash to log
            if (btnTrashOrLog.getText().toString().equals("Trash")) {
                btnTrashOrLog.setText("Log");
                rvTrash.setVisibility(View.VISIBLE);
                lvLog.setVisibility(View.GONE);
                tvInfoTitle.setText("Trash");
            }
            else if (btnTrashOrLog.getText().toString().equals("Log")) {
                btnTrashOrLog.setText("Trash");
                rvTrash.setVisibility(View.GONE);
                lvLog.setVisibility(View.VISIBLE);
                tvInfoTitle.setText("Log");
            }
        }

        else if (view == btnResign) {
            // Shows an alert dialog to ask if he is he wants sure to resign
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you really want to resign?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                /**
                 * Handles yes answer and ends the dialog and the game.
                 * @param dialogInterface
                 * @param i
                 */
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    GameActivity.this.endGame();
                    game.setResigned(true);
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                /**
                 * Handles no answer and ends the dialog.
                 * @param dialogInterface
                 * @param i
                 */
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        else if (this.btnActions.contains(view)) {
            Help.nameToCard(game.getTurn().getWaitForFunction().getCardName()).handleButtonClicks(((Button)view).getText().toString(), game, this);
        }

        else if (view == ivArrowActionCardsPlaying) {
            ConstraintSet constraintSetPlayArea = new ConstraintSet();
            constraintSetPlayArea.clone(clPlayArea);
            if (this.positionOfRVActionCardsPlaying.equals("up")) {
                this.positionOfRVActionCardsPlaying = "down";
                ivArrowActionCardsPlaying.setImageResource(R.mipmap.arrow_up);
                constraintSetPlayArea.connect(clActionCardsPlaying.getId(), ConstraintSet.TOP, rlButtonsInPlay.getId(), ConstraintSet.BOTTOM);
                constraintSetPlayArea.clear(clActionCardsPlaying.getId(), ConstraintSet.BOTTOM);
            }
            else {
                this.positionOfRVActionCardsPlaying = "up";
                ivArrowActionCardsPlaying.setImageResource(R.mipmap.arrow_down);
                constraintSetPlayArea.connect(clActionCardsPlaying.getId(), ConstraintSet.BOTTOM, rlButtonsInPlay.getId(), ConstraintSet.TOP);
                constraintSetPlayArea.clear(clActionCardsPlaying.getId(), ConstraintSet.TOP);
            }
            constraintSetPlayArea.applyTo(clPlayArea);
        }
    }

    /**
     * A function that is called after creating gameManage object.
     * The function handles all views before the player pressed "start game".
     */
    public void beforeStart() {
        game.beforeGame();
        if (game.getGameManagerBeforeStart().isCreator()) {
            tvMyName.setText(String.valueOf(game.getGameManagerBeforeStart().getIdP1()));
            tvEnemyName.setText(game.getGameManagerBeforeStart().getIdP2());
        }
        else {
            tvMyName.setText(String.valueOf(game.getGameManagerBeforeStart().getIdP2()));
            tvEnemyName.setText(game.getGameManagerBeforeStart().getIdP1());
        }

        rvHand.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        this.game.getPlayer().updateArrayHand();
        handAdapter = new HandAdapter(this.game.getPlayer().getArrayHand(), this);
        rvHand.setAdapter(handAdapter);

        rvActionCardsPlaying.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        actionCardsPlayingAdapter = new ActionCardPlayingAdapter(game.getTurn().getWaitForFunction().getCardsForDialog(), this);
        rvActionCardsPlaying.setAdapter(actionCardsPlayingAdapter);

        logAdapter = new LogAdapter(this,0,0, game.getLog(), game);
        lvLog.setAdapter(logAdapter);
        lvLog.setDivider(null);
        lvLog.setDividerHeight(0);
        lvLog.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);

        rvTrash.setLayoutManager(new GridLayoutManager(this, 2));
        this.game.updateArrayTrash();
        trashAdapter = new TrashAdapter(this.game.getArrayTrash(), this);
        rvTrash.setAdapter(trashAdapter);

        this.insertDataToHashMaps();
        this.uploadBoard();

        for (String cardName : actionBigCards.keySet()) {
            ImageView iv = actionBigCards.get(cardName).findViewById(R.id.ivAction);
            iv.setContentDescription(cardName);
            iv.setImageResource(Help.nameToCard(cardName).getImageSource());
        }

        this.updateCountOfBoard();

        btnStart.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
        btnAutoplay.setOnClickListener(this);
        btnKingdomOrPlayArea.setOnClickListener(this);
        btnTrashOrLog.setOnClickListener(this);
        btnResign.setOnClickListener(this);

        for (Button button : btnActions) {
            button.setOnClickListener(this);
        }

        for (String cardName : victoryCards.keySet()) {
            victoryCards.get(cardName).setOnLongClickListener(this);
            victoryCards.get(cardName).setOnClickListener(this);
        }

        for (String cardName : treasureCards.keySet()) {
            treasureCards.get(cardName).setOnLongClickListener(this);
            treasureCards.get(cardName).setOnClickListener(this);
        }

        for (String cardName : actionCards.keySet()) {
            actionCards.get(cardName).setOnLongClickListener(this);
            actionCards.get(cardName).setOnClickListener(this);
        }

        for (String cardName : actionBigCards.keySet()) {
            actionBigCards.get(cardName).setOnLongClickListener(this);
        }

        ivMyDiscard.setOnLongClickListener(this);
        ivEnemyDiscard.setOnLongClickListener(this);
    }

    /**
     * A function that is called when both players pressed "start game" and starts the game.
     */
    public void startGame() {
        game.setStarted(true);
        tvTurn.setVisibility(View.VISIBLE);
        clTurn.setVisibility(View.VISIBLE);
        tvMyVP.setVisibility(View.VISIBLE);
        tvEnemyVP.setVisibility(View.VISIBLE);
        game.startGame();
        if (!this.game.getTurn().isMyTurn(game.getGameManagerBeforeStart())) {
            // when the player doesn't starts
            btnEnd.setVisibility(View.GONE);
            btnAutoplay.setVisibility(View.GONE);
            GameRequests.getDataInGame(false, game);
        }
        else {
            // when the player starts
            GameRequests.uploadDataInGame(false, game);
            turnUI();
            updateCards(true);
            game.addTurnNumberToLog();
        }

        myDeck.setOnLongClickListener(this);
        enemyDeck.setOnLongClickListener(this);
    }

    /**
     * A function that inserts all board cards to hash maps by their name reference.
     */
    public void insertDataToHashMaps() {
        victoryCards.put("Province", findViewById(R.id.province));
        victoryCards.put("Duchy", findViewById(R.id.duchy));
        victoryCards.put("Estate", findViewById(R.id.estate));
        victoryCards.put("Curse", findViewById(R.id.curse));

        treasureCards.put("Gold", findViewById(R.id.gold));
        treasureCards.put("Silver", findViewById(R.id.silver));
        treasureCards.put("Copper", findViewById(R.id.copper));

        for(int i = 0; i < 10; i++) {
            int resID = getResources().getIdentifier(("action" + (i+1)), "id", getPackageName());
            actionCards.put(game.getActionCards()[i], findViewById(resID));
        }

        for(int i = 0; i < 10; i++) {
            int resID = getResources().getIdentifier(("actionBig" + (i+1)), "id", getPackageName());
            actionBigCards.put(game.getActionCards()[i], findViewById(resID));
        }
    }

    /**
     * A function that is called when the game is ended,
     * either if someone resigned or the game was ended according to the rules.
     */
    public void endGame() {
        btnEnd.setVisibility(View.GONE);
        btnAutoplay.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        if (game.getEnemyData().isResigned())
            progressDialog.setMessage(game.getGameManagerBeforeStart().getEnemyId() + " resigned...");
        else if (game.isResigned())
            progressDialog.setMessage("You resigned...");
        else
            progressDialog.setMessage("Game is ended...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * A function that is called when the game is ended
     * and finishes the intent to prepareGameActivity with the results of the game.
     */
    public void startPrepareActivity() {
        endProgressBar(false);
        game.getGameManagerBeforeStart().setReady1(false);
        game.getGameManagerBeforeStart().setReady2(false);
        game.getGameManagerBeforeStart().setStart1(false);
        game.getGameManagerBeforeStart().setStart2(false);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        if (game.getEnemyData().isResigned()) {
            intent.putExtra("gameManagerBeforeStart", game.getGameManagerBeforeStart());
            intent.putExtra("player", game.getGameManagerBeforeStart().getEnemyId());
            intent.putExtra("result", "resign");
            finish();
            return;
        }
        else if (game.isResigned()) {
            intent.putExtra("gameManagerBeforeStart", game.getGameManagerBeforeStart());
            intent.putExtra("player", game.getGameManagerBeforeStart().getMyId());
            intent.putExtra("result", "resign");
            finish();
            return;
        }
        int finalVP1 = game.getPlayer().getVictoryPoints(game);
        int finalVP2 = game.getEnemyData().getVictoryPoints();
        if (!game.getGameManagerBeforeStart().isCreator()) {
            int temp = finalVP1;
            finalVP1 = finalVP2;
            finalVP2 = temp;
        }
        intent.putExtra("gameManagerBeforeStart", game.getGameManagerBeforeStart());

        intent.putExtra("P1VP", finalVP1);
        intent.putExtra("P2VP", finalVP2);
        if (finalVP1 > finalVP2) {
            intent.putExtra("player", game.getGameManagerBeforeStart().getIdP1());
            intent.putExtra("result", "win");
        }
        else if (finalVP1 < finalVP2) {
            intent.putExtra("player", game.getGameManagerBeforeStart().getIdP2());
            intent.putExtra("result", "win");
        }
        else
            intent.putExtra("result", "draw");

        finish();
    }

    /**
     * A function that uploads the board images by type.
     */
    public void uploadBoard() {
        uploadByType(this.victoryCards);
        uploadByType(this.treasureCards);
        uploadByType(this.actionCards);
    }

    /**
     * A function that puts the image to the image views of the board.
     * @param hashMap A HashMap with card names and their references
     */
    public void uploadByType(HashMap<String, View> hashMap) {
        for (String cardName : hashMap.keySet()) {
            ImageView iv = hashMap.get(cardName).findViewById(R.id.ivAction);
            iv.setContentDescription(cardName);
            iv.setImageResource(Help.nameToCard(cardName).getShortImageSource());
        }
    }

    /**
     * A function that is called after any function which is doing changes in gameManager
     * and updates the changes on screen.
     * @param updateHand A Boolean which is true when if update hand is needed and false if not
     */
    public void updateCards(boolean updateHand) {
        updateCountOfBoard();
        logAdapter.notifyDataSetChanged();
        if (updateHand) {
            this.game.getPlayer().updateArrayHand();
            this.handAdapter.notifyDataSetChanged();
        }

        if (game.isStarted()) {
            tvTurn.setText(game.getTurn().toString());

            // My Data
            tvMyVP.setText(String.valueOf(game.getPlayer().getVictoryPoints(game)).concat(" VP"));
            int sizeDeck = game.getPlayer().getDeck().size();
            if (sizeDeck > 0) {
                myDeck.setVisibility(View.VISIBLE);
                TextView tv = myDeck.findViewById(R.id.countAction);
                tv.setText(String.valueOf(sizeDeck));
            } else
                myDeck.setVisibility(View.INVISIBLE);

            if (!game.getPlayer().getDiscard().isEmpty()) {
                ivMyDiscard.setVisibility(View.VISIBLE);
                String cardName = game.getPlayer().getDiscard().get(game.getPlayer().getDiscard().size() - 1);
                ivMyDiscard.setImageResource(Help.nameToCard(cardName).getShortImageSource());
                ivMyDiscard.setContentDescription(cardName);
            } else
                ivMyDiscard.setVisibility(View.INVISIBLE);

            // Enemy Data
            tvEnemyVP.setText(String.valueOf(game.getEnemyData().getVictoryPoints()).concat(" VP"));
            int sizeEnemyDeck = game.getEnemyData().getDeckSize();
            if (sizeEnemyDeck > 0) {
                enemyDeck.setVisibility(View.VISIBLE);
                TextView tv = enemyDeck.findViewById(R.id.countAction);
                tv.setText(String.valueOf(sizeEnemyDeck));
            } else
                enemyDeck.setVisibility(View.INVISIBLE);

            if (!game.getEnemyData().getLastCardOnDiscard().equals("")) {
                ivEnemyDiscard.setVisibility(View.VISIBLE);
                String cardName = game.getEnemyData().getLastCardOnDiscard();
                ivEnemyDiscard.setImageResource(Help.nameToCard(cardName).getShortImageSource());
                ivEnemyDiscard.setContentDescription(cardName);
            } else
                ivEnemyDiscard.setVisibility(View.INVISIBLE);

        }
        int sizeEnemyHand = game.getEnemyData().getHandSize();
        enemyHand.setVisibility(View.VISIBLE);
        TextView tv = enemyHand.findViewById(R.id.countAction);
        tv.setText(String.valueOf(sizeEnemyHand));
    }

    /**
     * A function that updates on screen the board if any changes done in gameManager.
     */
    public void updateCountOfBoard() {
        updateCountByType(this.victoryCards);
        updateCountByType(this.treasureCards);
        updateCountByType(this.actionCards);
    }

    /**
     * A function that updates the count of any image view on the board.
     * @param hashMap A HashMap with card names and their references
     */
    public void updateCountByType(HashMap<String, View> hashMap) {
        for (String cardName : hashMap.keySet()) {
            TextView tv = hashMap.get(cardName).findViewById(R.id.countAction);
            tv.setText(String.valueOf(game.getBoard().get(cardName)));
        }
    }

    /**
     * A function that is called when changing turn.
     */
    public void turnActions() {
        if (!this.game.getTurn().isMyTurn(game.getGameManagerBeforeStart())) {
            btnEnd.setVisibility(View.GONE);
            btnAutoplay.setVisibility(View.GONE);
        }
        else {
            turnUI();
            updateCards(true);
        }

        if (game.gameEnded()) {
            game.setGameEnded(true);
            this.endGame();
            GameRequests.endGame(game);
        }
    }

    /**
     * A function that updates the turn buttons according to the turn phase.
     */
    public void turnUI() {
        if (game.getTurn().isMyTurn(game.getGameManagerBeforeStart())) {
            if (game.getTurn().isWaitingForEnemy()
                    || game.getTurn().getWaitForFunction().isWaitingForHand()
                    || game.getTurn().getWaitForFunction().isWaitingForBoard()
                    || game.getTurn().getWaitForFunction().isWaitingForButtonsOnly()
                    || game.getTurn().getWaitForFunction().isWaitingForActionCardsDialog()) {
                btnEnd.setVisibility(View.GONE);
                btnAutoplay.setVisibility(View.GONE);
                return;
            }
            if (game.getPlayer().containsTypeCards("action") && (game.getTurn().getActions() > 0 || game.getTimes().peek() != 1) && !game.getTurn().getForcedActionEnd()) {
                this.game.getTurn().setPhase("play-action");
                actionsUI();
            }
            else {
                this.game.getTurn().setPhase("play-treasure-buy");
                buysUI();
            }
        }
    }

    /**
     * A function that is called when the phase is play-action and updates game buttons.
     */
    public void actionsUI() {
        btnEnd.setVisibility(View.VISIBLE);
        btnEnd.setText("End Actions");
        btnAutoplay.setVisibility(View.GONE);
    }

    /**
     * A function that is called when the phase is play-treasure-buy and updates game buttons.
     */
    public void buysUI() {
        btnEnd.setVisibility(View.VISIBLE);
        btnEnd.setText("End Buys");
        if (game.getPlayer().containsTypeCards("treasure"))
            btnAutoplay.setVisibility(View.VISIBLE);
        else
            btnAutoplay.setVisibility(View.GONE);
    }

    /**
     * A function that handles waiting for pressing on hand after playing some special cards.
     * @param cardName A String which is the name of the card that is waiting for clicking on hand
     * @param minAmount An Integer with the minimum of cards that should be selected from hand
     * @param maxAmount An Integer with the maximum of cards that should be selected from hand
     * @param typeOfAction A String with the type of action that will be
     *                     done with the selected cards (trash, discard...)
     * @param handleClickOnCard A Boolean which is true if the card should handle every
     *                          click on card in hand and false if not
     */
    public void waitForHand(String cardName, int minAmount, int maxAmount, String typeOfAction, boolean handleClickOnCard) {
        game.getTurn().getWaitForFunction().handleWaitingForHand(cardName, minAmount, maxAmount, typeOfAction, handleClickOnCard);
        this.handAdapter.notifyDataSetChanged();
    }

    /**
     * A function that handles waiting for pressing on board after playing some special cards.
     * @param cardName A String which is the name of the card that is waiting for clicking on board
     * @param minAmount An Integer with the minimum of cards that should be selected from board
     * @param maxAmount An Integer with the maximum of cards that should be selected from board
     */
    public void waitForBoard(String cardName, int minAmount, int maxAmount) {
        game.getTurn().getWaitForFunction().handleWaitingForBoard(cardName, minAmount, maxAmount);
    }

    /**
     * A function that sets the visibility to the special buttons that appears for some cards.
     * @param i An Integer with the place in btnActions of the specific button
     * @param visibility An Integer with the visibility the should be set to this button
     */
    public void setVisibilityForAction(int i, int visibility) {
        btnActions.get(i).setVisibility(visibility);
    }

    /**
     * A function that makes the amount of special buttons invisible
     * @param n An Integer with the amount of buttons that should be invisible
     */
    public void invisibleButtons(int n) {
        for (int i = 0; i < n; i++)
            btnActions.get(i).setVisibility(View.GONE);
    }

    /**
     * A function that uploads the text of some buttons that are used for a card.
     * @param textOnButtons A String Array with the text on the buttons
     * @param hasUndoAndConfirm A Boolean which is true if there is undo and confirm
     *                          button and false if not
     */
    public void uploadActionButtons(String[] textOnButtons, boolean hasUndoAndConfirm) {
        for (int i = 0; i < textOnButtons.length; i++)
            btnActions.get(i).setText(textOnButtons[i]);

        game.getTurn().getWaitForFunction().setHasUndoAndConfirm(hasUndoAndConfirm);
        if (hasUndoAndConfirm)
            updateActionButtons();
        else
            this.turnUI();
    }

    /**
     * A function that updates the undo and confirm buttons visibility,
     * which are common buttons for special cards.
     */
    public void updateActionButtons() {
        // 0 is always undo
        if ((game.getTurn().getWaitForFunction().isWaitingForHand() || game.getTurn().getWaitForFunction().isWaitingForActionCardsDialog())
                && Help.sizeOfHash(game.getTurn().getWaitForFunction().getCardsForActionCardPlay()) > 0)
            btnActions.get(0).setVisibility(View.VISIBLE);
        else
            btnActions.get(0).setVisibility(View.GONE);

        // 1 is always confirm
        if ((game.getTurn().getWaitForFunction().isWaitingForHand() || game.getTurn().getWaitForFunction().isWaitingForActionCardsDialog())
                && (Help.sizeOfHash(game.getTurn().getWaitForFunction().getCardsForActionCardPlay()) <= game.getTurn().getWaitForFunction().getMaxAmount()
                || game.getTurn().getWaitForFunction().getMaxAmount() == -1)
                && Help.sizeOfHash(game.getTurn().getWaitForFunction().getCardsForActionCardPlay()) >= game.getTurn().getWaitForFunction().getMinAmount())
            btnActions.get(1).setVisibility(View.VISIBLE);
        else
            btnActions.get(1).setVisibility(View.GONE);

        this.turnUI();
    }

    public RecyclerView.Adapter getHandAdapter() {
        return this.handAdapter;
    }

    public RecyclerView.Adapter getActionCardsPlayingAdapter() {
        return this.actionCardsPlayingAdapter;
    }

    /**
     * A function that sets the visibility of the Action Cards Playing RecyclerView
     * and sets the place of the RecyclerView to be up.
     * @param visibility
     */
    public void setVisibilityForRVActionCardsPlaying(int visibility) {
        if (visibility == View.INVISIBLE) {
            this.clActionCardsPlaying.setVisibility(View.INVISIBLE);
            return;
        }

        ConstraintSet constraintSetPlayArea = new ConstraintSet();
        constraintSetPlayArea.clone(clPlayArea);

        constraintSetPlayArea.connect(clActionCardsPlaying.getId(), ConstraintSet.BOTTOM, rlButtonsInPlay.getId(), ConstraintSet.TOP);
        constraintSetPlayArea.clear(clActionCardsPlaying.getId(), ConstraintSet.TOP);
        this.positionOfRVActionCardsPlaying = "up";
        ivArrowActionCardsPlaying.setImageResource(R.mipmap.arrow_down);
        constraintSetPlayArea.applyTo(clPlayArea);

        this.clActionCardsPlaying.setVisibility(View.VISIBLE);
    }

    public RecyclerView.Adapter getTrashAdapter() {
        return this.trashAdapter;
    }

    /*
    public float getDefaultSize() {
        return tvTurn.getTextSize();
    }
     */

    /**
     * A function that ends the progress bar
     * @param isHandleBackground A Boolean which is true only when
     *                           the progress bar was shown at the start of the game.
     */
    public void endProgressBar(boolean isHandleBackground) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            if (isHandleBackground)
                background.setVisibility(View.INVISIBLE);
        }
    }
}