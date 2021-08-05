package com.example.app_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class Danger extends Control {


    private EditText ET_User;
    private ImageView mqtt_image;
    private ImageButton btn_home;
    private  Bitmap bitmap;

    private byte[] mqtt_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_danger);

        btn_home = findViewById(R.id.homeButton);
        ET_User = findViewById(R.id.ET_user);
        mqtt_image = findViewById(R.id.imageView);
        bitmap = null;
        mqtt_picture = null;

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Danger.this, Control.class);
                startActivity(intent);
            }
        });

        Intent picture = getIntent();

        if(picture.getByteArrayExtra("picture") != null) {
            bitmap = BitmapFactory.decodeByteArray(picture.getByteArrayExtra("picture"), 0, picture.getByteArrayExtra("picture").length);
        }
        else{
            bitmap = null;
        }

        mqtt_image.setImageBitmap(bitmap);
    }
}

