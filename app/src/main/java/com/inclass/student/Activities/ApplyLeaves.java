package com.inclass.student.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.inclass.student.AppController;
import com.inclass.student.Helpers.AppHelper;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.Helpers.VolleyMultipartRequest;
import com.inclass.student.R;
import com.inclass.student.utils.CommonMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class ApplyLeaves extends AppCompatActivity {

    SessionManagement sessionManagement;
    Spinner lt_type;
    List leavetypelist = new ArrayList();
    String leavetypeId;
    EditText fromtime,totime,leavedesc;
    TextView file;
    Button choose_file,apply_btn;

    private int RESULT_LOAD_IMAGE= 1;
    int CAMERA_WRITE_STORAGE_PERMISSION = 231;
    private int CAPTURE_IMAGE_REQUEST = 232;
    Bitmap reRotateImg = null;
    private ContentValues values;
    private Uri imageUri;
    public static int deviceHeight;
    public static int deviceWidth;

    private Calendar myCalendar;

    LinearLayout applyleave;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_leaves);

        sessionManagement = new SessionManagement(ApplyLeaves.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Apply Leaves");
        setSupportActionBar(toolbar);

        initComponent();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        getLeavesType();
    }

    private void initComponent() {
        myCalendar = Calendar.getInstance();
        lt_type = findViewById(R.id.lt_type);
        fromtime = findViewById(R.id.fromtime);
        totime = findViewById(R.id.totime);
        leavedesc = findViewById(R.id.leavedesc);
        file = findViewById(R.id.file);
        choose_file = findViewById(R.id.choose_file);
        apply_btn = findViewById(R.id.apply_btn);
        applyleave = findViewById(R.id.applyleave);

        file.setText("No File Attached");

        choose_file.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplyLeaves.this);
                builder.setTitle("Choose type");

                // add a list
                String[] animals = {"Open Camera", "Open Gallery"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // open camera
                                checkPermissions();
                                break;
                            case 1:
                                // open gallery
                                Intent i = new Intent(
                                        Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, RESULT_LOAD_IMAGE);

                                break;
                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFromDate();
            }

        };

        fromtime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ApplyLeaves.this, AlertDialog.THEME_HOLO_LIGHT, fromDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis()- 1000);
                datePickerDialog.show();
            }
        });

        final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateToDate();
            }

        };

        totime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ApplyLeaves.this, AlertDialog.THEME_HOLO_LIGHT, toDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis()- 1000);
                datePickerDialog.show();
            }
        });

        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSave_leave();
            }
        });
    }

    private void updateFromDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        fromtime.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateToDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        totime.setText(sdf.format(myCalendar.getTime()));
    }

    //    Picture 1
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (ApplyLeaves.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ApplyLeaves.this, Manifest.permission.CAMERA)) {

                // showing snackbar for permission
                showPermissionSnackBar();

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_WRITE_STORAGE_PERMISSION);
                }
            }

        } else {
            // start camera intent
            openCameraIntent();
        }
    }

    private void showPermissionSnackBar() {
        Snackbar.make(applyleave,
                getString(R.string.per_camera_and_write_storage),
                Snackbar.LENGTH_INDEFINITE
        ).setAction("ENABLE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_WRITE_STORAGE_PERMISSION);
                        }
                    }
                }
        ).show();
    }

    private void openCameraIntent() {

        values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Work image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Upload work image");
        imageUri = getApplicationContext().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {


        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            // image uri
            Bitmap resizeImg = null;
            try {
                resizeImg = getBitmapFromUri(this, imageUri);
                Log.e(TAG, String.valueOf(resizeImg));
                if (resizeImg != null) {
                    reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, imageUri));
                    file.setText("File Attached");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        if (requestCode == RESULT_LOAD_IMAGE &&
                resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap resizeImg = null;

            try {
                resizeImg = getBitmapFromUri(this, selectedImage);
                Log.e(TAG, String.valueOf(resizeImg));
                if (resizeImg != null) {
                    reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, selectedImage));
                    file.setText("File Attached");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_WRITE_STORAGE_PERMISSION) {

            boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (cameraPermission && readExternalFile) {
                // start camera intent
                openCameraIntent();
            } else {
                // showing snackbar for permission
                showPermissionSnackBar();
            }
        }

    }

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    private void updateDocumentWithBitMap(final Bitmap imageBitmap1) {

        final ProgressDialog customDialog = new ProgressDialog(ApplyLeaves.this);
        customDialog.setMessage("Loading...");
        customDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        customDialog.setCancelable(false);
        customDialog.show();


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.postLeaves , new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {


                customDialog.dismiss();

                String res = new String(response.data);
                Log.d("ProfileUpdateRes", "" + res);

                Log.d("Error = ", response.toString());

                //
                try {
                    JSONObject jsonObject = new JSONObject(res);

                    if (jsonObject.optString("error").equals("") || jsonObject.optString("error") == null) {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ApplyLeaves.this, Leaves.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Document upload error.", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage("Something Went Wrong");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("leave_type", leavetypeId);
                params.put("from_date", fromtime.getText().toString());
                params.put("to_date", totime.getText().toString());
                params.put("description", leavedesc.getText().toString());
                params.put("student_id", sessionManagement.getUserDetails().get(URLHelper.USERID));
                params.put("class_id", sessionManagement.getUserDetails().get(URLHelper.USERCLASSID));
                params.put("section_id", sessionManagement.getUserDetails().get(URLHelper.USERSECTIONID));
                Log.e("leaveparams===>",params.toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                params.put("content_file", new VolleyMultipartRequest.DataPart("documentImage" + "1" + ".png",
                        AppHelper.getFileDataFromBitmap(imageBitmap1), "image/png"));
                Log.e("Image===>",params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(volleyMultipartRequest);
    }

    private void getLeavesType() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.getLeaveType, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("AddLeaveResponse", response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        final JSONArray jsonArray = response.optJSONArray("leavetype");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getString("status").equals("1"))
                                leavetypelist.add(jsonObject.getString("name"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, leavetypelist);
                        lt_type.setAdapter(adapter);

                        lt_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                // Get select item
                                int sid = lt_type.getSelectedItemPosition();
                                for (int a = 0; a < jsonArray.length(); a++) {
                                    JSONObject jsonObject1 = null;
                                    try {
                                        jsonObject1 = jsonArray.getJSONObject(a);
                                        if (leavetypelist.get(sid).equals(jsonObject1.getString("name"))) {
                                            leavetypeId = jsonObject1.getString("id");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // TODO Auto-generated method stub
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("ApplyLeaveResponse===>", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void getSave_leave() {
        if (TextUtils.isEmpty(fromtime.getText().toString().trim())) {
            fromtime.requestFocus();
            Snackbar.make(applyleave, "Enter From Time", Snackbar.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(totime.getText().toString().trim())) {
            totime.requestFocus();
            Snackbar.make(applyleave, "Enter To Time", Snackbar.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(leavedesc.getText().toString().trim())) {
            leavedesc.requestFocus();
            Snackbar.make(applyleave, "Enter Description", Snackbar.LENGTH_LONG).show();
        }else if (CommonMethod.isOnline(getApplicationContext())) {
            progressDialog = new ProgressDialog(ApplyLeaves.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            progressDialog.setCancelable(false);
            if (reRotateImg == null)
                postAdd_Leavestype();
            else
                updateDocumentWithBitMap(reRotateImg);
        } else {
            Snackbar.make(applyleave, "No internet  connection avaliable", Snackbar.LENGTH_LONG).show();
        }
    }

    private void postAdd_Leavestype() {

        JSONObject object = new JSONObject();
        try {
            object.put("leave_type", leavetypeId);
            object.put("from_date", fromtime.getText().toString());
            object.put("to_date", totime.getText().toString());
            object.put("description", leavedesc.getText().toString());
            object.put("student_id", sessionManagement.getUserDetails().get(URLHelper.USERID));
            object.put("class_id", sessionManagement.getUserDetails().get(URLHelper.USERCLASSID));
            object.put("section_id", sessionManagement.getUserDetails().get(URLHelper.USERSECTIONID));
//            object.put("content_file", "");
            Log.e("leaveparams===>",object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URLHelper.postLeaves, object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("ApplyLeaveResponse===>", response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ApplyLeaves.this, Leaves.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("ApplyLeaveResponse===>", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void displayMessage(String toastString) {
        Log.d("displayMessage", "" + toastString);
        try {
            Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            try {
                Toast.makeText(getApplicationContext(), "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }
}
