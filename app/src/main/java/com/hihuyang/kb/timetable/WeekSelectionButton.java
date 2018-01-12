package com.hihuyang.kb.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;

/**
 * Created by Huyang on 2018/1/9.
 */

public class WeekSelectionButton extends android.support.v7.widget.AppCompatButton {
    private boolean isActive;
    public WeekSelectionButton(Context context) {
        super(context);
    }
    public WeekSelectionButton(Context context,int id) {
        super(context);
        isActive = false;
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.width = 0;
        param.columnSpec = GridLayout.spec((id-1)%5,1);
        param.rowSpec = GridLayout.spec((id-1)/5+1,1);
        param.setGravity(Gravity.FILL);
        setLayoutParams(param);
        setText(String.valueOf(id));
        setBackgroundColor(getResources().getColor(R.color.colorSelectNormal));
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isActive){
                    setBackgroundColor(getResources().getColor(R.color.colorSelectNormal));
                    isActive = false;
                }else{
                    setBackgroundColor(getResources().getColor(R.color.colorSelectActive));
                    isActive = true;
                }
            }
        });
    }

    public WeekSelectionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekSelectionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public boolean isActive(){
        return  isActive;
    }
    public void setActive(boolean flag){
        isActive = flag;
        if(isActive){
            setBackgroundColor(getResources().getColor(R.color.colorSelectActive));
        }else {
            setBackgroundColor(getResources().getColor(R.color.colorSelectNormal));
        }
    }
}
