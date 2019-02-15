package com.spk.questionnaire.answer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mohamedsayed on 2/7/2019.
 */

public class UserAnswer implements Serializable {
    @SerializedName("survey_name")
    private String surveyName;

    @SerializedName("answers")
    private Map<String,Answer> answerMap=new HashMap<>();

    public Map<String, Answer> getAnswerMap() {
        return answerMap;
    }

    public void setAnswerMap(Map<String, Answer> answerMap) {
        this.answerMap = answerMap;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }


}
