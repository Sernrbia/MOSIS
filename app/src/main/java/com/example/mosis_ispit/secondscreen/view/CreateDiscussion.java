package com.example.mosis_ispit.secondscreen.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mosis_ispit.R;

public class CreateDiscussion extends AppCompatActivity {

    private EditText topic, description, size;
    private Button create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_discussion_layout);

        topic = findViewById(R.id.create_discussion_layout_topic);
        description = findViewById(R.id.create_discussion_layout_description);
        size = findViewById(R.id.create_discussion_layout_size);
        create = findViewById(R.id.create_discussion_layout_create);
        Button close = findViewById(R.id.create_discussion_layout_close);

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

        create.setEnabled(false);

        create.setOnClickListener(v -> {
            Intent data = new Intent();
            data.putExtra("topic", topic.getText().toString().trim());
            data.putExtra("description", description.getText().toString().trim());
            data.putExtra("size", size.getText().toString().trim());
            setResult(RESULT_OK, data);
            finish();
        });

        close.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
