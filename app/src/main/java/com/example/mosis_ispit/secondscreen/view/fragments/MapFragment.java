package com.example.mosis_ispit.secondscreen.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.Discussion;
import com.example.mosis_ispit.addon.DiscussionPosition;
import com.example.mosis_ispit.addon.FriendRequest;
import com.example.mosis_ispit.addon.OnGetDataListener;
import com.example.mosis_ispit.addon.User;
import com.example.mosis_ispit.addon.UserOwner;
import com.example.mosis_ispit.addon.UserPosition;
import com.example.mosis_ispit.addon.UserStorage;
import com.example.mosis_ispit.secondscreen.view.CreateDiscussion;
import com.example.mosis_ispit.secondscreen.view.InDiscussion;
import com.example.mosis_ispit.secondscreen.view.InDiscussionJoin;
import com.example.mosis_ispit.secondscreen.view.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment {
    private Activity activity;
    private ValueEventListener usersLocationsListener;
    private ValueEventListener discussionsLocationsListener;

    private ArrayList<String> friendList;
    private boolean permission;
    FirebaseAuth auth;
    DatabaseReference database;
    private String selected;
    private TextView friends;
    private Button noFilter, friendsFilter, createDiscussion;
    private FloatingActionButton search;
    private Spinner filterDiscussions;
    private EditText radius;
    private Marker m;
    private UserPosition userPosition;
    private DiscussionPosition discussionPosition;
    private ArrayList<UserPosition> userLocations;
    private ArrayList<DiscussionPosition> discussionsLocations;
    User currentUser;
    MyLocationNewOverlay myLocationOverlay = null;
    MapView map = null;
    StorageReference storage;

    IMapController mapController = null;
    static final String FIREBASE_DISCUSSION = "discussions";
    public static final long MB=1024*1024;

    public static final int SHOW_ALL = 100;
    public static final int SHOW_FRIENDS = 200;
    static final int CREATE_DISCUSSION = 3;
    static final int SEARCH = 4;
    static final int IN_DISCUSSION = 5;
    static final int IN_DISCUSSION_CREATOR = 6;
    public LocationManager locationManager;
    public MyLocationListener listener;
    private GeoPoint startPoint;
    private ValueEventListener friendsListener;
    private int state;

    public boolean showFriends = false;
    public boolean showDiscussions = false;
    public boolean clearFilter = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("MapFragmentLifecycle", "onAttach");
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_layout, container, false);

        Log.d("MapFragmentLifecycle", "onCreateView");

        map = view.findViewById(R.id.map);
        noFilter = view.findViewById(R.id.map_filter_no);
        friendsFilter = view.findViewById(R.id.map_filter_friends);
        createDiscussion = view.findViewById(R.id.map_create_discussion);
        search = view.findViewById(R.id.map_search);
        filterDiscussions = view.findViewById(R.id.map_filter_discussion);
        radius = view.findViewById(R.id.map_layout_radius);
        radius.setVisibility(View.INVISIBLE);

        startPoint = new GeoPoint(43.3209, 21.8958); // Nis
//        GeoPoint startPoint = new GeoPoint(42.99806, 21.94611); // Leskovac

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("MapFragmentLifecycle", "onViewCreated");
        // Check permissions
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Permission not granted", Toast.LENGTH_LONG).show();
            permission = false;
        } else {
            state = 100;
            // load arguments from Activity
            Bitmap avatar = (Bitmap) this.getArguments().getParcelable("image");
            String user = (String) this.getArguments().getString("username");
            String fir = (String) this.getArguments().getString("firstName");
            String las = (String) this.getArguments().getString("lastName");
            String emai = (String) this.getArguments().getString("email");
            String ra = (String) this.getArguments().getString("rank");
            String po = (String) this.getArguments().getString("points");

            avatar = Bitmap.createScaledBitmap(avatar, 120, 120, true);
            database = FirebaseDatabase.getInstance().getReference();
            auth = FirebaseAuth.getInstance();
            storage= FirebaseStorage.getInstance().getReference();

            currentUser = new User(user, emai, Integer.parseInt(po), ra);
            currentUser.UID = auth.getCurrentUser().getUid();

            userLocations = new ArrayList<>();
            discussionsLocations = new ArrayList<>();
            friendList = new ArrayList<>();

            Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity));

            noFilter.setOnClickListener(v -> {
                if (state != SHOW_ALL) {
                    state = SHOW_ALL;
                    updateMap();
                }
            });

            friendsFilter.setOnClickListener(v -> {
                if (state != SHOW_FRIENDS) {
                    state = SHOW_FRIENDS;
                    updateMap();
                }
            });

            createDiscussion.setOnClickListener(v -> {
                Intent intentDisc = new Intent(activity, CreateDiscussion.class);
                startActivityForResult(intentDisc, CREATE_DISCUSSION);
            });

            search.setOnClickListener(v -> {
                Intent intentSearch = new Intent(activity, SearchActivity.class);
                intentSearch.putExtra("latitude", myLocationOverlay.getMyLocation().getLatitude());
                intentSearch.putExtra("longitude", myLocationOverlay.getMyLocation().getLongitude());
                startActivityForResult(intentSearch, SEARCH);
            });

            String[] items = new String[]{"all", "art", "sport", "politics", "range"};
            selected = "all";

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items);
            filterDiscussions.setAdapter(adapter);

            filterDiscussions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selected = items[position];
                    updateMap();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            listener = new MyLocationListener();

            // define listeners
            setListeners();

            // add listeners
            database.child("usersLocations").addValueEventListener(usersLocationsListener);
            database.child("discussionsLocations").addValueEventListener(discussionsLocationsListener);
            database.child("users").child(currentUser.UID).child("friends").addValueEventListener(friendsListener);

            mapController = map.getController();
            mapController.setZoom(15.0);
            mapController.setCenter(startPoint);
            map.setMultiTouchControls(true);
            permission = true;
            myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(activity), map);
            myLocationOverlay.enableMyLocation();
            myLocationOverlay.enableFollowLocation();
//            myLocationOverlay.setDrawAccuracyEnabled(true);
//            myLocationOverlay.setDirectionArrow(avatar, avatar);

            userPosition = new UserPosition(currentUser.getUsername(), startPoint.getLongitude(), startPoint.getLatitude());
            userPosition.UID = currentUser.UID;

            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        }
    }

    private void setListeners() {
        usersLocationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MapFragmentLifecycle", "User location change in db");

                try {
                    for (DataSnapshot us : snapshot.getChildren()) {
                        UserPosition up = us.getValue(UserPosition.class);
                        if (up != null) {
                            boolean found = false;
                            if (userLocations.size() > 0) {
                                for (UserPosition upos : userLocations) {
                                    if (upos.UID.equals(up.UID)) {
                                        found = true;
                                        upos.setLatitude(up.getLatitude());
                                        upos.setLongitude(up.getLongitude());
                                    }
                                }
                                if (!found) {
                                    userLocations.add(up);
                                }
                            } else {
                                userLocations.add(up);
                            }
                        }
                    }

                    updateMap();
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Location", error.getMessage());
            }
        };

        discussionsLocationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MapFragmentLifecycle", "Location change in db");

                try {
                    discussionsLocations.clear();

                    for (DataSnapshot us : snapshot.getChildren()) {
                        DiscussionPosition up = us.getValue(DiscussionPosition.class);
                        discussionsLocations.add(up);
                    }

                    updateMap();
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Location", error.getMessage());
            }
        };

        friendsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MapFragmentLifecycle", "User location change in db");

                try {
                    for (DataSnapshot us : snapshot.getChildren()) {
                        String up = (String) us.getValue();
                        if (up != null) {
                            boolean found = false;
                            if (friendList.size() > 0) {
                                for (String upos : friendList) {
                                    if (upos.equals(up)) {
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    friendList.add(up);
                                }
                            } else {
                                friendList.add(up);
                            }
                        }
                    }

                    updateMap();
                } catch (Exception e) {
                    Log.e("Log", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void updateMap() {
        try {
            map.getOverlays().clear();
            // users
            switch (state) {
                case SHOW_ALL:
                    showAll();
                    break;
                case SHOW_FRIENDS:
                    showFriends();
                    break;
            }
            // discussions
            if (selected.equals("all")) {
                radius.setVisibility(View.INVISIBLE);
                showAllDiscussion();
            } else if (selected.equals("range")) {
                radius.setVisibility(View.VISIBLE);
                radius.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        showInRangeDiscussion();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
//                showInRangeDiscussion();
            } else {
                radius.setVisibility(View.INVISIBLE);
                showFilteredDiscussion();
            }
            // update current location
            map.getOverlays().add(myLocationOverlay);
        } catch(Exception e) {
            Log.d("MapFragmentLifecycle", e.getMessage());
        }
    }

    private void showInRangeDiscussion() {
        double rad = 0.0013883333333311043;
        if (!radius.getText().toString().equals("")) {
            rad = Double.parseDouble(radius.getText().toString().trim());
        }
        if (discussionsLocations.size() > 0) {
            try {
                for (DiscussionPosition pos : discussionsLocations) {
                    double lat = pos.latitude - myLocationOverlay.getMyLocation().getLatitude();
                    double lon = pos.longitude - myLocationOverlay.getMyLocation().getLongitude();
                    if ((Math.abs(lat) <= rad) && (Math.abs(lon) <= rad)) {
                        Marker ma = new Marker(map);
                        ma.setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.round_explore_black_36));
                        ma.setVisible(true);
                        ma.setPosition(new GeoPoint(pos.latitude, pos.longitude));
                        map.getOverlays().add(ma);
                        ma.setTitle(pos.key);
                        ma.setSnippet(pos.topic);
                        ma.setId(pos.description);
                        ma.setSubDescription(pos.owner);
                        ma.setAlpha(pos.maxUsers);
                        ma.setOnMarkerClickListener((marker, mapView) -> {
                            double valueLatitude = marker.getPosition().getLatitude() - myLocationOverlay.getMyLocation().getLatitude();
                            double valueLongitude = marker.getPosition().getLongitude() - myLocationOverlay.getMyLocation().getLongitude();
                            Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                            Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                            if ((Math.abs(valueLatitude) <= 0.0013883333333311043) && (Math.abs(valueLongitude) <= 0.0013883333333311043)) {
                                // to implement
                                Log.d("MapFragmentLifecycle", marker.getTitle());
                                myLocationOverlay.disableMyLocation();
                                myLocationOverlay.disableFollowLocation();
                                Intent intent = new Intent(activity, InDiscussionJoin.class);
                                intent.putExtra("discussion", marker.getTitle());
                                intent.putExtra("description", marker.getId());
                                intent.putExtra("topic", marker.getSnippet());
                                intent.putExtra("owner", marker.getSubDescription());
                                intent.putExtra("maxUsers", (int) marker.getAlpha());
                                intent.putExtra("requestCode", "JOINING_DISCUSSION");
                                intent.putExtra("user", currentUser);
                                startActivityForResult(intent, IN_DISCUSSION);
                            } else {
                                Toast.makeText(activity, "Out of range, please get closer", Toast.LENGTH_LONG).show();
                            }
                            return true;
                        });

                        Marker m = new Marker(map);
                        m.setTextLabelFontSize(58);
                        m.setTextLabelBackgroundColor(Color.rgb(255, 255, 120));
//                    Toast.makeText(activity, pos.topic, Toast.LENGTH_SHORT).show();
                        m.setTextIcon(pos.topic);
                        m.setVisible(true);
                        ma.setPosition(new GeoPoint(pos.latitude, pos.longitude));
                        map.getOverlays().add(m);
                    }
                }
            } catch(Exception e) {
                Log.e("Log", e.getMessage());
            }
        }
    }

    private void showAllDiscussion() {
        if (discussionsLocations.size() > 0) {
            try {
                for (DiscussionPosition pos : discussionsLocations) {
                    Marker ma = new Marker(map);
                    ma.setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.round_explore_black_36));
                    ma.setVisible(true);
                    ma.setPosition(new GeoPoint(pos.latitude, pos.longitude));
                    map.getOverlays().add(ma);
                    ma.setTitle(pos.key);
                    ma.setSnippet(pos.topic);
                    ma.setId(pos.description);
                    ma.setSubDescription(pos.owner);
                    ma.setAlpha(pos.maxUsers);
                    ma.setOnMarkerClickListener((marker, mapView) -> {
                        double valueLatitude = marker.getPosition().getLatitude() - myLocationOverlay.getMyLocation().getLatitude();
                        double valueLongitude = marker.getPosition().getLongitude() - myLocationOverlay.getMyLocation().getLongitude();
                        Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                        Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                        if ((Math.abs(valueLatitude) <= 0.0013883333333311043) && (Math.abs(valueLongitude) <= 0.0013883333333311043)) {
                            // to implement
                            Log.d("MapFragmentLifecycle", marker.getTitle());
                            myLocationOverlay.disableMyLocation();
                            myLocationOverlay.disableFollowLocation();
                            Intent intent = new Intent(activity, InDiscussionJoin.class);
                            intent.putExtra("discussion", marker.getTitle());
                            intent.putExtra("description", marker.getId());
                            intent.putExtra("topic", marker.getSnippet());
                            intent.putExtra("owner", marker.getSubDescription());
                            intent.putExtra("maxUsers", (int) marker.getAlpha());
                            intent.putExtra("requestCode", "JOINING_DISCUSSION");
                            intent.putExtra("user", currentUser);
                            startActivityForResult(intent, IN_DISCUSSION);
                        } else {
                            Toast.makeText(activity, "Out of range, please get closer", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    });

                    Marker m = new Marker(map);
                    m.setTextLabelFontSize(58);
                    m.setTextLabelBackgroundColor(Color.rgb(255, 255, 120));
//                    Toast.makeText(activity, pos.topic, Toast.LENGTH_SHORT).show();
                    m.setTextIcon(pos.topic);
                    m.setVisible(true);
                    ma.setPosition(new GeoPoint(pos.latitude, pos.longitude));
                    map.getOverlays().add(m);
                }
            } catch (Exception e) {
                Log.e("Log", e.getMessage());
            }
        }
    }

    private void showFilteredDiscussion() {
        if (discussionsLocations.size() > 0) {
            try {
                for (DiscussionPosition pos : discussionsLocations) {
                    if (pos.type.equals(selected)) {
                        Marker ma = new Marker(map);
                        ma.setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.round_explore_black_36));
                        ma.setVisible(true);
                        ma.setPosition(new GeoPoint(pos.latitude, pos.longitude));
                        map.getOverlays().add(ma);
                        ma.setTitle(pos.key);
                        ma.setSnippet(pos.topic);
                        ma.setId(pos.description);
                        ma.setSubDescription(pos.owner);
                        ma.setAlpha(pos.maxUsers);
                        ma.setOnMarkerClickListener((marker, mapView) -> {
                            double valueLatitude = marker.getPosition().getLatitude() - myLocationOverlay.getMyLocation().getLatitude();
                            double valueLongitude = marker.getPosition().getLongitude() - myLocationOverlay.getMyLocation().getLongitude();
                            Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                            Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                            if ((Math.abs(valueLatitude) <= 0.0013883333333311043) && (Math.abs(valueLongitude) <= 0.0013883333333311043)) {
                                // to implement
                                Log.d("MapFragmentLifecycle", marker.getTitle());
                                myLocationOverlay.disableMyLocation();
                                myLocationOverlay.disableFollowLocation();
                                Intent intent = new Intent(activity, InDiscussionJoin.class);
                                intent.putExtra("discussion", marker.getTitle());
                                intent.putExtra("description", marker.getId());
                                intent.putExtra("topic", marker.getSnippet());
                                intent.putExtra("owner", marker.getSubDescription());
                                intent.putExtra("maxUsers", (int) marker.getAlpha());
                                intent.putExtra("requestCode", "JOINING_DISCUSSION");
                                intent.putExtra("user", currentUser);
                                startActivityForResult(intent, IN_DISCUSSION);
                            } else {
                                Toast.makeText(activity, "Out of range, please get closer", Toast.LENGTH_LONG).show();
                            }
                            return true;
                        });

                        Marker m = new Marker(map);
                        m.setTextLabelFontSize(58);
                        m.setTextLabelBackgroundColor(Color.rgb(255, 255, 120));
//                    Toast.makeText(activity, pos.topic, Toast.LENGTH_SHORT).show();
                        m.setTextIcon(pos.topic);
                        m.setVisible(true);
                        ma.setPosition(new GeoPoint(pos.latitude, pos.longitude));
                        map.getOverlays().add(m);
                    }
                }
            } catch (Exception e) {
                Log.e("Log", e.getMessage());
            }
        }
    }

    public void showAll() {
        if (userLocations.size() > 0) {
            try {
                for (UserPosition pos : userLocations) {
                    if(!currentUser.UID.equals(pos.UID)) {
                        if (friendList.contains(pos.username)) {
                            showFriend(pos);
                        } else {
                            Marker ma = new Marker(map);
                            ma.setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.ic_person_24dp));
                            ma.setVisible(true);
                            ma.setPosition(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
                            ma.setId(pos.UID);
                            ma.setTitle(pos.username);
                            ma.setSnippet(pos.UID);
                            ma.setOnMarkerClickListener((marker, mapView) -> {
                                double valueLatitude = marker.getPosition().getLatitude() - myLocationOverlay.getMyLocation().getLatitude();
                                double valueLongitude = marker.getPosition().getLongitude() - myLocationOverlay.getMyLocation().getLongitude();
                                Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                                Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                                if ((Math.abs(valueLatitude) <= 0.0013883333333311043) && (Math.abs(valueLongitude) <= 0.0013883333333311043)) {
                                    database.child("users").child(marker.getId()).child("notifications").child(currentUser.UID).setValue(currentUser.getUsername());
                                    Toast.makeText(activity, "Friend request sent", Toast.LENGTH_LONG).show();
                                    marker.setTitle("clicked");
                                } else {
                                    Toast.makeText(activity, "Out of range, please get closer to send friend request", Toast.LENGTH_LONG).show();
                                }
                                return true;
                            });
                            map.getOverlays().add(ma);

                            Marker m = new Marker(map);
                            m.setTextLabelFontSize(58);
                            m.setTextLabelBackgroundColor(Color.rgb(255, 255, 200));
                            m.setTextIcon(pos.username);
                            m.setVisible(true);
                            m.setPosition(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
                            m.setId(pos.UID);
                            m.setTitle(pos.username);
                            m.setSnippet(pos.UID);
                            map.getOverlays().add(m);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Log", e.getMessage());
            }
        }
    }

    public void showFriend (UserPosition pos) {
        storage.child("users").child(pos.UID).child("profile_img").getBytes(100*MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                try {
                    byte[] data = task.getResult();
                    Bitmap friendAvatar = BitmapFactory.decodeByteArray(data, 0, data.length);
                    friendAvatar = Bitmap.createScaledBitmap(friendAvatar, 200, 200, true);
                    Drawable displayFriendImage = new BitmapDrawable(getResources(), friendAvatar);
                    Marker ma = new Marker(map);
                    ma.setVisible(true);
                    ma.setPosition(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
                    ma.setIcon(displayFriendImage);
                    ma.setImage(displayFriendImage);
                    ma.setId(pos.UID);
                    ma.setTitle(pos.username);
                    ma.setSnippet(pos.UID);
                    map.getOverlays().add(ma);
//                                ma.setOnMarkerClickListener((marker, mapView) -> {
//                                    double valueLatitude = marker.getPosition().getLatitude() - myLocationOverlay.getMyLocation().getLatitude();
//                                    double valueLongitude = marker.getPosition().getLongitude() - myLocationOverlay.getMyLocation().getLongitude();
//                                    Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
//                                    Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
//                                    if ((Math.abs(valueLatitude) <= 0.0013883333333311043 || valueLatitude >= 0) && (Math.abs(valueLongitude) <= 0.0013883333333311043 || valueLongitude >= 0)) {
//                                        database.child("users").child(marker.getId()).child("notifications").child(currentUser.UID).setValue(currentUser.getUsername());
//                                        Toast.makeText(activity, "Friend request sent", Toast.LENGTH_LONG).show();
//                                        marker.setTitle("clicked");
//                                    } else {
//                                        Toast.makeText(activity, "Out of range, please get closer to send friend request", Toast.LENGTH_LONG).show();
//                                    }
//                                    return true;
//                                });

                    Marker m = new Marker(map);
                    m.setTextLabelFontSize(58);
                    m.setTextLabelBackgroundColor(Color.rgb(255, 255, 200));
                    m.setTextIcon(pos.username);
                    m.setVisible(true);
                    m.setPosition(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
                    m.setId(pos.UID);
                    m.setTitle(pos.username);
                    m.setSnippet(pos.UID);
                    m.setImage(displayFriendImage);
                    map.getOverlays().add(m);
                } catch (Exception e) {
                    Log.e("Log", e.getMessage());
                }
            }
        });
    }

    private void showFriends() {
        if (userLocations.size() > 0) {
            for (UserPosition pos : userLocations) {
                if(!currentUser.UID.equals(pos.UID)) {
                    if (friendList.contains(pos.username)) {
                        showFriend(pos);
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MapFragmentLifecycle", "onPause");
        if (permission) {
            map.onPause();
            myLocationOverlay.disableFollowLocation();
            myLocationOverlay.disableMyLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MapFragmentLifecycle", "onResume");
        if (permission) {
            map.onResume();
            myLocationOverlay.enableMyLocation();
            myLocationOverlay.enableFollowLocation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case CREATE_DISCUSSION:
                if (resultCode == RESULT_OK) {
                    try {
                        String topic = data.getStringExtra("topic");
                        String description = data.getStringExtra("description");
                        String size = data.getStringExtra("size");
                        String type = data.getStringExtra("type");
                        Discussion newDisc = new Discussion(topic, description, myLocationOverlay.getMyLocation().getLongitude(), myLocationOverlay.getMyLocation().getLatitude(), java.text.DateFormat.getDateTimeInstance().format(new Date()), Integer.parseInt(size), currentUser.UID, currentUser.getUsername(), type);
                        newDisc.key = database.push().getKey();
                        discussionPosition = new DiscussionPosition(topic, description, currentUser.getUsername(), myLocationOverlay.getMyLocation().getLongitude(), myLocationOverlay.getMyLocation().getLatitude(), newDisc.maxUsers, newDisc.type);
                        discussionPosition.key = newDisc.key;

                        database.child(FIREBASE_DISCUSSION).child(newDisc.key).child("data").setValue(newDisc);
                        UserOwner owner = new UserOwner(currentUser.getUsername(), currentUser.getPoints(), currentUser.getRank(), newDisc.active, newDisc.getTopic());
                        owner.UID = currentUser.UID;
                        database.child(FIREBASE_DISCUSSION).child(newDisc.key).child("owner").setValue(owner);
                        database.child(FIREBASE_DISCUSSION).child(newDisc.key).child("users").child(currentUser.UID).setValue(currentUser.getUsername());
                        database.child("discussionsLocations").child(discussionPosition.key).setValue(discussionPosition);

                        myLocationOverlay.disableMyLocation();
                        myLocationOverlay.disableFollowLocation();

                        database.child("usersLocations").child(currentUser.UID).setValue(userPosition);

                        Intent intent = new Intent(activity, InDiscussion.class);
                        intent.putExtra("discussion", newDisc);
                        intent.putExtra("requestCode", "CREATE_DISCUSSION");
                        intent.putExtra("user", currentUser);
                        startActivityForResult(intent, IN_DISCUSSION_CREATOR);
                    } catch(Exception e) {
                        Log.d("Feature", e.getMessage());
                    }
                }
                break;
            case IN_DISCUSSION_CREATOR:
                if (resultCode == 12) {
                    // clicked End
                    database.child("discussionsLocations").child(discussionPosition.key).removeValue();
                    database.child("usersLocations").child(currentUser.UID).setValue(userPosition);
                    myLocationOverlay.enableMyLocation();
                    myLocationOverlay.enableFollowLocation();
                }
                break;
            case IN_DISCUSSION:
                if (resultCode == 11) {
                    // clicked Join
                    database.child("usersLocations").child(currentUser.UID).setValue(userPosition);
                    myLocationOverlay.enableMyLocation();
                    myLocationOverlay.enableFollowLocation();
                } else if (resultCode == 10) {
                    // clicked Exit
                    myLocationOverlay.enableMyLocation();
                    myLocationOverlay.enableFollowLocation();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("MapFragmentLifecycle", "onDetach");
        activity = null;
        if (permission) {
            database.child("usersLocations").removeEventListener(usersLocationsListener);
            database.child("discussionsLocations").removeEventListener(discussionsLocationsListener);
            database.child("users").child(currentUser.UID).child("friends").removeEventListener(friendsListener);
            usersLocationsListener = null;
            discussionsLocationsListener = null;
            friendsListener = null;
            locationManager.removeUpdates(listener);
            locationManager = null;
            listener = null;
            map.onDetach();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("MapFragmentLifecycle", "onDestroyView");
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("*****", "Location changed");
            userPosition.setLongitude(loc.getLongitude());
            userPosition.setLatitude(loc.getLatitude());
            database.child("usersLocations").child(currentUser.UID).setValue(userPosition);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(activity.getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(activity.getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
    }
}
