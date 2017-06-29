package com.handybook.handybook.vegas.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ScratchOffView extends AppCompatImageView {

    private static final String TAG = ScratchOffView.class.getSimpleName();

    private static final float MIN_SCRATCH_DISTANCE = 1;
    private static final int DEFAULT_STROKE_WIDTH = 72;

    private OnScratchListener mOnScratchListener;
    private boolean mIsScratching = false;
    private boolean mIsFullyScratchedOff;
    private List<Path> mPaths;
    private Path mPath = new Path();
    private float mPathStartX;
    private float mPathStartY;
    private Bitmap mDrawableBitmap;
    private final Paint mScratchPaint;
    private final Paint mBitmapPaint;
    private Matrix mMatrix;

    {
        mScratchPaint = new Paint();
        mScratchPaint.setColor(Color.TRANSPARENT);
        mScratchPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mScratchPaint.setAntiAlias(true);
        mScratchPaint.setStyle(Paint.Style.STROKE);
        mScratchPaint.setStrokeCap(Paint.Cap.BUTT);
        mScratchPaint.setStrokeJoin(Paint.Join.ROUND);
        mScratchPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setDither(true);


    }

    public ScratchOffView(final Context context) {
        super(context);
        init(context, null, 0);
    }

    public ScratchOffView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ScratchOffView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        setDrawingCacheEnabled(true);
        setBackgroundColor(Color.TRANSPARENT);

        if (getDrawable() != null) {
            mDrawableBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        reset();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath = new Path();
                mPath.moveTo(event.getX(), event.getY());
                mPathStartX = event.getX();
                mPathStartY = event.getY();
                mPaths.add(mPath);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsScratching) {
                    continueScratch(event);
                }
                else {
                    if (shouldStartScratch(mPathStartX, event.getX(), mPathStartY, event.getY())) {
                        startScratch(event);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (isClickable() && !mIsScratching) {
                    performClick();
                }
                else if (mIsScratching) {
                    stopScratch(event);
                }
                break;
        }
        invalidate();
        return true;
    }

    public void reset() {
        mPathStartX = 0;
        mPathStartY = 0;
        mPath = new Path();
        mPaths = new ArrayList<>();
        mIsScratching = false;
        mIsFullyScratchedOff = false;
        invalidate();
    }

    private void startScratch(final MotionEvent event) {
        mIsScratching = true;
        final Path path = new Path(mPath);
        path.lineTo(event.getX(), event.getY());
        mPaths.add(path);
        if (mOnScratchListener != null) {
            mOnScratchListener.onScratchStart(event.getRawX(), event.getRawY());
        }
    }

    private void continueScratch(final MotionEvent event) {
        mPath.lineTo(event.getX(), event.getY());
        if (mOnScratchListener != null) {
            mOnScratchListener.onScratchMove(event.getRawX(), event.getRawY());
        }
    }

    private void stopScratch(final MotionEvent event) {
        mIsScratching = false;
        if (mOnScratchListener != null) {
            mOnScratchListener.onScratchStop(event.getRawX(), event.getRawY());
        }

    }

    private boolean shouldStartScratch(float oldX, float x, float oldY, float y) {
        float distance = (float) Math.hypot(oldX - x, oldY - y);
        return distance >= MIN_SCRATCH_DISTANCE;
    }

    public void setOnScratchListener(final OnScratchListener listener) {
        mOnScratchListener = listener;
    }

    public void scratchOffAll() {
        mIsFullyScratchedOff = true;
    }

    public double getScratchedOffRatio(final int pixelDelta) {
        destroyDrawingCache();
        buildDrawingCache();
        Bitmap b = getDrawingCache();
        final int w = b.getWidth();
        final int h = b.getHeight();
        int numTransparent = 0, numOpaque = 0;

        for (int y = 0; y < h; y += pixelDelta) {
            for (int x = 0; x < w; x += pixelDelta) {
                if ((b.getPixel(x, y) >>> 24) == 0) { // completely transparent
                    numTransparent++;
                }
                else {
                    numOpaque++;
                }
            }
        }
        return (double) numTransparent / (numTransparent + numOpaque);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mIsFullyScratchedOff) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            return;
        }
        if (mDrawableBitmap != null) {
            if (mMatrix == null) {
                float scaleWidth = (float) canvas.getWidth() / mDrawableBitmap.getWidth();
                float scaleHeight = (float) canvas.getHeight() / mDrawableBitmap.getHeight();
                mMatrix = new Matrix();
                mMatrix.postScale(scaleWidth, scaleHeight);
            }
            canvas.drawBitmap(mDrawableBitmap, mMatrix, mBitmapPaint);
            for (Path path : mPaths) {
                canvas.drawPath(path, mScratchPaint);
            }
        }
    }

    public interface OnScratchListener {

        void onScratchStart(float rawX, float rawY);

        void onScratchMove(float rawX, float rawY);

        void onScratchStop(float rawX, float rawY);

    }
}
