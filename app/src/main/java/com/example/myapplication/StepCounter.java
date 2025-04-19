package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.annotation.NonNull;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StepCounter extends AppCompatActivity implements SensorEventListener {

    private static final int ACTIVITY_RECOGNITION_REQUEST_CODE = 100;

    private TextView stepCountTextView, dailyLimitTextView, dateTextView, timeTextView;
    private Button updateLimitButton, historyButton;

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private boolean isSensorAvailable = false;

    private int stepCount = 0;
    private int dailyLimit = 10000; // Default

    private DatabaseReference userStepsRef;
    private String userId;

    private Handler timeHandler = new Handler();
    private Runnable timeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stepcounter);

        stepCountTextView = findViewById(R.id.stepCountTextView);
        dailyLimitTextView = findViewById(R.id.dailyLimitTextView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        updateLimitButton = findViewById(R.id.updateLimitButton);
        historyButton = findViewById(R.id.historyButton);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userStepsRef = FirebaseDatabase.getInstance().getReference("Steps").child(userId);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepDetectorSensor != null) {
            isSensorAvailable = true;
        } else {
            Toast.makeText(this, "Step Detector Sensor not available", Toast.LENGTH_SHORT).show();
        }

        // Buttons
        updateLimitButton.setOnClickListener(v -> startActivity(new Intent(StepCounter.this, UpdateDailyLimitActivity.class)));
        historyButton.setOnClickListener(v -> startActivity(new Intent(StepCounter.this, StepHistoryActivity.class)));

        // Load Firebase daily limit and set date/time
        loadDailyLimit();
        showDate();
        startClock();

        // Request permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    ACTIVITY_RECOGNITION_REQUEST_CODE);
        }
    }

    private void loadDailyLimit() {
        userStepsRef.child("dailyLimit").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dailyLimit = snapshot.getValue(Integer.class);
                } else {
                    userStepsRef.child("dailyLimit").setValue(dailyLimit);
                }
                dailyLimitTextView.setText("Daily Limit: " + dailyLimit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void showDate() {
        String currentDate = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
        dateTextView.setText("Date: " + currentDate);
    }

    private void startClock() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        .format(Calendar.getInstance().getTime());
                timeTextView.setText("Time: " + currentTime);
                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.post(timeRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepCount++;
            stepCountTextView.setText(String.valueOf(stepCount));

            // Save to Firebase under today's date
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Calendar.getInstance().getTime());
            StepData data = new StepData(stepCount, dailyLimit);
            userStepsRef.child("History").child(today).setValue(data);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorAvailable &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                        == PackageManager.PERMISSION_GRANTED) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        timeHandler.removeCallbacks(timeRunnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isSensorAvailable) {
                sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI);
            }
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }
}
