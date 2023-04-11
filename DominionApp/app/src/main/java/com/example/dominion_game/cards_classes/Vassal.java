package com.example.dominion_game.cards_classes;

import android.view.View;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

import java.util.ArrayList;

public class Vassal extends Card {
    public Vassal() {
        super("Vassal", 3, R.mipmap.vassal, R.mipmap.vassal_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getTurn().addTreasure(2);
        ArrayList<String> cards = game.getPlayer().takeCards(1, game, game.getTabs());
        if (cards.size() == 0) {
            game.useAfterPlay(this.getName(), false);
            return;
        }
        String cardName = cards.get(0);

        if (!Help.nameToCard(cardName).getType().equals("action")) {
            ArrayList<String> cardArrayList = new ArrayList<>();
            cardArrayList.add(cardName);
            game.getPlayer().getDiscard().addAll(cardArrayList);
            game.useAfterPlay(this.getName(), false);
            return;
        }
        game.getTurn().getWaitForFunction().handleWaitingForButtonsOnly(this.getName());
        gameActivity.uploadActionButtons(new String[]{"Play " + Help.nameToCard(cardName).getNameToDisplay(), "Don't Play"}, false);
        gameActivity.setVisibilityForAction(0, View.VISIBLE);
        gameActivity.setVisibilityForAction(1, View.VISIBLE);
        game.getTurn().getWaitForFunction().insertCardSelectedInHand(cardName);
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        String cardName = String.valueOf(game.getTurn().getWaitForFunction().getCardsForActionCardPlay().keySet().toArray()[0]);
        if (buttonText.startsWith("Play"))
            game.getTurn().addWaitForPlay(cardName, 1, 0, true, false);
        else
            game.getPlayer().addToDiscard(cardName);
        game.getTurn().getWaitForFunction().clear();
        gameActivity.invisibleButtons(2);
        gameActivity.turnUI();
        game.getPlayer().updateArrayHand();
        gameActivity.getHandAdapter().notifyDataSetChanged();
        game.useAfterPlay(this.getName(), false);
    }
}
