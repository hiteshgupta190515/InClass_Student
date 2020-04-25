package com.inclass.student.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class Notice extends AppCompatActivity {

    SessionManagement sessionManagement;
    RecyclerView noticeList;
    ProgressDialog progressDialog;
    String noticeof;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        sessionManagement = new SessionManagement(Notice.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Notice");
        setSupportActionBar(toolbar);

        noticeList = findViewById(R.id.noticeList);
        noticeof = "students - "+sessionManagement.getUserDetails().get(URLHelper.USERCLASS)+" "+sessionManagement.getUserDetails().get(URLHelper.USERSECTION);
        getNotice(noticeof);
    }

    private void getNotice(String noticeparams) {
        progressDialog = new ProgressDialog(Notice.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        JSONObject object = new JSONObject();
        try {
            object.put("notice_off", noticeparams);
            Log.d("Noticeparams===>", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.notice, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("NoticeResponse===>", response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        SubjectsListAdapter mondayTimetableListAdapter = new SubjectsListAdapter(Notice.this, jsonObject.optJSONArray("notice"));
                        noticeList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        noticeList.setAdapter(mondayTimetableListAdapter);
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
                Log.d("NoticeResponse===>", "Error: " + error.getMessage());
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
            View rootView = infalter.inflate(R.layout.item_notice_list, parent, false);

            return new SubjectsListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectsListAdapter.MyViewHolder holder, final int position) {
            holder.title.setText(jsonArray.optJSONObject(position).optString("title"));
            holder.fornotice.setText(jsonArray.optJSONObject(position).optString("send_to").toUpperCase());
            holder.noticedate.setText(jsonArray.optJSONObject(position).optString("notice_date"));
            holder.noticedesc.setText(Html.fromHtml(Html.fromHtml(jsonArray.optJSONObject(position).optString("description")).toString()));
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView title,fornotice,noticedate,noticedesc;

            MyViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                fornotice = itemView.findViewById(R.id.fornotice);
                noticedate = itemView.findViewById(R.id.noticedate);
                noticedesc = itemView.findViewById(R.id.noticedesc);
            }
        }
    }
}
