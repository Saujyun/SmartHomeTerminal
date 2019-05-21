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
                if (Protocols.userAndroidId.equals(Protocols.getRootAndroidId()) && order.charAt(0) == 'Z') {
                    Log.e(TAG, "接收到注册请求：");
                    EventBus.getDefault().post(new EventMessage("receiveRegister", order));
                }else {
                    EventBus.getDefault().post(new EventMessage("test", order));
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
        //回复普通手机用户的注册请求
        if (message.getMessgae().equals("agress")) {
            write("1".getBytes());
        } else if (message.getMessgae().equals("unagress")) {
            write("0".getBytes());
        }
        switch (message.getMessgae()) {
            case "openDoor":
                write(Transform.strToByteArray(Protocols.getOpenDoor(Protocols.userAndroidId)));
                break;
            case "closeDoor":
                write(Transform.strToByteArray(Protocols.getCloseDoor(Protocols.userAndroidId)));
                break;
            case "tianjia":
                Log.e(TAG, "tianjia: " + Protocols.getRegister(message.getOrder()));
                write(Transform.strToByteArray(Protocols.getRegister(message.getOrder())));
                break;
            case "shanchu":
                Log.e(TAG, "shanchu: " + Protocols.getRegister(message.getOrder()));
                write(Transform.strToByteArray(Protocols.getDeleteUser(message.getOrder())));
                break;
            case "record":
                Log.e(TAG, "record: " + Protocols.getRegister(message.getOrder()));
                write(Transform.strToByteArray(Protocols.getRecord(null)));
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
            Log.e(TAG, "cancel: " );
            EventBus.getDefault().unregister(this);
        } catch (IOException e) {
        }
    }

}