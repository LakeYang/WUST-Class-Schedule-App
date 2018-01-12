package com.hihuyang.kb.timetable;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.google.gson.Gson;

/**
 * Created by Huyang on 2018/1/7.
 */

public class AddClassButton extends android.support.v7.widget.AppCompatButton {
    public AddClassButton(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public AddClassButton(Context context){
        super(context);
    }
    void init(final int weekday, final int clockid, final int weekid){
        //setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_plus));
        setText("+");
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddCustomClassActivity.class);
                int[] timeInfos = {weekday,clockid,weekid};
                intent.putExtra("TIME_INFO", timeInfos);
                getContext().startActivity(intent);
            }
        });
    }

}
