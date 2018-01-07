package com.hihuyang.kb.timetable;

import android.content.Context;

/**
 * Created by Huyang on 2018/1/6.
 */

class TimetableTools {
    static int[] colorset = {
            0xa4c400,
            0x60a917,
            0x008a00,
            0x00aba9,
            0x1ba1e2,
            0x0050ef,
            0x6a00ff,
            0xaa00ff,
            0xf472d0,
            0xd80073,
            0xa20025,
            0xe51400,
            0xfa6800,
            0xf0a30a,
            0xe3c800,
            0x825a2c,
            0x6d8764,
            0x647687,
            0x76608a,
            0x87794e,
            0xdee6a4,
            0xdec59c,
            0x7b9ccd,
            0xbda4cd,
            0xeec5d5,
            0xb4bd83,
            0xb4b494,
            0xa4ac9c,
            0x000000
    };
    static String weekIntToText(int weeks, Context ctx){
        boolean[] week = new boolean[25];
        for(int i=0;i<25;i++){
            week[24-i] = weeks % 2 == 1;
            weeks = weeks/2;
        }
        int lastStart = 0;
        String result = "";
        for(int i=0;i<25;i++){
            if(week[i]){
                if(lastStart==0){
                    lastStart = i+1;
                }
            }else{
                if(lastStart!=0){
                    if(result!=""){
                        result += ",";
                    }
                    if(lastStart == i){
                        result += String.format(ctx.getResources().getString(R.string.week_placeholder),String.valueOf(lastStart));
                    }else{
                        String plus = String.valueOf(lastStart) + "-" + String.valueOf(i);
                        result += String.format(ctx.getResources().getString(R.string.week_placeholder),plus);
                    }
                    lastStart = 0;
                }
            }
        }
        if(lastStart!=0){
            if(result!=""){
                result += ",";
            }
            if(lastStart == 25){
                result += String.format(ctx.getResources().getString(R.string.week_placeholder),"25");
            }else{
                String plus = String.valueOf(lastStart) + "-25";
                result += String.format(ctx.getResources().getString(R.string.week_placeholder),plus);
            }
        }
        return result;
    }

}
