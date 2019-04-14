package com.spk.myquestionnaire.questions.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.spk.myquestionnaire.questions.qdb.QuestionChoicesDao;
import com.spk.myquestionnaire.questions.qdb.QuestionWithChoicesEntity;

@Database(entities = {QuestionWithChoicesEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "question_db";

    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)

                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract QuestionChoicesDao getQuestionChoicesDao();
}