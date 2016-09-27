package com.zxmn.jake;

import android.text.TextUtils;


import com.zxmn.time.adapter.WheelAdapter;

import java.util.ArrayList;

/**
 * Created by XUE on 2016/9/27.
 */
public class MyAdapter extends WheelAdapter {

    private ArrayList<String> mTimeList;

    private int mItemHeight;
    public MyAdapter(int itemHeight) {
        mTimeList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            mTimeList.add(i >= 10 ? String.valueOf(i) :
                    TextUtils.concat("0", String.valueOf(i)).toString());
        }
        mItemHeight = itemHeight;
    }

    @Override
    public String getItem(int position) {
        return mTimeList.get(position);
    }

    @Override
    public int getItemCount() {
        return mTimeList == null ? 0 : mTimeList.size();
    }

    @Override
    public int getItemHeight() {
        return mItemHeight;
    }
}
