package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.util.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * an action button with image and text, specific to this app
 */
public class ImageTextActionButton extends FrameLayout {

    @BindView(R.id.element_image_text_action_button_image_view)
    ImageView mImageView;

    @BindView(R.id.element_image_text_action_button_text_view)
    TextView mTextView;

    public ImageTextActionButton(final Context context) {
        super(context);
        init(null);
    }

    public ImageTextActionButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ImageTextActionButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.element_image_text_action_button, this);
        ButterKnife.bind(this);

        if(attributeSet != null)
        {
            final TypedArray typedArray = getContext().obtainStyledAttributes(
                    attributeSet, R.styleable.ImageTextActionButton);
            String text = typedArray.getString(R.styleable.ImageTextActionButton_text);
            Drawable drawable = typedArray.getDrawable(R.styleable.ImageTextActionButton_drawable);

            if (drawable != null) {
                int drawableTintResourceId = typedArray.getResourceId(
                        R.styleable.ImageTextActionButton_drawableTint,
                        -1
                );
                if (drawableTintResourceId >= 0) {
                    ColorStateList drawableTint = ContextCompat.getColorStateList(
                            getContext(),
                            drawableTintResourceId
                    );
                    drawable = DrawableCompat.wrap(drawable.mutate());

                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
                    DrawableCompat.setTintList(drawable, drawableTint);
                }
                mImageView.setImageDrawable(drawable);
                mImageView.setVisibility(VISIBLE);
            }
            else {
                mImageView.setVisibility(GONE);
            }

            if (!TextUtils.isBlank(text)) {
                int textTintResourceId
                        = typedArray.getResourceId(R.styleable.ImageTextActionButton_textTint, -1);
                if (textTintResourceId >= 0) {
                    ColorStateList textTint = ContextCompat.getColorStateList(
                            getContext(),
                            textTintResourceId
                    );
                    mTextView.setTextColor(textTint);
                }
                mTextView.setText(text);
                mTextView.setVisibility(VISIBLE);
            }
            else {
                mTextView.setVisibility(GONE);
            }

            typedArray.recycle();
        }
    }
}
