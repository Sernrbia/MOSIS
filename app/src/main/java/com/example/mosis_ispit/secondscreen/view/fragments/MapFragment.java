package com.example.mosis_ispit.secondscreen.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.Discussion;
import com.example.mosis_ispit.addon.MyLocationListener;
import com.example.mosis_ispit.addon.ProfileView;
import com.example.mosis_ispit.addon.User;
import com.example.mosis_ispit.addon.UserPosition;
import com.example.mosis_ispit.addon.UserStorage;
import com.example.mosis_ispit.secondscreen.view.CreateDiscussion;
import com.example.mosis_ispit.secondscreen.view.InDiscussion;
import com.example.mosis_ispit.secondscreen.view.ProfileViewActivity;
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

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
    private MyLocationListener locationListener ;
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

    public boolean showFriends = false;
    public boolean showDiscussions = false;
    public boolean clearFilter = false;


    private int state = 0;
    private boolean selCoorsEnabled = false;
    private GeoPoint placeLoc = null;
    private boolean selected = false;
    private Menu menu = null;
    Location currentLocation = null;
    LocationManager locationManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED){
            this.requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE},LOCATION_PERMISSION);
        }

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
        discussions = new ArrayList<Discussion>();
        avatar = Bitmap.createScaledBitmap(avatar, 120, 120, true);
        database= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        currentUser.UID = auth.getCurrentUser().getUid();
        myMarkers = new ArrayList<Marker>();
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        locationListener = new MyLocationListener();
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
//            Bundle bundle = new Bundle();
            Intent intentDisc = new Intent(getActivity(), CreateDiscussion.class);
//            bundle.putSerializable("user",clickedFriend);
//            intent.putExtras(bundle);
//            startActivity(intent);
            startActivityForResult(intentDisc, CREATE_DISCUSSION);
        });

        mapController = map.getController();

        userPosition = new UserPosition();
        GeoPoint startPoint = new GeoPoint(43.3209, 21.8958); // Nis
//        GeoPoint startPoint = new GeoPoint(42.99806, 21.94611); // Leskovac
        mapController.setCenter(startPoint);
//        map.setBuiltInZoomControls(true);
//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) locationListener);
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if( location != null ) {
//            currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
//        }
        map.setMultiTouchControls(true);
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED){
            this.requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE},LOCATION_PERMISSION);
        }
        else{
            startMap();
        }
        return view;
    }

    @SuppressLint("Missing Permissions")
    @Override
    public void onRequestPermissionsResult(int reqCode,String permissions[],int[] grantedResults ){
        startMap();
    }

    private void startMap(){
        switch (state){
            case SHOW_FRIENDS :
                setMyLocationOverlay();
                friendsView();
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
                showAllUsers();
                break;
        }
    }

    private void showAllUsers() {
        storage.updateAllUsers();
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapController.setZoom(15.0);
                myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), map);
                myLocationOverlay.enableMyLocation();
                myLocationOverlay.enableFollowLocation();
                myLocationOverlay.setDrawAccuracyEnabled(true);
                myLocationOverlay.setPersonIcon(avatar);
                map.getOverlays().add(0, myLocationOverlay);
                userPosition = new UserPosition(currentUser.getUsername(), 43.3209, 21.8958);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        myLocationOverlay.enableMyLocation();
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

                discussions.add(newDisc);
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

                Intent intent = new Intent(getActivity(), InDiscussion.class);
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
//                        Intent intent = new Intent(getActivity(), InDiscussion.class);
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
                database.child("usersLocations").child(currentUser.UID).setValue(userPosition);
            }
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
//                Intent intent = new Intent(getActivity(), ProfileView.class);
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
//        getActivity().map.getOverlays().add(myPlacesOverlay);
//    }
//
//    private void prikaziSveTokene() {
//        if(myPlacesOverlay!=null)
//            map.getOverlays().remove(myPlacesOverlay);
//        final ArrayList<OverlayItem> overlayArrayList = new ArrayList<>();
//        for(int i = 0; i<MyTokensData.getInstance().myTokens.size();i++){
//            Token token = MyTokensData.getInstance().getToken(i);
//            OverlayItem overlayItem = new OverlayItem(token.tokenIntensity.toString(),token.tokenType.toString(),new GeoPoint(token.getLatitude(),token.getLongitude()));
//            overlayItem.setMarker(getActivity().getResources().getDrawable(getTokenImage(token)));
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
//                Intent intent = new Intent(getActivity(), ViewToken.class);
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
//                    Intent intent = new Intent(getActivity(), CollectTokenActivity.class);
//                    bundle.putSerializable("token",token);
//                    intent.putExtras(bundle);
//                    finish();
//                    startActivity(intent);
//                }
//                return false;
//            }
//        },getApplicationContext());
//        getActivity().map.getOverlays().add(myPlacesOverlay);
//    }
}
