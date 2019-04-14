package com.spk.myquestionnaire;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.spk.myquestionnaire.questions.AnswersActivity;
import com.spk.myquestionnaire.questions.QuestionActivity;
import com.spk.myquestionnaire.questions.questionmodels.QuestionDataModel;
import com.spk.myquestionnaire.questions.questionmodels.Survey;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int QUESTIONNAIRE_REQUEST = 2018;
    private Button resultButton;
    private Button questionnaireButton;
    private QuestionDataModel questionDataModel = new QuestionDataModel();
    String jsonData;
    private ProgressDialog progressDialog;
    String androidId;
    private Object userSurveyAnswer;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        questionnaireButton = findViewById(R.id.questionnaireButton);
        resultButton = findViewById(R.id.resultButton);
        progressDialog = new ProgressDialog(this);
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Please check your internet connection and try again.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if (!AppStatus.getInstance(getApplicationContext()).isOnline()) {
            alertDialog.show();
            questionnaireButton.setVisibility(View.GONE);
            resultButton.setVisibility(View.GONE);
            return;
        }
     /*   androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/


        progressDialog.setMessage("Loading ......");
        progressDialog.setCanceledOnTouchOutside(false);
        parsingData();

    }

    void setUpToolbar() {
        Toolbar mainPageToolbar = findViewById(R.id.mainPageToolbar);
        setSupportActionBar(mainPageToolbar);
        getSupportActionBar().setTitle("Social Media Advertising");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QUESTIONNAIRE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //resultButton.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Questionnaire Completed!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        Intent questions;
        switch (b.getId()) {
            case R.id.questionnaireButton:
              /*  if (userSurveyAnswer != null) {
                    alertDialog.setMessage("You Already Voted !");
                    alertDialog.show();
                } else {*/
                resultButton.setVisibility(View.GONE);
                questions = new Intent(MainActivity.this, QuestionActivity.class);
                questions.putExtra("questions", questionDataModel);
                startActivityForResult(questions, QUESTIONNAIRE_REQUEST);
                //}
                break;
            case R.id.resultButton:
                questions = new Intent(MainActivity.this, AnswersActivity.class);
                questions.putExtra("surveyId", questionDataModel.getSurvey().getId());
                startActivity(questions);
                break;

        }
    }

    private void parsingData() {
        progressDialog.show();
        questionnaireButton.setOnClickListener(this);
        resultButton.setOnClickListener(this);
        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = dataBase.getReference("survey");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Gson gson = new Gson();
                //questionDataModel=gson.fromJson(gson.toJson(dataSnapshot.getValue()), QuestionDataModel.class);
                Survey survey = gson.fromJson(gson.toJson(dataSnapshot.getValue()), Survey.class);
                questionDataModel.setSurvey(survey);
                questionnaireButton.setText("Start " + survey.getName() + " Survey");
                progressDialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
                progressDialog.hide();
            }
        });

      /*  DatabaseReference databaseReference = dataBase.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Gson gson = new Gson();
                questionDataModel=gson.fromJson(gson.toJson(dataSnapshot.getValue()), QuestionDataModel.class);

                dataSnapshot = dataSnapshot.child("user_answers").getChildren().iterator().next();

             //   userSurveyAnswer = dataSnapshot.child("user_answers").getChildren().iterator().
                userSurveyAnswer = dataSnapshot.child(String.valueOf(questionDataModel.getSurvey().getId())).getValue();
                if (userSurveyAnswer == null) {
                    resultButton.setVisibility(View.GONE);
                }
                questionnaireButton.setText("Start " + questionDataModel.getSurvey().getName() + " Survey");
                progressDialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
                progressDialog.hide();
            }
        });
*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.privacyPolicyItemMenu:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/stposmita/privacy-policy-of-stposmita")));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}