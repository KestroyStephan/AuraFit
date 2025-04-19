package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class Meal_Planner_Activity extends AppCompatActivity {

    FloatingActionButton fab;
    RecyclerView recyclerView;
    SearchView searchView;
    List<DataClass> dataList;
    MyAdapter adapter;
    DatabaseReference databaseReference;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_planner);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        dataList = new ArrayList<>();
        adapter = new MyAdapter(Meal_Planner_Activity.this, dataList);
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(Meal_Planner_Activity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("Meal Planner");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    try {
                        DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                        if (dataClass != null) {
                            dataClass.setKey(itemSnapshot.getKey());
                            dataList.add(dataClass);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();

                if (dataList.isEmpty()) {
                    Toast.makeText(Meal_Planner_Activity.this, "No data found in Firebase.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(Meal_Planner_Activity.this, "Firebase Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Meal_Planner_Activity.this, UploadActivity.class);
                startActivity(intent);
            }
        });
    }

    private void searchList(String text) {
        ArrayList<DataClass> filteredList = new ArrayList<>();
        for (DataClass item : dataList) {
            if (item.getDataTitle() != null && item.getDataTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.searchDataList(filteredList);
    }
}