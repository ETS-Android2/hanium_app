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
                intent.putExtra("TO_MCU","RSUO\n");
                break;
            case R.id.set_OPEN:
                intent.putExtra("TO_MCU","RSUC\n");
                break;
            case R.id.effect_sound_off:
                intent.putExtra("TO_MCU","SES1\n");
                break;
            case R.id.effect_sound_on:
                intent.putExtra("TO_MCU","SES0\n");
                break;
            case R.id.alert_sound_off:
                intent.putExtra("TO_MCU","SAS1\n");
                break;
            case R.id.alert_sound_on:
                intent.putExtra("TO_MCU","SAS0\n");
                break;
        }
        startService(intent);
    }
}