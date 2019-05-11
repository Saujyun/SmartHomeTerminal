package com.example.a26792.smarthometerminal.utils;

import android.support.annotation.Nullable;

/**
 * Created by ${Saujyun} on 2019/5/11.
 */
public class EventMessage {


    private String message;
    private String order;

    public EventMessage(String message,@Nullable String order) {
        this.message = message;
        this.order=order;
    }

    public String getMessgae() {
        return message;
    }

    public String getOrder() {
        return order;
    }
}
