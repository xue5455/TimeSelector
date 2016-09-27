package com.zxmn.time.utils;

import android.util.Log;

/**
 * Created by XUE on 2016/9/27.
 */
public class Logger {
    private static final String TAG = "WheelView";

    public static void d(String msg) {
        Log.d(TAG,msg);
    }
    public static void d(int msg) {
        Log.d(TAG,String.valueOf(msg));
    }
}
