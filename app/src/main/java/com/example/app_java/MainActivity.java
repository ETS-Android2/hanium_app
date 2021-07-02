package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    boolean lock =true;

    Button btnSetLock;
    Button btnSetDelLock;
    Button btnChangePwd;
    SharedPreferences sharePref;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSetLock = (Button)findViewById(R.id.btnSetLock);
        Button btnSetDelLock = (Button)findViewById(R.id.btnSetDelLock);
        Button btnChangePwd = (Button)findViewById(R.id.btnChangePwd);

        sharePref = this.getSharedPreferences("appLock", Context.MODE_PRIVATE);
        editor = sharePref.edit();


        btnSetLock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent var3 = new Intent(MainActivity.this, appLock.class);
                var3.putExtra(app_lock_const.type, app_lock_const.ENABLE_PASSLOCK);
                startActivityForResult(var3, app_lock_const.ENABLE_PASSLOCK);
            }
        });

        btnSetDelLock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent var4 = new Intent(MainActivity.this, appLock.class);
                var4.putExtra(app_lock_const.type, app_lock_const.DISABLE_PASSLOCK);
                startActivityForResult(var4, app_lock_const.DISABLE_PASSLOCK);
            }
        });

        btnChangePwd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent var5 = new Intent(MainActivity.this, appLock.class);
                var5.putExtra(app_lock_const.type, app_lock_const.CHANGE_PASSLOCK);
                startActivityForResult(var5, app_lock_const.CHANGE_PASSLOCK);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case app_lock_const.ENABLE_PASSLOCK:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(this, "암호 설정됨", Toast.LENGTH_SHORT).show();
                    init();
                    lock = false;
                }
            case app_lock_const.DISABLE_PASSLOCK:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(this, "암호 삭제됨", Toast.LENGTH_SHORT).show();
                    init();
                    lock = false;
                }
            case app_lock_const.CHANGE_PASSLOCK:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(this, "암호 변경됨", Toast.LENGTH_SHORT).show();
                    init();
                    lock = false;
                }
            case app_lock_const.UNLOCK_PASSWORD:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(this, "암호 해제됨", Toast.LENGTH_SHORT).show();
                    init();
                    lock = false;
                }
        }
    }

    public void onStart() {
        super.onStart();
        if(this.lock && (isPassLockSet())){
            Intent var = new Intent(this, appLock.class);
            var.putExtra(app_lock_const.type,app_lock_const.UNLOCK_PASSWORD);
            MainActivity.this.startActivityForResult(var,app_lock_const.UNLOCK_PASSWORD);
        }
    }

    public void onPause(){
        super.onPause();
            if(isPassLockSet()){
                lock = true;
            }

    }

    private void init(){

        btnSetLock = (Button)findViewById(R.id.btnSetLock);
        btnSetDelLock = (Button)findViewById(R.id.btnSetDelLock);
        btnChangePwd = (Button)findViewById(R.id.btnChangePwd);

        if(isPassLockSet()){
            btnSetLock.setEnabled(false);
            btnSetDelLock.setEnabled(true);
            btnChangePwd.setEnabled(true);
            lock = true;
        }
        else{
            btnSetLock.setEnabled(true);
            btnSetDelLock.setEnabled(false);
            btnChangePwd.setEnabled(false);
            lock = false;
        }
    }

    void setPassLock(String password) {

        editor.putString("appLock", password);
        editor.apply();
    }

    void removePassLock() {
        this.editor.remove("appLock");
        this.editor.apply();
    }

    boolean checkPassLock(String password) {
        return this.sharePref.getString("appLock", "0").equals(password);
    }

    boolean isPassLockSet() {
        if(this.sharePref.contains("appLock")) {
            return true;
        }
        return false;
    }
}

