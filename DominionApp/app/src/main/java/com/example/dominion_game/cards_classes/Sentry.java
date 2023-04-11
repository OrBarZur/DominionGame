package com.example.dominion_game.cards_classes;

import android.view.View;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

import java.util.ArrayList;

public class Sentry extends Card {
    public Sentry() {
        super("Sentry", 5, R.mipmap.sentry, R.mipmap.sentry_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(1, gameActivity, game, game.getTabs());
        game.getTurn().addActions(1);
        ArrayList<String> cards = game.getPlayer().takeCards(2, game, game.getLastLineFromLog().getTabs());
        if (cards.size() == 0) {
            game.useAfterPlay(this.getName(), false);
            return;
        }

        game.getTurn().getWaitForFunction().handleWaitingForActionCardsDialog(this.getName(), 0, 2, "trash", false, cards);
        gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
        gameActivity.setVisibilityForRVActionCardsPlaying(View.VISIBLE);
        gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), gameActivity.getString(R.string.confirm_trash)}, true);
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        if (buttonText.equals(gameActivity.getString(R.string.undo))) {
            game.getTurn().getWaitForFunction().undo();
            gameActivity.updateActionButtons();
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
        }
        else if (buttonText.equals(gameActivity.getString(R.string.confirm_trash))) {
            ArrayList<String> cardSelected = game.getTurn().getWaitForFunction().cardsSelectedForDialog();
            for (String cardName : cardSelected)
                game.addToTrash(cardName, 1);

            ArrayList<String> cardsLeft = game.getTurn().getWaitForFunction().cardsLeftForDialog();
            game.getTurn().getWaitForFunction().clear();
            gameActivity.updateActionButtons();
            if (cardsLeft.size() == 0) {
                game.getPlayer().updateArrayHand();
                gameActivity.getHandAdapter().notifyDataSetChanged();
                gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
                gameActivity.setVisibilityForRVActionCardsPlaying(View.INVISIBLE);
                game.useAfterPlay(this.getName(), false);
                return;
            }
            game.getTurn().getWaitForFunction().handleWaitingForActionCardsDialog(this.getName(), 0, cardsLeft.size(), "discard", false, cardsLeft);
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
            gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), gameActivity.getString(R.string.confirm_discard)}, true);
        }

        else if (buttonText.equals(gameActivity.getString(R.string.confirm_discard))) {
            ArrayList<String> cardSelected = game.getTurn().getWaitForFunction().cardsSelectedForDialog();
            for (String cardName : cardSelected)
                game.getPlayer().addToDiscard(cardName);

            ArrayList<String> cardsLeft = game.getTurn().getWaitForFunction().cardsLeftForDialog();
            game.getTurn().getWaitForFunction().clear();
            gameActivity.updateActionButtons();
            if (cardsLeft.size() != 2 || cardsLeft.get(0).equals(cardsLeft.get(1))) {
                game.getPlayer().putArrayInDeck(Help.arrayListOfPairsToArrayList(game.getTurn().getWaitForFunction().getCardsForDialog()));
                game.getPlayer().updateArrayHand();
                gameActivity.getHandAdapter().notifyDataSetChanged();
                gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
                gameActivity.setVisibilityForRVActionCardsPlaying(View.INVISIBLE);
                game.useAfterPlay(this.getName(), false);
                return;
            }
            game.getTurn().getWaitForFunction().handleWaitingForActionCardsDialog(this.getName(), 0, 2, "order", false, cardsLeft);
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
            gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.order)}, false);
            gameActivity.setVisibilityForAction(0, View.VISIBLE);
        }
        else if (buttonText.equals(gameActivity.getString(R.string.order))) {
            game.getPlayer().putArrayInDeck(Help.arrayListOfPairsToArrayList(game.getTurn().getWaitForFunction().getCardsForDialog()));
            game.getTurn().getWaitForFunction().clear();
            gameActivity.invisibleButtons(1);
            gameActivity.turnUI();
            game.getPlayer().updateArrayHand();
            gameActivity.getHandAdapter().notifyDataSetChanged();
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
            gameActivity.setVisibilityForRVActionCardsPlaying(View.INVISIBLE);
            game.useAfterPlay(this.getName(), false);
        }
    }

    @Override
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return true;
    }
}
