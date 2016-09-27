package com.zxmn.time.utils;

import android.content.Context;

/**
 * Created by XUE on 2016/9/27.
 */
public class DensityUtils {
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
