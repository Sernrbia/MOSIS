package com.example.mosis_ispit.firstscreen.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegisterFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECT = 2;
    private RegisterFragmentListener listener;
    private EditText username;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button btnRegister;
    private ImageView image;
    private Button addImage;
    private Button findImage;
    private Uri imgUri;
    private Bitmap avatar;
    private FirebaseAuth auth;

    public interface RegisterFragmentListener {
        void onInputRegister(User user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_layout, container, false);

        auth = FirebaseAuth.getInstance();
        username = view.findViewById(R.id.register_editUsername);
        firstName = view.findViewById(R.id.register_editFirstName);
        lastName = view.findViewById(R.id.register_editLastName);
        email = view.findViewById(R.id.register_editEmail);
        password = view.findViewById(R.id.register_editPassword);
        confirmPassword = view.findViewById(R.id.register_editConfirmPassword);
        addImage = view.findViewById(R.id.register_add_image);
        findImage = view.findViewById(R.id.register_find_image);
        image = view.findViewById(R.id.register_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        findImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnRegister = view.findViewById(R.id.register_btnRegister);

        btnRegister.setOnClickListener(v -> {
            if (password.getText().toString().equals(confirmPassword.getText().toString()) && avatar != null) {
                User user = new User(username.getText().toString().trim(), firstName.getText().toString().trim(), lastName.getText().toString().trim(), email.getText().toString().trim(), password.getText().toString().trim(), image);
                listener.onInputRegister(user);
            } else if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(getActivity(), "image not selected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "passwords don't match", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setEnabled(false);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnRegister.setEnabled(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty() && avatar != null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnRegister.setEnabled(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty() && avatar != null);
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
                btnRegister.setEnabled(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty() && avatar != null);
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
                btnRegister.setEnabled(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty() && avatar != null);
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
                btnRegister.setEnabled(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty() && avatar != null);
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
                btnRegister.setEnabled(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty() && avatar != null);
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

    private void pickImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, REQUEST_IMAGE_SELECT);
    }

//    private void uploadImage () {
//        FirebaseUser currentUser=auth.getCurrentUser();
//        image.setDrawingCacheEnabled(true);
//        image.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//        storage.child(USER_CHILD).child(currentUser.getUid()).child("profile").putBytes(data);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            avatar = cropToSquare(photo);
            image.setImageBitmap(avatar);
        } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = (Bitmap) MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            avatar = cropToSquare(bitmap);
            image.setImageBitmap(avatar);
        }
        btnRegister.setEnabled(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty() && avatar != null);
    }

    public static Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = Math.min(height, width);
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = Math.max(cropW, 0);
        int cropH = (height - width) / 2;
        cropH = Math.max(cropH, 0);
        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
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
