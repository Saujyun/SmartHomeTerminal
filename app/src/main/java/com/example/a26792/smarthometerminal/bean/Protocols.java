package com.example.a26792.smarthometerminal.bean;

import android.provider.Settings;
import android.support.annotation.Nullable;

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
    private static String openDoor = "O";
    private static String closeDoor = "C";
    private static String record="record";


    public static String getRegister(String userAndroidId) {
        return "R" + rootAndroidId + userAndroidId;
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

    public static String getRecord(@Nullable  String userAndroidId) {
        return "D"+rootAndroidId+"R";
    }

    public static String getRegisterFromOthers() {
        return "Z"+userAndroidId;
    }
}
