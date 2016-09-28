package com.zxmn.time.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zxmn.time.R;
import com.zxmn.time.adapter.WheelAdapter;
import com.zxmn.time.listeners.OnItemSelectedListener;
import com.zxmn.time.listeners.OnWheelScrollListener;
import com.zxmn.time.utils.Logger;

import java.util.ArrayList;


/**
 * Created by XUE on 2016/9/27.
 */
public class WheelView extends View implements OnWheelScrollListener, WheelAdapter.OnDataSetChangedListener {

    private WheelAdapter mAdapter;

    private int mItemHeight;

    private int mTotalHeight;

    private float mTextSize;

    private float mScaleFactor = 1.3f;

    private int mScrollY = 0;

    private TextPaint mNormalPaint;

    private TextPaint mScaledPaint;

    private Paint mLinesPaint;

    private Paint mClearPaint;

    private Paint mShadowPaint;

    private int mNormalColor;

    private int mHighLightColor;

    private int mUpperEdge;
    private int mLowerEdge;

    private WheelTouchHelper mTouchHelper;

    private OnItemSelectedListener mSelectedListener;

    private int mShadowHeight;

    private int mLastPosition = 0;

    private LinearGradient mBlackToTransparent;
    private LinearGradient mTransparentToBlack;

    private Bitmap mCacheBitmap;

    private Canvas mCacheCanvas;

    private Path mPath;

    private float[] mVerts;

    private Camera mCamera;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        mTextSize = getContext().getResources().getDimensionPixelSize(R.dimen.wheel_text_size);
        //init colors
        mHighLightColor = ContextCompat.getColor(getContext(), R.color.wheel_high_light_color);
        mNormalColor = ContextCompat.getColor(getContext(), R.color.wheel_normal_color);

        //init normal paint;
        mNormalPaint = getPaint(mTextSize, mNormalColor);
        //init scaled paint
        mScaledPaint = getPaint(mTextSize * mScaleFactor, mHighLightColor);

        //init lines paint
        mLinesPaint = getPaint(getContext().getResources().getDimensionPixelSize(R.dimen.wheel_edge_width), mNormalColor);

        //init shadow paint
        initShadowPaint();
        //init clear paint
        initClearPaint();
        mTouchHelper = new WheelTouchHelper(this);
        mShadowHeight = getResources().getDimensionPixelSize(R.dimen.wheel_shadow_height);

        mCacheCanvas = new Canvas();

        mCamera = new Camera();
    }

    private void initShadowPaint() {
        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mBlackToTransparent = new LinearGradient(0, 0, 0, 1, Color.parseColor("#30000000"), Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mTransparentToBlack = new LinearGradient(0, 0, 0, 1, Color.TRANSPARENT, Color.parseColor("#30000000"), Shader.TileMode.CLAMP);
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
                initVerts();
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
        //drawContent(canvas);
        // drawShadow(canvas);
        //   drawFilteredText(canvas);
        //   drawEdges(canvas);
        drawCache();
        //canvas.drawBitmap(mCacheBitmap, 0, 0, null);
        canvas.drawBitmapMesh(mCacheBitmap, 1, mVerts.length / 4 - 1, mVerts, 0, null, 0, null);
        // drawFilteredText(canvas);
    }

    private void drawCache() {
        //clear content
        mCacheCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //first we draw normal texts


        //first we draw normal texts
        drawContent(mCacheCanvas);
        //draw shadow
//        drawShadow(mCacheCanvas);
        //then clear center area
        //  clearCenterArea(canvas);
        //draw scaled text
        //drawFilteredText(mCacheCanvas);
        //draw two lines
        // drawEdges(mCacheCanvas);
    }

    private void drawContent(Canvas canvas) {
        canvas.save();
        if (mPath == null) {
            mPath = new Path();
            mPath.addRect(0, 0, getMeasuredWidth(), mUpperEdge, Path.Direction.CW);
            mPath.addRect(0, mLowerEdge, getMeasuredWidth(), getMeasuredHeight(), Path.Direction.CW);
        }
        //canvas.clipPath(mPath);
        drawTexts(mNormalPaint, canvas);
        canvas.restore();
    }

    private void drawTexts(Paint paint, Canvas canvas) {
        int firstPosition = mScrollY / mItemHeight;
        float top = -mScrollY % mItemHeight;
        int itemCount = (int) Math.ceil((getMeasuredHeight() - top) / mItemHeight);
        float x = getMeasuredWidth() / 2;
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        for (int i = 0; i < itemCount; i++) {
            String content = mAdapter.getItem(firstPosition);
            int baseline = (int) ((top + mItemHeight + top - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(content, x - paint.measureText(content) / 2, baseline, paint);
            top += mItemHeight;
            if (++firstPosition >= mAdapter.getItemCount()) {
                firstPosition = 0;
            }
        }
    }

    private void drawEdges(Canvas canvas) {
        canvas.drawLine(0, mUpperEdge, getMeasuredWidth(), mUpperEdge, mLinesPaint);
        canvas.drawLine(0, mLowerEdge, getMeasuredWidth(), mLowerEdge, mLinesPaint);
    }

    private void drawShadow(Canvas canvas) {
        mShadowPaint.setShader(mBlackToTransparent);
        canvas.drawRect(0, 0, getMeasuredWidth(), mShadowHeight, mShadowPaint);
        mShadowPaint.setShader(mTransparentToBlack);
        canvas.drawRect(0, getMeasuredHeight() - mShadowHeight, getMeasuredWidth(), getMeasuredHeight(), mShadowPaint);
    }

    private void drawFilteredText(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, mUpperEdge, getMeasuredWidth(), mLowerEdge);
        drawTexts(mScaledPaint, canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mTouchHelper != null &&
                mTouchHelper.onTouchEvent(event);
    }

    @Override
    public void onScrolled(int dy) {
        mScrollY = amend(mScrollY + dy);
        invalidate();
    }

    private int amend(int y) {
        if (y >= mTotalHeight) {
            y -= mTotalHeight;
        } else if (y < 0) {
            y += mTotalHeight;
        }
        return y;
    }

    @Override
    public void onScrollStateChanged(int newState) {
        if (newState == OnWheelScrollListener.STATE_IDLE) {
            //scroll to nearest position
            int top = amend(mScrollY + mUpperEdge);

            int firstPosition = top / mItemHeight;

            if (top % mItemHeight < mItemHeight / 2) {
                smoothScrollToPosition(firstPosition);
            } else {
                smoothScrollToPosition(firstPosition + 1);
            }
        }
    }

    public void smoothScrollToPosition(int position) {
        int destY = amend(position * mItemHeight - mUpperEdge);
        int dy = mScrollY - destY;
        if (mTotalHeight - mScrollY < mItemHeight && destY == 0) {
            dy = mScrollY - mTotalHeight;
        }
        if (position != mLastPosition && mSelectedListener != null)
            mSelectedListener.onItemSelected(position);
        mLastPosition = position;
        mTouchHelper.smoothScrollBy(dy);
    }

    public int getCurrentPosition() {
        int top = amend(mScrollY + mUpperEdge);
        return top / mItemHeight;
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
        if (mCacheBitmap == null) {
            mCacheBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            mCacheCanvas.setBitmap(mCacheBitmap);
        }
    }

    private void initVerts() {
        double r = getMeasuredHeight() / 2;
        int k = 0;
        mVerts = new float[4 * (getMeasuredHeight() + 1)];
        float lastY = 0;
        ArrayList<Float> topys = new ArrayList<>();
        float biggestDelta = 0;
        for (int i = 0; i <= getMeasuredHeight(); i++) {
            float y = 0;

            if (i <= r) {
                y = (float) (r - Math.sqrt(r * r - i * i));
                if (y - lastY >= biggestDelta)
                    biggestDelta = y - lastY;
                lastY = y;
                topys.add(y);
                if (y >= r - mItemHeight / 2) {
                    break;
                }
            }
        }


        /*mVerts = new float[80];
        double fromHeight = mItemHeight / 2;
        double endHeight = 0.5 * mTextSize;
        int n = 10;
        double k = (getMeasuredHeight() / 2 - 10 * endHeight) / (n * (n + 1) / 2);

        Logger.d("k " + k);
        int j = 0;
        float lastY = 0;

        mVerts[j++] = 0;
        mVerts[j++] = 0;
        mVerts[j++] = getMeasuredWidth();
        mVerts[j++] = 0;

        for (int i = 0; i < n - 1; i++) {
            lastY += (float) (endHeight + k * i);
            mVerts[j++] = 0;
            mVerts[j++] = lastY;
            mVerts[j++] = getMeasuredWidth();
            mVerts[j++] = lastY;
            Logger.d("y " + (endHeight + k * i));
        }

        mVerts[j++] = 0;
        mVerts[j++] = getMeasuredHeight() / 2;
        mVerts[j++] = getMeasuredWidth();
        mVerts[j++] = getMeasuredHeight() / 2;
        lastY = getMeasuredHeight() / 2;
        for (int i = 0; i < n - 1; i++) {
            lastY += (float) (fromHeight - k * i);
            mVerts[j++] = 0;
            mVerts[j++] = lastY;
            mVerts[j++] = getMeasuredWidth();
            mVerts[j++] = lastY;
            Logger.d("y " + (fromHeight - k * i));
        }*/
      /*  float factor = getMeasuredHeight()/5;
        float r = getMeasuredWidth();
        mVerts = new float[]{
            0,0,r,0,
            0,0.8f*factor,r,0.8f*factor,
            0,mUpperEdge,r,mUpperEdge,
            0,mLowerEdge,r,mLowerEdge,
            0,4.2f*factor,r,4.2f*factor,
            0,5f*factor,r,5f*factor
        };*/
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
        if (position != mLastPosition && mSelectedListener != null)
            mSelectedListener.onItemSelected(position);
        mLastPosition = position;
    }

    public void setCurrentPosition(int position) {
        if (position < 0 || mAdapter == null || position >= mAdapter.getItemCount()) {
            return;
        }
        //reset scrollY
        mScrollY = amend(position * mItemHeight - mUpperEdge);
        invalidate();
        if (position != mLastPosition && mSelectedListener != null)
            mSelectedListener.onItemSelected(position);
        mLastPosition = position;
    }
}
