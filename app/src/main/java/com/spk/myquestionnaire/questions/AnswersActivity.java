package com.spk.myquestionnaire.questions;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.spk.myquestionnaire.R;
import com.spk.myquestionnaire.answer.Answer;
import com.spk.myquestionnaire.answer.UserAnswer;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class AnswersActivity extends AppCompatActivity {
    Context context;
    LinearLayout resultLinearLayout;
    private UserAnswer userAnswer;
    private ProgressDialog progressDialog;
    String androidId;
    int surveyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);
       /* androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        surveyId = getIntent().getExtras().getInt("surveyId");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading .....");
        context = this;
        resultLinearLayout = findViewById(R.id.resultLinearLayout);
        toolBarInit();
        getResultData();
    }


    private void toolBarInit() {
        Toolbar answerToolBar = findViewById(R.id.answerToolbar);
        answerToolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        answerToolBar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getResultData() {
        progressDialog.show();
        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = dataBase.getReference("user_answers/" + androidId + "/" + surveyId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Gson gson = new Gson();
                userAnswer = gson.fromJson(gson.toJson(dataSnapshot.getValue()), UserAnswer.class);
                progressDialog.hide();
                prepareQuestionsAnswerView();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
                progressDialog.hide();
            }
        });
    }

    private void prepareQuestionsAnswerView() {
        List<Answer> answers = new ArrayList<Answer>(userAnswer.getAnswerMap().values());
        for (int i = 0; i < answers.size(); i++) {
            TextView questionTextView = new TextView(context);
            questionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            questionTextView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            questionTextView.setPadding(40, 30, 16, 30);
            questionTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            questionTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            questionTextView.setTypeface(null, Typeface.BOLD);
            questionTextView.setText(answers.get(i).getQuestionName());

            resultLinearLayout.addView(questionTextView);
            String formattedAnswer = "• " + answers.get(i).getAnswerName(); // alt + 7 --> •

            TextView answerTextView = new TextView(context);
            answerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            answerTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            answerTextView.setPadding(60, 30, 16, 30);
            answerTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            answerTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
            answerTextView.setText(formattedAnswer);

            View view = new View(context);
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));

            resultLinearLayout.addView(answerTextView);
            resultLinearLayout.addView(view);
        }
    }


}