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
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.example.a26792.smarthometerminal.utils.Transform;
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
    static BroadcastReceiver mReceiver = null;
    ConnectThread connectThread;
    private List<String> mAlreadyArrayList = new ArrayList<>();
    private List<String> mArrayList = new ArrayList<>();
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    private Set<BluetoothDevice> pairedDevices;
    private AcceptThread acceptThread;
    Set<String> temp = new HashSet<>();
    Set<String> temp1 = new HashSet<>();
    @BindView(R.id.button_connect)
    Button btn_connect;
    @BindView(R.id.button_disconnect)
    Button btn_disconnect;
    @BindView(R.id.button_search)
    Button btn_search;
    @BindView(R.id.button_generate_QR)
    Button btn_register;
    @BindView(R.id.requestagain_btn)
    Button btn_QR;
    @BindView(R.id.already_connect_bt_list)
    ListView mAlreadyBluetoothDevicesList;
    @BindView(R.id.bluetoothdevices_list)
    ListView mBluetoothDevices_lv;
    @BindView(R.id.info_tv)
    TextView info_tv;
    @BindView(R.id.send_message)
    TextView sendMessage_tv;
    @BindView(R.id.receive_message)
    TextView receiveMessage_tv;
    @BindView(R.id.receive_allmessage)
    TextView receiveAllmessage;
    private BluetoothAdapter mBluetoothAdapter;
    private Button jianting_btn;
    private Button tianjia_btn;
    private Button shanchu_btn;
    private Button others_btn;
    private String ANDROID_ID = "";
    //checkbox控件
    @BindView(R.id.root_cb)
    public CheckBox root_cb;
    @BindView(R.id.noroot_cb)
    public CheckBox notroot_cb;
    @BindView(R.id.connect__cb)
    public CheckBox connect_cb;
    @BindView(R.id.disconnect_cb)
    public CheckBox disconnect_cb;
    @BindView(R.id.service_cb)
    public CheckBox service_cb;
    @BindView(R.id.client_cb)
    public CheckBox client_cb;
    @BindView(R.id.register_cb)
    public CheckBox register_cb;
    @BindView(R.id.noregister_cb)
    public CheckBox noregister_cb;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Introduction:
                Intent intent1 = new Intent(MainActivity.this, IntroductionActivity.class);
                startActivity(intent1);
                break;
            case R.id.About:
                Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent2);
                break;
            case R.id.Help:
                Intent intent3 = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent3);
                break;
            default:
        }
        return true;
    }
//        return super.onOptionsItemSelected(item);


    protected void onCreate(Bundle paramBundle) {

        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("user").equals("root")) {
            //root用户
            isRootUser = true;
            isRegistered = true;
            info_tv.setText("点击下方item连接门禁系统蓝牙");
            root_cb.setChecked(true);
            register_cb.setChecked(true);
            initRootView();
        } else {
            //普通用户
            notroot_cb.setChecked(true);
            if (SharedPreferencesUtil.sharedPreferences.contains("isRegistered") && SharedPreferencesUtil.sharedPreferences.getBoolean("isRegistered", false) == true) {
                //普通用户已注册
                info_tv.setText("点击下方item连接门禁系统蓝牙");
                isRegistered = true;
                register_cb.setChecked(true);
            } else {
                //普通用户未曾注册
                noregister_cb.setChecked(true);
            }
        }
        disconnect_cb.setChecked(true);
        //初始化二维码生成器
        ZXingLibrary.initDisplayOpinion(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        startBroadcastReceiverAndListener();
        ANDROID_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e(TAG, "onCreate: " + ANDROID_ID);
        EventBus.getDefault().register(this);
    }

    /**
     * 初始化root用户的部分特殊功能view
     */
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
                        client_cb.setChecked(true);
                        service_cb.setChecked(false);
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
                client_cb.setChecked(true);
                service_cb.setChecked(false);
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

    /**
     * 按钮点击事件监听
     *
     * @param view
     */
    @OnClick(value = {R.id.button_connect, R.id.button_disconnect, R.id.button_search, R.id.button_generate_QR, R.id.requestagain_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_connect:
                Log.e(TAG, "onClick: button_connect");
                if (btn_connect.getText().equals("搜索")) {
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
                    btn_connect.setText("断开");
                    searchBluetoolDevices();
                } else {
                    //关闭蓝牙
                    mBluetoothAdapter.disable();
                    LogUtil.loge(TAG, "discoonect");
                    EventBus.getDefault().post(new EventMessage("closesocket", null));
                    service_cb.setChecked(false);
                    client_cb.setChecked(false);
                    connect_cb.setChecked(false);
                    disconnect_cb.setChecked(true);
                    if (jianting_btn != null) {
                        jianting_btn.setText("打开监听");
                    }
                    btn_connect.setText("搜索");
                }

                break;
            case R.id.button_disconnect:
                //TO-DO:开关灯
                if (connect_cb.isChecked()) {
                    if (btn_disconnect.getText().equals("开灯")) {
                        EventBus.getDefault().post(new EventMessage("openLight", null));

                        btn_disconnect.setText("关灯");
                    } else if (btn_disconnect.getText().equals("关灯")) {
                        EventBus.getDefault().post(new EventMessage("closeLight", null));
                        btn_disconnect.setText("开灯");
                    }
                } else {
                    Toast.makeText(this, "请先连接蓝牙", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button_search:
                //开关门
                if (connect_cb.isChecked()) {
                    if (btn_search.getText().equals("开门")) {
                        EventBus.getDefault().post(new EventMessage("openDoor", null));

                        btn_search.setText("关门");
                    } else if (btn_search.getText().equals("关门")) {
                        EventBus.getDefault().post(new EventMessage("closeDoor", null));
                        btn_search.setText("开门");
                    }
                } else {
                    Toast.makeText(this, "请先连接蓝牙", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button_generate_QR:
                //TO-DO：生成二维码
//                if (connect_cb.isChecked()) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    QrCodeFragment fragment = new QrCodeFragment();
                    fragmentTransaction.replace(R.id.qrcode_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);//fragment页面加到返回栈
                    fragmentTransaction.commit();
                    EventBus.getDefault().post(new EventMessage("request", null));
                    LogUtil.loge(TAG, "QR");
                    EventBus.getDefault().postSticky(new EventMessage("updataQRcode", null));
//                } else {
//                    Toast.makeText(this, "请先连接蓝牙", Toast.LENGTH_SHORT).show();
//                }

                break;
            case R.id.requestagain_btn:
                EventBus.getDefault().post(new EventMessage("register", null));
                break;
            case R.id.jianting:
                if (jianting_btn.getText().equals("打开监听")) {
                    acceptThread = new AcceptThread(mBluetoothAdapter);
                    acceptThread.start();
                    service_cb.setChecked(true);
                    client_cb.setChecked(false);
                    jianting_btn.setText("关闭监听");
                    Log.e(TAG, "openService: ");
                } else {
                    EventBus.getDefault().post(new EventMessage("closesocket", null));
                    jianting_btn.setText("打开监听");
                    service_cb.setChecked(false);
                    client_cb.setChecked(false);
                    if (acceptThread != null) {
                        acceptThread.cancel();
                    }
                }

                break;
            case R.id.tianjia:
                showSingleAlertDialog("add");
                Log.e(TAG, "onClick:tianjia ");
                break;
            case R.id.shanchu:
                showSingleAlertDialog("delete");
                Log.e(TAG, "onClick:shanchu ");
                break;
            case R.id.others:
                //改管理员
//                showSingleAlertDialog("show");
                EventBus.getDefault().post(new EventMessage("change", null));
                Log.e(TAG, "onClick:change ");
                break;
            case R.id.record:
                EventBus.getDefault().post(new EventMessage("record", null));
                break;
            default:
                break;
        }
    }

    /**
     * 该函数用于显示sharepreference里保存的list，点击来实现注册
     */
    public void showSingleAlertDialog(final String order) {

        if (SharedPreferencesUtil.sharedPreferences.contains("othersUser1")) {
            int count = SharedPreferencesUtil.sharedPreferences.getInt("userCount", 0);
            final String[] items = new String[count];
            for (int i = 0; i < count; i++) {
                items[i] = SharedPreferencesUtil.sharedPreferences.getString("othersUser" + (i + 1), "null");
            }
            android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
            if (order.equals("add")) {
                alertBuilder.setTitle("这是单选框,请选择你要添加的用户");
            } else if (order.equals("delete")) {
                alertBuilder.setTitle("这是单选框,请选择你要删除的用户");
            } else {
                alertBuilder.setTitle("这是所有普通用户");
            }


            alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "您选中了：" + items[i], Toast.LENGTH_SHORT).show();
                }
            });

            alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (order.equals("delete")) {
                        Log.e(TAG, "onClick: deletedailog" + i);
                        EventBus.getDefault().post(new EventMessage("shanchu", items[i + 1]));
                    } else if (order.equals("add")) {
                        Log.e(TAG, "onClick: tianjiaDailog" + i);
                        EventBus.getDefault().post(new EventMessage("tianjia", items[i + 1]));
                    }
                    dialogInterface.dismiss();
                }
            });

            alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            android.app.AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
        } else {
            Toast.makeText(this, "没有需要添加/删除的用户", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
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
            Log.e(TAG, "searchBluetoolDevices:获取已匹配设备信息完成！ ");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
                searchBluetoolDevices();
            } else {
                Toast.makeText(this, "蓝牙权限请求失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

// TODO: 2019/6/6 :可以尝试把这块代码抽离，放到透明的fragment中去

    /**
     * 该函数辅助Thread执行UI交互操作
     *
     * @param eventMessage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toast(EventMessage eventMessage) {
        Log.e(TAG, "toast: ");
        if (eventMessage.getMessgae().equals("sr")) {
            Toast.makeText(this, "注册请求已发送", Toast.LENGTH_SHORT).show();
        }
        if (eventMessage.getMessgae().equals("c")) {
            Toast.makeText(this, "连接成功，准备发送数据", Toast.LENGTH_SHORT).show();
        }
        if (eventMessage.getMessgae().equals("receiveRegister")) {
            connect_cb.setChecked(true);
            disconnect_cb.setChecked(false);
            addAlertDialog(eventMessage.getOrder());
        }
        if (eventMessage.getMessgae().equals("unagress") && !isRootUser) {
            Toast.makeText(this, "管理员不同意你的注册申请!", Toast.LENGTH_SHORT).show();
        }
        if (eventMessage.getMessgae().equals("agress") && !isRootUser) {
            Toast.makeText(this, "管理员同意你的注册申请!", Toast.LENGTH_SHORT).show();
            info_tv.setText("点击下方item连接门禁系统蓝牙");
            register_cb.setChecked(true);
            noregister_cb.setChecked(false);
        }
        if (eventMessage.getMessgae().equals("test")) {
            StringBuilder builder = new StringBuilder("接收到的数据：");
            StringBuilder builder2 = new StringBuilder(receiveAllmessage.getText().toString());
            builder.append(eventMessage.getOrder());
            builder2.append(eventMessage.getOrder()+"/n");
            receiveMessage_tv.setText(builder.toString());
            receiveAllmessage.setText(builder2);
            Toast.makeText(this, "test" + eventMessage.getOrder(), Toast.LENGTH_SHORT).show();
        }
        if (eventMessage.getMessgae().equals("connect")) {
            Toast.makeText(this, "connect" + eventMessage.getOrder(), Toast.LENGTH_SHORT).show();
            connect_cb.setChecked(true);
            disconnect_cb.setChecked(false);
        }
        if (eventMessage.getMessgae().equals("fire")) {
            Toast.makeText(this, "着火了，快跑啊！", Toast.LENGTH_LONG).show();
        }
        if (eventMessage.getMessgae().equals("air")) {
            Toast.makeText(this, "CO气体浓度过高，请注意！", Toast.LENGTH_LONG).show();
        }
        if (eventMessage.getMessgae().equals("sendMessage")) {
            StringBuilder builder = new StringBuilder("发送的数据：");
            builder.append(eventMessage.getOrder());
            sendMessage_tv.setText(builder.toString());
        }

    }

    /**
     * 收到注册请求后，dialog提示
     *
     * @param o
     */
    private void addAlertDialog(final String o) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("收到来自" + o + "注册请求,是否同意");
        builder.setCancelable(false);
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = SharedPreferencesUtil.sharedPreferences.edit();
                int userCount;
                if (SharedPreferencesUtil.sharedPreferences.contains("userCount")) {
                    userCount = SharedPreferencesUtil.sharedPreferences.getInt("userCount", 0);
                    userCount++;
                    editor.putInt("userCount", userCount);
                    editor.putString("othersUser" + userCount, o);
                    editor.commit();
                } else {
                    editor.putInt("userCount", 1);
                    editor.putString("othersUser1", o);
                    editor.commit();
                }

                EventBus.getDefault().post(new EventMessage("agress", "1"));
                dialog.dismiss();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("不同意", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, "不同意该用户的注册 ");
                EventBus.getDefault().post(new EventMessage("unagress", "0"));
                dialog.dismiss();
                dialog.cancel();
            }
        });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 在这里做好socket关闭，资源回收操作
     */
    @Override
    protected void onStop() {
        Log.e(TAG, "onStop: ");
        super.onStop();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        mBluetoothAdapter = null;
        temp = null;
        temp1 = null;
        EventBus.getDefault().post(new EventMessage("closesocket", null));
        EventBus.getDefault().unregister(this);
    }
}
