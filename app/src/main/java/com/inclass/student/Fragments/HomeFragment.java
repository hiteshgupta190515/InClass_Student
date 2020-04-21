package com.inclass.student.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inclass.student.Activities.Attendance;
import com.inclass.student.Activities.EditProfile;
import com.inclass.student.Activities.Login;
import com.inclass.student.Activities.MainActivity;
import com.inclass.student.Activities.Subjects;
import com.inclass.student.Activities.Timetable;
import com.inclass.student.AppController;
import com.inclass.student.Helpers.CustomDialog;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.SharedHelper;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.fontawesome.FontDrawable;

public class HomeFragment extends Fragment {
    private static String TAG = "HomeFragment";
    CustomDialog progressDialog;
    Context context;
    Activity activity;
    View root;
    FloatingActionButton dash_homework, dash_subjects, dash_attendance,dash_timetable,dash_logout;
    CircularImageView profile_image;
    TextView home_student_name, home_class_section;
    SessionManagement sessionManagement;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(SharedHelper.getKey(activity, "school_name"));
        setHasOptionsMenu(true);
        sessionManagement = new SessionManagement(activity);
        initComponent();

        return root;
    }

    private void initComponent() {
        progressDialog = new CustomDialog(activity);

        dash_subjects = root.findViewById(R.id.dash_subjects);
        FontDrawable drawable = new FontDrawable(activity, R.string.fa_book_solid, true, false);
        drawable.setTextColor(getResources().getColor(R.color.grey_80));
        dash_subjects.setImageDrawable(drawable);
        dash_subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent subjects_intent = new Intent(getActivity(), Subjects.class);
                startActivity(subjects_intent);
            }
        });

        dash_homework = root.findViewById(R.id.dash_homework);
        FontDrawable dash_homework_icon = new FontDrawable(activity, R.string.fa_file_word_solid, true, false);
        drawable.setTextColor(getResources().getColor(R.color.grey_80));
        dash_homework.setImageDrawable(dash_homework_icon);
        dash_homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeWork homeWork = new HomeWork();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, homeWork);
                fragmentTransaction.addToBackStack(null).commit();
            }
        });

        dash_timetable = root.findViewById(R.id.dash_timetable);
        FontDrawable dash_timetable_icon = new FontDrawable(activity, R.string.fa_calendar_week_solid, true, false);
        dash_timetable_icon.setTextColor(getResources().getColor(R.color.grey_80));
        dash_timetable.setImageDrawable(dash_timetable_icon);
        dash_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startmain = new Intent(getActivity(), Timetable.class);
                startActivity(startmain);
            }
        });

        dash_attendance = root.findViewById(R.id.dash_attendance);
        FontDrawable dash_attendance_icon = new FontDrawable(activity, R.string.fa_calendar_alt, true, false);
        drawable.setTextColor(getResources().getColor(R.color.grey_80));
        dash_attendance.setImageDrawable(dash_attendance_icon);
        dash_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startmain = new Intent(getActivity(), Attendance.class);
                startActivity(startmain);
            }
        });
        dash_logout = root.findViewById(R.id.dash_logout);
        FontDrawable dash_logout_icon = new FontDrawable(activity, R.string.fa_power_off_solid, true, false);
        drawable.setTextColor(getResources().getColor(R.color.grey_80));
        dash_logout.setImageDrawable(dash_logout_icon);
        dash_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        profile_image = root.findViewById(R.id.home_profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startmain = new Intent(getActivity(), EditProfile.class);
                startmain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startmain);
            }
        });

        home_student_name = root.findViewById(R.id.home_student_name);
        home_class_section = root.findViewById(R.id.home_class_section);
        home_student_name.setText(sessionManagement.getUserDetails().get(URLHelper.USERFNAME) + " " + sessionManagement.getUserDetails().get(URLHelper.USERLNAME));
        home_class_section.setText("Class " + sessionManagement.getUserDetails().get(URLHelper.USERCLASS) + "/" + "Section " + sessionManagement.getUserDetails().get(URLHelper.USERSECTION));
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                }
                return false;
            }
        });
    }

    private void logout() {
        progressDialog.startLoadingDialog();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONObject object = new JSONObject();
                try {
                    object.put("student_id", sessionManagement.getUserDetails().get(URLHelper.USERID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URLHelper.logout, object, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        progressDialog.dismissDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            if (jsonObject.getString("success").equals("1")) {
                                Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                sessionManagement.logoutSession();
                                Intent goToLogin = new Intent(activity, Login.class);
                                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(goToLogin);
                                activity.finish();
                            }
                            if (jsonObject.getString("success").equals("0")) {
                                Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismissDialog();
                        Log.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                jsonObjReq.setShouldCache(false);
                AppController.getInstance().addToRequestQueue(jsonObjReq);
            }
        }, 2000);
    }

}