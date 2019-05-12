package com.example.a26792.smarthometerminal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.a26792.smarthometerminal.bean.Protocols;
import com.example.a26792.smarthometerminal.utils.SharedPreferencesUtil;

/**
 * Created by ${Saujyun} on 2019/5/9.
 */
public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivitytest";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        //获取本机Android_id
        Protocols.userAndroidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e(TAG, "onCreate: " + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        //先删除上一次保存的数据
        SharedPreferencesUtil lastSharedPreferencesUtil = new SharedPreferencesUtil(getApplicationContext());
        SharedPreferencesUtil.clear();
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getApplicationContext());
    }

    public void rootLogin(View view) {
        if (Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).equals("6cd7a0115ddabd6d")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", "root");
            startActivity(intent);
        } else {
            Toast.makeText(this, "对不起你不是管理员用户，请注册为普通用户", Toast.LENGTH_SHORT).show();
        }

    }

    public void othersLogin(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("若未曾注册，跳转之后请通过蓝牙连接管理员账号完成注册操作，否则无法正常使用！");
        builder.setCancelable(false);
        builder.setPositiveButton("好的，谢谢", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("user", "others");
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
