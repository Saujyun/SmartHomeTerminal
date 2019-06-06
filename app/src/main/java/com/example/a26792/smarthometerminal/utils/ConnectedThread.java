package com.example.a26792.smarthometerminal.utils;

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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
 * Created by ${Saujyun} on 2019/4/30.
 * 连接成功后，服务端发送数据
 */
public class ConnectedThread extends Thread {
    private static final String TAG = "ConnectedThreadtest";
    private static final int MESSAGE_READ = 1;
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mHandler;

    public ConnectedThread(BluetoothSocket socket) {
        EventBus.getDefault().register(this);
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
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        Log.e(TAG, "run:in ");
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                //其实这个bytes返回的是输入流的长度，buffer[]才是返回的数据
                bytes = mmInStream.read(buffer);
                final String order = Transform.byteArrayToStr(buffer, bytes);
                // Send the obtained bytes to the UI activity
                Log.e(TAG, "run: " + order);
                forwardOrder(order);
                //Protocols.userAndroidId.equals(Protocols.getRootAndroidId()) 不需要加此判断

            } catch (IOException e) {
                break;
            }
        }
    }

    private void forwardOrder(String order) {
        if (order.charAt(0) == 'T') {
            Log.e(TAG, "接收到注册请求：");
            EventBus.getDefault().post(new EventMessage("receiveRegister", order));
        }
        if (order.charAt(0) == 'M') {
            SharedPreferencesUtil.editor.putString("password", order);
            EventBus.getDefault().postSticky(new EventMessage("updataQRcode", null));
        }
        if (order.charAt(0) == 'F') {
            EventBus.getDefault().post(new EventMessage("fire", null));
        }
        if (order.charAt(0) == 'A') {
            EventBus.getDefault().post(new EventMessage("air", null));
        }
        if (order.charAt(0) == 'M') {
            write(Transform.strToByteArray("MtestQR"));
        }
        EventBus.getDefault().post(new EventMessage("test", order));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void agress(EventMessage message) {
        //回复普通手机用户的注册请求
        if (message.getMessgae().equals("agress")) {
            write("1".getBytes());
        } else if (message.getMessgae().equals("unagress")) {
            write("0".getBytes());
        }
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
            case "tianjia":
                order = Protocols.getRegister(message.getOrder());
                Log.e(TAG, "tianjia: " + order);
                break;
            case "shanchu":
                order = Protocols.getRegister(message.getOrder());
                Log.e(TAG, "shanchu: " + order);
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