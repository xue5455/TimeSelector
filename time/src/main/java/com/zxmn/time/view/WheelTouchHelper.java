package com.zxmn.time.view;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Scroller;

import com.zxmn.time.listeners.OnWheelScrollListener;


/**
 * Created by XUE on 2016/9/27.
 */
public class WheelTouchHelper {
    private OnWheelScrollListener mListener;

    private float mLastY;

    private VelocityTracker mVelocityTracker;

    private Scroller mScroller;

    private WheelView mView;

    private int mLastFlingY;

    private int mMaxFlingDistance;

    private int mViewHeight;

    public WheelTouchHelper(WheelView view) {
        mListener = view;
        mVelocityTracker = VelocityTracker.obtain();
        mScroller = new Scroller(view.getContext());
        mView = view;
    }

    public void setViewHeight(int viewHeight) {
        mViewHeight = viewHeight;
        mMaxFlingDistance = (int) (1.5 * viewHeight);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastY = event.getY();
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float y = event.getY();
                int deltaY = (int) (mLastY - y);
                if (mListener != null) {
                    mListener.onScrolled(deltaY);
                }
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                mLastFlingY = 0;
                mScroller.fling(0, 0, 0, (int) mVelocityTracker.getYVelocity(), 0, 0, -mMaxFlingDistance, mMaxFlingDistance);
                mView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                return false;
        }
        return true;
    }

    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int dy = mLastFlingY - mScroller.getCurrY();
            mLastFlingY = mScroller.getCurrY();
            mListener.onScrolled(dy);
        }
    }
}
