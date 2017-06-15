package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * simple loading layout with a progress spinner and loading text
 */
public class LoadingLayout extends FrameLayout {

    @BindView(R.id.layout_loading_text)
    TextView mLoadingText;

    public LoadingLayout(final Context context) {
        super(context);
        init(null);
    }

    public LoadingLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LoadingLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attributeSet) {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, this);
        ButterKnife.bind(this);

        if (attributeSet != null) {
            final TypedArray typedArray = getContext().obtainStyledAttributes(
                    attributeSet, R.styleable.LoadingLayout);
            String loadingText = typedArray.getString(R.styleable.LoadingLayout_loadingText);
            mLoadingText.setText(loadingText);
            typedArray.recycle();
        }

    }
}
