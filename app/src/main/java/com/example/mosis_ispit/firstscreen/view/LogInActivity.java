package com.example.mosis_ispit.firstscreen.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.addon.User;
import com.example.mosis_ispit.firstscreen.view.fragments.LoginFragment;
import com.example.mosis_ispit.firstscreen.view.fragments.RegisterFragment;
import com.example.mosis_ispit.R;
import com.example.mosis_ispit.secondscreen.view.MainScreenActivity;
import com.example.mosis_ispit.secondscreen.view.fragments.MapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class LogInActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, RegisterFragment.RegisterFragmentListener {
    private Fragment selectedFragment;
    private FirebaseAuth auth;
    private FirebaseUser activeUser;
    private DatabaseReference database;
    private StorageReference storage;
    public static final String USER_CHILD="users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference();
        storage= FirebaseStorage.getInstance().getReference();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (selectedFragment == null) {
            selectedFragment = new LoginFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigationLogin:
                    selectedFragment = new LoginFragment();
                    break;
                case R.id.navigationRegister:
                    selectedFragment = new RegisterFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    public void onInputLoginSent(CharSequence email, CharSequence password) {
        if(selectedFragment instanceof LoginFragment) {
            auth.signInWithEmailAndPassword(email.toString(), password.toString())
                    .addOnCompleteListener(LogInActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(LogInActivity.this, MainScreenActivity.class);
                            activeUser = auth.getCurrentUser();
                            String uid = activeUser.getUid();
//                            MyTokensData.getInstance().init();
//                            AllUsersData.getInstance().resetListeners();
//                            Intent i1= new Intent(LogInActivity.this, LocationService.class);
//                            LogInActivity.this.startService(i1);
//                            Intent i = new Intent(LogInActivity.this, Map.class);
//                            i.putExtra("state", Map.SHOW_MAP);
                            ((LoginFragment) selectedFragment).notification("Authentication success");
                            startActivity(i);
                            finish();
                        } else {
                            ((LoginFragment) selectedFragment).notification("E-mail or password incorrect");
                        }
                    });
        }
    }

    @Override
    public void onInputRegister(User user) {
        if(selectedFragment instanceof RegisterFragment) {
            auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(LogInActivity.this, task -> {
                        if (task.isSuccessful()) {
                            activeUser=auth.getCurrentUser();
                            user.UID = activeUser.getUid();
                            uploadImage(user.getImage());
                            database.child(USER_CHILD).child(activeUser.getUid()).child("data").setValue(user);
                            ((RegisterFragment) selectedFragment).notification("Registration success");
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch(FirebaseAuthWeakPasswordException e) {
                                ((RegisterFragment) selectedFragment).notification("Registration failed: Weak password");
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                ((RegisterFragment) selectedFragment).notification("Registration failed: email not in correct format");
                            } catch(FirebaseAuthUserCollisionException e) {
                                ((RegisterFragment) selectedFragment).notification("Registration failed: user already exists");
                            } catch(Exception e) {
                                ((RegisterFragment) selectedFragment).notification("Registration failed");
                            }
                        }
                    });
        }
    }

    private void uploadImage (@NotNull ImageView image) {
        FirebaseUser currentUser=auth.getCurrentUser();
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        storage.child(USER_CHILD).child(currentUser.getUid()).child("profile_img").putBytes(data);
    }

    @Override
    public void onStart() {
        super.onStart();

        activeUser = auth.getCurrentUser();
        if (activeUser != null) {
            Intent i = new Intent(LogInActivity.this, MainScreenActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activeUser = auth.getCurrentUser();
        if (activeUser != null) {
            Intent i = new Intent(LogInActivity.this, MainScreenActivity.class);
            startActivity(i);
            finish();
        }
    }

}
