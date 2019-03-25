package com.example.a26792.smarthometerminal;

import android.os.Bundle;


import android.content.Intent;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.a26792.smarthometerminal.utils.LogUtil;

import butterknife.BindViews;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindViews({R.id.button_connect, R.id.button_disconnect, R.id.button_search, R.id.button_generate_QR})
    Button btn_connect;
    Button btn_disconnect;
    Button btn_search;
    Button btn_QR;


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);

    }

    @OnClick(value = {R.id.button_connect, R.id.button_disconnect, R.id.button_search, R.id.button_generate_QR})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_connect:
                Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                startActivity(intent);
                break;
            case R.id.button_disconnect:
                LogUtil.loge(TAG, "discoonect");
                break;
            case R.id.button_search:
                LogUtil.loge(TAG, "search");
                break;
            default:
                break;
        }
    }


}
