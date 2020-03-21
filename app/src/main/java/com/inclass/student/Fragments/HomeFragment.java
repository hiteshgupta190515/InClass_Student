package com.inclass.student.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inclass.student.Activities.EditProfile;
import com.inclass.student.Activities.Login;
import com.inclass.student.Activities.MainActivity;
import com.inclass.student.Activities.Splash;
import com.inclass.student.Helpers.SessionManagement;
import com.inclass.student.Helpers.URLHelper;
import com.inclass.student.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import info.androidhive.fontawesome.FontDrawable;

public class HomeFragment extends Fragment {
    View root;
    FloatingActionButton fab_dashboard, fab_messages;
    CircularImageView profile_image;
    TextView home_student_name,home_class_section;
    SessionManagement sessionManagement;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Home");
        setHasOptionsMenu(true);
        sessionManagement = new SessionManagement(getActivity());
        initComponent();

        return root;
    }

    private void initComponent() {
        fab_messages = root.findViewById(R.id.fab_messages);
        FontDrawable drawable = new FontDrawable(getActivity(), R.string.fa_paper_plane_solid, true, false);
        drawable.setTextColor(getResources().getColor(R.color.grey_80));
        fab_messages.setImageDrawable(drawable);
        profile_image = root.findViewById(R.id.home_profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startmain = new Intent(getActivity(), EditProfile.class);
                startmain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startmain);
            }
        });

        home_student_name = root.findViewById(R.id.home_student_name);
        home_class_section = root.findViewById(R.id.home_class_section);
        home_student_name.setText(sessionManagement.getUserDetails().get(URLHelper.USERFNAME)+" "+sessionManagement.getUserDetails().get(URLHelper.USERLNAME));
        home_class_section.setText(sessionManagement.getUserDetails().get(URLHelper.USERCLASS) + " " + sessionManagement.getUserDetails().get(URLHelper.USERSECTION));
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == android.view.KeyEvent.KEYCODE_BACK )
                {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                }
                return false;
            }
        } );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(getActivity(), "fragment : action home has clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}