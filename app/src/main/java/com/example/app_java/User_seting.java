package com.example.app_java;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User_seting extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private static final int REQUEST_CODE_Gallery = 0;
    private String imageFilePath;
    private Uri photoUri;

    private JSONObject jobj = new JSONObject();

    private Button btn_take_picture;
    private Button btn_take_gallery;
    private Button set_danger_btn;
    private Button set_user_btn;
    private ImageButton btn_home;
    private ImageView view_setimage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_seting);

        set_user_btn = findViewById(R.id.set_user_btn);
        set_danger_btn = findViewById(R.id.set_danger_btn);
        btn_take_picture = findViewById(R.id.picture);
        btn_take_gallery = findViewById(R.id.gallery);
        btn_home = (ImageButton)findViewById(R.id.homeButton);
        view_setimage = (ImageView)findViewById(R.id.photo);


        init();


        btn_take_picture.setOnClickListener(new View.OnClickListener() {
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

        btn_take_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_Gallery);
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_home = new Intent(User_seting.this, Control.class );
                startActivity(intent_home);
            }
        });


        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_home = new Intent(User_seting.this, Control.class );
                startActivity(intent_home);
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_home = new Intent(User_seting.this, Control.class );
                startActivity(intent_home);
            }
        });

    }

    public void onClick(View view) {
        BitmapDrawable drawable = (BitmapDrawable) view_setimage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        switch (view.getId()){
            case R.id.set_user_btn:
                //set_user_intent.putExtra("set_face_user",bitmapToByteArray(bitmap));/*
                /*
                try {
                  this.jobj.put("Data",bitmapToByteArray(bitmap));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                data pasrsing (json)*/
                try {
                    MyService.mqttAndroidClient.publish("set_person_user", bitmapToByteArray(bitmap), 0 , false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Log.e("사용자 설정","Clicked");
                Toast.makeText(getApplicationContext(),"설정이 완료되었습니다",Toast.LENGTH_LONG).show();
                break;

            case R.id.set_danger_btn:
                /*
                try {
                    this.jobj.put("Target","Danger");
                    this.jobj.put("Data",bitmapToByteArray(bitmap));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                data pasrsing (json)*/

                //set_danger_intent.putExtra("set_face_danger",bitmapToByteArray(bitmap));
                try {
//                    MyService.mqttAndroidClient.publish("set_person",jobj.toString().getBytes(), 0 , false);
                    MyService.mqttAndroidClient.publish("set_person_danger",bitmapToByteArray(bitmap), 0 , false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                Log.e("위험인물 설정 설정","Clicked");
                Toast.makeText(getApplicationContext(),"설정이 완료되었습니다",Toast.LENGTH_LONG).show();
                break;
        }
        this.init();
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
                set_user_btn.setEnabled(true);
                set_user_btn.setVisibility(View.VISIBLE);
                set_danger_btn.setEnabled(true);
                set_danger_btn.setVisibility(View.VISIBLE);

            }

            if(requestCode == REQUEST_CODE_Gallery)
            {
                if(resultCode == RESULT_OK)
                {
                    try{
                        InputStream in = getContentResolver().openInputStream(data.getData());

                        Bitmap img = BitmapFactory.decodeStream(in);

                        ExifInterface exif = null;
                        try {
                            exif = new ExifInterface(String.valueOf(img));
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

                        in.close();

                        ((ImageView) findViewById(R.id.photo)).setImageBitmap(img);
                        ((ImageView) findViewById(R.id.photo)).setImageBitmap(rotate(img,exifDegree));
                        set_user_btn.setEnabled(true);
                        set_user_btn.setVisibility(View.VISIBLE);
                        set_danger_btn.setEnabled(true);
                        set_danger_btn.setVisibility(View.VISIBLE);
                    }catch(Exception e)
                    {
                        Toast.makeText(this, "갤러리 접근 중 오류 발생", Toast.LENGTH_LONG).show();

                    }
                }
                else if(resultCode == RESULT_CANCELED)
                {
                    Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
                }
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

        private void  init(){
            set_user_btn.setEnabled(false);
            set_user_btn.setVisibility(View.INVISIBLE);
            set_danger_btn.setEnabled(false);
            set_danger_btn.setVisibility(View.INVISIBLE);
            view_setimage.setImageBitmap(null);
        }

    }