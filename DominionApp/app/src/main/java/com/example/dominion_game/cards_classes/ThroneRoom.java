package com.example.dominion_game.cards_classes;

import android.view.View;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class ThroneRoom extends Card {
    public ThroneRoom() {
        super("ThroneRoom", 4, R.mipmap.throneroom, R.mipmap.throneroom_sh, "action");
    }

    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        if (!game.getPlayer().containsTypeCards("action")) {
            game.useAfterPlay(this.getName(), false);
            return;
        }

        game.getTimes().push(2);
        gameActivity.waitForHand(this.getName(), 1, 1, "use", true);
        gameActivity.uploadActionButtons(new String[]{"Don't Throne"}, false);
        gameActivity.setVisibilityForAction(0, View.VISIBLE);
    }

    @Override
    public void handleClickOnHandOrDialog(String cardName, GameManager game, GameActivity gameActivity) {
        game.getTurn().getWaitForFunction().clear();
        gameActivity.invisibleButtons(1);
        game.getTurn().addWaitForPlay(cardName, game.getTimes().peek(), 0, false, false);
        game.getTurn().addWaitForPlay(cardName, game.getTimes().peek(), 1, false, false);
        game.useAfterPlay(this.getName(), false);
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        if (buttonText.equals("Don't Throne")) {
            game.getTurn().getWaitForFunction().clear();
            gameActivity.invisibleButtons(1);
            gameActivity.turnUI();
            game.useAfterPlay(this.getName(), false);
        }
    }

    @Override
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return Help.nameToCard(cardName).getType().equals("action");
    }

    @Override
    public String getNameToDisplay() {
        return "Throne Room";
    }
}
