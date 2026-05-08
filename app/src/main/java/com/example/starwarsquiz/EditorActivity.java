package com.example.starwarsquiz;

import android.content.Intent;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.util.Log;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<Question> questions = new ArrayList<>();
    private File dataFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        listView = findViewById(R.id.listView);
        Button addButton = findViewById(R.id.addButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        dataFile = new File(getFilesDir(), "questions.txt");

        loadQuestions();
        refreshList();

        listView.setOnItemClickListener((parent, view, position, id) -> showEditDialog(position));
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmDialog(position);
            return true;
        });

        addButton.setOnClickListener(v -> showEditDialog(-1));
        deleteButton.setOnClickListener(v -> showSelectQuestionToDeleteDialog());

        Button backButton = findViewById(R.id.backToMainButton);
        backButton.setOnClickListener(v -> {
            saveAllQuestions(); // СОХРАНЯЕМ ПРИ ВЫХОДЕ
            Intent intent = new Intent(EditorActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    // АВТОСОХРАНЕНИЕ при любом уходе с экрана (назад, свайп, свернули приложение)
    @Override
    protected void onPause() {
        super.onPause();
        if (questions != null && !questions.isEmpty()) {
            saveAllQuestions();
            Log.d("EditorActivity", "Автосохранение в onPause, вопросов: " + questions.size());
        }
    }

    private void showSelectQuestionToDeleteDialog() {
        if (questions.isEmpty()) {
            Toast.makeText(this, "Нет вопросов для удаления", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] questionTitles = new String[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            questionTitles[i] = (i+1) + ". " + questions.get(i).getQuestionText();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите вопрос для удаления");
        builder.setItems(questionTitles, (dialog, which) -> showDeleteConfirmDialog(which));
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showDeleteConfirmDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить вопрос")
                .setMessage("Удалить вопрос:\n" + questions.get(position).getQuestionText() + "?")
                .setPositiveButton("Да", (dialog, which) -> {
                    questions.remove(position);
                    saveAllQuestions();
                    refreshList();
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void loadQuestions() {
        questions.clear();
        if (!dataFile.exists()) {
            createAllQuestions();
            saveAllQuestions();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String content = sb.toString();
            Log.d("EditorActivity", "Прочитан файл, длина: " + content.length());

            if (content.startsWith("\uFEFF")) {
                content = content.substring(1);
            }
            String[] blocks = content.split("---");
            Log.d("EditorActivity", "Найдено блоков: " + blocks.length);

            for (String block : blocks) {
                if (block.trim().isEmpty()) continue;
                Question q = parseBlock(block);
                if (q != null) {
                    questions.add(q);
                    Log.d("EditorActivity", "Загружен вопрос: " + q.getQuestionText());
                } else {
                    Log.w("EditorActivity", "Не удалось распарсить блок:\n" + block);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка чтения файла", Toast.LENGTH_SHORT).show();
        }

        if (questions.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Файл вопросов пуст или повреждён")
                    .setMessage("Загрузить стандартные вопросы?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        createAllQuestions();
                        saveAllQuestions();
                        refreshList();
                    })
                    .setNegativeButton("Нет", (dialog, which) -> {
                        saveAllQuestions();
                        refreshList();
                    })
                    .show();
        }
    }

    private Question parseBlock(String block) {
        String question = "";
        String[] opts = new String[4];
        int correct = -1;
        String[] lines = block.split("\n");
        for (String l : lines) {
            l = l.trim();
            if (l.isEmpty()) continue;
            if (l.startsWith("ВОПРОС:")) {
                question = l.substring(7).trim();
            } else if (l.startsWith("ОТВЕТ A:")) {
                opts[0] = l.substring(8).trim();
            } else if (l.startsWith("ОТВЕТ B:")) {
                opts[1] = l.substring(8).trim();
            } else if (l.startsWith("ОТВЕТ C:")) {
                opts[2] = l.substring(8).trim();
            } else if (l.startsWith("ОТВЕТ D:")) {
                opts[3] = l.substring(8).trim();
            } else if (l.startsWith("ПРАВИЛЬНЫЙ ОТВЕТ:")) {
                String c = l.substring(17).trim();
                if (c.equals("A")) correct = 0;
                else if (c.equals("B")) correct = 1;
                else if (c.equals("C")) correct = 2;
                else if (c.equals("D")) correct = 3;
            }
        }
        if (question.isEmpty() || opts[0] == null || opts[0].isEmpty() || correct == -1) {
            Log.e("parseBlock", "Ошибка парсинга: вопрос=" + question + ", correct=" + correct);
            return null;
        }
        return new Question(question, opts, correct);
    }

    private void createAllQuestions() {
        questions.clear();
        // 21 стандартный вопрос
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

        // 22-й вопрос про Звёздный Разрушитель
        questions.add(new Question("Как называется самый известный тип имперского корабля, который часто называют просто «Разрушитель»?",
                new String[]{"Звезда Смерти", "Звёздный Разрушитель", "Тысячелетний Сокол", "Исполнитель"}, 1));
    }

    private void saveAllQuestions() {
        Log.d("EditorActivity", "Начинаем сохранение " + questions.size() + " вопросов");
        try (FileWriter writer = new FileWriter(dataFile)) {
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
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
                writer.flush();
            }
            Log.d("EditorActivity", "Файл успешно записан");

            // Диагностика - проверяем, что файл реально создался
            if (dataFile.exists()) {
                long fileSize = dataFile.length();
                Toast.makeText(this, "✓ Сохранено! Всего вопросов: " + questions.size() + " (файл: " + fileSize + " байт)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "ПРЕДУПРЕЖДЕНИЕ: Файл не создался!", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("EditorActivity", "Ошибка сохранения: " + e.getMessage());
            Toast.makeText(this, "ОШИБКА сохранения: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void refreshList() {
        List<String> display = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            display.add((i+1) + ". " + questions.get(i).getQuestionText());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display);
        listView.setAdapter(adapter);
    }

    private void showEditDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(position == -1 ? "Новый вопрос" : "Редактировать");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText questionInput = new EditText(this);
        questionInput.setHint("Введите вопрос *");
        final EditText[] answerInputs = new EditText[4];
        for (int i = 0; i < 4; i++) {
            answerInputs[i] = new EditText(this);
            answerInputs[i].setHint("Ответ " + (char)('A'+i) + " *");
        }

        TextView hintCorrect = new TextView(this);
        hintCorrect.setText("★ Выберите правильный ответ (обязательно) ★");
        hintCorrect.setTextColor(Color.YELLOW);
        hintCorrect.setTextSize(12);
        hintCorrect.setPadding(0, 16, 0, 8);

        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.HORIZONTAL);
        RadioButton rbA = new RadioButton(this); rbA.setText("A");
        RadioButton rbB = new RadioButton(this); rbB.setText("B");
        RadioButton rbC = new RadioButton(this); rbC.setText("C");
        RadioButton rbD = new RadioButton(this); rbD.setText("D");
        radioGroup.addView(rbA); radioGroup.addView(rbB); radioGroup.addView(rbC); radioGroup.addView(rbD);

        if (position != -1) {
            Question q = questions.get(position);
            questionInput.setText(q.getQuestionText());
            String[] opts = q.getOptions();
            for (int i = 0; i < 4; i++) answerInputs[i].setText(opts[i]);
            switch (q.getCorrectAnswerIndex()) {
                case 0: rbA.setChecked(true); break;
                case 1: rbB.setChecked(true); break;
                case 2: rbC.setChecked(true); break;
                case 3: rbD.setChecked(true); break;
            }
        }

        layout.addView(questionInput);
        for (EditText et : answerInputs) layout.addView(et);
        layout.addView(hintCorrect);
        layout.addView(radioGroup);
        builder.setView(layout);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String qText = questionInput.getText().toString().trim();
            String[] opts = new String[4];
            for (int i = 0; i < 4; i++) opts[i] = answerInputs[i].getText().toString().trim();
            int correctIdx = -1;
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId == rbA.getId()) correctIdx = 0;
            else if (checkedId == rbB.getId()) correctIdx = 1;
            else if (checkedId == rbC.getId()) correctIdx = 2;
            else if (checkedId == rbD.getId()) correctIdx = 3;

            if (qText.isEmpty()) {
                Toast.makeText(this, "Введите текст вопроса", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean hasEmptyOption = false;
            for (int i = 0; i < 4; i++) {
                if (opts[i].isEmpty()) {
                    Toast.makeText(this, "Заполните ответ " + (char)('A'+i), Toast.LENGTH_SHORT).show();
                    hasEmptyOption = true;
                    break;
                }
            }
            if (hasEmptyOption) return;
            if (correctIdx == -1) {
                Toast.makeText(this, "Выберите правильный ответ (A, B, C или D)", Toast.LENGTH_LONG).show();
                return;
            }

            Question newQ = new Question(qText, opts, correctIdx);
            if (position == -1) questions.add(newQ);
            else questions.set(position, newQ);
            saveAllQuestions();
            refreshList();
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}