package com.app.bilgiyarismasi.activitys;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import com.app.bilgiyarismasi.R;
import com.app.bilgiyarismasi.db.Database;
import com.app.bilgiyarismasi.entity.Questions;
import com.app.bilgiyarismasi.entity.Scors;
import com.app.bilgiyarismasi.json.JSONParser;
import com.app.bilgiyarismasi.utils.ConstantValues;
import com.app.bilgiyarismasi.utils.InternetConnection;
import com.app.bilgiyarismasi.utils.Keys;

public class MainActivity extends AppCompatActivity {

    private static final int ALERT_DIALOG_CHANGE_USER = 0;
    private static final int ALERT_DIALOG_SCOR = 1;

    private TextView dialog_user_header, tv_username, tv_scor, tv_level, tv_section;
    private Dialog dialog_change_user, dialog_scor;
    private Button dialog_btn_add, dialog_btn_cancel;
    private EditText dialog_et_username;
    private ImageButton btn_scor_menu_close;

    public Questions[] questionsList;
    private Database db;
    private Intent intent;

    public int server_question_count;
    public boolean web_service_state;
    private int scor_count;
    private boolean name_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        try {
            connectionInternet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.view_username_update:
                name_state = true;
                showDialog(ALERT_DIALOG_CHANGE_USER);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        name_state = false;
        web_service_state = false;
    }

    private void connectionInternet() throws IOException {
        db = new Database(getApplicationContext());
        if (InternetConnection.checkConnection(getApplicationContext()))
            new MyAsynTask().execute("count");
        else if (db.getQuestionCount() == 0)
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.firstNotConnection), Toast.LENGTH_LONG).show();
    }

    private void addUser() {
        scor_count = db.getScorCount();
        if (scor_count < 1) {
            showDialog(ALERT_DIALOG_CHANGE_USER);
        }
    }

    public void ButtonsClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_start:
                int total_question_count = db.getQuestionCount();
                if (total_question_count != 0) {
                        intent = new Intent(getApplicationContext(), com.app.bilgiyarismasi.activitys.ShowLevel.class);
                        startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.notConnection), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_scor:
                showDialog(ALERT_DIALOG_SCOR);
                break;
            case R.id.button_share:
                String link = getResources().getString(R.string.my_app_link);
                Intent _intent = new Intent();
                _intent.setAction(Intent.ACTION_SEND);
                _intent.putExtra(Intent.EXTRA_TEXT, link);
                _intent.setType("text/plain");
                startActivity(_intent);
                break;
            case R.id.button_exit:
                intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                System.exit(0);
                break;

        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ALERT_DIALOG_CHANGE_USER:
                dialog_change_user = new Dialog(this, R.style.dialog_name);
                dialog_change_user.setContentView(R.layout.custom_user_dialog);
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.25);
                dialog_change_user.getWindow().setLayout(width, height);

                dialog_btn_add = (Button) dialog_change_user.findViewById(R.id.view_btn_add);
                dialog_btn_cancel = (Button) dialog_change_user.findViewById(R.id.view_btn_cancel);
                dialog_user_header = (TextView) dialog_change_user.findViewById(R.id.view_user_header);
                dialog_et_username = (EditText) dialog_change_user.findViewById(R.id.view_field_name);
                dialog_change_user.setCancelable(false);

                if (name_state)
                    dialog_user_header.setText(getResources().getString(R.string.update_username));
                else
                    dialog_user_header.setText(getResources().getString(R.string.add_username));


                dialog_btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = dialog_et_username.getText().toString().trim();
                        if (!username.equals("")) {
                            db = new Database(getApplicationContext());
                            if (name_state) {
                                db.updateUserName(username);
                                Toast.makeText(getApplicationContext(), username + " olarak isminiz değiştirildi.", Toast.LENGTH_SHORT).show();
                            } else {
                                db.saveScors(username);
                                Toast.makeText(getApplicationContext(), username + " kullanıcısı oluşturuldu.", Toast.LENGTH_SHORT).show();
                            }
                            dialog_et_username.setText("");
                            dialog_change_user.cancel();
                            removeDialog(ALERT_DIALOG_CHANGE_USER);
                        } else {
                            Toast.makeText(MainActivity.this, "Lütfen isminizi giriniz...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog_btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!name_state) {
                            if (scor_count < 1) {
                                db.saveScors("Root");
                                Toast.makeText(getApplicationContext(), "Root kullanıcısı oluşturuldu.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog_change_user.cancel();
                        removeDialog(ALERT_DIALOG_CHANGE_USER);
                    }
                });
                return dialog_change_user;

            case ALERT_DIALOG_SCOR:
                dialog_scor = new Dialog(this);
                dialog_scor.setContentView(R.layout.scor_dialog);
                dialog_scor.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                int _width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                int _height = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
                dialog_scor.getWindow().setLayout(_width, _height);
                dialog_scor.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_scor.setCancelable(false);

                btn_scor_menu_close = (ImageButton) dialog_scor.getWindow().findViewById(R.id.view_scor_menu_close);
                tv_username = (TextView) dialog_scor.findViewById(R.id.view_hello_username);
                tv_scor = (TextView) dialog_scor.findViewById(R.id.view_point);
                tv_level = (TextView) dialog_scor.findViewById(R.id.view_level);
                tv_section = (TextView) dialog_scor.findViewById(R.id.view_section);

                db = new Database(getApplicationContext());
                List<Scors> listAllScors = db.getAllScors();

                String list_username = listAllScors.get(0).getUsername();
                int list_scor = listAllScors.get(0).getScor();
                String list_level_header = listAllScors.get(0).getScor_header_level();
                int list_section = listAllScors.get(0).getScor_section();

                tv_username.setText("Merhaba" + "\n" + list_username);
                tv_scor.setText(String.valueOf(list_scor));
                tv_level.setText(list_level_header);
                tv_section.setText(String.valueOf(list_section));

                btn_scor_menu_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_scor.dismiss();
                        removeDialog(ALERT_DIALOG_SCOR);
                    }
                });
                return dialog_scor;
        }
        return dialog_change_user;
    }

    public class MyAsynTask extends AsyncTask<String, Void, Void> {
        ProgressDialog ringProgressDialog;
        //LayoutInflater layoutInflater = getLayoutInflater();
        //GifView gifView1;
        //MaterialDialog dialog_gif;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (web_service_state) {
                /*View view = layoutInflater.inflate(R.layout.dialog_loading, null);
                gifView1 = (GifView) view.findViewById(R.id.gif1);
                dialog_gif = new MaterialDialog.Builder(MainActivity.this).customView(view, true).build();
                dialog_gif.show();
                gifView1.setVisibility(View.VISIBLE);
                dialog_gif.setCancelable(false);*/

                ringProgressDialog = ProgressDialog.show(MainActivity.this, "Lütfen bekleyiniz ...", "Sorular Yükleniyor ...", true);
                ringProgressDialog.setCancelable(true);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            String getParam = params[0];
            try {
                if (getParam.equals("count")) {
                    JSONArray jsonArray_count = JSONParser.getDataCount(ConstantValues.API_URL);
                    JSONObject jsonObject = jsonArray_count.getJSONObject(0);
                    server_question_count = jsonObject.getInt(Keys.KEY_QUESTION_COUNT);
                    web_service_state = true;
                } else if (getParam.equals("questions")) {
                    questionsList = JSONParser.getDataFromService(ConstantValues._URL, Questions[].class);
                    web_service_state = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            db = new Database(getApplicationContext());
            if (web_service_state) {
                int local_question_count = db.getQuestionCount();
                if (server_question_count > local_question_count || server_question_count < local_question_count) {
                    new MyAsynTask().execute("questions");
                }
            } else {
                db.deleteQuestions();
                long save_error = db.saveQuestions(questionsList);
                if (save_error == -1) {
                    Toast.makeText(getApplicationContext(), "Kayıt sırasında bir hata oluştu.", Toast.LENGTH_SHORT).show();
                }
                //dialog_gif.dismiss();
                ringProgressDialog.dismiss();
                addUser();
            }
        }
    }
}
