package com.example.a26792.smarthometerminal.utils;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ${Saujyun} on 2019/4/30.
 * 连接成功后，发送数据
 */
public class ConnectedThread extends Thread {
    private static final String TAG="ConnectedThreadtest";
    private static final int MESSAGE_READ = 1;
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mHandler;

    public ConnectedThread(BluetoothSocket socket) {
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

                // Send the obtained bytes to the UI activity
                Log.e("test", "run: "+bytes );
//                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//                        .sendToTarget();
                if (buffer[1]==0||buffer[0]==0){
                    Log.e(TAG, "接收到数据： 0" );
                }
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
}