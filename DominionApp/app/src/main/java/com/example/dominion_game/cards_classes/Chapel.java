package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class Chapel extends Card {
    public Chapel() {
        super("Chapel", 2, R.mipmap.chapel, R.mipmap.chapel_sh, "action");
    }

    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        if (Help.sizeOfHash(game.getPlayer().getHand()) > 0) {
            gameActivity.waitForHand(this.getName(), 0, 4, "trash", false);
            gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), gameActivity.getString(R.string.confirm_trash)}, true);
        }
        else
            game.useAfterPlay(this.getName(), false);
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        if (buttonText.equals(gameActivity.getString(R.string.undo))) {
            game.getTurn().getWaitForFunction().undo();
            gameActivity.updateActionButtons();
            gameActivity.getHandAdapter().notifyDataSetChanged();
        }
        else if (buttonText.equals(gameActivity.getString(R.string.confirm_trash))) {
            for (String cardName : game.getTurn().getWaitForFunction().getCardsForActionCardPlay().keySet()) {
                if (game.getPlayer().getHand().get(cardName).equals(game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName)))
                    game.getPlayer().getHand().remove(cardName);
                else if (game.getPlayer().getHand().get(cardName) > game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName))
                    game.getPlayer().getHand().put(cardName, game.getPlayer().getHand().get(cardName) - game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName));
                game.addToTrash(cardName, game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName));
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
