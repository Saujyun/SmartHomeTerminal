//package com.example.a26792.smarthometerminal;
//
//
//import android.bluetooth.BluetoothDevice;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//
///**
// * Created by ${Saujyun} on 2019/3/24.
// */
//public class BlueToothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
//    private final LayoutInflater mInflater;
//    private int mResource;
//
//    public BlueToothDeviceAdapter(Context paramContext, int paramInt) {
//        super(paramContext, paramInt);
//        this.mInflater = LayoutInflater.from(paramContext);
//        this.mResource = paramInt;
//    }
//
//    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
//        View localView = paramView;
//        if (paramView == null) {
//            localView = this.mInflater.inflate(this.mResource, paramViewGroup, false);
//        }
//        paramView = (TextView) localView.findViewById(2131492952);
//        paramViewGroup = (TextView) localView.findViewById(2131492953);
//        BluetoothDevice localBluetoothDevice = (BluetoothDevice) getItem(paramInt);
//        paramView.setText(localBluetoothDevice.getName());
//        paramViewGroup.setText(localBluetoothDevice.getAddress());
//        return localView;
//    }
//}
