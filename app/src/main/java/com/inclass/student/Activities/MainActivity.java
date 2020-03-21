package com.inclass.student.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.inclass.student.AppController;
import com.inclass.student.Fragments.HomeFragment;
import com.inclass.student.Helpers.SharedHelper;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.R;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Login";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initComponent();
    }

    public void initComponent(){
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, homeFragment);
                fragmentTransaction.commitAllowingStateLoss();

                getSchoolSetting();
    }


    private void getSchoolSetting() {

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        JSONObject object = new JSONObject();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, URLHelper.getSchoolsetting, object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {
                        SharedHelper.putKey(getApplicationContext(),"school_name",jsonObject.optJSONObject("setting").getString("name"));
                        SharedHelper.putKey(getApplicationContext(),"school_address",jsonObject.optJSONObject("setting").getString("address"));
                        SharedHelper.putKey(getApplicationContext(),"school_phone",jsonObject.optJSONObject("setting").getString("phone"));
                        SharedHelper.putKey(getApplicationContext(),"school_email",jsonObject.optJSONObject("setting").getString("email"));
                        SharedHelper.putKey(getApplicationContext(),"school_fees_due_days",jsonObject.optJSONObject("setting").getString("fees_due_days"));
                        SharedHelper.putKey(getApplicationContext(),"school_description",jsonObject.optJSONObject("setting").getString("description"));
                        SharedHelper.putKey(getApplicationContext(),"school_session_name",jsonObject.optJSONObject("setting").getString("session_name"));

                    }
                    if (jsonObject.getString("success").equals("0")) {
                        Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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


}
