package com.millionairecodergame.millionairecoder.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

public class Player {
    private boolean playedBefore = false;
    @BsonProperty(value = "playerName")
    private String playerName;
    @BsonProperty(value = "score")
    private int amountWon;
    @BsonProperty(value = "lifelines")
    private boolean[] lifelines; // boolean array to track lifeline usage

    private List<Question> questions; // Array of questions for the player
    private int currentQuestionIndex = -1; // Index of the current question

    public Player() {
    }
    public Player(String playerName, int amountWon, boolean[] lifelines, List<Question> questions) {
        this.playerName = playerName;
        this.amountWon = amountWon;
        this.lifelines = lifelines; // 3 lifelines: 50:50, Ask the Audience, Phone a Friend
        this.questions = questions;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getAmountWon() {
        return amountWon;
    }

    public void setAmountWon(int amountWon) {
        this.amountWon = amountWon;
    }

    public boolean[] getLifelines() {
        return lifelines;
    }

    public void setLifelines(boolean[] lifelines) {
        this.lifelines = lifelines;
    }

    public void useLifeline(int lifelineIndex) {
        lifelines[lifelineIndex] = true;
    }

    public List<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public boolean getPlayedBefore() {
        return playedBefore;
    }
    public void setPlayedBefore(boolean playedBefore) {
        this.playedBefore = playedBefore;
    }
}

