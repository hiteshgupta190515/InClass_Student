package com.inclass.student.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inclass.student.Adapters.StudentProfileAdapter;
import com.inclass.student.R;

import java.util.ArrayList;

public class StudentProfileFragment extends Fragment {

    RecyclerView listView;
    int[] studentHeaderArray;
    ArrayList <String> personalValues = new ArrayList<String>();
    StudentProfileAdapter adapter;

    public static StudentProfileFragment newInstance(int[] personalHeaderArray, ArrayList<String> personalValues) {

        StudentProfileFragment personalFragment = new StudentProfileFragment();
        Bundle args = new Bundle();
        args.putIntArray("heads", personalHeaderArray);
        args.putStringArrayList("values", personalValues);
        personalFragment.setArguments(args);

        return personalFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        studentHeaderArray = getArguments().getIntArray("heads");
        personalValues = getArguments().getStringArrayList("values");


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_student_profile, container, false);

        listView = (RecyclerView) root.findViewById(R.id.studentProfileFragment_listview);
        adapter = new StudentProfileAdapter(getActivity().getApplicationContext(), studentHeaderArray, personalValues);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(adapter);

        return root;
    }
}