/**
 * ActionCardPlayingAdapter is the adapter of the RecyclerView in which some
 * cards are appeared as a dialog when some special cards are played.
 */
package com.example.dominion_game.activities;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.Help;

import java.util.ArrayList;

public class ActionCardPlayingAdapter extends RecyclerView.Adapter<ActionCardPlayingAdapter.MyViewHolder> {

    private ArrayList<Pair<String, Boolean>> objects;
    private GameActivity gameActivity;

    /**
     * The constructor
     * @param objects An ArrayList of Pairs of the card name with a boolean which is
     *                true if the card is selected and false if not.
     * @param gameActivity A reference to GameActivity
     */
    public ActionCardPlayingAdapter(ArrayList<Pair<String, Boolean>> objects, GameActivity gameActivity) {
        this.objects = objects;
        this.gameActivity = gameActivity;
    }

    /**
     * A view holder for every item in RecyclerView
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        ImageView ivAction, ivGreenMargin, ivRedMargin, ivYellowMargin, ivGreenX, ivRedX, ivYellowX;
        TextView tvAction;
        ConstraintLayout constraintLayout;

        /**
         * The constructor
         * @param view A View of one item in RecyclerView
         */
        public MyViewHolder(View view) {
            super(view);
            ivAction = view.findViewById(R.id.ivAction);
            ivGreenMargin = view.findViewById(R.id.ivGreenMargin);
            ivRedMargin = view.findViewById(R.id.ivRedMargin);
            ivYellowMargin = view.findViewById(R.id.ivYellowMargin);
            ivGreenX = view.findViewById(R.id.ivGreenX);
            ivRedX = view.findViewById(R.id.ivRedX);
            ivYellowX = view.findViewById(R.id.ivYellowX);
            tvAction = view.findViewById(R.id.tvAction);
            constraintLayout = view.findViewById(R.id.constraintLayout);
            view.setOnLongClickListener(this);
            view.setOnClickListener(this);
        }

        /**
         * A function that handles long press on an item in the RecyclerView
         * The function shows a dialog of the card pressed in big.
         * @param view A View of an item in RecyclerView that was pressed long
         * @return A Boolean which is always true
         */
        @Override
        public boolean onLongClick(View view) {
            ImageView iv = gameActivity.cardDialog.findViewById(R.id.ivAction);
            ImageView resourceIv = view.findViewById(R.id.ivAction);
            iv.setImageResource(Help.nameToCard(resourceIv.getContentDescription().toString()).getImageSource());
            gameActivity.cardDialog.show();
            return true;
        }

        /**
         * A function that handles a press on an item in the RecyclerView
         * The function checks if the card can be selected.
         * @param view A View of an item in RecyclerView that was pressed
         */
        @Override
        public void onClick(View view) {
            if (!gameActivity.game.getTurn().getWaitForFunction().isWaitingForActionCardsDialog())
                return;

            ImageView resourceIv = view.findViewById(R.id.ivAction);
            if (!Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName())
                    .isCardToUse(resourceIv.getContentDescription().toString(), gameActivity.game, gameActivity)
                    || Help.sizeOfHash(gameActivity.game.getTurn().getWaitForFunction().getCardsForActionCardPlay()) == gameActivity.game.getTurn().getWaitForFunction().getMaxAmount())
                return;

            if (gameActivity.game.getTurn().getWaitForFunction().isHandleClickOnCard()
                    && !Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName()).isMarkCardSelectedFromHandWhenHandle()) {
                gameActivity.game.getTurn().getWaitForFunction().updateCardsForDialogByPosition(getAdapterPosition(), true);
                Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName())
                        .handleClickOnHandOrDialog(resourceIv.getContentDescription().toString(), gameActivity.game, gameActivity);
                return;
            }

            if (gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("trash"))
                view.findViewById(R.id.ivRedX).setVisibility(View.VISIBLE);

            if (!gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("trash")
                    && !gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("order"))
                view.findViewById(R.id.ivYellowX).setVisibility(View.VISIBLE);

            if (gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("order")) {
                if (gameActivity.game.getTurn().getWaitForFunction().getCardsForDialog().get(getAdapterPosition()).second) {
                    view.findViewById(R.id.ivYellowMargin).setVisibility(View.INVISIBLE);
                    gameActivity.game.getTurn().getWaitForFunction().updateCardsForDialogByPosition(getAdapterPosition(), false);
                }
                else if (Help.sizeOfHash(gameActivity.game.getTurn().getWaitForFunction().getCardsForActionCardPlay()) == 1) {
                    gameActivity.game.getTurn().getWaitForFunction().updateCardsForDialogByPosition(getAdapterPosition(), true);
                    gameActivity.game.getTurn().getWaitForFunction().order();
                    gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
                }
                else {
                    gameActivity.game.getTurn().getWaitForFunction().updateCardsForDialogByPosition(getAdapterPosition(), true);
                    view.findViewById(R.id.ivYellowMargin).setVisibility(View.VISIBLE);
                }
            }
            else
                gameActivity.game.getTurn().getWaitForFunction().updateCardsForDialogByPosition(getAdapterPosition(), true);

            if (gameActivity.game.getTurn().getWaitForFunction().isHandleClickOnCard()
                    && Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName()).isMarkCardSelectedFromHandWhenHandle()) {
                Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName())
                        .handleClickOnHandOrDialog(resourceIv.getContentDescription().toString(), gameActivity.game, gameActivity);
                return;
            }
            if (gameActivity.game.getTurn().getWaitForFunction().isHasUndoAndConfirm())
                gameActivity.updateActionButtons();
        }
    }

    /**
     * A function that creates the layout for every item in RecyclerView.
     * @param parent
     * @param viewType
     * @return A view for each item in RecyclerView
     */
    @Override
    public ActionCardPlayingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_for_action_cards_playing, parent, false);

        return new MyViewHolder(itemView);
    }

    /**
     * // A function that replaces the contents of every item in RecyclerView.
     * @param holder A view for each item in RecyclerView
     * @param position An Integer of the position of the view in the ArrayList
     */
    @Override
    public void onBindViewHolder(ActionCardPlayingAdapter.MyViewHolder holder, int position) {
        holder.ivAction.setImageResource(Help.nameToCard(this.objects.get(position).first).getImageSource());
        holder.ivAction.setContentDescription(this.objects.get(position).first);
        holder.constraintLayout.setBackgroundColor(gameActivity.getResources().getColor(R.color.white));
        holder.constraintLayout.getBackground().setAlpha(150);

        if (!gameActivity.game.getTurn().getWaitForFunction().isWaitingForActionCardsDialog()
                || !Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName())
                .isCardToUse(this.objects.get(position).first, gameActivity.game, gameActivity)
                || gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("")) {
            holder.ivRedMargin.setVisibility(View.INVISIBLE);
            holder.ivRedX.setVisibility(View.INVISIBLE);
            holder.ivYellowMargin.setVisibility(View.INVISIBLE);
            holder.ivYellowX.setVisibility(View.INVISIBLE);
            holder.ivGreenMargin.setVisibility(View.INVISIBLE);
            return;
        }
        if (gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("trash")) {
            holder.ivRedMargin.setVisibility(View.VISIBLE);
            if (this.objects.get(position).second)
                holder.ivRedX.setVisibility(View.VISIBLE);
            else
                holder.ivRedX.setVisibility(View.INVISIBLE);
        }
        else {
            holder.ivRedMargin.setVisibility(View.INVISIBLE);
            holder.ivRedX.setVisibility(View.INVISIBLE);
        }

        if (gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("use"))
            holder.ivGreenMargin.setVisibility(View.VISIBLE);
        else
            holder.ivGreenMargin.setVisibility(View.INVISIBLE);

        if (gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("order")) {
            if (position == 0)
                holder.tvAction.setText("Bottom");
            else if (position == this.getItemCount() - 1)
                holder.tvAction.setText("Top");
            else
                holder.tvAction.setText(String.valueOf(this.getItemCount() - position));
            holder.tvAction.setVisibility(View.VISIBLE);
            // holder.tvAction.setTypeface(null, Typeface.BOLD);
        }
        else
            holder.tvAction.setVisibility(View.GONE);

        if (!gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("trash")
                && !gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("use")) {
            if (!gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("order")
                    || this.objects.get(position).second)
                holder.ivYellowMargin.setVisibility(View.VISIBLE);
            else
                holder.ivYellowMargin.setVisibility(View.INVISIBLE);

            if (!gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("order"))
                if (this.objects.get(position).second)
                    holder.ivYellowX.setVisibility(View.VISIBLE);
                else
                    holder.ivYellowX.setVisibility(View.INVISIBLE);
        }
        else {
            holder.ivYellowMargin.setVisibility(View.INVISIBLE);
            holder.ivYellowX.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @return An Integer of the size of the ArrayList of objects
     */
    @Override
    public int getItemCount() {
        return this.objects.size();
    }
}
