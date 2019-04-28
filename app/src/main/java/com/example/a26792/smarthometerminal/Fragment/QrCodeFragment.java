package com.example.a26792.smarthometerminal.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a26792.smarthometerminal.R;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ${Saujyun} on 2019/4/28.
 */
public class QrCodeFragment extends Fragment {

    ImageView qrcode_iv;
    private Bitmap mBitmap;
    private String TAG="QrCodeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.qrcode_layout,container,false);
        qrcode_iv=view.findViewById(R.id.qrcode_iv);
        generateQRCode();
        return view;
    }

    private void generateQRCode() {
        String textContent = "test for QRCode";
        if (TextUtils.isEmpty(textContent)) {
            Toast.makeText(getContext(), "您的输入为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        mBitmap = CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        if (mBitmap==null||qrcode_iv==null){
            Log.e(TAG, "generateQRCode: ");
        }else {
            qrcode_iv.setImageBitmap(mBitmap);
        }

    }
}
