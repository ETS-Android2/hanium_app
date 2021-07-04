package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    Uri videoUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.video_safe);
        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.safe);

        videoView.setVideoURI(videoUri);
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(MainActivity.this, appLock.class);
                startActivity(intent);
            }
        });
    }



}


