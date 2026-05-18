package com.example.starwarsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;

    private TextView questionText;
    private RadioGroup radioGroup;
    private RadioButton radio1, radio2, radio3, radio4;
    private Button nextButton, backButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionText = findViewById(R.id.questionText);
        radioGroup = findViewById(R.id.radioGroup);
        radio1 = findViewById(R.id.radio1);
        radio2 = findViewById(R.id.radio2);
        radio3 = findViewById(R.id.radio3);
        radio4 = findViewById(R.id.radio4);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);
        exitButton = findViewById(R.id.exitButton);

        questions = loadQuestionsFromFile();
        if (questions.isEmpty()) {
            Toast.makeText(this, "Нет вопросов!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        displayQuestion(currentQuestionIndex);

        // Кнопка "ОТВЕТИТЬ"
        nextButton.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Выберите ответ!", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadio = findViewById(selectedId);
            int answerIndex = radioGroup.indexOfChild(selectedRadio);
            if (answerIndex == questions.get(currentQuestionIndex).getCorrectAnswerIndex()) {
                correctAnswers++;
            }

            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion(currentQuestionIndex);
                radioGroup.clearCheck();
            } else {
                // Викторина завершена
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("correct", correctAnswers);
                intent.putExtra("total", questions.size());
                startActivity(intent);
                finish();
            }
        });

        // Кнопка "НАЗАД"
        backButton.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayQuestion(currentQuestionIndex);
                radioGroup.clearCheck();
            } else {
                Toast.makeText(this, "Это первый вопрос", Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка "ВЫЙТИ"
        exitButton.setOnClickListener(v -> {
            // Просто возвращаемся на главный экран
            startActivity(new Intent(QuizActivity.this, MainActivity.class));
            finish();
        });
    }

    private void displayQuestion(int index) {
        Question q = questions.get(index);
        questionText.setText(q.getQuestionText());
        String[] options = q.getOptions();
        radio1.setText(options[0]);
        radio2.setText(options[1]);
        radio3.setText(options[2]);
        radio4.setText(options[3]);
    }

    private List<Question> loadQuestionsFromFile() {
        List<Question> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(openFileInput("questions.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Question q = Question.fromFileString(line);
                if (q != null) list.add(q);
            }
        } catch (IOException e) {
            list = getDefaultQuestions();
            saveDefaultQuestions(list);
        }
        if (list.isEmpty()) {
            list = getDefaultQuestions();
        }
        return list;
    }

    // 21 вопрос (ваш список)
    private List<Question> getDefaultQuestions() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("В какой момент Оби‑Ван Кеноби начинает использовать имя «Бен» и скрываться на Татуине?",
                new String[]{"После битвы на Мустафаре", "После Приказа 66 и падения Республики", "Во время Войн клонов", "После дуэли с Дартом Молом"}, 1));
        questions.add(new Question("Какой световой меч использует Люк Скайуокер в конце фильма «Возвращение джедая»?",
                new String[]{"Меч Энакина", "Самодельный синий меч", "Меч Оби‑Вана", "Зелёный меч, созданный им самим"}, 3));
        questions.add(new Question("В Star Wars Jedi: Survivor (2023) какой новый стиль боя появился у Кэла Кестиса?",
                new String[]{"Два меча", "Стандартный меч", "Меч и бластер", "Двухклинковый меч"}, 2));
        questions.add(new Question("Чем уникален световой меч Мейса Винду?",
                new String[]{"Меняет цвет", "Из редкого металла", "Фиолетовый цвет", "Разделяется на два"}, 2));
        questions.add(new Question("Где Йода провёл годы изгнания после Приказа 66?",
                new String[]{"Корусант", "Дагоба", "Кашиик", "Мустафар"}, 1));
        questions.add(new Question("С кем Эзра Бриджер установил ментальную связь в «Повстанцах»?",
                new String[]{"C-3PO", "Корабль Призрак", "Пурргил", "Голокрон ситхов"}, 2));
        questions.add(new Question("Чьей внучкой является Рей?",
                new String[]{"Люка Скайуокера", "Хана Соло", "Палпатина", "Мейса Винду"}, 2));
        questions.add(new Question("В каком мультсериале впервые появился Гранд-адмирал Траун?",
                new String[]{"Войны клонов", "Повстанцы", "Бракованная партия", "Сказания о джедаях"}, 1));
        questions.add(new Question("Какую должность занимал Орсон Кренник?",
                new String[]{"Главнокомандующий", "Министр пропаганды", "Руководитель проекта Звезда Смерти", "Советник Палпатина"}, 2));
        questions.add(new Question("Каким оружием пользуется Дарт Мол в «Скрытой угрозе»?",
                new String[]{"Стандартный меч", "Двухклинковый меч", "Энергетический посох", "Бластер"}, 1));
        questions.add(new Question("Какой титул носил граф Дуку до перехода на Тёмную сторону?",
                new String[]{"Лорд ситхов", "Генерал Республики", "Мастер-джедай", "Сенатор"}, 2));
        questions.add(new Question("Что верно описывает генерала Гривуса?",
                new String[]{"Чистокровный клон", "Киборг с 4 руками", "Последний из тогрут", "Дроид-командир"}, 1));
        questions.add(new Question("Какой приказ сделал Энакина Дартом Вейдером?",
                new String[]{"Приказ 45", "Приказ 99", "Приказ 66", "Приказ 113"}, 2));
        questions.add(new Question("Чьим внуком является Кайло Рен (Бен Соло)?",
                new String[]{"Люка и Мары", "Хана и Леи", "Энакина и Падме", "Ландо и Миры"}, 1));
        questions.add(new Question("Какую планету представляла Лея Органа в Сенате?",
                new String[]{"Набу", "Альдераан", "Корусант", "Татуин"}, 1));
        questions.add(new Question("Какой корабль пилотирует Хан Соло?",
                new String[]{"Тысячелетний сокол", "Призрак", "Тантив IV", "Сокол свободы"}, 0));
        questions.add(new Question("Кем был Боба Фетт по отношению к Джанго Фетту?",
                new String[]{"Брат", "Сын", "Клон", "Ученик"}, 2));
        questions.add(new Question("В каком фильме Кассиан Андор крадёт чертежи Звезды Смерти?",
                new String[]{"Новая надежда", "Последние джедаи", "Изгой-один", "Хан Соло"}, 2));
        questions.add(new Question("К какому подразделению принадлежал капитан Рекс?",
                new String[]{"501-й легион", "212-й батальон", "99-й отряд", "332-я рота"}, 0));
        questions.add(new Question("Какой тип дроида R2-D2?",
                new String[]{"Протокольный", "Астромеханик", "Боевой", "Разведчик"}, 1));
        questions.add(new Question("Какую должность занимал Лэндо на Беспине?",
                new String[]{"Губернатор", "Барон-администратор", "Комендант", "Представитель"}, 1));
        return questions;
    }

    private void saveDefaultQuestions(List<Question> questions) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(openFileOutput("questions.txt", MODE_PRIVATE)))) {
            for (Question q : questions) {
                writer.write(q.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}