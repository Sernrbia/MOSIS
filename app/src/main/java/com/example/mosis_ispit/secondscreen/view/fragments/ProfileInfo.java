package com.example.mosis_ispit.secondscreen.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class ProfileInfo extends Fragment {
    static final int REQUEST_IMAGE_SELECT = 1;
    private TextView username, email, fullname, rank, points;
    private ImageView avatar;
    FirebaseAuth auth;
    private ProfileInfoListener listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_view_layout, container, false);

        username = view.findViewById(R.id.profile_info_name);
        email = view.findViewById(R.id.profile_info_email);
        fullname = view.findViewById(R.id.profile_info_fullname);
        rank = view.findViewById(R.id.profile_info_rankinfo);
        avatar = view.findViewById(R.id.profile_info_avatar);
        points = view.findViewById(R.id.profile_info_pointsinfo);
        Button logout = view.findViewById(R.id.profile_info_logout);
        logout.setOnClickListener(s -> {
            listener.onLogout();
        });

        Bitmap bmp = (Bitmap) this.getArguments().getParcelable("image");
        auth=FirebaseAuth.getInstance();

        String user = (String) this.getArguments().getString("username");
        String full = (String) this.getArguments().getString("fullname");
        String emai = (String) this.getArguments().getString("email");
        String ra = (String) this.getArguments().getString("rank");
        String po = (String) this.getArguments().getString("points");
        avatar.post(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        username.setText(user);
                        fullname.setText(full);
                        email.setText(emai);
                        rank.setText(ra);
                        points.setText(po);
                        avatar.setImageBitmap(Bitmap.createScaledBitmap(bmp, avatar.getWidth(), avatar.getHeight(), false));
                    }
                });
            }
        });
        return view;
    }

    // interface definition
    public interface ProfileInfoListener {
        void onLogout();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if(context instanceof ProfileInfoListener) {
            listener = (ProfileInfoListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
