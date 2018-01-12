package com.hihuyang.kb.timetable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ClassInfoActivity extends AppCompatActivity {
    private CourseClass course;
    private CourseManager cm;
    private FloatingActionButton changeColorBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView class_teacher = findViewById(R.id.class_teacher);
        TextView class_name = findViewById(R.id.class_name);
        TextView class_weeks = findViewById(R.id.class_weeks);
        TextView class_place = findViewById(R.id.class_place);
        changeColorBtn = findViewById(R.id.color_setting);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);

        Intent intent = getIntent();
        String json = intent.getStringExtra("CLASS_OBJECT");
        Gson gson = new Gson();
        course = gson.fromJson(json, CourseClass.class);

        cm = new CourseManager(this);
        int color = cm.getColorByName(course.name);
        changeColorBtn.setBackgroundTintList(ColorStateList.valueOf(color));
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.class_details));

        toolbarLayout.setTitle(course.name);
        class_teacher.setText(course.teacher);
        class_name.setText(course.name);
        class_place.setText(course.place);
        class_weeks.setText(TimetableTools.weekIntToText(course.week,this));
        changeColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectColorDialog();
            }
        });
    }
    private void showSelectColorDialog() {
        // Prepare grid view
        GridView gridView = new GridView(this);
        List<Integer> mList = new ArrayList<Integer>();
        for (int i = 0; i < TimetableTools.colorset.length; i++) {
            mList.add(i);
        }

        gridView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, mList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setBackgroundColor(TimetableTools.colorset[position]+0xff000000);
                return view;
            }
        });
        gridView.setNumColumns(5);
        // Set grid view to alertDialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);
        builder.setTitle(getResources().getString(R.string.pick_class_background_color));
        final AlertDialog ColorDialog = builder.show();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(findViewById(R.id.toolbar), getResources().getString(R.string.background_color_has_changed), Snackbar.LENGTH_SHORT).show();
                cm.setClassColor(course.name,position);
                changeColorBtn.setBackgroundTintList(ColorStateList.valueOf(TimetableTools.colorset[position]+0xff000000));
                ColorDialog.dismiss();
            }
        });
    }
}
