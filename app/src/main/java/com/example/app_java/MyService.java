package com.example.app_java;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

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

public class MyService extends Service {

    IBinder binder = new MyBinder();
    public byte[] $byteArray;
    public String msg_mcu;

    class MyBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    private MqttAndroidClient mqttAndroidClient;
    private IMqttToken token;

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
            if (TO_MCU != null) {
                Log.e("TO_MCU", TO_MCU);
                try {
                    mqttAndroidClient.publish("TO_MCU", TO_MCU.getBytes(), 0 , false );
                    //버튼을 클릭하면 jmlee 라는 토픽으로 메시지를 보냄
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

        }
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
        mqttAndroidClient.setCallback(new MqttCallback() {  //클라이언트의 콜백을 처리하는부분

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override

            public void messageArrived(String topic, MqttMessage message) throws Exception {    //모든 메시지가 올때 Callback method
                if (topic.equals("common")) {     //topic 별로 분기처리하여 작업을 수행할수도있음
                    String msg = new String(message.getPayload());
                    //msg -->얼굴인식 확인용 msg
                    //Log.e("arrive message : ", msg);
                } else if (topic.equals("picture")) {
                    $byteArray = message.getPayload();
                    showNoti($byteArray, request_from_MCU, change_notice_msg);
                    String response = "OK";
                    try {
                        mqttAndroidClient.publish("pic_response", response.getBytes(), 0, false);
                        //버튼을 클릭하면 jmlee 라는 토픽으로 메시지를 보냄
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                    else if(topic.equals("TO_APP")){
                        msg_mcu = new String(message.getPayload());
                        if(msg_mcu.equals("RPL1\n")){
                            request_from_MCU = true;
                            Log.e("Result","RPL1");
                        }
                        else if(msg_mcu.equals("FAS1\n")){
                            change_notice_msg = true;
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
            intent.putExtra("picture", data);
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
            else {
                builder.setContentTitle("위험 인물 감지!");
                builder.setContentText("금고 주변에 위험 인물이 감지되었습니다.");
            }
        }
        builder.setSmallIcon(R.drawable.notification_icon);

        Notification notification = builder.build();

        manager.notify(1,notification);

    }

}