package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Set_Pwd extends AppCompatActivity {
    private ImageButton btn_home;
    private ImageButton app_pwd_set;
    private ImageButton door_pwd_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        app_pwd_set = (ImageButton) findViewById(R.id.smartphone);
        door_pwd_set = (ImageButton) findViewById(R.id.doorlock);
        btn_home = (ImageButton)findViewById(R.id.homeButton);


        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_home = new Intent(Set_Pwd.this, Control.class);
                startActivity(intent_home);
            }
        });

        app_pwd_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_chg_app = new Intent(Set_Pwd.this, appLock.class);
                intent_chg_app.putExtra(app_lock_const.type, app_lock_const.CHANGE_PASSLOCK);
                Set_Pwd.this.startActivityForResult(intent_chg_app, app_lock_const.CHANGE_PASSLOCK);
            }
        });

        door_pwd_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_chg_door = new Intent(Set_Pwd.this, appLock.class);
                intent_chg_door.putExtra(app_lock_const.type, app_lock_const.SET_TOUCHPAD);
                Set_Pwd.this.startActivityForResult(intent_chg_door, app_lock_const.SET_TOUCHPAD);
            }
        });
    }
}



