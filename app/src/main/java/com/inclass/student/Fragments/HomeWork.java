package com.inclass.student.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.inclass.student.Activities.EditProfile;
import com.inclass.student.Activities.MainActivity;
import com.inclass.student.AppController;
import com.inclass.student.Helpers.CustomDialog;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.SharedHelper;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.Models.CompletedHomework;
import com.inclass.student.Models.PendingHomework;
import com.inclass.student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeWork#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeWork extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String TAG = "HomeFragment";
    CustomDialog progressDialog;
    Activity activity;
    View root;
    SessionManagement sessionManagement;
    LinearLayout lyt_expand_pending, lyt_expand_completed;
    RecyclerView pendingList, completedList;
    ImageButton bt_toggle_pending, bt_toggle_completed;
    ArrayList<PendingHomework> pendingHomeworkArrayList;
    ArrayList<CompletedHomework> completedHomeworkArrayList;

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
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeWork() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeWork.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeWork newInstance(String param1, String param2) {
        HomeWork fragment = new HomeWork();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home_work, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_homework);
        setHasOptionsMenu(true);
        sessionManagement = new SessionManagement(activity);
        initComponent();

        return root;
    }

    private void initComponent() {
        progressDialog = new CustomDialog(activity);
        pendingHomeworkArrayList = new ArrayList<PendingHomework>();
        completedHomeworkArrayList = new ArrayList<CompletedHomework>();

        lyt_expand_pending = root.findViewById(R.id.lyt_expand_pending);
        lyt_expand_completed = root.findViewById(R.id.lyt_expand_completed);
        pendingList = root.findViewById(R.id.pendingList);
        completedList = root.findViewById(R.id.completedList);
        bt_toggle_pending = root.findViewById(R.id.bt_toggle_pending);
        bt_toggle_pending.setImageResource(R.drawable.ic_arrow_drop_up);
        bt_toggle_completed = root.findViewById(R.id.bt_toggle_completed);

        bt_toggle_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyt_expand_completed.setVisibility(View.GONE);
                lyt_expand_pending.setVisibility(View.VISIBLE);
                bt_toggle_pending.setImageResource(R.drawable.ic_arrow_drop_up);
                bt_toggle_completed.setImageResource(R.drawable.ic_arrow_drop_down);
            }
        });

        bt_toggle_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyt_expand_pending.setVisibility(View.GONE);
                lyt_expand_completed.setVisibility(View.VISIBLE);
                bt_toggle_completed.setImageResource(R.drawable.ic_arrow_drop_up);
                bt_toggle_pending.setImageResource(R.drawable.ic_arrow_drop_down);
            }
        });

        progressDialog.startLoadingDialog();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getHomework();
            }
        }, 2000);
    }

    private void getHomework() {

        JSONObject object = new JSONObject();
        try {
            object.put("student_id", sessionManagement.getUserDetails().get(URLHelper.USERID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.homework, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("SubjectResponse", response.toString());
                progressDialog.dismissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        pendingHomeworkArrayList.clear();
                        for (int i = 0; i < jsonObject.getJSONArray("homework_evaluation").length(); i++) {
                            try {
                                JSONObject json_homework = jsonObject.getJSONArray("homework_evaluation").getJSONObject(i);
                                if(json_homework.getString("completed").equals("0")) {
                                    PendingHomework pendingHomework = new PendingHomework();
                                    pendingHomework.setId(json_homework.getString("id"));
                                    pendingHomework.setHomework_id(json_homework.getString("homework_id"));
                                    pendingHomework.setCompleted(json_homework.getString("completed"));
                                    pendingHomework.setHomework_date(json_homework.getString("homework_date"));
                                    pendingHomework.setSubmission_date(json_homework.getString("submission_date"));
                                    pendingHomework.setDescription(json_homework.getString("description"));
                                    pendingHomework.setSubject_name(json_homework.getString("subject_name"));
                                    pendingHomework.setSubjectgrp_name(json_homework.getString("subjectgrp_name"));
                                    pendingHomeworkArrayList.add(pendingHomework);
                                } else if(json_homework.getString("completed").equals("1")) {
                                    CompletedHomework completedHomework = new CompletedHomework();
                                    completedHomework.setId(json_homework.getString("id"));
                                    completedHomework.setHomework_id(json_homework.getString("homework_id"));
                                    completedHomework.setCompleted(json_homework.getString("completed"));
                                    completedHomework.setHomework_date(json_homework.getString("homework_date"));
                                    completedHomework.setSubmission_date(json_homework.getString("submission_date"));
                                    completedHomework.setDescription(json_homework.getString("description"));
                                    completedHomework.setSubject_name(json_homework.getString("subject_name"));
                                    completedHomework.setSubjectgrp_name(json_homework.getString("subjectgrp_name"));
                                    completedHomeworkArrayList.add(completedHomework);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        PendingListAdapter pendingListAdapter = new PendingListAdapter(getActivity(),pendingHomeworkArrayList);
                        pendingList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                        pendingList.setAdapter(pendingListAdapter);
                        pendingListAdapter.notifyDataSetChanged();

                        CompletedListAdapter completedListAdapter = new CompletedListAdapter(getActivity(),completedHomeworkArrayList);
                        completedList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                        completedList.setAdapter(completedListAdapter);
                        completedListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismissDialog();
                Log.d("HomeworkResponse", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<PendingHomework> pendingHomeworks;

        PendingListAdapter(Context context, ArrayList<PendingHomework> pendingHomeworks) {
            this.pendingHomeworks = pendingHomeworks;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getActivity());
        }

        @NonNull
        @Override
        public PendingListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_homework_list, parent, false);

            return new PendingListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull PendingListAdapter.MyViewHolder holder, final int position) {
            final PendingHomework p = pendingHomeworks.get(position);
            holder.hm_date.setText(p.getHomework_date());
            holder.hm_subdate.setText(p.getSubmission_date());

            holder.pendinghmlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayMessage("Success");
                }
            });

        }

        @Override
        public int getItemCount() {
            return pendingHomeworks.size();

        }
        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView hm_date,hm_subdate;
            CardView pendinghmlayout;

            MyViewHolder(View itemView) {
                super(itemView);
                hm_date = itemView.findViewById(R.id.hm_date);
                hm_subdate = itemView.findViewById(R.id.hm_subdate);
                pendinghmlayout = itemView.findViewById(R.id.pendinghmlayout);
            }
        }
    }

    private class CompletedListAdapter extends RecyclerView.Adapter<CompletedListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<CompletedHomework> completedHomeworks;

        CompletedListAdapter(Context context, ArrayList<CompletedHomework> completedHomeworks) {
            this.completedHomeworks = completedHomeworks;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getActivity());
        }

        @NonNull
        @Override
        public CompletedListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_homework_list, parent, false);

            return new CompletedListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull CompletedListAdapter.MyViewHolder holder, final int position) {
            final CompletedHomework p = completedHomeworks.get(position);
            holder.hm_date.setText(p.getHomework_date());
            holder.hm_subdate.setText(p.getSubmission_date());
            holder.pendinghmlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayMessage("Success");
                }
            });
        }

        @Override
        public int getItemCount() {
            return completedHomeworks.size();

        }
        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView hm_date,hm_subdate;
            CardView pendinghmlayout;

            MyViewHolder(View itemView) {
                super(itemView);
                hm_date = itemView.findViewById(R.id.hm_date);
                hm_subdate = itemView.findViewById(R.id.hm_subdate);
                pendinghmlayout = itemView.findViewById(R.id.pendinghmlayout);
            }
        }
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
