package com.example.mosis_ispit.secondscreen.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.DiscussionPosition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText searchbar, range;
    private Spinner type, typeSelect;
    private Button exit;
    private ListView list;
    private String selected;
    private String selected2;
    private ArrayList<DiscussionPosition> discussionsLocations;
    private ValueEventListener discussionsLocationsListener;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchbar = findViewById(R.id.search_searchbar);
        range = findViewById(R.id.search_activity_range);
        type = findViewById(R.id.search_type);
        typeSelect = findViewById(R.id.search_type_selector);
        exit = findViewById(R.id.search_done);
        list = findViewById(R.id.search_list);

        latitude = getIntent().getDoubleExtra("latitude", 43.3209);
        longitude = getIntent().getDoubleExtra("longitude", 21.8958);
        discussionsLocations = new ArrayList<>();
        discussionsLocationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                discussionsLocations.clear();

                for (DataSnapshot us : snapshot.getChildren()) {
                    DiscussionPosition up = us.getValue(DiscussionPosition.class);
                    discussionsLocations.add(up);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference().child("discussionsLocations").addValueEventListener(discussionsLocationsListener);

        String[] items = new String[]{"topic", "type", "range"};
        selected = "topic";
        searchbar.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        type.setAdapter(adapter);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = items[position];
                if (selected.equals("topic")) {
                    typeSelect.setVisibility(View.INVISIBLE);
                    range.setVisibility(View.INVISIBLE);
                    searchbar.setVisibility(View.VISIBLE);
                } else if (selected.equals("type")) {
                    searchbar.setVisibility(View.INVISIBLE);
                    range.setVisibility(View.INVISIBLE);
                    typeSelect.setVisibility(View.VISIBLE);
                } else {
                    searchbar.setVisibility(View.INVISIBLE);
                    typeSelect.setVisibility(View.INVISIBLE);
                    range.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] items2 = new String[]{"art", "sport", "politics"};
        selected2 = "art";

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        typeSelect.setAdapter(adapter2);

        typeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected2 = items2[position];
                if (selected.equals("type")) {
                    searchByType();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selected.equals("topic")) {
                    searchByTopic(searchbar.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        range.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selected.equals("range") && !range.getText().toString().trim().equals("")) {
                    searchByRange(Double.parseDouble(range.getText().toString().trim()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        exit.setOnClickListener(v -> {
            finish();
        });
    }

    private void searchByTopic(String topic) {
        ArrayList<String> filtered = new ArrayList<>();
        for (DiscussionPosition pos : discussionsLocations) {
            if (pos.topic.contains(topic)) {
                filtered.add("Topic: " + pos.topic + " Type: " + pos.type + " Location: x:" + pos.latitude + ", y:" + pos.longitude);
            }
        }

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filtered);
        list.setAdapter(itemsAdapter);
    }

    private void searchByType() {
        ArrayList<String> filtered = new ArrayList<>();
        for (DiscussionPosition pos : discussionsLocations) {
            if (pos.type.equals(selected2)) {
                filtered.add("Topic: " + pos.topic + " Type: " + pos.type + " Location: x:" + pos.latitude + ", y:" + pos.longitude);
            }
        }

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filtered);
        list.setAdapter(itemsAdapter);
    }

    private void searchByRange(double ran) {
        ArrayList<String> filtered = new ArrayList<>();
        for (DiscussionPosition pos : discussionsLocations) {
            double valueLatitude = latitude - pos.latitude;
            double valueLongitude = longitude - pos.longitude;
            if ((Math.abs(valueLatitude) <= ran) && (Math.abs(valueLongitude) <= ran)) {
                filtered.add("Topic: " + pos.topic + " Type: " + pos.type + " Location: x:" + pos.latitude + ", y:" + pos.longitude);
            }
        }

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filtered);
        list.setAdapter(itemsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference().child("discussionsLocations").removeEventListener(discussionsLocationsListener);
        discussionsLocationsListener = null;
    }
}