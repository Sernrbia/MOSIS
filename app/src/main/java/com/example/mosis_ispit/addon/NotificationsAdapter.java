package com.example.mosis_ispit.addon;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mosis_ispit.R;

import java.util.ArrayList;

public class NotificationsAdapter extends ArrayAdapter<String> {
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mNotifications;
    private int  mViewResourceId;

    public NotificationsAdapter(Context context, int notifId, ArrayList<String> notifications){
        super(context, notifId,notifications);
        this.mNotifications = notifications;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = notifId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        String notification = mNotifications.get(position);

        if (notification != null) {
            TextView deviceName = (TextView) convertView.findViewById(R.id.notificationId);

            if (deviceName != null) {
                deviceName.setText(notification);
            }
        }

        return convertView;
    }
}
