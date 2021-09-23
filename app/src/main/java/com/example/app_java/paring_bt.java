package com.example.app_java;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.datepicker.MonthsPagerAdapter;
//
//import java.util.ArrayList;
//import java.util.UUID;
//
//public class paring_bt extends ListActivity {
//
//    private EditText ET_SSID;
//    private EditText ET_PWD;
//    private Button btn_cancel;
//    private Button mBtnSendData;
//
//    private LeDeviceListAdapter mLeDeviceListAdapter;
//    private BluetoothAdapter mBluetoothAdapter;
//    private boolean mScanning;
//    private Handler mHandler;
//
//
//    private static final int REQUEST_ENABLE_BT = 1;
//    private static final long SCAN_PERIOD = 10000;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_paring_bt);
//        getActionBar().setTitle(title_devices);
//        mHandler = new Handler();
//
//        mBtnSendData = findViewById(R.id.btnSendData);
//        btn_cancel = findViewById(R.id.btn_cancel);
//
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        ET_SSID = null;
//        ET_PWD = null;
//
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, "BLE is not supported", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(paring_bt.this, Control.class);
//            startActivity(intent);
//        }
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {//api 18 이상이면
//            final BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//            if (btManager != null) {
//                mBluetoothAdapter = btManager.getAdapter();//Bluetooth adapter crate, initialize
//            }
//        }
//
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(paring_bt.this, Control.class);
//            startActivity(intent);
//        }
//
//
//        btn_cancel.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent set_user_intent = new Intent(paring_bt.this, Control.class);
//                startActivity(set_user_intent);
//            }
//        });
//
//
//
//
//
//        mBtnSendData.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mThreadConnectedBluetooth != null) {
//                    String wifi_data = ET_SSID.getText().toString() + "pwd" + ET_PWD.getText().toString();
//
//                }
//            }
//        });
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED){
//            Toast.makeText(this,"블루투스 요청을 거부 하였습니다. 다시 요청 해주세요", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    protected void onResume() {//블루투스가 켜진지 확인후 꺼져있으면 블투 활성화 요청
//        super.onResume();
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBt, REQUEST_ENABLE_BT);
//        }
//
//        mLeDeviceListAdapter = new LeDeviceListAdapter();
//        setListAdapter(mLeDeviceListAdapter);
//        scanLeDevice(true);
//    }
//
//
//    private class LeDeviceListAdapter extends BaseAdapter {
//        private ArrayList<BluetoothDevice> mLeDevices;
//        private LayoutInflater mInflator;
//
//        public LeDeviceListAdapter() {
//            super();
//            mLeDevices = new ArrayList<BluetoothDevice>();
//            mInflator = paring_bt.this.getLayoutInflater();
//        }
//
//        public void addDevice(BluetoothDevice device){
//            if(!mLeDevices.contains(device)){
//                mLeDevices.add(device);
//            }
//        }
//
//        public BluetoothDevice getDevice(int position){return mLeDevices.get(position);}
//        public  void clear(){mLeDevices.clear();}
//
//        @Override
//        public int getCount() {
//            return mLeDevices.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return mLeDevices.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            ViewHolder viewHolder;
//            if(view == null){
//                view = mInflator.inflate(R.layout.listitem_device, null);
//                viewHolder = new ViewHolder();
//            }
//            return null;
//        }
//    }
//
//}