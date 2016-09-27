package com.zxmn.time.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zxmn.time.R;
import com.zxmn.time.adapter.HourAdapter;
import com.zxmn.time.adapter.MinuteAdapter;

/**
 * Created by XUE on 2016/9/24.
 */
public class TimeSelector extends FrameLayout {

    private WheelView mHourView;

    private WheelView mMinuteView;


    public TimeSelector(Context context) {
        this(context, null);
    }

    public TimeSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_time_selector, this, false);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHourView = (WheelView) findViewById(R.id.wv_hour);
        mMinuteView = (WheelView) findViewById(R.id.wv_minute);
        int itemHeight = getResources().getDimensionPixelSize(R.dimen.wheel_item_height);
        mHourView.setAdapter(new HourAdapter(itemHeight));
        mMinuteView.setAdapter(new MinuteAdapter(itemHeight));
    }
}
