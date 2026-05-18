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
        Button editBtn = findViewById(R.id.editBtn);

        // Запуск викторины
        startButton.setOnClickListener(v ->
                startActivity(new Intent(this, QuizActivity.class)));

        // Запуск редактора
        editBtn.setOnClickListener(v ->
                startActivity(new Intent(this, EditorActivity.class)));
    }
}