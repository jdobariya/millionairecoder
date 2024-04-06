package com.millionairecodergame.millionairecoder.model;

import java.util.List;

public class Question {

    private String qid = null;
    private String difficulty;
    private String question;
    private String correctAnswer;
    private List<String> incorrectAnswers;

    private String userAnswer = null;
    public Question() {
        // Default constructor
    }

    public Question(String difficulty, String question, String correctAnswer, List<String> incorrectAnswers) {
        this.difficulty = difficulty;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
        this.qid = java.util.UUID.randomUUID().toString();
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public String getUserAnswer() {
        return userAnswer;
    }
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getQid() {
        return qid;
    }
    public void setQid(String qid) {
        this.qid = qid;
    }
}

