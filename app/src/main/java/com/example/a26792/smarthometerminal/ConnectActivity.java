//package com.example.a26792.smarthometerminal;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothServerSocket;
//import android.bluetooth.BluetoothSocket;
//import android.content.BroadcastReceiver;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.res.Resources;
//import android.os.Bundle;
//
//import android.support.v7.app.AppCompatActivity;
//import android.telephony.TelephonyManager;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
///**
// * Created by ${Saujyun} on 2019/3/24.
// */
//
//public class ConnectActivity extends AppCompatActivity implements View.OnClickListener {
//    private static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private static final String NAME = "BT_DEMO";
//    static String androidId_data;
//    static String new_androidId_data;
//    private final int BUFFER_SIZE = 1024;
//    public MainActivity a;
//    private BlueToothDeviceAdapter adapter;
//    String androidId = null;
//    private BluetoothAdapter bTAdatper;
//    Button btn_kaideng;
//    Button btn_kaifengshan;
//    Button btn_kaimen;
//    Button btn_openBT;
//    byte[] buffer = new byte['?'];
//    int bytes;
//    ConnectThread connectThread;
//    boolean kaidengorguandeng = true;
//    boolean kaifengshanorguanfengshan = true;
//    boolean kaimenorguanmen = true;
//    boolean lianjie_state = false;
//    private ListView listView;
//    private ListView listView1;
//    private ListenerThread listenerThread;
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent) {
//            String action = paramAnonymousIntent.getAction();
//            if ("android.bluetooth.device.action.FOUND".equals(action)) {
//                BluetoothDevice bluetoothDevice = (BluetoothDevice) paramAnonymousIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (bluetoothDevice.getBondState() != 12) {
//                    ConnectActivity.this.adapter.add(paramAnonymousContext);
//                    ConnectActivity.this.adapter.notifyDataSetChanged();
//                }
//            }
//            do {
//                return;
//                if ("android.bluetooth.adapter.action.DISCOVERY_STARTED".equals(action)) {
//                    Toast.makeText(ConnectActivity.this, "搜索开始", 0).show();
//                    return;
//                }
//            }
//            while (!"android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action));
//            Toast.makeText(ConnectActivity.this, "搜索结束", 0).show();
//        }
//    };
//    boolean switch_bluetooth = true;
//    TextView textView1;
//    private TextView text_msg;
//    TextView text_state;
//    Toast tst;
//
//    private void connectDevice(BluetoothDevice paramBluetoothDevice) {
//        this.text_state.setText(getResources().getString(2131099679));
//        try {
//            this.connectThread = new ConnectThread(paramBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID), true);
//            this.connectThread.start();
//            return;
//        } catch (IOException paramBluetoothDevice) {
//            paramBluetoothDevice.printStackTrace();
//        }
//    }
//
//    private void getBoundedDevices() {
//        Object localObject = this.bTAdatper.getBondedDevices();
//        if (((Set) localObject).size() > 0) {
//            this.adapter.clear();
//            localObject = ((Set) localObject).iterator();
//            while (((Iterator) localObject).hasNext()) {
//                BluetoothDevice localBluetoothDevice = (BluetoothDevice) ((Iterator) localObject).next();
//                this.adapter.add(localBluetoothDevice);
//            }
//        }
//    }
//
//    private List<String> getData(String paramString) {
//        Object localObject1 = "";
//        ArrayList localArrayList = new ArrayList();
//        int i = 0;
//        while (i < paramString.length() / 20) {
//            int j = i;
//            while (j < i + 20) {
//                char c = paramString.charAt(i);
//                Object localObject2;
//                if ((c < 'A') || (c > 'z')) {
//                    localObject2 = localObject1;
//                    if (c >= '0') {
//                        localObject2 = localObject1;
//                        if (c > '9') {
//                        }
//                    }
//                } else {
//                    localObject2 = (String) localObject1 + c;
//                }
//                j += 1;
//                localObject1 = localObject2;
//            }
//            localArrayList.add(localObject1);
//            i += 20;
//        }
//        return localArrayList;
//    }
//
//    private String getPhoneNumber() {
//        return ((TelephonyManager) getSystemService("phone")).getLine1Number();
//    }
//
//    private void initReceiver() {
//        IntentFilter localIntentFilter = new IntentFilter();
//        localIntentFilter.addAction("android.bluetooth.device.action.FOUND");
//        localIntentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
//        localIntentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
//        registerReceiver(this.mReceiver, localIntentFilter);
//    }
//
//    private void initView() {
//        findViewById(2131492938).setOnClickListener(this);
//        findViewById(2131492942).setOnClickListener(this);
//        findViewById(2131492949).setOnClickListener(this);
//        findViewById(2131492950).setOnClickListener(this);
//        findViewById(2131492951).setOnClickListener(this);
//        findViewById(2131492937).setOnClickListener(this);
//        findViewById(2131492939).setOnClickListener(this);
//        this.text_state = ((TextView) findViewById(2131492941));
//        this.text_msg = ((TextView) findViewById(2131492943));
//        this.textView1 = ((TextView) findViewById(2131492945));
//        this.androidId = Settings.Secure.getString(getContentResolver(), "android_id");
//        this.listView = ((ListView) findViewById(2131492944));
//        this.adapter = new BlueToothDeviceAdapter(getApplicationContext(), 2130968602);
//        this.listView.setAdapter(this.adapter);
//        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong) {
//                if (ConnectActivity.this.bTAdatper.isDiscovering()) {
//                    ConnectActivity.this.bTAdatper.cancelDiscovery();
//                }
//                paramAnonymousAdapterView = (BluetoothDevice) ConnectActivity.this.adapter.getItem(paramAnonymousInt);
//                ConnectActivity.this.connectDevice(paramAnonymousAdapterView);
//            }
//        });
//    }
//
//    private void openBlueTooth() {
//        if (this.bTAdatper == null) {
//            Toast.makeText(this, "����������������������", 0).show();
//        }
//        if (!this.bTAdatper.isEnabled()) {
//            this.bTAdatper.enable();
//        }
//        for (; ; ) {
//            if (this.bTAdatper.getScanMode() != 23) {
//                Intent localIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
//                localIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 0);
//                startActivity(localIntent);
//            }
//            return;
//            Toast.makeText(this, "����������", 0).show();
//        }
//    }
//
//    private static String returnResultMultiple(String paramString) {
//        Object localObject1 = "";
//        if (paramString.equals("")) {
//            return "";
//        }
//        int i = 0;
//        while (i < paramString.length()) {
//            char c = paramString.charAt(i);
//            Object localObject2;
//            if ((c < 'A') || (c > 'z')) {
//                localObject2 = localObject1;
//                if (c >= '0') {
//                    localObject2 = localObject1;
//                    if (c > '9') {
//                    }
//                }
//            } else {
//                localObject2 = (String) localObject1 + c;
//            }
//            i += 1;
//            localObject1 = localObject2;
//        }
//        return (String) localObject1;
//    }
//
//    private void searchDevices() {
//        if (!this.bTAdatper.isEnabled()) {
//            Toast.makeText(this, "����������������������", 0).show();
//        }
//        if (this.bTAdatper.isDiscovering()) {
//            this.bTAdatper.cancelDiscovery();
//        }
//        getBoundedDevices();
//        this.bTAdatper.startDiscovery();
//    }
//
//    public void onClick(View paramView) {
//        switch (paramView.getId()) {
//            case 2131492940:
//            case 2131492941:
//            case 2131492943:
//            case 2131492944:
//            case 2131492945:
//            default:
//            case 2131492936:
//            case 2131492938:
//            case 2131492942:
//                do {
//                    return;
//                    if (this.switch_bluetooth) {
//                        openBlueTooth();
//                        this.btn_openBT.setText("��������");
//                        this.switch_bluetooth = false;
//                        return;
//                    }
//                    this.bTAdatper.disable();
//                    this.btn_openBT.setText("��������");
//                    Toast.makeText(getApplicationContext(), "����������", 1);
//                    this.switch_bluetooth = true;
//                    return;
//                    if (this.bTAdatper.isEnabled()) {
//                        searchDevices();
//                        this.textView1.setText("������  \t\t \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tMAC");
//                        return;
//                    }
//                    Toast.makeText(this, "��������������", 0).show();
//                    return;
//                } while (this.connectThread == null);
//                this.connectThread.sendMsg("111");
//                return;
//            case 2131492949:
//                if (this.lianjie_state) {
//                    this.tst = Toast.makeText(this, this.androidId, 0);
//                    this.tst.show();
//                    ((ClipboardManager) getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("����Android_ID��", this.androidId));
//                    this.connectThread.sendMsg(this.androidId);
//                    return;
//                }
//                Toast.makeText(this, "��������������", 0).show();
//                return;
//            case 2131492946:
//                if (this.lianjie_state) {
//                    if (this.kaimenorguanmen) {
//                        this.connectThread.sendMsg("D" + this.androidId + "1");
//                        this.kaimenorguanmen = false;
//                        this.btn_kaimen.setText("����");
//                        return;
//                    }
//                    this.connectThread.sendMsg("D" + this.androidId + "0");
//                    this.kaimenorguanmen = true;
//                    this.btn_kaimen.setText("����");
//                    return;
//                }
//                Toast.makeText(this, "��������������", 0).show();
//                return;
//            case 2131492947:
//                if (this.lianjie_state) {
//                    if (this.kaidengorguandeng) {
//                        this.connectThread.sendMsg("L" + this.androidId + "1");
//                        this.kaidengorguandeng = false;
//                        this.btn_kaideng.setText("����");
//                        return;
//                    }
//                    this.connectThread.sendMsg("L" + this.androidId + "0");
//                    this.kaidengorguandeng = true;
//                    this.btn_kaideng.setText("����");
//                    return;
//                }
//                Toast.makeText(this, "��������������", 0).show();
//                return;
//            case 2131492948:
//                if (this.lianjie_state) {
//                    if (this.kaifengshanorguanfengshan) {
//                        this.connectThread.sendMsg("F" + this.androidId + "1");
//                        this.kaifengshanorguanfengshan = false;
//                        this.btn_kaifengshan.setText("������");
//                        return;
//                    }
//                    this.connectThread.sendMsg("F" + this.androidId + "0");
//                    this.kaifengshanorguanfengshan = true;
//                    this.btn_kaifengshan.setText("������");
//                    return;
//                }
//                Toast.makeText(this, "��������������", 0).show();
//                return;
//            case 2131492939:
//                if (this.lianjie_state) {
//                    this.connectThread.sendMsg("U" + this.androidId);
//                    return;
//                }
//                Toast.makeText(this, "��������������", 0).show();
//                return;
//            case 2131492937:
//                if (this.lianjie_state) {
//                    this.connectThread.sendMsg("J" + this.androidId);
//                    return;
//                }
//                Toast.makeText(this, "��������������", 0).show();
//                return;
//            case 2131492950:
//                if (this.text_msg.getText().toString() != null) {
//                    if ((returnResultMultiple(this.text_msg.getText().toString()).length() == 16) || (returnResultMultiple(this.text_msg.getText().toString()).length() == 15)) {
//                        this.connectThread.sendMsg(this.androidId + "T" + returnResultMultiple(this.text_msg.getText().toString()));
//                        Toast.makeText(this, "������������", 0).show();
//                        return;
//                    }
//                    Toast.makeText(this, "��������������������������", 0).show();
//                    return;
//                }
//                Toast.makeText(this, "��������������������", 0).show();
//                return;
//        }
//        if (this.text_msg.getText().toString() != null) {
//            if ((returnResultMultiple(this.text_msg.getText().toString()).length() == 16) || (returnResultMultiple(this.text_msg.getText().toString()).length() == 15)) {
//                this.connectThread.sendMsg(this.androidId + "S" + returnResultMultiple(this.text_msg.getText().toString()));
//                Toast.makeText(this, "������������", 0).show();
//                return;
//            }
//            Toast.makeText(this, "��������������������������", 0).show();
//            return;
//        }
//        Toast.makeText(this, "��������������������", 0).show();
//    }
//
//    protected void onCreate(Bundle paramBundle) {
//        super.onCreate(paramBundle);
//        setContentView(2130968601);
//        initView();
//        this.bTAdatper = BluetoothAdapter.getDefaultAdapter();
//        initReceiver();
//        this.listenerThread = new ListenerThread(null);
//        this.listenerThread.start();
//        this.btn_kaimen = ((Button) findViewById(2131492946));
//        this.btn_kaimen.setOnClickListener(this);
//        this.btn_kaideng = ((Button) findViewById(2131492947));
//        this.btn_kaideng.setOnClickListener(this);
//        this.btn_kaifengshan = ((Button) findViewById(2131492948));
//        this.btn_kaifengshan.setOnClickListener(this);
//        this.btn_openBT = ((Button) findViewById(2131492936));
//        this.btn_openBT.setOnClickListener(this);
//        this.listView1 = new ListView(this);
//        androidId_data = "7659483c763e8649";
//        if (!this.bTAdatper.isEnabled()) {
//            this.btn_openBT.setText("��������");
//            this.switch_bluetooth = true;
//            return;
//        }
//        this.btn_openBT.setText("��������");
//        this.switch_bluetooth = false;
//    }
//
//    protected void onDestroy() {
//        super.onDestroy();
//        if ((this.bTAdatper != null) && (this.bTAdatper.isDiscovering())) {
//            this.bTAdatper.cancelDiscovery();
//        }
//        unregisterReceiver(this.mReceiver);
//    }
//
//    boolean shifouhanyouwenzi(String paramString) {
//        if (paramString.equals("")) {
//        }
//        int i;
//        do {
//            return false;
//            i = paramString.charAt(0);
//        } while (((i >= 65) && (i <= 122)) || ((i >= 48) && (i <= 57)));
//        return true;
//    }
//
//    public class ConnectThread extends Thread {
//        private boolean activeConnect;
//        InputStream inputStream;
//        OutputStream outputStream;
//        private BluetoothSocket socket;
//
//        public ConnectThread(BluetoothSocket paramBluetoothSocket, boolean paramBoolean) {
//            this.socket = paramBluetoothSocket;
//            this.activeConnect = paramBoolean;
//        }
//
//        public void run() {
//            try {
//                if (this.activeConnect) {
//                    this.socket.connect();
//                }
//                ConnectActivity.this.text_state.post(new Runnable() {
//                    public void run() {
//                        ConnectActivity.this.text_state.setText(ConnectActivity.this.getResources().getString(2131099677));
//                        ConnectActivity.this.lianjie_state = true;
//                    }
//                });
//                this.inputStream = this.socket.getInputStream();
//                this.outputStream = this.socket.getOutputStream();
//                for (; ; ) {
//                    ConnectActivity.this.bytes = this.inputStream.read(ConnectActivity.this.buffer);
//                    if (ConnectActivity.this.bytes > 0) {
//                        final byte[] arrayOfByte = new byte[ConnectActivity.this.bytes];
//                        System.arraycopy(ConnectActivity.this.buffer, 0, arrayOfByte, 0, ConnectActivity.this.bytes);
//                        ConnectActivity.this.text_msg.post(new Runnable() {
//                            public void run() {
//                                ConnectActivity.this.text_msg.setText(ConnectActivity.this.getResources().getString(2131099684) + new String(arrayOfByte));
//                            }
//                        });
//                    }
//                }
//                return;
//            } catch (IOException localIOException) {
//                localIOException.printStackTrace();
//                ConnectActivity.this.text_state.post(new Runnable() {
//                    public void run() {
//                        ConnectActivity.this.text_state.setText(ConnectActivity.this.getResources().getString(2131099675));
//                        ConnectActivity.this.lianjie_state = false;
//                    }
//                });
//            }
//        }
//
//        public void sendMsg(final String paramString) {
//            byte[] arrayOfByte = paramString.getBytes();
//            if (this.outputStream != null) {
//            }
//            try {
//                this.outputStream.write(arrayOfByte);
//                ConnectActivity.this.text_msg.post(new Runnable() {
//                    public void run() {
//                        ConnectActivity.this.text_msg.setText(ConnectActivity.this.getResources().getString(2131099704) + paramString);
//                    }
//                });
//                return;
//            } catch (IOException localIOException) {
//                localIOException.printStackTrace();
//                ConnectActivity.this.text_msg.post(new Runnable() {
//                    public void run() {
//                        ConnectActivity.this.text_msg.setText(ConnectActivity.this.getResources().getString(2131099703) + paramString);
//                    }
//                });
//            }
//        }
//    }
//
//    private class ListenerThread
//            extends Thread {
//        private BluetoothServerSocket serverSocket;
//        private BluetoothSocket socket;
//
//        private ListenerThread() {
//        }
//
//        public void run() {
//            try {
//                this.serverSocket = .this.bTAdatper.listenUsingRfcommWithServiceRecord("BT_DEMO", ConnectActivity.BT_UUID);
//                for (; ; ) {
//                    this.socket = this.serverSocket.accept();
//                    ConnectActivity.this.text_state.post(new Runnable() {
//                        public void run() {
//                            ConnectActivity.this.text_state.setText(ConnectActivity.this.getResources().getString(2131099679));
//                        }
//                    });
//                    ConnectActivity.this.connectThread = new ConnectActivity.ConnectThread(ConnectActivity.this, this.socket, false);
//                    ConnectActivity.this.connectThread.start();
//                }
//                return;
//            } catch (IOException localIOException) {
//                localIOException.printStackTrace();
//            }
//        }
//    }
//}
