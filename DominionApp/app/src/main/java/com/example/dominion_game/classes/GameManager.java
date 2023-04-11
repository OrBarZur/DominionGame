/**
 * GameManager is a class that keeps all the data about the game for the player.
 */
package com.example.dominion_game.classes;

import android.util.Pair;

import com.example.dominion_game.activities.GameActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class GameManager {
    private static final int numberOfCards = 5;
    private static final int victoryAmount = 8;
    private static final int curseAmount = 10;
    private static final int actionAmount = 10;
    private static final int copperAmount = 46;
    private static final int silverAmount = 40;
    private static final int goldAmount = 30;
    private static final int decksToEndGame = 3;

    private GameManagerBeforeStart gameManagerBeforeStart;
    private GameActivity gameActivity;
    private String[] actionCards;
    private Player player;
    private EnemyData enemyData;
    private HashMap <String, Integer> board;
    private HashMap <String, Integer> trash;
    private ArrayList<Pair<String, Integer>> arrayTrash;

    private Turn turn;
    private ArrayList<LogLine> log;
    private boolean isStarted;
    private boolean isGameEnded;
    private boolean resigned;

    private boolean playsAttack;
    private boolean doneAttack;

    private Stack <Integer> times; // times playing an action card (for card throne room)

    /**
     * The constructor with default values for all attributes but gameManagerBeforeStart
     * and gameActivity that were created before
     */
    public GameManager(GameManagerBeforeStart gameManagerBeforeStart, GameActivity gameActivity) {
        this.gameActivity = gameActivity;
        this.player = new Player();
        this.enemyData = new EnemyData();
        this.gameManagerBeforeStart = gameManagerBeforeStart;

        this.board = new HashMap<>();
        this.board.put("Estate", victoryAmount);
        this.board.put("Duchy", victoryAmount);
        this.board.put("Province", victoryAmount);
        this.board.put("Curse", curseAmount);
        this.board.put("Copper", copperAmount);
        this.board.put("Silver", silverAmount);
        this.board.put("Gold", goldAmount);
        this.trash = new HashMap<>();
        this.arrayTrash = new ArrayList<>();
        this.log = new ArrayList<>();
        this.isStarted = false;
        this.isGameEnded = false;
        this.resigned = false;
        this.playsAttack = false;
        this.doneAttack = false;
        times = new Stack<>();
        times.push(1);
    }

    /**
     * A function that sends the creator to a function for uploading data
     * and the non creator for a function for getting data.
     * This function also generates for the creator
     * the action cards and chooses randomly who starts.
     */
    public void startUploadAndGet() {
        if (gameManagerBeforeStart.isCreator()) {
            this.actionCards = Help.getRandomCards(new String[]{"Base"}, 10, new String[]{});

            Random randomStart = new Random();
            this.turn = new Turn(randomStart.nextInt(2) == 0 ? gameManagerBeforeStart.getIdP1() : gameManagerBeforeStart.getIdP2());

            this.gameActivity.beforeStart();
            GameRequests.uploadDataInGame(true, this);
        }
        else {
            this.actionCards = new String[10];
            this.turn = new Turn();
            GameRequests.getDataInGame(true, this);
        }
    }

    /**
     * A function that generates before start by put in hand the start cards
     * and put in board the starting cards.
     */
    public void beforeGame() {
        for (String nameCard : actionCards) {
            if (Help.nameToCard(nameCard).getType().equals("victory"))
                this.board.put(nameCard, victoryAmount);
            else
                this.board.put(nameCard, actionAmount);
        }

        this.player.getHand().put("Copper", 7);
        this.player.getHand().put("Estate", 3);

        if (this.gameManagerBeforeStart.isCreator()) {
            this.log.add(new LogLine("Starts with 7 Coppers", this.gameManagerBeforeStart.getMyId(), false, false, 0, "start"));
            this.log.add(new LogLine("Starts with 3 Estates", this.gameManagerBeforeStart.getMyId(), false, false, 0, "start"));
            this.log.add(new LogLine("Starts with 7 Coppers", this.gameManagerBeforeStart.getEnemyId(), false, false, 0, "start"));
            this.log.add(new LogLine("Starts with 3 Estates", this.gameManagerBeforeStart.getEnemyId(), false, false, 0, "start"));
        }
    }

    /**
     * A function that generates start after both players were ready.
     */
    public void startGame() {
        this.cleanUpPhase();

        this.player.discardToDeck(this, 0);
        this.player.takeCardsToHand(numberOfCards, this.gameActivity, this, 0);

        if (this.turn.isMyTurn(this.gameManagerBeforeStart)) {
            this.log.add(new LogLine("Shuffles his deck", this.gameManagerBeforeStart.getEnemyId(), false, true, "pink", 0, "start"));
            this.log.add(new LogLine("Draws " + numberOfCards + " card" + (numberOfCards == 1 ? "" : "s"), this.gameManagerBeforeStart.getEnemyId(), false, false, 0, "start"));
        }
        else {
            this.deleteLastLineFromLog();
            this.deleteLastLineFromLog();
        }
    }

    /**
     * A function that adds to log the turn number.
     * This function is called at the start of every turn.
     */
    public void addTurnNumberToLog() {
        this.log.add(new LogLine("", "", false, false, 0, "change turn"));
        this.log.add(new LogLine("Turn " + this.turn.getTurnNumber() + " - " + this.turn.getTurnId(), "", true, false, 0, "change turn"));
    }

    /**
     * A function that uses a card n times if the card can be used.
     * @param cardName A String which is the name of the card which should be used
     * @param n An Integer which is the number of times that the card should be played
     * @param forceUse A Boolean which is true if the use of the card was forced or not
     * @param autoPlay A Boolean which is true if useCard was played with autoPlay and false if not
     */
    public void useCard(String cardName, int n, boolean forceUse, boolean autoPlay) {
        // n > 1 when throne room or autoPlay
        if (!this.turn.isMyTurn(this.gameManagerBeforeStart) || Help.nameToCard(cardName).getType().equals("victory") ||
                (Help.nameToCard(cardName).getType().equals("action") && this.getTurn().getActions() == 0 && n == 1))
            return;

        for (int i = 0; i < n; i++)
            this.playCardOrAddToWaitList(cardName, n, i, forceUse, autoPlay);
    }

    /**
     * A function that plays a card or adding it to the wait list of cards.
     * @param cardName A String which is the name of the card which should be used
     * @param n An Integer which is the number of times that the card should be played
     * @param i An Integer which is the place of the card in the wait queue
     * @param forceUse A Boolean which is true if the use of the card was forced or not
     * @param autoPlay A Boolean which is true if useCard was played with autoPlay and false if not
     */
    public void playCardOrAddToWaitList(String cardName, int n, int i, boolean forceUse, boolean autoPlay) {
        if (this.turn.getLastCardForWaitForPlay().equals("")) {
            this.turn.setLastCardForWaitForPlay(cardName);
            this.playCard(cardName, n, i, forceUse, autoPlay);
        }
        else
            this.turn.addWaitForPlay(cardName, n, i, forceUse, autoPlay);
    }
    /**
     * A function that plays a card and adds to log.
     * @param cardName A String which is the name of the card which should be used
     * @param n An Integer which is the number of times that the card should be played
     * @param i An Integer which is the place of the card in the wait queue
     * @param forceUse A Boolean which is true if the use of the card was forced or not
     * @param autoPlay A Boolean which is true if useCard was played with autoPlay and false if not
     */
    public void playCard(String cardName, int n, int i, boolean forceUse, boolean autoPlay) {
        if (this.player.getHand().containsKey(cardName) && (autoPlay || n == 1 || i == 0)) {
            this.player.removeFromHand(cardName);
            this.player.updateArrayHand();
            gameActivity.getHandAdapter().notifyDataSetChanged();
        }

        if (Help.nameToCard(cardName).getType().equals("action") && !forceUse)
            this.log.add(new LogLine("Plays a " + Help.nameToCard(cardName).getNameToDisplay(), this.turn.getTurnId(), false, false, this.getTabs() - 1, "use action"));

        else if (forceUse || (!autoPlay && n > 1 && i == 0)) // throne room first or vassal
            this.log.add(new LogLine("Plays a " + Help.nameToCard(cardName).getNameToDisplay(), this.turn.getTurnId(), false, false, this.getLastLineFromLog().getTabs() + 1, "use action"));

        else if ((!autoPlay && n > 1)) // not first played in throne room
            this.log.add(new LogLine("Plays a " + Help.nameToCard(cardName).getNameToDisplay(), this.turn.getTurnId(), false, false, this.getLastLineFromLog().getTabs() - 1, "use action"));

        if (Help.nameToCard(cardName).getType().equals("action") && n == 1 && !forceUse)
            this.getTurn().useAction();

        if (Help.nameToCard(cardName).getType().equals("action") && i == 0) {
            this.turn.addActionCard(cardName);
        }
        else if (Help.nameToCard(cardName).getType().equals("treasure")) {
            this.turn.addTreasureCard(cardName);
            this.addHashToLog(this.turn.getTreasureCardsPlayed(), "use treasure", "Plays");
        }

        Help.nameToCard(cardName).play(this, gameActivity);
    }

    /**
     * A function that calls the function afterPlay
     * for every card used before in this turn.
     * @param cardNameUsed A String with the name of card used
     */
    public void useAfterPlay(String cardNameUsed, boolean addWaitForEnemy) {
        this.turn.setLastCardForWaitForPlay("");
        for (String cardName : this.getTurn().getActionCardsPlayed())
            Help.nameToCard(cardName).afterPlay(this, cardNameUsed);

        for (String cardName : this.turn.getTreasureCardsPlayed().keySet())
            for (int i = 0; i < this.turn.getTreasureCardsPlayed().get(cardName); i++)
                Help.nameToCard(cardName).afterPlay(this, cardNameUsed);

        if (addWaitForEnemy) {
            this.turn.addWaitForEnemy(cardNameUsed);
            gameActivity.turnUI();
            gameActivity.updateCards(true);
        }
        else
            this.endCard();
    }

    /**
     * A function that is called after the card ended and plays future cards if has.
     */
    public void endCard() {
        if (!this.turn.getCardsForWaitForPlay().isEmpty()) {
            PlayCardArguments playCardArguments = this.turn.getCardsForWaitForPlay().remove(0);
            this.turn.setLastCardForWaitForPlay(playCardArguments.getCardName());
            this.playCard(playCardArguments.getCardName(), playCardArguments.getN(), playCardArguments.getI(), playCardArguments.isForceUse(), playCardArguments.isAutoPlay());
        }
        else {
            if (this.times.peek() != 1)
                this.times.pop();
        }
        gameActivity.turnUI();
        gameActivity.updateCards(true);
    }

    /**
     * A function that buys a card times if the card can be bought.
     * @param cardName A String which is the name of the card which should be used
     */
    public void buyCard(String cardName) {
        if ((this.board.get(cardName) == 0 || this.turn.getBuys() == 0 || this.turn.getTreasure() < Help.nameToCard(cardName).getPrice()))
            return;

        this.turn.useBuy();
        this.turn.useTreasure(Help.nameToCard(cardName).getPrice());
        // remove from board cards
        this.board.put(cardName, this.board.get(cardName) - 1);
        this.turn.addCardBought(cardName);
        this.addHashToLog(this.turn.getCardsBought(), "buy and gain", "Buys and Gains");
    }

    /**
     * A function that removes a card from board and returns it.
     * @param cardName A String with the card name
     * @return A String with the card name
     */
    public String getCard(String cardName) {
        if (this.board.get(cardName) == 0)
            return "";
        // remove from board cards
        this.board.put(cardName, this.board.get(cardName) - 1);
        return cardName;
    }

    /**
     * A function that handles cleanUp and add all cards that
     * were used to the discard and clear the hand.
     */
    public void cleanUpPhase() {
        this.addToDiscardByType("action");
        this.addToDiscardByType("treasure");
        this.addToDiscardByType("victory");
        this.player.getDiscard().addAll(this.turn.getActionCardsPlayed());
        this.player.getDiscard().addAll(Help.hashToArray(this.turn.getTreasureCardsPlayed()));
        this.player.getDiscard().addAll(Help.hashToArray(this.turn.getCardsBought()));

        this.player.getHand().clear();
        this.player.updateArrayHand();
        gameActivity.getHandAdapter().notifyDataSetChanged();
        times.clear();
        times.push(1);
    }

    /**
     * A function that adds the cards of this type from hand to discard.
     * @param type A String with the type of the card (action, treasure or victory)
     */
    public void addToDiscardByType(String type) {
        for (String cardName : this.player.getHand().keySet())
            if (Help.nameToCard(cardName).getType().equals(type))
                for (int i = 0; i < this.player.getHand().get(cardName); i++)
                    this.player.getDiscard().add(cardName);
    }

    /**
     * A function that autoPlay all treasures in hand by using each treasure card.
     */
    public void autoPlayTreasures() {
        String[] arrayKeys = this.player.getHand().keySet().toArray
                (new String[this.player.getHand().keySet().size()]);
        for (int i = 0; i < arrayKeys.length; i++) {
            if (Help.nameToCard(arrayKeys[i]).getType().equals("treasure"))
                useCard(arrayKeys[i], this.player.getHand().get(arrayKeys[i]), false, true);
        }

        this.addHashToLog(this.turn.getTreasureCardsPlayed(), "use treasure", "Plays");
    }

    /**
     * A function that adds to log the use or buy of HashMap with cards.
     * @param hm A HashMap of card names with the amount of it
     * @param action A String with the action in the log
     */
    public void addHashToLog(HashMap<String, Integer> hm, String action, String textBefore) {
        if ((action.equals("buy and gain") && this.getLastLineFromLog().getType().equals("buy and gain"))
                || (action.equals("use treasure") && this.getLastLineFromLog().getTabs() == 0 && this.getLastLineFromLog().getType().equals("use treasure")))
            this.deleteLastLineFromLog(); // deletes the last line because it will add an updated line

        String[] arrayKeys = hm.keySet().toArray
                (new String[hm.keySet().size()]);
        for (int i = 0; i < arrayKeys.length; i++) {
            if (i == 0)
                this.log.add(new LogLine(textBefore + " " + (hm.get(arrayKeys[0]) == 1
                       ? "a " : hm.get(arrayKeys[0]) + " ") + Help.nameToCard(arrayKeys[0]).getNameToDisplay() + (hm.get(arrayKeys[i]) == 1 ? "" : "s"),
                       this.turn.getTurnId(), false, false, 0, action));

            else if (i < arrayKeys.length - 1)
                this.getLastLineFromLog().setText(this.getLastLineFromLog().getText() + ", " + (hm.get(arrayKeys[i]) == 1
                        ? "a " : hm.get(arrayKeys[i]) + " ") + Help.nameToCard(arrayKeys[i]).getNameToDisplay() + (hm.get(arrayKeys[i]) == 1 ? "" : "s"));

            else
                this.getLastLineFromLog().setText(this.getLastLineFromLog().getText() + " and " + (hm.get(arrayKeys[i]) == 1
                        ? "a " : hm.get(arrayKeys[i]) + " ") + Help.nameToCard(arrayKeys[i]).getNameToDisplay() + (hm.get(arrayKeys[i]) == 1 ? "" : "s"));
        }
    }

    public String[] getActionCards() {
        return this.actionCards;
    }

    public void setActionCards(String[] actionCards) {
        this.actionCards = actionCards;
    }

    public HashMap<String, Integer> getBoard() {
        return this.board;
    }

    public void setBoard(HashMap<String, Integer> board) {
        this.board = board;
    }

    public HashMap<String, Integer> getTrash() {
        return this.trash;
    }

    public void setTrash(HashMap<String, Integer> trash) {
        this.trash = trash;
    }

    public ArrayList<Pair<String, Integer>> getArrayTrash() {
        return this.arrayTrash;
    }

    /**
     * A function that adds a card to trash
     * @param cardName A String with the name of card to be added to trash
     */
    public void addToTrash(String cardName, int n) {
        if (this.trash.containsKey(cardName))
            this.trash.put(cardName, this.trash.get(cardName) + n);
        else
            this.trash.put(cardName, n);
        this.updateArrayTrash();
        gameActivity.getTrashAdapter().notifyDataSetChanged();
    }

    /**
     * A function that updates the arrayTrash to be same as in trash.
     */
    public void updateArrayTrash() {
        this.arrayTrash.clear();
        for (String cardName : this.trash.keySet())
            this.arrayTrash.add(new Pair<>(cardName, this.trash.get(cardName)));
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * A function that returns whether the game is ended according to the rules.
     * @return A Boolean which is true if the game is ended according to the rules and false if not
     */
    public boolean gameEnded() {
        if (this.board.get("Province") == 0)
            return true;
        int count = 0;
        for (String cardName : this.board.keySet()) {
            if (this.board.get(cardName) == 0)
                count++;
            if (count >= decksToEndGame)
                return true;
        }
        return false;
    }

    /**
     * A function that changes the turn by calling clean up, restarting turn and take new cards.
     */
    public void changeTurn() {
        this.turn.setPhase("cleanUp");
        this.cleanUpPhase();
        this.turn.changeTurn(this.gameManagerBeforeStart);
        this.player.takeCardsToHand(numberOfCards, this.gameActivity, this, 0);
    }

    public Turn getTurn() {
        return this.turn;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public ArrayList<LogLine> getLog() {
        return this.log;
    }

    /**
     * A function that deletes the last line in the log.
     */
    public void deleteLastLineFromLog() {
        if (this.log.size() > 0)
            this.log.remove(this.log.size() - 1);
    }

    /**
     * A function that returns the last line in the log.
     * @return A LogLine which is the last line in the log
     */
    public LogLine getLastLineFromLog() {
        if (this.log.size() > 0)
            return this.log.get(this.log.size() - 1);
        return null;
    }

    public Stack<Integer> getTimes() {
        return this.times;
    }

    public int getTabs() {
        return this.times.size();
    }

    public GameActivity getGameActivity() {
        return this.gameActivity;
    }

    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    /**
     * A function that creates a JSONObject from the data in
     * gameManager to upload to server before starting game.
     * @return A JSONObject of the gameManager attributes
     */
    public JSONObject gameManagerToJsonStart() {
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        try {
            jsonObject.put("gameManagerBeforeStart", this.gameManagerBeforeStart.gameManagerBeforeStartToJson());
            jsonObject.put("board", gson.toJson(this.board));
            jsonObject.put("turn", this.turn.turnToJson(true));
            jsonObject.put("trash", gson.toJson(this.trash));
            jsonObject.put("log", gson.toJson(this.logToJson()));
            jsonObject.put("actionCards", gson.toJson(this.actionCards));
            jsonObject.put("isGameEnded", this.isGameEnded);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * A function that creates a JSONObject from the data in
     * gameManager to upload to server after starting game.
     * @return A JSONObject of the gameManager attributes
     */
    public JSONObject gameManagerToJsonRealTime() {
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        try {
            jsonObject.put("gameManagerBeforeStart", this.gameManagerBeforeStart.gameManagerBeforeStartToJson());
            jsonObject.put("isGameEnded", this.isGameEnded);
            if (this.turn.isWaitingForEnemy() && this.turn.getLastActionCardForWait().equals("") && this.turn.isMyTurn(this.gameManagerBeforeStart))
                return jsonObject;

            jsonObject.put("board", gson.toJson(this.board));
            jsonObject.put("trash", gson.toJson(this.trash));
            jsonObject.put("log", gson.toJson(this.logToJson()));
            jsonObject.put("turn", this.turn.turnToJson(this.doneAttack || this.turn.isMyTurn(this.gameManagerBeforeStart)));

            if (this.doneAttack) {
                this.doneAttack = false;
                this.playsAttack = false;
            }

            if (!this.turn.getLastActionCardForWait().equals(""))
                this.turn.removeLastAttack();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * A function that updates the values on the attributes
     * @param jsonObject A JSONObject that is given from the server
     */
    public void jsonToGameManagerStart(JSONObject jsonObject) {
        Gson gson = new Gson();
        try {
            this.gameManagerBeforeStart.jsonToGameManagerBeforeStart(jsonObject, true);
            Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
            this.board = gson.fromJson(jsonObject.getString("board"), type);
            this.turn.jsonToTurn(jsonObject.getJSONObject("turn"), this);
            type = new TypeToken<ArrayList<JSONObject>>(){}.getType();
            this.jsonToLog((ArrayList<JSONObject>)gson.fromJson(jsonObject.getString("log"), type));
            this.actionCards = gson.fromJson(jsonObject.getString("actionCards"), String[].class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * A function that updates the values on the attributes
     * @param jsonObject A JSONObject that is given from the server
     */
    public void jsonToGameManagerRealTime(JSONObject jsonObject) {
        if (!this.turn.isMyTurn(this.gameManagerBeforeStart) && !jsonObject.has("turn"))
            return;
        Gson gson = new Gson();
        try {
            Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
            this.board = gson.fromJson(jsonObject.getString("board"), type);
            if (!this.trash.equals(gson.fromJson(jsonObject.getString("trash"), type))) {
                this.trash = gson.fromJson(jsonObject.getString("trash"), type);
                this.updateArrayTrash();
                gameActivity.getTrashAdapter().notifyDataSetChanged();
            }
            type = new TypeToken<ArrayList<JSONObject>>(){}.getType();
            this.jsonToLog((ArrayList<JSONObject>)gson.fromJson(jsonObject.getString("log"), type));
            this.turn.jsonToTurn(jsonObject.getJSONObject("turn"), this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * A function that creates a JSONObject from the relevant data
     * for uploading to server about the size of deck and hand and more.
     * @return A JSONObject relevant attributes for the other player
     */
    public JSONObject myDataToJson() {
        JSONObject myData = new JSONObject();
        try {
            if (!this.player.getDiscard().isEmpty())
                myData.put("lastCardOnDiscard", this.player.getDiscard().get(this.player.getDiscard().size() - 1));
            else
                myData.put("lastCardOnDiscard", "");
            myData.put("deckSize", this.player.getDeck().size());
            myData.put("handSize", Help.sizeOfHash(this.player.getHand()));
            myData.put("victoryPoints", this.player.getVictoryPoints(this));
            myData.put("resigned", this.resigned);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject gameManagerBeforeStartJson = this.gameManagerBeforeStart.gameManagerBeforeStartToJson();
            jsonObject.put("gameManagerBeforeStart", gameManagerBeforeStartJson);
            jsonObject.put("myData", myData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * A function that returns an ArrayList of JSONObject with all the log lines.
     * @return An ArrayList of JSONObject with all the log lines
     */
    public ArrayList<JSONObject> logToJson() {
        ArrayList<JSONObject> logLines = new ArrayList<>();
        for (LogLine logLine : this.log)
            logLines.add(logLine.logLineToJson());

        return logLines;
    }

    /**
     * A function that updates new log lines in log.
     * @param jsonLogLines A JSONObject that is given from the server with the lines of log
     */
    public void jsonToLog(ArrayList<JSONObject> jsonLogLines) {
        ArrayList<LogLine> logLines = new ArrayList<>();
        for (JSONObject logLine : jsonLogLines)
            logLines.add(new LogLine(logLine));

        if (logLines.size() > this.log.size())
            this.log.addAll(logLines.subList(this.log.size(), logLines.size()));
    }

    public GameManagerBeforeStart getGameManagerBeforeStart() {
        return this.gameManagerBeforeStart;
    }

    public EnemyData getEnemyData() {
        return this.enemyData;
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    public void setStarted(boolean started) {
        this.isStarted = started;
    }

    public boolean isGameEnded() {
        return this.isGameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        this.isGameEnded = gameEnded;
    }

    public boolean isResigned() {
        return this.resigned;
    }

    public void setResigned(boolean resigned) {
        this.resigned = resigned;
    }

    public boolean isPlaysAttack() {
        return this.playsAttack;
    }

    public void setPlaysAttack(boolean playsAttack) {
        this.playsAttack = playsAttack;
    }

    public boolean isDoneAttack() {
        return this.doneAttack;
    }

    public void setDoneAttack(boolean doneAttack) {
        this.doneAttack = doneAttack;
    }
}
