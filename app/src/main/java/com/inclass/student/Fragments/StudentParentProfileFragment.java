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

import com.inclass.student.Adapters.StudentParentProfileAdapter;
import com.inclass.student.R;

import java.util.ArrayList;

public class StudentParentProfileFragment extends Fragment {

    RecyclerView listView;
    int[] parentHeaderArray;
    ArrayList<String> parentValues = new ArrayList<String>();
    StudentParentProfileAdapter adapter;

    public static StudentParentProfileFragment newInstance(int[] parentHeaderArray, ArrayList<String> parentValues) {

        StudentParentProfileFragment parentFragment = new StudentParentProfileFragment();
        Bundle args = new Bundle();
        args.putIntArray("heads", parentHeaderArray);
        args.putStringArrayList("values", parentValues);
        parentFragment.setArguments(args);

        return parentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentHeaderArray = getArguments().getIntArray("heads");
        parentValues = getArguments().getStringArrayList("values");

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_student_parent_profile, container, false);
        listView = (RecyclerView) root.findViewById(R.id.studentParentProfileFragment_listview);
        adapter = new StudentParentProfileAdapter(getActivity().getApplicationContext(), parentHeaderArray, parentValues);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(adapter);
        return root;
    }
}