package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class remote_lock extends AppCompatActivity {
    ImageButton Tohome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_lock);

        Tohome = (ImageButton)findViewById(R.id.homeButton);

        Tohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(remote_lock.this, Control.class);
                startActivity(intent);
            }
        });
    }

    public void remote_OnClick(View view){
        Intent intent = new Intent(remote_lock.this, MyService.class);
        switch (view.getId()){
            case R.id.set_LOCK:
                intent.putExtra("TO_MCU","SLO0\\n");
                break;
            case R.id.set_OPEN:
                intent.putExtra("TO_MCU","SLO1\\n");
                break;
        }
        startService(intent);
    }
}