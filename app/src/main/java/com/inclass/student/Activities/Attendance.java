package com.inclass.student.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.inclass.student.AppController;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Attendance extends AppCompatActivity {
    SessionManagement sessionManagement;
    ProgressDialog progressDialog;
    ArrayList<String> dates = new ArrayList<String>();
    Calendar calendar;
    SimpleDateFormat sdf;
    Date date;
    List<EventDay> events = new ArrayList<>();
    CalendarView calendarView;
    TextView remarks;
    LinearLayout remark_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        sessionManagement = new SessionManagement(Attendance.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Attendance");
        setSupportActionBar(toolbar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        remarks = (TextView) findViewById(R.id.remarks);
        remark_layout = (LinearLayout) findViewById(R.id.remark_layout);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Log.e("Event", String.valueOf(eventDay.isEnabled()));
                if(eventDay.isEnabled()) {
//                Log.e("Event",eventDay.getNote()+" <--");
                remark_layout.setVisibility(View.VISIBLE);
                if(eventDay.getNote()==(null))
                    remarks.setText("No remarks");
                else
                    remarks.setText(eventDay.getNote());
                }
            }
        });
        getAttendance();
    }

    private void getAttendance() {
        progressDialog = new ProgressDialog(Attendance.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        JSONObject object = new JSONObject();
        try {
            object.put("student_id", sessionManagement.getUserDetails().get(URLHelper.USERID));
            Log.d("attendanceparams===>", sessionManagement.getUserDetails().get(URLHelper.USERID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.attendance, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("AttendanceResponse===>", response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        for (int i = 0; i < jsonObject.getJSONArray("attendance").length(); i++) {
                            try {
                                JSONObject json_homework = jsonObject.getJSONArray("attendance").getJSONObject(i);
                                calendar = Calendar.getInstance();
                                sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String dateInString = "2020-04-15";
                                date = sdf.parse(json_homework.getString("attendance_date"));
                                calendar.setTime(date);
                                Log.e("Day===>", String.valueOf(date));
                                if(json_homework.getString("attendance").equals("P"))
                                    events.add(new EventDay(calendar, R.drawable.present_letter, json_homework.getString("remark")));
                                else if(json_homework.getString("attendance").equals("A"))
                                    events.add(new EventDay(calendar, R.drawable.absent_letter, json_homework.getString("remark")));
                                else if(json_homework.getString("attendance").equals("H"))
                                    events.add(new EventDay(calendar, R.drawable.half_letter, json_homework.getString("remark")));
                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        calendarView.setEvents(events);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("AttendanceResponse===>", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

}
