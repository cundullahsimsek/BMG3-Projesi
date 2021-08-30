package com.app.bilgiyarismasi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.app.bilgiyarismasi.R;
import com.app.bilgiyarismasi.entity.MyAnswer;
import com.app.bilgiyarismasi.entity.Questions;

public class MyRecycleViewAdaptor extends RecyclerView.Adapter<MyRecycleViewAdaptor.ViewHolder> {
    public List<Questions> getQuestionsList = new ArrayList<>();
    public List<MyAnswer> getMyAnswersList = new ArrayList<>();

    public MyRecycleViewAdaptor(List<Questions> getQuestionsList, List<MyAnswer> getMyAnswersList) {
        this.getQuestionsList = getQuestionsList;
        this.getMyAnswersList = getMyAnswersList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_question, txt_choice_a, txt_choice_b, txt_choice_c, txt_choice_d, txt_my_answer, txt_count;

        public ViewHolder(View v) {
            super(v);
            txt_question = (TextView) v.findViewById(R.id.view_question);
            txt_choice_a = (TextView) v.findViewById(R.id.view_txt_a);
            txt_choice_b = (TextView) v.findViewById(R.id.view_txt_b);
            txt_choice_c = (TextView) v.findViewById(R.id.view_txt_c);
            txt_choice_d = (TextView) v.findViewById(R.id.view_txt_d);
            txt_my_answer = (TextView) v.findViewById(R.id.view_txt_answer);
            txt_count = (TextView) v.findViewById(R.id.view_count);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_recycler, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String my_answer = getMyAnswersList.get(position).getMyAnswer().trim();
        String answer = getQuestionsList.get(position).getCorrect_answer().trim();
        String question = getQuestionsList.get(position).getQuestion();
        String option_a = getQuestionsList.get(position).getAnswer_a().trim();
        String option_b = getQuestionsList.get(position).getAnswer_b().trim();
        String option_c = getQuestionsList.get(position).getAnswer_c().trim();
        String option_d = getQuestionsList.get(position).getAnswer_d().trim();

        holder.txt_choice_a.setText(option_a);
        holder.txt_choice_b.setText(option_b);
        holder.txt_choice_c.setText(option_c);
        holder.txt_choice_d.setText(option_d);
        holder.txt_question.setText(question);
        holder.txt_my_answer.setText(my_answer);
        holder.txt_count.setText(String.valueOf(position + 1));

        if (answer.equals(option_a))
            holder.txt_choice_a.setBackgroundResource(R.drawable.show_answer_true);
        else
            holder.txt_choice_a.setBackgroundResource(R.drawable.show_options);

        if (answer.equals(option_b))
            holder.txt_choice_b.setBackgroundResource(R.drawable.show_answer_true);
        else
            holder.txt_choice_b.setBackgroundResource(R.drawable.show_options);

        if (answer.equals(option_c))
            holder.txt_choice_c.setBackgroundResource(R.drawable.show_answer_true);
        else
            holder.txt_choice_c.setBackgroundResource(R.drawable.show_options);

        if (answer.equals(option_d))
            holder.txt_choice_d.setBackgroundResource(R.drawable.show_answer_true);
        else
            holder.txt_choice_d.setBackgroundResource(R.drawable.show_options);




        if (my_answer.equals(answer)) {
            holder.txt_my_answer.setBackgroundResource(R.drawable.show_answer_true);
        } else {
            holder.txt_my_answer.setBackgroundResource(R.drawable.show_answer_false);
        }
    }

    @Override
    public int getItemCount() {
        return getMyAnswersList.size();
    }

}
