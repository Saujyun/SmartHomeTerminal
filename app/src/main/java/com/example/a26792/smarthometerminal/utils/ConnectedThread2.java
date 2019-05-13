package com.example.a26792.smarthometerminal.utils;


import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.a26792.smarthometerminal.MainActivity;
import com.example.a26792.smarthometerminal.bean.Protocols;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * 客户端建立蓝牙连接后所开启的线程
 * Created by ${Saujyun} on 2019/5/10.
 */

public class ConnectedThread2 extends Thread {
    private static final String TAG = "ConnectedThread2test";
    private static final int MESSAGE_READ = 1;
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mHandler;

    public ConnectedThread2(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        EventBus.getDefault().register(this);
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        int status = 1;
        while (true) {
            try {
                // Read from the InputStream
                //其实这个bytes返回的是输入流的长度，buffer[]才是返回的数据
                if (status == 1) {
                    EventBus.getDefault().post(new EventMessage("connect", null));
                    status++;
                }
                bytes = mmInStream.read(buffer);
                Log.e(TAG, "run: " + bytes);
                final String order = Transform.byteArrayToStr(buffer, bytes);
                // Send the obtained bytes to the UI activity
                Log.e("test", "run: " + order);
                if (order.equals("1")) {
                    Log.e(TAG, "run: 管理员同意您的申请:");
                    MainActivity.isRegistered = true;
                    SharedPreferences.Editor editor = SharedPreferencesUtil.sharedPreferences.edit();
                    editor.putBoolean("isRegistered", true);
                    editor.commit();
                    EventBus.getDefault().post(new EventMessage("agress", null));
                } else {
                    Log.e(TAG, "run: 管理员不同意您的申请:");
                    EventBus.getDefault().post(new EventMessage("unagress", null));
                }
                if (order.charAt(0)=='M'){
                    SharedPreferencesUtil.editor.putString("password",order);
                    EventBus.getDefault().postSticky(new EventMessage("updataQRcode",null));
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void agress(EventMessage message) {

        switch (message.getMessgae()) {
            case "register":
                Log.e(TAG, "register: " + Protocols.getRegisterFromOthers());
                write(Transform.strToByteArray(Protocols.getRegisterFromOthers()));
                break;
            case "openDoor":
                Log.e(TAG, "openDoor: " + Protocols.getOpenDoor(Protocols.userAndroidId));
                write(Transform.strToByteArray(Protocols.getOpenDoor(Protocols.userAndroidId)));
                break;
            case "closeDoor":
                write(Transform.strToByteArray(Protocols.getCloseDoor(Protocols.userAndroidId)));
                break;
            case "record":
                write(Transform.strToByteArray(Protocols.getRecord(Protocols.userAndroidId)));
                break;
            case "request":
                write(Transform.strToByteArray(Protocols.getPassword()));
            default:
                break;
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
            Log.e(TAG, "cancel: ");
            EventBus.getDefault().unregister(this);
        } catch (IOException e) {
        }
    }


}