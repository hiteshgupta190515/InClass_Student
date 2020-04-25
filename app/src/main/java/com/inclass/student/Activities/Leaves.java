package com.inclass.student.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inclass.student.AppController;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.SharedHelper;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Leaves extends AppCompatActivity {

    SessionManagement sessionManagement;
    RecyclerView leavesList;
    FloatingActionButton addleaves;
    ProgressDialog progressDialog;
    List leavelist = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaves);

        sessionManagement = new SessionManagement(Leaves.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.menu_leaves);
        setSupportActionBar(toolbar);

        leavesList = findViewById(R.id.leavesList);
        addleaves = findViewById(R.id.addleaves);
        addleaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startmain = new Intent(Leaves.this, ApplyLeaves.class);
                startActivity(startmain);
            }
        });

        getLeaves();
    }

    private void getLeaves() {
        progressDialog = new ProgressDialog(Leaves.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        JSONObject object = new JSONObject();
        try {
            object.put("student_id", sessionManagement.getUserDetails().get(URLHelper.USERID));
            Log.e("leaveparams===>",object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.getLeaves, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("LeaveResponse===>", response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        LeavesListAdapter feesTypeListAdapter = new LeavesListAdapter(response.optJSONArray("leaves"));
                        leavesList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        leavesList.setAdapter(feesTypeListAdapter);
                        feesTypeListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("LeaveResponse===>", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private class LeavesListAdapter extends RecyclerView.Adapter<LeavesListAdapter.MyViewHolder> {

        JSONArray jsonArray;
        LayoutInflater infalter;

        LeavesListAdapter(JSONArray array) {
            this.jsonArray = array;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public LeavesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_leaves_list, parent, false);

            return new LeavesListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull LeavesListAdapter.MyViewHolder holder, final int position) {
            holder.apply_date_list.setText("Applied Date - " + jsonArray.optJSONObject(position).optString("created_at").substring(0,10));
            holder.from_date_list.setText(jsonArray.optJSONObject(position).optString("from_date"));
            holder.to_date_list.setText(jsonArray.optJSONObject(position).optString("to_date"));
            if(jsonArray.optJSONObject(position).optString("status").equals("Pending")) {
                holder.leave_status_list.setText(jsonArray.optJSONObject(position).optString("status"));
                holder.leave_status_list.setBackgroundResource(R.drawable.yellow_border);
            } else if(jsonArray.optJSONObject(position).optString("status").equals("Accepted")) {
                holder.leave_status_list.setText(jsonArray.optJSONObject(position).optString("status"));
                holder.leave_status_list.setBackgroundResource(R.drawable.green_border);
            } else if(jsonArray.optJSONObject(position).optString("status").equals("Cancelled")) {
                holder.leave_status_list.setText(jsonArray.optJSONObject(position).optString("status"));
                holder.leave_status_list.setBackgroundResource(R.drawable.red_border);
            }
            holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteLeaves(jsonArray.optJSONObject(position).optString("id"));
                }
            });
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();

        }
        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView apply_date_list,from_date_list,to_date_list,leave_status_list;
            LinearLayout detailsBtn;

            MyViewHolder(View itemView) {
                super(itemView);
                apply_date_list = itemView.findViewById(R.id.apply_date_list);
                from_date_list = itemView.findViewById(R.id.from_date_list);
                to_date_list = itemView.findViewById(R.id.to_date_list);
                leave_status_list = itemView.findViewById(R.id.leave_status_list);
                detailsBtn = itemView.findViewById(R.id.detailsBtn);
            }
        }
    }

    private void deleteLeaves(String leave_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("leaves_id", leave_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URLHelper.deleteLeaves, object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("DeleteLeaves===>", response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        Toast.makeText(Leaves.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        getLeaves();
                    } else {
                        Toast.makeText(Leaves.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("DeleteLeaves===>", "Error: " + error.getMessage());
            }
        });

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
