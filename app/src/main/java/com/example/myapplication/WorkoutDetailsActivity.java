package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class WorkoutDetailsActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailLang;
    ImageView detailImage;
    FloatingActionButton deleteButton, editButton;
    String key = "", imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail); // Rename layout file accordingly

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        detailLang = findViewById(R.id.detailLang);

        Intent intent = getIntent();
        if (intent != null) {
            detailDesc.setText(intent.getStringExtra("Description"));
            detailTitle.setText(intent.getStringExtra("Title"));
            detailLang.setText(intent.getStringExtra("Language"));
            key = intent.getStringExtra("Key");
            imageUrl = intent.getStringExtra("Image");

            Glide.with(this).load(imageUrl).into(detailImage);
        }

        deleteButton.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Workout Planner");
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

            storageReference.delete().addOnSuccessListener(unused -> {
                reference.child(key).removeValue();
                Toast.makeText(WorkoutDetailsActivity.this, "Workout Deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WorkoutDetailsActivity.this, WorkoutActivity.class));
                finish();
            });
        });

        editButton.setOnClickListener(v -> {
            Intent updateIntent = new Intent(WorkoutDetailsActivity.this, WorkoutUpdateActivity.class);
            updateIntent.putExtra("Title", detailTitle.getText().toString());
            updateIntent.putExtra("Description", detailDesc.getText().toString());
            updateIntent.putExtra("Language", detailLang.getText().toString());
            updateIntent.putExtra("Image", imageUrl);
            updateIntent.putExtra("Key", key);
            startActivity(updateIntent);
        });
    }
}
