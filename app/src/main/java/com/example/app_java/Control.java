package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Control extends AppCompatActivity {

    Button btnset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Button btnset = (Button) findViewById(R.id.btnset);


        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Control.this, appLock.class);
                intent.putExtra(app_lock_const.type,app_lock_const.CHANGE_PASSLOCK);
                Control.this.startActivityForResult(intent,app_lock_const.CHANGE_PASSLOCK);
            }
        });
    }
}
