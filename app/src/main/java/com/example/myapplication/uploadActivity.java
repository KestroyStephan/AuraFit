package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class uploadActivity extends AppCompatActivity {

    ImageView uploadImage;
    Button saveButton;
    EditText uploadTopic,uploadDesc,uploadLang;
    String imageUrl;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        uploadImage = findViewById(R.id.uploadImage);
        uploadDesc = findViewById(R.id.uploadDes);
        uploadTopic = findViewById(R.id.uploadTopic);
        uploadLang = findViewById(R.id.uploadlang);
        uploadImage = findViewById(R.id.uploadImage);
        saveButton = findViewById(R.id.saveButton);

    }
}