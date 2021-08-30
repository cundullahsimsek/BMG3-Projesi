package com.app.bilgiyarismasi.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.app.bilgiyarismasi.entity.MyAnswer;
import com.app.bilgiyarismasi.entity.Questions;
import com.app.bilgiyarismasi.entity.Scors;


public class Database extends SQLiteOpenHelper {

    private static final String VERITABANI_NAME = "quizDb";
    private static final int VERITABANI_VERSION = 15;
    //Tables
    private static final String TABLE_NAME_QUESTIONS = "questions";
    private static final String TABLE_NAME_MYANSWERS = "myAnswers";
    private static final String TABLE_NAME_SCORS = "scors";
    private static final String TABLE_NAME_REPEATING = "repeatingQuestions";


    private static final String QUESTION_ID = "question_id";
    private static final String QUESTION = "question";
    private static final String ANSWER_A = "answer_a";
    private static final String ANSWER_B = "answer_b";
    private static final String ANSWER_C = "answer_c";
    private static final String ANSWER_D = "answer_d";
    private static final String CORRECT_ANSWER = "correct_answer";
    private static final String QUESTION_LEVEL = "level";

    private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE " + TABLE_NAME_QUESTIONS +
            " (" + QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            QUESTION + " TEXT, " +
            ANSWER_A + " TEXT, " +
            ANSWER_B + " TEXT, " +
            ANSWER_C + " TEXT, " +
            ANSWER_D + " TEXT, " +
            CORRECT_ANSWER + " TEXT, " +
            QUESTION_LEVEL + " TEXT);";


    private static final String ANSWER_ID = "answer_id";
    private static final String MY_ANSWER = "myAnswer";
    private static final String MY_ANSWER_POSITION = "myposition";

    private static final String CREATE_TABLE_MYANSWER = "CREATE TABLE " + TABLE_NAME_MYANSWERS +
            " (" + ANSWER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MY_ANSWER + " TEXT, " +
            MY_ANSWER_POSITION + " INTEGER);";


    private static final String SCOR_ID = "scor_id";
    private static final String SCOR_NAME = "name";
    private static final String SCOR_SCORS = "scors";
    private static final String SCOR_LEVELS_HEADER = "level_header";
    private static final String SCOR_LEVEL_SECTION = "level_section";

    private static final String CREATE_TABLE_SCORS = "CREATE TABLE " + TABLE_NAME_SCORS +
            " (" + SCOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SCOR_NAME + " TEXT, " +
            SCOR_SCORS + " INTEGER, " +
            SCOR_LEVELS_HEADER + " TEXT, " +
            SCOR_LEVEL_SECTION + " INTEGER);";

    //REPEATING QUESTİON= TEKRARSIZ SORU
    private static final String REPEATING_ID = "repeating_id";
    private static final String REPEATING_QUESTION_ID_NO = "question_id_no";

    private static final String CREATE_TABLE_REPEATING = "CREATE TABLE " + TABLE_NAME_REPEATING +
            " (" + REPEATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            REPEATING_QUESTION_ID_NO + " INTEGER);";


    public Database(Context context) {
        super(context, VERITABANI_NAME, null, VERITABANI_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_QUESTIONS);
        db.execSQL(CREATE_TABLE_MYANSWER);
        db.execSQL(CREATE_TABLE_SCORS);
        db.execSQL(CREATE_TABLE_REPEATING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MYANSWERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SCORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REPEATING);
        onCreate(db);
    }

    //---------------------------------------  Questions   -------------------------------------------

    public List<Questions> _getAllQuestions(String level) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "Select * from " + TABLE_NAME_QUESTIONS + " Where " + QUESTION_LEVEL + "='" + level + "'";
        Cursor cursor = database.rawQuery(query, null);

        List<Questions> questionList = new ArrayList<Questions>();
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex(QUESTION_ID));
            String _question = cursor.getString(cursor.getColumnIndex(QUESTION)).trim();
            String _a = cursor.getString(cursor.getColumnIndex(ANSWER_A)).trim();
            String _b = cursor.getString(cursor.getColumnIndex(ANSWER_B)).trim();
            String _c = cursor.getString(cursor.getColumnIndex(ANSWER_C)).trim();
            String _d = cursor.getString(cursor.getColumnIndex(ANSWER_D)).trim();
            String _correct_answer = cursor.getString(cursor.getColumnIndex(CORRECT_ANSWER)).trim();
            String _level = cursor.getString(cursor.getColumnIndex(QUESTION_LEVEL)).trim();

            questionList.add(new Questions(_id, _question, _a, _b, _c, _d, _correct_answer, _level));

        }
        database.close();
        return questionList;

    }


    public int getQuestionCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME_QUESTIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public void deleteQuestions() { //DeleteQuestions >>> deleteQuestions
        //Tabloyu temnizlerken aynı anda id sifırlanıyor.
        String selectQuery = "delete from sqlite_sequence where name='" + TABLE_NAME_QUESTIONS + "'";
        SQLiteDatabase db_sil = this.getWritableDatabase();
        db_sil.delete(TABLE_NAME_QUESTIONS, null, null);
        db_sil.execSQL(selectQuery);

        //SQLiteDatabase db_sil = getWritableDatabase();
        //db_sil.delete(TABLO_ISMI,null,null);
        //ikinci paramatrenin null olması tablodaki bütün verileri seçer. Yani ikinci parametreye null yazarsak bütün veriler silinir.
        db_sil.close();
    }

    public long saveQuestions(Questions[] questions) {// SaveQuestions >>> saveQuestions
        int capacity = 0;
        long error = 0;
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values;
        while (capacity < questions.length) {
            values = new ContentValues();
            values.put(QUESTION, questions[capacity].getQuestion());
            values.put(ANSWER_A, questions[capacity].getAnswer_a());
            values.put(ANSWER_B, questions[capacity].getAnswer_b());
            values.put(ANSWER_C, questions[capacity].getAnswer_c());
            values.put(ANSWER_D, questions[capacity].getAnswer_d());
            values.put(CORRECT_ANSWER, questions[capacity].getCorrect_answer());
            values.put(QUESTION_LEVEL, questions[capacity].getLevel());
            error = database.insert(TABLE_NAME_QUESTIONS, null, values);
            capacity++;
        }
        database.close();
        return error;
    }

    public List<Questions> getAllQuestions(String level, List<Integer> repetitiveList) {
        SQLiteDatabase database = this.getReadableDatabase();
       /* String[] columns = new String[]{QUESTION_ID, QUESTION, ANSWER_A, ANSWER_B, ANSWER_C, ANSWER_D, CORRECT_ANSWER, LEVEL};
        //Performans için Cursor kullanılır.
        Cursor cursor = database.query(TABLE_NAME_QUESTIONS, columns, LEVEL + "=?",
                new String[]{String.valueOf(level).trim()}, null, null, null, null);*/
        String query = "Select * from " + TABLE_NAME_QUESTIONS + " Where " + QUESTION_LEVEL + "='" + level + "'";
        Cursor cursor = database.rawQuery(query, null);

        List<Questions> questionList = new ArrayList<Questions>();
        while (cursor.moveToNext()) {
            boolean isState = repetitiveList.contains(cursor.getInt(cursor.getColumnIndex(QUESTION_ID)));
            if (!isState) {
                int _id = cursor.getInt(cursor.getColumnIndex(QUESTION_ID));
                String _question = cursor.getString(cursor.getColumnIndex(QUESTION)).trim();
                String _a = cursor.getString(cursor.getColumnIndex(ANSWER_A)).trim();
                String _b = cursor.getString(cursor.getColumnIndex(ANSWER_B)).trim();
                String _c = cursor.getString(cursor.getColumnIndex(ANSWER_C)).trim();
                String _d = cursor.getString(cursor.getColumnIndex(ANSWER_D)).trim();
                String _correct_answer = cursor.getString(cursor.getColumnIndex(CORRECT_ANSWER)).trim();
                String _level = cursor.getString(cursor.getColumnIndex(QUESTION_LEVEL)).trim();

                questionList.add(new Questions(_id, _question, _a, _b, _c, _d, _correct_answer, _level));
            }
        }
        database.close();
        return questionList;

    }
    /*public List<Questions> getAllQuestions(String level, List<Integer> repetitiveList) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "Select * from " + TABLE_NAME_QUESTIONS + " Where " + QUESTION_LEVEL + "='" + level + "'";
        Cursor cursor = database.rawQuery(query, null);

        List<Questions> questionList = new ArrayList<Questions>();
        while (cursor.moveToNext()) {
            Questions questions = new Questions();
            boolean isState = repetitiveList.contains(cursor.getInt(cursor.getColumnIndex(QUESTION_ID)));
            if (!isState) {
                questions.setQ_id(cursor.getInt(cursor.getColumnIndex(QUESTION_ID)));
                questions.setQuestion(cursor.getString(cursor.getColumnIndex(QUESTION)).trim());
                questions.setAnswer_a(cursor.getString(cursor.getColumnIndex(ANSWER_A)).trim());
                questions.setAnswer_b(cursor.getString(cursor.getColumnIndex(ANSWER_B)).trim());
                questions.setAnswer_c(cursor.getString(cursor.getColumnIndex(ANSWER_C)).trim());
                questions.setAnswer_d(cursor.getString(cursor.getColumnIndex(ANSWER_D)).trim());
                questions.setCorrect_answer(cursor.getString(cursor.getColumnIndex(CORRECT_ANSWER)).trim());
                questions.setLevel(cursor.getString(cursor.getColumnIndex(QUESTION_LEVEL)));
                questionList.add(questions);
            }
        }
        database.close();
        return questionList;

    }*/


    //---------------------------------------  Repeating   -------------------------------------------

    public void repetitiveQuestionSave(int position) {//questionPozitionSave >>> repetitiveQuestionSave

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REPEATING_QUESTION_ID_NO, position);

        db.insert(TABLE_NAME_REPEATING, null, contentValues);
        db.close();
    }

    public List<Integer> getRepetitiveAllQuestion() {//gösterilmiş soruların idleri bu tabloda.
        List<Integer> myList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME_REPEATING;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int position_sequence_no = cursor.getColumnIndex(REPEATING_QUESTION_ID_NO);
        if (cursor.moveToFirst()) {
            do {
                int question_position = cursor.getInt(position_sequence_no);
                myList.add(question_position);
            } while (cursor.moveToNext());
        }

        return myList;
    }

    public void deleteRepeatingQuestions() {
        //Tabloyu temnizlerken aynı anda id sifırlanıyor.
        String selectQuery = "delete from sqlite_sequence where name='" + TABLE_NAME_REPEATING + "'";
        SQLiteDatabase db_sil = this.getWritableDatabase();
        db_sil.delete(TABLE_NAME_REPEATING, null, null);
        db_sil.execSQL(selectQuery);

        //SQLiteDatabase db_sil = getWritableDatabase();
        //db_sil.delete(TABLO_ISMI,null,null);
        //ikinci paramatrenin null olması tablodaki bütün verileri seçer. Yani ikinci parametreye null yazarsak bütün veriler silinir.
        db_sil.close();
    }

    //---------------------------------------  MyAnswers   -------------------------------------------

    public void deleteMyAnswers() {
        String selectQuery = "delete from sqlite_sequence where name='" + TABLE_NAME_MYANSWERS + "'";
        SQLiteDatabase db_sil = this.getWritableDatabase();
        db_sil.delete(TABLE_NAME_MYANSWERS, null, null);
        db_sil.execSQL(selectQuery);
        db_sil.close();
    }

    public long myAnswerSave(List<MyAnswer> list) {
        int capacity = 0;
        long error = 0;
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values;
        while (capacity < list.size()) {
            values = new ContentValues();
            values.put(MY_ANSWER, list.get(capacity).getMyAnswer());
            values.put(MY_ANSWER_POSITION, list.get(capacity).getMyAnswerPosition());
            error = database.insert(TABLE_NAME_MYANSWERS, null, values);
            capacity++;
        }
        database.close();
        return error;
    }

    public List<MyAnswer> getAllMyAnswers() {
        List<MyAnswer> my_list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME_MYANSWERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int answer_no = cursor.getColumnIndex(MY_ANSWER);
        int position_no = cursor.getColumnIndex(MY_ANSWER_POSITION);
        if (cursor.moveToFirst()) {
            do {
                String answers = cursor.getString(answer_no);
                int answer_position = cursor.getInt(position_no);
                my_list.add(new MyAnswer(answer_position, answers));
            } while (cursor.moveToNext());
        }
        return my_list;
    }


    //---------------------------------------  Scors   -------------------------------------------

    public List<Scors> getAllScors() {//getScors() >>>getAllScors
        List<Scors> scorsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_SCORS, new String[]{SCOR_ID, SCOR_NAME, SCOR_SCORS, SCOR_LEVELS_HEADER, SCOR_LEVEL_SECTION}, null, null, null, null, null, null);

        int id_sequence_no = cursor.getColumnIndex(SCOR_ID);
        int name_sequence_no = cursor.getColumnIndex(SCOR_NAME);
        int scor_sequence_no = cursor.getColumnIndex(SCOR_SCORS);
        int header_sequence_no = cursor.getColumnIndex(SCOR_LEVELS_HEADER);
        int section_sequence_no = cursor.getColumnIndex(SCOR_LEVEL_SECTION);
        if (cursor.moveToFirst()) {
            do {
                int _id = cursor.getInt(id_sequence_no);
                String _username = cursor.getString(name_sequence_no);
                int _scor = cursor.getInt(scor_sequence_no);
                String _level = cursor.getString(header_sequence_no);
                int _section = cursor.getInt(section_sequence_no);

                scorsList.add(new Scors(_id, _username, _scor, _level, _section));
            } while (cursor.moveToNext());
        }
        return scorsList;
    }

    public void updateUserName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCOR_NAME, name);
        db.update(TABLE_NAME_SCORS, values, null, null);
    }

    public void saveScors(String name) {//saveUser >>> saveScors
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SCOR_NAME, name);
        contentValues.put(SCOR_SCORS, 0);
        contentValues.put(SCOR_LEVELS_HEADER, "Kolay");
        contentValues.put(SCOR_LEVEL_SECTION, 0);

        db.insert(TABLE_NAME_SCORS, null, contentValues);
        db.close();
    }

    public int getScorCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME_SCORS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }


    public String getScorLevelHeader() {
        String levelHeader = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_SCORS, new String[]{SCOR_ID, SCOR_NAME, SCOR_SCORS, SCOR_LEVELS_HEADER, SCOR_LEVEL_SECTION}, null, null, null, null, null, null);
        int scor_header_sequence_no = cursor.getColumnIndex(SCOR_LEVELS_HEADER);
        if (cursor.moveToFirst()) {
            do {
                levelHeader = cursor.getString(scor_header_sequence_no).trim();
            } while (cursor.moveToNext());
        }
        return levelHeader;
    }

    public void updateScor(int scor, String level_header, int level_section) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCOR_SCORS, scor);
        values.put(SCOR_LEVELS_HEADER, level_header);
        values.put(SCOR_LEVEL_SECTION, level_section);
        db.update(TABLE_NAME_SCORS, values, null,null);
    }

    public String getUserName() {
        String name = null;
        String selectQuery = "SELECT " + SCOR_NAME + " FROM " + TABLE_NAME_SCORS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int name_sequence_no = cursor.getColumnIndex(SCOR_NAME);
        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(name_sequence_no);
            } while (cursor.moveToNext());
        }
        return name;
    }


    //-------------------------------------------------------------------FINISHED







    /*public void MyAnswerSave(String getAnswer, int position) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MY_ANSWER, getAnswer);
        contentValues.put(MY_ANSWER_POSITION, position);

        db.insert(TABLE_NAME_MYANSWERS, null, contentValues);
        db.close();
    }*/


    public void DELETE() {
        String selectQuery = "delete from '" + TABLE_NAME_SCORS + "'";
        SQLiteDatabase db_sil = this.getWritableDatabase();
        db_sil.delete(TABLE_NAME_SCORS, null, null);
        db_sil.execSQL(selectQuery);
        db_sil.close();
    }


    public int getUserScor(String name) {
        int scor = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_SCORS, new String[]{SCOR_ID, SCOR_NAME, SCOR_SCORS}, SCOR_NAME + "=?",
                new String[]{String.valueOf(name)}, null, null, null, null);
        int scor_sequence_no = cursor.getColumnIndex(SCOR_SCORS);
        if (cursor.moveToFirst()) {
            do {
                scor = cursor.getInt(scor_sequence_no);
            } while (cursor.moveToNext());
        }
        return scor;
    }


    public void deleteScor(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_SCORS, SCOR_NAME + " = ?",
                new String[]{String.valueOf(name)});
        db.close();
    }


    /*  Son Eklenenin IDsi  */
    public int scorID() {
        List<Integer> scorsIdList = new ArrayList<>();
        //SELECT scor_id FROM scors ORDER BY scor_id DESC
        String selectQuery = "SELECT " + SCOR_ID + " FROM " + TABLE_NAME_SCORS + " ORDER BY " + SCOR_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int scor_sequence_no = cursor.getColumnIndex(SCOR_ID);
        if (cursor.moveToFirst()) {
            do {
                int scor = cursor.getInt(scor_sequence_no);
                scorsIdList.add(scor);
            } while (cursor.moveToNext());
        }
        return scorsIdList.get(0);
    }

    public void deleteScor(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.rawQuery("DELETE FROM " + TABLE_NAME_SCORS+"", null);
        /*db.delete(TABLE_NAME_SCORS, SCORS_ID + "="
                + id, null);*/
        db.delete(TABLE_NAME_SCORS, SCOR_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }


    public List<Integer> getAllScorsDescInt() {
        List<Integer> scorsList = new ArrayList<>();
        String selectQuery = "SELECT " + SCOR_SCORS + " FROM " + TABLE_NAME_SCORS + " ORDER BY " + SCOR_SCORS + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int scor_sequence_no = cursor.getColumnIndex(SCOR_SCORS);
        if (cursor.moveToFirst()) {
            do {
                int scor = cursor.getInt(scor_sequence_no);
                scorsList.add(scor);
            } while (cursor.moveToNext());
        }
        return scorsList;
    }

    /*public List<Scors> getAllScorsDesc() {

        List<Scors> scorsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME_SCORS + " ORDER BY " + SCOR_SCORS + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int name_sequence_no = cursor.getColumnIndex(SCOR_NAME);
        int scor_sequence_no = cursor.getColumnIndex(SCOR_SCORS);
        int level_sequence_no = cursor.getColumnIndex(SCOR_LEVELS_HEADER);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(name_sequence_no);
                int scor = cursor.getInt(scor_sequence_no);
                String level = cursor.getString(level_sequence_no);
                scorsList.add(new Scors(name, scor, level));
            } while (cursor.moveToNext());
        }
        return scorsList;
    }*/


    public String dataControl(String name) {
        String s = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME_SCORS + " WHERE " + SCOR_NAME + " like '" + name + "'", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    s = c.getString(c.getColumnIndex(SCOR_NAME));
                } while (c.moveToNext());
            }
        }
        return s;

    }
}


