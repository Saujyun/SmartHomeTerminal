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
                forwardOrder(order);

            } catch (IOException e) {
                break;
            }
        }
    }

    /**
     * 转发命令
     *
     * @param order
     */
    private void forwardOrder(String order) {
        if (order.equals("1")) {
            Log.e(TAG, "run: 管理员同意您的申请:");
            MainActivity.isRegistered = true;
            SharedPreferences.Editor editor = SharedPreferencesUtil.sharedPreferences.edit();
            editor.putBoolean("isRegistered", true);
            editor.commit();
            EventBus.getDefault().post(new EventMessage("agress", null));
        } else if (order.equals("0")) {
            Log.e(TAG, "run: 管理员不同意您的申请:");
            EventBus.getDefault().post(new EventMessage("unagress", null));
        }
        if (order.charAt(0) == 'M') {
            Log.e(TAG, "forwardOrder: M");
            SharedPreferences.Editor editor = SharedPreferencesUtil.sharedPreferences.edit();
            editor.putString("password", order);
            editor.commit();
            EventBus.getDefault().postSticky(new EventMessage("updataQRcode", null));
        }
        if (order.charAt(0) == 'F') {
            EventBus.getDefault().post(new EventMessage("fire", null));
        }
        if (order.charAt(0) == 'A') {
            EventBus.getDefault().post(new EventMessage("air", null));
        }
        EventBus.getDefault().post(new EventMessage("test", order));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void agress(EventMessage message) {
        String order = "";
        switch (message.getMessgae()) {
            case "register":
                order = Protocols.getRegisterFromOthers();
                Log.e(TAG, "register: " + order);
                break;
            case "openDoor":
                order = Protocols.getOpenDoor(Protocols.userAndroidId);
                Log.e(TAG, "openDoor: " + order);
                break;
            case "closeDoor":
                order = Protocols.getCloseDoor(Protocols.userAndroidId);
                break;
            case "openLight":
                order = Protocols.getLight(Protocols.userAndroidId, 1);
                Log.e(TAG, "openLight: " + order);
                break;
            case "closeLight":
                order = Protocols.getLight(Protocols.userAndroidId, 0);
                break;
            case "record":
                order = Protocols.getRecord(Protocols.userAndroidId);
                break;
            case "request":
                order = Protocols.getPassword();
                break;
            case "change":
                order = Protocols.changeRoot();
                break;
            case "closesocket":
                cancel();
                break;
            default:
                break;
        }
        if (!order.equals("")) {
            write(Transform.strToByteArray(order));
            EventBus.getDefault().post(new EventMessage("sendMessage", order));

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
        if (mmSocket.isConnected()) {
            try {
                mmSocket.close();
                Log.e(TAG, "cancel: ");
                EventBus.getDefault().unregister(this);
            } catch (IOException e) {
            }
        }

    }


}