package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class Militia extends Card {
    public Militia() {
        super("Militia", 4, R.mipmap.militia, R.mipmap.militia_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getTurn().addTreasure(2);
        game.useAfterPlay(this.getName(), true);
    }

    @Override
    public void attack(GameManager game, GameActivity gameActivity) {
        if (Help.sizeOfHash(game.getPlayer().getHand()) > 3) {
            gameActivity.waitForHand(this.getName(), Help.sizeOfHash(game.getPlayer().getHand()) - 3,
                    Help.sizeOfHash(game.getPlayer().getHand()) - 3, "discard", false);
            gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), gameActivity.getString(R.string.confirm_discard)}, true);
        }
        else
            game.setDoneAttack(true);
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
            game.setDoneAttack(true);
        }
    }

    @Override
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return true;
    }
}
