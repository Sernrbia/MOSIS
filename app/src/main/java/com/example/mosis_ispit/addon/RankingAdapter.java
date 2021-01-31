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

public class RankingAdapter extends ArrayAdapter<UserForComparison> {
    private final LayoutInflater mLayoutInflater;
    private final ArrayList<UserForComparison> mUsers;
    private final int  mViewResourceId;

    public RankingAdapter(Context context, int userResourceId, ArrayList<UserForComparison> users) {
        super(context, userResourceId, users);
        this.mUsers = users;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = userResourceId;
    }

    @SuppressLint({"SetTextI18n", "ViewHolder"})
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        UserForComparison user = mUsers.get(position);

        if (user != null) {
            TextView userName = (TextView) convertView.findViewById(R.id.userUsernameHistory);

            if (userName != null) {
                userName.setText((mUsers.indexOf(user) + 1) + ". " + user);
            }
        }

        return convertView;
    }

}
