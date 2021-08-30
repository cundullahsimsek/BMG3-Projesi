package com.app.bilgiyarismasi.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.bilgiyarismasi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.app.bilgiyarismasi.db.Database;
import com.app.bilgiyarismasi.entity.MyAnswer;
import com.app.bilgiyarismasi.entity.Questions;

public class QuizScreen extends AppCompatActivity {

    private static final int ALERT_DIALOG_TIME_FINISHED = 0;
    private static final int ALERT_DIALOG_GAMEOVER = 1;
    private static final String LEVEL_KOLAY = "Kolay";
    private static final String LEVEL_ORTA = "Orta";
    private static final String LEVEL_ZOR = "Zor";
    private static final int PROGRESS_BAR_MAX = 5000;

    private TextView txt_question, txt_time, txt_heart_packets, txt_count, tv_heart, tv_point;
    private ImageButton imgBtn_pass, imgBtn_percent, imgBtn_time, btn_watch_heart_plus, btn_heart_plus;
    private Button btn_a, btn_b, btn_c, btn_d;
    private LinearLayout linear_question;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private Dialog dialog_question_pass, dialog_gameover;

    private Database db;
    private Intent intent;
    private AlphaAnimation alpha;

    private List<Questions> questionsList;
    private List<Integer> repetitiveQuestionList;
    private List<MyAnswer> myAnswerList = new ArrayList<>();
    private List<Integer> percentList;

    private int position = 0;
    private int questionSequence = 0;
    private boolean my_answer_state = false;
    private int pass_packet = 1;
    private int heart_packet = 3;
    private int first_elimination = 0, second_elimination = 0;
    private int scor = 0;
    private int timer_active = 0;
    private boolean isPaused = false;
    private int correct = 0;
    private int wrong = 0;
    private int empty = 0;
    private int internalPosition = 0;
    public String level = null;

    private CountDownTimer timer;
    private static long millisInFuture = 30000;
    private static long countDownInterval = 1;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_screen);


        initViews();
        ToolbarCustom();
        AnimationPage();
        deleteAllMyAnswer();
        getLevelQuestions();
        shared();
        setQuestion();
        timer_active = 1;
    }

    @Override
    public void onBackPressed() {
    }

    private void initViews() {
        txt_question = (TextView) findViewById(R.id.view_question);
        btn_a = (Button) findViewById(R.id.view_button_a);
        btn_b = (Button) findViewById(R.id.view_button_b);
        btn_c = (Button) findViewById(R.id.view_button_c);
        btn_d = (Button) findViewById(R.id.view_button_d);
        linear_question = (LinearLayout) findViewById(R.id.view_question_linear);
        imgBtn_pass = (ImageButton) findViewById(R.id.view_pass);
        imgBtn_percent = (ImageButton) findViewById(R.id.view_percent);
        imgBtn_time = (ImageButton) findViewById(R.id.view_time);
        txt_count = (TextView) findViewById(R.id.view_count);

        txt_heart_packets = (TextView) findViewById(R.id.view_heart_packet);
        txt_heart_packets.setText(String.valueOf(heart_packet));
    }

    private void ToolbarCustom() {
        toolbar = (Toolbar) findViewById(R.id.view_toolbar);
        progressBar = (ProgressBar) findViewById(R.id.view_progressbar);
        txt_time = (TextView) findViewById(R.id.view_time_count);
        setSupportActionBar(toolbar);
        progressBar.setMax(PROGRESS_BAR_MAX);
    }

    private void AnimationPage() {
        Animation animyon4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_question);
        Animation animyon1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation1);
        Animation animyon2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation2);

        btn_a.startAnimation(animyon1);
        btn_b.startAnimation(animyon2);
        btn_c.startAnimation(animyon1);
        btn_d.startAnimation(animyon2);
        linear_question.startAnimation(animyon4);
    }

    private void deleteAllMyAnswer() {
        db = new Database(getApplicationContext());
        db.deleteMyAnswers();
    }

    private void getLevelQuestions() {
        db = new Database(getApplicationContext());
        String level_header = db.getScorLevelHeader();
        if (level_header.equals(LEVEL_KOLAY))
            level = LEVEL_KOLAY;
        else if (level_header.equals(LEVEL_ORTA))
            level = LEVEL_ORTA;
        else if (level_header.equals(LEVEL_ZOR))
            level = LEVEL_ZOR;


        repetitiveQuestionList = new ArrayList<>();
        questionsList = new ArrayList<>();
        repetitiveQuestionList = db.getRepetitiveAllQuestion();// Gösterilmiş soruların id sini bu listede tut
        questionsList = db.getAllQuestions(level, repetitiveQuestionList);

    }

    private void shared() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
    }

    private void setQuestion() {
        questionSequence++;
        if (questionSequence > 20) {
            saveMyAnswer();
            calculateScor();
            editor.putInt("scor", scor);
            editor.putInt("correct", correct);
            editor.putInt("wrong", wrong);
            editor.putInt("empty", empty);
            editor.putInt("count", (questionSequence - 1));
            editor.commit();

            intent= new Intent(getApplicationContext(), com.app.bilgiyarismasi.activitys.QuizResults.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            txt_count.setText(String.valueOf(questionSequence));
            randomPosition();
            txt_question.setText(questionsList.get(position).getQuestion());
            btn_a.setText(questionsList.get(position).getAnswer_a());
            btn_b.setText(questionsList.get(position).getAnswer_b());
            btn_c.setText(questionsList.get(position).getAnswer_c());
            btn_d.setText(questionsList.get(position).getAnswer_d());
            timerProgresBar();
        }
    }

    private void randomPosition() {
        Random rnd = new Random();
        position = rnd.nextInt(questionsList.size());
        internalPosition = questionsList.get(position).getQ_id();

        // Tekrar olmasın diye gelen soruların id lerini kaydediyoruz.Repeating Tablosuna
        db = new Database(getApplicationContext());
        db.repetitiveQuestionSave(internalPosition);
    }

    private void timerProgresBar() {
        if (timer_active != 0) {
            timer.cancel();
        }
        millisInFuture = 30000;
        progressBar.setProgress(PROGRESS_BAR_MAX);
        timer = new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                if (isPaused) {
                    cancel();
                } else {
                    txt_time.setText("" + millisUntilFinished / 1000);
                    int progress = (int) (millisUntilFinished / 6);
                    progressBar.setProgress(progress);
                }
            }

            public void onFinish() {
                try {
                    showDialog(ALERT_DIALOG_TIME_FINISHED);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Bekleyiniz...", Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    public void ButtonsClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.view_button_a:
                btn_a.setClickable(false);
                answerControl(btn_a.getText().toString().trim(), id);
                break;
            case R.id.view_button_b:
                btn_b.setClickable(false);
                answerControl(btn_b.getText().toString().trim(), id);
                break;
            case R.id.view_button_c:
                btn_c.setClickable(false);
                answerControl(btn_c.getText().toString().trim(), id);
                break;
            case R.id.view_button_d:
                btn_d.setClickable(false);
                answerControl(btn_d.getText().toString().trim(), id);
                break;
            case R.id.view_pass:
                passQuestion();
                break;
            case R.id.view_percent:
                selectedPercentButton();
                break;
            case R.id.view_time:
                imgBtn_time.setClickable(false);
                imgBtn_time.setBackgroundResource(R.drawable.clock_passive);
                timerProgresBar();
                break;
        }
    }

    private void answerControl(String selected_answer, int selected_btn_id) {
        my_answer_state = false;
        myAnswerList.add(new MyAnswer(internalPosition, selected_answer));

        if (questionsList.get(position).getCorrect_answer().equals(selected_answer)) {
            my_answer_state = true;
            correct++;
        } else {
            my_answer_state = false;
            wrong++;
            heartDecrease();//Can Azalt()
        }
        switch (selected_btn_id) {
            case R.id.view_button_a:
                if (my_answer_state == true) {
                    btn_a.setBackgroundResource(R.drawable.button_correct);
                } else {
                    btn_a.setBackgroundResource(R.drawable.buton_wrong);
                }
                break;
            case R.id.view_button_b:
                if (my_answer_state == true) {
                    btn_b.setBackgroundResource(R.drawable.button_correct);
                } else {
                    btn_b.setBackgroundResource(R.drawable.buton_wrong);
                }
                break;
            case R.id.view_button_c:
                if (my_answer_state == true) {
                    btn_c.setBackgroundResource(R.drawable.button_correct);
                } else {
                    btn_c.setBackgroundResource(R.drawable.buton_wrong);
                }
                break;
            case R.id.view_button_d:
                if (my_answer_state == true) {
                    btn_d.setBackgroundResource(R.drawable.button_correct);
                } else {
                    btn_d.setBackgroundResource(R.drawable.buton_wrong);
                }
        }

        if (questionsList.get(position).getCorrect_answer().equals(questionsList.get(position).getAnswer_a()))
            btn_a.setBackgroundResource(R.drawable.button_correct);
        if (questionsList.get(position).getCorrect_answer().equals(questionsList.get(position).getAnswer_b()))
            btn_b.setBackgroundResource(R.drawable.button_correct);
        if (questionsList.get(position).getCorrect_answer().equals(questionsList.get(position).getAnswer_c()))
            btn_c.setBackgroundResource(R.drawable.button_correct);
        if (questionsList.get(position).getCorrect_answer().equals(questionsList.get(position).getAnswer_d()))
            btn_d.setBackgroundResource(R.drawable.button_correct);

        questionsList.remove(position);//Gösterilen soruyu listeden sil
        timeAndSetNewQuestion();
    }

    private void passQuestion() {
        pass_packet--;
        myAnswerList.add(new MyAnswer(internalPosition, "Bos"));
        empty++;
        if (pass_packet == 0) {
            imgBtn_pass.setClickable(false);
            imgBtn_pass.setBackgroundResource(R.drawable.skip_passive);
        }
        questionsList.remove(position);
        timeAndSetNewQuestion();
    }

    private void timeAndSetNewQuestion() {
        if (heart_packet != 0) {
            int SPLASH_TIME_OUT = 700;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AnimationPage();
                    setQuestion();
                    setButtonsOldColor();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    private void setButtonsOldColor() {
        btn_a.setBackgroundResource(R.drawable.button_normal);
        btn_b.setBackgroundResource(R.drawable.button_normal);
        btn_c.setBackgroundResource(R.drawable.button_normal);
        btn_d.setBackgroundResource(R.drawable.button_normal);

        btn_a.setClickable(true);
        btn_b.setClickable(true);
        btn_c.setClickable(true);
        btn_d.setClickable(true);
    }

    private void heartDecrease() {
        heart_packet--;
        if (heart_packet != 0)
            txt_heart_packets.setText(String.valueOf(heart_packet));
        else {
            txt_heart_packets.setText(String.valueOf(heart_packet));
            showDialog(ALERT_DIALOG_GAMEOVER);
        }
    }

    private void calculateScor() {
        if (pass_packet == 0)
            scor = correct * 10 - wrong * 4 - (empty - 1) * 2;
        else
            scor = correct * 10 - wrong * 4 - empty * 2;
    }

    private void saveMyAnswer() {
        db = new Database(getApplicationContext());
        db.myAnswerSave(myAnswerList);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ALERT_DIALOG_TIME_FINISHED:
                dialog_question_pass = new Dialog(this);
                dialog_question_pass.setTitle("Uyarı !");
                dialog_question_pass.setContentView(R.layout.custom_time_dialog);
                dialog_question_pass.setCancelable(false);

                Button btn_next_question = (Button) dialog_question_pass.findViewById(R.id.view_next_button);
                btn_next_question.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myAnswerList.add(new MyAnswer(internalPosition, "Bos"));
                        empty++;
                        timeAndSetNewQuestion();
                        dialog_question_pass.dismiss();
                        removeDialog(ALERT_DIALOG_TIME_FINISHED);
                    }
                });
                return dialog_question_pass;
            case ALERT_DIALOG_GAMEOVER:
                dialog_gameover = new Dialog(this);
                dialog_gameover.setContentView(R.layout.gameover_dialog);
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
                dialog_gameover.getWindow().setLayout(width, height);
                dialog_gameover.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_gameover.setCancelable(false);

                isPaused = true;//timer pause et

                tv_heart = (TextView) dialog_gameover.findViewById(R.id.view_heart);
                tv_point = (TextView) dialog_gameover.findViewById(R.id.view_point);
                Button btn_continuation = (Button) dialog_gameover.findViewById(R.id.view_continuation);
                Button btn_next = (Button) dialog_gameover.findViewById(R.id.view_next);
                btn_heart_plus = (ImageButton) dialog_gameover.findViewById(R.id.view_heart_plus);

                btn_watch_heart_plus = (ImageButton) dialog_gameover.findViewById(R.id.view_watch_heart_plus);
                btn_watch_heart_plus.setClickable(false);
                alpha = new AlphaAnimation(0.5F, 0.5F);
                alpha.setDuration(0);
                alpha.setFillAfter(true);
                btn_watch_heart_plus.startAnimation(alpha);

                calculateScor();//Scoru hesapla



                tv_heart.setText(String.valueOf(heart_packet));
                tv_point.setText(String.valueOf(scor));


                btn_watch_heart_plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                btn_heart_plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((scor - 50) >= 0) {
                            heart_packet++;
                            scor-=50;
                            tv_heart.setText(String.valueOf(heart_packet));
                            tv_point.setText(String.valueOf(scor));
                        } else {
                            Toast.makeText(QuizScreen.this, "Can almak için puanınız yetersiz ...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btn_continuation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (heart_packet != 0) {
                            isPaused = false;
                            timeAndSetNewQuestion();
                            dialog_gameover.dismiss();
                            removeDialog(ALERT_DIALOG_GAMEOVER);
                            txt_heart_packets.setText(String.valueOf(heart_packet));
                        } else {
                            Toast.makeText(QuizScreen.this, "Canınız tükenmiştir !!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveMyAnswer();
                        editor.putInt("scor", scor);
                        editor.putInt("correct", correct);
                        editor.putInt("wrong", wrong);
                        editor.putInt("empty", empty);
                        editor.putInt("count", questionSequence);
                        editor.commit();

                        intent= new Intent(getApplicationContext(), com.app.bilgiyarismasi.activitys.QuizResults.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                return dialog_gameover;
            default:
                return super.onCreateDialog(id);
        }
    }

    private void selectedPercentButton() {
        percentList = new ArrayList<>();
        percentList.add(0);
        percentList.add(1);
        percentList.add(2);
        percentList.add(3);

        imgBtn_percent.setClickable(false);
        imgBtn_percent.setBackgroundResource(R.drawable.percent_passive);
        if (questionsList.get(position).getCorrect_answer().equals(questionsList.get(position).getAnswer_a())) {
            randomFirstValue(0);
            randomSecondValue();
            switch (first_elimination) {
                case 1:
                    btn_b.setClickable(false);
                    btn_b.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 2:
                    btn_c.setClickable(false);
                    btn_c.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 3:
                    btn_d.setClickable(false);
                    btn_d.setBackgroundResource(R.drawable.passive_button);
                    break;
            }
            switch (second_elimination) {
                case 1:
                    btn_b.setClickable(false);
                    btn_b.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 2:
                    btn_c.setClickable(false);
                    btn_c.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 3:
                    btn_d.setClickable(false);
                    btn_d.setBackgroundResource(R.drawable.passive_button);
                    break;
            }

        } else if (questionsList.get(position).getCorrect_answer().equals(questionsList.get(position).getAnswer_b())) {
            randomFirstValue(1);
            randomSecondValue();
            switch (first_elimination) {
                case 0:
                    btn_a.setClickable(false);
                    btn_a.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 2:
                    btn_c.setClickable(false);
                    btn_c.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 3:
                    btn_d.setClickable(false);
                    btn_d.setBackgroundResource(R.drawable.passive_button);
                    break;
            }
            switch (second_elimination) {
                case 0:
                    btn_a.setClickable(false);
                    btn_a.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 2:
                    btn_c.setClickable(false);
                    btn_c.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 3:
                    btn_d.setClickable(false);
                    btn_d.setBackgroundResource(R.drawable.passive_button);
                    break;
            }
        } else if (questionsList.get(position).getCorrect_answer().equals(questionsList.get(position).getAnswer_c())) {
            randomFirstValue(2);
            randomSecondValue();
            switch (first_elimination) {
                case 0:
                    btn_a.setClickable(false);
                    btn_a.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 1:
                    btn_b.setClickable(false);
                    btn_b.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 3:
                    btn_d.setClickable(false);
                    btn_d.setBackgroundResource(R.drawable.passive_button);
                    break;
            }
            switch (second_elimination) {
                case 0:
                    btn_a.setClickable(false);
                    btn_a.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 1:
                    btn_b.setClickable(false);
                    btn_b.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 3:
                    btn_d.setClickable(false);
                    btn_d.setBackgroundResource(R.drawable.passive_button);
                    break;
            }
        } else if (questionsList.get(position).getCorrect_answer().equals(questionsList.get(position).getAnswer_d())) {
            randomFirstValue(3);
            randomSecondValue();
            switch (first_elimination) {
                case 0:
                    btn_a.setClickable(false);
                    btn_a.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 1:
                    btn_b.setClickable(false);
                    btn_b.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 2:
                    btn_c.setClickable(false);
                    btn_c.setBackgroundResource(R.drawable.passive_button);
                    break;
            }
            switch (second_elimination) {
                case 0:
                    btn_a.setClickable(false);
                    btn_a.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 1:
                    btn_b.setClickable(false);
                    btn_b.setBackgroundResource(R.drawable.passive_button);
                    break;
                case 2:
                    btn_c.setClickable(false);
                    btn_c.setBackgroundResource(R.drawable.passive_button);
                    break;
            }
        }

    }

    private void randomFirstValue(int number1) {
        percentList.remove(number1);
        int size = percentList.size();
        Random rn_ilk = new Random();
        int p = rn_ilk.nextInt(size);
        first_elimination = percentList.get(p);
        percentList.remove(p);
    }

    private void randomSecondValue() {
        Random rn_son = new Random();
        int size = percentList.size();
        int p = rn_son.nextInt(size);
        second_elimination = percentList.get(p);
    }
}

