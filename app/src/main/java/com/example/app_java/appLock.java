package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class appLock extends AppCompatActivity {

    private String oldPwd = "";
    private String newpwd = "";
    String[] pwd = new String[4];
    private boolean changePwdUnlock = false;

    SharedPreferences sharePref;
    SharedPreferences.Editor editor;
    ImageView IV1;
    ImageView IV2;
    ImageView IV3;
    ImageView IV4;
    TextView Info;
    int currentValue = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);

        sharePref = this.getSharedPreferences("appLock", Context.MODE_PRIVATE);
        editor = sharePref.edit();

        IV1 = (ImageView)findViewById(R.id.ImageView1);
        IV2 = (ImageView)findViewById(R.id.ImageView2);
        IV3 = (ImageView)findViewById(R.id.ImageView3);
        IV4 = (ImageView)findViewById(R.id.ImageView4);
        Info = (TextView)findViewById(R.id.etInputInfo);
    }


    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.btn0:
                currentValue = 0;
                break;
            case R.id.btn1:
                currentValue = 1;
                break;
            case R.id.btn2:
                currentValue = 2;
                break;
            case R.id.btn3:
                currentValue = 3;
                break;
            case R.id.btn4:
                currentValue = 4;
                break;
            case R.id.btn5:
                currentValue = 5;
                break;
            case R.id.btn6:
                currentValue = 6;
                break;
            case R.id.btn7:
                currentValue = 7;
                break;
            case R.id.btn8:
                currentValue = 8;
                break;
            case R.id.btn9:
                currentValue = 9;
                break;
            case R.id.btnClear:
                onClear();
            case R.id.btnErase:
                onDeleteKey();
        }

        String strCurrentValue = String.valueOf(currentValue);
        IV1.requestFocus();


        if(currentValue != -1) {
            if(pwd[0] == null && pwd[1] == null && pwd[2] == null && pwd[3] == null ) {
                //setEditText(Et1, Et2, strCurrentValue);
                pwd[0] = strCurrentValue;
                IV1.setImageResource(R.drawable.safe_open);
                IV2.requestFocus();
                pwd[1] = null;
            }
            else if(!(pwd[0] == null) && pwd[1] == null && pwd[2] == null && pwd[3] == null) {
                //setEditText(Et2, Et3, strCurrentValue);
                pwd[1] = strCurrentValue;
                IV2.setImageResource(R.drawable.safe_open);
                IV3.requestFocus();
                pwd[2] = null;
            }
            else if(!(pwd[0] == null) && !(pwd[1] == null) && pwd[2] == null && pwd[3] == null) {
                //(Et3, Et4, strCurrentValue);
                pwd[2] = strCurrentValue;
                IV3.setImageResource(R.drawable.safe_open);
                IV4.requestFocus();
                pwd[3] = null;
            }
            else if(!(pwd[0] == null) && !(pwd[1] == null) && !(pwd[2] == null) && pwd[3] == null) {
                IV4.setImageResource(R.drawable.safe_open);
                pwd[3] = strCurrentValue;
            }
        }

        if(!(pwd[0] == null) && !(pwd[1] == null) && !(pwd[2] == null) && !(pwd[3] == null) ){
            int type = this.getIntent().getIntExtra("type",0);
            inputType(type);
        }

    }
    private void onDeleteKey(){
        currentValue = -1;
        if(IV1.isFocused()){
            pwd[0] = null;
        }
        else if(IV2.isFocused()){
            pwd[0] = null;
            IV1.requestFocus();
        }
        else if(IV3.isFocused()){
            pwd[1] = null;
            IV2.requestFocus();
        }
        else if(IV4.isFocused()){
            pwd[2] = null;
            IV3.requestFocus();
        }
    }

    private void onClear() {
        pwd[0] = null;
        pwd[1] = null;
        pwd[2] = null;
        pwd[3] = null;
        IV1.setImageResource(R.drawable.safe_close);
        IV2.setImageResource(R.drawable.safe_close);
        IV3.setImageResource(R.drawable.safe_close);
        IV4.setImageResource(R.drawable.safe_close);
        IV1.requestFocus();
    }

    public String inputPassword(){
        return pwd[0] + pwd[1] + pwd[2] + pwd[3];
    }

    private  void inputType(int type) {

        if (type == app_lock_const.ENABLE_PASSLOCK) {
            if (oldPwd.isEmpty()) {
                oldPwd = inputPassword();
                onClear();
                Info.setText("다시 한번 입력");
            }  else {
                if (oldPwd.equals(inputPassword())) {
                    setPassLock(oldPwd);
                    setResult(Activity.RESULT_OK);
                    Info.setText("비밀번호 설정 완료");
                    Intent Control = new Intent(appLock.this, Control.class);
                    startActivity(Control);
                } else{
                    onClear();
                    Info.setText("비밀번호가 일치하지 않습니다");
                }
            }
        }
/*
        else if (type == app_lock_const.DISABLE_PASSLOCK) {
            if (isPassLockSet()) {
                if (checkPassLock(inputPassword())) {
                    removePassLock();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Info.setText("비밀번호가 틀립니다 (DISABLE_PASSLOCK)");
                    onClear();
                }
            } else {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }
*/////////금고 비활성화
        else if (type == app_lock_const.UNLOCK_PASSWORD) {
            if (checkPassLock(inputPassword())) {
                setResult(Activity.RESULT_OK);
                Intent Control = new Intent(appLock.this, Control.class);
                startActivity(Control);
            } else {
                Info.setText("비밀번호가 틀립니다 (UNLOCK_PASSWORD)");
                onClear();
            }
        }

        else if (type == app_lock_const.CHANGE_PASSLOCK) {
            if (checkPassLock(inputPassword()) && !changePwdUnlock) {
                changePwdUnlock = true;
                newpwd = inputPassword();
                onClear();
                Info.setText("새로운 비밀번호 입력");
            }
            else if(checkPassLock(newpwd)){
                changePwdUnlock = true;
                newpwd = inputPassword();
                onClear();
                Info.setText("다시 한번 입력");
            }
            else{
                if(newpwd.equals(inputPassword())){
                    setPassLock(inputPassword());
                    setResult(Activity.RESULT_OK);
                    Intent Control = new Intent(appLock.this, Control.class);
                    startActivity(Control);
                }else{
                    changePwdUnlock = false;
                    oldPwd = inputPassword();
                    onClear();
                    Info.setText("비밀번호가 일치하지 않습니다");
                }
            }
        }
                else{
                Info.setText("비밀번호가 틀립니다" + type);
                changePwdUnlock = false;
                onClear();
            }
    }

    void setPassLock(String password) {
        this.editor.putString("appLock", password);
        this.editor.apply();
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


