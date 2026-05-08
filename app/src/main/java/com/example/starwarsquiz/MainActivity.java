package com.example.starwarsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, QuizActivity.class)));

        Button editButton = findViewById(R.id.editBtn);
        editButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EditorActivity.class)));
    }
}