package com.example.starwarsquiz;

import java.io.Serializable;

public class Question implements Serializable {
    private String questionText;
    private String[] options;
    private int correctAnswerIndex;

    public Question(String questionText, String[] options, int correctAnswerIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() { return questionText; }
    public String[] getOptions() { return options; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }

    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setOptions(String[] options) { this.options = options; }
    public void setCorrectAnswerIndex(int correctAnswerIndex) { this.correctAnswerIndex = correctAnswerIndex; }

    // Сохранение: вопрос|отв1|отв2|отв3|отв4|индекс
    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(questionText);
        for (String opt : options) {
            sb.append("|").append(opt);
        }
        sb.append("|").append(correctAnswerIndex);
        return sb.toString();
    }

    public static Question fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 6) return null;
        String qText = parts[0];
        String[] opts = new String[]{parts[1], parts[2], parts[3], parts[4]};
        int idx = Integer.parseInt(parts[5]);
        if (idx < 0 || idx > 3) return null;
        return new Question(qText, opts, idx);
    }

    @Override
    public String toString() {
        return questionText;
    }
}