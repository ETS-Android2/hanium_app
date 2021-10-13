package com.example.app_java;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.File;


public class Set_lock extends Fragment {

    private TextView storage_tv;
    private ImageView mqtt_image;
    private ImageButton btn_home;
    private Bitmap bitmap;
    private ImageButton storage_btn;
    private String file_name[];
    private int idx = 0;

    SharedPreferences shar_idx;
    SharedPreferences.Editor shar_idx_editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_set_lock, container, false);

        shar_idx = getActivity().getSharedPreferences("file_idx", Context.MODE_PRIVATE);
        shar_idx_editor = shar_idx.edit();

        btn_home = rootview.findViewById(R.id.homeButton);
        mqtt_image = rootview.findViewById(R.id.imageView);
        storage_btn = rootview.findViewById(R.id.Read_Storage);

        storage_tv = rootview.findViewById(R.id.storage_tv);


        idx = shar_idx.getInt("file_idx", 0);
        file_name = new String[idx];
        File tempdir = new File(getActivity().getCacheDir() + "/");    //내부저장소 파일
        File file_list[] = tempdir.listFiles();
        for (int i = 0; i < file_list.length; i++) {
            file_name[i] = file_list[i].getName();
            Log.e("file","Exist");
        }

    storage_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e("Access", "Dialog Access1");
            idx = shar_idx.getInt("file_idx", 0);
            String storage_file[] = new String[idx];

            for (int i = 0; i < idx; i++) {
                storage_file[i] = file_name[i];
            }

//                    for(int i = 0; i < idx; i++){
//                        Log.e("file_name",file_name[i]);
//                    }
            try {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle("확인할 사진을 선택해주세요");
                Log.e("Access", "Dialog Access2");
                dlg.setSingleChoiceItems(storage_file, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String img_name = getActivity().getCacheDir() + "/" + file_name[which];
                        Bitmap storage_bmp = BitmapFactory.decodeFile(img_name);
                        mqtt_image.setImageBitmap(storage_bmp);
                    }
                });
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("Access", "Dialog Access5");
                        //토스트 메시지
                        Toast.makeText(getActivity(), "확인.", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alert = dlg.create();
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 69, 79, 62)));
                Log.e("Access", "Dialog Access6");
                dlg.show();
            } catch (Exception e) {
                Log.e("Dialog", "Not Exist");
            }
        }
    });

        return rootview;
    }

}

