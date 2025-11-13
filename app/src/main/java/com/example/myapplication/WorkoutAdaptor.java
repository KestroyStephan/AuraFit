package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class WorkoutAdaptor extends RecyclerView.Adapter<WorkoutAdaptor.WorkoutViewHolder> {

    private Context context;
    private List<DataClass> workoutList;

    public WorkoutAdaptor(Context context, List<DataClass> workoutList) {
        this.context = context;
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_workout_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        DataClass currentWorkout = workoutList.get(position);

        holder.workoutTitle.setText(currentWorkout.getDataTitle());
        holder.workoutDesc.setText(currentWorkout.getDataDesc());
        holder.workoutType.setText(currentWorkout.getDataLang()); // You can treat `dataLang` as workout type

        Glide.with(context).load(currentWorkout.getDataImage()).into(holder.workoutImage);

        holder.workoutCard.setOnClickListener(v -> {
            Intent intent = new Intent(context, WorkoutDetailsActivity.class);
            intent.putExtra("Title", currentWorkout.getDataTitle());
            intent.putExtra("Description", currentWorkout.getDataDesc());
            intent.putExtra("Language", currentWorkout.getDataLang());
            intent.putExtra("Image", currentWorkout.getDataImage());
            intent.putExtra("Key", currentWorkout.getKey());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList) {
        workoutList = searchList;
        notifyDataSetChanged();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        ImageView workoutImage;
        TextView workoutTitle, workoutDesc, workoutType;
        CardView workoutCard;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutImage = itemView.findViewById(R.id.recImage);
            workoutCard = itemView.findViewById(R.id.recCard);
            workoutTitle = itemView.findViewById(R.id.recTitle);
            workoutDesc = itemView.findViewById(R.id.recDesc);
            workoutType = itemView.findViewById(R.id.recLang);
        }
    }
}
