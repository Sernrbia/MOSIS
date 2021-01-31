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

public class DiscussionHistoryAdapter extends ArrayAdapter<String> {
    private final LayoutInflater mLayoutInflater;
    private final ArrayList<String> mDiscussions;
    private final int  mViewResourceId;

    public DiscussionHistoryAdapter(Context context, int discussionId, ArrayList<String> discussion){
        super(context, discussionId, discussion);
        this.mDiscussions = discussion;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = discussionId;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        String discussion = mDiscussions.get(position);

        if (discussion != null) {
            TextView userName = (TextView) convertView.findViewById(R.id.userUsernameHistory);

            if (userName != null) {
                userName.setText(discussion);
            }
        }

        return convertView;
    }
}
