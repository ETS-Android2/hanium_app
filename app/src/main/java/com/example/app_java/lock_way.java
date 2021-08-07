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
    private CheckBox way3;
    private CheckBox way4;
    private ImageButton btn_home;
    private Button set_final;
    public  String way_data[];
    private EditText set_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_way);

        way_pwd = (CheckBox) findViewById(R.id.way_pwd);
        way_face_recog = (CheckBox) findViewById(R.id.face_recog);
        way3 = (CheckBox) findViewById(R.id.way3);
        way4 = (CheckBox) findViewById(R.id.way4);
        btn_home = (ImageButton) findViewById(R.id.homeButton);
        set_final = (Button) findViewById(R.id.final_set);
        set_value = (EditText)findViewById(R.id.set_value);
//        way_data = new int[10];
        way_data = new String[10];
        for(int i = 0; i < 10; i++){
            way_data[i] = "0";
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
                Log.e("checkbox", "set");
                String result_data = "SLW" + way_data[0] + way_data[1] + way_data[2] + way_data[3] + "\\n";
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
                    this.way_data[0] = "1";
                }
                else{
                    this.way_data[0] = "0";
                }
                break;
            case R.id.face_recog:
                if(checked){
                    this.way_data[1] = "1";
                }
                else{
                    this.way_data[1] = "0";
                }
                break;
            case R.id.way3:
                if(checked){
                    this.way_data[2] = "1";
                }
                else{
                    this.way_data[2] = "0";
                }
                break;
            case R.id.way4:
                if(checked){
                    this.way_data[3] = "1";
                }
                else{
                    this.way_data[3] = "0";
                }
                break;
        }
    }


}