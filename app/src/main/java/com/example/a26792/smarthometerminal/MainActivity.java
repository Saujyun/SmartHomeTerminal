package com.example.a26792.smarthometerminal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a26792.smarthometerminal.Fragment.QrCodeFragment;
import com.example.a26792.smarthometerminal.utils.LogUtil;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 10;
    private boolean isEnable = false;
    static BroadcastReceiver mReceiver;
    private List<String> mAlreadyArrayList = new ArrayList<>();
    private List<String> mArrayList = new ArrayList<>();
    Set<String> temp = new HashSet<>();
    Set<String> temp1 = new HashSet<>();
    @BindView(R.id.button_connect)
    Button btn_connect;
    @BindView(R.id.button_disconnect)
    Button btn_disconnect;
    @BindView(R.id.button_search)
    Button btn_search;
    @BindView(R.id.button_generate_QR)
    Button btn_QR;
    @BindView(R.id.already_connect_bt_list)
    ListView mAlreadyBluetoothDevicesList;
    @BindView(R.id.bluetoothdevices_list)
    ListView mBluetoothDevicesList;
    private BluetoothAdapter mBluetoothAdapter;


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //初始化二维码生成器
        ZXingLibrary.initDisplayOpinion(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Create a BroadcastReceiver for ACTION_FOUND
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    if (!temp1.contains(device.getName() + "\n" + device.getAddress())) {
                        mArrayList.add(device.getName() + "\n" + device.getAddress());
                    }

                    temp1.add(device.getName() + "\n" + device.getAddress());


                    upDate();
                    LogUtil.loge(TAG, "receive");
                }
            }


        };
// Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

    }

    private void upDate() {
        if (!mArrayList.isEmpty()) {
            mBluetoothDevicesList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mArrayList));
        } else {
            LogUtil.loge(TAG, "mArrayList is null");
        }
    }


    @OnClick(value = {R.id.button_connect, R.id.button_disconnect, R.id.button_search, R.id.button_generate_QR})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_connect:
                if (!mBluetoothAdapter.isEnabled()) {
                    //蓝牙开启请求
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    //蓝牙可检测请求
                    Intent discoverableIntent = new
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(discoverableIntent);
                } else {
                    isEnable = true;
                }
                searchBluetoolDevices();
                break;
            case R.id.button_disconnect:
                //TO-DO:关闭蓝牙
                mBluetoothAdapter.disable();
                LogUtil.loge(TAG, "discoonect");
                break;
//            case R.id.button_search:
//                //TO-DO：蓝牙搜索
//                searchBluetoolDevices();
//                LogUtil.loge(TAG, "search");
//                break;
            case R.id.button_generate_QR:
                //TO-DO：生成二维码
                FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                QrCodeFragment fragment=new QrCodeFragment();
                fragmentTransaction.replace(R.id.qrcode_fragment,fragment);
                fragmentTransaction.addToBackStack(null);//fragment页面加到返回栈
                fragmentTransaction.commit();
                LogUtil.loge(TAG, "QR");
                break;
            default:
                break;
        }
    }

    private void searchBluetoolDevices() {
        if (isEnable) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            //确保不重复转载信息


// If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    if (!temp.contains(device.getName() + "\n" + device.getAddress())) {
                        mAlreadyArrayList.add(device.getName() + "\n" + device.getAddress());
                    }
                    temp.add(device.getName() + "\n" + device.getAddress());
                    LogUtil.loge(TAG, "get");

                }

                if (!mAlreadyArrayList.isEmpty()) {
                    mAlreadyBluetoothDevicesList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mAlreadyArrayList));

                }
            }

        } else {
            Toast.makeText(this, "请先申请蓝牙权限", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            isEnable = true;
            Toast.makeText(this, "蓝牙权限请求成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "蓝牙权限请求失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mBluetoothAdapter = null;
        temp = null;
        temp1 = null;
    }
}
