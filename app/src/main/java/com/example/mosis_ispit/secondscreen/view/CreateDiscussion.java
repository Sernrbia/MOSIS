package com.example.mosis_ispit.secondscreen.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mosis_ispit.R;

public class CreateDiscussion extends AppCompatActivity {

    private EditText topic, description, size;
    private Spinner type;
    private Button create;
    private String selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_discussion_layout);

        topic = findViewById(R.id.create_discussion_layout_topic);
        description = findViewById(R.id.create_discussion_layout_description);
        size = findViewById(R.id.create_discussion_layout_size);
        create = findViewById(R.id.create_discussion_layout_create);
        type = findViewById(R.id.create_discussion_layout_type);
        Button close = findViewById(R.id.create_discussion_layout_close);

        String[] items = new String[]{"art", "sport", "politics"};
        selected = "art";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        type.setAdapter(adapter);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            data.putExtra("type", selected);
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
