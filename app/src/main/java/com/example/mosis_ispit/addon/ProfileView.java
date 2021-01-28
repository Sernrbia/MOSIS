package com.example.mosis_ispit.addon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mosis_ispit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileView extends AppCompatActivity {
    static int NEW_PLACE;
    public static final long MB=1024*1024;
    private ImageView avatar;
    private TextView username, email, friends_number, points;
    private Button close;
    StorageReference storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_info_layout);

        storage= FirebaseStorage.getInstance().getReference();
        
        avatar = findViewById(R.id.profile_view_avatar);
        username = findViewById(R.id.profile_view_username);
        email = findViewById(R.id.profile_view_email);
        friends_number = findViewById(R.id.profile_view_friends_count);
        points = findViewById(R.id.profile_view_points);
        close = findViewById(R.id.profile_view_close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        User user = null;
        try{
            Intent listIntent = getIntent();
            Bundle dataBundle = listIntent.getExtras();
            user = (User) dataBundle.get("user");
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            finish();
        }
        if(user != null){
            storage.child("users").child(user.UID).child("profile").getBytes(10*MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    byte[] data = task.getResult();
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    avatar.setImageBitmap(Bitmap.createScaledBitmap(bmp, avatar.getWidth(),
                            avatar.getHeight(), false));
                }
            });
            username.setText(user.getUsername());
            email.setText(user.getEmail());
            friends_number.setText(user.friends.size());
            points.setText(user.getPoints());
        }

    }
}
