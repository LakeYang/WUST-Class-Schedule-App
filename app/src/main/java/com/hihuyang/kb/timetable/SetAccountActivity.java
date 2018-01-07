package com.hihuyang.kb.timetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetAccountActivity extends AppCompatActivity{
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_account);
        sharedPref = getSharedPreferences("USER_STORE",Context.MODE_PRIVATE);
        String username = sharedPref.getString("account_username", "");
        String password = sharedPref.getString("account_password", "");
        final EditText usernameField = findViewById(R.id.username);
        final EditText passwordField = findViewById(R.id.password);
        Button submitBtn = findViewById(R.id.save_account_button);
        usernameField.setText(username);
        passwordField.setText(password);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("account_username",usernameField.getText().toString());
                editor.putString("account_password",passwordField.getText().toString());
                editor.apply();
                finish();
            }
        });

    }
}

