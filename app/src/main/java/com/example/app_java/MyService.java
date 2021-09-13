package com.example.app_java;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service {

    IBinder binder = new MyBinder();
    public byte[] $byteArray;
    public String msg_mcu;

    private String[] Dialog_Arr;
    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    SharedPreferences shar_idx;
    SharedPreferences.Editor shar_idx_editor;



    class MyBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    public static MqttAndroidClient mqttAndroidClient;
    public IMqttToken token;

    NotificationManager manager;
    NotificationCompat.Builder builder;


    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";

    private boolean request_from_MCU = false;
    private boolean change_notice_msg = false;

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_STICKY;//서비스가 종료되도 다시 자동 실행
        } else {
            String TO_MCU = intent.getStringExtra("TO_MCU");
//            byte[] set_face_user = intent.getByteArrayExtra("set_face_user");
//            byte[] set_face_danger = intent.getByteArrayExtra("set_face_danger");
            if (TO_MCU != null) {
                Log.e("TO_MCU", TO_MCU);
                try {
                    mqttAndroidClient.publish("TO_MCU", TO_MCU.getBytes(), 0 , false );
                    //버튼을 클릭하면 jmlee 라는 토픽으로 메시지를 보냄
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

//            if (set_face_user != null) {
//                Log.e("set_face_user", "Send!");
//                try {
//                    mqttAndroidClient.publish("set_face_user", set_face_user, 0 , false );
//                    //버튼을 클릭하면 jmlee 라는 토픽으로 메시지를 보냄
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if (set_face_danger != null) {
//                Log.e("set_face_danger", "Send!");
//                try {
//                    mqttAndroidClient.publish("set_face_danger", set_face_danger, 0 , false );
//                    //버튼을 클릭하면 jmlee 라는 토픽으로 메시지를 보냄
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        mqttAndroidClient.setCallback(new MqttCallback() {  //클라이언트의 콜백을 처리하는부분

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override

            public void messageArrived(String topic, MqttMessage message) throws Exception {    //모든 메시지가 올때 Callback method
//                if (topic.equals("common")) {     //topic 별로 분기처리하여 작업을 수행할수도있음
//                    String msg = new String(message.getPayload());
//                    //msg -->얼굴인식 확인용 msg
//                    //Log.e("arrive message : ", msg);}
                if (topic.equals("picture")) {
                    $byteArray = message.getPayload();
                    Bitmap BitMap_mqtt = BitmapFactory.decodeByteArray($byteArray, 0, $byteArray.length);

                }
                else if(topic.equals("TO_APP")){

                    msg_mcu = new String(message.getPayload());
                    if(msg_mcu.equals("RAU\n")){
                        request_from_MCU = true;
                        Log.e("Result","RAU");
                    }
                    else if(msg_mcu.equals("FIS\n")){
                        change_notice_msg = true;
                    }
                    else if(msg_mcu.equals("Danger\n"))
                    {
                        showNoti($byteArray, request_from_MCU, change_notice_msg);
                    }

                    Log.e("to_app",msg_mcu);
                    showNoti($byteArray, request_from_MCU, change_notice_msg);

                    request_from_MCU = false;
                    change_notice_msg = false;
                }
//                    Intent danger_data = new Intent(MyService.this, Danger.class);
//                    danger_data.putExtra("picture",$byteArray);
//                    startActivity(danger_data);
                //bitmap --> 위험인물 이미지

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        shar_idx = this.getSharedPreferences("file_idx", Context.MODE_PRIVATE);
        shar_idx_editor = shar_idx.edit();

        $byteArray = null;
        token = null;
        msg_mcu = null;
        mqttAndroidClient = new MqttAndroidClient(this, "tcp://" + "54.185.18.26" + ":1883", MqttClient.generateClientId());

        // 2번째 파라메터 : 브로커의 ip 주소 , 3번째 파라메터 : client 의 id를 지정함 여기서는 paho 의 자동으로 id를 만들어주는것
        try {
            token = mqttAndroidClient.connect(getMqttConnectionOption());    //mqtttoken 이라는것을 만들어 connect option을 달아줌
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttAndroidClient.setBufferOpts(getDisconnectedBufferOptions());    //연결에 성공한경우
                    Log.e("Connect_success", "Success");
                    try {
                        mqttAndroidClient.subscribe("common", 0);   //연결에 성공하면 common 라는 토픽으로 subscribe함
                        mqttAndroidClient.subscribe("picture", 0);
                        mqttAndroidClient.subscribe("TO_APP",0);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {   //연결에 실패한경우
                    Log.e("connect_fail", "Failure " + exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

/*
        try {
            mqttAndroidClient.subscribe("common", 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
*/

    }


    private DisconnectedBufferOptions getDisconnectedBufferOptions() {

        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();

        disconnectedBufferOptions.setBufferEnabled(true);

        disconnectedBufferOptions.setBufferSize(100);

        disconnectedBufferOptions.setPersistBuffer(true);

        disconnectedBufferOptions.setDeleteOldestMessages(false);

        return disconnectedBufferOptions;

    }





    private MqttConnectOptions getMqttConnectionOption() {

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

        mqttConnectOptions.setCleanSession(false);

        mqttConnectOptions.setAutomaticReconnect(true);

        mqttConnectOptions.setWill("aaa", "I am going offline".getBytes(), 1, true);

        return mqttConnectOptions;

    }

    public void showNoti(byte[] data, boolean flag_Request, boolean flag_access){builder = null; manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent;
        if(flag_Request == true){
            intent = new Intent(getApplicationContext(), appLock.class);
            intent.putExtra(app_lock_const.type, app_lock_const.APP_MCULOCK);
        }else {
            intent = new Intent(getApplicationContext(), Danger.class);
        }

        PendingIntent mPendingIntent = PendingIntent.getActivity(
                MyService.this,
                0, // 보통 default값 0을 삽입
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT) );
            builder = new NotificationCompat.Builder(this,CHANNEL_ID);

        }else{
            builder = new NotificationCompat.Builder(this);
        }
        builder.setContentIntent(mPendingIntent);

        if(flag_Request == true){
            builder.setContentTitle("잠금해제 요청");
            builder.setContentText("금고로부터 어플로 잠금해제 요청입니다!");
        }else {
            if(flag_access == true){
                builder.setContentTitle("강제 접근");
                builder.setContentText("누군가 금고에 강제로 접근하려 합니다!.");
            }
            else{
                builder.setContentTitle("위험 인물 감지!");
                builder.setContentText("금고 주변에 위험 인물이 감지되었습니다.");
            }
        }
        builder.setAutoCancel(true);

        builder.setSmallIcon(R.drawable.noti);

        Notification notification = builder.build();

        manager.notify(1,notification);

    }

    public void saveBitmapToJpeg(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        int idx = this.shar_idx.getInt("file_idx",0);
        String[] file_name = new String[idx];
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        String getTime = mFormat.format(mDate);
        File tempFile = new File(getCacheDir(), "Danger" + getTime);    // 파일 경로와 이름 넣기
        file_name[idx] = "Danger" + getTime;
        idx++;
        shar_idx_editor.putInt("file_idx", idx);
        shar_idx_editor.apply();
        Toast.makeText(getApplicationContext(),String.valueOf(idx),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),file_name[idx - 1],Toast.LENGTH_LONG).show();
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
            Toast.makeText(getApplicationContext(), "파일 저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
            idx --;
            shar_idx_editor.putInt("file_idx", idx);
            shar_idx_editor.apply();
        }
    }

}