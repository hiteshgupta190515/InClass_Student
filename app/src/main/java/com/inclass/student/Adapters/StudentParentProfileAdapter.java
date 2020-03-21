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

public class StudentParentProfileAdapter extends RecyclerView.Adapter<StudentParentProfileAdapter.MyViewHolder> {

    private Context context;
    private int[] othersHeaderArray;
    private ArrayList<String> othersValues;

    public StudentParentProfileAdapter(Context applicationContext, int[] parentHeaderArray, ArrayList<String> parentValues) {

        this.context = applicationContext;
        this.othersHeaderArray = parentHeaderArray;
        this.othersValues = parentValues;

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
