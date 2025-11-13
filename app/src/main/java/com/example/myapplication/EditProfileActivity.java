package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView profileImageView;
    private EditText editName, editAge, editGender, editWeight, editHeight;
    private Button updateBtn, btnUploadImage, btnRemoveImage;

    private Uri imageUri;

    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        // Init views
        profileImageView = findViewById(R.id.editProfileImage);
        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editGender = findViewById(R.id.editGender);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        updateBtn = findViewById(R.id.updateBtn);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnRemoveImage = findViewById(R.id.btnRemoveImage);

        loadProfile();

        btnUploadImage.setOnClickListener(v -> chooseImage());
        btnRemoveImage.setOnClickListener(v -> {
            profileImageView.setImageResource(R.drawable.profile); // default image
            userRef.child("imageUrl").removeValue();
        });

        updateBtn.setOnClickListener(v -> updateProfile());
    }

    private void loadProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    editName.setText(snapshot.child("name").getValue(String.class));
                    editAge.setText(snapshot.child("age").getValue(String.class));
                    editGender.setText(snapshot.child("gender").getValue(String.class));
                    editWeight.setText(snapshot.child("weight").getValue(String.class));
                    editHeight.setText(snapshot.child("height").getValue(String.class));

                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    if (imageUrl != null) {
                        Glide.with(EditProfileActivity.this).load(imageUrl).into(profileImageView);
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void updateProfile() {
        String name = editName.getText().toString();
        String age = editAge.getText().toString();
        String gender = editGender.getText().toString();
        String weight = editWeight.getText().toString();
        String height = editHeight.getText().toString();

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("age", age);
        map.put("gender", gender);
        map.put("weight", weight);
        map.put("height", height);

        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(currentUser.getUid() + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        map.put("imageUrl", uri.toString());
                        userRef.updateChildren(map).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                                finish(); // Close activity to trigger ProfileActivity refresh
                            }
                        });
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            );
        } else {
            userRef.updateChildren(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity to trigger ProfileActivity refresh
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
