package com.example.a26792.smarthometerminal.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ${Saujyun} on 2019/5/11.
 */
public class SharedPreferencesUtil {
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public SharedPreferencesUtil(Context context){
        sharedPreferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean putString(String key, String string){
        editor.putString(key, string);
        return editor.commit();
    }
    public String getString(String key){
        String res = sharedPreferences.getString(key, "");
        if (res.equals(""))
            return null;
        return res;
    }

}
