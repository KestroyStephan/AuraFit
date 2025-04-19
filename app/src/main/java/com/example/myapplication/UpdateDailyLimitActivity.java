package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateDailyLimitActivity extends AppCompatActivity {

    private EditText limitEditText;
    private Button updateButton;

    private FirebaseAuth mAuth;
    private DatabaseReference stepsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_daily_limit);

        limitEditText = findViewById(R.id.limitEditText);
        updateButton = findViewById(R.id.updateLimitButton);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        stepsRef = FirebaseDatabase.getInstance().getReference("Steps").child(userId);

        updateButton.setOnClickListener(v -> {
            String newLimit = limitEditText.getText().toString();
            if (!newLimit.isEmpty()) {
                int limitValue = Integer.parseInt(newLimit);
                stepsRef.child("dailyLimit").setValue(limitValue);
                Toast.makeText(this, "Updated Daily Limit", Toast.LENGTH_SHORT).show();

                // 🔁 Go back to StepCounter and show updated value
                Intent intent = new Intent(UpdateDailyLimitActivity.this, StepCounter.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
