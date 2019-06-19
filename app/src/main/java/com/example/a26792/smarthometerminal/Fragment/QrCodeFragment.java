package com.example.a26792.smarthometerminal.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a26792.smarthometerminal.R;
import com.example.a26792.smarthometerminal.utils.EventMessage;
import com.example.a26792.smarthometerminal.utils.SharedPreferencesUtil;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ${Saujyun} on 2019/4/28.
 */
public class QrCodeFragment extends Fragment {

    ImageView qrcode_iv;
    private Bitmap mBitmap;
    private String TAG = "QrCodeFragmenttest";
    private String path;

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qrcode_layout, container, false);
        qrcode_iv = view.findViewById(R.id.qrcode_iv);
        EventBus.getDefault().register(this);
        //禁止截图
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        generateQRCode();
    }

    private void generateQRCode() {
        Log.e(TAG, "generateQRCode: ");
        String textContent = "test for QRCode";
        if (SharedPreferencesUtil.sharedPreferences.contains("password")) {
            textContent = SharedPreferencesUtil.sharedPreferences.getString("password", "");
        }
        if (TextUtils.isEmpty(textContent)) {
            Toast.makeText(getContext(), "您的输入为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        mBitmap = CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        if (mBitmap == null || qrcode_iv == null) {
            Log.e(TAG, "generateQRCode: ");
        } else {
            qrcode_iv.setBackground(null);
            qrcode_iv.setImageBitmap(mBitmap);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updataQRcode(EventMessage eventMessage) {
        if (eventMessage.getMessgae().equals("updataQRcode")) {
            Log.e(TAG, "updataQRcode: ");
            generateQRCode();
        }

    }

//    /**
//     * 保存位图到本地
//     *
//     * @param bitmap
//     * @param path   本地路径
//     * @return void
//     */
//    public void savaImage(Bitmap bitmap, String path) {
//        File file = new File(path);
//        FileOutputStream fileOutputStream = null;
//        //文件夹不存在，则创建它
//        if (!file.exists()) {
//            file.mkdir();
//        }
//        try {
//            fileOutputStream = new FileOutputStream(path + ".png");
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//            fileOutputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
