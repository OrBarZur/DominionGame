package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class Workshop extends Card {
    public Workshop() {
        super("Workshop", 3, R.mipmap.workshop, R.mipmap.workshop_sh, "action");
    }

    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        gameActivity.waitForBoard(this.getName(), 1, 1);
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
        return Help.nameToCard(cardName).getPrice() <= 4;
    }
}
