package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class Mine extends Card {
    public Mine() {
        super("Mine", 5, R.mipmap.mine, R.mipmap.mine_sh, "action");
    }
    @Override

    public void play(GameManager game, GameActivity gameActivity) {
        if (game.getPlayer().containsTypeCards("treasure")) {
            gameActivity.waitForHand(this.getName(), 0, 1, "trash", false);
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
            if (game.getTurn().getWaitForFunction().getCardsForActionCardPlay().size() == 0) {
                game.getTurn().getWaitForFunction().clear();
                gameActivity.updateActionButtons();
                game.getPlayer().updateArrayHand();
                gameActivity.getHandAdapter().notifyDataSetChanged();
                game.useAfterPlay(this.getName(), false);
                return;
            }
            String cardName = String.valueOf(game.getTurn().getWaitForFunction().getCardsForActionCardPlay().keySet().toArray()[0]);
            if (game.getPlayer().getHand().get(cardName).equals(game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName)))
                game.getPlayer().getHand().remove(cardName);
            else if (game.getPlayer().getHand().get(cardName) > game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName))
                game.getPlayer().getHand().put(cardName, game.getPlayer().getHand().get(cardName) - game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName));
            game.addToTrash(cardName, game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(cardName));
            game.getTurn().getWaitForFunction().clear();

            gameActivity.waitForBoard(this.getName(), 1, 1);
            game.getTurn().getWaitForFunction().setMaxPriceForGain(Help.nameToCard(cardName).getPrice() + 3);
            gameActivity.updateActionButtons();
            game.getPlayer().updateArrayHand();
            gameActivity.getHandAdapter().notifyDataSetChanged();
            game.useAfterPlay(this.getName(), false);
        }
    }

    @Override
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return Help.nameToCard(cardName).getType().equals("treasure");
    }

    @Override
    public void clickOnBoard(String cardName, GameManager game, GameActivity gameActivity) {
        if (!game.getPlayer().addToHand(game.getCard(cardName))) {
            game.getTurn().getWaitForFunction().getCardsForActionCardPlay().clear();
            return;
        }
        game.getTurn().getWaitForFunction().clear();
        gameActivity.turnUI();
        game.getPlayer().updateArrayHand();
        gameActivity.getHandAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean isCardToGetFromBoard(String cardName, GameManager game, GameActivity gameActivity) {
        return Help.nameToCard(cardName).getPrice() <= game.getTurn().getWaitForFunction().getMaxPriceForGain()
                && Help.nameToCard(cardName).getType().equals("treasure");
    }
}
