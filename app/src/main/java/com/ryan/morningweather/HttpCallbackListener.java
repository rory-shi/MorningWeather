package com.ryan.morningweather;

/**
 * Created by rory9 on 2016/1/3.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
