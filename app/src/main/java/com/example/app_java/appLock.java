package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.app_java.R;
import com.example.app_java.app_lock_const;

import java.util.Random;

public class appLock extends AppCompatActivity {

    private String oldPwd = "";
    private String newpwd = "";
    private int pwd_index;
    private boolean changePwdUnlock = false;
    private boolean changepwddoorlock = false;
    private Animation anim;


    SharedPreferences sharePref;
    SharedPreferences sharePref_door;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor_door;
    EditText et_pwd;
    Button pwd_set_btn;
    ImageButton Visible_btn;
    TextView Info;
    int currentValue;
    private String pwd;
    private Button[] btn_array = null;
    private int btnum;
    private int randnum1 ,randnum2;
    private Random rand = new Random();
    private String fake_num;
    private String door_pwd;

    private long mLastClickTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);

        sharePref = this.getSharedPreferences("appLock", Context.MODE_PRIVATE);
        sharePref_door = this.getSharedPreferences("doorLock",Context.MODE_PRIVATE);
        editor = sharePref.edit();
        editor_door = sharePref_door.edit();

        et_pwd = (EditText)findViewById(R.id.text_pwd);
        pwd_set_btn = (Button)findViewById(R.id.set_pwd);
        Visible_btn = (ImageButton)findViewById(R.id.visible_btn);
        Info = (TextView)findViewById(R.id.etInputInfo);
        btn_array = new Button[12];
        btnum = 0;
        pwd_index = 0;
        fake_num = "";
        pwd = "";
        door_pwd = "";

        int[] btn_id = {
                R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,
                R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9,R.id.btnClear,R.id.btnErase
        };
        for( btnum = 0 ; btnum < 12 ; btnum++){
            this.btn_array[btnum] = findViewById(btn_id[btnum]);
        }

        anim = new AlphaAnimation(0.0f,1.0f);
        anim.setDuration(1000);
        anim.setStartOffset(0);

        pwd_set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pwd = inputPassword();
                int type = getIntent().getIntExtra("type", 0);
                inputType(type);
            }
        });
        Visible_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int status = event.getAction();
                et_pwd.setText(fake_num);

                if(status == MotionEvent.ACTION_DOWN){
                    et_pwd.setText(pwd);
                    return true;
                }
                return false;
            }
        });




    }

    public void Rand_ani(int btn){

        btn_array[randnum1].clearAnimation();
        btn_array[randnum2].clearAnimation();

        String strcurrnetvalue = String.valueOf(btn);
        pwd = pwd + strcurrnetvalue;
        fake_num = fake_num + "*";
        pwd_index++;
        randnum1 = rand.nextInt(12);
        randnum2 = rand.nextInt(12);

        btn_array[randnum1].startAnimation(anim);
        btn_array[randnum2].startAnimation(anim);
        Log.e("num",strcurrnetvalue);
    }

    public void onSingleClick(View view) {

        if(pwd == ""){
            pwd_index = 0;
        }

        switch (view.getId()) {
            case R.id.btn0:
                currentValue = 0;
                Rand_ani(currentValue);
                break;
            case R.id.btn1:
                currentValue = 1;
                Rand_ani(currentValue);
                break;
            case R.id.btn2:
                currentValue = 2;
                Rand_ani(currentValue);
                break;
            case R.id.btn3:
                currentValue = 3;
                Rand_ani(currentValue);
                break;
            case R.id.btn4:
                currentValue = 4;
                Rand_ani(currentValue);
                break;
            case R.id.btn5:
                currentValue = 5;
                Rand_ani(currentValue);
                break;
            case R.id.btn6:
                currentValue = 6;
                Rand_ani(currentValue);
                break;
            case R.id.btn7:
                currentValue = 7;
                Rand_ani(currentValue);
                break;
            case R.id.btn8:
                currentValue = 8;
                Rand_ani(currentValue);
                break;
            case R.id.btn9:
                currentValue = 9;
                Rand_ani(currentValue);
                break;
            case R.id.btnClear:
                pwd = "";
                fake_num = "";
                pwd_index = 0;
                break;
            case R.id.btnErase:
                //onDeleteKey();
                if(pwd_index == 1 ){
                    pwd = "";
                    fake_num = "";
                    pwd_index = pwd_index - 1;
                    break;
                }else if(pwd_index > 1){
                    fake_num = fake_num.substring(0, pwd_index - 1);
                    pwd = pwd.substring(0, pwd_index - 1);
                    pwd_index = pwd_index - 1;
                    break;
                }
        }

        et_pwd.setText(fake_num);

    }
/*
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
*/




    private void onClear() {
        pwd = "";
        fake_num = "";
        et_pwd.setText("");
    }

    public String inputPassword(){
        return pwd;
    }

    private  void inputType(int type) {

        if (isPassLockSet() && (type != app_lock_const.CHANGE_PASSLOCK)) {//폰으로는 !isPassLockSet()으로
            if (checkPassLock(pwd)) {
                Intent intent = new Intent(appLock.this, Control.class);
                startActivity(intent);
            } else {
                Info.setText("비밀번호가 일치하지 않습니다"+type);
                onClear();
            }
        } else {

            if (type == app_lock_const.ENABLE_PASSLOCK) {
                if (oldPwd.isEmpty()) {
                    oldPwd = inputPassword();
                    onClear();
                    Info.setText("다시 한번 입력");
                } else {
                    if (oldPwd.equals(inputPassword())) {
                        setPassLock(oldPwd);
                        setResult(Activity.RESULT_OK);
                        Info.setText("비밀번호 설정 완료");
                        Intent Service = new Intent(appLock.this, MyService.class);
                        startService(Service);//서비스 시작
                        Log.e("Service", "서비스 실행");
                        Intent Control = new Intent(appLock.this, Control.class);
                        startActivity(Control);//메인 화면 이동
                    } else {
                        onClear();
                        Info.setText("비밀번호가 일치하지 않습니다11");
                    }
                }
            } else if (type == app_lock_const.DISABLE_PASSLOCK) {
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
////////금고 비활성화
//
//        else if (type == app_lock_const.UNLOCK_PASSWORD) {
//            if (checkPassLock(inputPassword())) {
//                setResult(Activity.RESULT_OK);
//                Intent Control = new Intent(appLock.this, Control.class);
//                startActivity(Control);
//            } else {
//                Info.setText("비밀번호가 틀립니다 (UNLOCK_PASSWORD)");
//                onClear();
//            }
//        }
        }

        if (type == app_lock_const.CHANGE_PASSLOCK) {
            if (checkPassLock(inputPassword()) && !changePwdUnlock) {
                changePwdUnlock = true;
                newpwd = inputPassword();
                onClear();
                Info.setText("새로운 비밀번호 입력");
            } else if (checkPassLock(newpwd)) {
                changePwdUnlock = true;
                newpwd = inputPassword();
                onClear();
                Info.setText("다시 한번 입력");
            } else {
                if (newpwd.equals(inputPassword())) {
                    setPassLock(inputPassword());
                    setResult(Activity.RESULT_OK);
                    Intent Control = new Intent(appLock.this, Control.class);
                    startActivity(Control);
                } else {
                    changePwdUnlock = false;
                    oldPwd = inputPassword();
                    onClear();
                    Info.setText("비밀번호가 일치하지 않습니다33");
                }
            }
        }

        if(type == app_lock_const.SET_TOUCHPAD){
            if(changepwddoorlock == false) {

                Log.e("door_pwd","w" + pwd);
                onClear();
                Info.setText("다시한번 입력");
                changepwddoorlock = true;
            }else {
                if (pwd.equals(inputPassword())) {
                    Intent set_door_pwd = new Intent(appLock.this, MyService.class);
                    set_door_pwd.putExtra("TO_MCU", "SPP" + pwd + "\\n");
                    startService(set_door_pwd);
                    Intent home = new Intent(appLock.this, Control.class);
                    startActivity(home);
                } else {
                    Info.setText("비밀번호가 일치하지 않습니다.");
                    onClear();
                }
            }
        }
//         else {
//            Info.setText("비밀번호가 틀립니다" + type);
//            changePwdUnlock = false;
//            onClear();
//        }
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


