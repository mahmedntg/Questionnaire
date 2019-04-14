package com.spk.myquestionnaire.answer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohamedsayed on 2/7/2019.
 */

public class UserAnswer implements Serializable {
    @SerializedName("survey_name")
    private String surveyName;

    /*@SerializedName("answers")
    private List<Answer> answers = new ArrayList<>();
    */
    @SerializedName("answers")
    private Map<String,Answer> answerMap=new HashMap<>();

    public Map<String, Answer> getAnswerMap() {
        return answerMap;
    }

    public void setAnswerMap(Map<String, Answer> answerMap) {
        this.answerMap = answerMap;
    }

    /*public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }*/

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }


}
