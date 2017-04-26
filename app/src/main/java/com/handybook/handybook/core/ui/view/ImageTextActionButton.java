package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * an action button with image and text, specific to this app
 */
public class ImageTextActionButton extends FrameLayout {

    @Bind(R.id.element_image_text_action_button_image_view)
    ImageView mImageView;

    @Bind(R.id.element_image_text_action_button_text_view)
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

            mImageView.setImageDrawable(drawable);
            mTextView.setText(text);
            typedArray.recycle();
        }
    }
}
