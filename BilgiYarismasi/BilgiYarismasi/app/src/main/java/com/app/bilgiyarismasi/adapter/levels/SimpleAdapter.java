package com.app.bilgiyarismasi.adapter.levels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.app.bilgiyarismasi.R;
import com.app.bilgiyarismasi.db.Database;
import com.app.bilgiyarismasi.entity.Levels;
import com.app.bilgiyarismasi.entity.Scors;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {
    private static final int COUNT = 36;
    private static int POINT = 0;
    private int LOCKED = 0;
    private int level_pointer = 0;
    private static final String LEVEL_EASY = "Kolay";
    private static final String LEVEL_MIDDLE = "Orta";
    private static final String LEVEL_HARD = "Zor";
    private final Context mContext;
    public List<Levels> levels_list;
    public List<Scors> scors_list;
    private Database db;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView point;
        public final ImageView locked;
        public final LinearLayout _background;

        public SimpleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.view_level_text);
            point = (TextView) view.findViewById(R.id.view_level_point);
            locked = (ImageView) view.findViewById(R.id.view_level_lock);
            _background = (LinearLayout) view.findViewById(R.id.view_level_background);

        }
    }

    public SimpleAdapter(Context context) {

        mContext = context;
        listFill();
        listUpdate();
    }

    private void listUpdate() {
        db = new Database(mContext);
        scors_list = new ArrayList<>();
        scors_list = db.getAllScors();

        //String headers = scors_list.get(0).getScor_header_level();
        int section = scors_list.get(0).getScor_section();
        String header = scors_list.get(0).getScor_header_level();
        int pointer = 0;
        if (header.trim().equals(LEVEL_EASY))
            pointer = 1;
        if (header.trim().equals(LEVEL_MIDDLE))
            pointer = 2;
        if (header.trim().equals(LEVEL_HARD))
            pointer = 3;

        listSettings(section, pointer);
    }

    private void listSettings(int section, int pointer) {

        switch (pointer) {
            case 2:
                section = section + 12;
                break;
            case 3:
                section = section + 24;
                break;
            default:break;
        }

        for (int s = 0; s < section; s++) {
            int v = levels_list.get(s).getLevel_name();
            int p = levels_list.get(s).getLevel_point();

            levels_list.set(s, new Levels(s, v, p, 1));
        }
    }

    private void listFill() {
        levels_list = new ArrayList<>();

        level_pointer = 0;
        int level = 0;

        for (int i = 0; i < COUNT; i++) {
            if (i < 12) {
                level_pointer = 1;
                level = i + 1;
            } else if (i < 24) {
                level_pointer = 2;
                level = i + 1 - 12;
            } else if (i < 36) {
                level_pointer = 3;
                level = i + 1 - 24;
            }

            //BÖLÜM PUANI
            if (level % 12 == 1) {
                POINT = 0;
            }
            POINT = 240 + level_pointer * level * 10 + POINT;

            //BÖLÜM ADI
            int level_value = level % 12;
            if (level % 12 == 0) {
                level_value = 12;
            }


            //KİLİT
            LOCKED = 0;

            addItem(i, level_value);

        }

    }


    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.custom_gridview, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {


        holder.title.setText(String.valueOf(levels_list.get(position).getLevel_name()));
        holder.point.setText(String.valueOf(levels_list.get(position).getLevel_point()));

        if (levels_list.get(position).getLocked() == 1) {
            holder.locked.setBackgroundResource(R.drawable.tik);
            holder._background.setBackgroundResource(R.drawable.level_active_shape);
        } else {
            holder.locked.setBackgroundResource(R.drawable.locked);
            holder._background.setBackgroundResource(R.drawable.level_passive_shape);
        }
    }

    public void addItem(int position, int level_value) {
        //position += 1;
        levels_list.add(new Levels(position, level_value, POINT, LOCKED));
        notifyItemInserted(position - 1);
    }

    public void removeItem(int position) {
        //mItems.remove(position);
        levels_list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return levels_list.size();
        //return mItems.size();
    }
}