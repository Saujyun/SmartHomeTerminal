package com.example.a26792.smarthometerminal.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by ${Saujyun} on 2019/4/30.
 */
public class ConnectThread extends Thread {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG="ConnectThreadtest";
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mBluetoothAdapter;


    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter) {
        this.mBluetoothAdapter = bluetoothAdapter;
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();

        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                Log.e(TAG, "run: 无法连接，关闭socket" );
                mmSocket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        Log.e(TAG, "manageConnectedSocket: 连接成功，准备发送数据" );
      ConnectedThread connectedThread=new ConnectedThread(mmSocket);
      connectedThread.start();
      connectedThread.write(new byte[]{1,0});
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}