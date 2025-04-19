// StepHistoryActivity.java - View history in a list
package com.example.myapplication;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StepHistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private StepHistoryAdapter adapter;
    private List<String> historyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_history);

        historyListView = findViewById(R.id.historyListView);
        historyItems = new ArrayList<>();
        adapter = new StepHistoryAdapter(this, historyItems);
        historyListView.setAdapter(adapter);

        loadStepHistory();
    }

    private void loadStepHistory() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("Steps").child(userId).child("History");

        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyItems.clear();
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    StepData data = dateSnapshot.getValue(StepData.class);
                    historyItems.add(date + " - Steps: " + data.getSteps());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StepHistoryActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });
    }
}