package com.app.bilgiyarismasi.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bilgiyarismasi.R;

import java.util.ArrayList;
import java.util.List;

import com.app.bilgiyarismasi.adapter.levels.SectionedGridRecyclerViewAdapter;
import com.app.bilgiyarismasi.adapter.levels.SimpleAdapter;
import com.app.bilgiyarismasi.db.Database;
import com.app.bilgiyarismasi.entity.Scors;

public class ShowLevel extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Intent intent;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_level);

        //Your RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.view_recycler_level);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        //Your RecyclerView.Adapter
        mAdapter = new SimpleAdapter(getApplicationContext());


        //This is the code to provide a sectioned grid
        List<SectionedGridRecyclerViewAdapter.Section> sections =
                new ArrayList<SectionedGridRecyclerViewAdapter.Section>();

        //Sections
        sections.add(new SectionedGridRecyclerViewAdapter.Section(0, R.drawable.easy));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(12, R.drawable.middle));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(24, R.drawable.hard));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(36, R.drawable.bottom));

        //Add your adapter to the sectionAdapter
        SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
        SectionedGridRecyclerViewAdapter mSectionedAdapter = new
                SectionedGridRecyclerViewAdapter(getApplicationContext(), R.layout.section_header, R.id.view_headers, mRecyclerView, mAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        //Apply this adapter to the RecyclerView
        mRecyclerView.setAdapter(mSectionedAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private boolean gameState() {
        List<Scors> scorList = new ArrayList<>();
        db = new Database(getApplicationContext());
        scorList = db.getAllScors();
        int section = scorList.get(0).getScor_section();
        String header = scorList.get(0).getScor_header_level();
        if (header.equals("Zor") && section == 12)
            return true;
        else
            return false;
    }


                /*if (game_finish) {
                    db = new Database(getApplicationContext());
                    db.updateScor(0, username, LEVEL_EASY, 0);
                    db.deleteRepeatingQuestions();
                }*/


    public void clicksLevel(View v) {
        switch (v.getId()) {
            case R.id.view_back:
                onBackPressed();
                break;
            case R.id.view_play:
                boolean game_finish_state = gameState();
                if (!game_finish_state) {
                    intent = new Intent(getApplicationContext(), com.app.bilgiyarismasi.activitys.QuizScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Bilgi Yarışması");
                    alertDialog.setMessage("Oyun tamamlanmıştır. Oyunu baştan oynamak istiyorsanız bu oyunu " +
                            "silmelisiniz. Oyunu baştan oynamak ister misiniz ?");
                    alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            db = new Database(getApplicationContext());
                            db.updateScor(0, "Kolay", 0);
                            db.deleteRepeatingQuestions();
                            intent = new Intent(getApplicationContext(), ShowLevel.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Artık oyunu oynayabilirsiniz ...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialog.show();
                }
                break;
        }
    }
}
