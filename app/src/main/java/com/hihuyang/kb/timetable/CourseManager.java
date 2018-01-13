package com.hihuyang.kb.timetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created by Huyang on 2017/12/20.
 */


public class CourseManager {
    private DbHelper Helper;
    private SQLiteDatabase db;
    private int[] colorset;

    CourseManager(Context context){
        Helper = new DbHelper(context);
        db = Helper.getWritableDatabase();
        colorset = TimetableTools.colorset;
    }
    CourseClass getCourseByTime(int week, int day, int time){
        CourseClass cc = new CourseClass();
        boolean[] weekmap = new boolean[25];
        Cursor cursor = db.query(
                "course",
                null,
                null,
                null,
                null,
                null,
                null
        );
        while(cursor.moveToNext()) {
            int rweek = cursor.getInt(cursor.getColumnIndexOrThrow("week"));
            for(int i=0;i<25;i++){
                weekmap[24 - i] = rweek % 2 != 0;
                rweek = rweek/2;
            }
            if(weekmap[week-1]){
                if(cursor.getInt(cursor.getColumnIndexOrThrow("weekday")) == day){
                    int temp = cursor.getInt(cursor.getColumnIndexOrThrow("clock"));
                    temp = temp/10;
                    if(temp == time){
                        cc.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        cc.teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                        cc.place = cursor.getString(cursor.getColumnIndexOrThrow("place"));
                        cc.week = cursor.getInt(cursor.getColumnIndexOrThrow("week"));
                        cc.weekday = cursor.getInt(cursor.getColumnIndexOrThrow("weekday"));
                        cc.clock = cursor.getInt(cursor.getColumnIndexOrThrow("clock"));
                        cc.intype = cursor.getInt(cursor.getColumnIndexOrThrow("intype"));
                        cursor.close();
                        return cc;
                    }
                }
            }
        }
        cursor.close();
        return null;
    }
    CourseClass getCourseByTimeInAllWeeks(int weekday, int startclock){
        CourseClass cc = new CourseClass();
        String selection = "weekday = ?";
        String[] selectionArgs = {String.valueOf(weekday)};
        String sortOrder = "week DESC";
        Cursor cursor = db.query(
                "course",
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        while(cursor.moveToNext()) {
            int clock = cursor.getInt(cursor.getColumnIndexOrThrow("clock"));
            if(clock/10 == startclock){
                cc.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                cc.teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                cc.place = cursor.getString(cursor.getColumnIndexOrThrow("place"));
                cc.week = cursor.getInt(cursor.getColumnIndexOrThrow("week"));
                cc.weekday = cursor.getInt(cursor.getColumnIndexOrThrow("weekday"));
                cc.clock = cursor.getInt(cursor.getColumnIndexOrThrow("clock"));
                cc.intype = cursor.getInt(cursor.getColumnIndexOrThrow("intype"));
                cursor.close();
                return cc;
            }
        }
        cursor.close();
        return null;
    }
    long addCourse(CourseClass cc){
        ContentValues values = cc.getContentValues();
        long newRowId = db.insert("course", null, values);
        return newRowId;
    }
    int getNumOfClassesInWeek(int weekID){
        CourseClass cc = new CourseClass();
        boolean[] weekmap = new boolean[25];
        String[] projection = {"week"};
        int num = 0;
        Cursor cursor = db.query(
                "course",
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while(cursor.moveToNext()) {
            int rweek = cursor.getInt(cursor.getColumnIndexOrThrow("week"));
            for(int i=0;i<25;i++){
                weekmap[24 - i] = rweek % 2 != 0;
                rweek = rweek/2;
            }
            if(weekmap[weekID-1]){
                num++;
            }
        }
        cursor.close();
        return num;
    }
    public int getNumOfAllClasses(){
        return (int)DatabaseUtils.queryNumEntries(db, "course");
    }
    void truncateDatabase(){
        db.execSQL("DROP TABLE course;");
        db.execSQL("DROP TABLE color;");
        db.execSQL("CREATE TABLE IF NOT EXISTS course(id Integer PRIMARY KEY AUTOINCREMENT,name Text,teacher Text,week Integer,weekday Integer,clock Integer,place Text,intype Integer);");
        db.execSQL("CREATE TABLE IF NOT EXISTS color(id Integer PRIMARY KEY AUTOINCREMENT,name Text,colorid Integer);");
    }
    int getColorByName(String name){
        String selection = "name = ?";
        String[] selectionArgs = {name};
        Cursor c = db.query(
                "color",
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if( c != null && c.moveToFirst() ){
            int color = c.getInt(c.getColumnIndexOrThrow("colorid"));
            c.close();
            return colorset[color]+0xff000000;
        }else{
            c.close();
            return allocateNewColor(name);
        }
    }
    void setClassColor(String name,int colorID){
        ContentValues values = new ContentValues();
        values.put("colorid", colorID);
        String selection = "name = ?";
        String[] selectionArgs = { name };
        db.update(
                "color",
                values,
                selection,
                selectionArgs);
    }
    private int allocateNewColor(String name){
        int cnt  = (int)DatabaseUtils.queryNumEntries(db, "color");
        int colorthis = cnt % colorset.length;
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("colorid", colorthis);
        db.insert("color", null, values);
        return colorset[colorthis]+0xff000000;
    }
}

class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "timetable.db";

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS course(id Integer PRIMARY KEY AUTOINCREMENT,name Text,teacher Text,week Integer,weekday Integer,clock Integer,place Text,intype Integer);");
        db.execSQL("CREATE TABLE IF NOT EXISTS color(id Integer PRIMARY KEY AUTOINCREMENT,name Text,colorid Integer);");

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}