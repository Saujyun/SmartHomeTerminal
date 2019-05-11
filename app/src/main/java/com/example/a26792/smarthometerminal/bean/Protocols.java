package com.example.a26792.smarthometerminal.bean;

import android.provider.Settings;

import com.example.a26792.smarthometerminal.utils.MyApplication;

/**
 * Created by ${Saujyun} on 2019/5/10.
 */
public class Protocols {

    private static String register = "";
    private static String registerFromOthers = "";
    private static String deleteUser = "";
    private static String rootAndroidId = "6cd7a0115ddabd6d";
    public static String userAndroidId = "";
    private static String openDoor = "1";
    private static String closeDoor = "0";
    private static String record="record";


    public static String getRegister(String userAndroidId) {
        return "Z" + rootAndroidId + userAndroidId;
    }

    public static String getDeleteUser(String userAndroidId) {
        return "S" + rootAndroidId + userAndroidId;
    }

    public static String getRootAndroidId() {
        return rootAndroidId;
    }


    public static String getOpenDoor(String userAndroidId) {
        return "D" + userAndroidId + openDoor;
    }

    public static String getCloseDoor(String userAndroidId) {
        return "D" + userAndroidId + closeDoor;
    }

    public static String getRecord(String userAndroidId) {
        return userAndroidId+record;
    }

    public static String getRegisterFromOthers() {
        return "R"+userAndroidId;
    }
}
