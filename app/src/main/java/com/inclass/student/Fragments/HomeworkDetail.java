package com.inclass.student.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.inclass.student.Activities.MainActivity;
import com.inclass.student.Helpers.CustomDialog;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeworkDetail extends Fragment {
    private static String TAG = "HomeworkDetail";
    CustomDialog progressDialog;
    Activity activity;
    View root;
    SessionManagement sessionManagement;
    String hw_completed,hw_subjectname,hw_description,hw_submissiondate;
    TextView hm_name,hm_submission,hm_status,hm_desctiption;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public HomeworkDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_homework_detail, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_homework);
        setHasOptionsMenu(true);
        sessionManagement = new SessionManagement(activity);
        progressDialog = new CustomDialog(activity);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            hw_completed = bundle.getString("hw_completed");
            hw_subjectname = bundle.getString("hw_subjectname");
            hw_description = bundle.getString("hw_description");
            hw_submissiondate = bundle.getString("hw_submissiondate");
        }
        initComponent();
        return root;
    }

    private void initComponent() {
        hm_name = root.findViewById(R.id.hm_name);
        hm_submission = root.findViewById(R.id.hm_submission);
        hm_status = root.findViewById(R.id.hm_status);
        hm_desctiption = root.findViewById(R.id.hm_desctiption);

        hm_name.setText(hw_subjectname);
        hm_submission.setText(hw_submissiondate);
        if(hw_completed.equals("0"))
            hm_status.setText(R.string.pending);
        else if(hw_completed.equals("1"))
            hm_status.setText(R.string.completed);
        hm_desctiption.setText(hw_description);
    }

    private void displayMessage(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_layout, (ViewGroup) root.findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
