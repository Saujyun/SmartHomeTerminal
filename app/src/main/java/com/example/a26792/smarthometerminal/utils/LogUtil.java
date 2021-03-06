package com.example.a26792.smarthometerminal.utils;

import android.util.Log;

/**
 * Created by ${Saujyun} on 2019/3/24.
 * Log工具类
 */
public class LogUtil {
    private static int level = 10;

    public static void loge(String tag, String string) {
        if (level > 0) {
            Log.e(tag, string);
        }
    }

    public static void logd(String tag, String string) {
        if (level > 0) {
            Log.d(tag, string);
        }
    }

    public static void logw(String tag, String string) {
        if (level > 0) {
            Log.w(tag, string);
        }
    }

    public static void logi(String tag, String string) {
        if (level > 0) {
            Log.i(tag, string);
        }
    }

    public static void logv(String tag, String s) {
        if (level > 0) {
            Log.v(tag, s);
        }
    }
}
