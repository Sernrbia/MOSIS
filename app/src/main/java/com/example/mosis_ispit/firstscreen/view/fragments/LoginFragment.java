package com.example.mosis_ispit.firstscreen.view.fragments;

import android.content.Context;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.addon.Animation;
import com.example.mosis_ispit.R;

import org.jetbrains.annotations.NotNull;

public class LoginFragment extends Fragment {
    private LoginFragmentListener listener;
    private EditText email;
    private EditText password;
    private Button btnLogin;
    private Animation animation;

    public interface LoginFragmentListener {
        void onInputLoginSent(CharSequence email, CharSequence password);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_layout, container, false);
        // Inflate the layout for this fragment

        email = view.findViewById(R.id.main_editEmail);
        password = view.findViewById(R.id.main_editPassword);

        btnLogin = view.findViewById(R.id.main_btnLogin);

        btnLogin.setOnClickListener(v -> {
            animation.startAnimation();
            CharSequence us = email.getText();
            CharSequence pass = password.getText();
            listener.onInputLoginSent(us, pass);
        });

        animation = new Animation(view.findViewById(R.id.main_loading));

//        btnLogin.setOnClickListener(view1 -> {
//            animation.startAnimation();
//            auth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
//                    .addOnCompleteListener(LoginFragment.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            animation.stopAnimation();
//                            if (task.isSuccessful()) {
////                                MyTokensData.getInstance().init();
////                                AllUsersData.getInstance().resetListeners();
////                                Intent i1= new Intent(LoginActivity.this, LocationService.class);
////                                LoginActivity.this.startService(i1);
////                                Intent i = new Intent(LoginActivity.this, Map.class);
////                                i.putExtra("state", Map.SHOW_MAP);
////                                startActivity(i);
////                                finish();
//                            }
//                            else {
//                                Toast.makeText(this, "Email or password incorrect", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    })
//        });

        btnLogin.setEnabled(false);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnLogin.setEnabled(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnLogin.setEnabled(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    public void notification(String result) {
        animation.stopAnimation();
        Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if(context instanceof LoginFragmentListener) {
            listener = (LoginFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
