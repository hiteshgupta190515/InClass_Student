package com.inclass.student.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import static com.inclass.student.Helpers.URLHelper.ADMISSIONDATE;
import static com.inclass.student.Helpers.URLHelper.ADMISSIONNO;
import static com.inclass.student.Helpers.URLHelper.IS_LOGIN;
import static com.inclass.student.Helpers.URLHelper.PARENTID;
import static com.inclass.student.Helpers.URLHelper.PREFS_NAME;
import static com.inclass.student.Helpers.URLHelper.PREFS_NAME2;
import static com.inclass.student.Helpers.URLHelper.USERCLASS;
import static com.inclass.student.Helpers.URLHelper.USERCLASSID;
import static com.inclass.student.Helpers.URLHelper.USEREMAIL;
import static com.inclass.student.Helpers.URLHelper.USERFNAME;
import static com.inclass.student.Helpers.URLHelper.USERID;
import static com.inclass.student.Helpers.URLHelper.USERLNAME;
import static com.inclass.student.Helpers.URLHelper.USERPHONE;
import static com.inclass.student.Helpers.URLHelper.USERSECTION;
import static com.inclass.student.Helpers.URLHelper.USERSECTIONID;

/**
 * Created by Hitesh Gupta on 31/1/2020.
 */

public class SessionManagement {

    SharedPreferences prefs;
    SharedPreferences prefs2;

    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor2;

    Context context;

    int PRIVATE_MODE = 0;

    public SessionManagement(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = prefs.edit();

        prefs2 = context.getSharedPreferences(PREFS_NAME2, PRIVATE_MODE);
        editor2 = prefs2.edit();
    }

    public void createLoginSession(String user_id, String user_fname, String user_lname, String user_email,
                                   String user_phone, String user_classid, String user_class, String user_sectionid,
                                   String user_section, String user_admissionno,
                                   String user_admissiondate, String user_parentid) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(USERID, user_id);
        editor.putString(USERFNAME, user_fname);
        editor.putString(USERLNAME, user_lname);
        editor.putString(USEREMAIL, user_email);
        editor.putString(USERPHONE, user_phone);
        editor.putString(USERCLASSID, user_classid);
        editor.putString(USERCLASS, user_class);
        editor.putString(USERSECTIONID, user_sectionid);
        editor.putString(USERSECTION, user_section);
        editor.putString(ADMISSIONNO, user_admissionno);
        editor.putString(ADMISSIONDATE, user_admissiondate);
        editor.putString(PARENTID, user_parentid);

        editor.commit();
    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(USERID, prefs.getString(USERID, null));
        user.put(USERFNAME, prefs.getString(USERFNAME, null));
        user.put(USERLNAME, prefs.getString(USERLNAME, null));
        user.put(USEREMAIL, prefs.getString(USEREMAIL, null));
        user.put(USERPHONE, prefs.getString(USERPHONE, null));
        user.put(USERCLASSID, prefs.getString(USERCLASSID, null));
        user.put(USERCLASS, prefs.getString(USERCLASS, null));
        user.put(USERSECTIONID, prefs.getString(USERSECTIONID, null));
        user.put(USERSECTION, prefs.getString(USERSECTION, null));
        user.put(ADMISSIONNO, prefs.getString(ADMISSIONNO, null));
        user.put(ADMISSIONDATE, prefs.getString(ADMISSIONDATE, null));
        user.put(PARENTID, prefs.getString(PARENTID, null));

        // return user
        return user;
    }

    public void updateData(String user_id, String user_fname, String user_lname, String user_email,
                           String user_phone, String user_class, String user_section, String user_admissionno,
                           String user_admissiondate, String user_parentid) {

        editor.putString(USERID, user_id);
        editor.apply();
    }


    public void logoutSession() {
        editor.clear();
        editor.commit();


//        Intent logout = new Intent(context, Login.class);
//        // Closing all the Activities
//        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Add new Flag to start new Activity
//        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        context.startActivity(logout);
    }

    public void logoutSessionwithchangepassword() {
        editor.clear();
        editor.commit();

//        Intent logout = new Intent(context, Login.class);
//        // Closing all the Activities
//        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Add new Flag to start new Activity
//        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        context.startActivity(logout);
    }


    // Get Login State
    public boolean isLoggedIn() {
        return prefs.getBoolean(IS_LOGIN, false);
    }


}
