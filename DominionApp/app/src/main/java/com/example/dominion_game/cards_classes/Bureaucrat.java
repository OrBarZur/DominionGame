package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class Bureaucrat extends Card {
    public Bureaucrat() {
        super("Bureaucrat", 4, R.mipmap.bureaucrat, R.mipmap.bureaucrat_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().addToDeck(game.getCard("Silver"));
        game.useAfterPlay(this.getName(), true);
    }

    @Override
    public void attack(GameManager game, GameActivity gameActivity) {
        if (game.getPlayer().containsTypeCards("victory")) {
            gameActivity.waitForHand(this.getName(), 1, 1, "topdeck", false);
            gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), gameActivity.getString(R.string.confirm_top_deck)}, true);
        }
        else {
            game.addHashToLog(game.getPlayer().getHand(), "in action", "Reveals hand:");
            game.setDoneAttack(true);
        }
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
            game.setDoneAttack(true);
        }
    }

    @Override
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return Help.nameToCard(cardName).getType().equals("victory");
    }
}
