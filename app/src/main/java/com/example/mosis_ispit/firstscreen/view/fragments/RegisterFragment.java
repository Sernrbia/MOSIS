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

import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProviders;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.User;

import org.jetbrains.annotations.NotNull;

public class RegisterFragment extends Fragment {
//    RegisterVM viewmodel;
    private RegisterFragmentListener listener;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText phone;
    private Button btnRegister;
//    private Animation animation;

    public interface RegisterFragmentListener {
        void onInputRegister(User user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_layout, container, false);

//        animation = new Animation(view.findViewById(R.id.main_loading));

        firstName = view.findViewById(R.id.register_editFirstName);
        lastName = view.findViewById(R.id.register_editLastName);
        email = view.findViewById(R.id.register_editEmail);
        password = view.findViewById(R.id.register_editPassword);
        confirmPassword = view.findViewById(R.id.register_editConfirmPassword);
        phone = view.findViewById(R.id.register_editPhone);

        btnRegister = view.findViewById(R.id.register_btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                    User user = new User("default", firstName.getText().toString().trim(), lastName.getText().toString().trim(), email.getText().toString().trim(), password.getText().toString().trim(), phone.getText().toString().trim());
                    listener.onInputRegister(user);
                } else {
                    Toast.makeText(getActivity(), "passwords don't match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setEnabled(false);

        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty() || phone.getText().toString().isEmpty()){
                    btnRegister.setEnabled(false);
                }
                else {
                    btnRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty() || phone.getText().toString().isEmpty()) {
                    btnRegister.setEnabled(false);
                }
                else {
                    btnRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty() || phone.getText().toString().isEmpty()){
                    btnRegister.setEnabled(false);
                }
                else {
                    btnRegister.setEnabled(true);
                }
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
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty() || phone.getText().toString().isEmpty()){
                    btnRegister.setEnabled(false);
                }
                else {
                    btnRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty() || phone.getText().toString().isEmpty()){
                    btnRegister.setEnabled(false);
                }
                else {
                    btnRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty() || phone.getText().toString().isEmpty()){
                    btnRegister.setEnabled(false);
                }
                else {
                    btnRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    public void notification(String result) {
//        animation.stopAnimation();
        Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if(context instanceof RegisterFragmentListener) {
            listener = (RegisterFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RegisterFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
