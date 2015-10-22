package com.handybook.handybook.ui.transformation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Transformation to round corners of drawables, by default it rounds all the corners, but corners
 * can be picked one by one.
 */
public class RoundedTransformation implements
        Transformation
{
    private final float mRadius;
    private final float mMargin; // dp
    private final boolean mRoundTopRight;
    private final boolean mRoundBottomRight;
    private final boolean mRoundBottomLeft;
    private final boolean mRoundTopLeft;

    /**
     * @param radius  corner radius in dp
     * @param padding padding in dp
     */
    public RoundedTransformation(final float radius, final float padding)
    {
        mRadius = radius;
        mMargin = padding;
        mRoundTopRight = true;
        mRoundBottomRight = true;
        mRoundBottomLeft = true;
        mRoundTopLeft = true;
    }

    /**
     * @param radius           corner radius in dp
     * @param padding          padding in dp
     * @param roundTopRight    should round top right corner
     * @param roundBottomRight should round bottom right corner
     * @param roundBottomLeft  should round bottom left corner
     * @param roundTopLeft     should round top right corner
     */
    public RoundedTransformation(
            final float radius,
            final float padding,
            final boolean roundTopRight,
            final boolean roundBottomRight,
            final boolean roundBottomLeft,
            final boolean roundTopLeft
    )
    {
        mRadius = radius;
        mMargin = padding;
        mRoundTopRight = roundTopRight;
        mRoundBottomRight = roundBottomRight;
        mRoundTopLeft = roundTopLeft;
        mRoundBottomLeft = roundBottomLeft;
    }

    @Override
    public Bitmap transform(final Bitmap source)
    {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        final int width = source.getWidth();
        final int height = source.getHeight();
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(
                new RectF(
                        mMargin,
                        mMargin,
                        width - mMargin,
                        height - mMargin
                ),
                mRadius,
                mRadius,
                paint);
        if (!mRoundTopRight)
        {
            canvas.drawRect(
                    new RectF(
                            width - mMargin - mRadius,
                            mMargin,
                            width - mMargin,
                            mMargin + mRadius

                    ),
                    paint);
        }
        if (!mRoundBottomRight)
        {
            canvas.drawRect(
                    new RectF(
                            width - mMargin - mRadius,
                            height - mMargin - mRadius,
                            width - mMargin,
                            height - mMargin

                    ),
                    paint);
        }
        if (!mRoundBottomLeft)
        {
            canvas.drawRect(
                    new RectF(
                            mMargin,
                            height - mMargin - mRadius,
                            mMargin + mRadius,
                            height - mMargin

                    ),
                    paint);
        }
        if (!mRoundTopLeft)
        {
            canvas.drawRect(
                    new RectF(
                            mMargin,
                            mMargin,
                            mMargin + mRadius,
                            mMargin + mRadius

                    ),
                    paint);
        }
        if (source != output)
        {
            source.recycle();
        }
        return output;
    }

    @Override
    public String key()
    {
        return "rounded";
    }
}
