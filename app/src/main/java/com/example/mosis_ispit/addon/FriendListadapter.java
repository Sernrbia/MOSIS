package com.example.mosis_ispit.addon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mosis_ispit.R;

import java.util.ArrayList;

public class FriendListadapter extends ArrayAdapter<String> {
    private final LayoutInflater mLayoutInflater;
    private final ArrayList<String> mFriends;
    private final int  mViewResourceId;

    public FriendListadapter(Context context, int friendId, ArrayList<String> friend){
        super(context, friendId, friend);
        this.mFriends = friend;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = friendId;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        String friend = mFriends.get(position);

        if (friend != null) {
            TextView userName = (TextView) convertView.findViewById(R.id.friendUsername);

            if (userName != null) {
                userName.setText(friend);
            }
        }

        return convertView;
    }
}
