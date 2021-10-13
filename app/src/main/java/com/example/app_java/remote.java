package com.example.app_java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.LinkedList;

public class remote extends Fragment {
    private CheckBox btn_set_lock;
    private CheckBox btn_effect_sound;
    private CheckBox btn_alert_sound;

    private CheckBox way_pwd;
    private CheckBox way_face_recog;
    private CheckBox way_app;

    private LinkedList<Integer> link;

    private ImageButton btn_alert_sound_on;
    Intent intent;

    private Button set_final;
    public String way_data[];
    private int way_index;
    private int way_seq[] = new int[10];
    private char way_data_char[];
    private String data_str;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_remote, container, false);

        link = new LinkedList<>();
        btn_set_lock = (CheckBox) rootview.findViewById(R.id.set_lock);
        btn_effect_sound = (CheckBox) rootview.findViewById(R.id.effect_sound);
        btn_alert_sound = (CheckBox) rootview.findViewById(R.id.alert_sound);

        way_pwd = (CheckBox) rootview.findViewById(R.id.way_pwd);
        way_face_recog = (CheckBox) rootview.findViewById(R.id.face_recog);
        way_app = (CheckBox) rootview.findViewById(R.id.way_app);

        set_final = (Button) rootview.findViewById(R.id.final_set);

        way_index = 0;
//        way_data = new int[10];
        way_data = new String[10];
        for (int i = 0; i < 10; i++) {
            way_data[i] = "X";
        }
        way_data_char = new char[10];
        way_data_char[0] = 'S';
        way_data_char[1] = 'L';
        way_data_char[2] = 'W';
        for (int i = 3; i < 10; i++) {
            way_data_char[i] = 'N';
        }

        for (int i = 0; i < way_seq.length; i++) {
            way_seq[i] = 0;
        }


        intent = new Intent(getActivity(), MyService.class);

        btn_effect_sound.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();
                if(checked){
                    intent.putExtra("TO_MCU", "SES1\n");
                }
                else{
                    intent.putExtra("TO_MCU", "SES0\n");
                }

                getActivity().startService(intent);
            }
        });

        btn_alert_sound.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();
                if(checked){
                    intent.putExtra("TO_MCU", "SAS1\n");
                }
                else{
                    intent.putExtra("TO_MCU", "SAS0\n");
                }
                getActivity().startService(intent);
            }
        });

        btn_set_lock.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();
                if(checked){
                    intent.putExtra("TO_MCU", "RSUO\n");
                }
                else{
                    intent.putExtra("TO_MCU", "RSUC\n");
                }
                getActivity().startService(intent);
            }
        });

        set_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                data_str = "";
                for(int i = 0 ; i < way_index ; i++){
                    data_str += link.get(i);
                }

                String result_data = "SLW" + data_str + "\n";
                Intent set_way = new Intent(getActivity(), MyService.class);
                set_way.putExtra("TO_MCU", result_data);
                getActivity().startService(set_way);
                Log.e("way_data", result_data);
            }
        });

        way_pwd.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox)view).isChecked();
                if(checked) {
                    way_seq[1] = way_index;
                    link.addLast(1);
                    way_index++;
                    Log.e("way_index_plus",String.valueOf(way_index));
                }
                else {
                    if(way_index > 0){
                        way_index--;
                    }
                    link.remove(way_seq[1]);
                    Log.e("way_index_minus",String.valueOf(way_index));
                }
//                Log.e("index",String.valueOf(way_index));
//                boolean checked = ((CheckBox) view).isChecked();
//                if (checked) {
//                    way_data[way_index] = "1";
//                    way_seq[0] = way_index;
//                    way_index++;
//
//                } else {
//                    way_data[way_seq[0]] = "";
//                    if (way_index != 0) {
//                        way_seq[0] = 0;
//                        if(way_index == 3){
//                            way_index = way_index;
//                        }
//                        else way_index--;
//                    }
//                }
            }
        });

        way_face_recog.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean checked = ((CheckBox)view).isChecked();
                    if(checked) {
                        way_seq[2] = way_index;
                        link.addLast(2);
                        way_index++;
                        Log.e("way_index_plus",String.valueOf(way_index));
                    }
                    else {
                        if(way_index > 0){
                            way_index--;
                        }
                        link.remove(way_seq[2]);
                        Log.e("way_index_plus",String.valueOf(way_index));
                    }
//                    Log.e("index",String.valueOf(way_index));
//                    boolean checked = ((CheckBox) view).isChecked();
//                    if (checked) {
//                        way_seq[1] = way_index;
//                        way_data[way_index] = "2";
//                        way_index++;
//
//                    } else {
//                        way_data[way_seq[1]] = "";
//                        if (way_index != 0) {
//                            way_seq[1] = 0;
//                            if(way_index == 3){
//                                way_index = way_index;
//                            }
//                            else way_index--;
//                        }
//                    }
                }
        });

        way_app.setOnClickListener(new CheckBox.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean checked = ((CheckBox)view).isChecked();
                        if(checked) {

                            way_seq[3] = way_index;
                            link.addLast(3);
                            way_index++;
                            Log.e("way_index_plus",String.valueOf(way_index));
                        }
                        else {

                            if(way_index > 0){
                                way_index--;
                            }
                            link.remove(way_seq[3]);
                            Log.e("way_index_plus",String.valueOf(way_index));
                        }
//                        Log.e("index",String.valueOf(way_index));
//                        boolean checked = ((CheckBox) view).isChecked();
//                        if (checked) {
//                            way_seq[2] = way_index;
//                            way_data[way_seq[2]] = "3";
//                            way_index++;
//
//                        } else {
//                            way_data[way_seq[2]] = "";
//                            if (way_index != 0) {
//                                way_seq[2] = 0;
//                                if(way_index == 3)way_index = way_index;
//                                else way_index--;
//                            }
//                        }
                    }
        });
        return rootview;

    }



//        btn_effect_sound_on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                intent.putExtra("TO_MCU", "SES0\n");
//                getActivity().startService(intent);
//            }
//        });
//
//        btn_alert_sound_off.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                intent.putExtra("TO_MCU", "SAS1\n");
//                getActivity().startService(intent);
//            }
//        });


}

