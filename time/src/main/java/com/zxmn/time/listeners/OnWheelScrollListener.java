package com.zxmn.time.listeners;

/**
 * Created by XUE on 2016/9/27.
 */
public interface OnWheelScrollListener {
    
    void onScrolled(int dy);

    void onScrollStateChanged(int newState);

    int STATE_DRAG = 1;
    int STATE_FLING = 2;
    int STATE_IDLE = 3;
}
