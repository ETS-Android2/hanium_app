package com.example.app_java;

import android.annotation.SuppressLint;
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
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
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
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    IBinder binder = new MyBinder();
    public byte[] $byteArray;
    public String msg_mcu;

    private String who;
    private String[] Dialog_Arr;
    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private Intent request_intent;
    private String Beacon_check;
    SharedPreferences shar_idx;
    SharedPreferences.Editor shar_idx_editor;

    SharedPreferences share_beacon;
    SharedPreferences.Editor share_beacon_editor;

    SharedPreferences share_beacon_check;
    SharedPreferences.Editor share_beacon_check_editor;

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


    public MyService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        who = "" ;
        Toast.makeText(getApplicationContext(), "서비스 실행", Toast.LENGTH_SHORT).show();
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
                    saveBitmapToJpeg(BitMap_mqtt);

                }
                if(topic.equals("TO_APP")){

                    msg_mcu = new String(message.getPayload());
                    if(msg_mcu.contains("RAU\n")){
                        request_from_MCU = true;
                        Log.e("Result","RAU");
                    }
                    else if(msg_mcu.equals("Danger\n") || msg_mcu.equals("Unknown\n"))
                    {
                        showNoti($byteArray, request_from_MCU);
                    }

                    else if(msg_mcu.contains("RCR")){
                        Log.e("Payload","RCR\n");
                        if(share_beacon_check.getString("Check_Beacon","").equals("IN")) {
                            try {
                                mqttAndroidClient.publish("TO_MCU", "SAR1\n".getBytes(), 0, false);
                                Log.e("RCR ->","SAR1\n");
                            }catch (Exception e){}
                        }else if(share_beacon_check.getString("Check_Beacon","").equals("EXIT")) {
                            try {
                                mqttAndroidClient.publish("TO_MCU", "SAR0\n".getBytes(), 0, false);
                                Log.e("RCR ->","SAR0\n");
                            }catch (Exception e){}
                        }


                    }

                    Log.e("to_app",msg_mcu);
                    showNoti($byteArray, request_from_MCU);

                    request_from_MCU = false;

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
        return START_NOT_STICKY;
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

        share_beacon = this.getSharedPreferences("Beacon", Context.MODE_PRIVATE);
        share_beacon_editor = share_beacon.edit();

        share_beacon_check = this.getSharedPreferences("Check_Beacon", Context.MODE_PRIVATE);
        share_beacon_check_editor = share_beacon_check.edit();

        $byteArray = null;
        token = null;
        msg_mcu = null;
        mqttAndroidClient = new MqttAndroidClient(this, "tcp://" + "54.201.98.240" + ":1883", MqttClient.generateClientId());

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

        Timer timer = new Timer();

        TimerTask reset_beacon_check = new TimerTask() {
            @Override
            public void run() {
                share_beacon_check_editor.putString("Check_Beacon","EXIT");
                share_beacon_check_editor.apply();
                Log.e("Timer","On");
            }
        };
        timer.schedule(reset_beacon_check,0, 60000);
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

    public void showNoti(byte[] data, boolean flag_Request){builder = null; manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(this.msg_mcu.equals("RAU\n")) {
            this.request_intent = new Intent(getApplicationContext(), appLock.class);
            this.request_intent.putExtra(app_lock_const.type, app_lock_const.APP_MCULOCK);
        }
        else if(this.msg_mcu.equals("FISg\n")) {
            //침입 상황
            this.request_intent = new Intent(getApplicationContext(), Control.class);
        }
        else if(this.msg_mcu.equals("FISi\n")) {
            //금고 개방
            this.request_intent = new Intent(getApplicationContext(), Control.class);
        }
        else if(this.msg_mcu.equals("PAD\n")){
            //비밀번호 10회 실패
            this.request_intent = new Intent(getApplicationContext(), Control.class);
        }
        else if(this.msg_mcu.equals("FFB\n")){
            //금고 도난 상황
            this.request_intent = new Intent(getApplicationContext(), Control.class);
        }
        else if(this.msg_mcu.equals("Danger\n")) {
            this.request_intent = new Intent(getApplicationContext(), Control.class);
        }
        else if(this.msg_mcu.equals("Unknown\n")) {
            this.request_intent = new Intent(getApplicationContext(), Control.class);
        }

        PendingIntent mPendingIntent = PendingIntent.getActivity(
                MyService.this,
                0, // 보통 default값 0을 삽입
                this.request_intent,
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



        if(this.msg_mcu.equals("RAU\n")) {
                builder.setContentTitle("잠금해제 요청");
                builder.setContentText("금고로부터 어플로 잠금해제 요청입니다!");
        }
        else if(this.msg_mcu.equals("FISg\n")) {
                builder.setContentTitle("침입 상황");
                builder.setContentText("누군가 금고 손잡이를 강하게 당기고 있습니다!");
        }
        else if(this.msg_mcu.equals("FISi\n")) {
                builder.setContentTitle("금고 개방");
                builder.setContentText("누군가 강제로 금고를 개방하였습니다!");
        }
        else if(this.msg_mcu.equals("PAD\n")){
                builder.setContentTitle("비밀번호 10회 실패");
                builder.setContentText("키패드 잠금 보안 비밀번호를 10회 이상 실패하셨습니다!");
        }
        else if(this.msg_mcu.equals("FFB\n")){
                builder.setContentTitle("금고 도난 상황");
                builder.setContentText("현재 금고가 도난 당하고 있습니다!");
        }
        else if(this.msg_mcu.equals("Danger\n")) {
                builder.setContentTitle("위험 인물 감지!");
                builder.setContentText("금고 주변에 위험 인물이 감지되었습니다.");
        }
        else if(this.msg_mcu.equals("Unknown\n")) {
            builder.setContentTitle("미등록 인물 감지!");
            builder.setContentText("금고 주변에 미등록 인물이 감지되었습니다.");
        }

        builder.setAutoCancel(true);

        builder.setSmallIcon(R.drawable.noti);

        Notification notification = builder.build();

        manager.notify(1,notification);

        Vibrator vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vib.vibrate(1000);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE );
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG" );
        wakeLock.acquire(3000);


    }

    public void saveBitmapToJpeg(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        int idx = this.shar_idx.getInt("file_idx",0);
        String[] file_name = new String[idx + 1];
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        String getTime = mFormat.format(mDate);

        if(msg_mcu.equals("Danger\n")){
            who = "Danger";    // 파일 경로와 이름 넣기

        }
        else if(msg_mcu.equals("Unknown\n")){
            who = "Unknown";    // 파일 경로와 이름 넣기

        }
        File tempFile = new File(getCacheDir(), who + getTime);
        file_name[idx] = who + getTime;

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
            Toast.makeText(getApplicationContext(), "위험 인물 파일 저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "위험 인물 파일 저장 실패", Toast.LENGTH_SHORT).show();
            idx --;
            shar_idx_editor.putInt("file_idx", idx);
            shar_idx_editor.apply();
        }
    }

}
