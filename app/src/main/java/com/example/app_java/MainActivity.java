package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.widget.Toast;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    Uri videoUri;

    private Intent serviceIntent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.loading);
        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.loading);


        videoView.setVideoURI(videoUri);
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                Intent intent = new Intent(MainActivity.this, appLock.class);
//                intent.putExtra(app_lock_const.type,app_lock_const.ENABLE_PASSLOCK);
//                startActivityForResult(intent,app_lock_const.ENABLE_PASSLOCK);
                Intent intent = new Intent(MainActivity.this, Swipe.class);
                startActivity(intent);
            }
        });


    }




}


