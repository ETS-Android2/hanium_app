package com.example.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;



public class Control extends AppCompatActivity {

    public ImageButton btn_remote;
    public ImageButton pw_menu;
    private ImageButton set_user;
    public ImageButton btn_cam;
    public  ImageButton set_lock_way;
    private Button connect_safe;


    MyService ms;
    boolean isService = false;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder mb = (MyService.MyBinder)service;
            ms = mb.getService();
            isService = true;
            Log.e("메세지","서비스에 연결됨");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent service = new Intent(Control.this, MyService.class);
        bindService(service, conn, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.activity_control);
        btn_remote = (ImageButton) findViewById(R.id.lockButton);
        pw_menu = (ImageButton) findViewById(R.id.pw_menu);
        set_user = findViewById(R.id.userBotton);
        btn_cam = (ImageButton) findViewById(R.id.Cam_Button);
        set_lock_way = (ImageButton) findViewById(R.id.set_lock_way);
        connect_safe = (Button) findViewById(R.id.btn_connect_safe);

        set_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set_user_intent = new Intent(Control.this, User_seting.class);
                startActivity(set_user_intent);
            }
        });


        pw_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Control.this, Set_Pwd.class);
                startActivity(intent);
            }
        });


        btn_remote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Control.this, remote_lock.class);
                startActivity(intent);
            }
        });

        btn_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set_user_intent = new Intent(Control.this, Danger.class);
                startActivity(set_user_intent);
            }
        });

        set_lock_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set_lock_way = new Intent(Control.this, lock_way.class);
                startActivity(set_lock_way);
            }
        });


//        connect_safe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent connect = new Intent(Control.this, paring_bt.class);
//                startActivity(connect);
//            }
//        });
    }

/*
    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            drawerLayout.openDrawer(drawerView);
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    };
*/


/*
    public void showNoti(){ builder = null; manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT) );
        builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        }else{
            builder = new NotificationCompat.Builder(this);
        }
        builder.setContentTitle("위험 인물 감지!");

        builder.setContentText("금고 주변에 위험 인물이 감지되었습니다.");

        builder.setSmallIcon(R.drawable.notification_icon);

        Notification notification = builder.build();

        manager.notify(1,notification);

    }

    public byte[] bitmapToByteArray( Bitmap bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

*/
}

