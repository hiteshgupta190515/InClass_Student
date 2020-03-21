package com.inclass.student.Helpers;

public class URLHelper {

    public static final String PREFS_NAME = "InclassAdminPref";
    public static final String PREFS_NAME2 = "InclassAdminPref2";
    public static final String IS_LOGIN = "isLogin";

    public static final String USERID = "id";
    public static final String USERFNAME = "fname";
    public static final String USERLNAME = "lname";

    public static final String USEREMAIL = "email";
    public static final String USERPHONE = "phone";
    public static final String USERCLASS = "class_name";
    public static final String USERSECTION = "section_name";
    public static final String ADMISSIONNO = "addmission_no";
    public static final String ADMISSIONDATE = "created_at";
    public static final String PARENTID = "parent_id";

    public static final String base = "http://inclass.markatouch.site/public/";
    public static final String getSchoolsetting = base + "api/student/schoolsetting";
    public static final String signin = base + "api/student/login";
    public static final String profile = base + "api/student/profile";
    public static final String logout = base + "api/student/logout";

}
