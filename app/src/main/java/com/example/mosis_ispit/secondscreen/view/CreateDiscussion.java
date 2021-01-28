package com.example.mosis_ispit.secondscreen.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

public class CreateDiscussion extends AppCompatActivity {

    private EditText topic, description, size;
    private Button pub, priv, create, close;
    private Boolean type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_discussion_layout);
        type = false;

        topic = findViewById(R.id.create_discussion_layout_topic);
        description = findViewById(R.id.create_discussion_layout_description);
        size = findViewById(R.id.create_discussion_layout_size);
        pub = findViewById(R.id.create_discussion_layout_public);
        priv = findViewById(R.id.create_discussion_layout_private);
        create = findViewById(R.id.create_discussion_layout_create);
        close = findViewById(R.id.create_discussion_layout_close);

        topic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                create.setEnabled(!topic.getText().toString().isEmpty() && !description.getText().toString().isEmpty() && !size.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                create.setEnabled(!topic.getText().toString().isEmpty() && !description.getText().toString().isEmpty() && !size.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        size.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                create.setEnabled(!topic.getText().toString().isEmpty() && !description.getText().toString().isEmpty() && !size.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priv.setBackgroundColor(Color.parseColor("#F44336"));
                pub.setBackgroundColor(Color.parseColor("#4CAF50"));
                type = false;
            }
        });

        priv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pub.setBackgroundColor(Color.parseColor("#F44336"));
                priv.setBackgroundColor(Color.parseColor("#4CAF50"));
                type = true;
            }
        });

        create.setEnabled(false);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("topic", topic.getText().toString().trim());
                data.putExtra("description", description.getText().toString().trim());
                data.putExtra("type", String.valueOf(type));
                data.putExtra("size", size.getText().toString().trim());
//                String text = "Hello world!";
//                String text = topic.getText().toString().trim();
                //---set the data to pass back---
//                data.setData(Uri.parse(text));
                setResult(RESULT_OK, data);
                //---close the activity---
                finish();

//                listener.listeneronCreate(topic.getText().toString().trim(), description.getText().toString().trim(), type);
//                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
