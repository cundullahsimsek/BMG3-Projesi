package com.app.bilgiyarismasi.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.bilgiyarismasi.R;

import java.util.ArrayList;
import java.util.List;

import com.app.bilgiyarismasi.db.Database;
import com.app.bilgiyarismasi.entity.Scors;

public class QuizResults extends AppCompatActivity {

    private static final int ALERT_DIALOG_ID = 0;
    private static final String LEVEL_EASY = "Kolay";
    private static final String LEVEL_MIDDLE = "Orta";
    private static final String LEVEL_HARD = "Zor";

    private ProgressBar progressBarCircle;
    private Button btn_restart;
    private TextView txt_percent, txt_rightAnswer, txt_wrongAnswer, txt_emptyAnswer, txt_scor, txt_generate_scor, txt_level_header, txt_level_section;

    private Dialog dialog;
    private Database db;
    private Handler handler = new Handler();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Intent intent;

    private int pStatus = 0;
    private double correct_answer = 0.0;
    private double wrong_answer = 0.0;
    private double empty_answer = 0.0;


    private List<Scors> scorList;
    private List<Scors> newScorList;
    private List<Integer> scorListSequence;

    public int scorSize;
    private int btnID;
    private int mySequenceIlk;
    private int mySequenceSon;
    private int oldScor = 0;
    private boolean game_finish = false;
    private int totalScor;
    private int level_section;
    private int newScor = 0;
    private int q_count = 0;
    private int resultPercent = 0;
    private String username;
    private String level_header;
    private  int level_pointer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        initViews();
        getScorDatas();
        getDataOnShared();
        calculateResult();
        scorSave();
        setNewScor();
        progresBarRate();


        //congratulationsMessage();

    }


    @Override
    public void onBackPressed() {
    }

    private void initViews() {
        progressBarCircle = (ProgressBar) findViewById(R.id.view_progressBarCircle);
        txt_percent = (TextView) findViewById(R.id.view_percentiles);
        txt_rightAnswer = (TextView) findViewById(R.id.view_rightAnswer);
        txt_wrongAnswer = (TextView) findViewById(R.id.view_wrongAnswer);
        txt_emptyAnswer = (TextView) findViewById(R.id.view_emptyAnswer);
        txt_scor = (TextView) findViewById(R.id.view_scor);
        txt_generate_scor = (TextView) findViewById(R.id.view_generate_scor);
        txt_level_header = (TextView) findViewById(R.id.view_level_header);
        txt_level_section = (TextView) findViewById(R.id.view_level_section);
        btn_restart = (Button) findViewById(R.id.view_button_home);
    }

    private void getScorDatas() {
        scorList = new ArrayList<>();
        db = new Database(getApplicationContext());
        scorList = db.getAllScors();

        username = scorList.get(0).getUsername();
        oldScor = scorList.get(0).getScor();
        level_header = scorList.get(0).getScor_header_level();
        level_section = scorList.get(0).getScor_section();
    }

    private void getDataOnShared() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();


        correct_answer = (double) sharedPreferences.getInt("correct", 0);
        wrong_answer = (double) sharedPreferences.getInt("wrong", 0);
        empty_answer = (double) sharedPreferences.getInt("empty", 0);
        newScor = sharedPreferences.getInt("scor", 0);
        q_count = sharedPreferences.getInt("count", 0);
    }


    private void scorSave() {
        if (newScor < 0)
            newScor = 0;

        totalScor = oldScor + newScor;

        level_pointer = 0;
        int level_section2 = 0;
        if (level_header.trim().equals(LEVEL_EASY))
            level_pointer = 1;
        if (level_header.trim().equals(LEVEL_MIDDLE))
            level_pointer = 2;
        if (level_header.trim().equals(LEVEL_HARD))
            level_pointer = 3;

        int POINT = 0;

        level_section = level_section % 13;
        level_section2 = level_section;
        if (level_section == 12) {
            level_section = 0;
            level_pointer++;
        }

        for (int s = 1; s <= (level_section + 1); s++) {
            POINT = 240 + (level_section + 1) * level_pointer * 10 + POINT;
        }

        if (level_pointer == 3 && level_section2 == 11 && totalScor >= POINT) {
            level_section++;
            game_finish = true;
            showDialog(ALERT_DIALOG_ID);
        } else if (totalScor >= POINT && level_section2 == 12) {
            switch (level_pointer) {
                case 1:
                    level_header = LEVEL_EASY;
                    break;
                case 2:
                    level_header = LEVEL_MIDDLE;
                    break;
                case 3:
                    level_header = LEVEL_HARD;
                    break;
            }
            showDialog(ALERT_DIALOG_ID);
            totalScor = 0;
        } else if (totalScor >= POINT && level_section2 < 12) {
            level_section++;
            showDialog(ALERT_DIALOG_ID);
        }

        db.updateScor(totalScor,level_header, level_section);
    }


    private void setNewScor() {
        txt_rightAnswer.setText((int) correct_answer + "");
        txt_wrongAnswer.setText((int) wrong_answer + "");
        txt_emptyAnswer.setText((int) empty_answer + "");
        txt_scor.setText(newScor + "");
        txt_generate_scor.setText(totalScor + "");
        txt_level_header.setText(level_header);
        txt_level_section.setText(String.valueOf(level_section));
    }

    public void ButtonClick(View v) {
        btnID = v.getId();
        switch (btnID) {
            case R.id.view_show_answer:
                intent = new Intent(getApplicationContext(), ShowAnswer.class);
                startActivity(intent);
                break;
            case R.id.view_button_home:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }


    private void calculateResult() {
        int fullPoint = q_count * 10;
        resultPercent = (100 * newScor) / fullPoint;//(100*5)/100
    }

    //Her sorunun doğru yanıtı 10puan
    //Her sorununyanlış yanıtı -5 puan
    //Her oyunda 20 soru gelecek: 20*10= max puan=200
    //Oyun yarıda kesilirse yani 3 yanlış ve 7 doğru: 3*-5=-15 + 7*10=70 = 55   10
    private void progresBarRate() {
        progressBarCircle.setMax(1000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus < 1000) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pStatus++;
                            if (pStatus <= (resultPercent * 10)) {
                                progressBarCircle.setProgress(pStatus);
                                txt_percent.setText("%" + pStatus / 10);
                            }
                            progressBarCircle.setSecondaryProgress(pStatus * 4 / 3);
                        }
                    });

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ALERT_DIALOG_ID:
                dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_success);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation1;
                int _width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
                int _height = (int) (getResources().getDisplayMetrics().heightPixels * 0.4);
                dialog.getWindow().setLayout(_width, _height);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);

                ImageButton ib = (ImageButton) dialog.getWindow().findViewById(R.id.view_close_tebrik);
                TextView tv2 = (TextView) dialog.findViewById(R.id.view_point_value2);
                TextView tv3 = (TextView) dialog.findViewById(R.id.view_level);

                tv2.setText(String.valueOf(totalScor));
                if (game_finish)
                    tv3.setText("OYUN BAŞARIYLA TAMAMLANMIŞTIR.");
                else if (level_section == 0 && level_pointer!=3)
                    tv3.setText(level_header + " seviyenin\nkilidini açtınız ...");
                else
                    tv3.setText(level_header + " seviyesinde\n" + level_section + ". Bölüme geçtiniz.");

                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        removeDialog(ALERT_DIALOG_ID);
                    }
                });

                return dialog;
            default:
                return super.onCreateDialog(id);
        }
    }
}
