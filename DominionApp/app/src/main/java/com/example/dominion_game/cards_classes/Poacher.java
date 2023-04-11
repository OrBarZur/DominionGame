package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;

public class Poacher extends Card {
    public Poacher() {
        super("Poacher", 4, R.mipmap.poacher, R.mipmap.poacher_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(1, gameActivity, game, game.getTabs());
        game.getTurn().addActions(1);
        game.getTurn().addTreasure(1);
        int count = 0;
        for (String cardName : game.getBoard().keySet()) {
            if (game.getBoard().get(cardName) == 0)
                count++;
        }
        if (count == 0) {
            game.useAfterPlay(this.getName(), false);
            return;
        }
        gameActivity.waitForHand(this.getName(), count, count, "discard", false);
        gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), gameActivity.getString(R.string.confirm_discard)}, true);
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        if (buttonText.equals(gameActivity.getString(R.string.undo))) {
            game.getTurn().getWaitForFunction().undo();
            gameActivity.updateActionButtons();
            gameActivity.getHandAdapter().notifyDataSetChanged();
        }
        else if (buttonText.equals(gameActivity.getString(R.string.confirm_discard))) {
            for (String cardName : game.getTurn().getWaitForFunction().getCardsForActionCardPlay().keySet()) {
                if (game.getPlayer().getHand().get(cardName).equals(game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName)))
                    game.getPlayer().getHand().remove(cardName);
                else if (game.getPlayer().getHand().get(cardName) > game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName))
                    game.getPlayer().getHand().put(cardName, game.getPlayer().getHand().get(cardName) - game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName));
                for (int i = 0; i < game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName); i++)
                    game.getPlayer().getDiscard().add(cardName);
            }
            game.getTurn().getWaitForFunction().clear();
            gameActivity.updateActionButtons();
            game.getPlayer().updateArrayHand();
            gameActivity.getHandAdapter().notifyDataSetChanged();
            game.useAfterPlay(this.getName(), false);
        }
    }

    @Override
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return true;
    }
}
