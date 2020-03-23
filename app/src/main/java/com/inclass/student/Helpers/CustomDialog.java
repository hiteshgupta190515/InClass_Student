package com.inclass.student.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.inclass.student.R;


public class CustomDialog  {

    Activity activity;
    AlertDialog dialog;
    public CustomDialog(Activity myActivty){
        activity = myActivty;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = 600;
        lp.height = 600;
        dialog.getWindow().setAttributes(lp);
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
