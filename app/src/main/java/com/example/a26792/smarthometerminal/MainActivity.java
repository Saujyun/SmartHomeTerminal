package com.example.a26792.smarthometerminal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a26792.smarthometerminal.Fragment.QrCodeFragment;
import com.example.a26792.smarthometerminal.bean.Protocols;
import com.example.a26792.smarthometerminal.utils.AcceptThread;
import com.example.a26792.smarthometerminal.utils.ConnectThread;
import com.example.a26792.smarthometerminal.utils.ConnectedThread;
import com.example.a26792.smarthometerminal.utils.ConnectedThread2;
import com.example.a26792.smarthometerminal.utils.EventMessage;
import com.example.a26792.smarthometerminal.utils.LogUtil;
import com.example.a26792.smarthometerminal.utils.MyApplication;
import com.example.a26792.smarthometerminal.utils.SharedPreferencesUtil;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivitytest";
    private static boolean isRootUser = false;
    public static boolean isRegistered = false;
    private static final int REQUEST_ENABLE_BT = 10;
    private boolean isEnable = false;
    static BroadcastReceiver mReceiver;
    private ConnectedThread2 connectedThread2;
    private ConnectedThread connectedThread;
    ConnectThread connectThread;
    private List<String> mAlreadyArrayList = new ArrayList<>();
    private List<String> mArrayList = new ArrayList<>();
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    private Set<BluetoothDevice> pairedDevices;
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
    ListView mBluetoothDevices_lv;
    @BindView(R.id.info_tv)
    TextView info_tv;
    private BluetoothAdapter mBluetoothAdapter;
    private Button jianting_btn;
    private Button tianjia_btn;
    private Button shanchu_btn;
    private Button others_btn;
    private String ANDROID_ID = "";


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("user").equals("root")) {
            isRootUser = true;
            isRegistered = true;
            info_tv.setText("点击下方item连接门禁系统蓝牙");
            initRootView();
        }


        if (SharedPreferencesUtil.sharedPreferences.contains("isRegistered") && SharedPreferencesUtil.sharedPreferences.getBoolean("isRegistered", false) == true) {
            info_tv.setText("点击下方item连接门禁系统蓝牙");
            isRegistered = true;
        }

        //初始化二维码生成器
        ZXingLibrary.initDisplayOpinion(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        startBroadcastReceiverAndListener();
        ANDROID_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e(TAG, "onCreate: " + ANDROID_ID);
        EventBus.getDefault().register(this);
    }

    private void initRootView() {
        ViewStub viewStub = findViewById(R.id.mViewStub);
        viewStub.inflate();
        jianting_btn = findViewById(R.id.jianting);
        tianjia_btn = findViewById(R.id.tianjia);
        shanchu_btn = findViewById(R.id.shanchu);
        others_btn = findViewById(R.id.others);
        jianting_btn.setOnClickListener(this);
        tianjia_btn.setOnClickListener(this);
        shanchu_btn.setOnClickListener(this);
        others_btn.setOnClickListener(this);
    }


    /**
     * 注册广播接收器并开启两个listView的监听
     */
    private void startBroadcastReceiverAndListener() {
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
                        bluetoothDeviceList.add(device);
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
        //已配对列表点击事件监听
        mAlreadyBluetoothDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "connecting" + position, Toast.LENGTH_SHORT).show();
                for (BluetoothDevice device : pairedDevices) {
                    if ((position--) == 0) {
                        Log.e(TAG, "onItemClick: " + position);
                        connectThread = new ConnectThread(device, mBluetoothAdapter);
                        connectThread.start();
                        connectedThread2 = connectThread.getConnectedThread();

                    }

                }
            }
        });
        //搜索到的其他设备列表的点击事件监听
        mBluetoothDevices_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "connecting" + position, Toast.LENGTH_SHORT).show();
                connectThread = new ConnectThread(bluetoothDeviceList.get(position), mBluetoothAdapter);
                connectThread.start();
                connectedThread2 = connectThread.getConnectedThread();
            }
        });
    }

    //更新蓝牙设备列表
    private void upDate() {
        if (!mArrayList.isEmpty()) {
            mBluetoothDevices_lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mArrayList));
        } else {
            LogUtil.loge(TAG, "mArrayList is null");
        }
    }

    //点击事件监听
    @OnClick(value = {R.id.button_connect, R.id.button_disconnect, R.id.button_search, R.id.button_generate_QR})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_connect:
                Log.e(TAG, "onClick: button_connect");
                if (!mBluetoothAdapter.isEnabled()) {
                    //确保已启用蓝牙,否则在这里请求蓝牙权限
                    Log.d(TAG, "onClick() returned: mBluetoothAdapter is Enabled");
                    //蓝牙开启请求
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    //蓝牙可检测请求
                    Intent discoverableIntent = new
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(discoverableIntent);

                }
                searchBluetoolDevices();
                break;
            case R.id.button_disconnect:
                //TO-DO:关闭蓝牙
                mBluetoothAdapter.disable();
                LogUtil.loge(TAG, "discoonect");
                break;
            case R.id.button_search:
                //开关门
                if (connectedThread2 == null) {
                    Toast.makeText(this, "请先连接蓝牙设备", Toast.LENGTH_SHORT).show();
                } else {
                    if (btn_search.getText().equals("开门")) {
                        send("openDoor");
                        btn_search.setText("关门");
                    } else if (btn_search.getText().equals("关门")) {
                        send("closeDoor");
                        btn_search.setText("开门");
                    }
                }
                break;
            case R.id.button_generate_QR:
                //TO-DO：生成二维码
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                QrCodeFragment fragment = new QrCodeFragment();
                fragmentTransaction.replace(R.id.qrcode_fragment, fragment);
                fragmentTransaction.addToBackStack(null);//fragment页面加到返回栈
                fragmentTransaction.commit();
                LogUtil.loge(TAG, "QR");
                break;
            case R.id.jianting:
                synchronized (this) {
                    AcceptThread acceptThread = new AcceptThread(mBluetoothAdapter);
                    acceptThread.start();
                    connectedThread = acceptThread.getConnectedThread();
                    Log.e(TAG, "openService: ");
                    while (connectedThread != null)
                        this.notifyAll();
                    break;
                }

            case R.id.tianjia:
                Log.e(TAG, "onClick:tianjia ");
                break;
            case R.id.shanchu:
                Log.e(TAG, "onClick:shanchu ");
                break;
            case R.id.others:
                Log.e(TAG, "onClick:others ");
                break;
            default:
                break;
        }
    }

    private void searchBluetoolDevices() {

        pairedDevices = mBluetoothAdapter.getBondedDevices();
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
                LogUtil.loge(TAG, "获取已匹配设备信息");

            }
            if (!mAlreadyArrayList.isEmpty()) {
                mAlreadyBluetoothDevicesList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mAlreadyArrayList));

            }
        }
        //开启蓝牙搜索，异步进程
        mBluetoothAdapter.startDiscovery();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙权限请求成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "蓝牙权限请求失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean send(String s) {
        Log.e(TAG, "send: ");
        switch (s) {
            case "register":
                connectedThread2.write(strToByteArray(Protocols.getRegister(Protocols.userAndroidId)));
                break;
            case "openDoor":
                connectedThread2.write(strToByteArray(Protocols.getOpenDoor(Protocols.userAndroidId)));
                break;
            case "closeDoor":
                connectedThread2.write(strToByteArray(Protocols.getCloseDoor(Protocols.userAndroidId)));
                break;
            case "record":
                connectedThread2.write(strToByteArray(Protocols.getRecord(Protocols.userAndroidId)));
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * String转byte[]
     *
     * @param str
     * @return
     */
    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toast(EventMessage eventMessage) {
        Log.e(TAG, "toast: ");
        if (eventMessage.getMessgae().equals("sr")) {
            Toast.makeText(MyApplication.getContext(), "注册请求已发送", Toast.LENGTH_SHORT).show();
        }
        if (eventMessage.getMessgae().equals("c")) {
            Toast.makeText(MyApplication.getContext(), "连接成功，准备发送数据", Toast.LENGTH_SHORT).show();
        }
        if (eventMessage.getMessgae().equals("receiveRegister")) {
            addAlertDialog(eventMessage.getOrder());
        }
    }

    /**
     * 收到注册请求后，dialog提示
     *
     * @param o
     */
    private void addAlertDialog(final String o) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("收到来自" + "注册请求,是否同意");
        builder.setCancelable(false);
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor = SharedPreferencesUtil.sharedPreferences.edit();
                editor.putString("others", o);
                editor.commit();
                EventBus.getDefault().post(new EventMessage("agress","1"));
//                if (connectedThread == null) {
//                    synchronized (this) {
//                        try {
//                            this.wait();
//                            connectedThread.write(new byte[]{1});
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else {
//                    connectedThread.write(new byte[]{1});
//                }

                dialog.dismiss();
                dialog.cancel();


            }
        });
        builder.setNegativeButton("不同意", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, "不同意该用户的注册 ");
                EventBus.getDefault().post(new EventMessage("unagress","1"));
                dialog.dismiss();
                dialog.cancel();
            }
        });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mBluetoothAdapter = null;
        temp = null;
        temp1 = null;
        EventBus.getDefault().unregister(this);
    }


}
