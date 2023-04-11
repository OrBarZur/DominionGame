package com.example.dominion_game.cards_classes;

import android.view.View;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class Moat extends Card {
    public Moat() {
        super("Moat", 2, R.mipmap.moat, R.mipmap.moat_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(2, gameActivity, game, game.getTabs());
        game.useAfterPlay(this.getName(), false);
    }

    @Override
    public void reaction(GameManager game, GameActivity gameActivity) {
        game.getTurn().getWaitForFunction().handleWaitingForButtonsOnly(this.getName());
        gameActivity.uploadActionButtons(new String[]{"Reveal Moat", "Don't Reveal"}, false);
        gameActivity.setVisibilityForAction(0, View.VISIBLE);
        gameActivity.setVisibilityForAction(1, View.VISIBLE);
        game.getTurn().getWaitForFunction().insertCardSelectedInHand(game.getTurn().getLastActionCardForWait());
        game.getTurn().removeLastAttack();

    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        String cardName = String.valueOf(game.getTurn().getWaitForFunction().getCardsForActionCardPlay().keySet().toArray()[0]);
        game.getTurn().getWaitForFunction().clear();
        gameActivity.invisibleButtons(2);

        if (buttonText.startsWith("Reveal"))
            game.setDoneAttack(true);
        else
            Help.nameToCard(cardName).attack(game, gameActivity);
    }
}
