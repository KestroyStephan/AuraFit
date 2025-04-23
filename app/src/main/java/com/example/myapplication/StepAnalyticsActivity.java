package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class StepAnalyticsActivity extends AppCompatActivity {

    private TextView weeklyAverageTextView, totalStepsTextView, daysOverLimitTextView;
    private BarChart barChart;

    private DatabaseReference userStepsRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_screen);

        weeklyAverageTextView = findViewById(R.id.weeklyAverageTextView);
        totalStepsTextView = findViewById(R.id.totalStepsTextView);
        daysOverLimitTextView = findViewById(R.id.daysOverLimitTextView);
        barChart = findViewById(R.id.barChart);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userStepsRef = FirebaseDatabase.getInstance().getReference("Steps").child(userId).child("History");

        loadAnalytics();
    }

    private void loadAnalytics() {
        userStepsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalSteps = 0;
                int daysOverLimit = 0;
                int count = 0;

                List<BarEntry> barEntries = new ArrayList<>();
                List<String> dates = new ArrayList<>();

                // Get last 7 days
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                for (int i = 6; i >= 0; i--) {
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_YEAR, -i);
                    String dateKey = sdf.format(calendar.getTime());

                    DataSnapshot daySnapshot = snapshot.child(dateKey);
                    StepData data = daySnapshot.getValue(StepData.class);

                    int steps = 0;
                    if (data != null) {
                        steps = data.getSteps();
                        totalSteps += steps;
                        if (steps > data.getDailyLimit()) {
                            daysOverLimit++;
                        }
                        count++;
                    }

                    dates.add(dateKey.substring(5)); // MM-DD
                    barEntries.add(new BarEntry(6 - i, steps));
                }

                int average = (count > 0) ? totalSteps / count : 0;
                weeklyAverageTextView.setText("Weekly Average: " + average);
                totalStepsTextView.setText("Total Steps: " + totalSteps);
                daysOverLimitTextView.setText("Days Over Limit: " + daysOverLimit);

                BarDataSet dataSet = new BarDataSet(barEntries, "Steps");
                dataSet.setColor(getResources().getColor(R.color.teal_700)); // or any color
                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.9f);

                barChart.setData(barData);
                barChart.setFitBars(true);
                barChart.getDescription().setEnabled(false);
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                barChart.getXAxis().setGranularity(1f);
                barChart.getXAxis().setLabelRotationAngle(-45);
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
