package com.example.a26792.smarthometerminal.utils;

import android.support.annotation.Nullable;

/**
 * Created by ${Saujyun} on 2019/5/11.
 * message:代表哪个eventbus需要响应
 * order：额外的命令信息
 */
public class EventMessage {


    private String message;
    private String order;

    public EventMessage(String message, @Nullable String order) {
        this.message = message;
        this.order = order;
    }

    public String getMessgae() {
        return message;
    }

    public String getOrder() {
        return order;
    }
}
