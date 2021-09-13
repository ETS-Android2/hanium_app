package com.example.app_java;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CharArrayBuffer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Danger extends Control {


    private ImageView mqtt_image;
    private ImageButton btn_home;
    private Bitmap bitmap;
    private Button storage_btn;
    private String file_name[];
    private int idx = 0;

    SharedPreferences shar_idx;
    SharedPreferences.Editor shar_idx_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_danger);

        shar_idx = this.getSharedPreferences("file_idx", Context.MODE_PRIVATE);
        shar_idx_editor = shar_idx.edit();

        btn_home = findViewById(R.id.homeButton);
        mqtt_image = findViewById(R.id.imageView);
        bitmap = null;
        storage_btn = findViewById(R.id.Read_Storage);

        idx = shar_idx.getInt("file_idx",0);
        file_name = new String[idx];
        File tempdir = new File(getCacheDir() + "/");    //내부저장소 파일
        File file_list[] = tempdir.listFiles();
        for(int i = 0 ; i < file_list.length ; i++){
            file_name[i] = file_list[i].getName();
        }




        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Danger.this, Control.class);
                startActivity(intent);
            }
        });


        storage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Log.e("Access", "Dialog Access1");
                    idx = shar_idx.getInt("file_idx",0);
                    String storage_file[] = new String[idx];
                    for(int i = 0; i < idx; i++){
                        storage_file[i] = file_name[i];
                    }
                    for(int i = 0; i < idx; i++){
                        Log.e("file_name",file_name[i]);
                    }
                    AlertDialog.Builder dlg = new AlertDialog.Builder(Danger.this);
                    dlg.setTitle("확인할 사진을 선택해주세요");
                try {
                    dlg.setSingleChoiceItems(storage_file, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String img_name = getCacheDir() + "/" + file_name[which];
                            Bitmap storage_bmp = BitmapFactory.decodeFile(img_name);
                            mqtt_image.setImageBitmap(storage_bmp);
                        }
                    });
                    dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //토스트 메시지
                            Toast.makeText(Danger.this, "확인.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog alert = dlg.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255,69,79,62)));

                    dlg.show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Not Exist", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


}

