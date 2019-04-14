package com.spk.myquestionnaire.answer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mohamedsayed on 2/7/2019.
 */

public class Answer implements Serializable {

    @SerializedName("question_name")
    private String questionName;

    @SerializedName("answer_name")
    private String answerName;

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getAnswerName() {
        return answerName;
    }

    public void setAnswerName(String answerName) {
        this.answerName = answerName;
    }
}
