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
 * 连接成功后，发送数据
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
                final String order = byteArrayToStr(buffer, bytes);
                // Send the obtained bytes to the UI activity
                Log.e(TAG, "run: " + order);
//
//                mHandler=new Handler(Looper.getMainLooper(), new Handler.Callback() {
//                    @Override
//                    public boolean handleMessage(Message msg) {
//                        if (msg.what==200){
//                            addAlertDialog(order);
//                        }
//                        Toast.makeText(MyApplication.getContext(),"接收到数据:" +msg.what,Toast.LENGTH_SHORT).show();
//                        if (msg.what==300){
//                            Toast.makeText(MyApplication.getContext(), "请连接门禁系统，为普通用户完成注册", Toast.LENGTH_SHORT).show();
//                        }
//                        return false;
//                    }
//                });

                if (Protocols.userAndroidId.equals(Protocols.getRootAndroidId()) && order.charAt(0) == 'Z') {
                    Log.e(TAG, "接收到注册请求：");
                    EventBus.getDefault().post(new EventMessage("receiveRegister", order));

                    //mHandler.sendEmptyMessage(200);
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void agress(EventMessage message) {
        if (message.getMessgae().equals("agress")) {
            write(new byte[]{1});
        } else if (message.getMessgae().equals("unagress")) {
            write(new byte[]{0});
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
            EventBus.getDefault().unregister(this);
        } catch (IOException e) {
        }
    }

    /**
     * byte[]转String
     * @param byteArray
     * @return
     */
    public static String byteArrayToStr(byte[] byteArray,int length) {
        if (byteArray == null) {
            return null;
        }
        byte[]byteArray2=new byte[length];
        for (int i=0;i<length;i++){
            byteArray2[i]=byteArray[i];
        }
        //  byteArray2=byteArray.clone();不可以这样子不然会出现乱码
        String str = new String(byteArray2);
        return str;
    }
}