package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedpreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String storedUsername = sharedpreferences.getString("username", "");
        TextView textViewName = findViewById(R.id.textViewName);
        textViewName.setText("Hello ,"+storedUsername);

        ImageView profile = findViewById(R.id.imageViewProfile);
        profile.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        // logout
        CardView exit = findViewById(R.id.logout);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
        // Workout
        CardView workout = findViewById(R.id.workout);
        workout.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, WorkoutActivity.class)));
        // live Workout
        CardView workoutTrack = findViewById(R.id.workouttracker);
        workoutTrack.setOnClickListener(view-> startActivity(new Intent(HomeActivity.this, LiveWorkoutActivity.class)));
        // Step Count
        CardView stepCount = findViewById(R.id.stepCount);
        stepCount.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, StepCounter.class)));
        // Meal plane
        CardView mealPlane = findViewById(R.id.mealPlane);
        mealPlane.setOnClickListener(view ->startActivity(new Intent(HomeActivity.this, Meal_Planner_Activity.class)));
        // Progress
        CardView progress = findViewById(R.id.progress);
        progress.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, Analytics_screenActivity.class)));
    }
}