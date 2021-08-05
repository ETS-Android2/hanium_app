package com.example.app_java;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User_seting extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;
    private MqttAndroidClient mqttAndroidClient;
    private IMqttToken token;
    private Button set_user_btn;
    private Button btn_picture;
    private ImageButton btn_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_seting);
        token = null;
        set_user_btn = findViewById(R.id.set_user);
        btn_picture = findViewById(R.id.picture);
        btn_home = (ImageButton)findViewById(R.id.homeButton);
        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(User_seting.this, Manifest.permission.CAMERA);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(User_seting.this, new String[]{Manifest.permission.CAMERA}, 0);
                } else {
                    sendTakePhotoIntent();
                }

            }
        });

        mqttAndroidClient = new MqttAndroidClient(this,  "tcp://" + "54.185.18.26" + ":1883", MqttClient.generateClientId());

        // 2번째 파라메터 : 브로커의 ip 주소 , 3번째 파라메터 : client 의 id를 지정함 여기서는 paho 의 자동으로 id를 만들어주는것
        try {
            token = mqttAndroidClient.connect(getMqttConnectionOption());    //mqtttoken 이라는것을 만들어 connect option을 달아줌
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttAndroidClient.setBufferOpts(getDisconnectedBufferOptions());    //연결에 성공한경우

                    Log.e("Connect_success", "Success");
                    try {
                        mqttAndroidClient.subscribe("common", 0 );   //연결에 성공하면 common 라는 토픽으로 subscribe함
                        mqttAndroidClient.subscribe("picture",0);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {   //연결에 실패한경우
                    Log.e("connect_fail", "Failure " + exception.toString());
                }
            });
        } catch (MqttException e)
        {
            e.printStackTrace();
        }
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_home = new Intent(User_seting.this, Control.class );
                startActivity(intent_home);
            }
        });


        set_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                ExifInterface exif = null;

                try {
                    exif = new ExifInterface(imageFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int exifOrientation;
                int exifDegree;

                if (exif != null) {
                    exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    exifDegree = exifOrientationToDegrees(exifOrientation);
                } else {
                    exifDegree = 0;
                }
                try {
                    mqttAndroidClient.publish("set_user", bitmapToByteArray(rotate(bitmap, exifDegree)), 0 , false );
                    //버튼을 클릭하면 jmlee 라는 토픽으로 메시지를 보냄
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

    }


        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResult){
            super.onRequestPermissionsResult(requestCode, permissions, grantResult);
            if (requestCode == 0) {
                if (grantResult[0] == 0) {
                    Toast.makeText(this, "카메라 권한이 승인됨", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "카메라 권한이 거절되었습니다.카메라를 이용하려면 권한을 승낙하여야 합니다",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){

            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                ExifInterface exif = null;

                try {
                    exif = new ExifInterface(imageFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int exifOrientation;
                int exifDegree;

                if (exif != null) {
                    exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    exifDegree = exifOrientationToDegrees(exifOrientation);
                } else {
                    exifDegree = 0;
                }

                ((ImageView) findViewById(R.id.photo)).setImageBitmap(rotate(bitmap, exifDegree));

            }
        }


        private int exifOrientationToDegrees ( int exifOrientation){
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
            }
            return 0;
        }

        private Bitmap rotate (Bitmap bitmap,float degree){
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }


        private void sendTakePhotoIntent () {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }

                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }

        private File createImageFile () throws IOException {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "TEST_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,      /* prefix */
                    ".jpg",         /* suffix */
                    storageDir          /* directory */
            );
            imageFilePath = image.getAbsolutePath();
            return image;
        }

        public byte[] bitmapToByteArray (Bitmap bitmap ){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
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


    }