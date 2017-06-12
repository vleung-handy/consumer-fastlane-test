package com.handybook.handybook.vegas.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.handybook.handybook.R;

import java.util.ArrayList;
import java.util.List;

public class ScratchView extends SurfaceView
        implements ScratchableInterface, SurfaceHolder.Callback {

    private static final String TAG = "ScratchView";

    // default value constants
    private static final int DEFAULT_COLOR = 0xff444444; // default color is dark gray
    private static final int DEFAULT_REVEAL_SIZE = 60;

    public static final int DEFAULT_SCRATCH_TEST_SPEED = 4;

    private ScratchViewThread mThread;
    List<Path> mPathList = new ArrayList<Path>();
    private int mOverlayColor;
    private Paint mOverlayPaint;
    private int mRevealSize;
    private boolean mIsScratchable = true;
    private boolean mIsAntiAlias = false;
    private Path path;
    private float startX = 0;
    private float startY = 0;
    private boolean mScratchStart = false;
    private Bitmap mScratchBitmap;
    private Drawable mScratchDrawable = null;
    private Paint mBitmapPaint;
    private Matrix mMatrix;
    private Bitmap mScratchedTestBitmap;
    private Canvas mScratchedTestCanvas;
    private OnScratchCallback mOnScratchCallback;

    private boolean mIsFullyRevealed = false;
    private boolean mIsClickable = false;

    public ScratchView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init(ctx, attrs);
    }

    public ScratchView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        // default value
        mOverlayColor = DEFAULT_COLOR;
        mRevealSize = DEFAULT_REVEAL_SIZE;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScratchView, 0, 0);

        final int indexCount = ta.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case R.styleable.ScratchView_overlayColor:
                    mOverlayColor = ta.getColor(attr, DEFAULT_COLOR);
                    break;
                case R.styleable.ScratchView_revealSize:
                    mRevealSize = ta.getDimensionPixelSize(attr, DEFAULT_REVEAL_SIZE);
                    break;
                case R.styleable.ScratchView_antiAlias:
                    mIsAntiAlias = ta.getBoolean(attr, false);
                    break;
                case R.styleable.ScratchView_scratchable:
                    mIsScratchable = ta.getBoolean(attr, true);
                    break;
                case R.styleable.ScratchView_scratchDrawable:
                    mScratchDrawable = ta.getDrawable(R.styleable.ScratchView_scratchDrawable);
                    break;
            }
        }
        ta.recycle();

        setZOrderOnTop(true);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);

        mOverlayPaint = new Paint();
        mOverlayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mOverlayPaint.setStyle(Paint.Style.STROKE);
        mOverlayPaint.setStrokeCap(Paint.Cap.BUTT);
        mOverlayPaint.setStrokeJoin(Paint.Join.ROUND);

        // convert drawable to bitmap if drawable already set in xml
        if (mScratchDrawable != null) {
            mScratchBitmap = ((BitmapDrawable) mScratchDrawable).getBitmap();
        }

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setDither(true);
        if (isInEditMode()) {

        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Clear all area if mClearCanvas is true
        if (mIsFullyRevealed) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            return;
        }

        if (mScratchBitmap != null) {
            if (mMatrix == null) {
                float scaleWidth = (float) canvas.getWidth() / mScratchBitmap.getWidth();
                float scaleHeight = (float) canvas.getHeight() / mScratchBitmap.getHeight();
                mMatrix = new Matrix();
                mMatrix.postScale(scaleWidth, scaleHeight);
            }
            canvas.drawBitmap(mScratchBitmap, mMatrix, mBitmapPaint);
        }
        else {
            canvas.drawColor(mOverlayColor);
        }

        mOverlayPaint.setAntiAlias(mIsAntiAlias);
        mOverlayPaint.setStrokeWidth(mRevealSize);
        for (Path path : mPathList) {
            canvas.drawPath(path, mOverlayPaint);
        }

    }

    private void updateScratchedPercentage() {
        if (mOnScratchCallback == null) { return; }
        mOnScratchCallback.onScratch(getRevealedRatio());
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        synchronized (mThread.getSurfaceHolder()) {
            if (!mIsScratchable) {
                return true;
            }

            switch (me.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path = new Path();
                    path.moveTo(me.getX(), me.getY());
                    startX = me.getX();
                    startY = me.getY();
                    mPathList.add(path);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mScratchStart) {
                        path.lineTo(me.getX(), me.getY());
                    }
                    else {
                        if (isScratch(startX, me.getX(), startY, me.getY())) {
                            mScratchStart = true;
                            path.lineTo(me.getX(), me.getY());
                        }
                    }
                    updateScratchedPercentage();
                    break;
                case MotionEvent.ACTION_UP:
                    //Set call back if user's finger detach
                    if (mOnScratchCallback != null) {
                        mOnScratchCallback.onDetach(true);
                    }
                    //perform Click action if the motion is not move
                    //and the WScratchView is clickable
                    if (!mScratchStart && mIsClickable) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                performClick();
                            }
                        });
                    }
                    mScratchStart = false;
                    break;
            }
            return true;
        }
    }

    private boolean isScratch(float oldX, float x, float oldY, float y) {
        float distance = (float) Math.sqrt(Math.pow(oldX - x, 2) + Math.pow(oldY - y, 2));
        return distance > mRevealSize * 2;
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // do nothing
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        mThread = new ScratchViewThread(getHolder(), this);
        mThread.setRunning(true);
        mThread.start();

        mScratchedTestBitmap = Bitmap.createBitmap(
                arg0.getSurfaceFrame().width(),
                arg0.getSurfaceFrame().height(),
                Bitmap.Config.ARGB_8888
        );
        mScratchedTestCanvas = new Canvas(mScratchedTestBitmap);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        boolean retry = true;
        mThread.setRunning(false);
        while (retry) {
            try {
                mThread.join();
                retry = false;
            }
            catch (InterruptedException e) {
                // do nothing but keep retry
            }
        }

    }

    private class ScratchViewThread extends Thread {

        private final SurfaceHolder mSurfaceHolder;
        private ScratchView mView;
        private boolean mRun = false;

        ScratchViewThread(@NonNull SurfaceHolder surfaceHolder, ScratchView view) {
            mSurfaceHolder = surfaceHolder;
            mView = view;
        }

        void setRunning(boolean run) {
            mRun = run;
        }

        @NonNull
        SurfaceHolder getSurfaceHolder() {
            return mSurfaceHolder;
        }

        @Override
        public void run() {
            Canvas c;
            while (mRun) {
                c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (c != null) {
                            mView.draw(c);
                        }
                    }
                }
                finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

    @Override
    public void resetView() {
        synchronized (mThread.getSurfaceHolder()) {
            mPathList.clear();
            mIsFullyRevealed = false;
        }
    }

    @Override
    public boolean isScratchable() {
        return mIsScratchable;
    }

    @Override
    public void setScratchable(boolean flag) {
        mIsScratchable = flag;
    }

    @Override
    public void setOverlayColor(int ResId) {
        mOverlayColor = ResId;
    }

    @Override
    public void setBrushRadius(int radiusPx) {
        mRevealSize = radiusPx;
    }

    @Override
    public void setAntiAlias(boolean flag) {
        mIsAntiAlias = flag;
    }

    @Override
    public void setScratchDrawable(Drawable d) {
        mScratchDrawable = d;
        if (mScratchDrawable != null) {
            mScratchBitmap = ((BitmapDrawable) mScratchDrawable).getBitmap();
        }
    }

    @Override
    public void setScratchBitmap(Bitmap b) {
        mScratchBitmap = b;
    }

    @Override
    public float getRevealedRatio() {
        return getRevealedRatio(DEFAULT_SCRATCH_TEST_SPEED);
    }

    @Override
    public float getRevealedRatio(int speed) {
        if (null == mScratchedTestBitmap) {
            return 0;
        }
        draw(mScratchedTestCanvas);

        final int width = mScratchedTestBitmap.getWidth();
        final int height = mScratchedTestBitmap.getHeight();

        int count = 0;
        for (int i = 0; i < width; i += speed) {
            for (int j = 0; j < height; j += speed) {
                if (0 == Color.alpha(mScratchedTestBitmap.getPixel(i, j))) {
                    count++;
                }
            }
        }
        return (float) count / ((width / speed) * (height / speed)) * 100;
    }

    @Override
    public void setOnScratchCallback(OnScratchCallback callback) {
        mOnScratchCallback = callback;
    }

    @Override
    public void revealAll() {
        mIsFullyRevealed = true;
    }

    @Override
    public void setBackgroundClickable(boolean clickable) {
        mIsClickable = clickable;
    }
}
