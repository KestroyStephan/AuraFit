package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class StepHistoryAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> values;

    public StepHistoryAdapter(Context context, List<String> values) {
        super(context, R.layout.step_history_item, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.step_history_item, parent, false);

        TextView stepDateText = rowView.findViewById(R.id.stepDateText);
        stepDateText.setText(values.get(position));

        return rowView;
    }
}
