package com.hihuyang.kb.timetable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;

public class ModifyClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_class);
        final GridLayout weekGrid = findViewById(R.id.week_gridlayout);
        final EditText courseName = findViewById(R.id.coursename);
        final EditText classPlace = findViewById(R.id.classplace);
        final EditText teacherName = findViewById(R.id.teachername);
        final EditText startClock = findViewById(R.id.startclock);
        final EditText endClock = findViewById(R.id.endclock);
        final Spinner weekdaySelect = findViewById(R.id.weekday_select);
        Button saveButton = findViewById(R.id.button_save);

        final WeekSelectionButton[] selectionButtonset = new WeekSelectionButton[25];

        for(int i=1;i<=25;i++){
            selectionButtonset[i-1] = new WeekSelectionButton(this,i);
            weekGrid.addView(selectionButtonset[i-1]);
        }
    }
}
