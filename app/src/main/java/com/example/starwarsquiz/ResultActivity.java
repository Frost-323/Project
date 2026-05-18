package com.example.starwarsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultText = findViewById(R.id.resultText);
        TextView messageText = findViewById(R.id.messageText);
        Button backButton = findViewById(R.id.backButton);

        int correct = getIntent().getIntExtra("correct", 0);
        int total = getIntent().getIntExtra("total", 0);

        resultText.setText(correct + " / " + total);

        // Выбираем сообщение в зависимости от результата
        String message = getMotivationalMessage(correct, total);
        messageText.setText(message);

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private String getMotivationalMessage(int correct, int total) {
        double percent = (double) correct / total;
        Random random = new Random();

        if (percent < 0.4) {                    // 0–8 баллов из 21
            String[] phrases = {
                    "Даже Оби-Ван не всегда побеждал. Тренируйся, юный падаван!",
                    "Энакин тоже ошибался. Не переходи на тёмную сторону, попробуй ещё раз."
            };
            return phrases[random.nextInt(phrases.length)];
        } else if (percent < 0.7) {            // 9–14 баллов
            String[] phrases = {
                    "Сила ещё растёт в тебе. Продолжай обучение, и станешь рыцарем.",
                    "Ты на верном пути, словно Люк на Дагобе. Не останавливайся!"
            };
            return phrases[random.nextInt(phrases.length)];
        } else {                               // 15–21 балл
            String[] phrases = {
                    "Великий джедай! Твоя мудрость достойна совета магистров.",
                    "Магистр Йода гордился бы тобой. Истинный хранитель мира!",
                    "Светлая сторона выбрала тебя. Ты — надежда галактики!"
            };
            return phrases[random.nextInt(phrases.length)];
        }
    }
}