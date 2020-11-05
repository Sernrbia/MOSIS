package com.example.mosis_ispit.firstscreen.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.addon.User;
import com.example.mosis_ispit.firstscreen.view.fragments.LoginFragment;
import com.example.mosis_ispit.firstscreen.view.fragments.RegisterFragment;
import com.example.mosis_ispit.R;
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

public class LogInActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, RegisterFragment.RegisterFragmentListener {
    private Fragment selectedFragment;
    private FirebaseAuth auth;
    private FirebaseUser activeUser;
    private DatabaseReference database;
    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference();
        storage= FirebaseStorage.getInstance().getReference();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(selectedFragment == null) {
            selectedFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
//        logIn.setOnClickListener(this);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

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
//        if(selectedFragment instanceof LoginFragment) {
//            ((LoginFragment) selectedFragment).notification(email.toString() + " " + password.toString());
//        }

        if(selectedFragment instanceof LoginFragment) {
            auth.signInWithEmailAndPassword(email.toString(), password.toString())
                    .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                ((LoginFragment) selectedFragment).notification("Authentification success");
//                                activeUser = auth.getCurrentUser();
//                                Intent i = new Intent(LogInActivity.this, MainScreenActivity.class);
                                //                            MyTokensData.getInstance().init();
                                //                            AllUsersData.getInstance().resetListeners();
                                //                            Intent i1= new Intent(LogInActivity.this, LocationService.class);
                                //                            LogInActivity.this.startService(i1);
                                //                            Intent i = new Intent(LogInActivity.this, Map.class);
                                //                            i.putExtra("state", Map.SHOW_MAP);
//                                startActivity(i);
//                                finish();
                            } else {
                                ((LoginFragment) selectedFragment).notification("E-mail or password incorrect");
                            }
                        }
                    });
        }
    }

    @Override
    public void onInputRegister(User user) {
        if(selectedFragment instanceof RegisterFragment) {
            auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                ((RegisterFragment) selectedFragment).notification("Registration success");

//                                activeUser = auth.getCurrentUser();
//                                Intent i = new Intent(LogInActivity.this, MainScreenActivity.class);
//                                //                            MyTokensData.getInstance().init();
//                                //                            AllUsersData.getInstance().resetListeners();
//                                //                            Intent i1= new Intent(LogInActivity.this, LocationService.class);
//                                //                            LogInActivity.this.startService(i1);
//                                //                            Intent i = new Intent(LogInActivity.this, Map.class);
//                                //                            i.putExtra("state", Map.SHOW_MAP);
//                                startActivity(i);
//                                finish();
                            } else {
                                try {
                                    throw task.getException();
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
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        activeUser = auth.getCurrentUser();
        if (activeUser != null) {
            Toast.makeText(this, activeUser.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
