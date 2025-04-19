package com.example.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class LiveWorkoutActivity extends AppCompatActivity {

    private NumberPicker hourPicker, minutePicker, secondPicker;
    private TextView currentDateTime, timerCountdown;
    private Button startTimerBtn;
    private RecyclerView workoutHistoryView;

    private CountDownTimer countDownTimer;
    private long timerInMillis;

    private DatabaseReference databaseReference;
    private ArrayList<WorkoutHistory> historyList;
    private WorkoutHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_workout);

        // Initialize views
        currentDateTime = findViewById(R.id.currentDateTime);
        timerCountdown = findViewById(R.id.timerCountdown);
        startTimerBtn = findViewById(R.id.startTimerBtn);
        workoutHistoryView = findViewById(R.id.workoutHistoryView);

        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        secondPicker = findViewById(R.id.secondPicker);

        // Setup pickers
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        // Show current date and time
        String dateTime = DateFormat.format("dd MMM yyyy HH:mm:ss", Calendar.getInstance()).toString();
        currentDateTime.setText(dateTime);

        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("WorkoutHistory");

        // Setup RecyclerView
        workoutHistoryView.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        adapter = new WorkoutHistoryAdapter(this, historyList, databaseReference);
        workoutHistoryView.setAdapter(adapter);

        fetchHistory();

        startTimerBtn.setOnClickListener(v -> {
            int hours = hourPicker.getValue();
            int minutes = minutePicker.getValue();
            int seconds = secondPicker.getValue();

            timerInMillis = (hours * 3600L + minutes * 60L + seconds) * 1000L;
            if (timerInMillis > 0) {
                startCountdown(timerInMillis);
            } else {
                Toast.makeText(this, "Please set a valid time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCountdown(long timeInMillis) {
        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secs = millisUntilFinished / 1000;
                long h = secs / 3600;
                long m = (secs % 3600) / 60;
                long s = secs % 60;
                timerCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s));
            }

            @Override
            public void onFinish() {
                timerCountdown.setText("00:00:00");
                saveWorkoutToFirebase();
            }
        }.start();
    }

    private void saveWorkoutToFirebase() {
        String key = databaseReference.push().getKey();
        if (key == null) return;

        String currentTime = DateFormat.format("dd MMM yyyy HH:mm:ss", Calendar.getInstance()).toString();
        WorkoutHistory history = new WorkoutHistory(key, currentTime, formatTime(timerInMillis));
        databaseReference.child(key).setValue(history);
        fetchHistory();
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void fetchHistory() {
        databaseReference.get().addOnCompleteListener(task -> {
            historyList.clear();
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    WorkoutHistory history = snapshot.getValue(WorkoutHistory.class);
                    if (history != null) {
                        historyList.add(history);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
