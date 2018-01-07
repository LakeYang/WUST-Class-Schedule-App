package com.hihuyang.kb.timetable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private TimeTableFragment fTimeTable;
    private ToolsFragment fTools;
    private SettingsFragment fSettings;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);
            switch (item.getItemId()) {
                case R.id.navigation_tools:
                    transaction.replace(R.id.FragmentLayout, fTools);
                    transaction.commit();
                    //TODO: Remove this before release
                    DatabaseUtil.copyDatabaseToExtStg(MainActivity.this);
                    return true;
                case R.id.navigation_timetable:
                    transaction.replace(R.id.FragmentLayout, fTimeTable);
                    transaction.commit();
                    return true;
                case R.id.navigation_settings:
                    transaction.replace(R.id.FragmentLayout, fSettings);
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_timetable);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        fTimeTable = TimeTableFragment.newInstance();
        fTools = ToolsFragment.newInstance();
        fSettings = new SettingsFragment();
        transaction.replace(R.id.FragmentLayout, fTimeTable);
        transaction.commit();
    }
}
