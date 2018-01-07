package com.hihuyang.kb.timetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

/**
 * Created by Huyang on 2017/12/27.
 */

public class ClassButton extends android.support.v7.widget.AppCompatButton {
    private CourseClass course;
    private Context ctx;

    public ClassButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        // TODO Auto-generated constructor stub
    }
    public ClassButton(Context context) {
        super(context);
        ctx = context;
        // TODO Auto-generated constructor stub
    }
    public void setCourse(CourseClass cc){
        course = cc;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, ClassInfoActivity.class);
                Gson gson = new Gson();
                String json = gson.toJson(course);
                intent.putExtra("CLASS_OBJECT", json);
                ctx.startActivity(intent);
            }
        });
        setText(cc.name + "@" + cc.place);
    }
}