package com.zxmn.time.adapter;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by XUE on 2016/9/27.
 */
public class MinuteAdapter extends WheelAdapter {
    private ArrayList<String> mHourList;

    private int mItemHeight;

    public MinuteAdapter(int height) {
        mHourList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            mHourList.add(i >= 10 ? String.valueOf(i) : TextUtils.concat("0", String.valueOf(i)).toString());
        }
        mItemHeight = height;
    }

    @Override
    public String getItem(int position) {
        return mHourList.get(position);
    }

    @Override
    public int getItemCount() {
        return mHourList.size();
    }

    @Override
    public int getItemHeight() {
        return mItemHeight;
    }
}
