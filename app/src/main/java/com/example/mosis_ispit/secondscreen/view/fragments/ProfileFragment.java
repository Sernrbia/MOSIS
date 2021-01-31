package com.example.mosis_ispit.secondscreen.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.Animation;
import com.example.mosis_ispit.addon.User;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private Fragment selectedFragment;
    private Button info;
    private Button history;
    private Button friends;
    private FrameLayout content;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_layout, container, false);
        info = view.findViewById(R.id.profile_info);
        history = view.findViewById(R.id.profile_history);
        friends = view.findViewById(R.id.profile_friends);
        content = view.findViewById(R.id.profile_placeholder);
        
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bitmap bmp = (Bitmap) this.getArguments().getParcelable("image");
        String username = (String) this.getArguments().getString("username");
        String fullname = (String) this.getArguments().getString("fullname");
        String email = (String) this.getArguments().getString("email");
        String rank = (String) this.getArguments().getString("rank");
        String points = (String) this.getArguments().getString("points");
        String fr = (String) this.getArguments().getString("friends");
        String di = (String) this.getArguments().getString("discussions");

        Bundle b = new Bundle();
        b.putParcelable("image", bmp);
        b.putString("username", username);
        b.putString("fullname", fullname);
        b.putString("email", email);
        b.putString("rank", rank);
        b.putString("points", points);
        b.putString("friends", fr);
        b.putString("discussions", di);

        info.setOnClickListener(v -> {
            selectedFragment = new ProfileInfo();
            selectedFragment.setArguments(b);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.profile_placeholder, selectedFragment).commit();
        });

        history.setOnClickListener(v -> {
            selectedFragment = new ProfileHistory();
            selectedFragment.setArguments(b);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.profile_placeholder, selectedFragment).commit();
        });

        friends.setOnClickListener(v -> {
            selectedFragment = new ProfileFriends();
            selectedFragment.setArguments(b);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.profile_placeholder, selectedFragment).commit();
        });
        if (selectedFragment == null) {
            selectedFragment = new ProfileInfo();
        }
        selectedFragment.setArguments(b);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.profile_placeholder, selectedFragment).commit();
    }
}
