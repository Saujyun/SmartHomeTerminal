package com.example.a26792.smarthometerminal.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.MainThread;
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
import java.util.UUID;
import java.util.logging.Handler;

/**
 * Created by ${Saujyun} on 2019/4/30.
 */
public class ConnectThread extends Thread {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "ConnectThreadtest";
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mBluetoothAdapter;
    private ConnectedThread2 connectedThread;


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
                Log.e(TAG, "run: 无法连接，关闭socket");
                mmSocket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
            return;
        }
        EventBus.getDefault().post("c");

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);

    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        Log.e(TAG, "manageConnectedSocket: 连接成功，准备发送数据");
        synchronized (this) {
            connectedThread = new ConnectedThread2(mmSocket);
            connectedThread.start();
            if (!MainActivity.isRegistered) {
                //未曾注册，先发送注册信号
                send("register");
                EventBus.getDefault().post("sr");
            }
            this.notifyAll();
        }


    }



    public ConnectedThread2 getConnectedThread() {
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

    public boolean send(String s) {
        Log.e(TAG, "send: ");
        switch (s) {
            case "register":
                connectedThread.write(strToByteArray(Protocols.getRegister(Protocols.userAndroidId)));
                break;
            case "openDoor":
                connectedThread.write(strToByteArray(Protocols.getOpenDoor(Protocols.userAndroidId)));
                break;
            case "closeDoor":
                connectedThread.write(strToByteArray(Protocols.getCloseDoor(Protocols.userAndroidId)));
                break;
            case "record":
                connectedThread.write(strToByteArray(Protocols.getRecord(Protocols.userAndroidId)));
                break;
            default:
                break;
        }
        return true;
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

    /**
     * String转byte[]
     *
     * @param str
     * @return
     */
    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }


}