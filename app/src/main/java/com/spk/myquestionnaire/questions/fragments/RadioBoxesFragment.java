package com.spk.myquestionnaire.questions.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spk.myquestionnaire.R;
import com.spk.myquestionnaire.questions.QuestionActivity;
import com.spk.myquestionnaire.questions.database.AppDatabase;
import com.spk.myquestionnaire.questions.qdb.QuestionWithChoicesEntity;
import com.spk.myquestionnaire.questions.questionmodels.AnswerOptions;
import com.spk.myquestionnaire.questions.questionmodels.QuestionsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * This fragment provide the RadioButton/Single Options.
 */
public class RadioBoxesFragment extends Fragment {
    private final ArrayList<RadioButton> radioButtonArrayList = new ArrayList<>();
    private boolean screenVisible = false;
    private QuestionsItem radioButtonTypeQuestion;
    private FragmentActivity mContext;
    private Button nextOrFinishButton;
    //private Button previousButton;
    private TextView questionRBTypeTextView;
    private RadioGroup radioGroupForChoices;
    private boolean atLeastOneChecked = false;
    private AppDatabase appDatabase;
    private String questionId = "";
    private int currentPagePosition = 0;
    private int clickedRadioButtonPosition = 0;
    private String qState = "0";
    private static Map<Integer, QuestionsItem> questionsItems = new HashMap<>();
    private int surveyId;
    private String surveyName;
    String androidId;
    List<QuestionWithChoicesEntity> questionsWithAllChoicesList = new ArrayList<>();

    public RadioBoxesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_radio_boxes, container, false);
        androidId = androidId == null ? Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID) : androidId;
        appDatabase = AppDatabase.getAppDatabase(getActivity());

        nextOrFinishButton = rootView.findViewById(R.id.nextOrFinishButton);
        //previousButton = rootView.findViewById(R.id.previousButton);
        questionRBTypeTextView = rootView.findViewById(R.id.questionRBTypeTextView);
        radioGroupForChoices = rootView.findViewById(R.id.radioGroupForChoices);

        nextOrFinishButton.setOnClickListener(v -> {
            if (currentPagePosition == ((QuestionActivity) mContext).getTotalQuestionsSize()) {
                /* Here, You go back from where you started OR If you want to go next Activity just change the Intent*/
                Intent returnIntent = new Intent();
                mContext.setResult(Activity.RESULT_OK, returnIntent);
                mContext.finish();
                getResultFromDatabase();
            } else {
                ((QuestionActivity) mContext).nextQuestion();
            }
        });

        return rootView;
    }


    /*This method get called only when the fragment get visible, and here states of Radio Button(s) retained*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            screenVisible = true;
            for (int i = 0; i < radioButtonArrayList.size(); i++) {
                RadioButton radioButton = radioButtonArrayList.get(i);
                String cbPosition = String.valueOf(i);
                String answerName = (String) radioButton.getText();
                String[] data = new String[]{questionId, cbPosition};

                Observable.just(data)
                        .map(this::getTheStateOfRadioBox)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(String s) {
                                qState = s;
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                if (qState.equals("1")) {
                                    radioButton.setChecked(true);
                                } else {
                                    radioButton.setChecked(false);
                                }
                            }
                        });

            }
        }
    }

    private String getTheStateOfRadioBox(String[] data) {
        return appDatabase.getQuestionChoicesDao().isChecked(data[0], data[1]);
    }

    private void saveActionsOfRadioBox() {
        for (int i = 0; i < radioButtonArrayList.size(); i++) {
            if (i == clickedRadioButtonPosition) {
                RadioButton radioButton = radioButtonArrayList.get(i);
                if (radioButton.isChecked()) {
                    atLeastOneChecked = true;

                    String cbPosition = String.valueOf(radioButtonArrayList.indexOf(radioButton));

                    String[] data = new String[]{"1", questionId, cbPosition};
                    insertChoiceInDatabase(data);


                } else {
                    String cbPosition = String.valueOf(radioButtonArrayList.indexOf(radioButton));

                    String[] data = new String[]{"0", questionId, cbPosition};
                    insertChoiceInDatabase(data);
                }
            }
        }

        if (atLeastOneChecked) {
            nextOrFinishButton.setEnabled(true);
        } else {
            nextOrFinishButton.setEnabled(false);
        }
    }

    private void insertChoiceInDatabase(String[] data) {
        Observable.just(data)
                .map(this::insertingInDb)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private String insertingInDb(String[] data) {
        appDatabase.getQuestionChoicesDao().updateQuestionWithChoice(data[0], data[1], data[2]);
        return "";
    }

    private void getResultFromDatabase() {
        Completable.fromAction(() -> {
            questionsWithAllChoicesList = appDatabase.getQuestionChoicesDao().getAllQuestionsWithChoices("1");
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        insertingInFireBase();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void insertingInFireBase() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key=database.getReference("user_answers").push().getKey();
        for (int i = 0; i < questionsWithAllChoicesList.size(); i++) {
            String[] data = new String[]{"1", questionsWithAllChoicesList.get(i).getQuestionId(), questionsWithAllChoicesList.get(i).getAnswerChoicePosition()};
            QuestionsItem questionsItem = questionsItems.get(Integer.parseInt(data[1]));
            AnswerOptions answerOption = questionsItem.getAnswerOptions().get(Integer.parseInt(data[2]));

            Map<String, Map<String, Object>> userAnswers = new HashMap<>();
            Map<String, Object> answers = new HashMap<>();
            answers.put( data[1] + "/question_name", questionsItem.getQuestionName());
            answers.put( data[1] + "/answer_name", answerOption.getName());
            answers.put( data[1] + "/question_id", questionsItem.getId());

            DatabaseReference answersRef = database.getReference("user_answers").child(key).child(String.valueOf(surveyId));
            answersRef.child("survey_name").setValue(surveyName);
            answersRef.updateChildren(answers);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        if (getArguments() != null) {
            surveyId = getArguments().getInt("surveyId");
            surveyName = getArguments().getString("surveyName");
            radioButtonTypeQuestion = getArguments().getParcelable("question");
            questionsItems.put(radioButtonTypeQuestion.getId(), radioButtonTypeQuestion);
            questionId = String.valueOf(radioButtonTypeQuestion != null ? radioButtonTypeQuestion.getId() : 0);
            currentPagePosition = getArguments().getInt("page_position") + 1;
        }

        questionRBTypeTextView.setText(radioButtonTypeQuestion.getQuestionName());

        List<AnswerOptions> choices = radioButtonTypeQuestion.getAnswerOptions();
        radioButtonArrayList.clear();

        for (AnswerOptions choice : choices) {
            RadioButton rb = new RadioButton(mContext);
            rb.setText(choice.getName());
            rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            rb.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
            rb.setPadding(10, 40, 10, 40);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 25;
            rb.setLayoutParams(params);

            View view = new View(mContext);
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.divider));
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));

            radioGroupForChoices.addView(rb);
            radioGroupForChoices.addView(view);
            radioButtonArrayList.add(rb);

            rb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (screenVisible) {
                    clickedRadioButtonPosition = radioButtonArrayList.indexOf(buttonView);
                    saveActionsOfRadioBox();
                }
            });
        }

        if (atLeastOneChecked) {
            nextOrFinishButton.setEnabled(true);
        } else {
            nextOrFinishButton.setEnabled(false);
        }

        /* If the current question is last in the myquestionnaire then
        the "Next" button will change into "Finish" button*/
        if (currentPagePosition == ((QuestionActivity) mContext).getTotalQuestionsSize()) {
            nextOrFinishButton.setText(R.string.finish);
        } else {
            nextOrFinishButton.setText(R.string.next);
        }
    }
}