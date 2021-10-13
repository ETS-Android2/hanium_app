package com.example.app_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Set_Pwd extends Fragment{

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private static final int REQUEST_CODE_Gallery = 0;
    private String imageFilePath;
    private Uri photoUri;

    private ImageButton btn_take_picture;
    private ImageButton btn_take_gallery;
    private ImageButton set_danger_btn;
    private ImageButton set_user_btn;

    private TextView tv_camera;
    private TextView tv_gallery;
    private TextView tv_user;
    private TextView tv_danger;

    private ImageView set_pic;


    private ImageButton app_pwd_set;
    private ImageButton door_pwd_set;

    SharedPreferences share_beacon;
    SharedPreferences.Editor share_beacon_editor;

    SharedPreferences share_select_beacon;
    SharedPreferences.Editor share_select_beacon_editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_set_pwd, container, false);

        app_pwd_set = (ImageButton) rootview.findViewById(R.id.smartphone);
        door_pwd_set = (ImageButton) rootview.findViewById(R.id.set_safe);

        set_user_btn = rootview.findViewById(R.id.set_user);
        set_danger_btn = rootview.findViewById(R.id.set_danger);
        btn_take_picture = rootview.findViewById(R.id.use_camera);
        btn_take_gallery = rootview.findViewById(R.id.use_gallery);

        tv_camera = rootview.findViewById(R.id.camera_text);
        tv_gallery = rootview.findViewById(R.id.gallery_text);
        tv_user = rootview.findViewById(R.id.user_text);
        tv_danger = rootview.findViewById(R.id.danger_text);

        set_pic = rootview.findViewById(R.id.set_pic);

        init();

        set_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) set_pic.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                try {
                    MyService.mqttAndroidClient.publish("set_person_user", bitmapToByteArray(bitmap), 0 , false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Log.e("사용자 설정","Clicked");
                Toast.makeText(getActivity(),"설정이 완료되었습니다",Toast.LENGTH_LONG).show();
                init();

            }
        });

        set_danger_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) set_pic.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                try {
//                    MyService.mqttAndroidClient.publish("set_person",jobj.toString().getBytes(), 0 , false);
                    MyService.mqttAndroidClient.publish("set_person_danger",bitmapToByteArray(bitmap), 0 , false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                Log.e("위험인물 설정 설정","Clicked");
                Toast.makeText(getActivity(),"설정이 완료되었습니다",Toast.LENGTH_LONG).show();
                init();
            }
        });

        btn_take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);

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


        app_pwd_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_chg_app = new Intent(getActivity(), appLock.class);
                intent_chg_app.putExtra(app_lock_const.type, app_lock_const.CHANGE_PASSLOCK);
                Set_Pwd.this.startActivityForResult(intent_chg_app, app_lock_const.CHANGE_PASSLOCK);
            }
        });

        door_pwd_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_chg_door = new Intent(getActivity(), appLock.class);
                intent_chg_door.putExtra(app_lock_const.type, app_lock_const.SET_TOUCHPAD);
                Set_Pwd.this.startActivityForResult(intent_chg_door, app_lock_const.SET_TOUCHPAD);
            }
        });



        return rootview;
    }


    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (requestCode == 0) {
            if (grantResult[0] == 0) {
                Toast.makeText(getActivity(), "카메라 권한이 승인됨", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "카메라 권한이 거절되었습니다.카메라를 이용하려면 권한을 승낙하여야 합니다",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
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

            (set_pic).setImageBitmap(rotate(bitmap, exifDegree));
            opposite_init();
        }

        if(requestCode == REQUEST_CODE_Gallery)
        {
            if(resultCode == getActivity().RESULT_OK)
            {
                try{
                    InputStream in = getActivity().getContentResolver().openInputStream(data.getData());

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

//                        ((ImageView) findViewById(R.id.photo)).setImageBitmap(img);
                    (set_pic).setImageBitmap(rotate(img,exifDegree + 90));
                    opposite_init();


                }catch(Exception e)
                {
                    Toast.makeText(getActivity(), "갤러리 접근 중 오류 발생", Toast.LENGTH_LONG).show();

                }
            }
            else if(resultCode == getActivity().RESULT_CANCELED)
            {
                Toast.makeText(getActivity(), "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }

    }



    private int exifOrientationToDegrees ( int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } else {
            return 0;
        }
    }
    private Bitmap rotate (Bitmap bitmap,float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private void sendTakePhotoIntent () {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile () throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        tv_camera.setVisibility(View.VISIBLE);
        tv_gallery.setVisibility(View.VISIBLE);
        tv_danger.setVisibility(View.GONE);
        tv_user.setVisibility(View.GONE);

        set_pic.setVisibility(View.GONE);

        btn_take_gallery.setVisibility(View.VISIBLE);
        btn_take_picture.setVisibility(View.VISIBLE);
        set_danger_btn.setVisibility(View.GONE);
        set_user_btn.setVisibility(View.GONE);

//        btn_take_gallery.setEnabled(true);
//        btn_take_picture.setEnabled(true);
//        set_danger_btn.setEnabled(false);
//        set_user_btn.setEnabled(false);
    }

    private void opposite_init(){
        tv_camera.setVisibility(View.GONE);
        tv_gallery.setVisibility(View.GONE);
        tv_danger.setVisibility(View.VISIBLE);
        tv_user.setVisibility(View.VISIBLE);

        set_pic.setVisibility(View.GONE);

        btn_take_gallery.setVisibility(View.GONE);
        btn_take_picture.setVisibility(View.GONE);
        set_danger_btn.setVisibility(View.VISIBLE);
        set_user_btn.setVisibility(View.VISIBLE);
//
//        btn_take_gallery.setEnabled(true);
//        btn_take_picture.setEnabled(true);
//        set_danger_btn.setEnabled(true);
//        set_user_btn.setEnabled(true);
    }
}



