package com.example.a26792.smarthometerminal.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by ${Saujyun} on 2019/4/30.
 * 服务端的线程
 */
public class AcceptThread extends Thread {
    private static final String TAG = "AcceptThreadtest";
    private static final String NAME = "accpet";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter mBluetoothAdapter;
    private ConnectedThread connectedThread;

    public AcceptThread(BluetoothAdapter bluetoothAdapter) {

        this.mBluetoothAdapter = bluetoothAdapter;
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) {
        }
        mmServerSocket = tmp;
    }

    public void run() {
        Log.e(TAG, "run: ");
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {

            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                Log.e(TAG, "run:已连接设备，开启接收线程 ");
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        synchronized (this){
            ConnectedThread connectedThread = new ConnectedThread(socket);
            connectedThread.start();
            this.notifyAll();
        }

        // InputStream inputStream=socket.getInputStream();
        Log.e(TAG, "manageConnectedSocket: ");

    }
    public ConnectedThread getConnectedThread() {
        if (connectedThread == null) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return connectedThread;
            }
        } else {
            return connectedThread;
        }

    }
    /**
     * Will cancel the listening socket, and cause the thread to finish
     */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
        }
    }
}