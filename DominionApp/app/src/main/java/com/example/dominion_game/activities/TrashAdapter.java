/**
 * TrashAdapter is the adapter of the RecyclerView of trash of the game.
 * The adapter updates the cards shown according to the trash of the game.
 * */
package com.example.dominion_game.activities;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.Help;

import java.util.ArrayList;

public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.MyViewHolder> {

    private ArrayList<Pair<String, Integer>> objects;
    private GameActivity gameActivity;

    /**
     * The constructor
     * @param objects An ArrayList of Pairs of the card name with the count of this card in trash.
     * @param gameActivity A reference to GameActivity
     */
    public TrashAdapter(ArrayList<Pair<String, Integer>> objects, GameActivity gameActivity) {
        this.objects = objects;
        this.gameActivity = gameActivity;
    }

    /**
     * A view holder for every item in RecyclerView
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        ImageView ivAction;
        TextView countAction;

        /**
         * The constructor
         * @param view A View of one item in RecyclerView
         */
        public MyViewHolder(View view) {
            super(view);
            view.setOnLongClickListener(this);
            ivAction = view.findViewById(R.id.ivAction);
            countAction = view.findViewById(R.id.countAction);
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
    }

    /**
     * A function that creates the layout for every item in RecyclerView.
     * @param parent
     * @param viewType
     * @return A view for each item in RecyclerView
     */
    @Override
    public TrashAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_trash, parent, false);
        itemView.setPadding(20, 20, 20, 20);

        return new MyViewHolder(itemView);
    }

    /**
     * // A function that replaces the contents of every item in RecyclerView.
     * @param holder A view for each item in RecyclerView
     * @param position An Integer of the position of the view in the ArrayList
     */
    @Override
    public void onBindViewHolder(TrashAdapter.MyViewHolder holder, int position) {
        holder.ivAction.setImageResource(Help.nameToCard(this.objects.get(position).first).getShortImageSource());
        holder.countAction.setText(String.valueOf(this.objects.get(position).second));
        holder.ivAction.setContentDescription(this.objects.get(position).first);
    }

    /**
     * @return An Integer of the size of the ArrayList
     */
    @Override
    public int getItemCount() {
        return this.objects.size();
    }
}
