
package com.example.app_java;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import android.view.View;

import android.widget.ImageButton;
import android.widget.Toast;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.valueOf;


public class Control extends AppCompatActivity implements BeaconConsumer{

    public ImageButton btn_lock;
    private ImageButton set_app;
    public ImageButton set_lock_way;
    private ImageButton btn_remote;


    private String InRange_Select_beacon;

    SharedPreferences share_beacon;
    SharedPreferences.Editor share_beacon_editor;

    SharedPreferences share_select_beacon;
    SharedPreferences.Editor share_select_beacon_editor;

    SharedPreferences share_beacon_check;
    SharedPreferences.Editor share_beacon_check_editor;

    private final int fragment_first = 1;
    private final int fragment_second = 2;
    private final int fragment_third = 3;

    private int idx;
    private int select_beacon_idx = 0;

    private BeaconManager beacon_manager;
    private List<Beacon> beaconList;
    private List<Beacon> Array_beacon;
    private String[] beacon_id;



    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_LOCATION_ENABLE_CODE = 1;
    MyService ms;
    boolean isService = false;
    public Handler mhandler;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder mb = (MyService.MyBinder) service;
            ms = mb.getService();
            isService = true;
            Log.e("메세지", "서비스에 연결됨");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        FragmentView(fragment_first);

        Intent service = new Intent(Control.this, MyService.class);
        bindService(service, conn, Context.BIND_AUTO_CREATE);

        share_beacon = getSharedPreferences("Beacon", Context.MODE_PRIVATE);
        share_beacon_editor = share_beacon.edit();

        share_select_beacon = getSharedPreferences("Select_Beacon", Context.MODE_PRIVATE);
        share_select_beacon_editor = share_select_beacon.edit();

        share_beacon_check = getSharedPreferences("Check_Beacon", Context.MODE_PRIVATE);
        share_beacon_check_editor = share_beacon_check.edit();

        beacon_id = new String[10];
        for (int i = 0; i < 10; i++) {
            beacon_id[i] = "";
        }
        idx = 1;

        beacon_manager = BeaconManager.getInstanceForApplication(this);
        beacon_manager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beacon_manager.bind((BeaconConsumer) this);
        beacon_manager.setRegionStatePersistenceEnabled(false);
        beacon_manager.setBackgroundBetweenScanPeriod(0);
        beacon_manager.setBackgroundScanPeriod(1000);
        beaconList = new ArrayList<>();
        Array_beacon = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("위치 접근 동의");
                builder.setMessage("금고 도난 방지를 위한 스마트폰 위치 접근 동의를 해주세요");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }


        btn_lock = (ImageButton) findViewById(R.id.lockButton);
        set_app = findViewById(R.id.Set_app);
        btn_remote = findViewById(R.id.remoteButton);


        set_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(fragment_first);
            }
        });

        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentView(fragment_second);
            }
        });

        btn_remote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentView(fragment_third);
            }
        });

    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    private void FragmentView(int fragment){

        //FragmentTransactiom를 이용해 프래그먼트를 사용합니다.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
                // 첫번 째 프래그먼트 호출
                Set_Pwd fragment1 = new Set_Pwd();
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.commit();
                break;

            case 2:
                // 두번 째 프래그먼트 호출
                Set_lock fragment2 = new Set_lock();
                transaction.replace(R.id.fragment_container, fragment2);
                transaction.commit();
                break;

            case 3:
                // 두번 째 프래그먼트 호출
                remote fragment3 = new remote();
                transaction.replace(R.id.fragment_container, fragment3);
                transaction.commit();
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beacon_manager.unbind((BeaconConsumer) this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beacon_manager.setRangeNotifier(new RangeNotifier() {

            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                try {
                } catch (NullPointerException e) {
                }
                beaconList.clear();
                if(beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        //Log.e("plus", "add b eacon");
                        beaconList.add(beacon);
                        Array_beacon.add(beacon);
                        if (beacon_id[idx] == "") {
                            if (idx == 1) {
                                beacon_id[idx] = beacon.getId1().toString();
                                idx++;
                            } else if (idx > 1) {
                                if (!beacon_id[idx - 1].equals(beacon.getId1().toString())) {
                                    beacon_id[idx] = beacon.getId1().toString();
                                }
                            }
                        }
                    }
                }




                    InRange_Select_beacon = share_select_beacon.getString("Select_Beacon", "");
                    //Log.e("beacon_size", String.valueOf(beaconList.size()));
                    if (!InRange_Select_beacon.equals("")) {
                        //Log.e("BeaconList",beaconList.toString());
                        for (int i = 0; i < Array_beacon.size() + 1; i++) {
                            if (beaconList.size() == 0) break;
                            else {
                                if (beaconList.get(i).getId1().toString().equals(InRange_Select_beacon)) {
                                    share_beacon_check_editor.putString("Check_Beacon","IN");
                                    share_beacon_check_editor.apply();
                                    break;
                                }
                            }
                        }
                    }

            }

        });

        try {
            beacon_manager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }

    }
    // 버튼이 클릭되면 textView 에 비콘들의 정보를 뿌린다.
    public void OnButtonClicked(View view) {

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("비콘 선택"); //제목
        dlg.setIcon(R.drawable.beacon); // 아이콘 설정
        String[] dlg_string;
        for (int i = 1; ; i++) {
            if (beacon_id[i].equals("")) {
                dlg_string = new String[i - 1];
                for (int j = 0; j < i - 1; j++) {
                    dlg_string[j] = "";
                }
                break;
            }
        }
        for (int i = 1; !beacon_id[i].equals(""); i++) {
            dlg_string[i - 1] = beacon_id[i];
        }

        dlg.setItems(dlg_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int idx) {
                select_beacon_idx = idx;
                Log.e("select_beacon index", String.valueOf(select_beacon_idx));
                Log.e("BeaconList", Array_beacon.get(select_beacon_idx).toString());
                String select_beacon = Array_beacon.get(select_beacon_idx).getId1().toString();
                String save_beacon_id = share_select_beacon.getString("Select_Beacon", "");
                if (save_beacon_id.equals("")) {
                    share_select_beacon_editor.putString("Select_Beacon", select_beacon);
                    share_select_beacon_editor.apply();
                    Toast.makeText(getApplicationContext(), "비콘 선택이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    Log.e("select", share_select_beacon.getString("Select_Beacon", ""));
                } else {
                    share_select_beacon_editor.putString("Select_Beacon", select_beacon);
                    share_select_beacon_editor.apply();
                    Toast.makeText(getApplicationContext(), "설정될 비콘이 변경되었습니다", Toast.LENGTH_SHORT).show();
                    Log.e("select", share_select_beacon.getString("Select_Beacon", ""));
                }
                //handler.sendEmptyMessage(0);
            }
        });
//                버튼 클릭시 동작
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //토스트 메시지
            }

        });
        dlg.show();
        // 아래에 있는 handleMessage를 부르는 함수. 맨 처음에는 0초간격이지만 한번 호출되고 나면
        // 1초마다 불러온다.
    }
}




