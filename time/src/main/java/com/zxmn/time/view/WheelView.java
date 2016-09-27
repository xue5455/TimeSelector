package com.zxmn.time.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zxmn.time.R;
import com.zxmn.time.adapter.WheelAdapter;
import com.zxmn.time.listeners.OnWheelScrollListener;
import com.zxmn.time.utils.DensityUtils;


/**
 * Created by XUE on 2016/9/27.
 */
public class WheelView extends View implements OnWheelScrollListener, WheelAdapter.OnDataSetChangedListener {

    private WheelAdapter mAdapter;

    private int mItemHeight;

    private int mTotalHeight;

    private float mTextSize;

    private float mScaleFactor = 1.2f;

    private int mScrollY = 0;

    private TextPaint mNormalPaint;

    private TextPaint mScaledPaint;

    private Paint mLinesPaint;

    private Paint mClearPaint;

    private int mNormalColor;

    private int mHighLightColor;

    private int mUpperEdge;
    private int mLowerEdge;

    private WheelTouchHelper mTouchHelper;


    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        mTextSize = DensityUtils.dip2px(getContext(), 15);
        //init colors
        mHighLightColor = ContextCompat.getColor(getContext(), R.color.wheel_high_light_color);
        mNormalColor = ContextCompat.getColor(getContext(), R.color.wheel_normal_color);

        //init normal paint;
        mNormalPaint = getPaint(mTextSize, mNormalColor);
        //init scaled paint
        mScaledPaint = getPaint(mTextSize * mScaleFactor, mHighLightColor);

        //init lines paint
        mLinesPaint = getPaint(getContext().getResources().getDimensionPixelSize(R.dimen.wheel_edge_width), mNormalColor);
        //init clear paint
        initClearPaint();
        mTouchHelper = new WheelTouchHelper(this);
    }

    private void initClearPaint() {
        mClearPaint = new Paint();
        mClearPaint.setColor(Color.TRANSPARENT);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    private Paint getPaint(int strokeWidth, int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        return paint;
    }

    private TextPaint getPaint(float textSize, int color) {
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(color);
        return textPaint;
    }

    public void setAdapter(WheelAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetChangedListener(this);
        }
        mAdapter = adapter;
        adapter.registerDataSetChangedListener(this);
        mItemHeight = adapter.getItemHeight();
        mTotalHeight = adapter.getItemHeight() * adapter.getItemCount();
        post(new Runnable() {
            @Override
            public void run() {
                mUpperEdge = getMeasuredHeight() / 2 - mItemHeight / 2;
                mLowerEdge = mUpperEdge + mItemHeight;
                mScrollY = mTotalHeight - getMeasuredHeight() / 2 + mItemHeight / 2;
                invalidate();
            }
        });
    }


    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }

    public void setNormalTextColor(int textColor) {
        mNormalColor = textColor;
        invalidate();
    }

    public void setHighLightColor(int textColor) {
        mHighLightColor = textColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAdapter == null)
            return;
        drawContent(canvas);
        drawEdges(canvas);
    }

    private void drawContent(Canvas canvas) {
        int firstPosition = mScrollY / mItemHeight;
        float top = -mScrollY % mItemHeight;
        int itemCount = (int) Math.ceil((getMeasuredHeight() - top) / mItemHeight);
        float x = getMeasuredWidth() / 2;
        Paint.FontMetrics fontMetrics = mNormalPaint.getFontMetrics();
        for (int i = 0; i < itemCount; i++) {
            String content = mAdapter.getItem(firstPosition);
            int baseline = (int) ((top + mItemHeight + top - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(content, x - mNormalPaint.measureText(content) / 2, baseline, mNormalPaint);
            top += mItemHeight;
            if (++firstPosition >= mAdapter.getItemCount()) {
                firstPosition = 0;
            }
        }
    }

    private void drawEdges(Canvas canvas) {
        canvas.drawRect(0, mUpperEdge, getMeasuredWidth(), mLowerEdge, mClearPaint);
        canvas.drawLine(0, mUpperEdge, getMeasuredWidth(), mUpperEdge, mLinesPaint);
        canvas.drawLine(0, mLowerEdge, getMeasuredWidth(), mLowerEdge, mLinesPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mTouchHelper != null &&
                mTouchHelper.onTouchEvent(event);
    }

    @Override
    public void onScrolled(int dy) {
        mScrollY += dy;
        if (mScrollY >= mTotalHeight) {
            mScrollY -= mTotalHeight;
        } else if (mScrollY < 0) {
            mScrollY += mTotalHeight;
        }
        invalidate();
    }

    public void setScaledFactor(float factor) {
        mScaleFactor = factor;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        mTouchHelper.setViewHeight(getMeasuredHeight());
    }

    @Override
    public void onDataSetChanged() {
        mTotalHeight = mAdapter.getItemHeight() * mAdapter.getItemCount();
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mTouchHelper != null)
            mTouchHelper.computeScroll();
        super.computeScroll();
    }


    public void scrollToPosition(int position) {
        if (position < 0 || mAdapter == null || position >= mAdapter.getItemCount()) {
            return;
        }
        mScrollY = position * mItemHeight + getMeasuredHeight() / 2;
        invalidate();
    }

    public void setCurrentPosition(int position) {
        if (position < 0 || mAdapter == null || position >= mAdapter.getItemCount()) {
            return;
        }
        //
        invalidate();
    }
}
