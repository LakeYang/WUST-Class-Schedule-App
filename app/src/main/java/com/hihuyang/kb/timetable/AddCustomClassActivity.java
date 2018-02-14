package com.hihuyang.kb.timetable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;

import com.google.gson.Gson;

public class AddCustomClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_class);
        final Context ctx = this;
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

        Intent intent = getIntent();
        int[] timeInfos = intent.getIntArrayExtra("TIME_INFO");
        if(timeInfos != null){
            weekdaySelect.setSelection(timeInfos[0]-1);
            startClock.setText(String.valueOf(timeInfos[1]));
            selectionButtonset[timeInfos[2]-1].setActive(true);
        }



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(courseName.getText().toString().matches("")){
                    Snackbar.make(courseName, getResources().getString(R.string.please_input_coursename),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                int start;
                int end;
                try{
                    start = Integer.valueOf(startClock.getText().toString());
                    end = Integer.valueOf(endClock.getText().toString());
                }catch (Exception e){
                    Snackbar.make(courseName, getResources().getString(R.string.please_input_clock),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(start>end){
                    Snackbar.make(courseName, getResources().getString(R.string.startclock_greater_than_end),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(start==0){
                    Snackbar.make(courseName, getResources().getString(R.string.startclock_cannot_zero),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(end>12){
                    Snackbar.make(courseName, getResources().getString(R.string.endclock_exceed),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(end-start>8){
                    Snackbar.make(courseName, getResources().getString(R.string.class_too_long),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                int weekid = 0;
                for(int i=0;i<25;i++){
                    if(selectionButtonset[i].isActive()){
                        weekid += Math.pow(2,24-i);
                    }
                }
                if(weekid==0){
                    Snackbar.make(courseName, getResources().getString(R.string.at_least_one_week_selected),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                CourseManager cm = new CourseManager(ctx);
                CourseClass cc = new CourseClass();
                cc.intype = 2;
                cc.place = classPlace.getText().toString();
                cc.week = weekid;
                cc.teacher = teacherName.getText().toString();
                cc.clock = start*10 + end - start + 1;
                cc.weekday = weekdaySelect.getSelectedItemPosition()+1;
                cc.name = courseName.getText().toString();
                cm.addCourse(cc);
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage(getResources().getString(R.string.add_class_manual_success))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
