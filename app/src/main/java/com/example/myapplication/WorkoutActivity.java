package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    FloatingActionButton fabWorkout;
    RecyclerView recyclerWorkout;
    SearchView searchWorkout;
    List<DataClass> workoutList;
    MyAdapter adapter;
    DatabaseReference databaseReference;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout); // Make sure you create this layout

        recyclerWorkout = findViewById(R.id.recyclerWorkout); // Make sure the ID matches in XML
        fabWorkout = findViewById(R.id.fabWorkout);
        searchWorkout = findViewById(R.id.searchWorkout);
        searchWorkout.clearFocus();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerWorkout.setLayoutManager(layoutManager);
        workoutList = new ArrayList<>();
        adapter = new MyAdapter(WorkoutActivity.this, workoutList);
        recyclerWorkout.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("Workout Planner");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workoutList.clear();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    try {
                        DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                        if (dataClass != null) {
                            dataClass.setKey(itemSnapshot.getKey());
                            workoutList.add(dataClass);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();

                if (workoutList.isEmpty()) {
                    Toast.makeText(WorkoutActivity.this, "No workouts found in Firebase.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(WorkoutActivity.this, "Firebase Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        searchWorkout.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchWorkoutList(newText);
                return true;
            }
        });

        fabWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutActivity.this, WorkoutUploadActivity.class);
                startActivity(intent);
            }
        });
    }

    private void searchWorkoutList(String text) {
        ArrayList<DataClass> filteredList = new ArrayList<>();
        for (DataClass item : workoutList) {
            if (item.getDataTitle() != null && item.getDataTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.searchDataList(filteredList);
    }
}
