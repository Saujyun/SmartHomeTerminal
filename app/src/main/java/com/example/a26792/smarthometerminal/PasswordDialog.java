package com.example.a26792.smarthometerminal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.a26792.smarthometerminal.bean.Protocols;
import com.example.a26792.smarthometerminal.utils.SharedPreferencesUtil;

import java.nio.Buffer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ${Saujyun} on 2019/5/20.
 */
public class PasswordDialog extends Dialog {
    @BindView(R.id.title_DL)
    TextView textView;
    @BindView(R.id.password_et)
    EditText password_et;
    @BindView(R.id.yes)
    Button yes;
    private onYesOnclickListener yesOnclickListener;
    private String title="请输入密码：";

    public PasswordDialog( Context context) {
        super(context);
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {

        this.yesOnclickListener = onYesOnclickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_layout);
        ButterKnife.bind(this);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        textView.setText(title);
        //初始化界面控件的事件
        initEvent();
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    password_et.setTag(password_et.getText());
                    if (!SharedPreferencesUtil.sharedPreferences.contains("userpassword")) {
                        if (password_et.getText().toString().equals("123456"))
                            yesOnclickListener.onYesClick(true);
                        else yesOnclickListener.onYesClick(false);
                    }else {
                        if (password_et.getText().toString().equals(SharedPreferencesUtil.sharedPreferences.getString("userpassword","error")))
                            yesOnclickListener.onYesClick(true);
                        else yesOnclickListener.onYesClick(false);
                    }
                }
            }
        });
    }

    public void setTitle(String title) {
        this.title=title;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick(boolean rigth);
    }

}
