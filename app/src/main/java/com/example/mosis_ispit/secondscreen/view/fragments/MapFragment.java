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
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment {
    private Activity activity;
    private ValueEventListener usersLocationsListener;

    FirebaseAuth auth;
    DatabaseReference database;
    private TextView friends;
    private Button noFilter, friendsFilter, discussionsFilter, createDiscussion;
    private Bitmap avatar;
    private Marker m;
    private UserPosition userPosition;
    private ArrayList<UserPosition> userLocations;
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

    public boolean showFriends = false;
    public boolean showDiscussions = false;
    public boolean clearFilter = false;


    private int state = 0;

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
        discussionsFilter = view.findViewById(R.id.map_filter_discussions);
        createDiscussion = view.findViewById(R.id.map_create_discussion);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("MapFragmentLifecycle", "onViewCreated");

        // load arguments from Activity
        avatar = (Bitmap) this.getArguments().getParcelable("image");
        String user = (String) this.getArguments().getString("username");
        String fir = (String) this.getArguments().getString("firstName");
        String las = (String) this.getArguments().getString("lastName");
        String emai = (String) this.getArguments().getString("email");
        String ra = (String) this.getArguments().getString("rank");
        String po = (String) this.getArguments().getString("points");


//        storage = new UserStorage();

        avatar = Bitmap.createScaledBitmap(avatar, 120, 120, true);
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        currentUser = new User(user, emai, Integer.parseInt(po), ra);
        currentUser.UID = auth.getCurrentUser().getUid();
        userPosition = new UserPosition();
        userLocations = new ArrayList<>();

        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity));
//        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

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
            Intent intentDisc = new Intent(activity, CreateDiscussion.class);
            startActivityForResult(intentDisc, CREATE_DISCUSSION);
        });

        mapController = map.getController();

        listener = new MyLocationListener();

        usersLocationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MapFragmentLifecycle", "Location change in db");

                HashMap<String, ArrayList<String>> pos = (HashMap<String, ArrayList<String>>) snapshot.getValue();
                for (DataSnapshot us : snapshot.getChildren()) {
                    String key = us.getKey();
                    UserPosition up = us.getValue(UserPosition.class);
                    boolean found = false;
                    if (userLocations.size() > 0) {
                        for (UserPosition upos : userLocations) {
                            if (upos.UID.equals(up.UID)) {
                                upos.setLatitude(up.getLatitude());
                                upos.setLongitude(up.getLongitude());
                                found = true;
                            }
                        }
                        if (!found) {
                            userLocations.add(up);
                        }
                    } else {
                        userLocations.add(up);
                    }
                }
                showAllUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Location", error.getMessage());
            }
        };

        database.child("usersLocations").addValueEventListener(usersLocationsListener);

        GeoPoint startPoint = new GeoPoint(43.3209, 21.8958); // Nis
//        GeoPoint startPoint = new GeoPoint(42.99806, 21.94611); // Leskovac

        mapController.setCenter(startPoint);
        map.setMultiTouchControls(true);

        // Check permissions
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Permission not granted", Toast.LENGTH_LONG).show();
        } else {
            startMap();
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        }
    }

    private void startMap(){
        switch (state){
//            case SHOW_FRIENDS :
//                setMyLocationOverlay();
//                friendsView();
//                break;
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
//                showAllUsers();
                break;
        }
    }

    private void showAllUsers() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                map.getOverlays().clear();
                for (UserPosition pos : userLocations) {
                    if(!currentUser.UID.equals(pos.UID)) {
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
                // update current location
                map.getOverlays().add(myLocationOverlay);
            }
        });
    }

    private void friendsView() {
//        HashMap<String, UserPosition> friendMap = UserStorage.getInstance().getFriendsLocations();
//        Collection<String> keys = friendMap.keySet();
//        final Object[] arr = keys.toArray();
//
//        if(myPlacesOverlay!=null)
//            map.getOverlays().remove(myPlacesOverlay);
//        final ArrayList<OverlayItem> overlayArrayList = new ArrayList<>();
//        for (Object o : arr) {
//            UserPosition userLocation = friendMap.get(o);
//            OverlayItem overlayItem = new OverlayItem("Item title", "Item snippet", new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude()));
//            overlayItem.setMarker(new BitmapDrawable(getResources(), UserStorage.getInstance().friendImages.get(o)));
//            overlayArrayList.add(overlayItem);
//        }
//
//        myPlacesOverlay = new ItemizedIconOverlay<>(overlayArrayList, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
//            @Override
//            public boolean onItemSingleTapUp(int index, OverlayItem item) {
//                User clickedFriend = UserStorage.getInstance().getFriend(arr[index].toString());
//                Bundle bundle = new Bundle();
//                Intent intent = new Intent(getActivity(), ProfileViewActivity.class);
//                bundle.putSerializable("user",clickedFriend);
//                intent.putExtras(bundle);
////                startActivity(intent);
//                startActivityForResult(intent, PROFILE_VIEW);
//                return false;
//            }
//
//            @Override
//            public boolean onItemLongPress(int index, OverlayItem item) {
//                return false;
//            }
//        },getContext());
//        this.map.getOverlays().add(myPlacesOverlay);
    }

    private void updateMap(){
//        map.
    }
    private void setMyLocationOverlay(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapController.setZoom(15.0);
                myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(activity), map);
                myLocationOverlay.enableMyLocation();
                myLocationOverlay.enableFollowLocation();
                myLocationOverlay.setDrawAccuracyEnabled(true);
//                myLocationOverlay.setPersonIcon(avatar);
                myLocationOverlay.setDirectionArrow(avatar, avatar);
//                map.getOverlays().add(0, myLocationOverlay);
                userPosition = new UserPosition(currentUser.getUsername(), 43.3209, 21.8958);
                userPosition.UID = currentUser.UID;
//                myLocationOverlay.runOnFirstFix(new Runnable() {
//                    @Override
//                    public void run() {
//                        myLocationOverlay.setDirectionArrow(avatar, avatar);
//                        if(map.getOverlays().size() > 0) {
//                            map.getOverlays().set(0, myLocationOverlay);
//                        } else {
//                            map.getOverlays().add(0, myLocationOverlay);
//                        }
//                        userPosition.setLongitude(myLocationOverlay.getMyLocation().getLongitude());
//                        userPosition.setLatitude(myLocationOverlay.getMyLocation().getLatitude());
//
//                        database.child("usersLocations").child(currentUser.UID).setValue(userPosition);
//
////                        showAllUsers();
//                    }
//                });
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MapFragmentLifecycle", "onPause");
        map.onPause();
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MapFragmentLifecycle", "onResume");
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

                database.child(FIREBASE_DISCUSSION).child(newDisc.key).child("data").setValue(newDisc);
                database.child(FIREBASE_DISCUSSION).child(newDisc.key).child("users").child(currentUser.UID).setValue(currentUser.getUsername());

                myLocationOverlay.disableMyLocation();
                myLocationOverlay.disableFollowLocation();
                map.getOverlays().remove(myLocationOverlay);

                m = new Marker(map);
                m.setTextLabelFontSize(40);
                m.setTextIcon(newDisc.getTopic());
                //must set the icon last
//                        m.setIcon(null);
                m.setVisible(true);
                m.setPosition(new GeoPoint(myLocationOverlay.getMyLocation().getLatitude(), myLocationOverlay.getMyLocation().getLongitude()));
                // Add the overlay to the MapView
                map.getOverlays().add(m);

                // -----------------------------------------------

                Intent intent = new Intent(activity, InDiscussion.class);
                intent.putExtra("discussion", newDisc);
                intent.putExtra("requestCode", "CREATE_DISCUSSION");
                intent.putExtra("user", currentUser);
                startActivityForResult(intent, IN_DISCUSSION_CREATOR);
//                database.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User user_data = dataSnapshot.getValue(User.class);
//                        String topic = data.getStringExtra("topic");
//                        String description = data.getStringExtra("description");
//                        String type = data.getStringExtra("type");
//                        String size = data.getStringExtra("size");
//                        Discussion newDisc = new Discussion(topic, description, myLocationOverlay.getMyLocation().getLongitude(), myLocationOverlay.getMyLocation().getLatitude(), java.text.DateFormat.getDateTimeInstance().format(new Date()), Boolean.parseBoolean(type), Integer.parseInt(size), user_data);
//                        newDisc.key = database.push().getKey();
//
//                        database.child(FIREBASE_DISCUSSION).child(newDisc.key).setValue(newDisc);
//
//                        discussions.add(newDisc);
//                        myLocationOverlay.disableMyLocation();
//                        myLocationOverlay.disableFollowLocation();
//                        map.getOverlays().remove(myLocationOverlay);
//
//                        m = new Marker(map);
//                        m.setTextLabelFontSize(40);
//                        m.setTextIcon(newDisc.getTopic());
//                        //must set the icon last
////                        m.setIcon(null);
//                        m.setVisible(true);
//                        m.setPosition(new GeoPoint(myLocationOverlay.getMyLocation().getLatitude(), myLocationOverlay.getMyLocation().getLongitude()));
//                        // Add the overlay to the MapView
//                        map.getOverlays().add(m);
//
//                        // -----------------------------------------------
//
//                        Intent intent = new Intent(activity, InDiscussion.class);
//                        intent.putExtra("discussion", newDisc);
//                        intent.putExtra("requestCode", "CREATE_DISCUSSION");
//                        intent.putExtra("user", user_data);
//                        startActivityForResult(intent, IN_DISCUSSION_CREATOR);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
            }
        } else if (requestCode == IN_DISCUSSION_CREATOR) {
            if (resultCode == RESULT_OK) {
                myLocationOverlay.enableMyLocation();
                myLocationOverlay.enableFollowLocation();
                map.getOverlays().add(myLocationOverlay);
                map.getOverlays().remove(m);
                userPosition.setLongitude(myLocationOverlay.getMyLocation().getLongitude());
                userPosition.setLatitude(myLocationOverlay.getMyLocation().getLatitude());
//                database.child("usersLocations").child(currentUser.UID).setValue(userPosition);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("MapFragmentLifecycle", "onDetach");
        locationManager = null;
        listener = null;
        usersLocationsListener = null;
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
    //    private void showFriends(){
//        HashMap<String, UserPosition> friendMap = UserStorage.getInstance().getFriendsLocations();
//        Collection<String> keys = friendMap.keySet();
//        final Object[] niz = keys.toArray();
//
//        if(myPlacesOverlay!=null)
//            map.getOverlays().remove(myPlacesOverlay);
//        final ArrayList<OverlayItem> overlayArrayList = new ArrayList<>();
//        for(int i = 0; i<niz.length;i++){
//            UserPosition userLocation = friendMap.get(niz[i]);
//            OverlayItem overlayItem = new OverlayItem("AAAA","BBBB",new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude()));
//            overlayItem.setMarker(new BitmapDrawable(getResources(),UserStorage.getInstance().friendImages.get(niz[i])));
//            overlayArrayList.add(overlayItem);
//        }
//
//        myPlacesOverlay = new ItemizedIconOverlay<>(overlayArrayList, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
//            @Override
//            public boolean onItemSingleTapUp(int index, OverlayItem item) {
//                User clickedFriend = UserStorage.getInstance().getFriend(niz[index].toString());
//                Bundle bundle = new Bundle();
//                Intent intent = new Intent(activity, ProfileView.class);
//                bundle.putSerializable("user",clickedFriend);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                return false;
//            }
//
//            @Override
//            public boolean onItemLongPress(int index, OverlayItem item) {
//                return false;
//            }
//        },getApplicationContext());
//        activity.map.getOverlays().add(myPlacesOverlay);
//    }
//
//    private void prikaziSveTokene() {
//        if(myPlacesOverlay!=null)
//            map.getOverlays().remove(myPlacesOverlay);
//        final ArrayList<OverlayItem> overlayArrayList = new ArrayList<>();
//        for(int i = 0; i<MyTokensData.getInstance().myTokens.size();i++){
//            Token token = MyTokensData.getInstance().getToken(i);
//            OverlayItem overlayItem = new OverlayItem(token.tokenIntensity.toString(),token.tokenType.toString(),new GeoPoint(token.getLatitude(),token.getLongitude()));
//            overlayItem.setMarker(activity.getResources().getDrawable(getTokenImage(token)));
//            overlayArrayList.add(overlayItem);
//            if(MyTokensData.getInstance().currentLocation == null){
//                double a = calculateDistanceBetweenTwoGeoPoints(21.8958,43.3209,token.getLongitude(),token.getLatitude());
//                token.setDistance(a);
//            }
//            else{
//                double a = calculateDistanceBetweenTwoGeoPoints(MyTokensData.getInstance().currentLocation.getLongitude(),MyTokensData.getInstance().currentLocation.getLatitude(),token.getLongitude(),token.getLatitude());
//                token.setDistance(a);
//            }
//        }
//
//        myPlacesOverlay = new ItemizedIconOverlay<>(overlayArrayList, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
//            @Override
//            public boolean onItemSingleTapUp(int index, OverlayItem item) {
//                Token token=MyTokensData.getInstance().getToken(index);
//                Bundle bundle = new Bundle();
//                Intent intent = new Intent(activity, ViewToken.class);
//                bundle.putSerializable("token",token);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                return false;
//            }
//            @Override
//            public boolean onItemLongPress(int index, OverlayItem item) {
//                Token token=MyTokensData.getInstance().getToken(index);
//                if(token.distance<=1300 && !token.getUserId().equals(auth.getCurrentUser().getUid())){
//                    Bundle bundle = new Bundle();
//                    Intent intent = new Intent(activity, CollectTokenActivity.class);
//                    bundle.putSerializable("token",token);
//                    intent.putExtras(bundle);
//                    finish();
//                    startActivity(intent);
//                }
//                return false;
//            }
//        },getApplicationContext());
//        activity.map.getOverlays().add(myPlacesOverlay);
//    }
}
