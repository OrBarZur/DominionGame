package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class Remodel extends Card {
    public Remodel() {
        super("Remodel", 4, R.mipmap.remodel, R.mipmap.remodel_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        gameActivity.waitForHand(this.getName(), 1, 1, "trash", false);
        gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), gameActivity.getString(R.string.confirm_trash)}, true);
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        if (buttonText.equals(gameActivity.getString(R.string.undo))) {
            game.getTurn().getWaitForFunction().undo();
            gameActivity.updateActionButtons();
            gameActivity.getHandAdapter().notifyDataSetChanged();
        }
        else if (buttonText.equals(gameActivity.getString(R.string.confirm_trash))) {
            String cardName = String.valueOf(game.getTurn().getWaitForFunction().getCardsForActionCardPlay().keySet().toArray()[0]);
            if (game.getPlayer().getHand().get(cardName).equals(game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName)))
                game.getPlayer().getHand().remove(cardName);
            else if (game.getPlayer().getHand().get(cardName) > game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName))
                game.getPlayer().getHand().put(cardName, game.getPlayer().getHand().get(cardName) - game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName));
            game.addToTrash(cardName, game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName));
            game.getTurn().getWaitForFunction().clear();

            gameActivity.waitForBoard(this.getName(), 1, 1);
            game.getTurn().getWaitForFunction().setMaxPriceForGain(Help.nameToCard(cardName).getPrice() + 2);
            gameActivity.updateActionButtons();
            game.getPlayer().updateArrayHand();
            gameActivity.getHandAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return true;
    }

    @Override
    public void clickOnBoard(String cardName, GameManager game, GameActivity gameActivity) {
        game.getPlayer().addToDiscard(game.getCard(cardName));
        game.getTurn().getWaitForFunction().clear();
        gameActivity.turnUI();
        game.getPlayer().updateArrayHand();
        gameActivity.getHandAdapter().notifyDataSetChanged();
        game.useAfterPlay(this.getName(), false);
    }

    @Override
    public boolean isCardToGetFromBoard(String cardName, GameManager game, GameActivity gameActivity) {
        return Help.nameToCard(cardName).getPrice() <= game.getTurn().getWaitForFunction().getMaxPriceForGain();
    }
}
