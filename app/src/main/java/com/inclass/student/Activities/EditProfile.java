package com.inclass.student.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.tabs.TabLayout;
import com.inclass.student.AppController;
import com.inclass.student.Fragments.StudentParentProfileFragment;
import com.inclass.student.Fragments.StudentProfileFragment;
import com.inclass.student.Helpers.CustomDialog;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.SharedHelper;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditProfile extends AppCompatActivity {

    private static String TAG = "EditProfile";
    CustomDialog progressDialog;
    private ViewPager view_pager;
    private TabLayout tab_layout;
    int[] studentHeaderArray = {R.string.profile_admissionno, R.string.profile_admissiondate, R.string.profile_dob,
            R.string.profile_phone, R.string.profile_email, R.string.profile_presentadd, R.string.profile_premadd,
            R.string.profile_bloodgroup, R.string.profile_religion, R.string.profile_caste,
            R.string.profile_height, R.string.profile_weight, R.string.profile_gender};
    int[] parentHeaderArray = {R.string.profile_father, R.string.profile_mother, R.string.profile_primaryph,
            R.string.profile_secondaryph, R.string.profile_fatheroccup, R.string.profile_motheroccup,
            R.string.profile_address};
    SessionManagement sessionManagement;
    ArrayList<String> personalValues = new ArrayList<String>();
    ArrayList<String> parentValues = new ArrayList<String>();
    TextView student_name, class_section;
    CircularImageView student_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        //Session Initaited
        sessionManagement = new SessionManagement(EditProfile.this);
        progressDialog = new CustomDialog(EditProfile.this);
        progressDialog.startLoadingDialog();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataFromApi();
            }
        }, 2000);


        view_pager = (ViewPager) findViewById(R.id.view_pager);


        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);
        student_name = findViewById(R.id.student_name);
        class_section = findViewById(R.id.class_section);
        student_image = findViewById(R.id.student_image);

        student_name.setText(sessionManagement.getUserDetails().get(URLHelper.USERFNAME) + " " + sessionManagement.getUserDetails().get(URLHelper.USERLNAME));
        class_section.setText(sessionManagement.getUserDetails().get(URLHelper.USERCLASS) + " " + sessionManagement.getUserDetails().get(URLHelper.USERSECTION));
        Picasso.get()
                .load(URLHelper.base + "public/image/" + SharedHelper.getKey(getApplicationContext(), "student_img"))
                .placeholder(R.drawable.photo_male_8)
                .error(R.drawable.photo_male_8)
                .into(student_image);
    }

    private void getDataFromApi() {
        JSONObject object = new JSONObject();
        try {
            object.put("student_id", sessionManagement.getUserDetails().get(URLHelper.USERID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URLHelper.profile, object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressDialog.dismissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.getString("success").equals("1")) {

                        personalValues.add(jsonObject.optJSONObject("student").getString("addmission_no"));
                        personalValues.add(jsonObject.optJSONObject("student").getString("created_at"));
                        personalValues.add(jsonObject.optJSONObject("student").getString("dob"));
                        personalValues.add(jsonObject.optJSONObject("student").getString("phone"));
                        personalValues.add(jsonObject.optJSONObject("student").getString("email"));
                        personalValues.add(jsonObject.optJSONObject("student").getString("present_address"));
                        personalValues.add(jsonObject.optJSONObject("student").getString("per_address"));
                        personalValues.add(jsonObject.optJSONObject("student").getString("blood_group"));
                        personalValues.add(jsonObject.optJSONObject("student").getString("religion"));
                        personalValues.add(jsonObject.optJSONObject("student").getString("caste"));
                        personalValues.add(jsonObject.optJSONObject("student").getString("height") + " " + "cm");
                        personalValues.add(jsonObject.optJSONObject("student").getString("weight") + " " + "kg");
                        personalValues.add(jsonObject.optJSONObject("student").getString("gender"));

                        parentValues.add(jsonObject.optJSONObject("parent").getString("father_name"));
                        parentValues.add(jsonObject.optJSONObject("parent").getString("mother_name"));
                        parentValues.add(jsonObject.optJSONObject("parent").getString("father_phone"));
                        parentValues.add(jsonObject.optJSONObject("parent").getString("mother_phone"));
                        parentValues.add(jsonObject.optJSONObject("parent").getString("father_occ"));
                        parentValues.add(jsonObject.optJSONObject("parent").getString("mother_occ"));
                        parentValues.add(jsonObject.optJSONObject("parent").getString("address"));
                        setupViewPager(view_pager);
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Log.e("arrays", personalValues.toString());
        adapter.addFragment(new StudentProfileFragment().newInstance(studentHeaderArray, personalValues), getResources().getString(R.string.login_student));
        adapter.addFragment(new StudentParentProfileFragment().newInstance(parentHeaderArray, parentValues), getResources().getString(R.string.login_parent));
        viewPager.setAdapter(adapter);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        Intent startmain = new Intent(EditProfile.this, MainActivity.class);
        startmain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startmain);
    }
}
