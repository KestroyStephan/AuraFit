package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class WorkoutUploadActivity extends AppCompatActivity {

    ImageView workoutImage;
    Button saveWorkoutButton;
    EditText workoutTitle, workoutDesc, workoutType;
    String imageURL;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_upload);

        workoutImage = findViewById(R.id.uploadImage);
        workoutDesc = findViewById(R.id.uploadDes);
        workoutTitle = findViewById(R.id.uploadTopic);
        workoutType = findViewById(R.id.uploadLang);
        saveWorkoutButton = findViewById(R.id.saveButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            workoutImage.setImageURI(uri);
                        } else {
                            Toast.makeText(WorkoutUploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        workoutImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        saveWorkoutButton.setOnClickListener(view -> saveData());
    }

    public void saveData() {
        if (uri == null) {
            Toast.makeText(this, "Please select an image first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = System.currentTimeMillis() + "." + getContentResolver().getType(uri).split("/")[1];

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Workout Images")
                .child(fileName);

        AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutUploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete());
            Uri urlImage = uriTask.getResult();
            imageURL = urlImage.toString();
            dialog.dismiss();
            uploadData();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(WorkoutUploadActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void uploadData() {
        String title = workoutTitle.getText().toString().trim();
        String desc = workoutDesc.getText().toString().trim();
        String type = workoutType.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        DataClass workoutData = new DataClass(title, desc, type, imageURL);
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        FirebaseDatabase.getInstance().getReference("Workout Planner").child(currentDate)
                .setValue(workoutData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(WorkoutUploadActivity.this, "Workout saved successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(WorkoutUploadActivity.this, "Failed to save workout!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(WorkoutUploadActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
