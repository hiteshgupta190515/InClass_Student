package com.inclass.student.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.applandeo.materialcalendarview.EventDay;
import com.inclass.student.AppController;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.Models.AssignmentsModel;
import com.inclass.student.Models.MondayTimetable;
import com.inclass.student.Models.StudyMaterialModel;
import com.inclass.student.Models.SyllabusModel;
import com.inclass.student.Models.TuesdayTimetable;
import com.inclass.student.Models.WednesdayTimetable;
import com.inclass.student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DownloadCenter extends AppCompatActivity {

    SessionManagement sessionManagement;
    ProgressDialog progressDialog;
    RecyclerView assign_list,study_list,syllabus_list;
    ArrayList<AssignmentsModel> assignmentsModelArrayList = new ArrayList<AssignmentsModel>();
    ArrayList<StudyMaterialModel> studyMaterialModelArrayList = new ArrayList<StudyMaterialModel>();
    ArrayList<SyllabusModel> syllabusModelArrayList = new ArrayList<SyllabusModel>();
    TextView selectedoption;
    ImageView assign_option,study_option,syllabus_option;
    String FileName;
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_center);

        sessionManagement = new SessionManagement(DownloadCenter.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Download Center");
        setSupportActionBar(toolbar);

        assign_list = findViewById(R.id.assign_list);
        study_list = findViewById(R.id.study_list);
        syllabus_list = findViewById(R.id.syllabus_list);
        selectedoption = findViewById(R.id.selectedoption);
        assign_option = findViewById(R.id.assign_option);
        study_option = findViewById(R.id.study_option);
        syllabus_option = findViewById(R.id.syllabus_option);

        if (ContextCompat.checkSelfPermission(DownloadCenter.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestStoragePermission();
        }

        assign_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedoption.setText("Assignments");
                assign_list.setVisibility(View.VISIBLE);
                study_list.setVisibility(View.GONE);
                syllabus_list.setVisibility(View.GONE);
            }
        });

        study_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedoption.setText("Study Materials");
                assign_list.setVisibility(View.GONE);
                study_list.setVisibility(View.VISIBLE);
                syllabus_list.setVisibility(View.GONE);
            }
        });

        syllabus_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedoption.setText("Syllabus");
                assign_list.setVisibility(View.GONE);
                study_list.setVisibility(View.GONE);
                syllabus_list.setVisibility(View.VISIBLE);
            }
        });

        getdownloadabledata();
    }

    private void getdownloadabledata() {
        progressDialog = new ProgressDialog(DownloadCenter.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        JSONObject object = new JSONObject();
        try {
            object.put("class_id", sessionManagement.getUserDetails().get(URLHelper.USERCLASSID));
            object.put("section_id", sessionManagement.getUserDetails().get(URLHelper.USERSECTIONID));
            Log.d("DownloadCenterparams==>", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.downloadcenter, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("DownloadCenterResponse", response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        for (int i = 0; i < jsonObject.getJSONArray("downloadable").length(); i++) {
                            try {
                                JSONObject json_downloadable = jsonObject.getJSONArray("downloadable").getJSONObject(i);
                                if(json_downloadable.getString("content_type").equals("assignment")){
                                    AssignmentsModel assignmentsModel = new AssignmentsModel();
                                    assignmentsModel.setContent_title(json_downloadable.getString("content_title"));
                                    assignmentsModel.setContent_desription(json_downloadable.getString("content_title"));
                                    assignmentsModel.setContent_file(json_downloadable.getString("content_file"));
                                    assignmentsModel.setUpdatedate(json_downloadable.getString("up_date"));
                                    assignmentsModelArrayList.add(assignmentsModel);

                                    AssigmentListAdapter assigmentListAdapter = new AssigmentListAdapter(DownloadCenter.this, assignmentsModelArrayList);
                                    assign_list.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                                    assign_list.setAdapter(assigmentListAdapter);
                                    assigmentListAdapter.notifyDataSetChanged();

                                } else if(json_downloadable.getString("content_type").equals("study_material")){
                                    StudyMaterialModel studyMaterialModel = new StudyMaterialModel();
                                    studyMaterialModel.setContent_title(json_downloadable.getString("content_title"));
                                    studyMaterialModel.setContent_desription(json_downloadable.getString("content_title"));
                                    studyMaterialModel.setContent_file(json_downloadable.getString("content_file"));
                                    studyMaterialModel.setUpdatedate(json_downloadable.getString("up_date"));
                                    studyMaterialModelArrayList.add(studyMaterialModel);

                                    StudyMaterialListAdapter studyMaterialListAdapter = new StudyMaterialListAdapter(DownloadCenter.this, studyMaterialModelArrayList);
                                    study_list.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                                    study_list.setAdapter(studyMaterialListAdapter);
                                    studyMaterialListAdapter.notifyDataSetChanged();

                                } else if(json_downloadable.getString("content_type").equals("syllabus")){
                                    SyllabusModel syllabusModel = new SyllabusModel();
                                    syllabusModel.setContent_title(json_downloadable.getString("content_title"));
                                    syllabusModel.setContent_desription(json_downloadable.getString("content_title"));
                                    syllabusModel.setContent_file(json_downloadable.getString("content_file"));
                                    syllabusModel.setUpdatedate(json_downloadable.getString("up_date"));
                                    syllabusModelArrayList.add(syllabusModel);

                                    SyllabusListAdapter syllabusListAdapter = new SyllabusListAdapter(DownloadCenter.this, syllabusModelArrayList);
                                    syllabus_list.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                                    syllabus_list.setAdapter(syllabusListAdapter);
                                    syllabusListAdapter.notifyDataSetChanged();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("DownloadCenterResponse", "Error: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private class AssigmentListAdapter extends RecyclerView.Adapter<AssigmentListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<AssignmentsModel> assignmentsModels;

        AssigmentListAdapter(Context context, ArrayList<AssignmentsModel> assignmentsModels) {
            this.assignmentsModels = assignmentsModels;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public AssigmentListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_downloadable, parent, false);

            return new AssigmentListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull AssigmentListAdapter.MyViewHolder holder, final int position) {
            final AssignmentsModel p = assignmentsModels.get(position);
            holder.title.setText(p.getContent_title());
            holder.updated.setText(p.getUpdatedate());
            holder.description.setText(p.getContent_desription());

            holder.downloadBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FileName = p.getContent_file();
                    startDownloading(URLHelper.base+"public/public/image/"+p.getContent_file());
                }
            });
        }

        @Override
        public int getItemCount() {
            return assignmentsModels.size();

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView downloadBtn, description,updated,title;

            MyViewHolder(View itemView) {
                super(itemView);
                downloadBtn = itemView.findViewById(R.id.downloadBtn);
                description = itemView.findViewById(R.id.description);
                updated = itemView.findViewById(R.id.updated);
                title = itemView.findViewById(R.id.title);
            }
        }
    }

    private class StudyMaterialListAdapter extends RecyclerView.Adapter<StudyMaterialListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<StudyMaterialModel> studyMaterialModels;

        StudyMaterialListAdapter(Context context, ArrayList<StudyMaterialModel> studyMaterialModels) {
            this.studyMaterialModels = studyMaterialModels;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public StudyMaterialListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_downloadable, parent, false);

            return new StudyMaterialListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull StudyMaterialListAdapter.MyViewHolder holder, final int position) {
            final StudyMaterialModel p = studyMaterialModels.get(position);
            holder.title.setText(p.getContent_title());
            holder.updated.setText(p.getUpdatedate());
            holder.description.setText(p.getContent_desription());
            holder.downloadBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FileName = p.getContent_file();
                    startDownloading(URLHelper.base+"public/public/image/"+p.getContent_file());
                }
            });
        }

        @Override
        public int getItemCount() {
            return studyMaterialModels.size();

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView downloadBtn, description,updated,title;

            MyViewHolder(View itemView) {
                super(itemView);
                downloadBtn = itemView.findViewById(R.id.downloadBtn);
                description = itemView.findViewById(R.id.description);
                updated = itemView.findViewById(R.id.updated);
                title = itemView.findViewById(R.id.title);
            }
        }
    }

    private class SyllabusListAdapter extends RecyclerView.Adapter<SyllabusListAdapter.MyViewHolder> {

        Context ctx;
        LayoutInflater infalter;
        private ArrayList<SyllabusModel> syllabusModels;

        SyllabusListAdapter(Context context, ArrayList<SyllabusModel> studyMaterialModels) {
            this.syllabusModels = syllabusModels;
            this.ctx = context;
            this.infalter = LayoutInflater.from(getApplicationContext());
        }

        @NonNull
        @Override
        public SyllabusListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rootView = infalter.inflate(R.layout.item_downloadable, parent, false);

            return new SyllabusListAdapter.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull SyllabusListAdapter.MyViewHolder holder, final int position) {
            final SyllabusModel p = syllabusModels.get(position);
            holder.title.setText(p.getContent_title());
            holder.updated.setText(p.getUpdatedate());
            holder.description.setText(p.getContent_desription());
            holder.downloadBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FileName = p.getContent_file();
                    startDownloading(URLHelper.base+"public/public/image/"+p.getContent_file());
                }
            });
        }

        @Override
        public int getItemCount() {
            return syllabusModels.size();

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView downloadBtn, description,updated,title;

            MyViewHolder(View itemView) {
                super(itemView);
                downloadBtn = itemView.findViewById(R.id.downloadBtn);
                description = itemView.findViewById(R.id.description);
                updated = itemView.findViewById(R.id.updated);
                title = itemView.findViewById(R.id.title);
            }
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DownloadCenter.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startDownloading(String url){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(FileName);
        request.setDescription("Downloading File....");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, ""+ System.currentTimeMillis());

        DownloadManager manager = (DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(getApplicationContext(), "Downloading File....", Toast.LENGTH_LONG).show();
    }
}
