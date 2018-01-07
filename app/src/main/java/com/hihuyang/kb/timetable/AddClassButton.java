package com.hihuyang.kb.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

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
    void init(int weekid,int clockid){
        //setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_plus));
        setText("+");
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
