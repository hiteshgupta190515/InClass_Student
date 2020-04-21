package com.inclass.student.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.applandeo.materialcalendarview.EventDay;
import com.inclass.student.AppController;
import com.inclass.student.Fragments.HomeWork;
import com.inclass.student.Fragments.HomeworkDetail;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.Models.FridayTimetable;
import com.inclass.student.Models.MondayTimetable;
import com.inclass.student.Models.PendingHomework;
import com.inclass.student.Models.SaturdayTimetable;
import com.inclass.student.Models.ThursdayTimetable;
import com.inclass.student.Models.TuesdayTimetable;
import com.inclass.student.Models.WednesdayTimetable;
import com.inclass.student.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Timetable extends AppCompatActivity {
    SessionManagement sessionManagement;
    ProgressDialog progressDialog;
    RecyclerView classTimetable_mondayList,classTimetable_tuesdayList,classTimetable_wednesdayList,classTimetable_thursdayList,
            classTimetable_fridayList,classTimetable_saturdayList;
    ArrayList<MondayTimetable> mondayArrayList = new ArrayList<MondayTimetable>();
    ArrayList<TuesdayTimetable> tuesdayTimetableArrayList = new ArrayList<TuesdayTimetable>();
    ArrayList<WednesdayTimetable> wednesdayTimetableArrayList = new ArrayList<WednesdayTimetable>();
    ArrayList<ThursdayTimetable> thursdayTimetableArrayList = new ArrayList<ThursdayTimetable>();
    ArrayList<FridayTimetable> fridayTimetableArrayList = new ArrayList<FridayTimetable>();
    ArrayList<SaturdayTimetable> saturdayTimetableArrayList = new ArrayList<SaturdayTimetable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        sessionManagement = new SessionManagement(Timetable.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Class Timetable");
        setSupportActionBar(toolbar);
        initComponent();
        getTimetable();
    }

    private void initComponent(){
        classTimetable_mondayList = findViewById(R.id.classTimetable_mondayList);
        classTimetable_tuesdayList = findViewById(R.id.classTimetable_tuesdayList);
        classTimetable_wednesdayList = findViewById(R.id.classTimetable_wednesdayList);
        classTimetable_thursdayList = findViewById(R.id.classTimetable_thursdayList);
        classTimetable_fridayList = findViewById(R.id.classTimetable_fridayList);
        classTimetable_saturdayList = findViewById(R.id.classTimetable_saturdayList);
    }

    private void getTimetable() {
        progressDialog = new ProgressDialog(Timetable.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        JSONObject object = new JSONObject();
        try {
            object.put("class_id", sessionManagement.getUserDetails().get(URLHelper.USERCLASSID));
            object.put("section_id", sessionManagement.getUserDetails().get(URLHelper.USERSECTIONID));
            Log.d("timetableparams===>",object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.timetable, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TimetableResponse===>", response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        for (int i = 0; i < jsonObject.getJSONArray("classtimetable").length(); i++) {
                            MondayTimetable mondayTimetable = new MondayTimetable();
                            TuesdayTimetable tuesdayTimetable = new TuesdayTimetable();
                            WednesdayTimetable wednesdayTimetable = new WednesdayTimetable();
                            ThursdayTimetable thursdayTimetable = new ThursdayTimetable();
                            FridayTimetable fridayTimetable = new FridayTimetable();
                            SaturdayTimetable saturdayTimetable = new SaturdayTimetable();

                            JSONObject json_classtimetable = jsonObject.getJSONArray("classtimetable").getJSONObject(i);

                            if(json_classtimetable.getString("day").equals("monday")) {
                                mondayTimetable.setTimefrom(json_classtimetable.getString("time_from"));
                                mondayTimetable.setTimeto(json_classtimetable.getString("time_to"));
                                mondayTimetable.setSubject(json_classtimetable.getString("subjects_name"));
                                mondayTimetable.setRoom(json_classtimetable.getString("room_no"));
                                mondayArrayList.add(mondayTimetable);
                            }
                            if(json_classtimetable.getString("day").equals("tuesday")) {
                                tuesdayTimetable.setTimefrom(json_classtimetable.getString("time_from"));
                                tuesdayTimetable.setTimeto(json_classtimetable.getString("time_to"));
                                tuesdayTimetable.setSubject(json_classtimetable.getString("subjects_name"));
                                tuesdayTimetable.setRoom(json_classtimetable.getString("room_no"));
                                tuesdayTimetableArrayList.add(tuesdayTimetable);
                            }
                            if(json_classtimetable.getString("day").equals("wednesday")) {
                                wednesdayTimetable.setTimefrom(json_classtimetable.getString("time_from"));
                                wednesdayTimetable.setTimeto(json_classtimetable.getString("time_to"));
                                wednesdayTimetable.setSubject(json_classtimetable.getString("subjects_name"));
                                wednesdayTimetable.setRoom(json_classtimetable.getString("room_no"));
                                wednesdayTimetableArrayList.add(wednesdayTimetable);
                            }
                            if(json_classtimetable.getString("day").equals("thursday")) {
                                thursdayTimetable.setTimefrom(json_classtimetable.getString("time_from"));
                                thursdayTimetable.setTimeto(json_classtimetable.getString("time_to"));
                                thursdayTimetable.setSubject(json_classtimetable.getString("subjects_name"));
                                thursdayTimetable.setRoom(json_classtimetable.getString("room_no"));
                                thursdayTimetableArrayList.add(thursdayTimetable);
                            }
                            if(json_classtimetable.getString("day").equals("friday")) {
                                fridayTimetable.setTimefrom(json_classtimetable.getString("time_from"));
                                fridayTimetable.setTimeto(json_classtimetable.getString("time_to"));
                                fridayTimetable.setSubject(json_classtimetable.getString("subjects_name"));
                                fridayTimetable.setRoom(json_classtimetable.getString("room_no"));
                                fridayTimetableArrayList.add(fridayTimetable);
                            }
                            if(json_classtimetable.getString("day").equals("saturday")) {
                                saturdayTimetable.setTimefrom(json_classtimetable.getString("time_from"));
                                saturdayTimetable.setTimeto(json_classtimetable.getString("time_to"));
                                saturdayTimetable.setSubject(json_classtimetable.getString("subjects_name"));
                                saturdayTimetable.setRoom(json_classtimetable.getString("room_no"));
                                saturdayTimetableArrayList.add(saturdayTimetable);
                            }
                        }

                        MondayTimetableListAdapter mondayTimetableListAdapter = new MondayTimetableListAdapter(Timetable.this, mondayArrayList);
                        classTimetable_mondayList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        classTimetable_mondayList.setAdapter(mondayTimetableListAdapter);
                        mondayTimetableListAdapter.notifyDataSetChanged();

                        TuesdayTimetableListAdapter tuesdayTimetableListAdapter = new TuesdayTimetableListAdapter(Timetable.this, tuesdayTimetableArrayList);
                        classTimetable_tuesdayList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        classTimetable_tuesdayList.setAdapter(tuesdayTimetableListAdapter);
                        tuesdayTimetableListAdapter.notifyDataSetChanged();

                        WednesdayTimetableListAdapter wednesdayTimetableListAdapter = new WednesdayTimetableListAdapter(Timetable.this, wednesdayTimetableArrayList);
                        classTimetable_wednesdayList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        classTimetable_wednesdayList.setAdapter(wednesdayTimetableListAdapter);
                        wednesdayTimetableListAdapter.notifyDataSetChanged();

                        ThursdayTimetableListAdapter thursdayTimetableListAdapter = new ThursdayTimetableListAdapter(Timetable.this, thursdayTimetableArrayList);
                        classTimetable_thursdayList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        classTimetable_thursdayList.setAdapter(thursdayTimetableListAdapter);
                        thursdayTimetableListAdapter.notifyDataSetChanged();

                        FridayTimetableListAdapter fridayTimetableListAdapter = new FridayTimetableListAdapter(Timetable.this, fridayTimetableArrayList);
                        classTimetable_fridayList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        classTimetable_fridayList.setAdapter(fridayTimetableListAdapter);
                        fridayTimetableListAdapter.notifyDataSetChanged();

                        SaturdayTimetableListAdapter saturdayTimetableListAdapter = new SaturdayTimetableListAdapter(Timetable.this, saturdayTimetableArrayList);
                        classTimetable_saturdayList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        classTimetable_saturdayList.setAdapter(saturdayTimetableListAdapter);
                        saturdayTimetableListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("TimetableResponse===>", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private class MondayTimetableListAdapter extends RecyclerView.Adapter<MondayTimetableListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<MondayTimetable> mondayTimetables;

        MondayTimetableListAdapter(Context context, ArrayList<MondayTimetable> mondayTimetables) {
            this.mondayTimetables = mondayTimetables;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public MondayTimetableListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_classtimetable, parent, false);

            return new MondayTimetableListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull MondayTimetableListAdapter.MyViewHolder holder, final int position) {
            final MondayTimetable p = mondayTimetables.get(position);
                holder.time.setText(p.getTimefrom() + " to " + p.getTimeto());
                holder.subject.setText(p.getSubject());
                holder.roomno.setText(p.getRoom());

        }

        @Override
        public int getItemCount() {
            return mondayTimetables.size();

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView time, subject,roomno;

            MyViewHolder(View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.time);
                subject = itemView.findViewById(R.id.subject);
                roomno = itemView.findViewById(R.id.roomno);
            }
        }
    }

    private class TuesdayTimetableListAdapter extends RecyclerView.Adapter<TuesdayTimetableListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<TuesdayTimetable> tuesdayTimetables;

        TuesdayTimetableListAdapter(Context context, ArrayList<TuesdayTimetable> tuesdayTimetables) {
            this.tuesdayTimetables = tuesdayTimetables;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public TuesdayTimetableListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_classtimetable, parent, false);

            return new TuesdayTimetableListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull TuesdayTimetableListAdapter.MyViewHolder holder, final int position) {
            final TuesdayTimetable p = tuesdayTimetables.get(position);
            holder.time.setText(p.getTimefrom() + " to " + p.getTimeto());
            holder.subject.setText(p.getSubject());
            holder.roomno.setText(p.getRoom());

        }

        @Override
        public int getItemCount() {
            return tuesdayTimetables.size();

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView time, subject,roomno;

            MyViewHolder(View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.time);
                subject = itemView.findViewById(R.id.subject);
                roomno = itemView.findViewById(R.id.roomno);
            }
        }
    }

    private class WednesdayTimetableListAdapter extends RecyclerView.Adapter<WednesdayTimetableListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<WednesdayTimetable> wednesdayTimetables;

        WednesdayTimetableListAdapter(Context context, ArrayList<WednesdayTimetable> wednesdayTimetables) {
            this.wednesdayTimetables = wednesdayTimetables;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public WednesdayTimetableListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_classtimetable, parent, false);

            return new WednesdayTimetableListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull WednesdayTimetableListAdapter.MyViewHolder holder, final int position) {
            final WednesdayTimetable p = wednesdayTimetables.get(position);
            holder.time.setText(p.getTimefrom() + " to " + p.getTimeto());
            holder.subject.setText(p.getSubject());
            holder.roomno.setText(p.getRoom());

        }

        @Override
        public int getItemCount() {
            return wednesdayTimetables.size();

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView time, subject,roomno;

            MyViewHolder(View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.time);
                subject = itemView.findViewById(R.id.subject);
                roomno = itemView.findViewById(R.id.roomno);
            }
        }
    }

    private class ThursdayTimetableListAdapter extends RecyclerView.Adapter<ThursdayTimetableListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<ThursdayTimetable> thursdayTimetables;

        ThursdayTimetableListAdapter(Context context, ArrayList<ThursdayTimetable> thursdayTimetables) {
            this.thursdayTimetables = thursdayTimetables;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public ThursdayTimetableListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_classtimetable, parent, false);

            return new ThursdayTimetableListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull ThursdayTimetableListAdapter.MyViewHolder holder, final int position) {
            final ThursdayTimetable p = thursdayTimetables.get(position);
            holder.time.setText(p.getTimefrom() + " to " + p.getTimeto());
            holder.subject.setText(p.getSubject());
            holder.roomno.setText(p.getRoom());

        }

        @Override
        public int getItemCount() {
            return thursdayTimetables.size();

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView time, subject,roomno;

            MyViewHolder(View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.time);
                subject = itemView.findViewById(R.id.subject);
                roomno = itemView.findViewById(R.id.roomno);
            }
        }
    }

    private class FridayTimetableListAdapter extends RecyclerView.Adapter<FridayTimetableListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<FridayTimetable> fridayTimetables;

        FridayTimetableListAdapter(Context context, ArrayList<FridayTimetable> fridayTimetables) {
            this.fridayTimetables = fridayTimetables;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public FridayTimetableListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_classtimetable, parent, false);

            return new FridayTimetableListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull FridayTimetableListAdapter.MyViewHolder holder, final int position) {
            final FridayTimetable p = fridayTimetables.get(position);
            holder.time.setText(p.getTimefrom() + " to " + p.getTimeto());
            holder.subject.setText(p.getSubject());
            holder.roomno.setText(p.getRoom());

        }

        @Override
        public int getItemCount() {
            return fridayTimetables.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView time, subject,roomno;

            MyViewHolder(View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.time);
                subject = itemView.findViewById(R.id.subject);
                roomno = itemView.findViewById(R.id.roomno);
            }
        }
    }

    private class SaturdayTimetableListAdapter extends RecyclerView.Adapter<SaturdayTimetableListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<SaturdayTimetable> saturdayTimetables;

        SaturdayTimetableListAdapter(Context context, ArrayList<SaturdayTimetable> saturdayTimetables) {
            this.saturdayTimetables = saturdayTimetables;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public SaturdayTimetableListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_classtimetable, parent, false);

            return new SaturdayTimetableListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull SaturdayTimetableListAdapter.MyViewHolder holder, final int position) {
            final SaturdayTimetable p = saturdayTimetables.get(position);
            holder.time.setText(p.getTimefrom() + " to " + p.getTimeto());
            holder.subject.setText(p.getSubject());
            holder.roomno.setText(p.getRoom());

        }

        @Override
        public int getItemCount() {
            return saturdayTimetables.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView time, subject,roomno;

            MyViewHolder(View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.time);
                subject = itemView.findViewById(R.id.subject);
                roomno = itemView.findViewById(R.id.roomno);
            }
        }
    }
}
