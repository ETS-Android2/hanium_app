package com.example.app_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;


public class Control extends AppCompatActivity {

    public ImageButton btn_remote;
    public ImageButton pw_menu;
    private ImageButton set_user;
    public ImageButton btn_cam;
    public  ImageButton set_lock_way;

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
       // ImageButton btnopen = findViewById(R.id.btn_open);
        btn_remote = (ImageButton)findViewById(R.id.lockButton);
        pw_menu = (ImageButton) findViewById(R.id.pw_menu);
        set_user = findViewById(R.id.userBotton);
        btn_cam = (ImageButton)findViewById(R.id.Cam_Button);
        set_lock_way = (ImageButton)findViewById(R.id.set_lock_way);

        //drawerLayout = (DrawerLayout) findViewById(R.id.menu); 메뉴바 레이아웃
        //drawerView = (View) findViewById(R.id.drawerView);
        //drawerLayout.setDrawerListener(listener);

        //ET_User = findViewById(R.id.ET_User); MQTT msg 수신 창
        //mqtt_image = findViewById(R.id.mqtt_img); 위험인물 감지시 사진 수신
       /* btnopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btn_open:
                        drawerLayout.openDrawer(drawerView);
                }
            }
        });*/

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

