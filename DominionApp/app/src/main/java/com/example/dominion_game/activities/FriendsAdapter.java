package com.example.dominion_game.activities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.Friend;

import java.util.List;


public class FriendsAdapter extends ArrayAdapter<Friend> {

    Context context;
    List<Friend> data;

    public FriendsAdapter(Context context, int resource, int textViewResourceId, List<Friend> data) {
        super(context, resource, textViewResourceId, data);

        this.context=context;
        this.data=data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.friend_layout, parent, false);
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        ImageView ivFriends = (ImageView) view.findViewById(R.id.ivFriends);
        TextView btnFriends = (TextView) view.findViewById(R.id.btnFriends);
        Friend temp = data.get(position);

        if (data.get(position).areFriends())
            ivFriends.setImageResource(R.drawable.friends);
        else
            ivFriends.setImageResource(R.drawable.halffriends1);
        else if ()
            ivFriends.setImageResource(R.drawable.halffriends1);
        tvName.setText(temp.getName());
        tv.setText(temp.getDesc());
        return view;
        */
        return ((Activity) context).getLayoutInflater().inflate(R.layout.friend_layout, parent, false);
    }
}
