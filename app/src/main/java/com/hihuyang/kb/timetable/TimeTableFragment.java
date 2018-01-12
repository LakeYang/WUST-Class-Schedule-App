package com.hihuyang.kb.timetable;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class TimeTableFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private int currentWeek;
    private int nowWeek;
    private CourseManager courseManager;
    private Fragment thisFrag = this;
    private Calendar firstCd;

    private Button weekNoBtn;
    private Button prevBtn;
    private Button nextBtn;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private FloatingActionButton backToNowBtn;
    private FloatingActionButton setToCurrentWeekBtn;
    private ScrollView tableScrollView;
    private CoordinatorLayout outLayout;
    private GridLayout gridLayout;
    private TextView monthText;
    private TextView mondayText;
    private TextView tuesdayText;
    private TextView wednesdayText;
    private TextView thursdayText;
    private TextView fridayText;
    private TextView saturdayText;
    private TextView sundayText;
    private boolean ifAnyAddButtonShown;

    private float[] lastTouchDownXY = new float[2];
    public TimeTableFragment() {
        // Required empty public constructor
    }

    public static TimeTableFragment newInstance() {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Calculate current week
        Calendar nowCd = Calendar.getInstance();
        nowCd.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        nowCd.set(Calendar.HOUR_OF_DAY, 0);
        nowCd.set(Calendar.MINUTE, 0);
        nowCd.set(Calendar.SECOND, 0);
        nowCd.set(Calendar.MILLISECOND, 0);
        //minus 14 to prevent date display error
        nowCd.add(Calendar.DAY_OF_YEAR,-14);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        int first_week_year = sharedPref.getInt("first_week_year", 0);
        if(first_week_year == 0){
            editor.putInt("first_week_year",nowCd.get(Calendar.YEAR));
            first_week_year = nowCd.get(Calendar.YEAR);
        }
        int first_week_month = sharedPref.getInt("first_week_month", 0);
        if(first_week_month == 0){
            editor.putInt("first_week_month",nowCd.get(Calendar.MONTH));
            first_week_month = nowCd.get(Calendar.MONTH);
        }
        int first_week_day = sharedPref.getInt("first_week_day", 0);
        if(first_week_day == 0){
            editor.putInt("first_week_day",nowCd.get(Calendar.DAY_OF_MONTH));
            first_week_day = nowCd.get(Calendar.DAY_OF_MONTH);
        }
        editor.apply();
        firstCd = Calendar.getInstance();
        firstCd.set(Calendar.YEAR, first_week_year);
        firstCd.set(Calendar.MONTH, first_week_month);
        firstCd.set(Calendar.DAY_OF_MONTH, first_week_day);
        firstCd.set(Calendar.HOUR_OF_DAY, 0);
        firstCd.set(Calendar.MINUTE, 0);
        firstCd.set(Calendar.SECOND, 0);
        firstCd.set(Calendar.MILLISECOND, 0);
        Calendar preCd = Calendar.getInstance();
        currentWeek = (int)Math.abs(TimeUnit.MILLISECONDS.toDays(Math.abs(preCd.getTimeInMillis() - firstCd.getTimeInMillis())))/7 + 1;
        if(currentWeek>25){
            currentWeek = 25;
        }
        nowWeek = currentWeek;
        //Initiate CourseManager2
        courseManager = new CourseManager(getActivity());
        ifAnyAddButtonShown = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        onWeekUpdate();
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        /*ClassButton Btn = (ClassButton)getView().findViewById(R.id.but);
        Btn.setBackgroundColor(0xFFFF00FF);
        CourseClass ss = new CourseClass();
        Btn.setCourse(ss);*/

        weekNoBtn = getView().findViewById(R.id.week_selection_button);
        prevBtn = getView().findViewById(R.id.prev_button);
        nextBtn = getView().findViewById(R.id.next_button);
        nextBtn = getView().findViewById(R.id.next_button);
        backToNowBtn = getView().findViewById(R.id.backToNowButton);
        setToCurrentWeekBtn = getView().findViewById(R.id.setToCurrentWeekButton);
        tableScrollView = getView().findViewById(R.id.tableScrollView);
        outLayout = getView().findViewById(R.id.myCoordinatorLayout);
        gridLayout = getView().findViewById(R.id.class_grid);
        monthText = getView().findViewById(R.id.month_textview);
        mondayText = getView().findViewById(R.id.monday_textview);
        tuesdayText = getView().findViewById(R.id.tuesday_textview);
        wednesdayText = getView().findViewById(R.id.wednesday_textview);
        thursdayText = getView().findViewById(R.id.thursday_textview);
        fridayText = getView().findViewById(R.id.friday_textview);
        saturdayText = getView().findViewById(R.id.saturday_textview);
        sundayText = getView().findViewById(R.id.sunday_textview);
        tableScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(tableScrollView.getChildAt(0).getHeight()-tableScrollView.getHeight() - tableScrollView.getScrollY()<=50){
                    backToNowBtn.hide();
                    setToCurrentWeekBtn.hide();
                }else if(currentWeek != nowWeek){
                    backToNowBtn.show();
                    setToCurrentWeekBtn.show();
                }
            }
        });
        gridLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    lastTouchDownXY[0] = event.getX();
                    lastTouchDownXY[1] = event.getY();
                }
                return false;
            }
        });
        gridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float x = lastTouchDownXY[0];
                float y = lastTouchDownXY[1];
                float w = gridLayout.getWidth()/15;
                float h = gridLayout.getHeight()/11;
                int weekid = (int)((x-w)/2/w+1);
                int clockid = (int)(y/h+1);
                if(weekid == 0){
                    return;
                }
                if(ifAnyAddButtonShown){
                    gridLayout.removeViewAt(gridLayout.getChildCount()-1);
                    ifAnyAddButtonShown = false;
                }else{
                    ifAnyAddButtonShown = true;
                    //Showing addClass Button
                    AddClassButton addBtn = new AddClassButton(getActivity());
                    GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                    param.columnSpec = GridLayout.spec(weekid,1);
                    param.rowSpec = GridLayout.spec(clockid-1,1);
                    param.width = 0;
                    param.setGravity(Gravity.FILL);
                    addBtn.setLayoutParams(param);
                    gridLayout.addView(addBtn);
                    addBtn.init(weekid,clockid,currentWeek);
                }
            }
        });

        weekNoBtn.setText(String.format(getResources().getString(R.string.week_selection_btn),currentWeek));
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentWeek != 1){
                    currentWeek = currentWeek - 1;
                    weekNoBtn.setText(String.format(getResources().getString(R.string.week_selection_btn),currentWeek));

                }
                onWeekUpdate();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentWeek != 25){
                    currentWeek = currentWeek + 1;
                }
                onWeekUpdate();
            }
        });
        weekNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWeekSelectionDialog();
            }
        });
        backToNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWeek = nowWeek;
                onWeekUpdate();
                Snackbar.make(outLayout, getResources().getString(R.string.already_back_to_current_week),Snackbar.LENGTH_SHORT).show();
            }
        });
        setToCurrentWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetCurrentWeekDialog();
            }
        });
        onWeekUpdate();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_table, container, false);
    }


    private void onWeekUpdate(){
        ifAnyAddButtonShown = false;
        if(currentWeek == nowWeek){
            backToNowBtn.hide();
            setToCurrentWeekBtn.hide();
        }else{
            backToNowBtn.show();
            setToCurrentWeekBtn.show();
        }
        if(currentWeek != nowWeek){
            weekNoBtn.setText(String.format(getResources().getString(R.string.week_selection_btn),currentWeek)+"-"+getResources().getString(R.string.not_current_week));
        }else{
            weekNoBtn.setText(String.format(getResources().getString(R.string.week_selection_btn),currentWeek));
        }
        //showing Date
        Calendar thisMonday = Calendar.getInstance();
        thisMonday.set(Calendar.YEAR, firstCd.get(Calendar.YEAR));
        thisMonday.set(Calendar.MONTH, firstCd.get(Calendar.MONTH));
        thisMonday.set(Calendar.DAY_OF_MONTH, firstCd.get(Calendar.DAY_OF_MONTH));
        thisMonday.set(Calendar.HOUR_OF_DAY, 0);
        thisMonday.set(Calendar.MINUTE, 0);
        thisMonday.set(Calendar.SECOND, 0);
        thisMonday.set(Calendar.MILLISECOND, 0);
        thisMonday.add(Calendar.DAY_OF_YEAR, currentWeek*7-7);
        monthText.setText(String.format(getResources().getString(R.string.month_placeholder),thisMonday.get(Calendar.MONTH)+1));
        mondayText.setText(getResources().getString(R.string.monday) + "\n" + String.format(getResources().getString(R.string.day_placeholder),thisMonday.get(Calendar.DAY_OF_MONTH)));
        thisMonday.add(Calendar.DAY_OF_YEAR, 1);
        tuesdayText.setText(getResources().getString(R.string.tuesday) + "\n" + String.format(getResources().getString(R.string.day_placeholder),thisMonday.get(Calendar.DAY_OF_MONTH)));
        thisMonday.add(Calendar.DAY_OF_YEAR, 1);
        wednesdayText.setText(getResources().getString(R.string.wednesday) + "\n" + String.format(getResources().getString(R.string.day_placeholder),thisMonday.get(Calendar.DAY_OF_MONTH)));
        thisMonday.add(Calendar.DAY_OF_YEAR, 1);
        thursdayText.setText(getResources().getString(R.string.thursday) + "\n" + String.format(getResources().getString(R.string.day_placeholder),thisMonday.get(Calendar.DAY_OF_MONTH)));
        thisMonday.add(Calendar.DAY_OF_YEAR, 1);
        fridayText.setText(getResources().getString(R.string.friday) + "\n" + String.format(getResources().getString(R.string.day_placeholder),thisMonday.get(Calendar.DAY_OF_MONTH)));
        thisMonday.add(Calendar.DAY_OF_YEAR, 1);
        saturdayText.setText(getResources().getString(R.string.saturday) + "\n" + String.format(getResources().getString(R.string.day_placeholder),thisMonday.get(Calendar.DAY_OF_MONTH)));
        thisMonday.add(Calendar.DAY_OF_YEAR, 1);
        sundayText.setText(getResources().getString(R.string.sunday) + "\n" + String.format(getResources().getString(R.string.day_placeholder),thisMonday.get(Calendar.DAY_OF_MONTH)));
        //Applying colors to weekdays
        mondayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayNormal));
        tuesdayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayNormal));
        wednesdayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayNormal));
        thursdayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayNormal));
        fridayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayNormal));
        saturdayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayNormal));
        sundayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayNormal));
        if(currentWeek == nowWeek){
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case Calendar.MONDAY:
                    mondayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayActive));
                    break;
                case Calendar.TUESDAY:
                    tuesdayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayActive));
                    break;
                case Calendar.WEDNESDAY:
                    wednesdayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayActive));
                    break;
                case Calendar.THURSDAY:
                    thursdayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayActive));
                    break;
                case Calendar.FRIDAY:
                    fridayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayActive));
                    break;
                case Calendar.SATURDAY:
                    saturdayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayActive));
                    break;
                case Calendar.SUNDAY:
                    sundayText.setBackgroundColor(getResources().getColor(R.color.colorWeekdayActive));
                    break;
                default:
                    break;
            }
        }
        //Loading Interface
        int classNum = courseManager.getNumOfClassesInWeek(currentWeek);
        gridLayout.removeViews(19, gridLayout.getChildCount()-19);
        if (classNum != 0) {
            ClassButton[] buttonCollection = new ClassButton[classNum];
            int buttonPoint = 0;
            for(int iw=1;iw<=7;iw++){
                for(int ic=1;ic<=11;ic++){
                    if(courseManager.getCourseByTime(currentWeek,iw,ic) != null){
                        CourseClass cc = courseManager.getCourseByTime(currentWeek,iw,ic);
                        buttonCollection[buttonPoint] = new ClassButton(getActivity());
                        buttonCollection[buttonPoint].setCourse(cc);
                        int classDuration = cc.clock % 10;
                        int classStartAt = (cc.clock - classDuration)/10;
                        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                        param.columnSpec = GridLayout.spec(cc.weekday,1);
                        param.rowSpec = GridLayout.spec(classStartAt-1,classDuration);
                        param.width = 0;
                        param.setGravity(Gravity.FILL);
                        buttonCollection[buttonPoint].setLayoutParams(param);
                        buttonCollection[buttonPoint].setBackgroundResource(R.drawable.class_button);
                        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        boolean solid_grid = sharedPref.getBoolean("pref_solid_grid", false);
                        if(solid_grid) {
                            buttonCollection[buttonPoint].setMaxLines(classDuration * 3);
                            buttonCollection[buttonPoint].setEllipsize(TextUtils.TruncateAt.END);
                        }
                        buttonCollection[buttonPoint].setBackgroundColor(courseManager.getColorByName(cc.name));
                        buttonCollection[buttonPoint].setTextColor(0xffffffff);
                        gridLayout.addView(buttonCollection[buttonPoint]);
                        buttonPoint++;
                    }
                }
            }
        }

    }

    private void showWeekSelectionDialog() {
        final String[] items = new String[25];
        for(int i=0;i<25;i++){
            items[i] = String.format(getResources().getString(R.string.week_selection_btn),i+1) + " - " + String.format(getResources().getString(R.string.class_in_week),courseManager.getNumOfClassesInWeek(i+1));
            if(i == nowWeek -1){
                items[i] = items[i] + " - " + getResources().getString(R.string.current_week);
            }
        }
        AlertDialog.Builder listDialog = new AlertDialog.Builder(getActivity());
        listDialog.setTitle(getResources().getString(R.string.week_selection_title));
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentWeek = which + 1;
                onWeekUpdate();
            }
        });
        listDialog.show();
    }
    private void showSetCurrentWeekDialog(){
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
        normalDialog.setTitle(getResources().getString(R.string.set_current_week));
        normalDialog.setMessage(String.format(getResources().getString(R.string.confirm_set_current_week_with_placeholder),currentWeek));
        normalDialog.setPositiveButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firstCd.add(Calendar.DAY_OF_YEAR, (nowWeek-currentWeek)*7);
                        nowWeek = currentWeek;
                        editor.putInt("first_week_year",firstCd.get(Calendar.YEAR));
                        editor.putInt("first_week_month",firstCd.get(Calendar.MONTH));
                        editor.putInt("first_week_day",firstCd.get(Calendar.DAY_OF_MONTH));
                        editor.apply();
                        onWeekUpdate();
                        Snackbar.make(outLayout, getResources().getString(R.string.current_week_has_been_set),Snackbar.LENGTH_SHORT).show();
                    }
                });
        normalDialog.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing to do
                    }
                });
        normalDialog.show();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
