package com.example.mosis_ispit.secondscreen.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import com.example.mosis_ispit.addon.DiscussionsStorage;
import com.example.mosis_ispit.addon.FriendRequest;
import com.example.mosis_ispit.addon.User;
import com.example.mosis_ispit.addon.UserPosition;
import com.example.mosis_ispit.addon.UserStorage;
import com.example.mosis_ispit.secondscreen.view.CreateDiscussion;
import com.example.mosis_ispit.secondscreen.view.InDiscussion;
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
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment {
    FirebaseAuth auth;
    DatabaseReference database;
    private TextView friends;
    private Button noFilter, friendsFilter, discussionsFilter, createDiscussion;
    private ArrayList<Marker> myMarkers;
    private ArrayList<Discussion> discussions;
    private Bitmap avatar;
    private Marker m;
    private UserPosition userPosition;
    private DiscussionPosition discussionPosition;
    private ArrayList<UserPosition> userLocations = new ArrayList<>();
    private DiscussionsStorage storageDiscussions;
    private ArrayList<DiscussionPosition> discussionsLocations = new ArrayList<>();
    User currentUser;
    MyLocationNewOverlay myLocationOverlay = null;
    //    ArrayList<ItemizedIconOverlay> discussionsOverlay = new ArrayList<ItemizedIconOverlay>();
//    ArrayList<ItemizedIconOverlay> usersOverlay = new ArrayList<ItemizedIconOverlay>();
//    ItemizedIconOverlay discOverlay = new ItemizedIconOverlay();
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

    public boolean showFriends = false;
    public boolean showDiscussions = false;
    public boolean clearFilter = false;


    private int state = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_layout, container, false);
        avatar = (Bitmap) this.getArguments().getParcelable("image");
        String user = (String) this.getArguments().getString("username");
        String fir = (String) this.getArguments().getString("firstName");
        String las = (String) this.getArguments().getString("lastName");
        String emai = (String) this.getArguments().getString("email");
        String ra = (String) this.getArguments().getString("rank");
        String po = (String) this.getArguments().getString("points");
        currentUser = new User(user, emai, Integer.parseInt(po), ra);

        storage = new UserStorage();
        storageDiscussions = new DiscussionsStorage();
        discussions = new ArrayList<Discussion>();
        avatar = Bitmap.createScaledBitmap(avatar, 120, 120, true);
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        currentUser.UID = auth.getCurrentUser().getUid();
        myMarkers = new ArrayList<Marker>();

        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        map = view.findViewById(R.id.map);
        noFilter = view.findViewById(R.id.map_filter_no);
        friendsFilter = view.findViewById(R.id.map_filter_friends);
        discussionsFilter = view.findViewById(R.id.map_filter_discussions);
        createDiscussion = view.findViewById(R.id.map_create_discussion);

        noFilter.setOnClickListener(v -> {
            state = -1;
            clearFilter = true;
            startMap();
            clearFilter = false;
        });

        friendsFilter.setOnClickListener(v -> {
            state = SHOW_FRIENDS;
            startMap();
        });

        discussionsFilter.setOnClickListener(v -> {
            showDiscussions = !showDiscussions;
            startMap();
        });

        createDiscussion.setOnClickListener(v -> {
            Intent intentDisc = new Intent(getActivity(), CreateDiscussion.class);
            startActivityForResult(intentDisc, CREATE_DISCUSSION);
        });

        mapController = map.getController();

        userPosition = new UserPosition();
        GeoPoint startPoint = new GeoPoint(43.3209, 21.8958); // Nis
//        GeoPoint startPoint = new GeoPoint(42.99806, 21.94611); // Leskovac
        mapController.setCenter(startPoint);

//        database.child("usersLocations").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                updateMarkers();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase Location", error.getMessage());
//            }
//        });

        database.child("usersLocations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("*****", "Location changed VALUE EVENT LISTENER");
                if (snapshot.getValue(UserPosition.class) != null) {
                    UserPosition pos = snapshot.getValue(UserPosition.class);
                    if(pos != null) {
                        if (pos.UID != null) {
                            boolean found = false;
                            if (userLocations.size() > 0) {
                                for(UserPosition up : userLocations) {
                                    if (up.UID.equals(pos.UID)) {
                                        up.setLatitude(pos.getLatitude());
                                        up.setLongitude(pos.getLongitude());
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    userLocations.add(pos);
                                }
                            } else {
                                userLocations.add(pos);
                            }
                        }
                    }
                }
                updateMarkers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Location", error.getMessage());
            }
        });

        database.child("userLocations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("*****", "Location changed onChildAdded");
                if (dataSnapshot.getValue(UserPosition.class) != null) {
                    UserPosition pos = dataSnapshot.getValue(UserPosition.class);
                    if (pos != null) {
                        if (pos.UID != null) {
                            boolean found = false;
                            if (userLocations.size() > 0) {
                                for(UserPosition up : userLocations) {
                                    if (up.UID.equals(pos.UID)) {
                                        up.setLatitude(pos.getLatitude());
                                        up.setLongitude(pos.getLongitude());
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    userLocations.add(pos);
                                }
                            } else {
                                userLocations.add(pos);
                            }
                        }
                    }
                }
                updateMarkers();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("*****", "Location changed onChildChanged");
                if (dataSnapshot.getValue(UserPosition.class) != null) {
                    UserPosition pos = dataSnapshot.getValue(UserPosition.class);
                    if (pos != null) {
                        if (pos.UID != null) {
                            boolean found = false;
                            if (userLocations.size() > 0) {
                                for(UserPosition up : userLocations) {
                                    if (up.UID.equals(pos.UID)) {
                                        up.setLatitude(pos.getLatitude());
                                        up.setLongitude(pos.getLongitude());
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    userLocations.add(pos);
                                }
                            } else {
                                userLocations.add(pos);
                            }
                            updateMarkers();
                        }
                    }
                }
                updateMarkers();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        database.child("discussionsLocations").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { updateMarkers(); }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { updateMarkers(); }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { updateMarkers(); }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        map.setMultiTouchControls(true);
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, LOCATION_PERMISSION);
        } else {
            locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);

            startMap();
        }
        return view;
    }

    @SuppressLint({"Missing Permissions", "MissingPermission"})
    @Override
    public void onRequestPermissionsResult(int reqCode, String permissions[], int[] grantedResults) {
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);

        startMap();
    }

    private void startMap(){
        switch (state){
            case SHOW_FRIENDS :
                setMyLocationOverlay();
//                friendsView();
                break;
//            case SHOW_MAP :
//                setMyLocationOverlay();
//                prikaziSveTokene();
//                break;
//            case FILTER_TOKENS :
//                setMyLocationOverlay();
//                prikaziFiltriraneTokene(FilterData.getInstance().filter);
//                break;
            default:
                setMyLocationOverlay();
//                updateMarkers();
                break;
        }
    }

    public BitmapDrawable writeOnDrawable(int drawableId, String text){
        Bitmap bm = BitmapFactory.decodeResource(requireActivity().getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, 0, bm.getHeight()/2, paint);

        return new BitmapDrawable(requireActivity().getResources(), bm);
    }

    private void updateMarkers() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UserPosition pos : userLocations) {
                if(!currentUser.UID.equals(pos.UID)) {
                    if(!pos.inDiscussion) {
                        Marker ma = new Marker(map);
                        ma.setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.ic_person_24dp));
                        ma.setVisible(true);
                        ma.setPosition(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
                        map.getOverlays().add(ma);

                        Marker m = new Marker(map);
                        m.setTextLabelFontSize(48);
                        m.setTextLabelBackgroundColor(Color.rgb(255, 255, 200));
                        m.setTextIcon(pos.username);
                        m.setVisible(true);
                        m.setPosition(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
                        map.getOverlays().add(m);
                    }
                }
            }

//        discussionsLocations = storageDiscussions.getAllDiscussions();
//        for (DiscussionPosition posDis : discussionsLocations) {
//            Marker ma = new Marker(map);
//            ma.setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.round_explore_black_24dp));
//            ma.setVisible(true);
//            ma.setPosition(new GeoPoint(posDis.getLatitude(), posDis.getLongitude()));
//            map.getOverlays().add(ma);
//
//            Marker m = new Marker(map);
//            m.setTextLabelFontSize(40);
//            m.setTextIcon(posDis.topic);
//            m.setVisible(true);
//            m.setPosition(new GeoPoint(posDis.getLatitude(), posDis.getLongitude()));
//            map.getOverlays().add(m);
//        }
                map.getOverlays().add(myLocationOverlay);
            }
        });
//        storage.updateAllUsers();
////        storageDiscussions.resetListeners();
//        map.getOverlays().clear();
//        userLocations = storage.getUsersLocations();

    }

    private void setMyLocationOverlay(){
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapController.setZoom(15.0);
                myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireActivity()), map);
                myLocationOverlay.enableMyLocation();
                myLocationOverlay.enableFollowLocation();
                myLocationOverlay.setDrawAccuracyEnabled(true);
                myLocationOverlay.setPersonIcon(avatar);
                map.getOverlays().add(0, myLocationOverlay);
                userPosition = new UserPosition(currentUser.getUsername(), 43.3209, 21.8958, false);
                userPosition.UID = currentUser.UID;
                myLocationOverlay.runOnFirstFix(new Runnable() {
                    @Override
                    public void run() {
                        myLocationOverlay.setDirectionArrow(avatar, avatar);
                        if(map.getOverlays().size() > 0) {
                            map.getOverlays().set(0, myLocationOverlay);
                        } else {
                            map.getOverlays().add(0, myLocationOverlay);
                        }
                        userPosition.setLongitude(myLocationOverlay.getMyLocation().getLongitude());
                        userPosition.setLatitude(myLocationOverlay.getMyLocation().getLatitude());

                        database.child("usersLocations").child(currentUser.UID).setValue(userPosition);

                        updateMarkers();
                    }
                });
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_DISCUSSION) {
            if (resultCode == RESULT_OK) {
                String topic = data.getStringExtra("topic");
                String description = data.getStringExtra("description");
                String type = data.getStringExtra("type");
                String size = data.getStringExtra("size");
                Discussion newDisc = new Discussion(topic, description, myLocationOverlay.getMyLocation().getLongitude(), myLocationOverlay.getMyLocation().getLatitude(), java.text.DateFormat.getDateTimeInstance().format(new Date()), Boolean.parseBoolean(type), Integer.parseInt(size), currentUser);
                newDisc.key = database.push().getKey();
                discussionPosition = new DiscussionPosition(topic, myLocationOverlay.getMyLocation().getLongitude(), myLocationOverlay.getMyLocation().getLatitude());
                database.child(FIREBASE_DISCUSSION).child(newDisc.key).child("data").setValue(newDisc);
                database.child(FIREBASE_DISCUSSION).child(newDisc.key).child("users").child(currentUser.UID).setValue(currentUser.getUsername());
                database.child("activeDiscussions").child(newDisc.key).setValue(discussionPosition);
                userPosition.inDiscussion = true;
                database.child("usersLocations").child(currentUser.UID).child("inDiscussion").setValue(true);
                database.child("activeDiscussions").child(newDisc.key).setValue(discussionPosition);
                discussions.add(newDisc);

                Intent intent = new Intent(getActivity(), InDiscussion.class);
                intent.putExtra("discussion", newDisc);
                intent.putExtra("requestCode", "CREATE_DISCUSSION");
                intent.putExtra("user", currentUser);
                startActivityForResult(intent, IN_DISCUSSION_CREATOR);
            }
        } else if (requestCode == IN_DISCUSSION_CREATOR) {
            if (resultCode == RESULT_OK) {
                userPosition.inDiscussion = false;
                database.child("usersLocations").child(currentUser.UID).child("inDiscussion").setValue(false);
                userPosition.setLongitude(myLocationOverlay.getMyLocation().getLongitude());
                userPosition.setLatitude(myLocationOverlay.getMyLocation().getLatitude());
            }
        }
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
            Toast.makeText(requireActivity().getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(requireActivity().getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
    }
}
