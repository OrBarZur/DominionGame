package com.example.dominion_game.cards_classes;

import android.view.View;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class Moneylender extends Card {
    public Moneylender() {
        super("Moneylender", 4, R.mipmap.moneylender, R.mipmap.moneylender_sh, "action");
    }

    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        if (!game.getPlayer().containsCard("Copper")) {
            game.useAfterPlay(this.getName(), false);
            return;
        }

        gameActivity.waitForHand(this.getName(), 1, 1, "trash", true);
        gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), "Don't Trash", gameActivity.getString(R.string.confirm_trash)}, false);
        gameActivity.setVisibilityForAction(1, View.VISIBLE);
    }

    @Override
    public void handleClickOnHandOrDialog(String cardName, GameManager game, GameActivity gameActivity) {
        gameActivity.setVisibilityForAction(0, View.VISIBLE);
        gameActivity.setVisibilityForAction(2, View.VISIBLE);
    }

    @Override
    public boolean isMarkCardSelectedFromHandWhenHandle() {
        return true;
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        if (buttonText.equals(gameActivity.getString(R.string.undo))) {
            game.getTurn().getWaitForFunction().undo();
            gameActivity.setVisibilityForAction(0, View.GONE);
            gameActivity.setVisibilityForAction(2, View.GONE);
        }
        else if (buttonText.equals("Don't Trash")) {
            game.getTurn().getWaitForFunction().clear();
            gameActivity.invisibleButtons(3);
            gameActivity.turnUI();
            game.useAfterPlay(this.getName(), false);
        }
        else if (buttonText.equals(gameActivity.getString(R.string.confirm_trash))) {
            if (game.getPlayer().getHand().keySet().contains("Copper")) {
                if (!game.getPlayer().removeFromHand("Copper")) {
                    game.useAfterPlay(this.getName(), false);
                    return;
                }
                game.addToTrash("Copper", 1);
                game.getTurn().addTreasure(3);
            }
            game.getTurn().getWaitForFunction().clear();
            gameActivity.invisibleButtons(3);
            gameActivity.turnUI();
            game.useAfterPlay(this.getName(), false);
        }
        game.getPlayer().updateArrayHand();
        gameActivity.getHandAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return Help.nameToCard(cardName).getName().equals("Copper");
    }
}
