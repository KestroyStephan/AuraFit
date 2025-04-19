package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutViewHolder> {

    private Context context;
    private ArrayList<WorkoutHistory> historyList;
    private DatabaseReference databaseReference;

    public WorkoutHistoryAdapter(Context context, ArrayList<WorkoutHistory> historyList, DatabaseReference databaseReference) {
        this.context = context;
        this.historyList = historyList;
        this.databaseReference = databaseReference;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout_history, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutHistory history = historyList.get(position);
        holder.dateTimeText.setText(history.getDateTime());
        holder.durationText.setText(history.getDuration());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView dateTimeText, durationText;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTimeText = itemView.findViewById(R.id.dateTimeText);
            durationText = itemView.findViewById(R.id.durationText);
        }
    }
}
