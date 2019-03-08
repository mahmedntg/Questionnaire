package com.spk.questionnaire.questions;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import com.spk.questionnaire.R;
import com.spk.questionnaire.questions.adapters.ViewPagerAdapter;
import com.spk.questionnaire.questions.database.AppDatabase;
import com.spk.questionnaire.questions.fragments.RadioBoxesFragment;
import com.spk.questionnaire.questions.qdb.QuestionWithChoicesEntity;
import com.spk.questionnaire.questions.questionmodels.AnswerOptions;
import com.spk.questionnaire.questions.questionmodels.QuestionDataModel;
import com.spk.questionnaire.questions.questionmodels.QuestionsItem;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class QuestionActivity extends AppCompatActivity {
    final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    List<QuestionsItem> questionsItems = new ArrayList<>();
    private TextView questionPositionTV;
    private String totalQuestions = "1";
    private ViewPager questionsViewPager;
    private AppDatabase appDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        toolBarInit();
        appDatabase = AppDatabase.getAppDatabase(QuestionActivity.this);
        if (getIntent().getExtras() != null) {
            parsingData((QuestionDataModel) getIntent().getSerializableExtra("questions"));
        }
    }

    private void toolBarInit() {
        Toolbar questionToolbar = findViewById(R.id.questionToolbar);
        questionToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        questionToolbar.setNavigationOnClickListener(v -> onBackPressed());
        questionPositionTV = questionToolbar.findViewById(R.id.questionPositionTV);
    }

    /*This method decides how many Question-Screen(s) will be created and
    what kind of (Multiple/Single choices) each Screen will be.*/
    private void parsingData(QuestionDataModel questionDataModel) {
        int surveyId = questionDataModel.getSurvey().getId();
        String surveyName = questionDataModel.getSurvey().getName();
        questionsItems = questionDataModel.getSurvey().getQuestions();
        preparingInsertionInDb(questionsItems);
        totalQuestions = String.valueOf(questionsItems.size());
        String questionPosition = "1/" + totalQuestions;
        setTextWithSpan(questionPosition);

        for (int i = 0; i < questionsItems.size(); i++) {
            QuestionsItem question = questionsItems.get(i);
            RadioBoxesFragment radioBoxesFragment = new RadioBoxesFragment();
            Bundle radioButtonBundle = new Bundle();
            radioButtonBundle.putInt("surveyId", surveyId);
            radioButtonBundle.putString("surveyName", surveyName);
            radioButtonBundle.putParcelable("question", question);
            radioButtonBundle.putInt("page_position", i);
            radioBoxesFragment.setArguments(radioButtonBundle);
            fragmentArrayList.add(radioBoxesFragment);
        }

        questionsViewPager = findViewById(R.id.pager);
        questionsViewPager.setOffscreenPageLimit(1);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        questionsViewPager.setAdapter(mPagerAdapter);
    }

    public void nextQuestion() {
        int item = questionsViewPager.getCurrentItem() + 1;
        questionsViewPager.setCurrentItem(item);

        String currentQuestionPosition = String.valueOf(item + 1);

        String questionPosition = currentQuestionPosition + "/" + totalQuestions;
        setTextWithSpan(questionPosition);
    }

    public int getTotalQuestionsSize() {
        return questionsItems.size();
    }



    @Override
    public void onBackPressed() {
        if (questionsViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            int item = questionsViewPager.getCurrentItem() - 1;
            questionsViewPager.setCurrentItem(item);

            String currentQuestionPosition = String.valueOf(item + 1);

            String questionPosition = currentQuestionPosition + "/" + totalQuestions;
            setTextWithSpan(questionPosition);
        }
    }

    private void setTextWithSpan(String questionPosition) {
        int slashPosition = questionPosition.indexOf("/");

        Spannable spanText = new SpannableString(questionPosition);
        spanText.setSpan(new RelativeSizeSpan(0.7f), slashPosition, questionPosition.length(), 0);
        questionPositionTV.setText(spanText);
    }

    private void preparingInsertionInDb(List<QuestionsItem> questionsItems)
    {
        ArrayList<QuestionWithChoicesEntity> questionWithChoicesEntities = new ArrayList<>();

        for (int i = 0; i < questionsItems.size(); i++)
        {
            List<AnswerOptions> answerOptions = questionsItems.get(i).getAnswerOptions();

            for (int j = 0; j < answerOptions.size(); j++)
            {
                QuestionWithChoicesEntity questionWithChoicesEntity = new QuestionWithChoicesEntity();
                questionWithChoicesEntity.setQuestionId(String.valueOf(questionsItems.get(i).getId()));
                questionWithChoicesEntity.setAnswerChoice(answerOptions.get(j).getName());
                questionWithChoicesEntity.setAnswerChoicePosition(String.valueOf(j));
                questionWithChoicesEntity.setAnswerChoiceId(answerOptions.get(j).getAnswerId());
                questionWithChoicesEntity.setAnswerChoiceState("0");

                questionWithChoicesEntities.add(questionWithChoicesEntity);
            }
        }

        insertQuestionWithChoicesInDatabase(questionWithChoicesEntities);
    }

    private void insertQuestionWithChoicesInDatabase(List<QuestionWithChoicesEntity> questionWithChoicesEntities)
    {
        Observable.just(questionWithChoicesEntities)
                .map(this::insertingQuestionWithChoicesInDb)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    /*First, clear the table, if any previous data saved in it. Otherwise, we get repeated data.*/
    private String insertingQuestionWithChoicesInDb(List<QuestionWithChoicesEntity> questionWithChoicesEntities)
    {
        appDatabase.getQuestionChoicesDao().deleteAllChoicesOfQuestion();
        appDatabase.getQuestionChoicesDao().insertAllChoicesOfQuestion(questionWithChoicesEntities);
        return "";
    }
}