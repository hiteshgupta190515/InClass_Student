package com.inclass.student.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.inclass.student.AppController;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.SharedHelper;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private static String TAG = "Login";
    ProgressDialog progressDialog;
    SessionManagement sessionManagement;
    AutoCompleteTextView username;
    EditText password;
    Button email_sign_in_button;
    String device_token, device_UDID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();
        GetToken();
        sessionManagement = new SessionManagement(Login.this);
    }

    public void initComponent() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email_sign_in_button = findViewById(R.id.email_sign_in_button);

        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
                    Toast.makeText(Login.this, "Please enter the above fields", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog = new ProgressDialog(Login.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    signin();
                }
            }
        });
    }

    private void signin() {
        JSONObject object = new JSONObject();
        try {
            object.put("username", username.getText());
            object.put("password", password.getText());
            object.put("device_token", device_token);
            object.put("device_udid", device_UDID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URLHelper.signin, object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        Toast.makeText(Login.this, "Your are successfully logged in", Toast.LENGTH_SHORT).show();
                        String user_id = jsonObject.optJSONObject("user").getString("id");
                        String user_fname = jsonObject.optJSONObject("user").getString("fname");
                        String user_lname = jsonObject.optJSONObject("user").getString("lname");
                        String user_email = jsonObject.optJSONObject("user").getString("email");
                        String user_phone = jsonObject.optJSONObject("user").getString("phone");
                        String user_classid = jsonObject.optJSONObject("user").getString("class_id");
                        String user_class = jsonObject.optJSONObject("user").getString("class_name");
                        String user_sectionid = jsonObject.optJSONObject("user").getString("section_id");
                        String user_section = jsonObject.optJSONObject("user").getString("section_name");
                        String user_admissionno = jsonObject.optJSONObject("user").getString("addmission_no");
                        String user_admissiondate = jsonObject.optJSONObject("user").getString("created_at");
                        String user_parentid = jsonObject.optJSONObject("user").getString("parent_id");

                        sessionManagement.createLoginSession(user_id, user_fname, user_lname, user_email,
                                user_phone, user_classid,user_class, user_sectionid,user_section, user_admissionno, user_admissiondate,
                                user_parentid);
                        startActivity(new Intent(Login.this, MainActivity.class));
                    } else {
                        Toast.makeText(Login.this, "Please provide a valid credentials", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(getApplicationContext(), "device_token").equals("") && SharedHelper.getKey(getApplicationContext(), "device_token") != null) {
                device_token = SharedHelper.getKey(getApplicationContext(), "device_token");
                Log.e(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(getApplicationContext(), "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                Log.e(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.e(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.e(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.e(TAG, "Failed to complete device UDID");
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
