package com.spk.myquestionnaire.questions.questionmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionDataModel implements Serializable
{
    @SerializedName("survey")
    private Survey survey;


    public Survey getSurvey()
    {
        return survey;
    }

    public void setSurvey(Survey survey)
    {
        this.survey = survey;
    }
}