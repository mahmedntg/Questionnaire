package com.spk.myquestionnaire.questions.questionmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Survey implements Serializable {
    @SerializedName("questions")
    private List<QuestionsItem> questions;
    private int id;
    private String name;

    public List<QuestionsItem> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionsItem> questions) {
        this.questions = questions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}