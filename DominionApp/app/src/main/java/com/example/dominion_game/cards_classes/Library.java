package com.example.dominion_game.cards_classes;

import android.view.View;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

public class Library extends Card {
    public Library() {
        super("Library", 5, R.mipmap.library, R.mipmap.library_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        if (Help.sizeOfHash(game.getPlayer().getHand()) >= 7) {
            game.useAfterPlay(this.getName(), false);
            return;
        }

        game.getTurn().getWaitForFunction().handleWaitingForButtonsOnly(this.getName());
        this.takeCardsWhileNotAction(game, gameActivity);
    }

    public void takeCardsWhileNotAction(GameManager game, GameActivity gameActivity) {
        while (Help.sizeOfHash(game.getPlayer().getHand()) < 7 && !(game.getPlayer().getDiscard().isEmpty() && game.getPlayer().getDeck().isEmpty())) {
            if (game.getPlayer().getDeck().isEmpty())
                game.getPlayer().discardToDeck(game, game.getTabs());
            if (game.getPlayer().getHand().containsKey(game.getPlayer().getDeck().get(game.getPlayer().getDeck().size() - 1)))
                game.getPlayer().getHand().put(game.getPlayer().getDeck().get(game.getPlayer().getDeck().size() - 1),
                        game.getPlayer().getHand().get(game.getPlayer().getDeck().get(game.getPlayer().getDeck().size() - 1)) + 1);
            else
                game.getPlayer().getHand().put(game.getPlayer().getDeck().get(game.getPlayer().getDeck().size() - 1), 1);
            // removes the last index which is the first card to take from deck
            String cardName = game.getPlayer().getDeck().remove(game.getPlayer().getDeck().size() - 1);
            game.getPlayer().updateArrayHand();
            gameActivity.getHandAdapter().notifyDataSetChanged();
            if (Help.nameToCard(cardName).getType().equals("action")) {
                gameActivity.uploadActionButtons(new String[]{"Keep " + Help.nameToCard(cardName).getNameToDisplay(), "Skip It"}, false);
                gameActivity.setVisibilityForAction(0, View.VISIBLE);
                gameActivity.setVisibilityForAction(1, View.VISIBLE);
                game.getTurn().getWaitForFunction().insertCardSelectedInHand(cardName);
                return;
            }
        }
        game.getPlayer().putArrayInDiscard(Help.arrayListOfPairsToArrayList(game.getTurn().getWaitForFunction().getCardsForDialog()));
        game.getTurn().getWaitForFunction().clear();
        gameActivity.invisibleButtons(2);
        gameActivity.turnUI();
        game.getPlayer().updateArrayHand();
        gameActivity.getHandAdapter().notifyDataSetChanged();
        gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
        gameActivity.setVisibilityForRVActionCardsPlaying(View.INVISIBLE);
        game.useAfterPlay(this.getName(), false);
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        if (buttonText.equals("Skip It")) {
            String cardName = String.valueOf(game.getTurn().getWaitForFunction().getCardsForActionCardPlay().keySet().toArray()[0]);
            if (!game.getPlayer().removeFromHand(cardName)) {
                game.getTurn().getWaitForFunction().undo();
                this.takeCardsWhileNotAction(game, gameActivity);
                return;
            }

            game.getTurn().getWaitForFunction().undo();
            game.getTurn().getWaitForFunction().addToCardsForDialog(cardName);
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
            gameActivity.setVisibilityForRVActionCardsPlaying(View.VISIBLE);
        }
        this.takeCardsWhileNotAction(game, gameActivity);
    }
}
