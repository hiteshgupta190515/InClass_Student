package com.inclass.student.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonMethod {

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo;
        try {
            netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public static void closeKeyboard(final Context context, final View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public final static boolean isValidTelephone(String target) {

        if (TextUtils.isEmpty(target)) {
            return false;
        } else if (target.length() < 10) {
            return false;
        } else
            return true;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isPasswdAlphaNumeric(String password) {
        boolean isValid = false;
        String expression = "^(?=.*\\d)(?=.*[a-zA-Z]).{8,17}$";
        String expression1 = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}";
        CharSequence inPasswd = password;

        Pattern pattern = Pattern.compile(expression1, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inPasswd);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;


    }


    public static boolean isPasswdValid(String password) {
        boolean isValid = false;
        String expression = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        String expression1 = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}";
        CharSequence inPasswd = password;

        Pattern pattern = Pattern.compile(expression1, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inPasswd);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;


    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValidPass(CharSequence target) {

        if (TextUtils.isEmpty(target)) {
            return false;
        } else if (target.length() < 6) {
            return false;
        } else {
            return true;
        }
    }


    public static final boolean isPasswdord8ChargsLong(String passwd) {
        if (TextUtils.isEmpty(passwd)) {
            return false;
        } else if (passwd.length() < 8) {
            return false;
        } else {
            return true;
        }
    }
}
