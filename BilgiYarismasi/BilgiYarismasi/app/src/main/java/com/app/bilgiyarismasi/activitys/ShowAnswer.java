package com.app.bilgiyarismasi.activitys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bilgiyarismasi.R;

import java.util.ArrayList;
import java.util.List;

import com.app.bilgiyarismasi.adapter.MyRecycleViewAdaptor;
import com.app.bilgiyarismasi.db.Database;
import com.app.bilgiyarismasi.entity.MyAnswer;
import com.app.bilgiyarismasi.entity.Questions;
import com.app.bilgiyarismasi.entity.Scors;


public class ShowAnswer extends AppCompatActivity {

    private Button button_back;
    private RecyclerView recyclerView_main;
    private RecyclerView.Adapter adapter_main;
    private RecyclerView.LayoutManager layoutManager_main;
    private Database db;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<MyAnswer> getMyAnswersList;
    private List<Questions> getQuestionsList;
    private List<Questions> getAllList;
    private String level_header;
    private List<Integer> questionPositionList = new ArrayList<>();
    private List<Scors> scorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_show);

        initViews();
        getAnswers();
        setupAdapter();

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       
    }

    private void initViews() {
        button_back = (Button) findViewById(R.id.view_button_back);

        getMyAnswersList = new ArrayList<>();
        getQuestionsList = new ArrayList<>();

        recyclerView_main = (RecyclerView) findViewById(R.id.view_recycler);
        recyclerView_main.setHasFixedSize(true);
        layoutManager_main = new LinearLayoutManager(this);
        recyclerView_main.setLayoutManager(layoutManager_main);

    }

    private void getAnswers() {
        db = new Database(getApplicationContext());
        scorList = db.getAllScors();
        level_header = scorList.get(0).getScor_header_level().trim();

        getMyAnswersList = db.getAllMyAnswers();
        getQuestionsList = db._getAllQuestions(level_header);
    }

    private void setupAdapter() {
        getAllList = new ArrayList<>();
        for (int i = 0; i < getMyAnswersList.size(); i++) {
            int my_answer_position = getMyAnswersList.get(i).getMyAnswerPosition();
            for (int s = 0; s < getQuestionsList.size(); s++) {
                if (getQuestionsList.get(s).getQ_id() == my_answer_position) {
                    int id = getQuestionsList.get(s).getQ_id();
                    String answer = getQuestionsList.get(s).getCorrect_answer().trim();
                    String question = getQuestionsList.get(s).getQuestion();
                    String option_a = getQuestionsList.get(s).getAnswer_a().trim();
                    String option_b = getQuestionsList.get(s).getAnswer_b().trim();
                    String option_c = getQuestionsList.get(s).getAnswer_c().trim();
                    String option_d = getQuestionsList.get(s).getAnswer_d().trim();
                    String level = getQuestionsList.get(s).getLevel().trim();
                    getAllList.add(new Questions(id, question, option_a, option_b, option_c, option_d, answer,level));
                }
            }
        }
        adapter_main = new MyRecycleViewAdaptor(getAllList, getMyAnswersList);
        recyclerView_main.setAdapter(adapter_main);
    }


}
