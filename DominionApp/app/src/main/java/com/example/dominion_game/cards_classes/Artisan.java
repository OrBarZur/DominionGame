package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class Artisan extends Card {
    public Artisan() {
        super("Artisan", 6, R.mipmap.artisan, R.mipmap.artisan_sh, "action");
    }

    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        gameActivity.waitForBoard(this.getName(), 1, 1);
    }

    @Override
    public void clickOnBoard(String cardName, GameManager game, GameActivity gameActivity) {
        game.getPlayer().addToHand(game.getCard(cardName));
        game.getTurn().getWaitForFunction().clear();
        gameActivity.updateCards(false);

        gameActivity.waitForHand(this.getName(), 1, 1, "topdeck", false);
        gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), gameActivity.getString(R.string.confirm_top_deck)}, true);
        gameActivity.turnUI();
        game.getPlayer().updateArrayHand();
        gameActivity.getHandAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean isCardToGetFromBoard(String cardName, GameManager game, GameActivity gameActivity) {
        return Help.nameToCard(cardName).getPrice() <= 5;
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        if (buttonText.equals(gameActivity.getString(R.string.undo))) {
            game.getTurn().getWaitForFunction().undo();
            gameActivity.updateActionButtons();
            gameActivity.getHandAdapter().notifyDataSetChanged();
        }
        else if (buttonText.equals(gameActivity.getString(R.string.confirm_top_deck))) {
            String cardName = String.valueOf(game.getTurn().getWaitForFunction().getCardsForActionCardPlay().keySet().toArray()[0]);
            game.getPlayer().handToDeck(cardName, gameActivity);
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
