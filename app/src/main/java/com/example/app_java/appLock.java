package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class appLock extends AppCompatActivity {

    private String oldPwd = "";
    private String Change_str;
    private boolean changePwdUnlock = false;

    SharedPreferences sharePref;
    SharedPreferences.Editor editor;
    EditText Et1;
    EditText Et2;
    EditText Et3;
    EditText Et4;
    TextView Info;
    int currentValue = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);

        sharePref = this.getSharedPreferences("appLock", Context.MODE_PRIVATE);
        editor = sharePref.edit();

        Et1 = (EditText)findViewById(R.id.etPasscode1);
        Et2 = (EditText)findViewById(R.id.etPasscode2);
        Et3 = (EditText)findViewById(R.id.etPasscode3);
        Et4 = (EditText)findViewById(R.id.etPasscode4);
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

        if(currentValue != -1) {
            if(Et1.isFocused()) {
                //setEditText(Et1, Et2, strCurrentValue);
                Et1.setText(strCurrentValue);
                Et2.requestFocus();
                Et2.setText("");
            }
            else if(Et2.isFocused()) {
                //setEditText(Et2, Et3, strCurrentValue);
                Et2.setText(strCurrentValue);
                Et3.requestFocus();
                Et3.setText("");
            }
            else if(Et3.isFocused()) {
                //(Et3, Et4, strCurrentValue);
                Et3.setText(strCurrentValue);
                Et4.requestFocus();
                Et4.setText("");
            }
            else if(Et4.isFocused()) {
                Et4.setText(strCurrentValue);
            }
        }

        if(Et4.getText().toString().length() != 0 && Et3.getText().toString().length() != 0 && Et2.getText().toString().length() != 0 && Et1.getText().toString().length() != 0 ){
            int type = this.getIntent().getIntExtra("type",0);
            inputType(type);
        }

    }
    private void onDeleteKey(){
        currentValue = -1;
        if(Et1.isFocused()){
            Et1.setText("");
        }
        else if(Et2.isFocused()){
            Et1.setText("");
            Et1.requestFocus();
        }
        else if(Et3.isFocused()){
            Et2.setText("");
            Et2.requestFocus();
        }
        else if(Et4.isFocused()){
            Et3.setText("");
            Et3.requestFocus();
        }
    }

    private void onClear() {
        Et1.setText("");
        Et2.setText("");
        Et3.setText("");
        Et4.setText("");
        Et1.requestFocus();
    }

    public String inputPassword(){
        String str1 = Et1.getText().toString();
        String str2 = Et2.getText().toString();
        String str3 = Et3.getText().toString();
        String str4 = Et4.getText().toString();

        String result =str1+str2+str3+str4;
        return result;
    }
/*
    private void setEditText(EditText currentEditText,EditText nextEditText,String strCurrentValue){
        currentEditText.setText(strCurrentValue);
        nextEditText.requestFocus();
        nextEditText.setText("");
    }*/

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
                oldPwd = inputPassword();
                onClear();
                Info.setText("새로운 비밀번호 입력");
            }
            else if(checkPassLock(oldPwd)){
                oldPwd = inputPassword();
                onClear();
                Info.setText("다시 한번 입력");
            }
            else{
                if(oldPwd.equals(inputPassword())){
                    setPassLock(inputPassword());
                    setResult(Activity.RESULT_OK);
                    finish();
                }else{
                    changePwdUnlock = false;
                    oldPwd = inputPassword();
                    onClear();
                    Info.setText("비밀번호가 일치하지 않습니다");
                }
            }
        }
                else{
                Info.setText("비밀번호가 틀립니다");
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


