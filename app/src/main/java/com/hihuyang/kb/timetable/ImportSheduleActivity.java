package com.hihuyang.kb.timetable;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImportSheduleActivity extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();
    private TextView hinttext;
    private String lastJSESSIONID;
    private ProgressDialog waitingDialog;
    private String onDate;
    private String offDate;
    final Context ctx = this;
    CourseManager cm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_shedule);
        getSupportActionBar().setTitle(getResources().getString(R.string.import_shedule));
        waitingDialog = new ProgressDialog(this);
        hinttext = findViewById(R.id.textView);
        final ImageView captcha = findViewById(R.id.captchaImage);
        cm = new CourseManager(ctx);
        captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(hinttext.getText() != getResources().getString(R.string.loading_captcha)){
                loadCaptcha();
                captcha.setImageDrawable(getResources().getDrawable(R.drawable.ic_sync));
                hinttext.setText(getResources().getString(R.string.loading_captcha));
            }
            }
        });
        Calendar nowCal = Calendar.getInstance();
        ToggleButton tgBtn = findViewById(R.id.toggleButton);
        int month = nowCal.get(Calendar.MONTH);
        int year = nowCal.get(Calendar.YEAR);
        if(month>=8){
            tgBtn.setTextOn(getResources().getString(R.string.first_sem) + " (" + String.valueOf(year)+"-"+String.valueOf(year+1)+"-1)");
            onDate = String.valueOf(year)+"-"+String.valueOf(year+1)+"-1";
            tgBtn.setTextOff(getResources().getString(R.string.second_sem) + " (" + String.valueOf(year)+"-"+String.valueOf(year+1)+"-2)");
            offDate = String.valueOf(year)+"-"+String.valueOf(year+1)+"-2";
        }else if(month<=2){
            tgBtn.setTextOn(getResources().getString(R.string.first_sem) + " (" + String.valueOf(year-1)+"-"+String.valueOf(year)+"-1)");
            onDate = String.valueOf(year-1)+"-"+String.valueOf(year)+"-1";
            tgBtn.setTextOff(getResources().getString(R.string.second_sem) + " (" + String.valueOf(year-1)+"-"+String.valueOf(year)+"-2)");
            offDate = String.valueOf(year-1)+"-"+String.valueOf(year)+"-2";
        }else{
            tgBtn.setTextOn(getResources().getString(R.string.second_sem) + " (" + String.valueOf(year-1)+"-"+String.valueOf(year)+"-2)");
            onDate = String.valueOf(year-1)+"-"+String.valueOf(year)+"-2";
            tgBtn.setTextOff(getResources().getString(R.string.first_sem) + " (" + String.valueOf(year)+"-"+String.valueOf(year+1)+"-1)");
            offDate = String.valueOf(year)+"-"+String.valueOf(year+1)+"-1";
        }
        tgBtn.setText(onDate);
        SharedPreferences sharedPref = getSharedPreferences("USER_STORE",Context.MODE_PRIVATE);
        EditText usernameText = findViewById(R.id.username_text);
        EditText passwordText = findViewById(R.id.password_text);
        usernameText.setText(sharedPref.getString("account_username", ""));
        passwordText.setText(sharedPref.getString("account_password", ""));
        loadCaptcha();
    }
    public void import_Clicked(View view) {
        if(lastJSESSIONID == null){
            return;
        }
        Button import_now = findViewById(R.id.start_import);
        waitingDialog.setTitle(getResources().getString(R.string.importing));
        waitingDialog.setMessage(getResources().getString(R.string.logging_to_jwc));
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
        EditText usernameText = findViewById(R.id.username_text);
        EditText passwordText = findViewById(R.id.password_text);
        EditText captchaText = findViewById(R.id.captcha_text);
        String inputUsername = usernameText.getText().toString();
        String inputPassword = passwordText.getText().toString();
        String inputCaptcha = captchaText.getText().toString();
        FormBody formBody = new FormBody.Builder()
                .add("PASSWORD", inputPassword)
                .add("RANDOMCODE", inputCaptcha)
                .add("useDogCode", "")
                .add("USERNAME", inputUsername)
                .add("x", "55")
                .add("y", "17")
                .build();
        Request request = new Request.Builder()
                .url("http://jwxt.wust.edu.cn/whkjdx/Logon.do?method=logon")
                .addHeader("Cookie", lastJSESSIONID)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        waitingDialog.dismiss();
                        Snackbar.make(hinttext,getResources().getString(R.string.network_connection_failed),Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res.length()>200){
                            waitingDialog.dismiss();
                            if(res.contains("验证码错误")){
                                Snackbar.make(hinttext,getResources().getString(R.string.invalid_captcha),Snackbar.LENGTH_SHORT).show();
                            }else if(res.contains("该帐号不存在或密码错误")){
                                Snackbar.make(hinttext,getResources().getString(R.string.invalid_account),Snackbar.LENGTH_SHORT).show();
                            }
                            loadCaptcha();
                        }else{
                            waitingDialog.setMessage(getResources().getString(R.string.downloading_shedule));
                            //Success logon
                            waitingDialog.setMessage(getResources().getString(R.string.client_start_progress));
                            Request request = new Request.Builder()
                                    .url("http://jwxt.wust.edu.cn/whkjdx/framework/main.jsp")
                                    .addHeader("Cookie", lastJSESSIONID)
                                    .build();
                            Call call = client.newCall(request);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            waitingDialog.dismiss();
                                            Snackbar.make(hinttext,getResources().getString(R.string.network_connection_failed),Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, final Response responseFinal) throws IOException {
                                    Request request = new Request.Builder()
                                            .url("http://jwxt.wust.edu.cn/whkjdx/Logon.do?method=logonBySSO")
                                            .addHeader("Cookie", lastJSESSIONID)
                                            .build();
                                    Call call2 = client.newCall(request);
                                    call2.enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    waitingDialog.dismiss();
                                                    Snackbar.make(hinttext, getResources().getString(R.string.network_connection_failed), Snackbar.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        @Override
                                        public void onResponse(Call call, final Response responseFinal) throws IOException {
                                            final String r2 = responseFinal.body().string();

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    EditText usernameText = findViewById(R.id.username_text);
                                                    String inputUsername = usernameText.getText().toString();
                                                    ToggleButton tgBtn = findViewById(R.id.toggleButton);
                                                    String date;
                                                    if(tgBtn.isChecked()){
                                                        date = onDate;
                                                    }else{
                                                        date = offDate;
                                                    }
                                                    Request request = new Request.Builder()
                                                            .url("http://jwxt.wust.edu.cn/whkjdx/tkglAction.do?method=printExcelByFz&sql=&type=xsdy&xnxqh="+date+"&xsid="+inputUsername)
                                                            .addHeader("Cookie", lastJSESSIONID)
                                                            .build();
                                                    Call call3 = client.newCall(request);
                                                    call3.enqueue(new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    waitingDialog.dismiss();
                                                                    Snackbar.make(hinttext, getResources().getString(R.string.network_connection_failed), Snackbar.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                        @Override
                                                        public void onResponse(Call call, final Response responseFinal) throws IOException {
                                                            final String r3 = responseFinal.body().string();
                                                            Log.d("RES",r3);
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    String[] lines = r3.split("\n");
                                                                    int items = 0;
                                                                    for (String item : lines) {
                                                                        if(item.startsWith("objdocSheets.Sheets(\"Sheet1\").Cells") && item.length()>=60 && !item.startsWith("objdocSheets.Sheets(\"Sheet1\").Cells(2,2).Value")){
                                                                            items++;
                                                                            String[] temp = item.split("  ");
                                                                            items = items + temp.length - 1;
                                                                        }
                                                                    }
                                                                    String[] results = new String[items];
                                                                    items = 0;
                                                                    for (String item : lines) {
                                                                        if(item.startsWith("objdocSheets.Sheets(\"Sheet1\").Cells") && item.length()>=60 && !item.startsWith("objdocSheets.Sheets(\"Sheet1\").Cells(2,2).Value")){
                                                                            String substr = item.substring(item.indexOf("Cells(")+6);
                                                                            String[] temp = substr.split("  ");
                                                                            String[] titleTemps = substr.split("\"");
                                                                            String title = titleTemps[0] + "\"";
                                                                            String content = titleTemps[1];
                                                                            String[] ntemp = content.split("n");
                                                                            for(int t=3;t<=temp.length;t++){
                                                                                String newString = ntemp[(t-3)*2] + "n" + ntemp[1+(t-3)*2];
                                                                                newString = title + newString.substring(0, newString.length() - 1) + "\";";
                                                                                results[items] = newString;
                                                                                Log.d("EX",newString);
                                                                                items++;
                                                                            }
                                                                            String lastString = title + ntemp[ntemp.length-2] + "n" + ntemp[ntemp.length-1] + "\";";
                                                                            results[items] = lastString;
                                                                            Log.d("EX",lastString);
                                                                            items++;
                                                                        }
                                                                    }
                                                                    CourseClass[] resultCourse = new CourseClass[items];
                                                                    for(int i=0;i<items;i++){
                                                                        //Get Clock
                                                                        resultCourse[i] = new CourseClass();
                                                                        String[] temp = results[i].split(",");
                                                                        resultCourse[i].clock = ((Integer.valueOf(temp[0])-14)*2+1)*10+2;
                                                                        String temps = "";
                                                                        if(temp.length>2){
                                                                            for(int m=1;m<temp.length;m++){
                                                                                temps = temps + temp[m];
                                                                                if(m!=temp.length-1){
                                                                                    temps = temps + ",";
                                                                                }
                                                                            }
                                                                        }else{
                                                                            temps = temp[1];
                                                                        }
                                                                        //Get Weekdays
                                                                        temp = temps.split("\\)");
                                                                        String temps1 = temp[0];
                                                                        resultCourse[i].weekday = Integer.valueOf(temps1)-1;
                                                                        //Get ClassName
                                                                        temp = temps.split("\"");
                                                                        temps = temp[1];
                                                                        temp = temps.split("n");
                                                                        temps = temp[0];
                                                                        temps = temps.substring(0, temps.length() - 1);
                                                                        resultCourse[i].name = temps;
                                                                        temps = temp[1];
                                                                        //Get teacherName
                                                                        temp = temps.split("  ");
                                                                        resultCourse[i].teacher = temp[0];
                                                                        temps = temp[1];
                                                                        //Get week
                                                                        /*String[] weektemp = temps.split("-");
                                                                        Log.d("Debug",temps);
                                                                        if(weektemp.length == 1){
                                                                            String[] weeks = temps.split("周");
                                                                            int startWeek = Integer.valueOf(weeks[0]);
                                                                            int weekid = (int)Math.pow(2,25-startWeek);
                                                                            resultCourse[i].week = weekid;
                                                                        }else if (weektemp.length == 2) {
                                                                            temp = temps.split("-");
                                                                            int startWeek = Integer.valueOf(temp[0]);
                                                                            temps = temp[1];
                                                                            temp = temps.split("周");
                                                                            int endWeek = Integer.valueOf(temp[0]);
                                                                            temps = temp[1];
                                                                            int weekid = 0;
                                                                            if(startWeek<=endWeek){
                                                                                for(int j=startWeek;j<=25;j++){
                                                                                    weekid *= 2;
                                                                                    if(j<=endWeek){
                                                                                        weekid += 1;
                                                                                    }
                                                                                }
                                                                            }
                                                                            resultCourse[i].week = weekid;
                                                                        }else{
                                                                            //MultipleTime
                                                                            String[] weeks = temps.split("周");
                                                                            String allweeks = weeks[0];
                                                                            String[] everyWeekTime = allweeks.split(",");
                                                                            int allweekid = 0;
                                                                            int weekid = 0;
                                                                            for(String procWeek : everyWeekTime){
                                                                                String[] weeksplit = procWeek.split("-");
                                                                                int startWeek = Integer.valueOf(weeksplit[0]);
                                                                                int endWeek = Integer.valueOf(weeksplit[1]);
                                                                                if(startWeek<=endWeek){
                                                                                    for(int j=startWeek;j<=25;j++){
                                                                                        weekid *= 2;
                                                                                        if(j<=endWeek){
                                                                                            weekid += 1;
                                                                                        }
                                                                                    }
                                                                                }
                                                                                allweekid += weekid;
                                                                                weekid = 0;
                                                                            }
                                                                            resultCourse[i].week = allweekid;
                                                                            temp = temps.split("周");
                                                                            temps = temp[1];
                                                                        }*/
                                                                        //Get Week New Version
                                                                        String[] weektemp = temps.split("周");
                                                                        String allweeks = weektemp[0];
                                                                        String[] eachweek = allweeks.split(",");
                                                                        int allweekid = 0;
                                                                        for(String thisweek:eachweek){
                                                                            String[] splitline = thisweek.split("-");
                                                                            if(splitline.length==1){
                                                                                allweekid += Math.pow(2,25-Integer.valueOf(splitline[0]));
                                                                            }else{
                                                                                int startWeek = Integer.valueOf(splitline[0]);
                                                                                int endWeek = Integer.valueOf(splitline[1]);
                                                                                int weekid = 0;
                                                                                if(startWeek<=endWeek){
                                                                                    for(int j=startWeek;j<=25;j++){
                                                                                        weekid *= 2;
                                                                                        if(j<=endWeek){
                                                                                            weekid += 1;
                                                                                        }
                                                                                    }
                                                                                }
                                                                                allweekid += weekid;
                                                                            }
                                                                        }
                                                                        resultCourse[i].week = allweekid;
                                                                        temps = weektemp[1];
                                                                        //Get Place
                                                                        String[] placetemp = temps.split("节]");
                                                                        if(placetemp.length>1){
                                                                            resultCourse[i].place = placetemp[1];
                                                                        }else{
                                                                            resultCourse[i].place = temps;
                                                                        }
                                                                        //Finally
                                                                        resultCourse[i].intype = 1;

                                                                        cm.addCourse(resultCourse[i]);
                                                                    }
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                                                    int classNum = items;
                                                                    if(classNum==0){
                                                                        builder.setMessage(getResources().getString(R.string.no_class_imported))
                                                                                .setCancelable(false)
                                                                                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                                        waitingDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }else{
                                                                        builder.setMessage(String.format(getResources().getString(R.string.class_imported),classNum))
                                                                                .setCancelable(false)
                                                                                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                                        waitingDialog.dismiss();
                                                                                        finish();
                                                                                    }
                                                                                });
                                                                    }

                                                                    AlertDialog alert = builder.create();
                                                                    alert.show();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }
    private void getScheduleNow(){
        ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle(getResources().getString(R.string.importing));
        waitingDialog.setMessage(getResources().getString(R.string.logging_to_jwc));
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }
    public void loadCaptcha(){
        Request request = new Request.Builder()
                .get()
                .url("http://jwxt.wust.edu.cn/whkjdx/verifycode.servlet")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hinttext.setText(getResources().getString(R.string.load_captcha_failed_click_to_reload));
                        ImageView captcha = findViewById(R.id.captchaImage);
                        captcha.setImageDrawable(getResources().getDrawable(R.drawable.ic_sync));
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView captcha = findViewById(R.id.captchaImage);
                        captcha.setImageBitmap(bitmap);
                        hinttext.setText(getResources().getString(R.string.click_image_to_reload_captcha));
                        String[] separated = response.header("Set-Cookie").split(";");
                        lastJSESSIONID = separated[0];
                    }
                });
            }
        });
    }
}
