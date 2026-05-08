package com.example.starwarsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView questionCounter, questionText, scoreText;
    private RadioGroup radioGroup;
    private RadioButton[] optionButtons = new RadioButton[4];
    private Button prevButton, nextButton;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int[] userAnswers;
    private int score = 0;
    private File dataFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initializeViews();
        dataFile = new File(getFilesDir(), "questions.txt");

        loadQuestionsFromFile();

        // НОВАЯ ПРОВЕРКА
        if (questions == null || questions.size() != 21) {
            loadDefaultQuestions();
            saveQuestionsToFile();
        }

        userAnswers = new int[questions.size()];
        for (int i = 0; i < userAnswers.length; i++) userAnswers[i] = -1;

        displayQuestion(currentQuestionIndex);
        setupListeners();
    }

    private void initializeViews() {
        questionCounter = findViewById(R.id.questionCounter);
        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        radioGroup = findViewById(R.id.radioGroup);
        optionButtons[0] = findViewById(R.id.option0);
        optionButtons[1] = findViewById(R.id.option1);
        optionButtons[2] = findViewById(R.id.option2);
        optionButtons[3] = findViewById(R.id.option3);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
    }

    private void loadQuestionsFromFile() {
        questions = new ArrayList<>();
        if (!dataFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            String[] blocks = sb.toString().split("---");
            for (String block : blocks) {
                if (block.trim().isEmpty()) continue;
                Question q = parseBlock(block);
                if (q != null) questions.add(q);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Question parseBlock(String block) {
        String question = "";
        String[] opts = new String[4];
        int correct = -1;
        String[] lines = block.split("\n");
        for (String l : lines) {
            if (l.startsWith("ВОПРОС:")) question = l.substring(7).trim();
            else if (l.startsWith("ОТВЕТ A:")) opts[0] = l.substring(8).trim();
            else if (l.startsWith("ОТВЕТ B:")) opts[1] = l.substring(8).trim();
            else if (l.startsWith("ОТВЕТ C:")) opts[2] = l.substring(8).trim();
            else if (l.startsWith("ОТВЕТ D:")) opts[3] = l.substring(8).trim();
            else if (l.startsWith("ПРАВИЛЬНЫЙ ОТВЕТ:")) {
                String c = l.substring(17).trim();
                if (c.equals("A")) correct = 0;
                else if (c.equals("B")) correct = 1;
                else if (c.equals("C")) correct = 2;
                else if (c.equals("D")) correct = 3;
            }
        }
        if (question.isEmpty() || opts[0] == null || correct == -1) return null;
        return new Question(question, opts, correct);
    }

    private void loadDefaultQuestions() {
        questions = new ArrayList<>();
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
    }

    private void saveQuestionsToFile() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            for (Question q : questions) {
                writer.write("ВОПРОС: " + q.getQuestionText() + "\n");
                writer.write("ОТВЕТ A: " + q.getOptions()[0] + "\n");
                writer.write("ОТВЕТ B: " + q.getOptions()[1] + "\n");
                writer.write("ОТВЕТ C: " + q.getOptions()[2] + "\n");
                writer.write("ОТВЕТ D: " + q.getOptions()[3] + "\n");
                String correctLetter = "";
                switch (q.getCorrectAnswerIndex()) {
                    case 0: correctLetter = "A"; break;
                    case 1: correctLetter = "B"; break;
                    case 2: correctLetter = "C"; break;
                    case 3: correctLetter = "D"; break;
                }
                writer.write("ПРАВИЛЬНЫЙ ОТВЕТ: " + correctLetter + "\n");
                writer.write("---\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion(int index) {
        Question q = questions.get(index);
        questionText.setText(q.getQuestionText());
        questionCounter.setText("Вопрос " + (index + 1) + " из " + questions.size());

        String[] options = q.getOptions();
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setText(options[i]);
        }

        radioGroup.clearCheck();
        if (userAnswers[index] != -1) {
            optionButtons[userAnswers[index]].setChecked(true);
        }

        prevButton.setEnabled(index > 0);

        if (index == questions.size() - 1) {
            nextButton.setText("ЗАВЕРШИТЬ");
        } else {
            nextButton.setText("ДАЛЕЕ ▶");
        }
    }

    private void setupListeners() {
        prevButton.setOnClickListener(v -> {
            saveCurrentAnswer();
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
        });

        nextButton.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentQuestionIndex == questions.size() - 1) {
                calculateFinalScore();
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("totalScore", score);
                intent.putExtra("totalQuestions", questions.size());
                startActivity(intent);
                finish();
            } else {
                currentQuestionIndex++;
                displayQuestion(currentQuestionIndex);
            }
        });
    }

    private void saveCurrentAnswer() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            for (int i = 0; i < optionButtons.length; i++) {
                if (optionButtons[i].getId() == selectedId) {
                    userAnswers[currentQuestionIndex] = i;
                    break;
                }
            }
        }
    }

    private void calculateFinalScore() {
        score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers[i] != -1 && questions.get(i).isCorrect(userAnswers[i])) {
                score++;
            }
        }
    }
}