package com.example.mosis_ispit.secondscreen.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.mosis_ispit.addon.User;
import com.example.mosis_ispit.addon.UserOwner;
import com.example.mosis_ispit.addon.UserPosition;
import com.example.mosis_ispit.addon.UserStorage;
import com.example.mosis_ispit.secondscreen.view.CreateDiscussion;
import com.example.mosis_ispit.secondscreen.view.InDiscussion;
import com.example.mosis_ispit.secondscreen.view.InDiscussionJoin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private boolean permission;
    FirebaseAuth auth;
    DatabaseReference database;
    private TextView friends;
    private Button noFilter, friendsFilter, discussionsFilter, createDiscussion;
    private Bitmap avatar;
    private Marker m;
    private UserPosition userPosition;
    private DiscussionPosition discussionPosition;
    private ArrayList<UserPosition> userLocations;
    private ArrayList<DiscussionPosition> discussionsLocations;
    User currentUser;
    MyLocationNewOverlay myLocationOverlay = null;
    MapView map = null;
    UserStorage storage;

    IMapController mapController = null;
    static final String FIREBASE_DISCUSSION = "discussions";

    public static final int SHOW_MAP = 0;
    public static final int FILTER_TOKENS = 1;
    public static final int SHOW_FRIENDS = 2;
    static final int CREATE_DISCUSSION = 3;
    static final int PROFILE_VIEW = 4;
    static final int LOCATION_PERMISSION = 5;
    static final int IN_DISCUSSION = 6;
    static final int IN_DISCUSSION_CREATOR = 7;
    public LocationManager locationManager;
    public MyLocationListener listener;
    private GeoPoint startPoint;

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
        View view = null;
        view = inflater.inflate(R.layout.map_layout, container, false);

        Log.d("MapFragmentLifecycle", "onCreateView");

        map = view.findViewById(R.id.map);
        noFilter = view.findViewById(R.id.map_filter_no);
        friendsFilter = view.findViewById(R.id.map_filter_friends);
        discussionsFilter = view.findViewById(R.id.map_filter_discussions);
        createDiscussion = view.findViewById(R.id.map_create_discussion);

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

            // load arguments from Activity
            avatar = (Bitmap) this.getArguments().getParcelable("image");
            String user = (String) this.getArguments().getString("username");
            String fir = (String) this.getArguments().getString("firstName");
            String las = (String) this.getArguments().getString("lastName");
            String emai = (String) this.getArguments().getString("email");
            String ra = (String) this.getArguments().getString("rank");
            String po = (String) this.getArguments().getString("points");

            avatar = Bitmap.createScaledBitmap(avatar, 120, 120, true);
            database = FirebaseDatabase.getInstance().getReference();
            auth = FirebaseAuth.getInstance();

            currentUser = new User(user, emai, Integer.parseInt(po), ra);
            currentUser.UID = auth.getCurrentUser().getUid();

            userLocations = new ArrayList<>();
            discussionsLocations = new ArrayList<>();

            Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity));

//        noFilter.setOnClickListener(v -> {
//            state = -1;
//            clearFilter = true;
//            startMap();
//            clearFilter = false;
//        });
//
//        friendsFilter.setOnClickListener(v -> {
//            state = SHOW_FRIENDS;
//            startMap();
//        });
//
//        discussionsFilter.setOnClickListener(v -> {
//            showDiscussions = !showDiscussions;
//            startMap();
//        });

            createDiscussion.setOnClickListener(v -> {
                Intent intentDisc = new Intent(activity, CreateDiscussion.class);
                startActivityForResult(intentDisc, CREATE_DISCUSSION);
            });


            listener = new MyLocationListener();

            // define listeners
            setListeners();

            // add listeners
            database.child("usersLocations").addValueEventListener(usersLocationsListener);
            database.child("discussionsLocations").addValueEventListener(discussionsLocationsListener);

            mapController = map.getController();
            mapController.setZoom(15.0);
            mapController.setCenter(startPoint);
            map.setMultiTouchControls(true);
            permission = true;
            myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(activity), map);
            myLocationOverlay.enableMyLocation();
            myLocationOverlay.enableFollowLocation();
            myLocationOverlay.setDrawAccuracyEnabled(true);
            myLocationOverlay.setDirectionArrow(avatar, avatar);

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
//                                if (!up.inDiscussion) {
                                userLocations.add(up);
//                                }
                            }
                        } else {
                            userLocations.add(up);
                        }
                    }
                }

                updateMap();
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

                discussionsLocations.clear();

                for (DataSnapshot us : snapshot.getChildren()) {
                    DiscussionPosition up = us.getValue(DiscussionPosition.class);
                    discussionsLocations.add(up);
                }

                updateMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Location", error.getMessage());
            }
        };
    }

    private void updateMap() {
        try {
            map.getOverlays().clear();
            if (userLocations.size() > 0) {
                for (UserPosition pos : userLocations) {
                    if(!currentUser.UID.equals(pos.UID)) {
//                        if (!pos.inDiscussion) {
                        Marker ma = new Marker(map);
                        ma.setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.ic_person_24dp));
                        ma.setVisible(true);
                        ma.setPosition(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
                        map.getOverlays().add(ma);
                        ma.setId(pos.UID);
                        ma.setSnippet(pos.username);
                        ma.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                double valueLatitude = marker.getPosition().getLatitude() - myLocationOverlay.getMyLocation().getLatitude();
                                double valueLongitude = marker.getPosition().getLongitude() - myLocationOverlay.getMyLocation().getLongitude();
                                Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                                Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                                if ((Math.abs(valueLatitude) <= 0.0013883333333311043 || valueLatitude >= 0) && (Math.abs(valueLongitude) <= 0.0013883333333311043 || valueLongitude >= 0)) {
                                    // to implement
                                    database.child("users").child(marker.getId()).child("notifications").child(currentUser.UID).setValue(currentUser.getUsername());
                                    Toast.makeText(activity, "Friend request sent", Toast.LENGTH_LONG).show();
                                    marker.setTitle("clicked");
                                } else {
                                    Toast.makeText(activity, "Out of range, please get closer to send friend request", Toast.LENGTH_LONG).show();
                                }
                                return true;
                            }
                        });

                        Marker m = new Marker(map);
                        m.setTextLabelFontSize(58);
                        m.setTextLabelBackgroundColor(Color.rgb(255, 255, 200));
                        m.setTextIcon(pos.username);
                        m.setVisible(true);
                        m.setPosition(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
                        map.getOverlays().add(m);
//                        }
                    }
                }
            }

            if (discussionsLocations.size() > 0) {
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
                    ma.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                            double valueLatitude = marker.getPosition().getLatitude() - myLocationOverlay.getMyLocation().getLatitude();
                            double valueLongitude = marker.getPosition().getLongitude() - myLocationOverlay.getMyLocation().getLongitude();
                            Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                            Log.d("MapFragmentLifecycle", Double.toString(Math.abs(valueLatitude)));
                            if ((Math.abs(valueLatitude) <= 0.0013883333333311043 || valueLatitude >= 0) && (Math.abs(valueLongitude) <= 0.0013883333333311043 || valueLongitude >= 0)) {
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
                        }
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
            // update current location
            map.getOverlays().add(myLocationOverlay);
        } catch(Exception e) {
            Log.d("MapFragmentLifecycle", e.getMessage());
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
                    String topic = data.getStringExtra("topic");
                    String description = data.getStringExtra("description");
                    String size = data.getStringExtra("size");
                    Discussion newDisc = new Discussion(topic, description, myLocationOverlay.getMyLocation().getLongitude(), myLocationOverlay.getMyLocation().getLatitude(), java.text.DateFormat.getDateTimeInstance().format(new Date()), Integer.parseInt(size), currentUser.UID, currentUser.getUsername());
                    newDisc.key = database.push().getKey();
                    discussionPosition = new DiscussionPosition(topic, description, currentUser.getUsername(), myLocationOverlay.getMyLocation().getLongitude(), myLocationOverlay.getMyLocation().getLatitude(), newDisc.maxUsers);
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
                }
                break;
            case IN_DISCUSSION_CREATOR:
                if (resultCode == 12) {
                    // clicked End
                    database.child("discussionsLocations").child(discussionPosition.key).removeValue();
                    database.child("usersLocations").child(currentUser.UID).setValue(userPosition);
//                    User getUserFromDiscussion = (User) activity.getIntent().getSerializableExtra("host");
//                    if (getUserFromDiscussion != null) {
//                        currentUser = getUserFromDiscussion;
//                    }
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
            usersLocationsListener = null;
            discussionsLocationsListener = null;
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
