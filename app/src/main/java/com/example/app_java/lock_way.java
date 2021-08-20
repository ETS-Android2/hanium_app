package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

public class lock_way extends AppCompatActivity {

    private CheckBox way_pwd;
    private CheckBox way_face_recog;
    private CheckBox way_app;
    private CheckBox way4;
    private ImageButton btn_home;
    private Button set_final;
    public  String way_data[];
    private EditText set_value;
    private int way_index;
    private int way_seq[] = new int[4];
    private char way_data_char[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_way);

        way_pwd = (CheckBox) findViewById(R.id.way_pwd);
        way_face_recog = (CheckBox) findViewById(R.id.face_recog);
        way_app = (CheckBox) findViewById(R.id.way_app);
        way4 = (CheckBox) findViewById(R.id.way4);
        btn_home = (ImageButton) findViewById(R.id.homeButton);
        set_final = (Button) findViewById(R.id.final_set);
        set_value = (EditText)findViewById(R.id.set_value);
        way_index = 1;
//        way_data = new int[10];
        way_data = new String[10];
        for(int i = 0; i < 10; i++){
            way_data[i] = "X";
        }
        way_data_char = new char[10];
        way_data_char[0] = 'S';
        way_data_char[1] = 'L';
        way_data_char[2] = 'W';
        for(int i = 3; i < 10 ; i ++){
            way_data_char[i] = 'N';
        }

        for(int i = 0; i < way_seq.length ; i ++){
            way_seq[i] = 0;
        }

//        way_pwd.setOnClickListener(new CheckBox.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                way_pwd.toggle();
//            }
//        });
//
//        way_face_recog.setOnClickListener(new CheckBox.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                way_face_recog.toggle();
//            }
//        });
//
//        way3.setOnClickListener(new CheckBox.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                way3.toggle();
//            }
//        });
//
//        way4.setOnClickListener(new CheckBox.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                way4.toggle();
//            }
//        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(lock_way.this, Control.class);
                startActivity(home);
            }
        });

        set_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String data_str = "";
                for(int i = 1; !way_data[i].equals("X") ; i++){
                    data_str += way_data[i];
                }
                String result_data = "SLW" + data_str + "\n";
                Intent set_way = new Intent(lock_way.this, MyService.class);
                set_way.putExtra("TO_MCU",result_data);
                startService(set_way);
                Log.e("way_data", result_data);
                set_value.setText(result_data);
            }
        });

    }
    public void onCheckboxClicked(View view){
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()){
            case R.id.way_pwd:
                if(checked){
                    this.way_data[way_index] = "1";
                    way_seq[0] = way_index;
                    way_index++;
                }
                else {
                    this.way_data[way_seq[0]] = "";
                    if(way_index!= 0){
                        way_seq[0] = 0;
                        way_index--;
                    }
                }
                break;
            case R.id.face_recog:
                if(checked){
                    way_seq[1] = way_index;
                    this.way_data[way_index] = "2";
                    way_index++;
                }
                else{
                    this.way_data[way_seq[1]] = "";
                    if(way_index!= 0){
                        way_seq[1] = 0;
                        way_index--;
                    }
                }
                break;
            case R.id.way_app:
                if(checked){
                    way_seq[2] = way_index;
                    this.way_data[way_seq[2]] = "3";
                    way_index++;
                }
                else{
                    this.way_data[way_seq[2]] = "";
                    if(way_index!= 0){
                        way_seq[2] = 0;
                        way_index--;
                    }
                }
                break;
            case R.id.way4:
                if(checked){
                    way_seq[3] = way_index;
                    this.way_data[way_seq[3]] = "4";
                    way_index++;
                }
                else{
                    this.way_data[way_seq[3]] = "";
                    if(way_index!= 0){
                        way_seq[3] = 0;
                        way_index--;
                    }
                }
                break;
        }
    }


}