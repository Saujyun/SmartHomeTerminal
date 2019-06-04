package com.example.a26792.smarthometerminal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a26792.smarthometerminal.Fragment.PassWordFragment;
import com.example.a26792.smarthometerminal.Fragment.QrCodeFragment;
import com.example.a26792.smarthometerminal.bean.Protocols;
import com.example.a26792.smarthometerminal.utils.SharedPreferencesUtil;

/**
 * Created by ${Saujyun} on 2019/5/9.
 */
public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivitytest";
    private boolean check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        //获取本机Android_id
        Protocols.userAndroidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e(TAG, "onCreate: " + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        //先删除上一次保存的数据
        // TODO: 2019/5/21 完成之后，要删除该部分代码，同时要把data/share_pre文件删除 
        SharedPreferencesUtil lastSharedPreferencesUtil = new SharedPreferencesUtil(getApplicationContext());
//        SharedPreferencesUtil.clear();
//        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getApplicationContext());
    }

    public void rootLogin(View view) {
        // TODO: 2019/5/21 自动登录功能
        final PasswordDialog passwordDialog = new PasswordDialog(this);
        passwordDialog.setYesOnclickListener(new PasswordDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(boolean right) {
                if (right) {
                    if (!SharedPreferencesUtil.sharedPreferences.contains("userpassword")) {
                        showChangePassword();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user", "root");
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "请登录之后，马上更改门禁系统的管理员AndroidID", Toast.LENGTH_SHORT).show();
// TODO: 2019/5/21  更改门禁系统的管理员AndroidID操作
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "对不起你不是管理员用户，请注册为普通用户", Toast.LENGTH_SHORT).show();
                }
                passwordDialog.dismiss();
            }


        });
        passwordDialog.show();
//        if (Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).equals("6cd7a0115ddabd6d")) {
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra("user", "root");
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "对不起你不是管理员用户，请注册为普通用户", Toast.LENGTH_SHORT).show();
//        }

    }

    private void showChangePassword() {
        final PasswordDialog passwordDialog2 = new PasswordDialog(this);
        passwordDialog2.setTitle("更改密码为：");
        passwordDialog2.setYesOnclickListener(new PasswordDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(boolean right) {
                String s = (String) passwordDialog2.findViewById(R.id.password_et).getTag().toString();
                SharedPreferences.Editor editor = SharedPreferencesUtil.sharedPreferences.edit();
                editor.putString("userpassword", s);
                editor.commit();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user", "root");
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "请登录之后，马上更改门禁系统的管理员AndroidID", Toast.LENGTH_SHORT).show();
// TODO: 2019/5/21  更改门禁系统的管理员AndroidID操作

                passwordDialog2.dismiss();

            }
        });
        passwordDialog2.show();
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
