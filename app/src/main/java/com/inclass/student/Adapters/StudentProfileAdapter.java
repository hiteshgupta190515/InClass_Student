package com.inclass.student.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inclass.student.R;

import java.util.ArrayList;

public class StudentProfileAdapter extends RecyclerView.Adapter<StudentProfileAdapter.MyViewHolder> {

    private Context context;
    private int[] othersHeaderArray;
    private ArrayList<String> othersValues;

    public StudentProfileAdapter(Context applicationContext, int[] studentHeaderArray, ArrayList<String> personalValues) {

        this.context = applicationContext;
        this.othersHeaderArray = studentHeaderArray;
        this.othersValues = personalValues;

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView headerTV, valueTV;

        public MyViewHolder(View view) {
            super(view);
            headerTV = (TextView) view.findViewById(R.id.adapter_student_profile_head);
            valueTV = (TextView) view.findViewById(R.id.adapter_student_profile_value);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.headerTV.setText(context.getString(othersHeaderArray[position]));
        holder.valueTV.setText(othersValues.get(position));
    }

    @Override
    public int getItemCount() {
        return othersValues.size();
    }
}
