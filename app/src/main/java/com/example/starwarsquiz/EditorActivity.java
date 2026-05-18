package com.example.starwarsquiz;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends AppCompatActivity {

    private ListView listView;
    private Button addButton, deleteButton, backButton;
    private ArrayAdapter<String> adapter;
    private List<Question> questionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        listView = findViewById(R.id.listView);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backToMainButton);

        questionsList = loadQuestionsFromFile();

        List<String> texts = new ArrayList<>();
        for (Question q : questionsList) {
            texts.add(q.getQuestionText());
        }
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, texts);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        addButton.setOnClickListener(v -> showEditDialog(null, -1));

        deleteButton.setOnClickListener(v -> {
            int pos = listView.getCheckedItemPosition();
            if (pos != ListView.INVALID_POSITION) {
                questionsList.remove(pos);
                adapter.remove(adapter.getItem(pos));
                adapter.notifyDataSetChanged();
                listView.clearChoices();
                saveQuestionsToFile();
                Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Выберите вопрос для удаления", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showEditDialog(questionsList.get(position), position);
            return true;
        });

        backButton.setOnClickListener(v -> {
            saveQuestionsToFile();
            finish();
        });
    }

    private void showEditDialog(Question existing, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existing == null ? "Новый вопрос" : "Редактирование");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_question, null);
        EditText qEdit = dialogView.findViewById(R.id.questionEditText);
        EditText opt1 = dialogView.findViewById(R.id.option1EditText);
        EditText opt2 = dialogView.findViewById(R.id.option2EditText);
        EditText opt3 = dialogView.findViewById(R.id.option3EditText);
        EditText opt4 = dialogView.findViewById(R.id.option4EditText);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

        if (existing != null) {
            qEdit.setText(existing.getQuestionText());
            String[] opts = existing.getOptions();
            opt1.setText(opts[0]);
            opt2.setText(opts[1]);
            opt3.setText(opts[2]);
            opt4.setText(opts[3]);
            ((RadioButton) radioGroup.getChildAt(existing.getCorrectAnswerIndex())).setChecked(true);
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Сохранить", null);
        builder.setNegativeButton("Отмена", null);
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String qText = qEdit.getText().toString().trim();
            String o1 = opt1.getText().toString().trim();
            String o2 = opt2.getText().toString().trim();
            String o3 = opt3.getText().toString().trim();
            String o4 = opt4.getText().toString().trim();
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (qText.isEmpty() || o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty() || selectedId == -1) {
                Toast.makeText(this, "Заполните все поля и выберите правильный ответ", Toast.LENGTH_LONG).show();
                return;
            }

            RadioButton checkedRadio = radioGroup.findViewById(selectedId);
            int correctIndex = radioGroup.indexOfChild(checkedRadio);

            String[] options = {o1, o2, o3, o4};

            if (existing == null) {
                questionsList.add(new Question(qText, options, correctIndex));
                adapter.add(qText);
            } else {
                existing.setQuestionText(qText);
                existing.setOptions(options);
                existing.setCorrectAnswerIndex(correctIndex);
                adapter.remove(adapter.getItem(position));
                adapter.insert(qText, position);
            }
            adapter.notifyDataSetChanged();
            saveQuestionsToFile();
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void saveQuestionsToFile() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(openFileOutput("questions.txt", MODE_PRIVATE)))) {
            for (Question q : questionsList) {
                writer.write(q.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Question> loadQuestionsFromFile() {
        List<Question> list = new ArrayList<>();
        File file = new File(getFilesDir(), "questions.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(openFileInput("questions.txt")))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Question q = Question.fromFileString(line);
                    if (q != null) list.add(q);
                }
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
            }
        }
        if (list.isEmpty()) {
            list = getDefaultQuestions();
            saveQuestionsToFile();
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

    @Override
    protected void onPause() {
        super.onPause();
        saveQuestionsToFile();
    }
}