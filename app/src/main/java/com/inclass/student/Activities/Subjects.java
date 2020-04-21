package com.inclass.student.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.inclass.student.AppController;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Subjects extends AppCompatActivity {

    SessionManagement sessionManagement;
    RecyclerView subjectsList;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
        sessionManagement = new SessionManagement(Subjects.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Subjects");
        setSupportActionBar(toolbar);

        subjectsList = findViewById(R.id.subjectsList);

        getSubjects();
    }

    private void getSubjects() {
        progressDialog = new ProgressDialog(Subjects.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        JSONObject object = new JSONObject();
        try {
            object.put("class_id", sessionManagement.getUserDetails().get(URLHelper.USERCLASSID));
            object.put("section_id", sessionManagement.getUserDetails().get(URLHelper.USERSECTIONID));
            Log.d("Subjectsparams===>", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.subjects, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("SubjectsResponse===>", response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        SubjectsListAdapter mondayTimetableListAdapter = new SubjectsListAdapter(Subjects.this, jsonObject.optJSONArray("subjects"));
                        subjectsList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        subjectsList.setAdapter(mondayTimetableListAdapter);
                        mondayTimetableListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("SubjectsResponse===>", "Error: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private class SubjectsListAdapter extends RecyclerView.Adapter<SubjectsListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private JSONArray jsonArray;

        SubjectsListAdapter(Context context, JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public SubjectsListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_subjects, parent, false);

            return new SubjectsListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectsListAdapter.MyViewHolder holder, final int position) {
            holder.subject_name.setText(jsonArray.optJSONObject(position).optString("subjects_name"));
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView subject_name;

            MyViewHolder(View itemView) {
                super(itemView);
                subject_name = itemView.findViewById(R.id.subject_name);
            }
        }
    }
}
