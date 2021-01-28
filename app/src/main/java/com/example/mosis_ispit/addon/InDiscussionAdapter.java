package com.example.mosis_ispit.addon;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mosis_ispit.R;

import java.util.ArrayList;

public class InDiscussionAdapter extends ArrayAdapter<String> {
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mUsers;
    private int  mViewResourceId;

    public InDiscussionAdapter(Context context, int userId, ArrayList<String> users){
        super(context, userId, users);
        this.mUsers = users;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = userId;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        String user = mUsers.get(position);

        if (user != null) {
            TextView userName = (TextView) convertView.findViewById(R.id.userUsername);

            if (userName != null) {
                userName.setText(user);
            }
        }

        return convertView;
    }
}
