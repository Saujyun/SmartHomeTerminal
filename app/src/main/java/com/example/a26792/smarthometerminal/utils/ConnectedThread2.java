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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ${Saujyun} on 2019/5/10.
 */

public class ConnectedThread2 extends Thread {
    private static final String TAG="ConnectedThreadtest";
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
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                //其实这个bytes返回的是输入流的长度，buffer[]才是返回的数据

                bytes = mmInStream.read(buffer);
                Log.e(TAG, "run: "+bytes );
                final String order=byteArrayToStr(buffer,bytes);
                // Send the obtained bytes to the UI activity
                Log.e("test", "run: "+ order);
//                if (order.equals("1")){
//                    Log.e(TAG, "run: 管理员同意您的申请:");
//                    MainActivity.isRegistered=true;
//                    SharedPreferences.Editor editor=SharedPreferencesUtil.sharedPreferences.edit();
//                    editor.putBoolean("isRegistered",true);
//                    editor.commit();
//                }else {
//                    Toast.makeText(MyApplication.getContext(),"管理员不同意您的申请:",Toast.LENGTH_SHORT).show();
//                }
            } catch (IOException e) {
                break;
            }
        }
    }


    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
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