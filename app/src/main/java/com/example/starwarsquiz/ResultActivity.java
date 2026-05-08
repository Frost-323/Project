package com.example.starwarsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView resultMessage, resultQuote, scoreDisplay;
    private Button restartButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultMessage = findViewById(R.id.resultMessage);
        resultQuote = findViewById(R.id.resultQuote);
        scoreDisplay = findViewById(R.id.scoreDisplay);
        restartButton = findViewById(R.id.restartButton);
        exitButton = findViewById(R.id.exitButton);

        int totalScore = getIntent().getIntExtra("totalScore", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 21);
        int percentage = (totalScore * 100) / totalQuestions;

        scoreDisplay.setText(totalScore + " / " + totalQuestions);

        if (percentage >= 70) {
            resultMessage.setText("★ ПОЗДРАВЛЯЮ! ★");
            resultQuote.setText("«Да пребудет с тобой Сила»\n- Оби-Ван Кеноби");
            scoreDisplay.setTextColor(getColor(android.R.color.holo_green_dark));
        } else if (percentage >= 40) {
            resultMessage.setText("НЕПЛОХО, НО МОЖНО ЛУЧШЕ");
            resultQuote.setText("«Тренироваться надо, юный падаван»\n- Йода");
            scoreDisplay.setTextColor(getColor(android.R.color.holo_orange_dark));
        } else {
            resultMessage.setText("ПОРАЖЕНИЕ НА ТЁМНОЙ СТОРОНЕ");
            resultQuote.setText("«Ты был избранником! Ты должен был уничтожить ситхов, а не примкнуть к ним!»\n- Оби-Ван Кеноби");
            scoreDisplay.setTextColor(getColor(android.R.color.holo_red_dark));
        }

        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, QuizActivity.class);
            startActivity(intent);
            finish();
        });

        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}