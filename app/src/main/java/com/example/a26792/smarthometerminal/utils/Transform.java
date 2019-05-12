package com.example.a26792.smarthometerminal.utils;

/**
 * Created by ${Saujyun} on 2019/5/11.
 * 负责byte[]2String+String2byte[]
 */
public class Transform {

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

    /**
     * byte[]转String
     *
     * @param byteArray
     * @return
     */
    public static String byteArrayToStr(byte[] byteArray, int length) {
        if (byteArray == null) {
            return null;
        }
        byte[] byteArray2 = new byte[length];
        for (int i = 0; i < length; i++) {
            byteArray2[i] = byteArray[i];
        }
        //  byteArray2=byteArray.clone();不可以这样子不然会出现乱码
        String str = new String(byteArray2);
        return str;
    }
}
