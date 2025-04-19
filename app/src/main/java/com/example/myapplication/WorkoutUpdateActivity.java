package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkoutUpdateActivity extends AppCompatActivity {

    private ImageView updateImage;
    private Button updateButton;
    private EditText updateDesc, updateTitle, updateLang;

    private Uri uri;
    private String title, desc, lang, imageUrl, key, oldImageURL;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_update); // Update this layout name accordingly

        initViews();
        setupImagePicker();
        loadIntentData();

        updateButton.setOnClickListener(view -> {
            if (validateInput()) {
                if (uri != null) {
                    uploadNewImage();
                } else {
                    imageUrl = oldImageURL;
                    updateData();
                }
            }
        });
    }

    private void initViews() {
        updateImage = findViewById(R.id.updateImage);
        updateButton = findViewById(R.id.updateButton);
        updateDesc = findViewById(R.id.updateDesc);
        updateLang = findViewById(R.id.updateLang);
        updateTitle = findViewById(R.id.updateTitle);
    }

    private void setupImagePicker() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        uri = data.getData();
                        updateImage.setImageURI(uri);
                    } else {
                        Toast.makeText(WorkoutUpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                });

        updateImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });
    }

    private void loadIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this).load(bundle.getString("Image")).into(updateImage);
            updateTitle.setText(bundle.getString("Title"));
            updateDesc.setText(bundle.getString("Description"));
            updateLang.setText(bundle.getString("Language"));
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");

            databaseReference = FirebaseDatabase.getInstance().getReference("Workout Planner").child(key);
        }
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(updateTitle.getText()) ||
                TextUtils.isEmpty(updateDesc.getText()) ||
                TextUtils.isEmpty(updateLang.getText())) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadNewImage() {
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date())
                + "." + getContentResolver().getType(uri).split("/")[1];

        storageReference = FirebaseStorage.getInstance().getReference("Workout Images").child(fileName);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri urlImage = uriTask.getResult();
                    imageUrl = urlImage.toString();
                    dialog.dismiss();
                    updateData();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateData() {
        title = updateTitle.getText().toString().trim();
        desc = updateDesc.getText().toString().trim();
        lang = updateLang.getText().toString().trim();

        DataClass dataClass = new DataClass(title, desc, lang, imageUrl);

        databaseReference.setValue(dataClass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (uri != null) {
                            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                            reference.delete(); // Delete old image
                        }
                        Toast.makeText(this, "Workout Updated Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(WorkoutUpdateActivity.this, WorkoutActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
