package com.example.a26792.smarthometerminal.bean;

import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.a26792.smarthometerminal.utils.MyApplication;

/**
 * Created by ${Saujyun} on 2019/5/10.
 */
public class Protocols {

    private static String register = "";
    private static String registerFromOthers = "";
    private static String deleteUser = "";
    private static String rootAndroidId = "6cd7a0115ddabd6d";//默认管理员AndroidID
    public static String userAndroidId = "";
    private static String openDoor = "1";
    private static String closeDoor = "0";
    private static String record = "record";
    private static String password = "";
    private static String light = "L";
    private static String environment = "G";

    public static String getLight(String userAndroidId, int lightswitch) {
        if (lightswitch == 1) {
            return light + userAndroidId + "1";
        }
        return light + userAndroidId + "0";
    }

    public static String getEnvironment(String userAndroidId) {
        return environment + userAndroidId;
    }

    public static String getRegister(String userAndroidId) {
        //由于直接使用命令来进行注册，所以命令中的userAndroidId前面带有“Z”
        return "R" + rootAndroidId + userAndroidId;
    }

    public static String getDeleteUser(String userAndroidId) {
        String temp = userAndroidId.substring(1);

        return "R" + rootAndroidId + "S" + temp;
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

    public static String getRecord(@Nullable String userAndroidId) {
        return "D" + rootAndroidId + "R";
    }

    public static String getRegisterFromOthers() {
        return "T" + userAndroidId;
    }

    public static String getPassword() {
        return "M" + userAndroidId;
    }

    public static void setRootAndroidId(String rootAndroidId) {
        Protocols.rootAndroidId = rootAndroidId;

    }

    public static String changeRoot() {
        return "C" + rootAndroidId + "T" + userAndroidId;
    }
}
