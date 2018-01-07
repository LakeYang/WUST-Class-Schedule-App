package com.hihuyang.kb.timetable;

import android.content.ContentValues;

/**
 * Created by Huyang on 2017/12/21.
 */

public class CourseClass {
    public String name;
    public String teacher;
    public String place;
    public int week;
    public int clock;
    public int weekday;
    public int intype;
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("teacher", teacher);
        values.put("week", week);
        values.put("weekday", weekday);
        values.put("clock", clock);
        values.put("place", place);
        values.put("intype",intype);
        return values;
    }
    public void setValues(String gname,String gteacher,String gplace,int gweek,int gweekday,int gclock,int gintype){
        name = gname;
        teacher = gteacher;
        place = gplace;
        week = gweek;
        weekday = gweekday;
        clock = gclock;
        intype = gintype;
    }
}

